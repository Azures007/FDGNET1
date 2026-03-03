# 导入print_function以确保Python 2/3兼容性
from __future__ import print_function

# 导入PyTorch深度学习框架
import torch
# 导入PyTorch神经网络模块
import torch.nn as nn
# 导入NumPy数值计算库
import numpy as np


# 定义流形距离计算函数
def manifold_dis(x, y):
    # 计算x样本间的成对距离矩阵
    pd_r = pairwise_distances(x, x)
    # 计算y样本间的成对距离矩阵
    pd_f = pairwise_distances(y, y)

    # pd_r = pairwise_distances(F.normalize(p_SD), F.normalize(p_SD))
    # pd_f = pairwise_distances(F.normalize(p_ED), F.normalize(p_ED))

    # 计算距离矩阵间的欧几里得距离（流形结构差异）
    p_dist = torch.dist(pd_r, pd_f, 2)
    # 计算样本均值间的欧几里得距离（中心点差异）
    c_dist = torch.dist(x.mean(0), y.mean(0), 2)
    # #
    # 返回流形距离和中心距离
    return p_dist, c_dist


# 定义成对距离计算函数
def pairwise_distances(x, y):
    '''
    输入: x 是 Nxd 矩阵
           y 是可选的 Mxd 矩阵
    输出: dist 是 NxM 矩阵，其中 dist[i,j] 是 x[i,:] 和 y[j,:] 之间的平方范数
            如果没有给出y，则使用 'y=x'。
    即 dist[i,j] = ||x[i,:]-y[j,:]||^2
    '''
    # 计算x每行的平方和并重塑为列向量
    x_norm = (x ** 2).sum(1).view(-1, 1)
    # 如果提供了y
    if y is not None:
        # 转置y矩阵
        y_t = torch.transpose(y, 0, 1)
        # 计算y每行的平方和并重塑为行向量
        y_norm = (y ** 2).sum(1).view(1, -1)
    else:
        # 如果没有提供y，则使用x的转置
        y_t = torch.transpose(x, 0, 1)
        # 使用x的范数
        y_norm = x_norm.view(1, -1)

    # 计算成对距离公式：||x||² + ||y||² - 2<x,y>
    dist = x_norm + y_norm - 2.0 * torch.mm(x, y_t)
    # 确保对角线为零（当x=y时）
    # if y is None:
    #     dist = dist - torch.diag(dist.diag)
    # 限制距离值在[0, inf]范围内
    return torch.clamp(dist, 0.0, np.inf)


