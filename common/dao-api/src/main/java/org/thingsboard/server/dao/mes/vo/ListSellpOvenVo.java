package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
@ApiModel("烤炉折线图响应")
public class ListSellpOvenVo {

    @ApiModelProperty("设备名称")
    private String deviceName;


    @ApiModelProperty("设备编码")
    private String deviceCode;

    @ApiModelProperty("一区上半区温度列表")
    private List<BoardDataDevice> oneUpTempBoard;

    @ApiModelProperty("一区下半区温度列表")
    private List<BoardDataDevice> oneDownTempBoard;

    @ApiModelProperty("二区上半区温度列表")
    private List<BoardDataDevice> twoUpTempBoard;

    @ApiModelProperty("二区下半区温度列表")
    private List<BoardDataDevice> twoDownTempBoard;

    @ApiModelProperty("三区上半区温度列表")
    private List<BoardDataDevice> threeUpTempBoard;

    @ApiModelProperty("三区下半区温度列表")
    private List<BoardDataDevice> threeDownTempBoard;

    @ApiModelProperty("四区上半区温度列表")
    private List<BoardDataDevice> fourUpTempBoard;

    @ApiModelProperty("四区下半区温度列表")
    private List<BoardDataDevice> fourDownTempBoard;

}

