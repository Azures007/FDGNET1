package org.thingsboard.server.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/4/26 16:07
 * @Description:
 */
@Data
public class DeviceExcelVo extends BaseRowModel {

    @ExcelProperty(value = "设备名称",index = 0)
    private String deviceName;

    @ExcelProperty(value = "设备编号",index = 1)
    private String deviceNumber;

    @ExcelProperty(value = "基地",index = 2)
    private String pkOrgName;

    @ExcelProperty(value = "备注",index = 3)
    private String note;

    @ExcelProperty(value = "状态",index = 4)
    private String enabled="启用";


}
