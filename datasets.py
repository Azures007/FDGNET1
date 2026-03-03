# -*- coding: utf-8 -*-
"""
This file contains the PyTorch dataset for hyperspectral images and
related helpers.
"""
# 导入h5py库，用于读取HDF5格式的高光谱数据文件
import h5py
# 导入spectral库，用于高光谱图像处理
import spectral
# 导入numpy库，用于数值计算
import numpy as np
# 导入torch库，PyTorch深度学习框架
import torch
# 导入torch.utils模块
import torch.utils
# 导入torch.utils.data模块，用于数据集处理
import torch.utils.data
# 导入os模块，用于操作系统相关功能
import os
# 从tqdm导入进度条显示功能
from tqdm import tqdm
# 从scipy.linalg导入平方根矩阵计算函数
from scipy.linalg import sqrtm
# 尝试导入Python 3的URL检索模块
try:
    # Python 3
    from urllib.request import urlretrieve
except ImportError:
    # Python 2
    from urllib import urlretrieve

# 从utils_HSI模块导入文件打开函数
from utils_HSI import open_file
# 导入matplotlib.pyplot用于绘图
import matplotlib.pyplot as plt

# 数据集配置字典，定义了各个数据集的图像和标签文件名
DATASETS_CONFIG = {
        # 休斯顿2013年数据集配置
        'Houston13': {
            # 图像文件名
            'img': 'Houston13.mat',
            # 标签文件名
            'gt': 'Houston13_7gt.mat',
            },
        # 休斯顿2018年数据集配置
        'Houston18': {
            # 图像文件名
            'img': 'Houston18.mat',
            # 标签文件名
            'gt': 'Houston18_7gt.mat',
            },
        # 帕维亚大学数据集配置
        'paviaU': {
            # 图像文件名
            'img': 'paviaU.mat',
            # 标签文件名
            'gt': 'paviaU_7gt.mat',
            },
        # 帕维亚城堡数据集配置
        'paviaC': {
            # 图像文件名
            'img': 'paviaC.mat',
            # 标签文件名
            'gt': 'paviaC_7gt.mat',
            },
        # WHU数据集071配置
        'whu071': {
            # 图像文件名
            'img': 'paviaU.mat',
            # 标签文件名
            'gt': 'paviaU_7gt.mat',
            },
        # WHU数据集078配置
        'whu078': {
            # 图像文件名
            'img': 'paviaC.mat',
            # 标签文件名
            'gt': 'paviaC_7gt.mat',
            },
        # Dioni数据集配置
        'Dioni': {
            # 图像文件名
            'img': 'paviaU.mat',
            # 标签文件名
            'gt': 'paviaU_7gt.mat',
        },
        # Loukia数据集配置
        'Loukia': {
            # 图像文件名
            'img': 'paviaC.mat',
            # 标签文件名
            'gt': 'paviaC_7gt.mat',
        }
    }

# 尝试导入自定义数据集配置
try:
    # 从custom_datasets模块导入自定义数据集配置
    from custom_datasets import CUSTOM_DATASETS_CONFIG
    # 将自定义数据集配置更新到主配置字典中
    DATASETS_CONFIG.update(CUSTOM_DATASETS_CONFIG)
except ImportError:
    # 如果无法导入自定义数据集配置，则跳过
    pass

# 定义TqdmUpTo类，继承自tqdm，用于下载进度跟踪
class TqdmUpTo(tqdm):
    """提供`update_to(n)`方法，该方法使用`tqdm.update(delta_n)`来更新进度条."""
    def update_to(self, b=1, bsize=1, tsize=None):
        """
        b  : int, 可选参数
            已传输的数据块数量 [默认: 1].
        bsize  : int, 可选参数
            每个数据块的大小 (以tqdm单位计) [默认: 1].
        tsize  : int, 可选参数
            总大小 (以tqdm单位计). 如果 [默认: None] 则保持不变.
        """
        # 如果指定了总大小，则更新进度条的总数
        if tsize is not None:
            self.total = tsize
        # 更新进度条，增加传输的数据量
        self.update(b * bsize - self.n)  # 还会设置 self.n = b * bsize


