package org.thingsboard.server.dao.mes.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ApiModel("内包机设备运行看板设备对象")
public class InsourcingDeviceRunVo extends BaseRowModel {

    @ExcelProperty(value = "运行日期", index = 0)
    @ApiModelProperty("运行日期")
    private String runDate;

    @ExcelProperty(value = "设备编码", index = 1)
    @ApiModelProperty("设备编码")
    private String deviceCode;

    @ExcelProperty(value = "设备名称", index = 2)
    @ApiModelProperty("设备名称")
    private String deviceName;

    @ExcelProperty(value = "运行时间", index = 3)
    @ApiModelProperty("运行时间")
    private BigDecimal runSeund;

    @ExcelProperty(value = "最高速度", index = 4)
    @ApiModelProperty("最高速度")
    private BigDecimal maxSpeed;

    @ExcelProperty(value = "最低速度", index = 5)
    @ApiModelProperty("最低速度")
    private BigDecimal minSpeed;

    @ExcelProperty(value = "平均速度", index = 6)
    @ApiModelProperty("平均速度")
    private BigDecimal avgSpeed;

    @ExcelProperty(value = "包装件数", index = 7)
    @ApiModelProperty("包装件数")
    private BigDecimal pieceQty;

    @ExcelIgnore
    @ApiModelProperty("最高温度")
    private BigDecimal maxTemp;

    @ExcelIgnore
    @ApiModelProperty("最低温度")
    private BigDecimal minTemp;

    @ExcelIgnore
    @ApiModelProperty("平均温度")
    private BigDecimal avgTemp;

    @ExcelIgnore
    @ApiModelProperty("超标次数")
    private Integer overSize;

    @ExcelIgnore
    @ApiModelProperty("温度达标率")
    private BigDecimal tempSuccess;

}
