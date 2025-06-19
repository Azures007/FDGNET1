package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2023/2/2 15:56
 * @Description:
 */
@Data
@ApiModel("工序告警")
public class ProcessAlarmVo {

    @ApiModelProperty("明细-批号")
    private String bodyLot;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("废膜重量")
    private float reportFmQty;

    @ApiModelProperty("废膜重量")
    private String reportFmQtyStr;

    @ApiModelProperty("膜使用量")
    private float filmUse;

    @ApiModelProperty("膜使用量")
    private String filmUseStr;

    @ApiModelProperty("膜使用率")
    private String filmPer;

    @ApiModelProperty("isControl为1：业务强控，isControl为0：业务部强控")
    private Integer isController;

    @ApiModelProperty("订单类型：1=正常订单、2=移交订单、3=转移订单")
    private String type;

    @ApiModelProperty("警告用语")
    private String alarmStr;

    private String orderNo;

    private Integer orderId;

    private Integer orderProcessId;

    private Integer toOrderProcessId;

    private Integer interval;



}