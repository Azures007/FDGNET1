
# --------------------------------------------------------
# 版权所有 (c) 2021 Microsoft
# 根据MIT许可证授权
# --------------------------------------------------------

# 导入PyTorch深度学习框架
import torch
# 从PyTorch导入Tensor类型
from torch import Tensor
# 导入PyTorch神经网络模块
import torch.nn as nn
# 导入NumPy数值计算库
import numpy as np

# 定义梯度反转钩子函数生成器
def grl_hook(coeff):
    # 定义内部函数，用于梯度反转
    def func_(grad):
        # 返回反转后的梯度（乘以负系数）
        return -coeff * grad.clone()

    # 返回梯度反转函数
    return func_

# 定义熵计算函数
def entropy_func(x: Tensor) -> Tensor:
    """
    计算输入张量的熵
    :param x: [N, C] 概率分布矩阵
    :return: entropy: [N,] 每个样本的熵值
    """
    # 设置小的epsilon值避免log(0)
    epsilon = 1e-5
    # 计算熵：-p*log(p)
    entropy = -x * torch.log(x + epsilon)
    # 对所有类别求和得到每个样本的总熵
    entropy = torch.sum(entropy, dim=1)
    # 返回熵值向量
    return entropy

# 定义加权二元交叉熵损失类，继承自PyTorch神经网络模块
class WeightBCE(nn.Module):
    # 初始化函数，设置损失函数参数
    def __init__(self, epsilon: float = 1e-8) -> None:
        # 调用父类构造函数
        super(WeightBCE, self).__init__()
        # 保存数值稳定性的小量epsilon
        self.epsilon = epsilon

    # 前向传播函数，计算加权二元交叉熵损失
    def forward(self, x: Tensor, label: Tensor, weight: Tensor) -> Tensor:
        """
        :param x: [N, 1] 预测概率
        :param label: [N, 1] 真实标签
        :param weight: [N, 1] 样本权重
        """
        # 将标签转换为浮点数类型
        label = label.float()
        # 计算二元交叉熵：-y*log(p) - (1-y)*log(1-p)
        cross_entropy = - label * torch.log(x + self.epsilon) - (1 - label) * torch.log(1 - x + self.epsilon)
        # 应用权重并计算平均损失
        return torch.sum(cross_entropy * weight.float()) / 2.

# 定义域对齐UDA损失函数
def  d_align_uda(softmax_output: Tensor, features: Tensor = None, d_net=None,
                coeff: float = None, ent: bool = False):
    # 创建加权二元交叉熵损失实例
    loss_func = WeightBCE()

    # 选择域判别器的输入（特征或softmax输出）
    d_input = softmax_output if features is None else features
    # 通过域判别器网络
    d_output = d_net(d_input, coeff=coeff)
    # 应用sigmoid激活函数
    d_output = torch.sigmoid(d_output)

    # 计算批次大小（假设源域和目标域样本数量相等）
    batch_size = softmax_output.size(0) // 2
    # 创建域标签：前半部分为源域(1)，后半部分为目标域(0)
    labels = torch.tensor([[1]] * batch_size + [[0]] * batch_size).long().cuda()  # 2N x 1

    # 如果启用熵加权
    if ent:
        # 获取softmax输出
        x = softmax_output
        # 计算熵值
        entropy = entropy_func(x)
        # 注册梯度反转钩子
        entropy.register_hook(grl_hook(coeff))
        # 对熵值取指数的倒数作为权重
        entropy = torch.exp(-entropy)

        # 创建源域掩码
        source_mask = torch.ones_like(entropy)
        source_mask[batch_size:] = 0
        source_weight = entropy * source_mask
        # 创建目标域掩码
        target_mask = torch.ones_like(entropy)
        target_mask[:batch_size] = 0
        target_weight = entropy * target_mask
        # 归一化权重
        weight = source_weight / torch.sum(source_weight).detach().item() + \
                 target_weight / torch.sum(target_weight).detach().item()

    else:
        # 如果不使用熵加权，使用均匀权重
        weight = torch.ones_like(labels).float() / batch_size

    # 计算域对齐损失
    loss_alg = loss_func.forward(d_output, labels, weight.view(-1, 1))

    # 返回计算得到的损失
    return loss_alg

