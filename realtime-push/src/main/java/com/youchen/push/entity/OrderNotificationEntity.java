package com.youchen.push.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_bus_order_notification")
@ApiModel("订单通知详情表")
public class OrderNotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键ID")
    private Long id;

    @Column(name = "order_id", nullable = false)
    @ApiModelProperty("订单ID")
    private Integer orderId;

    @Column(name = "order_no", nullable = false, length = 100)
    @ApiModelProperty("订单号")
    private String orderNo;

    @Column(name = "order_process_id")
    @ApiModelProperty("订单执行表ID")
    private Integer orderProcessId;

    @Column(name = "product_name", nullable = false, length = 200)
    @ApiModelProperty("产品名称")
    private String productName;

    @Column(name = "estimated_output", length = 50)
    @ApiModelProperty("预计产量")
    private String estimatedOutput;

    @Column(name = "unit", length = 20)
    @ApiModelProperty("单位")
    private String unit;

    @Column(name = "specification", length = 100)
    @ApiModelProperty("规格")
    private String specification;

    @Column(name = "planned_start_time")
    @ApiModelProperty("计划开工时间")
    private LocalDateTime plannedStartTime;

    @Column(name = "planned_completion_time")
    @ApiModelProperty("计划完工时间")
    private LocalDateTime plannedCompletionTime;

    @Column(name = "created_time", nullable = false)
    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }
}
