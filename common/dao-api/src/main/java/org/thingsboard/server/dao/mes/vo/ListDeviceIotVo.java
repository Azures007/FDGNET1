package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.dao.constant.GlobalConstant;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
@ApiModel("大屏看板设备信息")
public class ListDeviceIotVo {
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("设备名称")
    private String deviceName;
    @ApiModelProperty("所属工厂")
    private String deviceFactory;
    @ApiModelProperty("设备状态 0:关机 1：开机")
    private BigDecimal deviceStatus;
    @ApiModelProperty("开机时长（分钟）")
    private BigDecimal deviceRunTimes;
    @ApiModelProperty("热门频率")
    private BigDecimal deviceHz;
    @ApiModelProperty("速度")
    private BigDecimal deviceSd;
    @ApiModelProperty("上一次开机时间")
    private String frontRunDate;

    @ApiModelProperty("温度1区上温")
    private BigDecimal maxOneTemp;
    @ApiModelProperty("温度1区下温")
    private BigDecimal minOneTemp;
    @ApiModelProperty("温度2区上温")
    private BigDecimal maxTwoTemp;
    @ApiModelProperty("温度2区下温")
    private BigDecimal minTwoTemp;
    @ApiModelProperty("温度3区上温")
    private BigDecimal maxThreeTemp;
    @ApiModelProperty("温度3区下温")
    private BigDecimal minThreeTemp;
    @ApiModelProperty("温度4区上温")
    private BigDecimal maxFourTemp;
    @ApiModelProperty("温度4区下温")
    private BigDecimal minFourTemp;

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

}
