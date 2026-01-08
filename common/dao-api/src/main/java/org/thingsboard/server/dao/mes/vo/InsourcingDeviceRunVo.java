package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ApiModel("内包机设备运行看板设备对象")
public class InsourcingDeviceRunVo {

    @ApiModelProperty("运行日期")
    private String runDate;

    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("设备名称")
    private String deviceName;
    @ApiModelProperty("运行时间")
    private Long runSeund;
    @ApiModelProperty("最高温度")
    private BigDecimal maxTemp;
    @ApiModelProperty("最低温度")
    private BigDecimal minTemp;
    @ApiModelProperty("平均温度")
    private BigDecimal avgTemp;
    @ApiModelProperty("最高速度")
    private BigDecimal maxSpeed;
    @ApiModelProperty("最低速度")
    private BigDecimal minSpeed;
    @ApiModelProperty("平均速度")
    private BigDecimal avgSpeed;
    @ApiModelProperty("包装件数")
    private BigDecimal pieceQty;
    @ApiModelProperty("超标次数")
    private Integer overSize;
    @ApiModelProperty("温度达标率")
    private BigDecimal tempSuccess;

}
