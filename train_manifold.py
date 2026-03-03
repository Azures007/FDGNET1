# 导入PyTorch深度学习框架相关库
import torch
# 导入PyTorch神经网络模块
import torch.nn as nn
# 导入PyTorch优化器
from torch import optim
# 导入PyTorch数据处理工具
import torch.utils.data as data
# 从HSI工具模块导入样本采样、评估指标、随机种子设置、测试函数等
from utils_HSI import sample_gt, metrics, seed_worker,test_hsi,hsi_metrics
# 从数据集模块导入数据集获取和HyperX数据类
from datasets import get_dataset, HyperX
# 导入随机数模块
import random
# 导入操作系统接口模块
import os
# 导入时间模块
import time
# 导入NumPy数值计算库
import numpy as np
# 导入Pandas数据处理库
import pandas as pd
# 导入命令行参数解析模块
import argparse
# 从约束损失模块导入监督对比损失和流形距离函数
from con_losses import SupConLoss, manifold_dis
# 从网络模块导入判别器
from network import discrim_hyperG
# 从网络模块导入生成器
from network import generator
# 导入日期时间模块
from datetime import datetime

# 创建命令行参数解析器，用于定义和解析训练脚本的所有参数
parser = argparse.ArgumentParser(description='PyTorch SDEnet')
# 设置模型和结果保存路径，默认为./results/
parser.add_argument('--save_path', type=str, default='./results/')
# 设置数据集路径，默认为./Datasets/Pavia/（帕维亚数据集）
parser.add_argument('--data_path', type=str, default='./Datasets/Pavia/')

# 设置源域数据集名称，默认为paviaU（帕维亚大学数据集）
parser.add_argument('--source_name', type=str, default='paviaU',
                    help='the name of the source dir')
# 设置目标域数据集名称，默认为paviaC（帕维亚城堡数据集），这是跨域适应的关键参数
parser.add_argument('--target_name', type=str, default='paviaC',
                    help='the name of the test dir')
# 设置GPU设备编号，默认为0，若设为-1则使用CPU
parser.add_argument('--gpu', type=int, default=0,
                    help="Specify CUDA device (defaults to -1, which learns on CPU)")

# 创建训练参数分组，便于管理相关参数
group_train = parser.add_argument_group('Training')
# 设置空间邻域补丁大小，默认为13，影响卷积网络的感受野
group_train.add_argument('--patch_size', type=int, default=13,
                         help="Size of the spatial neighbourhood (optional, if ""absent will be set by the model)")
# 设置学习率，默认为0.001，控制模型参数更新步长
group_train.add_argument('--lr', type=float, default=1e-3,
                         help="Learning rate, set by the model if not specified.")
# 设置SGD优化器动量参数，默认为0.9
parser.add_argument('--momentum', type=float, default=0.9,
                    help='SGD momentum (default: 0.5)')
# 设置训练批次大小，默认为256
group_train.add_argument('--batch_size', type=int, default=256,
                         help="Batch size (optional, if absent will be set by the model")
# 设置投影维度，默认为128，用于特征表示
group_train.add_argument('--pro_dim', type=int, default=128)
# 设置推理时滑动窗口步长，默认为1
group_train.add_argument('--test_stride', type=int, default=1,
                         help="Sliding window step stride during inference (default = 1)")
# 设置随机种子，默认为1233，确保实验可复现
parser.add_argument('--seed', type=int, default=1233,
                    help='random seed ')
# 设置L2权重衰减系数，默认为0.0001，用于正则化防止过拟合
parser.add_argument('--l2_decay', type=float, default=1e-4,
                    help='the L2  weight decay')
# 设置训练轮数，默认为400
parser.add_argument('--num_epoch', type=int, default=400,
                    help='the number of epoch')
# 设置训练样本比例，默认为0.8，决定训练/验证集划分
parser.add_argument('--training_sample_ratio', type=float, default=0.8,
                    help='training sample ratio')
# 设置数据增强倍数，默认为0
parser.add_argument('--re_ratio', type=int, default=0,
                    help='multiple of of data augmentation')
