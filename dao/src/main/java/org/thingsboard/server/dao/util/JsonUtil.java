package org.thingsboard.server.dao.util;

import com.alibaba.fastjson.JSON;
import org.thingsboard.server.common.data.mes.sys.TSysFieldConfig;

import java.util.*;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description Json工具类
 * @date 2025/7/9 15:10:59
 */
public class JsonUtil {


//    public static List<List<String>> beanToDateConverter(){
//
//        beanToListStrConverter();
//
//
//        return null;
//    };

    /**
     * 每行顺序是：  品名, 字段名称, 是否启用, 字段类型, 参数范围, 下拉框字段, 单位, 是否必填
     */
    /**
     *
     * @param productName 品名
     * @param fieldName 字段名称
     * @param fielValue  字段值 // 若有填写值，可从其他地方获取（比如前端输入、额外列），这里先空着
     * @param isEnabled  是否启用
     * @param fieldType 字段类型
     * @param paramRange  参数范围
     * @param dropdownField  下拉框字段
     * @param unit  单位
     * @param isRequired  是否必填
     * @return
     */
    public static List<String> beanToListStrConverter(String productName ,
                                                         String fieldName ,
                                                         String fielValue ,
                                                         String isEnabled ,
                                                         String fieldType ,
                                                         String paramRange ,
                                                         String dropdownField ,
                                                         String unit ,
                                                         String isRequired){
        List<String> list =List.of( productName, fieldName,fielValue, isEnabled, fieldType, paramRange, dropdownField, unit, isRequired);
        return list;
    };


    /**
     * 转换json
     * 例：{"消防栓门板（整个框架）
     * ":{"QCCF0001":[{"dropdownField":"","fieldName":"QCCF0001","fieldType":"ZDLX0001","fillValue":"","isEnabled":"1","isRequired":"1","paramRange":"","unit":""}],
     * "QCCF0002":[{"dropdownField":"","fieldName":"QCCF0002","fieldType":"ZDLX0001","fillValue":"","isEnabled":"1","isRequired":"1","paramRange":"","unit":""}],
     * "QCCF0003":[{"dropdownField":"","fieldName":"QCCF0003","fieldType":"ZDLX0001","fillValue":"","isEnabled":"1","isRequired":"1","paramRange":"","unit":""}],
     * "QCCF0004":[{"dropdownField":"是，否","fieldName":"QCCF0004","fieldType":"ZDLX0004","fillValue":"","isEnabled":"1","isRequired":"1","paramRange":"","unit":""}]}}
     * @param tableData
     * @return
     */
    public static String tableToJsonConverter(List<List<String>> tableData) {
//        // 模拟从表格读取的数据：每行是 [选择(布尔), 序号, 品名, 字段名称, 是否启用, 字段类型, 参数范围, 下拉框字段, 单位, 是否必填]
//        List<List<String>> tableData = new ArrayList<>();
//        // 第一行数据（示例）
//        tableData.add(List.of("true", "1", "中筋粉03", "每锅用量标准(kg)", "是", "固定值", "50", "", "", "是"));
//        tableData.add(List.of("true", "2", "中筋粉03", "每锅用量(kg)", "是", "文本", "", "", "", "是"));
//        tableData.add(List.of("true", "3", "中筋粉03", "原料批次", "是", "文本", "", "", "", "是"));
//        tableData.add(List.of("true", "4", "中筋粉03", "是否超保质期", "是", "下拉框", "", "是,否", "", "是"));

        // 存储最终结果：key 是“品名”，value 是各字段配置
        Map<String, Map<String, List<TSysFieldConfig>>> resultMap = new HashMap<>();

        // 遍历表格数据，构建 JSON 结构
        for (List<String> row : tableData) {
//            String isSelected = row.get(0);
//            if (!"true".equalsIgnoreCase(isSelected)) {
//                continue; // 跳过未选中的行
//            }
            String productName = row.get(0);
            String fieldName = row.get(1);
            String fielValue = ""; // 若有填写值，可从其他地方获取（比如前端输入、额外列），这里先空着
            String isEnabled = row.get(3);
            String fieldType = row.get(4);
            String paramRange = row.get(5);
            String dropdownField = row.get(6);
            String unit = row.get(7);
            String isRequired = row.get(8);

            // 按“品名”分组，初始化分组
            resultMap.putIfAbsent(productName, new HashMap<>());
            Map<String, List<TSysFieldConfig>> productMap = resultMap.get(productName);

            // 构建字段配置
            TSysFieldConfig config = TSysFieldConfig.build(
                    fieldName, fielValue,
                    isEnabled, fieldType,
                    paramRange, dropdownField,
                    unit, isRequired
            );

            // 字段名称作为 key，存储配置列表（可扩展多个填写值场景）
            productMap.putIfAbsent(fieldName, new ArrayList<>());
            productMap.get(fieldName).add(config);
        }

        // 转成最终 JSON（若只有一个品名，可直接取第一个）
        String finalJson = JSON.toJSONString(resultMap);
        return finalJson;
    }