# 定义多源域对齐MSDA损失函数
def d_align_msda(softmax_output: Tensor, features: Tensor = None, d_net=None,
                 coeff: float = None, ent: bool = False, batchsizes: list = []):
    # 选择域判别器的输入
    d_input = softmax_output if features is None else features
    # 通过域判别器网络
    d_output = d_net(d_input, coeff=coeff)

    # 根据批次大小创建域标签
    labels = torch.cat(
        (torch.tensor([1] * batchsizes[0]).long(), torch.tensor([0] * batchsizes[1]).long()), 0
    ).cuda()  # [B_S + B_T]

    # 如果启用熵加权
    if ent:
        # 获取softmax输出
        x = softmax_output
        # 计算熵值
        entropy = entropy_func(x)
        # 注册梯度反转钩子
        entropy.register_hook(grl_hook(coeff))
        # 对熵值取指数的倒数作为权重
        entropy = torch.exp(-entropy)

        # 创建源域掩码
        source_mask = torch.ones_like(entropy)
        source_mask[batchsizes[0]:] = 0
        source_weight = entropy * source_mask
        # 创建目标域掩码
        target_mask = torch.ones_like(entropy)
        target_mask[:batchsizes[0]] = 0
        target_weight = entropy * target_mask
        # 归一化权重
        weight = source_weight / torch.sum(source_weight).detach().item() + \
                 target_weight / torch.sum(target_weight).detach().item()

    else:
        # 如果不使用熵加权，使用均匀权重
        weight = torch.ones_like(labels).float() / softmax_output.shape[0]

    # 计算交叉熵损失（不进行平均）
    loss_ce = nn.CrossEntropyLoss(reduction='none')(d_output, labels)
    # 应用权重计算最终损失
    loss_alg = torch.sum(weight * loss_ce)

    # 返回计算得到的损失
    return loss_alg

