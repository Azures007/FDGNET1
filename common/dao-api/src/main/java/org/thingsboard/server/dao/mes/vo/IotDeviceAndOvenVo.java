package org.thingsboard.server.dao.mes.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString
@ApiModel("iot设备烤炉数据")
public class IotDeviceAndOvenVo extends BaseRowModel {

    @ExcelProperty(value = "运行日期", index = 0)
    @ApiModelProperty("时间日期(日期部分)")
    private String byDateDates;
    @ExcelProperty(value = "采集时间", index = 1)
    @ApiModelProperty("时间日期(时间部分)")
    private String byDateTimes;
    @ExcelIgnore
    @ApiModelProperty("时间日期")
    private String byDate;
    @ExcelIgnore
    @ApiModelProperty("时间戳")
    private Long byTs;
    @ExcelProperty(value = "采集时间", index = 2)
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ExcelProperty(value = "设备名称", index =3)
    @ApiModelProperty("设备名称")
    private String deviceName;
    @ExcelProperty(value = "温度1区上温", index = 4)
    @ApiModelProperty("温度1区上温")
    private BigDecimal upOneTemp;
    @ExcelProperty(value = "温度1区下温", index = 5)
    @ApiModelProperty("温度1区下温")
    private BigDecimal downOneTemp;
    @ExcelProperty(value = "温度2区上温", index = 6)
    @ApiModelProperty("温度2区上温")
    private BigDecimal upTwoTemp;
    @ExcelProperty(value = "温度2区下温", index = 7)
    @ApiModelProperty("温度2区下温")
    private BigDecimal downTwoTemp;
    @ExcelProperty(value = "温度3区上温", index = 8)
    @ApiModelProperty("温度3区上温")
    private BigDecimal upThreeTemp;
    @ExcelProperty(value = "温度3区下温", index = 9)
    @ApiModelProperty("温度3区下温")
    private BigDecimal downThreeTemp;
    @ExcelProperty(value = "温度4区上温", index = 10)
    @ApiModelProperty("温度4区上温")
    private BigDecimal upFourTemp;
    @ExcelProperty(value = "温度4区下温", index = 11)
    @ApiModelProperty("温度4区下温")
    private BigDecimal downFourTemp;
    @ExcelProperty(value = "循环风机1", index = 12)
    @ApiModelProperty("循环风机1")
    private BigDecimal oneFan;
    @ExcelProperty(value = "循环风机2", index = 13)
    @ApiModelProperty("循环风机2")
    private BigDecimal twoFan;
    @ExcelProperty(value = "鼓风机", index = 14)
    @ApiModelProperty("鼓风机")
    private BigDecimal airFan;
    @ExcelProperty(value = "速度", index = 15)
    @ApiModelProperty("速度")
    private BigDecimal bySellp;
    @ExcelIgnore
    @ApiModelProperty("是否故障0：否 1：是")
    private String byGz;
    @ExcelProperty(value = "是否故障", index = 16)
    @ApiModelProperty("是否故障")
    private String byGzBr;
    @ExcelIgnore
    @ApiModelProperty("温度1区上温最大值")
    private String overOneUpMaxTemp;
    @ExcelIgnore
    @ApiModelProperty("温度1区上温最小值")
    private String overOneUpMinTemp;
    @ExcelIgnore
    @ApiModelProperty("温度1区下温最大值")
    private String overOneDownMaxTemp;
    @ExcelIgnore
    @ApiModelProperty("温度1区下温最小值")
    private String overOneDownMinTemp;
    @ExcelIgnore
    @ApiModelProperty("温度2区上温最大值")
    private String overTwoUpMaxTemp;
    @ExcelIgnore
    @ApiModelProperty("温度2区上温最小值")
    private String overTwoUpMinTemp;
    @ExcelIgnore
    @ApiModelProperty("温度2区下温最大值")
    private String overTwoDownMaxTemp;
    @ExcelIgnore
    @ApiModelProperty("温度3区下温最小值")
    private String overTwoDownMinTemp;
    @ExcelIgnore
    @ApiModelProperty("温度3区上温最大值")
    private String overThreeUpMaxTemp;
    @ExcelIgnore
    @ApiModelProperty("温度3区上温最小值")
    private String overThreeUpMinTemp;
    @ExcelIgnore
    @ApiModelProperty("温度3区下温最大值")
    private String overThreeDownMaxTemp;
    @ExcelIgnore
    @ApiModelProperty("温度3区下温最小值")
    private String overThreeDownMinTemp;
    @ExcelIgnore
    @ApiModelProperty("温度4区上温最大值")
    private String overFourUpMaxTemp;
    @ExcelIgnore
    @ApiModelProperty("温度4区上温最小值")
    private String overFourUpMinTemp;
    @ExcelIgnore
    @ApiModelProperty("温度4区下温最大值")
    private String overFourDownMaxTemp;
    @ExcelIgnore
    @ApiModelProperty("温度4区下温最小值")
    private String overFourDownMinTemp;

}
