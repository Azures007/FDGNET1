# 导入PyTorch深度学习框架
import torch
# 导入PyTorch神经网络模块
import torch.nn as nn
# 导入NumPy数值计算库
import numpy as np
# 导入数学模块
import math
# 从PyTorch神经网络参数模块导入Parameter类
from torch.nn.parameter import Parameter
# 导入PyTorch函数式API
import torch.nn.functional as F


# 定义欧几里得距离计算函数
def Eu_dis(x):
    """
    计算x中每行之间的距离
    :param x: N X D
                N: 对象数量
                D: 特征维度
    :return: N X N 距离矩阵
    """
    # 将输入转换为矩阵格式
    x = np.mat(x)
    # 计算每行的平方和
    aa = np.sum(np.multiply(x, x), 1)
    # 计算矩阵乘积
    ab = x * x.T
    # 计算距离矩阵公式：||a||² + ||b||² - 2<a,b>
    dist_mat = aa + aa.T - 2 * ab
    # 将负值设为0（处理数值误差）
    dist_mat[dist_mat < 0] = 0
    # 开平方根得到欧几里得距离
    dist_mat = np.sqrt(dist_mat)
    # 确保距离矩阵对称性
    dist_mat = np.maximum(dist_mat, dist_mat.T)
    # 返回距离矩阵
    return dist_mat


# 定义点积注意力机制类，继承自PyTorch神经网络模块
class dot_attention(nn.Module):

    # 初始化函数，设置注意力机制参数
    def __init__(self, attention_dropout=0.0):
        # 调用父类构造函数
        super(dot_attention, self).__init__()
        # 创建Dropout层用于防止过拟合
        self.dropout = nn.Dropout(attention_dropout)
        # 创建Softmax层用于注意力权重归一化
        self.softmax = nn.Softmax(dim=2)

    # 前向传播函数
    def forward(self, q, k, v, scale=None, attn_mask=None):
        # 计算查询和键的点积注意力得分
        attention = torch.bmm(q, k.transpose(1, 2))
        # 如果提供了缩放因子，则应用缩放
        if scale:
            attention = attention * scale
        # 如果提供了注意力掩码，则应用掩码
        if attn_mask:
            attention = attention.masked_fill(attn_mask, -np.inf)

        # 对注意力得分应用Softmax归一化
        attention = self.softmax(attention)
        # 应用Dropout
        attention = self.dropout(attention)
        # 计算加权值：注意力权重乘以值
        context = torch.bmm(attention, v)
        # 返回上下文向量和注意力权重
        return context, attention


