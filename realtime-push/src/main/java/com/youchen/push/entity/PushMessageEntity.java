package com.youchen.push.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_bus_push_message")
@ApiModel("推送消息表")
public class PushMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键ID")
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    @ApiModelProperty("用户ID")
    private String userId;

    @Column(name = "type", nullable = false, length = 50)
    @ApiModelProperty("消息类型：order_today(今日任务),order_tomorrow(明日任务),order_cancelled(订单取消),qc_review(品质管控复核),qc_daily(品管日报复核)")
    private String type;

    @Column(name = "title", nullable = false, length = 200)
    @ApiModelProperty("消息标题")
    private String title;

    @Column(name = "base_id", length = 50)
    @ApiModelProperty("基地ID")
    private String baseId;

    @Column(name = "line_id", length = 50)
    @ApiModelProperty("生产线ID")
    private String lineId;

    @Column(name = "class_id")
    @ApiModelProperty("班组ID")
    private Integer classId;

    @Column(name = "is_read", nullable = false)
    @ApiModelProperty("是否已读")
    private Boolean isRead = false;

    @Column(name = "is_push", nullable = false)
    @ApiModelProperty("是否已推送")
    private Boolean isPush = false;

    @Column(name = "created_time", nullable = false)
    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @Column(name = "order_notification_id")
    @ApiModelProperty("关联订单通知详情ID")
    private Long orderNotificationId;

    @Column(name = "review_notification_id")
    @ApiModelProperty("关联复核通知详情ID")
    private Long reviewNotificationId;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }
}
