package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户设备详情表")
@Entity(name = "t_sys_user_devices")
public class TSysUserDevices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @ApiModelProperty("用户id")
    @Column(name = "user_id")
    private String userId;
    @ApiModelProperty("设备key")
    @Column(name = "devices_key")
    private String devicesKey;
    @ApiModelProperty("设备类型 1：指纹")
    @Column(name = "devices_type")
    private String devicesType;
    @ApiModelProperty("人员id")
    @Column(name = "personnel_id")
    private Integer personnelId;
    @ApiModelProperty("是否可用 0：不可用 1：可用")
    @Column(name = "enabled")
    private String enabled;
    @ApiModelProperty("创建时间")
    @Column(name = "created_time")
    private Date createdTime;
    @ApiModelProperty("创建人")
    @Column(name = "created_name")
    private String createdName;
    @ApiModelProperty("修改时间")
    @Column(name = "updated_time")
    private Date updatedTime;
    @ApiModelProperty("修改人")
    @Column(name = "update_name")
    private String updateName;

}
