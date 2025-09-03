package com.youchen.push.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单通知详情")
public class OrderNotificationDetail {
    @ApiModelProperty("订单ID")
    private Integer orderId;
    
    @ApiModelProperty("订单号")
    private String orderNo;
    
    @ApiModelProperty("订单执行表ID")
    private Integer orderProcessId;
    
    @ApiModelProperty("产品名称")
    private String productName;
    
    @ApiModelProperty("预计产量")
    private String estimatedOutput;
    
    @ApiModelProperty("规格")
    private String specification;
    
    @ApiModelProperty("计划开工时间")
    private String plannedStartTime;
    
    @ApiModelProperty("计划完工时间")
    private String plannedCompletionTime;
}
