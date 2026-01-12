package org.thingsboard.server.dao.mes.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ApiModel("温湿仪设备运行看板设备对象")
public class TanSensorDeviceRunVo extends BaseRowModel {
    @ExcelProperty(value = "运行日期",index = 1)
    @ApiModelProperty("运行日期")
    private String runDate;
    @ExcelProperty(value = "设备编码",index = 2)
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ExcelProperty(value = "设备名称",index = 3)
    @ApiModelProperty("设备名称")
    private String deviceName;
    @ExcelProperty(value = "运行时间",index = 4)
    @ApiModelProperty("运行时间")
    private Long runSeund;
    @ExcelProperty(value = "最高温度",index = 5)
    @ApiModelProperty("最高温度")
    private BigDecimal maxTemp;
    @ExcelProperty(value = "最低温度",index = 6)
    @ApiModelProperty("最低温度")
    private BigDecimal minTemp;
    @ExcelProperty(value = "平均温度",index = 7)
    @ApiModelProperty("平均温度")
    private BigDecimal avgTemp;
    @ExcelProperty(value = "温度达标率",index = 8)
    @ApiModelProperty("温度达标率")
    private BigDecimal tempSuccess;
    @ExcelProperty(value = "温度超标次数",index = 9)
    @ApiModelProperty("温度超标次数")
    private BigDecimal tempSize;
    @ExcelProperty(value = "最高湿度",index = 10)
    @ApiModelProperty("最高湿度")
    private BigDecimal maxHump;
    @ExcelProperty(value = "最低湿度",index = 11)
    @ApiModelProperty("最低湿度")
    private BigDecimal minHemp;
    @ExcelProperty(value = "平均湿度",index = 12)
    @ApiModelProperty("平均湿度")
    private BigDecimal avgHemp;
    @ExcelProperty(value = "湿度达标率",index = 13)
    @ApiModelProperty("湿度达标率")
    private BigDecimal hempSuccess;
    @ExcelProperty(value = "湿度超标次数",index = 14)
    @ApiModelProperty("湿度超标次数")
    private BigDecimal hempSize;


}
