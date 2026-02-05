package org.thingsboard.server.dao.mes.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
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

