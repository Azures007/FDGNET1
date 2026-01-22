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
@ApiModel("烤炉设备运行看板设备对象")
public class OvenDeviceRunVo extends BaseRowModel {
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
    private BigDecimal runSeund;
    @ExcelProperty(value = "温度1区最高温",index = 5)
    @ApiModelProperty("温度1区最高温")
    private BigDecimal maxOneTemp;
    @ExcelProperty(value = "温度1区最低温",index = 6)
    @ApiModelProperty("温度1区最低温")
    private BigDecimal minOneTemp;
    @ExcelProperty(value = "温度2区最高温",index = 7)
    @ApiModelProperty("温度2区最高温")
    private BigDecimal maxTwoTemp;
    @ExcelProperty(value = "温度2区最低温",index = 8)
    @ApiModelProperty("温度2区最低温")
    private BigDecimal minTwoTemp;
    @ExcelProperty(value = "温度3区最高温",index = 9)
    @ApiModelProperty("温度3区最高温")
    private BigDecimal maxThreeTemp;
    @ExcelProperty(value = "温度3区最低温",index = 10)
    @ApiModelProperty("温度3区最低温")
    private BigDecimal minThreeTemp;
    @ExcelProperty(value = "温度4区最高温",index = 11)
    @ApiModelProperty("温度4区最高温")
    private BigDecimal maxFourTemp;
    @ExcelProperty(value = "温度4区最低温",index = 12)
    @ApiModelProperty("温度4区最低温")
    private BigDecimal minFourTemp;
    @ApiModelProperty("总平均温度")
    private BigDecimal avgTemp;
    @ExcelProperty(value = "总平均温度",index = 13)
    @ApiModelProperty("总平均温度(导出专用)")
    private String avgTempExport;
    @ExcelProperty(value = "超标次数",index = 14)
    @ApiModelProperty("超标次数")
    private Integer overSize;
    @ApiModelProperty("达标率")
    private BigDecimal tempSuccess;
    @ExcelProperty(value = "达标率",index = 15)
    @ApiModelProperty("达标率（导出专用）")
    private String tempSuccessExport;
    @ApiModelProperty("平均速度")
    private BigDecimal avgSpeed;
    @ExcelProperty(value = "平均速度",index = 16)
    @ApiModelProperty("平均速度(导出专用)")
    private String avgSpeedExport;
    @ApiModelProperty("平均热风频率")
    private BigDecimal avgHotWind;
    @ExcelProperty(value = "平均热风频率",index = 17)
    @ApiModelProperty("平均热风频率（导出专用）")
    private String  avgHotWindExport;
    @ExcelProperty(value = "故障次数",index = 18)
    @ApiModelProperty("故障次数")
    private Integer errorSize;

}
