# 导入数学模块
import math
# 导入PyTorch深度学习框架
import torch
# 导入PyTorch神经网络模块
import torch.nn as nn
# 导入PyTorch函数式API
import torch.nn.functional as F


# 定义形态学操作基类，继承自PyTorch神经网络模块
class Morphology(nn.Module):
    '''
    形态学算子的基类
    目前仅支持stride=1, dilation=1, kernel_size H==W, 以及padding='same'的配置。
    '''

    # 初始化函数，设置形态学网络参数
    def __init__(self, in_channels, out_channels, kernel_size=5, soft_max=True, beta=15, type=None):
        '''
        in_channels: 标量，输入通道数
        out_channels: 标量，形态学神经元的数量
        kernel_size: 标量，形态学神经元的空间尺寸
        soft_max: 布尔值，使用soft max而非torch.max()，参考：Dense Morphological Networks: An Universal Function Approximator (Mondal et al. (2019))
        beta: 标量，soft_max使用的参数
        type: 字符串，'dilation2d'或'erosion2d'
        '''
        # 调用父类构造函数
        super(Morphology, self).__init__()
        # 保存输入通道数
        self.in_channels = in_channels
        # 保存输出通道数（形态学神经元数量）
        self.out_channels = out_channels
        # 保存卷积核尺寸
        self.kernel_size = kernel_size
        # 保存是否使用soft max
        self.soft_max = soft_max
        # 保存soft max的beta参数
        self.beta = beta
        # 保存操作类型（膨胀或腐蚀）
        self.type = type

        # 创建可学习的权重参数：(out_channels, in_channels, kernel_size, kernel_size)
        self.weight = nn.Parameter(torch.ones(out_channels, in_channels, kernel_size, kernel_size), requires_grad=True)
        # 创建unfold操作用于提取图像块
        self.unfold = nn.Unfold(kernel_size, dilation=1, padding=0, stride=1)

    # 前向传播函数
    def forward(self, x):
        '''
        x: 形状为 (B,C,H,W) 的张量
        '''
        # 进行固定填充以保持尺寸不变
        x = fixed_padding(x, self.kernel_size, dilation=1)

        # 展开操作：将图像分割为重叠的块
        x = self.unfold(x)  # (B, Cin*kH*kW, L), 其中L是块的数量
        x = x.unsqueeze(1)  # (B, 1, Cin*kH*kW, L)
        # 获取块的数量
        L = x.size(-1)
        # 计算块数量的平方根（假设是方形排列）
        L_sqrt = int(math.sqrt(L))

        # 腐蚀操作准备
        weight = self.weight.view(self.out_channels, -1)  # (Cout, Cin*kH*kW)
        weight = weight.unsqueeze(0).unsqueeze(-1)  # (1, Cout, Cin*kH*kW, 1)

        # 根据操作类型执行相应计算
        if self.type == 'erosion2d':
            # 腐蚀操作：权重减去输入
            x = weight - x  # (B, Cout, Cin*kH*kW, L)
        elif self.type == 'dilation2d':
            # 膨胀操作：权重加上输入
            x = weight + x  # (B, Cout, Cin*kH*kW, L)
        else:
            # 如果操作类型无效则抛出错误
            raise ValueError

        # 根据是否使用soft max选择不同的聚合方式
        if not self.soft_max:
            # 使用最大值聚合
            x, _ = torch.max(x, dim=2, keepdim=False)  # (B, Cout, L)
        else:
            # 使用soft max聚合（更加平滑）
            x = torch.logsumexp(x * self.beta, dim=2, keepdim=False) / self.beta  # (B, Cout, L)

        # 对于腐蚀操作，需要取负值
        if self.type == 'erosion2d':
            x = -1 * x

        # 重塑张量恢复空间结构（避免使用fold操作的内存拷贝）
        x = x.view(-1, self.out_channels, L_sqrt, L_sqrt)  # (B, Cout, L/2, L/2)

        # 返回处理后的张量
        return x

    # 定义二维膨胀操作类，继承自形态学基类


class Dilation2d(Morphology):
    # 初始化函数，设置膨胀操作参数
    def __init__(self, in_channels, out_channels, kernel_size=5, soft_max=True, beta=20):
        # 调用父类构造函数，指定操作类型为膨胀
        super(Dilation2d, self).__init__(in_channels, out_channels, kernel_size, soft_max, beta, 'dilation2d')


# 定义二维腐蚀操作类，继承自形态学基类
class Erosion2d(Morphology):
    # 初始化函数，设置腐蚀操作参数
    def __init__(self, in_channels, out_channels, kernel_size=5, soft_max=True, beta=20):
        # 调用父类构造函数，指定操作类型为腐蚀
        super(Erosion2d, self).__init__(in_channels, out_channels, kernel_size, soft_max, beta, 'erosion2d')


# 定义固定填充函数
def fixed_padding(inputs, kernel_size, dilation):
    # 计算有效的卷积核尺寸（考虑膨胀率）
    kernel_size_effective = kernel_size + (kernel_size - 1) * (dilation - 1)
    # 计算总的填充量
    pad_total = kernel_size_effective - 1
    # 计算开始和结束的填充量
    pad_beg = pad_total // 2
    pad_end = pad_total - pad_beg
    # 对输入进行对称填充
    padded_inputs = F.pad(inputs, (pad_beg, pad_end, pad_beg, pad_end))
    # 返回填充后的输入
    return padded_inputs