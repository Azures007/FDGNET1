# 导入PyTorch深度学习框架
import torch
# 导入PyTorch神经网络模块
import torch.nn as nn
# 导入PyTorch函数式API
import torch.nn.functional as F
# 从形态学层模块导入相关函数
from .morph_layers2D_torch import *
# 导入超图模块
import hypergraph
# 导入复制模块，用于深拷贝对象
import copy
# 导入上下文管理器模块
import contextlib
# 从PyTorch分布式模块导入规约操作
from torch.distributed import ReduceOp
# 从PyTorch批量归一化模块导入基础类
from torch.nn.modules.batchnorm import _BatchNorm


# from torch_geometric.data import Data
# from torch_geometric.nn import SAGEConv


# 定义判别器网络类，继承自PyTorch的nn.Module
class Discriminator(nn.Module):


    # 初始化函数，设置网络结构和参数
    def __init__(self, inchannel, outchannel, num_classes, patch_size, pad=False):
        # 调用父类构造函数
        super(Discriminator, self).__init__()
        # 设置隐藏层维度
        dim = 512
        # 保存补丁大小参数
        self.patch_size = patch_size
        # self.lambda1 = torch.nn.Parameter(torch.FloatTensor([0.95]), requires_grad=True)
        # 保存输入通道数
        self.inchannel = inchannel
        # 设置匹配配置
        self.matching_cfg = 'o2o'
        # self.node_affinity = Affinity(dim)
        # 设置匹配损失函数为均方误差损失
        self.matching_loss = nn.MSELoss(reduction='sum')
        # 设置是否使用超图
        self.with_hyper_graph = True
        # 设置超边数量
        self.num_hyper_edge = 3
        # 设置角度epsilon值
        self.angle_eps = 1e-3
        # 第一层卷积：输入通道->64通道，3x3卷积核，步长1，无填充
        self.conv1 = nn.Conv2d(inchannel, 64, kernel_size=3, stride=1, padding=0)
        # 最大池化层，2x2窗口
        self.mp = nn.MaxPool2d(2)
        # 第一个ReLU激活函数
        self.relu1 = nn.ReLU(inplace=True)
        # 根据pad参数决定第二层卷积的填充方式
        if pad:
            # 使用填充的卷积层
            self.conv2 = nn.Conv2d(64, 128, kernel_size=3, stride=1, padding=1)
        else:
            # 不使用填充的卷积层
            self.conv2 = nn.Conv2d(64, 128, kernel_size=3, stride=1, padding=0)
        # 第二个ReLU激活函数
        self.relu2 = nn.ReLU(inplace=True)
        # 全连接层1：将卷积特征展平后映射到dim维度
        self.fc1 = nn.Linear(self._get_final_flattened_size(), dim)
        # 第三个ReLU激活函数
        self.relu3 = nn.ReLU(inplace=True)
        # 全连接层2：dim->dim的映射
        self.fc2 = nn.Linear(dim, dim)
        # 第四个ReLU激活函数
        self.relu4 = nn.ReLU(inplace=True)

        # 源域分类头：dim->num_classes的分类层
        self.cls_head_src = nn.Linear(dim, num_classes)
        # 均值投影头：dim->outchannel，使用LeakyReLU激活
        self.p_mu = nn.Linear(dim, outchannel, nn.LeakyReLU())
        # 特征投影头：dim->outchannel，使用ReLU激活
        self.pro_head = nn.Linear(dim, outchannel, nn.ReLU())
        # 超图神经网络：用于图结构学习
        self.hgnn = hypergraph.HyperGraph(emb_dim=dim, K_neigs=self.num_hyper_edge)
        # 多头注意力机制
        self.gnn = hypergraph.MultiHeadAttention(model_dim=dim, num_heads=1)

    # 计算展平后的最终尺寸，用于确定全连接层输入维度
    def _get_final_flattened_size(self):
        # 在无梯度模式下计算
        with torch.no_grad():
            # 创建一个示例输入张量
            x = torch.zeros((1, self.inchannel,
                             self.patch_size, self.patch_size))
            # 获取批次大小
            in_size = x.size(0)
            # 第一层卷积+激活+池化
            out1 = self.mp(self.relu1(self.conv1(x)))
            # 第二层卷积+激活+池化
            out2 = self.mp(self.relu2(self.conv2(out1)))
            # 展平特征图
            out2 = out2.view(in_size, -1)
            # 获取展平后的维度
            w, h = out2.size()
            # 计算全连接层输入大小
            fc_1 = w * h
        # 返回计算得到的尺寸
        return fc_1

    # 前向传播函数
    def forward(self, x, mode='test'):
        # 获取输入批次大小
        in_size = x.size(0)
        # 第一层卷积+激活+池化
        out1 = self.mp(self.relu1(self.conv1(x)))
        # 第二层卷积+激活+池化
        out2 = self.mp(self.relu2(self.conv2(out1)))
        # 连续化张量并展平
        out2 = out2.contiguous().view(in_size, -1)
        # 第一个全连接层+激活
        out3 = self.relu3(self.fc1(out2))
        # node_s, edge_s = self.gnn(out3, out3, out3)
        # out_fus = self.lambda1*out3+(1-self.lambda1)*node_s
        # 第二个全连接层+激活
        # out4 = self.relu4(self.fc2(0.95*out3+0.05*node_s))
        out4 = self.relu4(self.fc2(out3))
        # out4 = self.relu4(self.fc2(out_fus))

        # matching_loss_affinity, affinity = self._forward_aff(node_s, node_t, src_label, tar_label)
        # matching_loss_quadratic = self._forward_qu(node_s, node_t, edge_s.detach(), edge_t.detach(), affinity)
        # edge_s = 0
        # proj = F.normalize(self.pro_head(out4))
        # return proj

        # 根据模式选择不同的输出
        if mode == 'test':
            # 测试模式：返回分类结果
            # clss = self.cls_head_src(out4)
            proj = self.cls_head_src(out4)
            return proj
        elif mode == 'train':
            # 训练模式：返回分类结果和归一化的特征投影
            proj = F.normalize(self.pro_head(out4))
            # proj = F.normalize(node_s)
            clss = self.cls_head_src(out4)

            return clss, proj


