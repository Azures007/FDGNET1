package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
@ApiModel("温湿仪折线图响应")
public class ListSellpTANSensorVo {

    @ApiModelProperty("设备名称")
    private String deviceName;


    @ApiModelProperty("设备编码")
    private String deviceCode;

    @ApiModelProperty("温度列表")
    private List<BoardDataDevice> tempBoard;

    @ApiModelProperty("一区下半区温度列表")
    private List<BoardDataDevice> hempBoard;

    @ApiModelProperty("设置的最大温度")
    private BigDecimal inMaxTemp;

    @ApiModelProperty("设置的最小温度")
    private BigDecimal inMinTemp;

    @ApiModelProperty("设置的最大湿度")
    private BigDecimal inMaxHemp;

    @ApiModelProperty("设置的最小湿度")
    private BigDecimal inMinHemp;

}
