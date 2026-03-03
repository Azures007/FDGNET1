# -*- coding: utf-8 -*-
# 导入随机数模块，用于生成随机数
import random
# 导入NumPy数值计算库，用于科学计算
import numpy as np
# 从sklearn.metrics导入混淆矩阵计算函数
from sklearn.metrics import confusion_matrix
# 导入sklearn的模型选择模块，用于数据分割
import sklearn.model_selection
# 导入itertools模块，用于高效的循环操作
import itertools
# 导入spectral库，用于高光谱图像处理
import spectral
# 导入matplotlib.pyplot用于绘图
import matplotlib.pyplot as plt
# 从scipy导入io模块，用于读取MATLAB文件
from scipy import io
# 导入imageio库，用于读取图像文件
import imageio
# 导入操作系统接口模块
import os
# 导入正则表达式模块
import re
# 导入PyTorch深度学习框架
import torch
# 导入NumPy数值计算库（重复导入，可能是为了强调重要性）
import numpy as np
# 从tqdm导入进度条显示功能
from tqdm import tqdm

# 定义获取设备函数，根据序号返回相应的计算设备
def get_device(ordinal):
    # 判断是否使用GPU
    if ordinal < 0:
        # 如果序号小于0，使用CPU计算
        print("Computation on CPU")
        device = torch.device('cpu')
    elif torch.cuda.is_available():
        # 如果CUDA可用，使用指定的GPU设备
        print("Computation on CUDA GPU device {}".format(ordinal))
        device = torch.device('cuda:{}'.format(ordinal))
    else:
        # 如果请求使用CUDA但不可用，回退到CPU计算
        print("/!\\ CUDA was requested but is not available! Computation will go on CPU. /!\\")
        device = torch.device('cpu')
    # 返回计算设备
    return device

# 定义随机种子设置函数，用于确保实验可重复性
def seed_worker(seed):
    # 设置PyTorch的随机种子
    torch.manual_seed(seed)
    # 如果CUDA可用，设置CUDA随机种子
    if torch.cuda.is_available():
        torch.cuda.manual_seed(seed)
        torch.cuda.manual_seed_all(seed)  # 如果使用多GPU，设置所有GPU的随机种子
    # 设置NumPy的随机种子
    np.random.seed(seed)  # Numpy模块
    # 设置Python内置random模块的随机种子
    random.seed(seed)  # Python random模块
    # 禁用cuDNN的benchmark模式以确保确定性
    torch.backends.cudnn.benchmark = False
    # 启用cuDNN的确定性模式
    torch.backends.cudnn.deterministic = True

# 定义训练专用的随机种子设置函数
def seed_worker_TRAIN(seed):
    # 设置PyTorch的随机种子
    torch.manual_seed(seed)
    # 如果CUDA可用，设置CUDA随机种子
    if torch.cuda.is_available():
        torch.cuda.manual_seed(seed)
        torch.cuda.manual_seed_all(seed)  # 如果使用多GPU，设置所有GPU的随机种子
    # 设置NumPy的随机种子
    np.random.seed(seed)  # Numpy模块
    # 设置Python内置random模块的随机种子
    random.seed(seed)  # Python random模块
    # 禁用cuDNN的benchmark模式以确保确定性
    torch.backends.cudnn.benchmark = False
    # 启用cuDNN的确定性模式
    torch.backends.cudnn.deterministic = True

# 定义文件打开函数，根据文件扩展名选择合适的读取方式
def open_file(dataset):
    # 分离文件名和扩展名
    _, ext = os.path.splitext(dataset)
    # 将扩展名转换为小写
    ext = ext.lower()
    # 如果是MATLAB文件格式
    if ext == '.mat':
        # 加载Matlab数组
        return io.loadmat(dataset)
    # 如果是TIFF图像格式
    elif ext == '.tif' or ext == '.tiff':
        # 加载TIFF文件
        return imageio.imread(dataset)
    # 如果是高光谱图像HDR格式
    elif ext == '.hdr':
        # 使用spectral库打开HDR图像
        img = spectral.open_image(dataset)
        # 加载图像数据
        return img.load()
    # 如果是未知文件格式
    else:
        # 抛出值错误异常
        raise ValueError("Unknown file format: {}".format(ext))