    /**
     * 转换json   采用了 productName 和 fieldList 的嵌套方式
     * 例：[
     *     {
     *         "productName": "消防栓门板",
     *         "fieldList": [
     *             {
     *                 "dropdownField": "",
     *                 "fieldName": "QCCF0001",
     *                 "fieldType": "ZDLX0001",
     *                 "fillValue": "",
     *                 "isEnabled": "1",
     *                 "isRequired": "1",
     *                 "paramRange": "",
     *                 "unit": ""
     *             },
     *             {
     *                 "dropdownField": "",
     *                 "fieldName": "QCCF0002",
     *                 "fieldType": "ZDLX0001",
     *                 "fillValue": "",
     *                 "isEnabled": "1",
     *                 "isRequired": "1",
     *                 "paramRange": "",
     *                 "unit": ""
     *             }        ]
     *     }
     * ]
     * @param tableData
     * @return
     */
    public static String tableToJsonConverter2(List<List<String>> tableData) {
//        // 模拟从表格读取的数据
//        List<List<String>> tableData = new ArrayList<>();
//        tableData.add(List.of("true", "1", "消防栓门板", "QCCF0001", "1", "ZDLX0001", "", "", "", "1"));
//        tableData.add(List.of("true", "2", "消防栓门板", "QCCF0002", "1", "ZDLX0001", "", "", "", "1"));
//        tableData.add(List.of("true", "3", "消防栓门板", "QCCF0003", "1", "ZDLX0001", "", "", "", "1"));
//        tableData.add(List.of("true", "4", "消防栓门板", "QCCF0004", "1", "ZDLX0004", "", "是，否", "", "1"));

        // 按品名分组存储产品信息
        Map<String, Product> productMap = new LinkedHashMap<>();

        // 遍历表格数据
        for (List<String> row : tableData) {
//            String isSelected = row.get(0);
//            if (!"true".equalsIgnoreCase(isSelected)) continue;

            String productName = row.get(0);
            String fieldName = row.get(1);
            String fielValue = ""; // 若有填写值，可从其他地方获取（比如前端输入、额外列），这里先空着
            String isEnabled = row.get(3);
            String fieldType = row.get(4);
            String paramRange = row.get(5);
            String dropdownField = row.get(6);
            String unit = row.get(7);
            String isRequired = row.get(8);

            // 初始化产品对象
            productMap.putIfAbsent(productName, new Product(productName, new ArrayList<>()));
            Product product = productMap.get(productName);

            // 添加字段配置
            TSysFieldConfig fieldConfig = TSysFieldConfig.build(
                    fieldName,fielValue,isEnabled,fieldType,paramRange,dropdownField,unit,isRequired
            );


            product.getFieldList().add(fieldConfig);
        }

        // 转换为最终的 JSON 数组
        List<Product> productList = new ArrayList<>(productMap.values());
        String finalJson = JSON.toJSONString(productList, true);
        System.out.println(finalJson);
        return finalJson;
    }

    // 产品类
    static class Product {
        private String productName;
        private List<TSysFieldConfig> fieldList;

        public Product(String productName, List<TSysFieldConfig> fieldList) {
            this.productName = productName;
            this.fieldList = fieldList;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public List<TSysFieldConfig> getFieldList() {
            return fieldList;
        }

        public void setFieldList(List<TSysFieldConfig> fieldList) {
            this.fieldList = fieldList;
        }
    }


}