# 定义形态学网络类
class MorphNet(nn.Module):
    # 初始化函数
    def __init__(self, inchannel):
        # 调用父类构造函数
        super(MorphNet, self).__init__()
        # 设置通道数
        num = 1
        # 设置卷积核大小
        kernel_size = 3
        # 1x1卷积层，用于通道变换
        self.conv1 = nn.Conv2d(inchannel, num, kernel_size=1, stride=1, padding=0)
        # 最大池化层
        self.mp = nn.MaxPool2d(2)
        # 腐蚀操作层
        self.Erosion2d_1 = Erosion2d(num, num, kernel_size, soft_max=False)
        # 膨胀操作层
        self.Dilation2d_1 = Dilation2d(num, num, kernel_size, soft_max=False)
        # 第二个腐蚀操作层
        self.Erosion2d_2 = Erosion2d(num, num, kernel_size, soft_max=False)
        # 第二个膨胀操作层
        self.Dilation2d_2 = Dilation2d(num, num, kernel_size, soft_max=False)

    # 前向传播函数
    def forward(self, x):
        # 1x1卷积+ReLU激活
        x = F.relu(self.conv1(x))
        # 开运算：先腐蚀后膨胀
        xop_2 = self.Dilation2d_1(self.Erosion2d_1(x))
        # 闭运算：先膨胀后腐蚀
        xcl_2 = self.Erosion2d_2(self.Dilation2d_2(x))
        # 顶帽变换：原图像减去开运算结果
        x_top = x - xop_2
        # 黑帽变换：闭运算结果减去原图像
        x_blk = xcl_2 - x
        # 连接四种形态学变换结果
        x_morph = torch.cat((x_top, x_blk, xop_2, xcl_2), 1)

        # 返回形态学特征
        return x_morph