# 定义将标签数组转换为彩色图像的函数
def convert_to_color_(arr_2d, palette=None):
    """将标签数组转换为RGB彩色编码图像
    
    参数:
        arr_2d: 整数类型的2D标签数组
        palette: 颜色调色板字典（标签数字 -> RGB元组）
    
    返回:
        arr_3d: RGB格式的彩色编码标签2D图像
    
    """
    # 创建三维零数组用于存储RGB图像
    arr_3d = np.zeros((arr_2d.shape[0], arr_2d.shape[1], 3), dtype=np.uint8)
    # 如果没有提供调色板则抛出异常
    if palette is None:
        raise Exception("Unknown color palette")

    # 遍历调色板中的每个颜色
    for c, i in palette.items():
        # 创建当前类别的掩码
        m = arr_2d == c
        # 将对应位置设置为指定颜色
        arr_3d[m] = i

    # 返回RGB图像
    return arr_3d

# 定义将RGB图像转换回标签数组的函数
def convert_from_color_(arr_3d, palette=None):
    """将RGB编码图像转换为灰度标签
    
    参数:
        arr_3d: 3通道的RGB编码标签图像
        palette: 颜色调色板字典（RGB元组 -> 标签数字）
    
    返回:
        arr_2d: 整数类型的2D标签数组
    
    """
    # 如果没有提供调色板则抛出异常
    if palette is None:
        raise Exception("Unknown color palette")

    # 创建二维零数组用于存储标签
    arr_2d = np.zeros((arr_3d.shape[0], arr_3d.shape[1]), dtype=np.uint8)

    # 遍历调色板中的每个颜色
    for c, i in palette.items():
        # 创建当前RGB颜色的掩码
        m = np.all(arr_3d == np.array(c).reshape(1, 1, 3), axis=2)
        # 将对应位置设置为指定标签
        arr_2d[m] = i

    # 返回标签数组
    return arr_2d

# 定义显示预测结果的函数
def display_predictions(pred, vis, gt=None, caption=""):
    # 如果没有提供真实标签
    if gt is None:
        # 只显示预测结果
        vis.images([np.transpose(pred, (2, 0, 1))],
                    opts={'caption': caption})
    else:
        # 同时显示预测结果和真实标签
        vis.images([np.transpose(pred, (2, 0, 1)),
                    np.transpose(gt, (2, 0, 1))],
                    nrow=2,
                    opts={'caption': caption})

# 定义显示数据集的函数
def display_dataset(img, gt, bands, labels, palette, vis):
    """显示指定的数据集
    
    参数:
        img: 3D高光谱图像
        gt: 2D标签数组
        bands: 要选择的RGB波段元组
        labels: 类别名称列表
        palette: 颜色字典
        display (可选): 显示类型（如果有的话）
    
    """
    # 打印图像的基本信息
    print("Image has dimensions {}x{} and {} channels".format(*img.shape))
    # 获取指定波段的RGB组合
    rgb = spectral.get_rgb(img, bands)
    # 归一化RGB值
    rgb /= np.max(rgb)
    # 转换为8位整数格式
    rgb = np.asarray(255 * rgb, dtype='uint8')

    # 显示RGB合成图像
    caption = "RGB (bands {}, {}, {})".format(*bands)
    # 发送到visdom服务器
    vis.images([np.transpose(rgb, (2, 0, 1))],
                opts={'caption': caption})

