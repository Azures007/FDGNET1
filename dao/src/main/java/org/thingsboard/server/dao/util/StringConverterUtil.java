package org.thingsboard.server.dao.util;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 字符串转换器
 * @date 2025/7/9 10:29:17
 */
public class StringConverterUtil {

    /**
     * 将小驼峰转换成下划线格式
     * 例：materialId===》material_id
     * @param camelCase
     * @return
     */
    public static String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        // 使用正则表达式在大写字母前插入下划线，并转为小写
        return camelCase.replaceAll("(?<!^)(?=[A-Z])", "_").toLowerCase();
    }




}