# 定义监督对比损失类，继承自PyTorch神经网络模块
class SupConLoss(nn.Module):
    """监督对比学习: https://arxiv.org/pdf/2004.11362.pdf.
    同时也支持SimCLR中的无监督对比损失"""

    # 初始化函数，设置损失函数参数
    def __init__(self, temperature=0.07, contrast_mode='all',
                 base_temperature=0.07, device=None):
        # 调用父类构造函数
        super(SupConLoss, self).__init__()
        # 保存温度参数，控制分布的锐度
        self.temperature = temperature
        # 保存对比模式（'one'或'all'）
        self.contrast_mode = contrast_mode
        # 保存基础温度参数
        self.base_temperature = base_temperature
        # 保存设备信息
        self.device = device

    # 前向传播函数，计算对比损失
    def forward(self, features, labels=None, mask=None, adv=False):
        """为模型计算损失。如果`labels`和`mask`都为None，
        则退化为SimCLR无监督损失：
        https://arxiv.org/pdf/2002.05709.pdf

        参数:
            features: 形状为 [bsz, n_views, ...] 的隐藏向量。
            labels: 形状为 [bsz] 的真实标签。
            mask: 形状为 [bsz, bsz] 的对比掩码，如果样本j与样本i属于同一类别则mask_{i,j}=1。可以是非对称的。
        返回:
            标量损失值。
        """
        # 确定计算设备
        if self.device is not None:
            device = self.device
        else:
            device = (torch.device('cuda')
                      if features.is_cuda
                      else torch.device('cpu'))

        # 检查特征维度是否符合要求
        if len(features.shape) < 3:
            raise ValueError('`features`需要是[bsz, n_views, ...]格式,'
                             '至少需要3个维度')
        # 如果特征维度超过3维，则展平多余维度
        if len(features.shape) > 3:
            features = features.view(features.shape[0], features.shape[1], -1)

        # 获取批次大小
        batch_size = features.shape[0]
        # 检查标签和掩码的设置
        if labels is not None and mask is not None:
            raise ValueError('不能同时定义`labels`和`mask`')
        elif labels is None and mask is None:
            # 如果都没有提供，则创建单位矩阵作为掩码（自对比）
            mask = torch.eye(batch_size, dtype=torch.float32).to(device)
        elif labels is not None:
            # 如果提供了标签
            labels = labels.contiguous().view(-1, 1)
            # 检查标签数量是否匹配特征数量
            if labels.shape[0] != batch_size:
                raise ValueError('标签数量与特征数量不匹配')
            # 创建基于标签相等性的掩码
            mask = torch.eq(labels, labels.T).float().to(device)
        else:
            # 使用提供的掩码
            mask = mask.float().to(device)

        # 获取对比视图数量
        contrast_count = features.shape[1]
        # 将所有视图的特征连接起来
        contrast_feature = torch.cat(torch.unbind(features, dim=1), dim=0)

        # 根据对比模式选择锚点特征
        if self.contrast_mode == 'one':
            # 只使用第一个视图作为锚点
            anchor_feature = features[:, 0]
            anchor_count = 1
        elif self.contrast_mode == 'all':
            # 使用所有视图作为锚点
            anchor_feature = contrast_feature
            anchor_count = contrast_count
        else:
            raise ValueError('未知模式: {}'.format(self.contrast_mode))

        # 计算logits（相似度分数）
        anchor_dot_contrast = torch.div(
            torch.matmul(anchor_feature, contrast_feature.T),
            self.temperature)
        # 为了数值稳定性，减去每行的最大值
        logits_max, _ = torch.max(anchor_dot_contrast, dim=1, keepdim=True)
        logits = anchor_dot_contrast - logits_max.detach()

        # 扩展掩码以匹配锚点和对比特征的数量
        mask = mask.repeat(anchor_count, contrast_count)
        # 创建logits掩码，排除自对比情况
        logits_mask = torch.scatter(
            torch.ones_like(mask),
            1,
            torch.arange(batch_size * anchor_count).view(-1, 1).to(device),
            0
        )
        # 应用掩码
        mask = mask * logits_mask
        # 计算指数logits
        exp_logits = torch.exp(logits) * logits_mask
        # log_prob = logits - torch.log(exp_logits.sum(1, keepdim=True))

        # 根据是否为对抗训练计算对数概率
        if adv:
            # 对抗训练模式：计算1-概率的对数
            log_prob = torch.log(1 - exp_logits / (exp_logits.sum(1, keepdim=True) + 1e-6) - 1e-6)
        else:
            # 标准对比学习模式：计算概率的对数
            log_prob = torch.log(exp_logits / (exp_logits.sum(1, keepdim=True) + 1e-6) + 1e-6)

        # 计算正样本的对数似然均值
        mean_log_prob_pos = (mask * log_prob).sum(1) / mask.sum(1)

        # 计算最终损失
        loss = - (self.temperature / self.base_temperature) * mean_log_prob_pos
        # 重塑损失并计算均值
        loss = loss.view(anchor_count, batch_size).mean()

        # 返回计算得到的损失
        return loss


# 主程序入口，用于测试
if __name__ == '__main__':
    # 导入PyTorch函数式API
    import torch.nn.functional as F

    # 设置随机种子以确保可重复性
    torch.manual_seed(0)
    # 生成随机特征数据：32个样本，每个样本2个视图，10维特征
    x = torch.randn(32, 2, 10)
    # 对特征进行归一化
    x = F.normalize(x)
    # 生成随机标签：32个样本，标签范围0-9
    y = torch.randint(0, 10, [32])
    # 创建监督对比损失实例
    loss_layer = SupConLoss()
    # 计算损失
    loss = loss_layer(x, y)
    # 打印损失值
    print(loss)