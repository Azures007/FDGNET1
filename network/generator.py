# 从判别器模块导入相关组件
from .discrim_hyperG import *
# 从timm模型层导入DropPath、to_2tuple、trunc_normal等工具函数
from timm.models.layers import DropPath, to_2tuple, trunc_normal_
# 导入PyTorch傅里叶变换模块
import torch.fft
# 导入随机数模块
import random
# 从math模块导入平方根函数
from math import sqrt
# 导入NumPy数值计算库
import numpy as np


# 定义多层感知机类，继承自PyTorch神经网络模块
class Mlp(nn.Module):
    # 初始化函数，设置MLP结构参数
    def __init__(self, in_features, hidden_features=None, out_features=None, act_layer=nn.GELU, drop=0.):
        # 调用父类构造函数
        super().__init__()
        # 如果未指定输出特征数，则与输入特征数相同
        out_features = out_features or in_features
        # 如果未指定隐藏特征数，则与输入特征数相同
        hidden_features = hidden_features or in_features
        # 第一个全连接层：输入特征->隐藏特征
        self.fc1 = nn.Linear(in_features, hidden_features)
        # 激活函数层
        self.act = act_layer()
        # 第二个全连接层：隐藏特征->输出特征
        self.fc2 = nn.Linear(hidden_features, out_features)
        # Dropout层用于防止过拟合
        self.drop = nn.Dropout(drop)

    # 前向传播函数
    def forward(self, x):
        # 第一层全连接
        x = self.fc1(x)
        # 激活函数
        x = self.act(x)
        # Dropout
        x = self.drop(x)
        # 第二层全连接
        x = self.fc2(x)
        # Dropout
        x = self.drop(x)
        # 返回输出
        return x


