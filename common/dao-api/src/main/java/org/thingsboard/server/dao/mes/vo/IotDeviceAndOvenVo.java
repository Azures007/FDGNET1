package org.thingsboard.server.dao.mes.vo;

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
public class IotDeviceAndOvenVo {

    @ApiModelProperty("时间日期")
    private String byDate;
    @ApiModelProperty("时间戳")
    private Long byTs;
    @ApiModelProperty("温度1区上温")
    private BigDecimal upOneTemp;
    @ApiModelProperty("温度1区下温")
    private BigDecimal downOneTemp;
    @ApiModelProperty("温度2区上温")
    private BigDecimal upTwoTemp;
    @ApiModelProperty("温度2区下温")
    private BigDecimal downTwoTemp;
    @ApiModelProperty("温度3区上温")
    private BigDecimal upThreeTemp;
    @ApiModelProperty("温度3区下温")
    private BigDecimal downThreeTemp;
    @ApiModelProperty("温度4区上温")
    private BigDecimal upFourTemp;
    @ApiModelProperty("温度4区下温")
    private BigDecimal downFourTemp;
    @ApiModelProperty("温度1区上温最大值")
    private String overOneUpMaxTemp;
    @ApiModelProperty("温度1区上温最小值")
    private String overOneUpMinTemp;
    @ApiModelProperty("温度1区下温最大值")
    private String overOneDownMaxTemp;
    @ApiModelProperty("温度1区下温最小值")
    private String overOneDownMinTemp;
    @ApiModelProperty("温度2区上温最大值")
    private String overTwoUpMaxTemp;
    @ApiModelProperty("温度2区上温最小值")
    private String overTwoUpMinTemp;
    @ApiModelProperty("温度2区下温最大值")
    private String overTwoDownMaxTemp;
    @ApiModelProperty("温度3区下温最小值")
    private String overTwoDownMinTemp;
    @ApiModelProperty("温度3区上温最大值")
    private String overThreeUpMaxTemp;
    @ApiModelProperty("温度3区上温最小值")
    private String overThreeUpMinTemp;
    @ApiModelProperty("温度3区下温最大值")
    private String overThreeDownMaxTemp;
    @ApiModelProperty("温度3区下温最小值")
    private String overThreeDownMinTemp;
    @ApiModelProperty("温度4区上温最大值")
    private String overFourUpMaxTemp;
    @ApiModelProperty("温度4区上温最小值")
    private String overFourUpMinTemp;
    @ApiModelProperty("温度4区下温最大值")
    private String overFourDownMaxTemp;
    @ApiModelProperty("温度4区下温最小值")
    private String overFourDownMinTemp;
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("设备名称")
    private String deviceName;
    @ApiModelProperty("循环风机1")
    private BigDecimal oneFan;
    @ApiModelProperty("循环风机2")
    private BigDecimal twoFan;
    @ApiModelProperty("鼓风机")
    private BigDecimal airFan;
    @ApiModelProperty("速度")
    private BigDecimal bySellp;
    @ApiModelProperty("是否故障0：否 1：是")
    private String byGz;
}
