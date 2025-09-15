package com.youchen.push.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("消息项")
public class MessageItem {
    @ApiModelProperty("消息ID")
    private Long id;
    
    @ApiModelProperty("消息类型")
    private String type;
    
    @ApiModelProperty("消息标题")
    private String title;
    
    @ApiModelProperty("消息内容")
    private String content;
    

    
    @ApiModelProperty("时间戳")
    private long ts;
    
    @ApiModelProperty("是否已读")
    private boolean read;

    @ApiModelProperty("是否已推送")
    private boolean push;
    
    @ApiModelProperty("订单通知详情")
    private OrderNotificationDetail orderDetail;
    
    @ApiModelProperty("复核通知详情")
    private ReviewNotificationDetail reviewDetail;
}