# 定义探索光谱特征的函数
def explore_spectrums(img, complete_gt, class_names, vis,
                      ignored_labels=None):
    """绘制每个类别的采样光谱及其均值±标准差
    
    参数:
        img: 3D高光谱图像
        complete_gt: 2D标签数组
        class_names: 类别名称列表
        ignored_labels (可选): 要忽略的标签列表
        vis: Visdom显示对象
    返回:
        mean_spectrums: 每个类别的平均光谱字典
    
    """
    # 存储各类别的平均光谱
    mean_spectrums = {}
    # 遍历所有唯一标签
    for c in np.unique(complete_gt):
        # 如果是被忽略的标签则跳过
        if c in ignored_labels:
            continue
        # 创建当前类别的掩码
        mask = complete_gt == c
        # 提取当前类别的所有光谱
        class_spectrums = img[mask].reshape(-1, img.shape[-1])
        # 计算采样步长
        step = max(1, class_spectrums.shape[0] // 100)
        # 创建新的图形
        fig = plt.figure()
        # 设置图形标题
        plt.title(class_names[c])
        # 从选定类别中采样并绘制光谱
        for spectrum in class_spectrums[::step, :]:
            plt.plot(spectrum, alpha=0.25)
        # 计算平均光谱
        mean_spectrum = np.mean(class_spectrums, axis=0)
        # 计算标准差光谱
        std_spectrum = np.std(class_spectrums, axis=0)
        # 计算下界光谱
        lower_spectrum = np.maximum(0, mean_spectrum - std_spectrum)
        # 计算上界光谱
        higher_spectrum = mean_spectrum + std_spectrum

        # 绘制基于标准差的均值光谱
        plt.fill_between(range(len(mean_spectrum)), lower_spectrum,
                         higher_spectrum, color="#3F5D7D")
        # 绘制白色粗线表示均值光谱
        plt.plot(mean_spectrum, alpha=1, color="#FFFFFF", lw=2)
        # 在visdom中显示图形
        vis.matplot(plt)
        # 存储当前类别的平均光谱
        mean_spectrums[class_names[c]] = mean_spectrum
    # 返回平均光谱字典
    return mean_spectrums

# 定义绘制光谱图的函数
def plot_spectrums(spectrums, vis, title=""):
    """绘制指定的光谱字典
    
    参数:
        spectrums: 要绘制的光谱字典（名称 -> 光谱）
        vis: Visdom显示对象
    """
    # 初始化窗口标识符
    win = None
    # 遍历光谱字典中的每个项目
    for k, v in spectrums.items():
        # 获取波段数量
        n_bands = len(v)
        # 确定更新模式
        update = None if win is None else 'append'
        # 在visdom中绘制线条图
        win = vis.line(X=np.arange(n_bands), Y=v, name=k, win=win, update=update,
                       opts={'title': title})

# 定义构建数据集的函数
def build_dataset(mat, gt, ignored_labels=None):
    """基于图像和掩码创建训练样本列表
    
    参数:
        mat: 要从中提取光谱的3D高光谱矩阵
        gt: 2D真实标签
        ignored_labels (可选): 要忽略的类别列表，例如0表示移除未标记像素
        return_indices (可选): 布尔值，设为True时返回所选样本的索引
    
    """
    # 存储样本和标签列表
    samples = []
    labels = []
    # 检查图像和真实标签具有相同的2D维度
    assert mat.shape[:2] == gt.shape[:2]

    # 遍历所有唯一标签
    for label in np.unique(gt):
        # 如果是被忽略的标签则跳过
        if label in ignored_labels:
            continue
        else:
            # 获取当前标签的所有索引位置
            indices = np.nonzero(gt == label)
            # 添加当前位置的所有光谱样本
            samples += list(mat[indices])
            # 为这些样本添加对应的标签
            labels += len(indices[0]) * [label]
    # 返回样本和标签数组
    return np.asarray(samples), np.asarray(labels)

# 定义获取随机窗口位置的函数
def get_random_pos(img, window_shape):
    """返回输入图像中随机窗口的角落坐标
    
    参数:
        img: 2D（或更高维）图像，例如RGB或灰度图像
        window_shape: 窗口的（宽度，高度）元组
    
    返回:
        xmin, xmax, ymin, ymax: 窗口角落坐标的元组
    
    """
    # 获取窗口尺寸
    w, h = window_shape
    # 获取图像尺寸
    W, H = img.shape[:2]
    # 随机生成左上角x坐标
    x1 = random.randint(0, W - w - 1)
    # 计算右下角x坐标
    x2 = x1 + w
    # 随机生成左上角y坐标
    y1 = random.randint(0, H - h - 1)
    # 计算右下角y坐标
    y2 = y1 + h
    # 返回窗口坐标
    return x1, x2, y1, y2

# 定义滑动窗口生成器函数
def sliding_window(image, step=10, window_size=(20, 20), with_data=True):
    """在输入图像上滑动窗口的生成器
    
    参数:
        image: 要在其上滑动窗口的2D+图像，例如RGB或高光谱图像
        step: 滑动窗口的整数步幅
        window_size: 窗口的整数元组，宽度和高度
        with_data (可选): 布尔值，设为True时同时返回数据和角落索引
    产出:
        ([data], x, y, w, h)，其中x和y是窗口的左上角，(w,h)是窗口大小
    
    """
    # 在图像上滑动窗口
    w, h = window_size
    W, H = image.shape[:2]
    # 计算宽度方向的偏移量
    offset_w = (W - w) % step
    # 计算高度方向的偏移量
    offset_h = (H - h) % step
    # 在宽度方向上遍历
    for x in range(0, W - w + offset_w, step):
        # 如果超出边界则调整位置
        if x + w > W:
            x = W - w
        # 在高度方向上遍历
        for y in range(0, H - h + offset_h, step):
            # 如果超出边界则调整位置
            if y + h > H:
                y = H - h
            # 如果需要返回数据
            if with_data:
                # 产出窗口数据和坐标信息
                yield image[x:x + w, y:y + h], x, y, w, h
            else:
                # 只产出坐标信息
                yield x, y, w, h

# 定义计算滑动窗口数量的函数
def count_sliding_window(top, step=10, window_size=(20, 20)):
    """计算图像中的窗口数量
    
    参数:
        image: 要在其上滑动窗口的2D+图像，例如RGB或高光谱图像...
        step: 滑动窗口的整数步幅
        window_size: 窗口的整数元组，宽度和高度
    返回:
        整数类型的窗口数量
    """
    # 创建不带数据的滑动窗口生成器
    sw = sliding_window(top, step, window_size, with_data=False)
    # 计算生成器中的项目数量
    return sum(1 for _ in sw)

# 定义分组迭代器函数
def grouper(n, iterable):
    """通过n个元素一组的方式浏览可迭代对象
    
    参数:
        n: 整数，组的大小
        iterable: 要浏览的可迭代对象
    产出:
        来自可迭代对象的n个元素的块
    
    """
    # 创建可迭代对象的迭代器
    it = iter(iterable)
    # 无限循环
    while True:
        # 从迭代器中切片n个元素
        chunk = tuple(itertools.islice(it, n))
        # 如果没有更多元素则返回
        if not chunk:
            return
        # 产出当前块
        yield chunk

# 定义计算评估指标的函数
def metrics(prediction, target, ignored_labels=[], n_classes=None):
    """计算并打印指标（准确率、混淆矩阵和F1分数）
    
    参数:
        prediction: 预测标签列表
        target: 目标标签列表
        ignored_labels (可选): 要忽略的标签列表，例如0表示未定义
        n_classes (可选): 类别数量，默认为max(target)
    返回:
        准确率、各类别的F1分数、混淆矩阵
    """
    # 创建忽略掩码
    ignored_mask = np.zeros(target.shape[:2], dtype=np.bool)
    # 标记要忽略的标签位置
    for l in ignored_labels:
        ignored_mask[target == l] = True
    # 取反掩码得到有效区域
    ignored_mask = ~ignored_mask
    #target = target[ignored_mask] -1
    # target = target[ignored_mask]
    # prediction = prediction[ignored_mask]

    # 存储结果字典
    results = {}

    # 确定类别数量
    n_classes = np.max(target) + 1 if n_classes is None else n_classes

    # 计算混淆矩阵
    cm = confusion_matrix(
        target,
        prediction,
        labels=range(n_classes))

    # 存储混淆矩阵
    results["Confusion_matrix"] = cm
    # 计算各类别准确率
    class_accuracy = [100*(cm[x][x] / sum(cm[x])) for x in range(len(cm))]
    # 存储各类别准确率
    results['class_acc'] = class_accuracy

    # 计算假阳性、假阴性、真阳性、真阴性
    FP = cm.sum(axis=0) - np.diag(cm)  
    FN = cm.sum(axis=1) - np.diag(cm)
    TP = np.diag(cm)
    TN = cm.sum() - (FP + FN + TP)

    # 转换为浮点数类型
    FP = FP.astype(float)
    FN = FN.astype(float)
    TP = TP.astype(float)
    TN = TN.astype(float)
    # 灵敏度、命中率、召回率或真正率
    TPR = TP/(TP+FN)
    # 存储真正率
    results["TPR"] = TPR
    # 计算全局准确率
    total = np.sum(cm)
    # 计算正确分类的样本数
    accuracy = sum([cm[x][x] for x in range(len(cm))])
    # 转换为百分比
    accuracy *= 100 / float(total)

    # 存储准确率
    results["Accuracy"] = accuracy

    # 计算F1分数
    F1scores = np.zeros(len(cm))
    for i in range(len(cm)):
        try:
            # 计算F1分数
            F1 = 2 * cm[i, i] / (np.sum(cm[i, :]) + np.sum(cm[:, i]))
        except ZeroDivisionError:
            # 处理除零错误
            F1 = 0.
        # 存储F1分数
        F1scores[i] = F1

    # 存储F1分数
    results["F1_scores"] = F1scores

    # 计算kappa系数
    pa = np.trace(cm) / float(total)
    pe = np.sum(np.sum(cm, axis=0) * np.sum(cm, axis=1)) / \
        float(total * total)
    kappa = (pa - pe) / (1 - pe)
    # 存储kappa系数
    results["Kappa"] = kappa

    # 存储预测结果和标签
    results["prediction"] = prediction
    results["label"] = target

    # 返回结果字典
    return results

# 定义显示结果的函数
def show_results(results, vis, label_values=None, agregated=False):
    # 初始化文本字符串
    text = ""

    # 如果是聚合结果
    if agregated:
        # 提取各次运行的准确率
        accuracies = [r["Accuracy"] for r in results]
        # 提取各次运行的kappa系数
        kappas = [r["Kappa"] for r in results]
        # 提取各次运行的F1分数
        F1_scores = [r["F1_scores"] for r in results]

        # 计算F1分数的均值和标准差
        F1_scores_mean = np.mean(F1_scores, axis=0)
        F1_scores_std = np.std(F1_scores, axis=0)
        # 计算混淆矩阵的均值
        cm = np.mean([r["Confusion_matrix"] for r in results], axis=0)
        # 添加聚合结果标题
        text += "Agregated results :\n"
    else:
        # 提取单一结果的各项指标
        cm = results["Confusion_matrix"]
        accuracy = results["Accuracy"]
        F1scores = results["F1_scores"]
        kappa = results["Kappa"]

    #label_values = label_values[1:]
    # 在visdom中显示热力图
    vis.heatmap(cm, opts={'title': "Confusion_matrix", 
                          'marginbottom': 150,
                          'marginleft': 150,
                          'width': 500,
                          'height': 500,
                          'rownames': label_values, 'columnnames': label_values})
    # 添加混淆矩阵文本
    text += "Confusion_matrix :\n"
    text += str(cm)
    text += "---\n"

    # 根据是否聚合结果显示不同的准确率信息
    if agregated:
        text += ("Accuracy: {:.03f} +- {:.03f}\n".format(np.mean(accuracies),
                                                         np.std(accuracies)))
    else:
        text += "Accuracy : {:.03f}%\n".format(accuracy)
    text += "---\n"

    # 显示F1分数
    text += "F1_scores :\n"
    if agregated:
        # 显示聚合结果的F1分数（均值±标准差）
        for label, score, std in zip(label_values, F1_scores_mean,
                                     F1_scores_std):
            text += "\t{}: {:.03f} +- {:.03f}\n".format(label, score, std)
    else:
        # 显示单一结果的F1分数
        for label, score in zip(label_values, F1scores):
            text += "\t{}: {:.03f}\n".format(label, score)
    text += "---\n"

    # 根据是否聚合结果显示不同的kappa系数信息
    if agregated:
        text += ("Kappa: {:.03f} +- {:.03f}\n".format(np.mean(kappas),
                                                      np.std(kappas)))
    else:
        text += "Kappa: {:.03f}\n".format(kappa)

    # 在visdom中显示文本
    vis.text(text.replace('\n', '<br/>'))
    # 在控制台打印文本
    print(text)

# 定义地面实况采样函数
def sample_gt(gt, train_size, mode='random'):
    """从标签数组中提取固定比例的样本
    
    参数:
        gt: 整数类型的2D标签数组
        percentage: [0, 1]范围的浮点数
    返回:
        train_gt, test_gt: 整数类型的2D标签数组
    
    """
    # 获取非零元素的索引
    indices = np.nonzero(gt)
    # 将索引打包为(x,y)特征对
    X = list(zip(*indices)) # x,y features
    # 展平标签数组获取类别
    y = gt[indices].ravel() # classes
    # 创建训练集和测试集标签数组
    train_gt = np.zeros_like(gt)
    test_gt = np.zeros_like(gt)
    # 如果训练大小大于1，则转换为整数
    if train_size > 1:
       train_size = int(train_size)
    # 初始化训练和测试标签列表
    train_label = []
    test_label = []
    # 如果采用随机采样模式
    if mode == 'random':
        # 如果训练大小为1
        if train_size == 1:
            # 随机打乱索引
            random.shuffle(X)
            # 解包训练索引
            train_indices = [list(t) for t in zip(*X)]
            # 收集训练标签
            [train_label.append(i) for i in gt[tuple(train_indices)]]
            # 创建训练集
            train_set = np.column_stack((train_indices[0],train_indices[1],train_label))
            # 在训练标签数组中标记训练样本
            train_gt[tuple(train_indices)] = gt[tuple(train_indices)]
            # 测试集为空
            test_gt = []
            test_set = []
        else:
            # 使用sklearn进行训练测试分割
            train_indices, test_indices = sklearn.model_selection.train_test_split(X, train_size=train_size, stratify=y, random_state=23)
            # 解包训练索引
            train_indices = [list(t) for t in zip(*train_indices)]
            # 解包测试索引
            test_indices = [list(t) for t in zip(*test_indices)]
            # 在训练标签数组中标记训练样本
            train_gt[tuple(train_indices)] = gt[tuple(train_indices)]
            # 在测试标签数组中标记测试样本
            test_gt[tuple(test_indices)] = gt[tuple(test_indices)]

            # 收集训练标签
            [train_label.append(i) for i in gt[tuple(train_indices)]]
            # 创建训练集
            train_set = np.column_stack((train_indices[0],train_indices[1],train_label))
            # 收集测试标签
            [test_label.append(i) for i in gt[tuple(test_indices)]]
            # 创建测试集
            test_set = np.column_stack((test_indices[0],test_indices[1],test_label))

    # 如果采用分离采样模式
    elif mode == 'disjoint':
        # 复制地面实况作为训练集和测试集
        train_gt = np.copy(gt)
        test_gt = np.copy(gt)
        # 遍历所有唯一类别
        for c in np.unique(gt):
            # 创建当前类别的掩码
            mask = gt == c
            # 遍历图像的行
            for x in range(gt.shape[0]):
                # 计算前半部分的像素数量
                first_half_count = np.count_nonzero(mask[:x, :])
                # 计算后半部分的像素数量
                second_half_count = np.count_nonzero(mask[x:, :])
                try:
                    # 计算前后部分的比例
                    ratio = first_half_count / second_half_count
                    # 如果比例在训练大小的0.9到1.1倍之间，则停止
                    if ratio > 0.9 * train_size and ratio < 1.1 * train_size:
                        break
                except ZeroDivisionError:
                    # 处理除零错误
                    continue
            # 将前半部分标记为0
            mask[:x, :] = 0
            # 在训练集中移除后半部分
            train_gt[mask] = 0

        # 在测试集中移除训练集中的样本
        test_gt[train_gt > 0] = 0
    else:
        # 如果采样模式未实现则抛出错误
        raise ValueError("{} sampling is not implemented yet.".format(mode))
    # 返回训练集、测试集和对应的样本集合
    return train_gt, test_gt, train_set, test_set

# 定义固定大小的地面实况采样函数
def sample_gt_fixed(gt, train_size_list, mode='random'):
    """从标签数组中提取固定比例的样本
    
    参数:
        gt: 整数类型的2D标签数组
        percentage: [0, 1]范围的浮点数
    返回:
        train_gt, test_gt: 整数类型的2D标签数组
    
    """
    # 获取非零元素的索引
    indices = np.nonzero(gt)
    # 将索引打包为(x,y)特征对
    X = list(zip(*indices))  # x,y features
    # 展平标签数组获取类别
    y = gt[indices].ravel()  # classes
    # 创建训练集和测试集标签数组
    train_gt = np.zeros_like(gt)
    test_gt = np.zeros_like(gt)

    # 初始化训练和测试标签列表
    train_label = []
    test_label = []
    # 打印采样信息
    print("Sampling {} with train size = {}".format(mode, train_size_list))
    # 初始化训练和测试索引列表
    train_indices, test_indices = [], []
    train_label = []
    test_label = []
    # 遍历所有唯一类别
    for c in np.unique(gt):
        # 跳过类别0
        if c == 0:
            continue
        # 获取当前类别的索引
        indices = np.nonzero(gt == c)
        # 将索引打包为(x,y)特征对
        X = list(zip(*indices))  # x,y features

        # 使用sklearn进行训练测试分割
        train, test = sklearn.model_selection.train_test_split(
            X, train_size=train_size_list[c-1], random_state=23)
        # 添加训练索引
        train_indices += train
        # 添加测试索引
        test_indices += test
    # 解包训练索引
    train_indices = [list(t) for t in zip(*train_indices)]
    # 解包测试索引
    test_indices = [list(t) for t in zip(*test_indices)]
    # 在训练标签数组中标记训练样本
    train_gt[train_indices] = gt[train_indices]
    # 在测试标签数组中标记测试样本
    test_gt[test_indices] = gt[test_indices]

    # 收集训练标签
    [train_label.append(i) for i in gt[train_indices]]
    # 创建训练集
    train_set = np.column_stack(
        (train_indices[0], train_indices[1], train_label))
    # 收集测试标签
    [test_label.append(i) for i in gt[test_indices]]
    # 创建测试集
    test_set = np.column_stack((test_indices[0], test_indices[1], test_label))

    # 返回训练集、测试集和对应的样本集合
    return train_gt, test_gt, train_set, test_set

# 定义计算逆中位频率权重的函数
def compute_imf_weights(ground_truth, n_classes=None, ignored_classes=[]):
    """计算用于类别平衡的逆中位频率权重
    
    对于每个类别i，计算其频率f_i，即类别i的像素数与总像素数的比率
    
    然后，计算所有频率的中位数m。对于每个类别，相关联的权重为m/f_i
    
    参数:
        ground_truth: 注释数组
        n_classes: 类别数量（可选，默认为max(ground_truth)）
        ignored_classes: 要忽略的类别id（可选）
    返回:
        包含IMF系数的numpy数组
    
    """
    # 确定类别数量
    n_classes = np.max(ground_truth) if n_classes is None else n_classes
    # 初始化权重和频率数组
    weights = np.zeros(n_classes)
    frequencies = np.zeros(n_classes)

    # 遍历所有类别
    for c in range(0, n_classes):
        # 如果是被忽略的类别则跳过
        if c in ignored_classes:
            continue
        # 计算当前类别的像素数量
        frequencies[c] = np.count_nonzero(ground_truth == c)

    # 归一化像素计数以获得频率
    frequencies /= np.sum(frequencies)
    # 获取非零频率的中位数
    idx = np.nonzero(frequencies)
    median = np.median(frequencies[idx])
    # 计算权重
    weights[idx] = median / frequencies[idx]
    # 将零频率的权重设为0
    weights[frequencies == 0] = 0.
    # 返回权重数组
    return weights

# 定义驼峰命名转蛇形命名的函数
def camel_to_snake(name):
    # 将驼峰命名转换为蛇形命名
    s = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    # 处理连续大写字母的情况
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s).lower()