def get_dataset(dataset_name, target_folder="./", datasets=DATASETS_CONFIG):
    """ Gets the dataset specified by name and return the related components.
    Args:
        dataset_name: string with the name of the dataset
        target_folder (optional): folder to store the datasets, defaults to ./
        datasets (optional): dataset configuration dictionary, defaults to prebuilt one
    Returns:
        img: 3D hyperspectral image (WxHxB)
        gt: 2D int array of labels
        label_values: list of class names
        ignored_labels: list of int classes to ignore
        rgb_bands: int tuple that correspond to red, green and blue bands
    """
    palette = None
    
    if dataset_name not in datasets.keys():
        raise ValueError("{} dataset is unknown.".format(dataset_name))

    dataset = datasets[dataset_name]

    folder = target_folder# + datasets[dataset_name].get('folder', dataset_name + '/')
    if dataset.get('download', False):
        # Download the dataset if is not present
        if not os.path.isdir(folder):
            os.mkdir(folder)
        for url in datasets[dataset_name]['urls']:
            # download the files
            filename = url.split('/')[-1]
            if not os.path.exists(folder + filename):
                with TqdmUpTo(unit='B', unit_scale=True, miniters=1,
                          desc="Downloading {}".format(filename)) as t:
                    urlretrieve(url, filename=folder + filename,
                                     reporthook=t.update_to)
    elif not os.path.isdir(folder):
       print("WARNING: {} is not downloadable.".format(dataset_name))

    # 如果数据集名称为Houston13
    if dataset_name == 'Houston13':
        # 加载图像
        # img = open_file(folder + 'Houston13.mat')['ori_data']
        # 读取HDF5格式的图像数据
        img1 = h5py.File(folder + 'Houston13.mat','r')['ori_data'][:]
        # for key in img1.keys():
        #     print(img1[key].name)
        # 转置图像数据维度，从(通道,宽,高)转为(宽,高,通道)
        img = np.copy(img1.transpose((1, 2, 0)))
    
        # 定义RGB波段索引
        rgb_bands = [13,20,33]
    
        # 读取HDF5格式的标签数据
        gt1 = h5py.File(folder + 'Houston13_7gt.mat','r')['map'][:]
        # 将标签数据转换为uint8类型
        gt = gt1.astype(np.uint8)
    
        # 定义标签值（类别名称）
        label_values = ["grass healthy", "grass stressed", "trees",
                        "water", "residential buildings",
                        "non-residential buildings", "road"]
    
        # 定义需要忽略的标签
        ignored_labels = [0]
    
    # 如果数据集名称为Houston18
    elif dataset_name == 'Houston18':
        # 加载图像
        # 读取HDF5格式的图像数据
        img1 = h5py.File(folder + 'Houston18.mat','r')['ori_data'][:]
        # 转置图像数据维度，从(通道,宽,高)转为(宽,高,通道)
        img = np.copy(img1.transpose((1, 2, 0)))
        # img = open_file(folder + 'Houston18.mat')['ori_data']
    
    
        # 定义RGB波段索引
        rgb_bands = [13,20,33]
    
        # 读取HDF5格式的标签数据
        gt1 = h5py.File(folder + 'Houston18_7gt.mat','r')['map'][:]
        # 将标签数据转换为uint8类型
        gt = gt1.astype(np.uint8)
    
        # 定义标签值（类别名称）
        label_values = ["grass healthy", "grass stressed", "trees",
                        "water", "residential buildings",
                        "non-residential buildings", "road"]
            
        # 定义需要忽略的标签
        ignored_labels = [0]
    
    # 如果数据集名称为paviaU
    elif dataset_name == 'paviaU':
        # 加载图像
        # 读取帕维亚大学数据集的图像数据
        img = open_file(folder + 'paviaU.mat')['ori_data']
    
        # 定义RGB波段索引
        rgb_bands = [20,30,30]
    
        # 读取帕维亚大学数据集的标签数据
        gt = open_file(folder + 'paviaU_7gt.mat')['map']
    
        # 定义标签值（类别名称）
        label_values = ["tree", "asphalt", "brick",
                        "bitumen", "shadow", 'meadow', 'bare soil']
    
        # 定义需要忽略的标签
        ignored_labels = [0]
            
    # 如果数据集名称为paviaC
    elif dataset_name == 'paviaC':
        # 加载图像
        # 读取帕维亚城堡数据集的图像数据
        img = open_file(folder + 'paviaC.mat')['ori_data']
    
        # 定义RGB波段索引
        rgb_bands = [20,30,30]
    
        # 读取帕维亚城堡数据集的标签数据
        gt = open_file(folder + 'paviaC_7gt.mat')['map']
    
        # 定义标签值（类别名称）
        label_values = ["tree", "asphalt", "brick",
                        "bitumen", "shadow", 'meadow', 'bare soil']
                        
        # 定义需要忽略的标签
        ignored_labels = [0]
    # 如果数据集名称为whu071
    elif dataset_name == 'whu071':
        # 加载图像
        # 读取WHU数据集的TIFF图像
        img = open_file(folder+'O1_0071.tif')
    
        # 定义RGB波段索引
        rgb_bands = [20, 30, 30]
    
        # 加载标签数据
        gt1 = np.load(folder + 'o1071.npy')
        # 获取标签的高和宽
        h,w = gt1.shape
        # 创建与标签相同形状的零数组
        gt = np.zeros_like(gt1)
        # 重新映射标签值
        gt[(gt1 == 1)[:h, :w]] = 1
        gt[(gt1 == 2)[:h, :w]] = 2
        gt[(gt1 == 4)[:h, :w]] = 3
    
        # 定义标签值（类别名称）
        label_values = ["tree", "asphalt", "brick",
                        "bitumen", "shadow"]
    
        # 定义需要忽略的标签
        ignored_labels = [0]
    # 如果数据集名称为whu078
    elif dataset_name == 'whu078':
        # 加载图像
        # 读取WHU数据集的TIFF图像
        img = open_file(folder + 'O1_0078.tif')
    
        # 定义RGB波段索引
        rgb_bands = [20, 30, 30]
    
        # 加载标签数据
        gt1 = np.load(folder + 'o1078.npy')
        # 获取标签的高和宽
        h, w = gt1.shape
        # 创建与标签相同形状的零数组
        gt = np.zeros_like(gt1)
        # 重新映射标签值
        gt[(gt1 == 1)[:h, :w]] = 1
        gt[(gt1 == 2)[:h, :w]] = 2
        gt[(gt1 == 4)[:h, :w]] = 3
    
        # 定义标签值（类别名称）
        label_values = ["tree", "asphalt", "brick",
                        "bitumen", "shadow"]
    
        # 定义需要忽略的标签
        ignored_labels = [0]
    # 如果数据集名称为Dioni
    elif dataset_name == 'Dioni':
        # 加载图像
        # 读取Dioni数据集的图像数据
        img = open_file(folder + 'Dioni.mat')['ori_data']
    
    
        # 定义RGB波段索引
        rgb_bands = (55, 41, 12)
    
        # 读取Dioni数据集的标签数据
        gt = open_file(folder + 'Dioni_gt_out68.mat')['map']
    
    
        # 定义标签值（类别名称）
        label_values = ['Undefined', 'Dense Urban Fabric', "Mineral Extraction Sites", 'Non Irrigated Arable Land',
                        'Fruit Trees', "Olive Groves",'Coniferous Forest','Dense Sderophyllous Vegetation',
                        'Sparse Sderophyllous Vegetation','Sparcely Vegetated Areas','Rocks and Sand',
                        'Water','Coastal Water']
    
        # 设置剩余波段数
        rest_band=108
        # 定义需要忽略的标签
        ignored_labels = [0]
    # 如果数据集名称为Loukia
    elif dataset_name == 'Loukia':
        # 加载图像
        # 读取Loukia数据集的图像数据
        img = open_file(folder + 'Loukia.mat')['ori_data']
    
    
        # 定义RGB波段索引
        rgb_bands = (55, 41, 12)
    
        # 读取Loukia数据集的标签数据
        gt = open_file(folder + 'Loukia_gt_out68.mat')['map']
    
        # 定义标签值（类别名称）
        label_values = ['Undefined', 'Dense Urban Fabric', "Mineral Extraction Sites", 'Non Irrigated Arable Land',
                        'Fruit Trees', "Olive Groves",'Coniferous Forest','Dense Sderophyllous Vegetation',
                        'Sparse Sderophyllous Vegetation','Sparcely Vegetated Areas','Rocks and Sand',
                        'Water','Coastal Water']
    
        # 设置剩余波段数
        rest_band = 108
        # 定义需要忽略的标签
        ignored_labels = [0]
    # 如果是其他自定义数据集
    else:
        # 自定义数据集
        # 使用自定义数据集加载器加载数据
        img, gt, rgb_bands, ignored_labels, label_values, palette = CUSTOM_DATASETS_CONFIG[dataset_name]['loader'](folder)

    # 过滤NaN值
    # 创建NaN掩码，标记包含NaN的像素点
    nan_mask = np.isnan(img.sum(axis=-1))
    # 如果存在NaN值，则打印警告
    if np.count_nonzero(nan_mask) > 0:
       print("Warning: NaN have been found in the data. It is preferable to remove them beforehand. Learning on NaN data is disabled.")
    # 将NaN位置的图像值设为0
    img[nan_mask] = 0
    # 将NaN位置的标签值设为0
    gt[nan_mask] = 0
    # 将0添加到需要忽略的标签列表中
    ignored_labels.append(0)

    # 去除重复的忽略标签
    ignored_labels = list(set(ignored_labels))
    # 归一化处理
    # 将图像数据转换为float32类型
    img = np.asarray(img, dtype='float32')
    
    # 获取图像的宽度、高度和波段数
    m, n, d = img.shape[0], img.shape[1], img.shape[2]
    # 将图像重塑为二维矩阵（像素数×波段数）
    img= img.reshape((m*n,-1))
    # 最大值归一化
    img = img/img.max()
    # 计算每个像素的欧几里得范数
    img_temp = np.sqrt(np.asarray((img**2).sum(1)))
    # 扩展维度以便后续计算
    img_temp = np.expand_dims(img_temp,axis=1)
    # 重复扩展后的数组以匹配波段数
    img_temp = img_temp.repeat(d,axis=1)
    # 避免除以零的情况
    img_temp[img_temp==0]=1
    # 对每个像素进行归一化
    img = img/img_temp
    # 将图像恢复为三维格式
    img = np.reshape(img,(m,n,-1))

    # 返回处理后的图像、标签、标签值、忽略标签、RGB波段和调色板
    return img, gt, label_values, ignored_labels, rgb_bands, palette


