package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@ApiModel("订单同步日志表")
@Data
@Entity
@Table(name = "t_sync_order_log")
public class TSyncOrderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ApiModelProperty("单据编号")
    @Column(name = "bill_no")
    private String billNo;
    @ApiModelProperty("同步时间")
    @Column(name = "sync_time")
    private Date syncTime;
    @ApiModelProperty("同步状态0：成功 1：失败")
    @Column(name = "sync_status")
    private String syncStatus;
    @ApiModelProperty("内容描述")
    @Column(name = "sync_content")
    private String syncContent;
    @ApiModelProperty("日志类型")
    @Column(name = "sync_type")
    private String syncType;
}