# 获取优化器的辅助函数
def get_optimizer(name, params, **kwargs):
    # 将优化器名称转换为小写
    name = name.lower()
    # 定义可用的优化器字典
    optimizers = {"adam": torch.optim.Adam, "sgd": torch.optim.SGD, "adamw": torch.optim.AdamW}
    # 获取对应的优化器类
    optim_cls = optimizers[name]

    # 返回优化器实例
    return optim_cls(params, **kwargs)


# 学习率调度器基类
class SchedulerBase:
    # 初始化函数
    def __init__(self, T_max, max_value, min_value=0.0, init_value=0.0, warmup_steps=0, optimizer=None):
        # 调用父类构造函数
        super(SchedulerBase, self).__init__()
        # 初始化当前步数
        self.t = 0
        # 设置最小值
        self.min_value = min_value
        # 设置最大值
        self.max_value = max_value
        # 设置初始值
        self.init_value = init_value
        # 设置预热步数
        self.warmup_steps = warmup_steps
        # 设置总步数
        self.total_steps = T_max

        # 记录当前学习率值，匹配torch.optim.lr_scheduler的API
        self._last_lr = [init_value]

        # 如果提供了优化器，将学习率应用到优化器的所有可训练参数
        # 如果优化器为None，只输出学习率值
        self.optimizer = optimizer

    # 步进函数
    def step(self):
        # 预热阶段的学习率计算
        if self.t < self.warmup_steps:
            value = self.init_value + (self.max_value - self.init_value) * self.t / self.warmup_steps
        # 预热结束时的学习率
        elif self.t == self.warmup_steps:
            value = self.max_value
        # 预热结束后按调度函数计算学习率
        else:
            value = self.step_func()
        # 增加步数计数
        self.t += 1

        # value = self.max_value

        # 如果提供了优化器，则将学习率应用到优化器
        if self.optimizer is not None:
            for param_group in self.optimizer.param_groups:
                param_group['lr'] = value

        # 更新最后的学习率记录
        self._last_lr = [value]

        # print(value)

        # 返回当前学习率值
        return value

    # 调度函数（需子类实现）
    def step_func(self):
        pass

    # 获取当前学习率
    def lr(self):
        return self._last_lr[0]


# 线性调度器类，继承自调度器基类
class LinearScheduler(SchedulerBase):
    # 实现线性调度函数
    def step_func(self):
        # 线性递减计算公式
        value = self.max_value + (self.min_value - self.max_value) * (self.t - self.warmup_steps) / (
                self.total_steps - self.warmup_steps)
        # 返回计算得到的学习率
        return value


# 禁用批量归一化运行统计的函数
def disable_running_stats(model):
    # 定义内部禁用函数
    def _disable(module):
        # 如果是批量归一化层
        if isinstance(module, _BatchNorm):
            # 保存当前动量值
            module.backup_momentum = module.momentum
            # 将动量设为0，禁用运行统计更新
            module.momentum = 0

    # 对模型的所有模块应用禁用函数
    model.apply(_disable)


# 启用批量归一化运行统计的函数
def enable_running_stats(model):
    # 定义内部启用函数
    def _enable(module):
        # 如果是批量归一化层且有备份的动量值
        if isinstance(module, _BatchNorm) and hasattr(module, "backup_momentum"):
            # 恢复原来的动量值
            module.momentum = module.backup_momentum

    # 对模型的所有模块应用启用函数
    model.apply(_enable)