# 设置最大训练轮数，默认为400
parser.add_argument('--max_epoch', type=int, default=400)
# 设置日志记录间隔，默认每40轮记录一次
parser.add_argument('--log_interval', type=int, default=40)
# 设置特征维度，默认为64
parser.add_argument('--d_se', type=int, default=64)
# 设置第一个损失权重，默认为1.0
parser.add_argument('--lambda_1', type=float, default=1.0)
# 设置第二个损失权重，默认为1.0
parser.add_argument('--lambda_2', type=float, default=1.0)
# 设置第三个损失权重，默认为0.1
parser.add_argument('--lambda_3', type=float, default=0.1)
# 设置学习率调度器，默认为'none'
parser.add_argument('--lr_scheduler', type=str, default='none')
# 设置是否使用低频干扰，默认为False
parser.add_argument('--low_freq', type=bool, default=False,
                    help="disturbed by low frequacy")

# 创建数据增强参数分组，便于管理增强策略
group_da = parser.add_argument_group('Data augmentation')
# 设置翻转增强开关，默认开启
group_da.add_argument('--flip_augmentation', action='store_true', default=True,
                      help="Random flips (if patch_size > 1)")
# 设置辐射增强开关，默认开启
group_da.add_argument('--radiation_augmentation', action='store_true', default=True,
                      help="Random radiation noise (illumination)")
# 设置混合增强开关，默认关闭
group_da.add_argument('--mixture_augmentation', action='store_true', default=False,
                      help="Random mixes between spectra")
# 解析命令行参数并存储到args对象中
args = parser.parse_args()


# 定义评估函数，用于验证模型性能
# 参数: net-网络模型, val_loader-验证数据加载器, hyperparameter-超参数, gpu-GPU设备, tgt-是否为目标域评估
def evaluate(net, val_loader, hyperparameter,gpu, tgt=False):
    # 存储预测结果列表
    ps = []
    # 存储真实标签列表
    ys = []
    # 遍历验证数据加载器
    for i, (x1, y1) in enumerate(val_loader):
        # 将标签减1以匹配索引
        y1 = y1 - 1
        # 使用torch.no_grad()禁用梯度计算，节省内存
        with torch.no_grad():
            # 将输入数据移动到指定GPU
            x1 = x1.to(gpu)
            # 前向传播获得预测结果
            p1 = net(x1)
            # 获取预测类别（取最大值的索引）
            p1 = p1.argmax(dim=1)
            # 将预测结果移至CPU并转换为NumPy数组，添加到预测列表
            ps.append(p1.detach().cpu().numpy())
            # 将真实标签转换为NumPy数组，添加到标签列表
            ys.append(y1.numpy())
    # 将所有预测结果连接成一个数组
    ps = np.concatenate(ps)
    # 将所有真实标签连接成一个数组
    ys = np.concatenate(ys)
    # 计算准确率
    acc = np.mean(ys == ps) * 100
    # 如果是目标域评估
    if tgt:
        # 计算评估指标
        results = metrics(ps, ys, n_classes=ys.max() + 1)
        # 打印混淆矩阵、平均精度、总体精度和Kappa系数
        print(results['Confusion_matrix'], '\n', 'AA:', results['class_acc'], '\n', 'OA:', results['Accuracy'], '\n',
              'Kappa:', results['Kappa'])
        # 在目标域上测试HSI模型
        probility = test_hsi(net, val_loader.dataset.data, hyperparameter)
        # 保存预测结果
        np.save(args.source_name+'tsne_pred.npy',probility)
        # 获取最终预测结果
        prediction = np.argmax(probility, axis=-1)

        # 计算HSI指标
        run_results = hsi_metrics(prediction, val_loader.dataset.label - 1, [-1],
                                  n_classes=hyperparameter['n_classes'])
        print(run_results)

        # with open('out_SDGnet_DG.log', 'a') as f:
        #     f.write("\n")
        #     f.write('OA:'+str(results['Accuracy'])+ "\n")
        #     f.write('AA:' + str(results['class_acc']) + "\n")
        #     f.write('Kappa:' + str(results['Kappa']) + "\n")
        #     f.write("\n")
        #
        # f.close()
        # 返回准确率、评估结果和预测结果
        return acc, results, prediction
    else:
        # 返回准确率
        return acc