# 定义HyperX类，继承自PyTorch数据集基类，用于处理高光谱图像场景
class HyperX(torch.utils.data.Dataset):
    """ 高光谱图像场景的通用类 """

    def __init__(self, data, gt, transform=None, **hyperparams):
        """
        参数:
            data: 3D高光谱图像
            gt: 2D标签数组
            patch_size: int, 空间邻域的大小
            center_pixel: bool, 设为True时仅考虑中心像素的标签
            data_augmentation: bool, 设为True时执行随机翻转
            supervision: 'full' 或 'semi' 监督学习算法
        """
        # 调用父类构造函数
        super(HyperX, self).__init__()
        # 设置数据转换函数
        self.transform = transform
        # 设置高光谱图像数据
        self.data = data
        # 设置标签数据
        self.label = gt
        # 设置补丁大小
        self.patch_size = hyperparams['patch_size']
        # 设置需要忽略的标签集合
        self.ignored_labels = set(hyperparams['ignored_labels'])
        # 设置翻转增强开关
        self.flip_augmentation = hyperparams['flip_augmentation']
        # 设置辐射噪声增强开关
        self.radiation_augmentation = hyperparams['radiation_augmentation'] 
        # 设置混合噪声增强开关
        self.mixture_augmentation = hyperparams['mixture_augmentation'] 
        # 设置是否只关注中心像素
        self.center_pixel = hyperparams['center_pixel']
        # 获取监督模式
        supervision = hyperparams['supervision']
        # 全监督：使用所有非忽略标签的像素
        if supervision == 'full':
            # 创建全1掩码
            mask = np.ones_like(gt)
            # 对于被忽略的标签，将其在掩码中的值设为0
            for l in self.ignored_labels:
                mask[gt == l] = 0
        # 半监督：使用所有像素，除了填充部分
        elif supervision == 'semi':
            # 创建全1掩码
            mask = np.ones_like(gt)
        # 获取掩码中非零元素的位置
        x_pos, y_pos = np.nonzero(mask)
        # 计算补丁半径
        p = self.patch_size // 2
        # 过滤掉靠近边界的像素，确保能提取完整补丁
        self.indices = np.array([(x,y) for x,y in zip(x_pos, y_pos) if x > p and x < data.shape[0] - p and y > p and y < data.shape[1] - p])
        # 提取对应位置的标签值
        self.labels = [self.label[x,y] for x,y in self.indices]
        
        # 保存当前随机状态
        state = np.random.get_state()
        # 随机打乱索引
        np.random.shuffle(self.indices)
        # 恢复随机状态
        np.random.set_state(state)
        # 随机打乱标签
        np.random.shuffle(self.labels)

    @staticmethod
    # 定义翻转方法，用于数据增强
    def flip(*arrays):
        # 随机决定是否水平翻转
        horizontal = np.random.random() > 0.5
        # 随机决定是否垂直翻转
        vertical = np.random.random() > 0.5
        # 如果进行水平翻转
        if horizontal:
            # 对所有数组进行水平翻转
            arrays = [np.fliplr(arr) for arr in arrays]
        # 如果进行垂直翻转
        if vertical:
            # 对所有数组进行垂直翻转
            arrays = [np.flipud(arr) for arr in arrays]
        # 返回翻转后的数组
        return arrays

    @staticmethod
    # 定义辐射噪声方法，用于添加辐射噪声增强
    def radiation_noise(data, alpha_range=(0.9, 1.1), beta=1/25):
        # 随机生成alpha值
        alpha = np.random.uniform(*alpha_range)
        # 生成正态分布噪声
        noise = np.random.normal(loc=0., scale=1.0, size=data.shape)
        # 返回添加辐射噪声后的数据
        return alpha * data + beta * noise

    # 定义混合噪声方法，用于添加混合噪声增强
    def mixture_noise(self, data, label, beta=1/25):
        # 随机生成两个alpha值
        alpha1, alpha2 = np.random.uniform(0.01, 1., size=2)
        # 生成正态分布噪声
        noise = np.random.normal(loc=0., scale=1.0, size=data.shape)
        # 创建与原数据相同形状的零数组
        data2 = np.zeros_like(data)
        # 遍历标签的所有位置和值
        for  idx, value in np.ndenumerate(label):
            # 如果当前标签值不是被忽略的标签
            if value not in self.ignored_labels:
                # 找到相同标签值的索引
                l_indices = np.nonzero(self.labels == value)[0]
                # 随机选择一个索引
                l_indice = np.random.choice(l_indices)
                # 断言确保选择的标签值一致
                assert(self.labels[l_indice] == value)
                # 获取对应的坐标
                x, y = self.indices[l_indice]
                # 从原始数据中复制对应位置的数据
                data2[idx] = self.data[x,y]
        # 返回混合噪声后的数据
        return (alpha1 * data + alpha2 * data2) / (alpha1 + alpha2) + beta * noise

    # 获取数据集长度的方法
    def __len__(self):
        # 返回索引的数量
        return len(self.indices)

    # 获取数据项的方法
    def __getitem__(self, i):
        # 获取索引i对应的坐标
        x, y = self.indices[i]
        # 计算补丁区域的左上角坐标
        x1, y1 = x - self.patch_size // 2, y - self.patch_size // 2
        # 计算补丁区域的右下角坐标
        x2, y2 = x1 + self.patch_size, y1 + self.patch_size

        # 提取数据补丁
        data = self.data[x1:x2, y1:y2]
        # 提取标签补丁
        label = self.label[x1:x2, y1:y2]

        # 如果启用翻转增强且补丁大小大于1且随机概率小于0.5
        if self.flip_augmentation and self.patch_size > 1 and np.random.random() < 0.5:
            # 执行数据增强（仅对2D补丁）
            data, label = self.flip(data, label)
        # 如果启用辐射噪声增强且随机概率小于0.5
        if self.radiation_augmentation and np.random.random() < 0.5:
                # 应用辐射噪声
                data = self.radiation_noise(data)
        # 如果启用混合噪声增强且随机概率小于0.5
        if self.mixture_augmentation and np.random.random() < 0.5:
                # 应用混合噪声
                data = self.mixture_noise(data, label)

        # 将数据复制到numpy数组（PyTorch不喜欢numpy视图）
        data = np.asarray(np.copy(data).transpose((2, 0, 1)), dtype='float32')
        # 将标签复制到numpy数组
        label = np.asarray(np.copy(label), dtype='int64')

        # 将数据加载到PyTorch张量
        data = torch.from_numpy(data)
        label = torch.from_numpy(label)
        # 如果需要且补丁大小大于1，则提取中心标签
        if self.center_pixel and self.patch_size > 1:
            # 获取中心像素的标签
            label = label[self.patch_size // 2, self.patch_size // 2]
        # 当处理单个光谱时移除未使用的维度
        elif self.patch_size == 1:
            # 移除空间维度
            data = data[:, 0, 0]
            # 移除空间维度
            label = label[0, 0]
        # 否则使用预先存储的标签
        else:
            # 使用预存的标签
            label = self.labels[i]
            
        # 为3D CNN添加第四维
        # if self.patch_size > 1:
        #     # 创建4D数据（（批处理 x）平面 x 通道 x 宽度 x 高度）
        #     data = data.unsqueeze(0)
        # plt.imshow(data[[10,23,23],:,:].permute(1,2,0))
        # plt.show()
        # 返回数据和标签
        return data, label

# 定义数据预取器类，用于异步加载数据以提高GPU利用率
class data_prefetcher():
    def __init__(self, loader):
        # 将数据加载器转换为迭代器
        self.loader = iter(loader)
        # 创建CUDA流用于异步数据传输
        self.stream = torch.cuda.Stream()
        # 预加载第一批数据
        self.preload()

    def preload(self):
        try:
            # 从数据加载器中获取下一个批次的数据和标签
            self.data, self.label = next(self.loader)

        except StopIteration:
            # 如果没有更多数据，则将next_input设为None
            self.next_input = None

            return
        # 在CUDA流中异步地将数据和标签移到GPU
        with torch.cuda.stream(self.stream):
            # 将数据移动到GPU，非阻塞模式
            self.data = self.data.cuda(non_blocking=True)
            # 将标签移动到GPU，非阻塞模式
            self.label = self.label.cuda(non_blocking=True)

    def next(self):
        # 等待CUDA流完成数据传输
        torch.cuda.current_stream().wait_stream(self.stream)
        # 保存当前数据和标签
        data = self.data
        label = self.label

        # 预加载下一组数据
        self.preload()
        # 返回当前的数据和标签
        return data, label