# SAGM优化器类，继承自PyTorch优化器基类
class SAGM(torch.optim.Optimizer):
    # 初始化函数
    def __init__(self, params, base_optimizer, model, alpha, rho_scheduler, adaptive=False, perturb_eps=1e-12,
                 grad_reduce='mean', **kwargs):
        # 设置默认参数
        defaults = dict(adaptive=adaptive, **kwargs)
        # 调用父类构造函数
        super(SAGM, self).__init__(params, defaults)
        # 保存模型引用
        self.model = model
        # 保存基础优化器
        self.base_optimizer = base_optimizer
        # 共享参数组
        self.param_groups = self.base_optimizer.param_groups
        # 设置自适应标志
        self.adaptive = adaptive
        # 保存rho调度器
        self.rho_scheduler = rho_scheduler
        # 设置扰动epsilon值
        self.perturb_eps = perturb_eps
        # 设置alpha参数
        self.alpha = alpha

        # 初始化rho_t值
        self.update_rho_t()

        # 设置跨工作进程的梯度规约
        if grad_reduce.lower() == 'mean':
            # 如果PyTorch版本支持AVG操作
            if hasattr(ReduceOp, 'AVG'):
                self.grad_reduce = ReduceOp.AVG
                self.manual_average = False
            else:  # PyTorch <= 1.11.0不支持AVG，需要手动平均
                self.grad_reduce = ReduceOp.SUM
                self.manual_average = True
        elif grad_reduce.lower() == 'sum':
            self.grad_reduce = ReduceOp.SUM
            self.manual_average = False
        else:
            raise ValueError('"grad_reduce" should be one of ["mean", "sum"].')

    # 更新rho_t值的装饰器函数
    @torch.no_grad()
    def update_rho_t(self):
        # 从调度器获取新的rho_t值
        self.rho_t = self.rho_scheduler.step()
        # 返回rho_t值
        return self.rho_t

    # 扰动权重的装饰器函数
    @torch.no_grad()
    def perturb_weights(self, rho=0.0):
        # 计算梯度范数
        grad_norm = self._grad_norm(weight_adaptive=self.adaptive)
        # 遍历所有参数组
        for group in self.param_groups:
            # 计算缩放因子
            scale = (rho / (grad_norm + self.perturb_eps) - self.alpha)

            # 遍历组内所有参数
            for p in group["params"]:
                # 如果参数没有梯度则跳过
                if p.grad is None: continue
                # 保存原始梯度
                self.state[p]["old_g"] = p.grad.data.clone()
                # 计算扰动向量
                e_w = p.grad * scale.to(p)
                # 如果使用自适应权重
                if self.adaptive:
                    e_w *= torch.pow(p, 2)
                # 应用扰动：爬升到局部最大值"w + e(w)"
                p.add_(e_w)
                # 保存扰动向量
                self.state[p]['e_w'] = e_w

    # 取消扰动的装饰器函数
    @torch.no_grad()
    def unperturb(self):
        # 遍历所有参数组
        for group in self.param_groups:
            # 遍历组内所有参数
            for p in group['params']:
                # 如果存在扰动向量则取消扰动
                if 'e_w' in self.state[p].keys():
                    p.data.sub_(self.state[p]['e_w'])

    # 梯度分解的装饰器函数
    @torch.no_grad()
    def gradient_decompose(self, alpha=0.0):
        # 遍历所有参数组
        for group in self.param_groups:
            # 遍历组内所有参数
            for p in group['params']:
                # 如果参数没有梯度则跳过
                if p.grad is None: continue
                # 计算SAM梯度
                sam_grad = self.state[p]['old_g'] * 0.5 - p.grad * 0.5
                # 将SAM梯度添加到当前梯度
                p.grad.data.add_(sam_grad)

    # 同步梯度的装饰器函数
    @torch.no_grad()
    def _sync_grad(self):
        # 如果使用了分布式训练
        if torch.distributed.is_initialized():
            # 同步所有参数组的梯度
            for group in self.param_groups:
                for p in group['params']:
                    # 如果参数没有梯度则跳过
                    if p.grad is None: continue
                    # 根据规约方式同步梯度
                    if self.manual_average:
                        torch.distributed.all_reduce(p.grad, op=self.grad_reduce)
                        world_size = torch.distributed.get_world_size()
                        p.grad.div_(float(world_size))
                    else:
                        torch.distributed.all_reduce(p.grad, op=self.grad_reduce)
        # 返回空值
        return

    # 计算梯度范数的装饰器函数
    @torch.no_grad()
    def _grad_norm(self, by=None, weight_adaptive=False):
        # shared_device = self.param_groups[0]["params"][0].device  # put everything on the same device, in case of model parallelism
        # 如果没有指定by参数
        if not by:
            # 计算梯度范数
            norm = torch.norm(
                torch.stack([
                    ((torch.abs(p.data) if weight_adaptive else 1.0) * p.grad).norm(p=2)
                    for group in self.param_groups for p in group["params"]
                    if p.grad is not None
                ]),
                p=2
            )

        else:
            # 计算指定状态的范数
            norm = torch.norm(
                torch.stack([
                    ((torch.abs(p.data) if weight_adaptive else 1.0) * self.state[p][by]).norm(p=2)
                    for group in self.param_groups for p in group["params"]
                    if p.grad is not None
                ]),
                p=2
            )

        # 返回计算得到的范数
        return norm

    # def norm(tensor_list: List[torch.tensor], p=2):
    #     """Compute p-norm for tensor list"""
    #     return torch.cat([x.flatten() for x in tensor_list]).norm(p)

    # 加载状态字典的函数
    def load_state_dict(self, state_dict):
        # 调用父类的加载函数
        super().load_state_dict(state_dict)
        # 更新基础优化器的参数组
        self.base_optimizer.param_groups = self.param_groups

    # 可能不需要同步的上下文管理器
    def maybe_no_sync(self):
        # 如果使用了分布式训练
        if torch.distributed.is_initialized():
            # 返回模型的no_sync上下文管理器
            return self.model.no_sync()
        else:
            # 返回空的退出栈
            return contextlib.ExitStack()

    # 设置闭包函数的装饰器函数
    @torch.no_grad()
    def set_closure(self, loss_fn, inputs, targets, **kwargs):
        # 创建forward_backward_func函数，该函数自动执行前向和反向传播
        # 这个函数不接受任何参数，输入和目标数据应该在偏函数定义中预设

        # 定义获取梯度的内部函数
        def get_grad():
            # 清零基础优化器的梯度
            self.base_optimizer.zero_grad()
            # 在启用梯度的上下文中
            with torch.enable_grad():
                # 模型前向传播
                outputs = self.model(inputs)
                # 计算损失
                loss = loss_fn(outputs, targets, **kwargs)
            # 克隆并分离损失值
            loss_value = loss.data.clone().detach()
            # 反向传播
            loss.backward()
            # 返回输出和损失值
            return outputs, loss_value

        # 保存前向反向函数
        self.forward_backward_func = get_grad

    # 步进函数的装饰器函数
    @torch.no_grad()
    def step(self, closure=None):
        # 如果提供了闭包函数
        if closure:
            get_grad = closure
        else:
            # 使用预设的前向反向函数
            get_grad = self.forward_backward_func

        # 在可能不需要同步的上下文中
        with self.maybe_no_sync():
            # 获取梯度
            outputs, loss_value = get_grad()

            # 扰动权重
            self.perturb_weights(rho=self.rho_t)

            # 禁用第二遍的运行统计
            disable_running_stats(self.model)

            # 在扰动权重处获取梯度
            get_grad()

            # 分解并获取新的更新方向
            self.gradient_decompose(self.alpha)

            # 取消扰动
            self.unperturb()

        # 跨工作进程同步梯度
        self._sync_grad()

        # 使用新方向更新参数
        self.base_optimizer.step()

        # 启用运行统计
        enable_running_stats(self.model)

        # 返回输出和损失值
        return outputs, loss_value