# 定义多头注意力机制类，继承自PyTorch神经网络模块
class MultiHeadAttention(nn.Module):
    # 初始化函数，设置多头注意力参数
    def __init__(self, model_dim=256, num_heads=4, dropout=0.0, version='v1'):
        # 调用父类构造函数
        super(MultiHeadAttention, self).__init__()

        # 计算每个头的维度
        self.dim_per_head = model_dim // num_heads
        # 保存头的数量
        self.num_heads = num_heads
        # 创建键、值、查询的线性变换层
        self.linear_k = nn.Linear(model_dim, self.dim_per_head * num_heads)
        self.linear_v = nn.Linear(model_dim, self.dim_per_head * num_heads)
        self.linear_q = nn.Linear(model_dim, self.dim_per_head * num_heads)

        # 创建点积注意力机制实例
        self.dot_product_attention = dot_attention(dropout)

        # 创建最终的线性变换层
        self.linear_final = nn.Linear(model_dim, model_dim)
        # 创建Dropout层
        self.dropout = nn.Dropout(dropout)
        # 创建层归一化层
        self.layer_norm = nn.LayerNorm(model_dim)
        # 保存版本信息
        self.version = version

    # 前向传播函数
    def forward(self, key, value, query, attn_mask=None):
        # 如果是版本2
        if self.version == 'v2':
            # 设置批次大小为1
            B = 1
            # key, value,query = key_value_query
            # 增加维度
            key = key.unsqueeze(1)
            value = value.unsqueeze(1)
            query = query.unsqueeze(1)
            # 保存残差连接
            residual = query

            # 获取每个头的维度和头数
            dim_per_head = self.dim_per_head
            num_heads = self.num_heads

            # 对键、值、查询进行线性变换
            key = self.linear_k(key)
            value = self.linear_v(value)
            query = self.linear_q(query)

            # 重塑张量并转置以适应多头注意力
            key = key.view(key.size(0), B * num_heads, dim_per_head).transpose(0, 1)
            value = value.view(value.size(0), B * num_heads, dim_per_head).transpose(0, 1)
            query = query.view(query.size(0), B * num_heads, dim_per_head).transpose(0, 1)

            # 计算缩放因子
            scale = (key.size(-1) // num_heads) ** -0.5
            # 应用点积注意力机制
            context, attention = self.dot_product_attention(query, key, value, scale, attn_mask)
            # (query, key, value, scale, attn_mask)
            # 重塑上下文向量
            context = context.transpose(0, 1).contiguous().view(query.size(1), B, dim_per_head * num_heads)  # set2
            # 应用最终线性变换
            output = self.linear_final(context)
            # 应用Dropout
            output = self.dropout(output)
            # 应用层归一化和残差连接
            output = self.layer_norm(residual + output)
            # output = residual + output

        # 如果是版本1
        elif self.version == 'v1':
            # key, value, query = key_value_query

            # 增加批次维度
            key = key.unsqueeze(0)
            value = value.unsqueeze(0)
            query = query.unsqueeze(0)
            # 保存残差连接
            residual = query
            # 获取张量维度
            B, L, C = key.size()
            # 获取每个头的维度和头数
            dim_per_head = self.dim_per_head
            num_heads = self.num_heads
            # 获取批次大小
            batch_size = key.size(0)

            # 对键、值、查询进行线性变换
            key = self.linear_k(key)
            value = self.linear_v(value)
            query = self.linear_q(query)

            # 重塑张量以适应多头注意力
            key = key.view(batch_size * num_heads, -1, dim_per_head)
            value = value.view(batch_size * num_heads, -1, dim_per_head)
            query = query.view(batch_size * num_heads, -1, dim_per_head)

            # 如果提供了注意力掩码，则重复应用于所有头
            if attn_mask:
                attn_mask = attn_mask.repeat(num_heads, 1, 1)
            # 计算缩放因子
            scale = (key.size(-1) // num_heads) ** -0.5
            # 应用点积注意力机制
            context, attention = self.dot_product_attention(query, key, value, scale, attn_mask)
            # 重塑上下文向量
            context = context.view(batch_size, -1, dim_per_head * num_heads)  # set1: directly use 'view'
            # 应用最终线性变换
            output = self.linear_final(context)
            # 应用Dropout
            output = self.dropout(output)
            # 应用残差连接（带权重）
            # output = self.layer_norm(residual + output)
            output = 0.9 * residual + 0.1 * output

        # 返回输出和注意力权重（去除额外维度）
        return output.squeeze(), attention.squeeze()


# 定义超图神经网络类，继承自PyTorch神经网络模块
class HyperGraph(nn.Module):
    '''
    Feng et al. 'Hypergraph Neural Networks'
    https://arxiv.org/pdf/1809.09401.pdf
    https://github.com/iMoonLab/HGNN
    '''

    # 初始化函数，设置超图参数
    def __init__(self, emb_dim=256, K_neigs=[3], num_layer=1, distance_metrix='Eu_dis'):
        # 调用父类构造函数
        super(HyperGraph, self).__init__()
        # 保存K近邻参数（可以是列表或单个值）
        self.K_neigs = K_neigs if isinstance(K_neigs, list) else [K_neigs]
        # 保存距离度量方法
        self.distance_metric = distance_metrix
        # 根据层数选择不同的HGNN卷积层
        if num_layer == 1:
            # 单层HGNN卷积
            self.hgnn_conv_layer = single_layer_HGNN_conv(emb_dim, emb_dim, bias=True)
        else:
            # 双层HGNN卷积
            self.hgnn_conv_layer = double_layer_HGNN_conv(emb_dim, emb_dim // 2, bias=True)

    # 前向传播函数
    def forward(self, node_feat, G):
        # 建立KNN超图
        # node_feat = node_feat[0]
        # with torch.no_grad():
        #     tmp = self.construct_H_with_KNN(node_feat.detach().cpu().numpy())
        #     H = self.hyperedge_concat(None, tmp)
        #     G = self.generate_G_from_H(H)
        #     G = torch.Tensor(G).to(node_feat.device)
        #     H = torch.Tensor(H).to(node_feat.device)
        # 应用HGNN卷积层
        outputs = self.hgnn_conv_layer(node_feat, G)
        # 返回输出和图结构
        return outputs, G

    # 从超图关联矩阵H生成图拉普拉斯矩阵G
    def generate_G_from_H(self, H, variable_weight=False):
        """
        从超图关联矩阵H计算G
        :param H: 超图关联矩阵H
        :param variable_weight: 超边权重是否可变
        :return: G
        """
        # 如果H不是列表格式
        if type(H) != list:
            # 调用内部函数处理单个矩阵
            return self._generate_G_from_H(H, variable_weight)
        else:
            # 处理多个矩阵的情况
            G = []
            # 遍历每个子矩阵
            for sub_H in H:
                # 递归处理每个子矩阵
                G.append(self.generate_G_from_H(sub_H, variable_weight))
            # 返回处理后的矩阵列表
            return G

    # 超边连接函数
    def hyperedge_concat(self, *H_list):
        """
        连接H_list中的超边组
        :param H_list: 包含两个或多个超图关联矩阵的超边组
        :return: 融合的超图关联矩阵
        """
        # 初始化结果矩阵
        H = None
        # 遍历所有输入矩阵
        for h in H_list:
            # 如果矩阵不为空且不为空列表
            if h is not None and h != []:
                # 对于第一个添加到融合超图关联矩阵的H
                if H is None:
                    H = h
                else:
                    # 如果不是列表格式，直接水平拼接
                    if type(h) != list:
                        H = np.hstack((H, h))
                    else:
                        # 如果是列表格式，逐个拼接
                        tmp = []
                        for a, b in zip(H, h):
                            tmp.append(np.hstack((a, b)))
                        H = tmp
        # 返回融合后的矩阵
        return H

    # 使用KNN构建超图关联矩阵
    def construct_H_with_KNN(self, X, split_diff_scale=False, is_probH=True, m_prob=1):
        """
        从原始节点特征矩阵初始化多尺度超图顶点-边矩阵
        :param X: N_object x feature_number
        :param K_neigs: 邻居扩展数量
        :param split_diff_scale: 是否在不同邻居尺度上分割超边组
        :param is_probH: 概率顶点-边矩阵还是二值矩阵
        :param m_prob: 概率参数
        :return: N_object x N_hyperedge
        """
        # 如果输入不是2D矩阵，则重塑为2D
        if len(X.shape) != 2:
            X = X.reshape(-1, X.shape[-1])
        # 如果K_neigs是整数，则转换为列表
        if type(self.K_neigs) == int:
            K_neigs = [self.K_neigs]
        # 计算欧几里得距离矩阵
        dis_mat = Eu_dis(X)
        # 初始化结果列表
        H = []
        # 遍历每个K近邻参数
        for k_neig in self.K_neigs:
            # 根据距离矩阵构建超图关联矩阵
            H_tmp = self.construct_H_with_KNN_from_distance(dis_mat, k_neig, is_probH, m_prob)
            # 如果不分割不同尺度
            if not split_diff_scale:
                # 连接超边
                H = self.hyperedge_concat(H, H_tmp)
            else:
                # 分别保存不同尺度的超边
                H.append(H_tmp)
        # 返回构建的超图关联矩阵
        return H

    # 根据距离矩阵构建超图关联矩阵
    def construct_H_with_KNN_from_distance(self, dis_mat, k_neig, is_probH=True, m_prob=1):
        """
        从超图节点距离矩阵构建超图关联矩阵
        :param dis_mat: 节点距离矩阵
        :param k_neig: K最近邻
        :param is_probH: 概率顶点-边矩阵还是二值矩阵
        :param m_prob: 概率参数
        :return: N_object X N_hyperedge
        """
        # 获取对象数量
        n_obj = dis_mat.shape[0]
        # 从每个节点的中心特征空间构建超边
        n_edge = n_obj
        # 初始化超图关联矩阵
        H = np.zeros((n_obj, n_edge))
        # 遍历每个中心节点
        for center_idx in range(n_obj):
            # 将对角线元素设为0（自己到自己的距离）
            dis_mat[center_idx, center_idx] = 0
            # 获取距离向量
            dis_vec = dis_mat[center_idx]
            # 对距离进行排序获取最近邻索引
            nearest_idx = np.array(np.argsort(dis_vec)).squeeze()
            # 计算平均距离
            avg_dis = np.average(dis_vec)
            # 确保中心节点在最近邻中
            if not np.any(nearest_idx[:k_neig] == center_idx):
                nearest_idx[k_neig - 1] = center_idx
            # 为最近邻节点设置关联权重
            for node_idx in nearest_idx[:k_neig]:
                # 如果使用概率矩阵
                if is_probH:
                    # 使用高斯核计算概率权重
                    H[node_idx, center_idx] = np.exp(-dis_vec[0, node_idx] ** 2 / (m_prob * avg_dis) ** 2)
                else:
                    # 使用二值权重
                    H[node_idx, center_idx] = 1.0
        # 返回超图关联矩阵
        return H

    # 内部函数：从超图关联矩阵H生成图拉普拉斯矩阵G
    def _generate_G_from_H(self, H, variable_weight=False):
        """
        从超图关联矩阵H计算G
        :param H: 超图关联矩阵H
        :param variable_weight: 超边权重是否可变
        :return: G
        """
        # 将H转换为NumPy数组
        H = np.array(H)
        # 获取超边数量
        n_edge = H.shape[1]
        # 超边的权重（初始化为1）
        W = np.ones(n_edge)
        # 节点的度数（按行求和）
        DV = np.sum(H * W, axis=1)
        # 超边的度数（按列求和）
        DE = np.sum(H, axis=0)
        # 超边度数的逆对角矩阵
        invDE = np.mat(np.diag(np.power(DE, -1)))
        # 节点度数的平方根逆对角矩阵
        DV2 = np.mat(np.diag(np.power(DV, -0.5)))
        # 权重对角矩阵
        W = np.mat(np.diag(W))
        # 将H转换为矩阵格式
        H = np.mat(H)
        # 计算H的转置
        HT = H.T

        # 如果权重可变
        if variable_weight:
            # 计算DV2 * H
            DV2_H = DV2 * H
            # 计算invDE * HT * DV2
            invDE_HT_DV2 = invDE * HT * DV2
            # 返回三个矩阵
            return DV2_H, W, invDE_HT_DV2
        else:
            # 计算标准的图拉普拉斯矩阵：DV2 * H * W * invDE * HT * DV2
            G = DV2 * H * W * invDE * HT * DV2
            # 返回图拉普拉斯矩阵
            return G


# 定义单层HGNN卷积类，继承自PyTorch神经网络模块
class single_layer_HGNN_conv(nn.Module):
    # 初始化函数，设置卷积参数
    def __init__(self, in_ft, out_ft, dropout=0.5, bias=True):
        # 调用父类构造函数
        super(single_layer_HGNN_conv, self).__init__()
        # 创建线性变换层
        self.linear = nn.Linear(in_ft, out_ft, bias=bias)
        # 保存dropout率
        self.dropout = dropout
        # 重置参数
        self.reset_parameters()

    # 重置参数函数
    def reset_parameters(self):
        # 计算标准差
        stdv = 1. / math.sqrt(self.linear.weight.size(1))
        # 使用正态分布初始化权重
        nn.init.normal_(self.linear.weight, std=stdv)
        # 将偏置初始化为0
        nn.init.constant_(self.linear.bias, 0)

    # 前向传播函数
    def forward(self, x: torch.Tensor, G: torch.Tensor):
        # 应用线性变换
        x = self.linear(x)
        # 应用图卷积：G * x
        x = G.matmul(x)
        # 返回卷积结果
        return x


# 定义双层HGNN卷积类，继承自PyTorch神经网络模块
class double_layer_HGNN_conv(nn.Module):
    # 初始化函数，设置卷积参数
    def __init__(self, in_ft, out_ft, dropout=0.5, bias=True):
        # 调用父类构造函数
        super(double_layer_HGNN_conv, self).__init__()
        # 创建第一层线性变换
        self.linear1 = nn.Linear(in_ft, out_ft, bias=bias)
        # 创建第二层线性变换
        self.linear2 = nn.Linear(out_ft, in_ft, bias=bias)
        # 保存dropout率
        self.dropout = dropout
        # 重置参数
        self.reset_parameters()

    # 重置参数函数
    def reset_parameters(self):
        # 计算标准差
        stdv = 1. / math.sqrt(self.linear1.weight.size(1))
        # 使用正态分布初始化第一层权重
        nn.init.normal_(self.linear1.weight, std=stdv)
        # 将第一层偏置初始化为0
        nn.init.constant_(self.linear1.bias, 0)
        # 使用正态分布初始化第二层权重
        nn.init.normal_(self.linear2.weight, std=stdv)
        # 将第二层偏置初始化为0
        nn.init.constant_(self.linear2.bias, 0)

    # 前向传播函数
    def forward(self, x: torch.Tensor, G: torch.Tensor):
        # 第一层线性变换
        x = self.linear1(x)
        # 应用图卷积和ReLU激活
        x = F.relu(G.matmul(x))
        # 应用Dropout
        x = F.dropout(x, self.dropout)
        # 第二层线性变换
        x = self.linear2(x)
        # 应用图卷积和ReLU激活
        x = F.relu(G.matmul(x))
        # 返回卷积结果
        return x