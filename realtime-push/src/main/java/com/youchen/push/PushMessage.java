package com.youchen.push;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("推送消息")
public class PushMessage {
    @ApiModelProperty("消息类型：order_today(今日任务),order_tomorrow(明日任务),order_cancelled(订单取消),qc_review(品质管控复核),qc_daily(品管日报复核)")
    private String type;
    @ApiModelProperty("消息标题")
    private String title;
    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("基地ID")
    private String baseId;
    @ApiModelProperty("生产线ID")
    private String lineId;
    @ApiModelProperty("班组ID")
    private Integer classId;
    @ApiModelProperty("关联订单通知详情ID")
    private Long orderNotificationId;
    @ApiModelProperty("关联复核通知详情ID")
    private Long reviewNotificationId;
}