# 定义高光谱图像测试函数
def test_hsi(net, img, hyperparams):
    """
    在特定图像上测试模型
    """
    # 设置网络为评估模式
    net.eval()
    # 获取补丁大小参数
    patch_size =hyperparams['patch_size']
    # 设置只关注中心像素
    center_pixel = True
    # 获取批次大小和设备参数
    batch_size, device = hyperparams['batch_size'], 'cuda'
    # 获取类别数量参数
    n_classes = hyperparams['n_classes']

    # 设置滑动窗口参数
    kwargs = {'step': 1, 'window_size': (patch_size, patch_size)}
    # 初始化概率数组
    probs = np.zeros(img.shape[:2] + (n_classes,))
    # 对图像进行对称填充
    img = np.pad(img,((patch_size // 2,patch_size // 2),(patch_size // 2,patch_size // 2),(0,0)),"symmetric")

    # 计算迭代次数
    iterations = count_sliding_window(img, **kwargs) // batch_size
    # 遍历分组的滑动窗口批次
    for batch in tqdm(grouper(batch_size, sliding_window(img, **kwargs)),
                      total=(iterations),
                      desc="Inference on the image"
                      ):
        # 在无梯度模式下进行推理
        with torch.no_grad():
            # 如果补丁大小为1
            if patch_size == 1:
                # 提取单个像素数据
                data = [b[0][0, 0] for b in batch]
                # 复制数据
                data = np.copy(data)
                # 转换为PyTorch张量
                data = torch.from_numpy(data)
            else:
                # 提取补丁数据
                data = [b[0] for b in batch]
                # 复制并转置数据维度
                data = (np.copy(data)).transpose(0, 3, 1, 2)
                # data_center = data[:,:,patch_size//2,patch_size//2]


                # number_batch,_,_,_=data1.shape
                # data = np.copy((data1[:, 0:144, :, :]).reshape((number_batch, 4, 96, 96)))
                # 转换为PyTorch张量
                data = torch.from_numpy(data)
                # data_center = torch.from_numpy(data_center)

                # if hyperparams['spectral_fusion']:
                #     data1 = np.copy(data[:,0:hyperparams['rest_band'], :, :])
                #     B1,C1, H1, W1 = data1.shape
                #     win_size = int(((C1 / 3) ** 0.5) * H1)
                #     lter_num = int(win_size / H1)
                #     spec_num = 0
                #     data_spectral = np.zeros((B1,3, win_size, win_size), dtype='float32')
                #     for i in range(lter_num):
                #         for j in range(lter_num):
                #             data_spectral[:,:, (H1 * i):(H1 * (i + 1)), (W1 * j):(W1 * (j + 1))] = np.copy(data1[:,
                #                                                                                          (3 * (spec_num)):(3 * (
                #                                                                                                  spec_num + 1)),:, :])
                #             # print((H1*i),(H1*(i+1)),(W1*j),(W1*(j+1)))
                #             spec_num += 1
                #     data = np.copy(data_spectral)
                #     data = torch.from_numpy(data)
                # elif hyperparams['model']=='hamida':
                #     data = data.unsqueeze(1)
                # data_transforms=transforms.Resize([224,224])
                # data=data_transforms(data)


            # 提取窗口坐标索引
            indices = [b[1:] for b in batch]
            # 将数据移动到指定设备
            data = data.to(device)
            # data_center = data_center.to(device)

            # 网络前向传播
            output = net(data)

            # 如果输出是元组则取第一个元素
            if isinstance(output, tuple):
                output = output[0]
            # 将输出移动到CPU
            output = output.to('cpu')

            # 根据补丁大小和中心像素设置处理输出
            if patch_size == 1 or center_pixel:
                output = output.numpy()
            else:
                output = np.transpose(output.numpy(), (0, 2, 3, 1))
            # 遍历索引和输出结果
            for (x, y, w, h), out in zip(indices, output):
                # 如果只关注中心像素
                if center_pixel:
                    # 累加中心像素的概率
                    probs[x, y] += out
                else:
                    # 累加整个窗口的概率
                    probs[x:x + w, y:y + h] += out
    # 返回概率数组
    return probs

# 定义高光谱图像评估指标计算函数
def hsi_metrics(prediction, target, ignored_labels=[], n_classes=None):
    """计算并打印指标（准确率、混淆矩阵和F1分数）
    
    参数:
        prediction: 预测标签列表
        target: 目标标签列表
        ignored_labels (可选): 要忽略的标签列表，例如0表示未定义
        n_classes (可选): 类别数量，默认为max(target)
    返回:
        准确率、各类别的F1分数、混淆矩阵
    """
    # 创建忽略掩码
    ignored_mask = np.zeros(target.shape[:2], dtype=np.bool)
    # 标记要忽略的标签位置
    for l in ignored_labels:
        ignored_mask[target == l] = True
    # 取反掩码得到有效区域
    ignored_mask = ~ignored_mask
    #target = target[ignored_mask] -1
    # 只保留有效区域的目标标签
    target = target[ignored_mask]
    # 只保留有效区域的预测标签
    prediction = prediction[ignored_mask]

    # 存储结果字典
    results = {}

    # 确定类别数量
    n_classes = np.max(target) + 1 if n_classes is None else n_classes

    # 计算混淆矩阵
    cm = confusion_matrix(
        target,
        prediction,
        labels=range(n_classes))

    # 存储混淆矩阵
    results["Confusion matrix"] = cm

    # 计算全局准确率
    total = np.sum(cm)

    # 计算正确分类的样本数
    accuracy = sum([cm[x][x] for x in range(len(cm))])

    # 计算各类别准确率
    class_accuracy = [cm[x][x] / sum(cm[x]) for x in range(len(cm))]
    # 存储各类别准确率
    results['class_acc'] = class_accuracy
    # 转换为百分比
    accuracy *= 100 / float(total)
    # 计算平均准确率（排除第一类）
    results['average_acc'] = np.mean(class_accuracy[1:])
    # print(results['average_acc'])
    # quit()

    # 存储准确率
    results["Accuracy"] = accuracy

    # 计算F1分数
    F1scores = np.zeros(len(cm))
    for i in range(len(cm)):
        try:
            # 计算F1分数
            F1 = 2. * cm[i, i] / (np.sum(cm[i, :]) + np.sum(cm[:, i]))
        except ZeroDivisionError:
            # 处理除零错误
            F1 = 0.
        # 存储F1分数
        F1scores[i] = F1

    # 存储F1分数
    results["F1 scores"] = F1scores

    # 计算kappa系数
    pa = np.trace(cm) / float(total)
    pe = np.sum(np.sum(cm, axis=0) * np.sum(cm, axis=1)) / \
        float(total * total)
    kappa = (pa - pe) / (1 - pe)
    # 存储kappa系数
    results["Kappa"] = kappa

    # 返回结果字典
    return results
