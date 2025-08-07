package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("产品标识单打印")
@AllArgsConstructor
@NoArgsConstructor
public class ListPrintRecordVo {
    @ApiModelProperty("单据编号")
    private String billNo;
    @ApiModelProperty("类型")
    private String myType;
    @ApiModelProperty("产品名称")
    private String bodyMaterialName;
    @ApiModelProperty("工序")
    private String processName;
    @ApiModelProperty("数量")
    private String recordQty;
    @ApiModelProperty("单位")
    private String recordUnit;
    @ApiModelProperty("单位名称")
    private String recordUnitName;
    @ApiModelProperty("料单批次")
    private String bodyLot;
    @ApiModelProperty("生产日期")
    private String sysTime;
    @ApiModelProperty("班别名称")
    private String className;
    @ApiModelProperty("责任人")
    private String firstName;
    @ApiModelProperty("完工时间")
    private String finishTime;
    @ApiModelProperty("接单时间")
    private String receiveTime;
    @ApiModelProperty("标题")
    private String title="产品标识单";
    @ApiModelProperty("报工时间")
    private String bgTime;



}
