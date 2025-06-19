package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_sys_personnel_info")
@ApiModel("人员类")
public class TSysPersonnelInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personnel_id")
    private Integer personnelId;
    @ApiModelProperty("姓名")
    @Column(name = "name")
    private String name;
    @ApiModelProperty("性别")
    @Column(name = "sex")
    private String sex;
    @ApiModelProperty("手机号")
    @Column(name = "phone")
    private String phone;

    @ApiModelProperty("岗位")
    @Column(name = "station")
    private String station;
    @ApiModelProperty("班组")
    @Column(name = "class_name")
    private String className;

    @ApiModelProperty("绑定的账号用户ID")
    @Column(name = "user_id")
    private String userId;
    @ApiModelProperty("绑定用户的账号")
    @Column(name = "user_email")
    private String userEmail;

    @ApiModelProperty("是否可用 0：禁用 1：启用")
    @Column(name = "enabled_st")
    private String enabledSt;
    @ApiModelProperty("创建人")
    @Column(name = "crt_user")
    private String crtUser;
    @ApiModelProperty("创建日期")
    @Column(name = "crt_time")
    private Date crtTime;
    @ApiModelProperty("修改日期")
    @Column(name = "update_time")
    private Date updateTime;
    @ApiModelProperty("修改人")
    @Column(name = "update_user")
    private String updateUser;

    @ApiModelProperty("岗位名称")
    @Transient
    private String stationName;

    @ApiModelProperty("是否采集过指纹 0：未采集 1：已采集")
    @Transient
    private String byDevicesType1;
}
