package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@ApiModel("NC同步日志表")
@Data
@Entity
@Table(name = "t_bus_nc_sync_log")
public class NcSyncLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键ID")
    private Integer id;

    @ApiModelProperty("同步类型（如：库存同步、订单同步等）")
    @Column(name = "sync_type", nullable = false, length = 50)
    private String syncType;

    @ApiModelProperty("同步时间")
    @Column(name = "sync_time", nullable = false)
    private Date syncTime;

    @ApiModelProperty("同步状态 0：成功 1：失败")
    @Column(name = "sync_status", nullable = false, length = 1)
    private String syncStatus;

    @ApiModelProperty("同步内容描述")
    @Column(name = "sync_content", columnDefinition = "TEXT")
    private String syncContent;

    @ApiModelProperty("原始JSON入参")
    @Column(name = "request_json", columnDefinition = "TEXT")
    private String requestJson;

    @ApiModelProperty("同步数据数量")
    @Column(name = "data_count")
    private Integer dataCount;

    @ApiModelProperty("同步耗时（毫秒）")
    @Column(name = "duration_ms")
    private Long durationMs;

    @ApiModelProperty("错误信息")
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}

