package com.youchen.push.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_bus_review_notification")
@ApiModel("复核通知详情表")
public class ReviewNotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键ID")
    private Long id;

    @Column(name = "doc_no", nullable = false, length = 100)
    @ApiModelProperty("单据编号")
    private String docNo;

    @Column(name = "product_name", nullable = false, length = 200)
    @ApiModelProperty("产品名称")
    private String productName;

    @Column(name = "checker", length = 100)
    @ApiModelProperty("检查人")
    private String checker;

    @Column(name = "inspection_time")
    @ApiModelProperty("检测时间")
    private LocalDateTime inspectionTime;

    @Column(name = "created_time", nullable = false)
    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }
}
