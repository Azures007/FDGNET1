package org.thingsboard.server.common.data;

import lombok.Data;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 存储每个字段配置及填写值的类
 * @date 2025/7/9 16:15:26
 */
@Data
public class TSysFieldConfig {

    private String fieldName;
    private String fielValue;
    private String isEnabled;
    private String fieldType;
    private String paramRange;
    private String dropdownField;
    private String unit;
    private String isRequired;

    // 快速构建方法，方便填充数据

    /**
     *
     * @param fieldName
     * @param fillValue
     * @param isEnabled
     * @param fieldType
     * @param paramRange
     * @param dropdownField
     * @param unit
     * @param isRequired
     * @return
     */
    public static TSysFieldConfig build(String fieldName, String fillValue,
                                    String isEnabled, String fieldType,
                                    String paramRange, String dropdownField,
                                    String unit, String isRequired) {
        TSysFieldConfig config = new TSysFieldConfig();
        config.setFieldName(fieldName);
        config.setFielValue(fillValue);
        config.setIsEnabled(isEnabled);
        config.setFieldType(fieldType);
        config.setParamRange(paramRange);
        config.setDropdownField(dropdownField);
        config.setUnit(unit);
        config.setIsRequired(isRequired);
        return config;
    }


}