# 算法基类，继承自PyTorch神经网络模块
class Algorithm(torch.nn.Module):
    """
    Algorithm的子类实现了领域泛化算法
    子类应该实现以下方法：
    - update()
    - predict()
    """

    # 变换字典
    transforms = {}

    # 初始化函数
    def __init__(self, num_classes, num_domains, hparams):
        # 调用父类构造函数
        super(Algorithm, self).__init__()
        # self.input_shape = input_shape
        # 保存类别数量
        self.num_classes = num_classes
        # 保存领域数量
        self.num_domains = num_domains
        # 保存超参数
        self.hparams = hparams

    # 更新函数（需子类实现）
    def update(self, x, y, **kwargs):
        """
        执行一次更新步骤，给定所有环境的(x, y)元组列表
        """
        raise NotImplementedError

    # 预测函数（需子类实现）
    def predict(self, x):
        raise NotImplementedError

    # 前向传播函数
    def forward(self, x):
        return self.predict(x)

    # 创建新优化器的函数
    def new_optimizer(self, parameters):
        # 获取优化器实例
        optimizer = get_optimizer(
            self.hparams["optimizer"],
            parameters,
            lr=self.hparams["lr"],
            weight_decay=self.hparams["weight_decay"],
        )
        # 返回优化器
        return optimizer

    # 克隆函数
    def clone(self):
        # 深拷贝当前对象
        clone = copy.deepcopy(self)
        # 为克隆对象创建新的优化器
        clone.optimizer = self.new_optimizer(clone.network.parameters())
        # 加载原优化器的状态字典
        clone.optimizer.load_state_dict(self.optimizer.state_dict())

        # 返回克隆对象
        return clone