# 定义局部最大均值差异损失类，继承自PyTorch神经网络模块
class LMMDLoss(nn.Module):
    # 初始化函数，设置LMMD损失参数
    def __init__(self, num_class, kernel_type='rbf', kernel_mul=2.0, kernel_num=5, fix_sigma=None,
                 gamma=1.0, max_iter=1000, **kwargs):
        '''
        局部MMD损失
        '''
        # 调用父类构造函数
        super(LMMDLoss, self).__init__()
        # 保存核函数相关参数
        self.kernel_num = kernel_num
        self.kernel_mul = kernel_mul
        self.fix_sigma = fix_sigma
        self.kernel_type = kernel_type
        # 保存动态权重参数
        self.gamma=gamma
        self.max_iter=max_iter
        # 初始化当前迭代次数
        self.curr_iter = 0

        # 保存类别数量
        self.num_class = num_class

    # 定义高斯核函数
    def guassian_kernel(self, source, target, kernel_mul=2.0, kernel_num=5, fix_sigma=None):
        # 计算总样本数
        n_samples = int(source.size()[0]) + int(target.size()[0])
        # 连接源域和目标域样本
        total = torch.cat([source, target], dim=0)
        # 创建用于计算距离的张量
        total0 = total.unsqueeze(0).expand(
            int(total.size(0)), int(total.size(0)), int(total.size(1)))
        total1 = total.unsqueeze(1).expand(
            int(total.size(0)), int(total.size(0)), int(total.size(1)))
        # 计算欧几里得距离的平方
        L2_distance = ((total0-total1)**2).sum(2)
        # 确定带宽参数
        if fix_sigma:
            bandwidth = fix_sigma
        else:
            bandwidth = torch.sum(L2_distance.data) / (n_samples**2-n_samples)
        # 调整带宽
        bandwidth /= kernel_mul ** (kernel_num // 2)
        # 创建多个带宽的核函数
        bandwidth_list = [bandwidth * (kernel_mul**i)
                          for i in range(kernel_num)]
        # 计算高斯核值
        kernel_val = [torch.exp(-L2_distance / bandwidth_temp)
                      for bandwidth_temp in bandwidth_list]
        # 返回核函数之和
        return sum(kernel_val)

    # 计算动态权重lambda
    def lamb(self):
        # 计算迭代进度比例
        p = self.curr_iter / self.max_iter
        # 计算sigmoid函数形式的权重
        lamb = 2. / (1. + np.exp(-self.gamma * p)) - 1
        # 返回计算得到的lambda值
        return lamb

    # 前向传播函数，计算LMMD损失
    def forward(self, source, target, source_label, target_logits):
        # 如果使用线性核（暂未实现）
        if self.kernel_type == 'linear':
            raise NotImplementedError("Linear kernel is not supported yet.")

        # 如果使用RBF核
        elif self.kernel_type == 'rbf':
            # 获取批次大小
            batch_size = source.size()[0]
            # 计算类别权重
            weight_ss, weight_tt, weight_st = self.cal_weight(source_label, target_logits)
            # 将权重转换为CUDA张量
            weight_ss = torch.from_numpy(weight_ss).cuda()  # B, B
            weight_tt = torch.from_numpy(weight_tt).cuda()
            weight_st = torch.from_numpy(weight_st).cuda()

            # 计算高斯核矩阵
            kernels = self.guassian_kernel(source, target,
                                           kernel_mul=self.kernel_mul, kernel_num=self.kernel_num,
                                           fix_sigma=self.fix_sigma)
            # 初始化损失张量
            loss = torch.Tensor([0]).cuda()
            # 检查是否存在NaN值
            if torch.sum(torch.isnan(sum(kernels))):
                return loss
            # 提取不同区域的核矩阵
            SS = kernels[:batch_size, :batch_size]
            TT = kernels[batch_size:, batch_size:]
            ST = kernels[:batch_size, batch_size:]

            # 计算LMMD损失
            loss += torch.sum(weight_ss * SS + weight_tt * TT - 2 * weight_st * ST)
            # 动态加权
            lamb = self.lamb()
            self.step()
            loss = loss * lamb
            # 返回计算得到的损失
            return loss

    # 更新迭代次数
    def step(self):
        # 递增当前迭代次数，不超过最大迭代次数
        self.curr_iter = min(self.curr_iter + 1, self.max_iter)

    # 计算类别权重函数
    def cal_weight(self, source_label, target_logits):
        # 获取批次大小
        batch_size = source_label.size()[0]
        # 将源域标签转换为numpy数组
        source_label = source_label.cpu().data.numpy()
        # 转换为one-hot编码
        source_label_onehot = np.eye(self.num_class)[source_label]  # one hot

        # 计算源域各类别样本数量
        source_label_sum = np.sum(source_label_onehot, axis=0).reshape(1, self.num_class)
        source_label_sum[source_label_sum == 0] = 100
        # 归一化得到标签比例
        source_label_onehot = source_label_onehot / source_label_sum  # label ratio

        # 生成目标域伪标签
        target_label = target_logits.cpu().data.max(1)[1].numpy()

        # 处理目标域预测概率
        target_logits = target_logits.cpu().data.numpy()
        target_logits_sum = np.sum(target_logits, axis=0).reshape(1, self.num_class)
        target_logits_sum[target_logits_sum == 0] = 100
        target_logits = target_logits / target_logits_sum

        # 初始化权重矩阵
        weight_ss = np.zeros((batch_size, batch_size))
        weight_tt = np.zeros((batch_size, batch_size))
        weight_st = np.zeros((batch_size, batch_size))

        # 获取源域和目标域的标签集合
        set_s = set(source_label)
        set_t = set(target_label)
        count = 0
        # 遍历所有类别
        for i in range(self.num_class):  # (B, C)
            # 如果该类别在源域和目标域中都存在
            if i in set_s and i in set_t:
                # 提取源域第i类的分布
                s_tvec = source_label_onehot[:, i].reshape(batch_size, -1)  # (B, 1)

                # 提取目标域第i类的分布
                t_tvec = target_logits[:, i].reshape(batch_size, -1)  # (B, 1)

                # 计算各类别间的权重
                ss = np.dot(s_tvec, s_tvec.T)  # (B, B)
                weight_ss = weight_ss + ss
                tt = np.dot(t_tvec, t_tvec.T)
                weight_tt = weight_tt + tt
                st = np.dot(s_tvec, t_tvec.T)
                weight_st = weight_st + st
                count += 1

        # 计算平均权重
        length = count
        if length != 0:
            weight_ss = weight_ss / length
            weight_tt = weight_tt / length
            weight_st = weight_st / length
        else:
            weight_ss = np.array([0])
            weight_tt = np.array([0])
            weight_st = np.array([0])
        # 返回各类别权重矩阵
        return weight_ss.astype('float32'), weight_tt.astype('float32'), weight_st.astype('float32')

# 定义CORAL损失函数（协方差对齐）
def CORAL(source, target):
    # 获取特征维度
    d = source.data.shape[1]
    # 获取源域和目标域样本数量
    ns, nt = source.data.shape[0], target.data.shape[0]
    # 计算源域协方差矩阵
    xm = torch.mean(source, 0, keepdim=True) - source
    xc = xm.t() @ xm / (ns - 1)

    # 计算目标域协方差矩阵
    xmt = torch.mean(target, 0, keepdim=True) - target
    xct = xmt.t() @ xmt / (nt - 1)

    # 计算源域和目标域协方差矩阵的Frobenius范数差异
    loss = torch.mul((xc - xct), (xc - xct))
    loss = torch.sum(loss) / (4*d*d)
    # 返回CORAL损失
    return loss

# 定义MMD损失类，继承自PyTorch神经网络模块
class MMD_loss(nn.Module):
    # 初始化函数，设置MMD损失参数
    def __init__(self, kernel_type='rbf', kernel_mul=2.0, kernel_num=5):
        # 调用父类构造函数
        super(MMD_loss, self).__init__()
        # 保存核函数相关参数
        self.kernel_num = kernel_num
        self.kernel_mul = kernel_mul
        self.fix_sigma = None
        self.kernel_type = kernel_type

    # 定义高斯核函数
    def guassian_kernel(self, source, target, kernel_mul=2.0, kernel_num=5, fix_sigma=None):
        # 计算总样本数
        n_samples = int(source.size()[0]) + int(target.size()[0])
        # 连接源域和目标域样本
        total = torch.cat([source, target], dim=0)
        # 创建用于计算距离的张量
        total0 = total.unsqueeze(0).expand(
            int(total.size(0)), int(total.size(0)), int(total.size(1)))
        total1 = total.unsqueeze(1).expand(
            int(total.size(0)), int(total.size(0)), int(total.size(1)))
        # 计算欧几里得距离的平方
        L2_distance = ((total0-total1)**2).sum(2)
        # 确定带宽参数
        if fix_sigma:
            bandwidth = fix_sigma
        else:
            bandwidth = torch.sum(L2_distance.data) / (n_samples**2-n_samples)
        # 调整带宽
        bandwidth /= kernel_mul ** (kernel_num // 2)
        # 创建多个带宽的核函数
        bandwidth_list = [bandwidth * (kernel_mul**i)
                          for i in range(kernel_num)]
        # 计算高斯核值
        kernel_val = [torch.exp(-L2_distance / bandwidth_temp)
                      for bandwidth_temp in bandwidth_list]
        # 返回核函数之和
        return sum(kernel_val)

    # 定义线性MMD计算函数
    def linear_mmd2(self, f_of_X, f_of_Y):
        # 初始化损失
        loss = 0.0
        # 计算源域和目标域特征均值的差异
        delta = f_of_X.float().mean(0) - f_of_Y.float().mean(0)
        # 计算差异的平方范数
        loss = delta.dot(delta.T)
        # 返回线性MMD损失
        return loss

    # 前向传播函数，计算MMD损失
    def forward(self, source, target):
        # 如果使用线性核
        if self.kernel_type == 'linear':
            return self.linear_mmd2(source, target)
        # 如果使用RBF核
        elif self.kernel_type == 'rbf':
            # 获取批次大小
            batch_size = int(source.size()[0])
            # 计算高斯核矩阵
            kernels = self.guassian_kernel(
                source, target, kernel_mul=self.kernel_mul, kernel_num=self.kernel_num, fix_sigma=self.fix_sigma)
            # 在无梯度模式下计算MMD各项
            with torch.no_grad():
                # 计算源域内部的平均核值
                XX = torch.mean(kernels[:batch_size, :batch_size])
                # 计算目标域内部的平均核值
                YY = torch.mean(kernels[batch_size:, batch_size:])
                # 计算源域到目标域的平均核值
                XY = torch.mean(kernels[:batch_size, batch_size:])
                # 计算目标域到源域的平均核值
                YX = torch.mean(kernels[batch_size:, :batch_size])
                # 计算最终的MMD损失
                loss = torch.mean(XX + YY - XY - YX)
                # 删除临时变量释放内存
                del XX, YY, XY, YX
            # 清空CUDA缓存
            torch.cuda.empty_cache()
            # 返回计算得到的MMD损失
            return loss