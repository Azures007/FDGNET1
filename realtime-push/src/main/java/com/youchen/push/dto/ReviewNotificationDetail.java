package com.youchen.push.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("复核通知详情")
public class ReviewNotificationDetail {
    @ApiModelProperty("单据编号")
    private String docNo;
    
    @ApiModelProperty("产品名称")
    private String productName;
    
    @ApiModelProperty("检查人")
    private String checker;
    
    @ApiModelProperty("检测时间")
    private String inspectionTime;
}
