package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ApiModel("温湿仪设备运行看板设备对象")
public class TanSensorDeviceRunVo {

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
    @ApiModelProperty("温度达标率")
    private BigDecimal tempSuccess;
    @ApiModelProperty("温度超标次数")
    private BigDecimal tempSize;
    @ApiModelProperty("最高湿度")
    private BigDecimal maxHump;
    @ApiModelProperty("最低湿度")
    private BigDecimal minHemp;
    @ApiModelProperty("平均湿度")
    private BigDecimal avgHemp;
    @ApiModelProperty("湿度达标率")
    private BigDecimal hempSuccess;
    @ApiModelProperty("湿度超标次数")
    private BigDecimal hempSize;


}