# 定义频谱滤波器类，用于在频域中添加噪声
class GlobalFilter_spec(nn.Module):
    # 初始化函数，设置频谱滤波器参数
    def __init__(self, dim, h=14, w=8,
                 mask_radio=0.1, mask_alpha=0.5,
                 noise_mode=1,
                 low_or_high=0, uncertainty_model=0, perturb_prob=0.5,
                 uncertainty_factor=1.0,
                 noise_layer_flag=0, gauss_or_uniform=0, ):
        # 调用父类构造函数
        super().__init__()
        # 创建复数权重参数，用于频域滤波
        self.complex_weight = nn.Parameter(torch.randn(h, w, (dim // 2) + 1, 2, dtype=torch.float32) * 0.02)
        # 保存宽度和高度参数
        self.w = w
        self.h = h

        # 保存掩码比例参数
        self.mask_radio = mask_radio

        # 保存噪声模式参数
        self.noise_mode = noise_mode
        # 保存噪声层标志
        self.noise_layer_flag = noise_layer_flag

        # 保存掩码alpha参数
        self.alpha = mask_alpha

        # 保存低频或高频标志
        self.low_or_high = low_or_high

        # 保存数值稳定性的小量
        self.eps = 1e-6
        # 保存不确定性因子
        self.factor = uncertainty_factor
        # 保存不确定性模型类型
        self.uncertainty_model = uncertainty_model
        # 保存扰动概率
        self.p = perturb_prob
        # 保存高斯或均匀分布标志
        self.gauss_or_uniform = gauss_or_uniform

    # 重参数化函数，用于采样
    def _reparameterize(self, mu, std, epsilon_norm):
        # epsilon = torch.randn_like(std) * self.factor
        # 使用标准化的噪声乘以因子
        epsilon = epsilon_norm * self.factor
        # 重参数化采样
        mu_t = mu + epsilon * std
        # 返回采样结果
        return mu_t

    # 频谱噪声添加函数
    def spectrum_noise(self, img_fft, ratio=1.0, noise_mode=1,
                       low_or_high=0, uncertainty_model=0, gauss_or_uniform=0):
        """输入图像尺寸：[H, W, C]的ndarray"""
        """noise_mode: 1幅度；2：相位 3：两者都"""
        """uncertainty_model: 1批次级建模 2：通道级建模 3：token级建模"""
        # 根据概率决定是否添加噪声
        if random.random() > self.p:
            return img_fft
        # 获取输入张量的维度
        batch_size, h, w, c = img_fft.shape

        # 计算幅度和相位
        img_abs, img_pha = torch.abs(img_fft), torch.angle(img_fft)

        # 如果是低频模式，则进行傅里叶变换移位
        if low_or_high == 0:
            img_abs = torch.fft.fftshift(img_abs, dim=3)

        # 计算裁剪的通道数
        c_crop = int(c * sqrt(ratio))
        # h_crop = int(h * sqrt(ratio))
        # w_crop = int(w * sqrt(ratio))
        # 计算起始位置
        c_start = c // 2 - c_crop // 2
        # w_start = w - w_crop

        # 克隆幅度图像
        img_abs_ = img_abs.clone()
        # 如果噪声模式不为0
        if noise_mode != 0:
            # 如果使用不确定性模型
            if uncertainty_model != 0:
                # 批次级建模
                if uncertainty_model == 1:
                    # 计算均值和方差
                    miu = torch.mean(img_abs_[:, :, :, c_start:c_start + c_crop], dim=(3), keepdim=True)
                    var = torch.var(img_abs_[:, :, :, c_start:c_start + c_crop], dim=(3), keepdim=True)
                    sig = (var + self.eps).sqrt()  # Bx1x1xC

                    # 计算均值和标准差的方差
                    var_of_miu = torch.var(miu, dim=0, keepdim=True)
                    var_of_sig = torch.var(sig, dim=0, keepdim=True)
                    # 计算均值和标准差的标准差
                    sig_of_miu = (var_of_miu + self.eps).sqrt().repeat(miu.shape[0], 1, 1, 1)
                    sig_of_sig = (var_of_sig + self.eps).sqrt().repeat(miu.shape[0], 1, 1, 1)  # Bx1x1xC

                    # 根据分布类型生成噪声
                    if gauss_or_uniform == 0:
                        # 高斯分布噪声
                        epsilon_norm_miu = torch.randn_like(sig_of_miu)  # N(0,1)
                        epsilon_norm_sig = torch.randn_like(sig_of_sig)

                        miu_mean = miu
                        sig_mean = sig

                        # 重参数化采样
                        beta = self._reparameterize(mu=miu_mean, std=sig_of_miu, epsilon_norm=epsilon_norm_miu)
                        gamma = self._reparameterize(mu=sig_mean, std=sig_of_sig, epsilon_norm=epsilon_norm_sig)
                    elif gauss_or_uniform == 1:
                        # 均匀分布噪声
                        epsilon_norm_miu = torch.rand_like(sig_of_miu) * 2 - 1.  # U(-1,1)
                        epsilon_norm_sig = torch.rand_like(sig_of_sig) * 2 - 1.
                        beta = self._reparameterize(mu=miu, std=sig_of_miu, epsilon_norm=epsilon_norm_miu)
                        gamma = self._reparameterize(mu=sig, std=sig_of_sig, epsilon_norm=epsilon_norm_sig)
                    else:
                        # 标准正态分布噪声
                        epsilon_norm_miu = torch.randn_like(sig_of_miu)  # N(0,1)
                        epsilon_norm_sig = torch.randn_like(sig_of_sig)
                        beta = self._reparameterize(mu=miu, std=1., epsilon_norm=epsilon_norm_miu)
                        gamma = self._reparameterize(mu=sig, std=1., epsilon_norm=epsilon_norm_sig)

                    # 调整每个样本的统计特性
                    img_abs[:, :, :, c_start:c_start + c_crop] = gamma * (
                            img_abs[:, :, :, c_start:c_start + c_crop] - miu) / sig + beta

                # 元素级建模
                elif uncertainty_model == 2:
                    # 计算元素级的均值和方差
                    miu_of_elem = torch.mean(img_abs_[:, :, :, c_start:c_start + c_crop], dim=0, keepdim=True)
                    var_of_elem = torch.var(img_abs_[:, :, :, c_start:c_start + c_crop], dim=0, keepdim=True)
                    sig_of_elem = (var_of_elem + self.eps).sqrt()  # 1xHxWxC

                    # 根据分布类型生成噪声
                    if gauss_or_uniform == 0:
                        # 高斯分布噪声
                        epsilon_sig = torch.randn_like(img_abs[:, :, :, c_start:c_start + c_crop])  # BxHxWxC N(0,1)
                        gamma = epsilon_sig * sig_of_elem * self.factor
                    elif gauss_or_uniform == 1:
                        # 均匀分布噪声
                        epsilon_sig = torch.rand_like(img_abs[:, :, :, c_start:c_start + c_crop]) * 2 - 1.  # U(-1,1)
                        gamma = epsilon_sig * sig_of_elem * self.factor
                    else:
                        # 标准正态分布噪声
                        epsilon_sig = torch.randn_like(
                            img_abs[:, :, :, c_start:c_start + c_crop])  # BxHxWxC N(0,1)
                        gamma = epsilon_sig * self.factor

                    # 添加噪声到幅度图像
                    img_abs[:, :, :, c_start:c_start + c_crop] = img_abs[:, :, :, c_start:c_start + c_crop] + gamma
        else:
            # 不添加噪声
            pass
        # 如果是低频模式，进行逆傅里叶变换移位恢复
        if low_or_high == 0:
            img_abs = torch.fft.ifftshift(img_abs, dim=(1, 2))  # recover

        # 重构复数图像
        img_mix = img_abs * (np.e ** (1j * img_pha))
        # 返回混合图像
        return img_mix

    # 前向传播函数
    def forward(self, x, layer_index=0, spatial_size=None):
        # 获取输入张量的维度
        B, N, C = x.shape
        # 如果没有指定空间尺寸
        if spatial_size is None:
            # 计算方形的空间尺寸
            a = b = int(math.sqrt(N))
        else:
            # 使用指定的空间尺寸
            a, b = spatial_size

        # 重塑张量为4D格式
        x = x.view(B, a, b, C)
        # 转换为浮点数类型
        x = x.to(torch.float32)
        # 进行2D快速傅里叶变换
        x = torch.fft.rfft2(x, dim=3, norm='ortho')

        # 如果处于训练模式
        if self.training:
            # 如果启用了噪声模式和噪声层标志
            if self.noise_mode != 0 and self.noise_layer_flag == 1:
                # 添加频谱噪声
                x = self.spectrum_noise(x, ratio=self.mask_radio, noise_mode=self.noise_mode,
                                        uncertainty_model=self.uncertainty_model,
                                        gauss_or_uniform=self.gauss_or_uniform)
        # 将复数权重转换为复数形式
        weight = torch.view_as_complex(self.complex_weight)
        # 应用滤波器权重
        x = x * weight
        # 进行逆2D快速傅里叶变换
        x = torch.fft.irfft2(x, s=C, dim=3, norm='ortho')
        # 重塑回原始形状
        x = x.reshape(B, N, C)
        # 返回处理后的张量
        return x


# 定义空间滤波器类，用于在空间域中添加噪声
class GlobalFilter_spa(nn.Module):
    # 初始化函数，设置空间滤波器参数
    def __init__(self, dim, h=14, w=8,
                 mask_radio=0.1, mask_alpha=0.5,
                 noise_mode=1,
                 low_or_high=0, uncertainty_model=0, perturb_prob=0.5,
                 uncertainty_factor=1.0,
                 noise_layer_flag=0, gauss_or_uniform=0, ):
        # 调用父类构造函数
        super().__init__()
        # 创建复数权重参数，注意维度计算方式不同
        self.complex_weight = nn.Parameter(torch.randn(h, w - (h // 2), dim, 2, dtype=torch.float32) * 0.02)
        # 保存宽度和高度参数
        self.w = w
        self.h = h

        # 保存掩码比例参数
        self.mask_radio = mask_radio

        # 保存噪声模式参数
        self.noise_mode = noise_mode
        # 保存噪声层标志
        self.noise_layer_flag = noise_layer_flag

        # 保存掩码alpha参数
        self.alpha = mask_alpha

        # 保存低频或高频标志
        self.low_or_high = low_or_high

        # 保存数值稳定性的小量
        self.eps = 1e-6
        # 保存不确定性因子
        self.factor = uncertainty_factor
        # 保存不确定性模型类型
        self.uncertainty_model = uncertainty_model
        # 保存扰动概率
        self.p = perturb_prob
        # 保存高斯或均匀分布标志
        self.gauss_or_uniform = gauss_or_uniform

    # 重参数化函数，用于采样
    def _reparameterize(self, mu, std, epsilon_norm):
        # epsilon = torch.randn_like(std) * self.factor
        # 使用标准化的噪声乘以因子
        epsilon = epsilon_norm * self.factor
        # 重参数化采样
        mu_t = mu + epsilon * std
        # 返回采样结果
        return mu_t

    # 空间噪声添加函数
    def spa_noise(self, img_fft, ratio=1.0, noise_mode=1,
                  low_or_high=0, uncertainty_model=0, gauss_or_uniform=0):
        """输入图像尺寸：[H, W, C]的ndarray"""
        """noise_mode: 1幅度；2：相位 3：两者都"""
        """uncertainty_model: 1批次级建模 2：通道级建模 3：token级建模"""
        # 根据概率决定是否添加噪声
        if random.random() > self.p:
            return img_fft
        # 获取输入张量的维度
        batch_size, h, w, c = img_fft.shape

        # 计算幅度和相位
        img_abs, img_pha = torch.abs(img_fft), torch.angle(img_fft)

        # 如果是低频模式，则进行傅里叶变换移位
        if low_or_high == 0:
            img_abs = torch.fft.fftshift(img_abs, dim=(1, 2))

        # 计算裁剪的高度和宽度
        h_crop = int(h * sqrt(ratio))
        w_crop = int(w * sqrt(ratio))
        # 计算起始位置
        h_start = h // 2 - h_crop // 2
        w_start = w - w_crop

        # 克隆幅度图像
        img_abs_ = img_abs.clone()
        # 如果噪声模式不为0
        if noise_mode != 0:
            # 如果使用不确定性模型
            if uncertainty_model != 0:
                # 批次级建模
                if uncertainty_model == 1:
                    # 计算均值和方差
                    miu = torch.mean(img_abs_[:, h_start:h_start + h_crop, w_start:, :], dim=(1, 2), keepdim=True)
                    var = torch.var(img_abs_[:, h_start:h_start + h_crop, w_start:, :], dim=(1, 2), keepdim=True)
                    sig = (var + self.eps).sqrt()  # Bx1x1xC

                    # 计算均值和标准差的方差
                    var_of_miu = torch.var(miu, dim=0, keepdim=True)
                    var_of_sig = torch.var(sig, dim=0, keepdim=True)
                    # 计算均值和标准差的标准差
                    sig_of_miu = (var_of_miu + self.eps).sqrt().repeat(miu.shape[0], 1, 1, 1)
                    sig_of_sig = (var_of_sig + self.eps).sqrt().repeat(miu.shape[0], 1, 1, 1)  # Bx1x1xC

                    # 根据分布类型生成噪声
                    if gauss_or_uniform == 0:
                        # 高斯分布噪声
                        epsilon_norm_miu = torch.randn_like(sig_of_miu)  # N(0,1)
                        epsilon_norm_sig = torch.randn_like(sig_of_sig)

                        miu_mean = miu
                        sig_mean = sig

                        # 重参数化采样
                        beta = self._reparameterize(mu=miu_mean, std=sig_of_miu, epsilon_norm=epsilon_norm_miu)
                        gamma = self._reparameterize(mu=sig_mean, std=sig_of_sig, epsilon_norm=epsilon_norm_sig)
                    elif gauss_or_uniform == 1:
                        # 均匀分布噪声
                        epsilon_norm_miu = torch.rand_like(sig_of_miu) * 2 - 1.  # U(-1,1)
                        epsilon_norm_sig = torch.rand_like(sig_of_sig) * 2 - 1.
                        beta = self._reparameterize(mu=miu, std=sig_of_miu, epsilon_norm=epsilon_norm_miu)
                        gamma = self._reparameterize(mu=sig, std=sig_of_sig, epsilon_norm=epsilon_norm_sig)
                    else:
                        # 标准正态分布噪声
                        epsilon_norm_miu = torch.randn_like(sig_of_miu)  # N(0,1)
                        epsilon_norm_sig = torch.randn_like(sig_of_sig)
                        beta = self._reparameterize(mu=miu, std=1., epsilon_norm=epsilon_norm_miu)
                        gamma = self._reparameterize(mu=sig, std=1., epsilon_norm=epsilon_norm_sig)

                    # 调整每个样本的统计特性
                    img_abs[:, h_start:h_start + h_crop, w_start:, :] = gamma * (
                            img_abs[:, h_start:h_start + h_crop, w_start:, :] - miu) / sig + beta

                # 元素级建模
                elif uncertainty_model == 2:
                    # 计算元素级的均值和方差
                    miu_of_elem = torch.mean(img_abs_[:, h_start:h_start + h_crop, w_start:, :], dim=0, keepdim=True)
                    var_of_elem = torch.var(img_abs_[:, h_start:h_start + h_crop, w_start:, :], dim=0, keepdim=True)
                    sig_of_elem = (var_of_elem + self.eps).sqrt()  # 1xHxWxC

                    # 根据分布类型生成噪声
                    if gauss_or_uniform == 0:
                        # 高斯分布噪声
                        epsilon_sig = torch.randn_like(
                            img_abs[:, h_start:h_start + h_crop, w_start:, :])  # BxHxWxC N(0,1)
                        gamma = epsilon_sig * sig_of_elem * self.factor
                    elif gauss_or_uniform == 1:
                        # 均匀分布噪声
                        epsilon_sig = torch.rand_like(
                            img_abs[:, h_start:h_start + h_crop, w_start:, :]) * 2 - 1.  # U(-1,1)
                        gamma = epsilon_sig * sig_of_elem * self.factor
                    else:
                        # 标准正态分布噪声
                        epsilon_sig = torch.randn_like(
                            img_abs[:, h_start:h_start + h_crop, w_start:, :])  # BxHxWxC N(0,1)
                        gamma = epsilon_sig * self.factor

                    # 添加噪声到幅度图像
                    img_abs[:, h_start:h_start + h_crop, w_start:, :] = img_abs[:, h_start:h_start + h_crop, w_start:,
                                                                        :] + gamma
        else:
            # 不添加噪声
            pass
        # 如果是低频模式，进行逆傅里叶变换移位恢复
        if low_or_high == 0:
            img_abs = torch.fft.ifftshift(img_abs, dim=(1, 2))  # recover

        # 重构复数图像
        img_mix = img_abs * (np.e ** (1j * img_pha))
        # 返回混合图像
        return img_mix

    # 前向传播函数
    def forward(self, x, layer_index=0, spatial_size=None):
        # 获取输入张量的维度
        B, N, C = x.shape
        # 如果没有指定空间尺寸
        if spatial_size is None:
            # 计算方形的空间尺寸
            a = b = int(math.sqrt(N))
        else:
            # 使用指定的空间尺寸
            a, b = spatial_size

        # 重塑张量为4D格式
        x = x.view(B, a, b, C)
        # 转换为浮点数类型
        x = x.to(torch.float32)
        # 进行2D快速傅里叶变换（空间域）
        x = torch.fft.rfft2(x, dim=(1, 2), norm='ortho')

        # 如果处于训练模式
        if self.training:
            # 如果启用了噪声模式和噪声层标志
            if self.noise_mode != 0 and self.noise_layer_flag == 1:
                # 添加空间噪声
                x = self.spa_noise(x, ratio=self.mask_radio, noise_mode=self.noise_mode,
                                   uncertainty_model=self.uncertainty_model,
                                   gauss_or_uniform=self.gauss_or_uniform)
        # 将复数权重转换为复数形式
        weight = torch.view_as_complex(self.complex_weight)
        # 应用滤波器权重
        x = x * weight
        # 进行逆2D快速傅里叶变换
        x = torch.fft.irfft2(x, s=(a, b), dim=(1, 2), norm='ortho')
        # 重塑回原始形状
        x = x.reshape(B, N, C)
        # 返回处理后的张量
        return x


# 定义频谱块层缩放类
class spec_BlockLayerScale(nn.Module):
    # 初始化函数，设置频谱块参数
    def __init__(self, dim, mlp_ratio=4., drop=0., drop_path=0., act_layer=nn.GELU,
                 norm_layer=nn.LayerNorm, h=14, w=8, init_values=1e-5,
                 mask_radio=0.1, mask_alpha=0.5, noise_mode=1, low_or_high=0,
                 uncertainty_model=0, perturb_prob=0.5, uncertainty_factor=1.0,
                 layer_index=0, noise_layers=[0, 1, 2, 3], gauss_or_uniform=0, ):
        # 调用父类构造函数
        super().__init__()
        # 创建第一层归一化层
        self.norm1 = norm_layer(dim)

        # 根据层索引决定是否启用噪声层
        if layer_index in noise_layers:
            noise_layer_flag = 1
        else:
            noise_layer_flag = 0
        # 创建频谱滤波器
        self.filter = GlobalFilter_spec(dim, h=h, w=w,
                                        mask_radio=mask_radio,
                                        mask_alpha=mask_alpha,
                                        noise_mode=noise_mode,
                                        low_or_high=low_or_high, uncertainty_model=uncertainty_model,
                                        perturb_prob=perturb_prob,
                                        uncertainty_factor=uncertainty_factor,
                                        noise_layer_flag=noise_layer_flag, gauss_or_uniform=gauss_or_uniform, )
        # 创建DropPath层用于随机深度
        self.drop_path = DropPath(drop_path) if drop_path > 0. else nn.Identity()
        # 创建第二层归一化层
        self.norm2 = norm_layer(dim)
        # 计算MLP隐藏维度
        mlp_hidden_dim = int(dim * mlp_ratio)
        # 创建MLP层
        self.mlp = Mlp(in_features=dim, hidden_features=mlp_hidden_dim, act_layer=act_layer, drop=drop)
        # 创建可学习的缩放参数gamma
        self.gamma = nn.Parameter(init_values * torch.ones((dim)), requires_grad=True)

        # 保存层索引
        self.layer_index = layer_index  # where is the block in

    # 前向传播函数
    def forward(self, input):
        # 保存输入
        x = input

        # 应用残差连接和缩放
        x = x + self.drop_path(self.gamma * self.mlp(self.norm2(self.filter(self.norm1(x), self.layer_index))))
        # 返回输出
        return x

    # 重复的前向传播函数（可能是冗余代码）
    def forward(self, input):
        # 保存输入
        x = input

        # 应用残差连接和缩放
        x = x + self.drop_path(self.gamma * self.mlp(self.norm2(self.filter(self.norm1(x), self.layer_index))))
        # 返回输出
        return x


# 定义空间块层缩放类
class spa_BlockLayerScale(nn.Module):
    # 初始化函数，设置空间块参数
    def __init__(self, dim, mlp_ratio=4., drop=0., drop_path=0., act_layer=nn.GELU,
                 norm_layer=nn.LayerNorm, h=14, w=8, init_values=1e-5,
                 mask_radio=0.1, mask_alpha=0.5, noise_mode=1, low_or_high=0,
                 uncertainty_model=0, perturb_prob=0.5, uncertainty_factor=1.0,
                 layer_index=0, noise_layers=[0, 1, 2, 3], gauss_or_uniform=0, ):
        # 调用父类构造函数
        super().__init__()
        # 创建第一层归一化层
        self.norm1 = norm_layer(dim)

        # 根据层索引决定是否启用噪声层
        if layer_index in noise_layers:
            noise_layer_flag = 1
        else:
            noise_layer_flag = 0
        # 创建空间滤波器
        self.filter = GlobalFilter_spa(dim, h=h, w=w,
                                       mask_radio=mask_radio,
                                       mask_alpha=mask_alpha,
                                       noise_mode=noise_mode,
                                       low_or_high=low_or_high, uncertainty_model=uncertainty_model,
                                       perturb_prob=perturb_prob,
                                       uncertainty_factor=uncertainty_factor,
                                       noise_layer_flag=noise_layer_flag, gauss_or_uniform=gauss_or_uniform, )
        # 创建DropPath层用于随机深度
        self.drop_path = DropPath(drop_path) if drop_path > 0. else nn.Identity()
        # 创建第二层归一化层
        self.norm2 = norm_layer(dim)
        # 计算MLP隐藏维度
        mlp_hidden_dim = int(dim * mlp_ratio)
        # 创建MLP层
        self.mlp = Mlp(in_features=dim, hidden_features=mlp_hidden_dim, act_layer=act_layer, drop=drop)
        # 创建可学习的缩放参数gamma
        self.gamma = nn.Parameter(init_values * torch.ones((dim)), requires_grad=True)

        # 保存层索引
        self.layer_index = layer_index  # where is the block in

    # 前向传播函数
    def forward(self, input):
        # 保存输入
        x = input

        # 应用残差连接和缩放
        x = x + self.drop_path(self.gamma * self.mlp(self.norm2(self.filter(self.norm1(x), self.layer_index))))
        # 返回输出
        return x


# 定义空间随机化类
class SpaRandomization(nn.Module):
    # 初始化函数，设置空间随机化参数
    def __init__(self, num_features, eps=1e-5, device=0):
        # 调用父类构造函数
        super().__init__()
        # 保存数值稳定性的小量
        self.eps = eps
        # 创建实例归一化层
        self.norm = nn.InstanceNorm2d(num_features, affine=False)
        # 创建可学习的alpha参数
        self.alpha = nn.Parameter(torch.tensor(0.5), requires_grad=True).to(device)

    # 前向传播函数
    def forward(self, x, ):
        # 获取输入张量的维度
        N, C, H, W = x.size()
        # x = self.norm(x)
        # 如果处于训练模式
        if self.training:
            # 重塑张量为3D格式
            x = x.view(N, C, -1)
            # 计算均值和方差
            mean = x.mean(-1, keepdim=True)
            var = x.var(-1, keepdim=True)

            # 进行归一化
            x = (x - mean) / (var + self.eps).sqrt()

            # 随机打乱样本顺序
            idx_swap = torch.randperm(N)
            # 生成随机alpha值
            alpha = torch.rand(N, 1, 1)
            # 混合均值
            mean = self.alpha * mean + (1 - self.alpha) * mean[idx_swap]
            # 混合方差
            var = self.alpha * var + (1 - self.alpha) * var[idx_swap]

            # 重构归一化后的张量
            x = x * (var + self.eps).sqrt() + mean
            # 重塑回4D格式
            x = x.view(N, C, H, W)

        # 返回处理后的张量和交换索引
        return x, idx_swap


# 定义频谱随机化类
class SpeRandomization(nn.Module):
    # 初始化函数，设置频谱随机化参数
    def __init__(self, num_features, eps=1e-5):
        # 调用父类构造函数
        super().__init__()
        # 保存数值稳定性的小量
        self.eps = eps
        # 创建实例归一化层
        self.norm = nn.InstanceNorm2d(num_features, affine=False)

    # 前向传播函数
    def forward(self, x, idx_swap, y=None):
        # 获取输入张量的维度
        N, C, H, W = x.size()

        # 如果处于训练模式
        if self.training:
            # 重塑张量为3D格式
            x = x.view(N, C, -1)
            # 计算均值和方差（沿通道维度）
            mean = x.mean(1, keepdim=True)
            var = x.var(1, keepdim=True)

            # 进行归一化
            x = (x - mean) / (var + self.eps).sqrt()
            # 如果提供了标签y
            if y != None:
                # 按类别进行随机化
                for i in range(len(y.unique())):
                    # 创建当前类别的掩码
                    index = y == y.unique()[i]
                    # 提取当前类别的数据
                    tmp, mean_tmp, var_tmp = x[index], mean[index], var[index]
                    # 随机打乱当前类别的样本
                    tmp = tmp[torch.randperm(tmp.size(0))].detach()
                    # 重构当前类别的数据
                    tmp = tmp * (var_tmp + self.eps).sqrt() + mean_tmp
                    # 更新原数据
                    x[index] = tmp
            else:
                # 使用提供的交换索引进行随机化
                # idx_swap = torch.randperm(N)
                x = x[idx_swap].detach()

                # 重构归一化后的张量
                x = x * (var + self.eps).sqrt() + mean
            # 重塑回4D格式
            x = x.view(N, C, H, W)
        # 返回处理后的张量
        return x


# 定义自适应实例归一化2D类
class AdaIN2d(nn.Module):
    # 初始化函数，设置AdaIN参数
    def __init__(self, style_dim, num_features):
        # 调用父类构造函数
        super().__init__()
        # 创建实例归一化层
        self.norm = nn.InstanceNorm2d(num_features, affine=False)
        # 创建风格映射的全连接层
        self.fc = nn.Linear(style_dim, num_features * 2)

    # 前向传播函数
    def forward(self, x, s):
        # 通过全连接层处理风格向量
        h = self.fc(s)
        # 重塑张量为4D格式
        h = h.view(h.size(0), h.size(1), 1, 1)
        # 分割为gamma和beta参数
        gamma, beta = torch.chunk(h, chunks=2, dim=1)
        # 应用AdaIN变换
        return (1 + gamma) * self.norm(x) + beta
        # return (1+gamma)*(x)+beta


# 定义重塑类
class Reshape(nn.Module):
    # 初始化函数，设置重塑参数
    def __init__(self, *args):
        # 调用父类构造函数
        super(Reshape, self).__init__()
        # 保存目标形状
        self.shape = args

    # 前向传播函数
    def forward(self, x):
        # 重塑张量为目标形状
        return x.view((x.size(0),) + self.shape)


# 定义生成器网络类，继承自PyTorch神经网络模块
class Generator(nn.Module):
    # 初始化函数，设置生成器网络结构
    def __init__(self, n=16, kernelsize=3, imdim=3, imsize=[13, 13], zdim=10, device=0, low_freq=False):
        ''' w_ln 局部噪声权重
        '''
        # 调用父类构造函数
        super().__init__()
        # 计算卷积步长
        stride = (kernelsize - 1) // 2
        # 保存潜在空间维度
        self.zdim = zdim
        # 保存图像维度
        self.imdim = imdim
        # 保存图像尺寸
        self.imsize = imsize
        # 保存设备信息
        self.device = device
        # 保存频率扰动标志
        self.freq_dis = low_freq
        # 设置形态学特征数量
        num_morph = 4
        # 创建形态学网络
        self.Morphology = MorphNet(imdim)
        # 创建AdaIN层用于形态学特征
        self.adain2_morph = AdaIN2d(zdim, num_morph)

        # 创建空间特征卷积层
        self.conv_spa1 = nn.Conv2d(imdim, 3, 1, 1)
        self.conv_spa2 = nn.Conv2d(3, n, 1, 1)
        # 创建频谱特征卷积层
        self.conv_spe1 = nn.Conv2d(imdim, n, imsize[0], 1)
        self.conv_spe2 = nn.ConvTranspose2d(n, n, imsize[0])
        # 创建融合卷积层
        self.conv1 = nn.Conv2d(n + n + num_morph, n, kernelsize, 1, stride)
        self.conv2 = nn.Conv2d(n, imdim, kernelsize, 1, stride)
        # 创建频谱和空间随机化层
        self.speRandom = SpeRandomization(n)
        self.spaRandom = SpaRandomization(3, device=device)

        # 创建低频空间和频谱块层
        self.low_freq_spa = spa_BlockLayerScale(dim=imdim, h=imsize[0], w=imsize[1], uncertainty_model=2)
        self.low_freq_spec = spec_BlockLayerScale(dim=imdim, h=imsize[0], w=imsize[1], uncertainty_model=2)
        # 创建频率融合卷积层
        self.convFRE = nn.Conv2d(2 * imdim, imdim, kernelsize, 1, stride)

    # 前向传播函数
    def forward(self, x):
        # 如果启用频率扰动
        if self.freq_dis:
            # 我们的编码器
            B, C, H, W = x.shape
            # 重塑张量为序列格式
            x1 = x.view(B, C, -1)
            x1 = x1.transpose(1, 2)
            # 应用低频空间和频谱块
            x_spa = self.low_freq_spa((x1))
            x_spec = self.low_freq_spec((x1))
            # 转换回图像格式
            x_spa = x_spa.transpose(1, 2)
            x_spa = x_spa.view(B, C, H, W)
            x_spec = x_spec.transpose(1, 2)
            x_spec = x_spec.view(B, C, H, W)
            # 连接空间和频谱特征
            x = torch.cat((x_spec, x_spa), dim=1)
            # 应用频率融合卷积
            x = self.convFRE(x)
        else:
            # SDGNet的编码器
            # 提取形态学特征
            x_morph = self.Morphology(x)
            # 生成随机风格向量
            z = torch.randn(len(x), self.zdim).to(self.device)
            # 应用AdaIN到形态学特征
            x_morph = self.adain2_morph(x_morph, z)

            # 提取空间特征
            x_spa = F.relu(self.conv_spa1(x))
            # 提取频谱特征
            x_spe = F.relu(self.conv_spe1(x))
            # 应用空间和频谱随机化
            x_spa, idx_swap = self.spaRandom(x_spa)
            x_spe = self.speRandom(x_spe, idx_swap)
            # 上采样频谱特征
            x_spe = self.conv_spe2(x_spe)
            # 处理空间特征
            x_spa = self.conv_spa2(x_spa)

            # 融合所有特征并生成输出
            x = F.relu(self.conv1(torch.cat((x_spa, x_spe, x_morph), 1)))
            x = torch.sigmoid(self.conv2(x))

        # 返回生成的图像
        return x