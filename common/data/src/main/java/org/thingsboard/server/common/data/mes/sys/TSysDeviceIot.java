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
@Table(name = "t_sys_device_iot")
@ApiModel("设备iot时间表")
@NoArgsConstructor
@AllArgsConstructor
public class TSysDeviceIot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("设备id")
    @Column(name = "device_id")
    private Integer deviceId;

    @ApiModelProperty("最后采集时间")
    @Column(name = "iot_time")
    private Date iotTime;
    @ApiModelProperty("设备代码")
    @Column(name = "device_code")
    private String deviceCode;

    @ApiModelProperty("类目类型（类目编码）:数据字典维护，内容包括：原辅料，二级品数量、产后数量、自定义报工")
    @Column(name = "recordType")
    private String recordType;
}