# SAGM领域泛化算法类，继承自Algorithm基类
class SAGM_DG(Algorithm):
    """
    经验风险最小化（ERM）
    """

    # def __init__(self, input_shape, num_classes, num_domains, hparams):
    #     assert input_shape[1:3] == (224, 224), "Mixstyle support R18 and R50 only"
    #     super().__init__(input_shape, num_classes, num_domains, hparams)

    # 初始化函数
    def __init__(self, num_classes, num_domains, hparams):
        # 调用父类构造函数
        super().__init__(num_classes, num_domains, hparams)
        # self.featurizer = networks.Featurizer(input_shape, self.hparams)
        # 创建特征提取器（判别器）
        self.featurizer = Discriminator(num_classes=num_classes, inchannel=hparams['n_bands'],
                                        outchannel=hparams['pro_dim'], patch_size=hparams['patch_size'])
        # 创建分类器：投影维度->类别数量
        self.classifier = nn.Linear(hparams['pro_dim'], num_classes)
        # 创建网络序列：特征提取器+分类器
        self.network = nn.Sequential(self.featurizer, self.classifier)
        # 创建优化器
        self.optimizer = get_optimizer(
            hparams["optimizer"],
            self.network.parameters(),
            lr=self.hparams["lr"],
            weight_decay=self.hparams["weight_decay"],
        )

        # 创建学习率调度器
        self.lr_scheduler = LinearScheduler(T_max=5000, max_value=self.hparams["lr"],
                                            min_value=self.hparams["lr"], optimizer=self.optimizer)

        # 创建rho调度器
        self.rho_scheduler = LinearScheduler(T_max=5000, max_value=0.05,
                                             min_value=0.05)

        # 创建SAGM优化器
        self.SAGM_optimizer = SAGM(params=self.network.parameters(), base_optimizer=self.optimizer,
                                   model=self.network,
                                   alpha=self.hparams["alpha"], rho_scheduler=self.rho_scheduler, adaptive=False)

    # 更新函数
    def update(self, x, y, **kwargs):
        # 保存输入和标签
        all_x = x
        all_y = y

        # 定义损失函数
        def loss_fn(predictions, targets):
            return F.cross_entropy(predictions, targets)

        # 设置SAGM优化器的闭包
        self.SAGM_optimizer.set_closure(loss_fn, all_x, all_y)
        # 执行一步优化
        predictions, loss = self.SAGM_optimizer.step()
        # 更新学习率调度器
        self.lr_scheduler.step()
        # 更新rho调度器
        self.SAGM_optimizer.update_rho_t()

        # 返回损失字典
        return {"loss": loss.item()}

    # 预测函数
    def predict(self, x):
        # 返回网络的预测结果
        return self.network(x)
