package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ApiModel("烤炉设备运行看板设备对象")
public class OvenDeviceRunVo {

    @ApiModelProperty("运行日期")
    private String runDate;

    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("设备名称")
    private String deviceName;
    @ApiModelProperty("运行时间")
    private Long runSeund;
    @ApiModelProperty("温度1区最高温")
    private BigDecimal maxOneTemp;
    @ApiModelProperty("温度1区最低温")
    private BigDecimal minOneTemp;
    @ApiModelProperty("温度2区最高温")
    private BigDecimal maxTwoTemp;
    @ApiModelProperty("温度2区最低温")
    private BigDecimal minTwoTemp;
    @ApiModelProperty("温度3区最高温")
    private BigDecimal maxThreeTemp;
    @ApiModelProperty("温度3区最低温")
    private BigDecimal minThreeTemp;
    @ApiModelProperty("温度4区最高温")
    private BigDecimal maxFourTemp;
    @ApiModelProperty("温度4区最低温")
    private BigDecimal minFourTemp;
    @ApiModelProperty("总平均温度")
    private BigDecimal avgTemp;
    @ApiModelProperty("超标次数")
    private Integer overSize;
    @ApiModelProperty("达标率")
    private BigDecimal tempSuccess;
    @ApiModelProperty("平均速度")
    private BigDecimal avgSpeed;
    @ApiModelProperty("平均热风频率")
    private BigDecimal avgHotWind;
    @ApiModelProperty("故障次数")
    private Integer errorSize;
    @ApiModelProperty("包装件数")
    private BigDecimal pieceQty;

}