# 定义目标域评估函数
# 参数: cls_net-分类器网络, gpu-GPU设备, loader-数据加载器, hyperparameter-超参数, modelpath-模型路径
def evaluate_tgt(cls_net, gpu, loader, hyperparameter,modelpath):
    # 加载保存的模型权重
    saved_weight = torch.load(modelpath)
    # 加载判别器的权重参数
    cls_net.load_state_dict(saved_weight['Discriminator'])
    # 设置网络为评估模式
    cls_net.eval()
    # 调用evaluate函数进行评估
    teacc, best_results,pred = evaluate(cls_net, loader, hyperparameter,gpu, tgt=True)
    # 返回准确率、最佳结果和预测结果
    return teacc, best_results,pred


# 定义实验主函数，包含完整的训练和评估流程
def experiment():
    # 复制当前局部变量设置，用于调试
    settings = locals().copy()
    # 打印设置信息
    print(settings)
    # 将命令行参数转换为字典格式
    hyperparams = vars(args)
    # 打印超参数信息
    print(hyperparams)
    # 获取当前时间
    now_time = datetime.now()
    # 格式化时间为字符串，用于创建唯一的日志目录
    time_str = datetime.strftime(now_time, '%m-%d_%H-%M-%S')
    # 构建根目录路径，格式为：保存路径/源域名to目标域名
    root = os.path.join(args.save_path, args.source_name + 'to' + args.target_name)
    # 构建日志目录路径，包含学习率、维度、补丁大小、批次大小和时间戳
    log_dir = os.path.join(root, str(args.lr) + '_dim' + str(args.pro_dim) +
                           '_pt' + str(args.patch_size) + '_bs' + str(args.batch_size) + '_' + time_str)
    # 如果根目录不存在则创建
    if not os.path.exists(root):
        os.makedirs(root)
    # 如果日志目录不存在则创建
    if not os.path.exists(log_dir):
        os.makedirs(log_dir)
    # writer = SummaryWriter(log_dir)
    # 将参数保存为DataFrame并写入CSV文件
    df = pd.DataFrame([args])
    df.to_csv(os.path.join(log_dir, 'params.txt'))

    # 设置随机种子，确保实验可重复性
    seed_worker(args.seed)
    # 获取源域数据集：图像、标签、标签值、忽略标签、RGB波段、调色板
    img_src, gt_src, LABEL_VALUES_src, IGNORED_LABELS, RGB_BANDS, palette = get_dataset(args.source_name,
                                                                                        args.data_path)
    # 获取目标域数据集：图像、标签、标签值、忽略标签、RGB波段、调色板
    img_tar, gt_tar, LABEL_VALUES_tar, IGNORED_LABELS, RGB_BANDS, palette = get_dataset(args.target_name,
                                                                                        args.data_path)
    # 统计源域样本数量
    sample_num_src = len(np.nonzero(gt_src)[0])
    # 统计目标域样本数量
    sample_num_tar = len(np.nonzero(gt_tar)[0])
    # 获取类别数量
    n_classes = np.max(gt_src)
    print(n_classes)
    # 遍历每个类别并统计数量
    for i in range(n_classes):
        # 复制源域标签
        count_class = np.copy(gt_src)
        # 复制目标域标签
        test_count = np.copy(gt_tar)
        # sparse_class=np.copy(sparse_ground_truth)

        # 只保留当前类别的标签
        count_class[(gt_src != i + 1)] = 0
        # sparse_class[(sparse_ground_truth != i + 1)[:H_SD, :W_SD]] = 0
        # 统计当前类别数量
        class_num = np.count_nonzero(count_class)

        # 只保留目标域当前类别的标签
        test_count[gt_tar != i + 1] = 0

        # 打印类别统计信息
        print([i + 1], ':', class_num, np.count_nonzero(test_count))

    # 打印总样本数
    print("Total",np.count_nonzero(gt_src),np.count_nonzero(gt_tar))
    # 计算数据比例，用于后续的数据增强
    tmp = args.training_sample_ratio * args.re_ratio * sample_num_src / sample_num_tar
    # 获取类别数量
    num_classes = gt_src.max()
    # 获取图像波段数
    N_BANDS = img_src.shape[-1]
    # 更新超参数字典，添加类别数、波段数、忽略标签、设备、中心像素和监督类型
    hyperparams.update({'n_classes': num_classes, 'n_bands': N_BANDS, 'ignored_labels': IGNORED_LABELS,
                        'device': args.gpu, 'center_pixel': None, 'supervision': 'full'})

    # 计算填充大小，用于卷积操作
    r = int(hyperparams['patch_size'] / 2) + 1
    # 对源域图像进行对称填充
    img_src = np.pad(img_src, ((r, r), (r, r), (0, 0)), 'symmetric')
    # 对目标域图像进行对称填充
    img_tar = np.pad(img_tar, ((r, r), (r, r), (0, 0)), 'symmetric')
    # 对源域标签进行常数填充
    gt_src = np.pad(gt_src, ((r, r), (r, r)), 'constant', constant_values=(0, 0))
    # 对目标域标签进行常数填充
    gt_tar = np.pad(gt_tar, ((r, r), (r, r)), 'constant', constant_values=(0, 0))


    # 如果源域名称包含'whu'，使用特殊的采样策略
    if 'whu' in args.source_name:
        # 从源域标签中随机采样训练和验证标签
        train_gt_src,val_gt_src, _, _ = sample_gt(gt_src, args.training_sample_ratio, mode='random')
        # 从训练集中再随机采样一部分作为验证集
        val_gt_src, _, _, _ = sample_gt(train_gt_src, 0.1, mode='random')
    else:
        # 从源域标签中随机采样训练和验证标签
        train_gt_src,val_gt_src, _, _ = sample_gt(gt_src, args.training_sample_ratio, mode='random')

    # val_gt_src, _,_, _ = sample_gt(train_gt_src, 0.1, mode='random')
    # 打印训练和验证样本数量
    print("All training number is,", np.count_nonzero(train_gt_src),np.count_nonzero(val_gt_src))
    # 从目标域标签中随机采样测试标签
    test_gt_tar, _, _, _ = sample_gt(gt_tar, 1, mode='random')
    # 复制源域图像和训练标签，用于数据增强
    img_src_con, train_gt_src_con = img_src, train_gt_src
    # 复制验证标签
    val_gt_src_con = val_gt_src
    # 如果数据比例小于1，则进行数据增强
    if tmp < 1:
        # 根据重采样比例进行多次数据增强
        for i in range(args.re_ratio - 1):
            # 连接源域图像
            img_src_con = np.concatenate((img_src_con, img_src))
            # 连接训练标签
            train_gt_src_con = np.concatenate((train_gt_src_con, train_gt_src))
            # 连接验证标签
            val_gt_src_con = np.concatenate((val_gt_src_con, val_gt_src))

    # 复制训练超参数
    hyperparams_train = hyperparams.copy()
    # 创建PyTorch随机数生成器
    g = torch.Generator()
    # 设置随机数生成器的种子
    g.manual_seed(args.seed)
    # 创建训练数据集实例
    train_dataset = HyperX(img_src_con, train_gt_src_con, **hyperparams_train)
    # 创建训练数据加载器
    train_loader = data.DataLoader(train_dataset,
                                   batch_size=hyperparams['batch_size'],
                                   pin_memory=True,  # 固定内存，加速数据传输
                                   worker_init_fn=seed_worker,  # 工作进程初始化函数
                                   generator=g,  # 随机数生成器
                                   shuffle=True, )  # 是否打乱数据
    # 创建验证数据集实例
    val_dataset = HyperX(img_src_con, val_gt_src_con, **hyperparams)
    # 创建验证数据加载器
    val_loader = data.DataLoader(val_dataset,
                                 pin_memory=True,  # 固定内存，加速数据传输
                                 batch_size=hyperparams['batch_size'])  # 批次大小
    # 创建测试数据集实例
    test_dataset = HyperX(img_tar, test_gt_tar, **hyperparams)
    # 创建测试数据加载器
    test_loader = data.DataLoader(test_dataset,
                                  pin_memory=True,  # 固定内存，加速数据传输
                                  worker_init_fn=seed_worker,  # 工作进程初始化函数
                                  generator=g,  # 随机数生成器
                                  batch_size=hyperparams['batch_size'])  # 批次大小
    # 设置图像尺寸
    imsize = [hyperparams['patch_size'], hyperparams['patch_size']]

    # D_net = discriminator.Discriminator(inchannel=N_BANDS, outchannel=args.pro_dim, num_classes=num_classes,
    #                                     patch_size=hyperparams['patch_size']).to(args.gpu)
    # 如果源域名称包含'whu'，设置填充标志
    if 'whu' in args.source_name:
        pad = True
    else:
        pad = False
    # 创建判别器网络实例，输入通道数为波段数，输出通道数为投影维度，类别数为目标类别数，补丁大小来自超参数
    D_net = discrim_hyperG.Discriminator(inchannel=N_BANDS, outchannel=args.pro_dim, num_classes=num_classes,
                                         patch_size=hyperparams['patch_size'],pad=pad).to(args.gpu)
    # D_net = vit_base_patch16(num_classes=num_classes, drop_path_rate=0.1, global_pool=True, img_size = hyperparams['patch_size'],
    #                          patch_size = 2,in_chans = N_BANDS,hidden_emb=args.pro_dim).to(args.gpu)
    # D_net = discriminator.D_Res_3d_CNN(in_channel=1, out_channel1=8, out_channel2=16, CLASS_NUM=num_classes,
    #                                    patch_size=hyperparams['patch_size'], n_bands=N_BANDS,embed_dim=args.pro_dim).to(args.gpu)
    # 创建判别器优化器，使用Adam优化算法，学习率来自参数
    D_opt = optim.Adam(D_net.parameters(), lr=args.lr)
    # 创建生成器网络实例，参数包括特征数、图像维度、图像尺寸、潜在维度和设备
    G_net = generator.Generator(n=args.d_se, imdim=N_BANDS, imsize=imsize, zdim=10, device=args.gpu,
                                low_freq=args.low_freq).to(args.gpu)
    # 创建生成器优化器，使用Adam优化算法，学习率来自参数
    G_opt = optim.Adam(G_net.parameters(), lr=args.lr)
    # 设置分类损失函数为交叉熵损失
    cls_criterion = nn.CrossEntropyLoss()
    # 设置对比损失函数，设备来自参数
    con_criterion = SupConLoss(device=args.gpu)

    # 初始化最佳准确率
    best_acc = 0
    # 初始化目标域最佳准确率
    best_acc_tgt = 0
    # 初始化目标域准确率和准确率列表
    taracc, taracc_list = 0, []
    # 开始训练循环，从1到最大轮数+1
    for epoch in range(1, args.max_epoch + 1):

        # 记录开始时间
        t1 = time.time()
        # 初始化损失列表
        loss_list = []
        # 设置判别器为训练模式
        D_net.train()
        # 遍历训练数据加载器
        for i, (x, y) in enumerate(train_loader):
            # 将输入数据和标签移动到指定GPU
            x, y = x.to(args.gpu), y.to(args.gpu)
            # 将标签减1以匹配索引
            y = y - 1
            # 在无梯度模式下使用生成器生成增强数据
            with torch.no_grad():
                x_ED = G_net(x)
            # 生成均匀分布的随机数
            rand = torch.nn.init.uniform_(torch.empty(len(x), 1, 1, 1)).to(args.gpu)  # Uniform distribution

            # 使用生成器生成目标域数据
            x_tgt = G_net(x)
            # 再次使用生成器生成目标域数据
            x2_tgt = G_net(x)
            # 判别器处理原始数据，返回预测和特征
            p_SD, z_SD = D_net(x, mode='train')
            # 判别器处理增强数据，返回预测和特征
            p_ED, z_ED = D_net(x_ED, mode='train')

            # 初始化插值数据张量
            x_ID = torch.zeros_like(x)

            # 遍历每个唯一标签
            for i, id in enumerate(y.unique()):
                # 创建掩码，筛选当前类别的样本
                mask = y == y.unique()[i]
                # 提取当前类别在原始和增强数据中的特征
                z_SD_i, z_ED_i = z_SD[mask], z_ED[mask]
                # 连接原始和增强特征
                z_all_i = torch.cat([z_SD_i, z_ED_i], dim=0)
                # 创建范围索引
                range_i = range(0, z_all_i.size(0))

                # 随机采样索引
                idx_rand = random.sample(range_i, z_SD_i.size(0))

                # 计算原始特征和混合特征的流形距离
                p_dist_sd, c_dist_sd = manifold_dis(z_SD_i, z_all_i[idx_rand, :])
                p_dist_ed, c_dist_ed = manifold_dis(z_ED_i, z_all_i[idx_rand, :])

                # 计算权重
                lambda_pr1 = args.lambda_3 * p_dist_sd + c_dist_sd
                lambda_pd1 = args.lambda_3 * p_dist_ed + c_dist_ed
                # 生成插值数据
                x_ID[mask, :, :, :] = ((lambda_pd1 / (lambda_pr1 + lambda_pd1))) * x[mask, :, :, :] + (
                            lambda_pr1 / (lambda_pr1 + lambda_pd1)) * x_ED[mask, :, :, :]

            # 判别器处理插值数据，返回预测和特征
            p_ID, z_ID = D_net(x_ID, mode='train')
            # 连接所有特征
            zsrc = torch.cat([z_SD.unsqueeze(1), z_ED.unsqueeze(1), z_ID.unsqueeze(1)], dim=1)
            # zsrc = torch.cat([z_SD.unsqueeze(1), z_ED.unsqueeze(1)], dim=1)
            # 计算源域分类损失
            src_cls_loss = cls_criterion(p_SD, y.long()) + cls_criterion(p_ED, y.long()) + cls_criterion(p_ID, y.long())
            # 判别器处理目标域数据，返回预测和特征
            p_tgt, z_tgt = D_net(x_tgt, mode='train')
            # 计算目标域分类损失
            tgt_cls_loss = cls_criterion(p_tgt, y.long())

            # r1 = torch.randperm(hyperparams['batch_size'])
            # r2 = torch.randperm(hyperparams['batch_size'])
            # ml_real_out_shuffle = z_SD[r1[:, None]].view(z_SD.shape[0], z_SD.shape[-1])
            # ml_fake_out_shuffle = z_ED[r2[:, None]].view(z_ED.shape[0], z_ED.shape[-1])

            ############################
            # 计算原始和增强数据的流形距离
            p_dist, c_dist = manifold_dis(z_SD, z_ED)

            #
            # pd_r = pairwise_distances(z_SD, z_SD)
            # pd_f = pairwise_distances(z_ED, z_ED)

            # pd_r = pairwise_distances(F.normalize(p_SD), F.normalize(p_SD))
            # pd_f = pairwise_distances(F.normalize(p_ED), F.normalize(p_ED))

            # p_dist = torch.dist(pd_r, pd_f, 2)
            # c_dist = torch.dist(z_SD.mean(0), z_ED.mean(0), 2)
            # #
            # 计算生成器损失
            g_loss = args.lambda_3 * p_dist + c_dist
            # g_loss = p_dist + c_dist
            # g_loss = 0.01*p_dist+(p_dist/g_dist)*c_dist

            # 连接所有特征用于对比学习
            zall = torch.cat([z_tgt.unsqueeze(1), zsrc], dim=1)
            # 计算对比损失
            con_loss = con_criterion(zall, y, adv=False)
            # 计算总损失
            loss = src_cls_loss + args.lambda_1 * con_loss + tgt_cls_loss
            # 判别器优化器清零梯度
            D_opt.zero_grad()
            # 反向传播，保留图结构
            loss.backward(retain_graph=True)

            # 获取唯一标签数量
            num_adv = y.unique().size()
            # zsrc_con = torch.cat([z_tgt.unsqueeze(1), z_ED.unsqueeze(1), z_ID.unsqueeze(1)], dim=1)
            # zsrc_con = torch.cat([z_tgt.unsqueeze(1), z_ED.unsqueeze(1)], dim=1)
            # 连接目标域和增强特征（带detach）
            zsrc_con = torch.cat([z_tgt.unsqueeze(1), z_ED.unsqueeze(1).detach(), z_ID.unsqueeze(1).detach()], dim=1)
            # 初始化对抗损失
            con_loss_adv = 0
            # 随机选择索引
            idx_1 = np.random.randint(0, zsrc.size(1))
            # z_SD_i, zsrc_i = z_SD[mask], zsrc_con[mask]
            # y_i = torch.cat([torch.zeros(z_SD_i.shape[0]), torch.ones(z_SD_i.shape[0])])
            # 连接特征用于对抗训练
            zall = torch.cat([z_tgt.unsqueeze(1), zsrc[:, idx_1:idx_1 + 1].detach()], dim=1)
            # 计算对抗损失
            con_loss_adv += con_criterion(zall, adv=True)

            # for i,id in enumerate(y.unique()):
            #     mask = y==y.unique()[i]
            #     z_SD_i, zsrc_i = z_SD[mask], zsrc_con[mask]
            #     y_i = torch.cat([torch.zeros(z_SD_i.shape[0]),torch.ones(z_SD_i.shape[0])])
            #     zall = torch.cat([z_SD_i.unsqueeze(1).detach(), zsrc_i[:,idx_1:idx_1+1]], dim=0)
            #     if y_i.size()[0] > 2:
            #         con_loss_adv += con_criterion(zall, y_i)
            # con_loss_adv = con_loss_adv/y.unique().shape[0]

            # 计算生成器损失
            loss = tgt_cls_loss + args.lambda_1 * con_loss_adv + args.lambda_2 *g_loss
            # 生成器优化器清零梯度
            G_opt.zero_grad()
            # 反向传播
            loss.backward()
            # 判别器优化器更新参数
            D_opt.step()
            # 生成器优化器更新参数
            G_opt.step()
            # if args.lr_scheduler in ['cosine']:
            #     scheduler.step()

            # 记录各项损失
            loss_list.append([src_cls_loss.item(), tgt_cls_loss.item(), con_loss.item(), con_loss_adv.item()])
        # 计算平均损失
        src_cls_loss, tgt_cls_loss, con_loss, con_loss_adv = np.mean(loss_list, 0)

        # 设置判别器为评估模式
        D_net.eval()
        # 评估验证集准确率
        teacc= evaluate(D_net, val_loader, hyperparams,args.gpu)
        # 如果当前准确率更好，保存模型
        if best_acc < teacc:
            best_acc = teacc
            # 保存判别器权重
            torch.save({'Discriminator': D_net.state_dict()}, os.path.join(log_dir, f'best.pkl'))
        # 记录结束时间
        t2 = time.time()

        # 打印训练信息
        print(
            f'epoch {epoch}, train {len(train_loader.dataset)}, time {t2 - t1:.2f}, src_cls {src_cls_loss:.4f} tgt_cls {tgt_cls_loss:.4f} G_Loss {g_loss:.4f} con_adv {con_loss_adv:.4f} '
            f'/// p_dis {p_dist:.4f},p_dis {c_dist:.4f}, teacc {teacc:2.2f}')
        # writer.add_scalar('src_cls_loss', src_cls_loss, epoch)
        # writer.add_scalar('tgt_cls_loss', tgt_cls_loss, epoch)
        # writer.add_scalar('con_loss', con_loss, epoch)
        # writer.add_scalar('con_loss_adv', con_loss_adv, epoch)
        # writer.add_scalar('teacc', teacc, epoch)

        # 每隔一定轮数进行目标域评估
        if epoch % args.log_interval == 0:
            # 设置模型路径
            pklpath = f'{log_dir}/best.pkl'
            # 评估目标域性能
            taracc, result_tgt,pred = evaluate_tgt(D_net, args.gpu, test_loader, hyperparams,pklpath)
            # 如果目标域准确率更高，更新最佳结果
            if best_acc_tgt < taracc:
                best_acc_tgt = taracc
                best_results = result_tgt
                # 保存预测结果
                np.save(args.source_name+'_pred_OURS.npy', pred)
            # 将准确率添加到列表
            taracc_list.append(round(taracc, 2))
            # 打印目标域评估结果
            print(f'load pth, target sample number {len(test_loader.dataset)}, max taracc {max(taracc_list):2.2f}')

    # 打开日志文件，追加模式
    with open('out_'+args.source_name+'_ablation.log', 'a') as f:
        # 写入换行
        f.write("\n")
        # 写入低频设置信息
        f.write('-----------------low_freq:' + str(args.low_freq) + "\n")
        # 写入最大准确率
        f.write('max:' + str(max(taracc_list)) + "\n")
        # 写入lambda1参数
        f.write('LAMBDA1:' + str(args.lambda_1) + "\n")
        # 写入lambda2参数
        f.write('LAMBDA2:' + str(args.lambda_2) + "\n")
        # 写入lambda3参数
        f.write('LAMBDA3:' + str(args.lambda_3) + "\n")
        # 写入低频设置信息
        f.write('-----------------low_freq:' + str(args.low_freq) + "\n")
        # 写入换行
        f.write("\n")
        # 写入总体准确率
        f.write('OA:' + str(best_results['Accuracy']) + "\n")
        # 写入平均准确率
        f.write('AA:' + str(best_results['class_acc']) + "\n")
        # 写入Kappa系数
        f.write('Kappa:' + str(best_results['Kappa']) + "\n")
        # 写入换行
        f.write("\n")

    # 关闭文件
    f.close()


# 程序入口点，当此文件被直接运行时执行experiment函数
if __name__ == '__main__':
    # 调用实验函数开始训练过程
    experiment()

