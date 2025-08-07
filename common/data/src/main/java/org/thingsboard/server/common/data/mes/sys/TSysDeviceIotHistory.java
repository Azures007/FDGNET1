package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_sys_device_iot_history")
@ApiModel("设备iot时间历史记录表")
@NoArgsConstructor
@AllArgsConstructor
public class TSysDeviceIotHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("订单号")
    @Column(name = "order_no")
    private String orderNo;

    @ApiModelProperty("工序执行id")
    @Column(name = "order_process_id")
    private Integer orderProcessId;

    @ApiModelProperty("工序编码")
    @Column(name = "process_code")
    private String processCode;

    @ApiModelProperty("设备id")
    @Column(name = "device_id")
    private Integer deviceId;

    @ApiModelProperty("设备编码")
    @Column(name = "device_code")
    private String deviceCode;

    @ApiModelProperty("创建时间")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("类目")
    @Column(name = "record_type")
    private String recordType="3";
}
