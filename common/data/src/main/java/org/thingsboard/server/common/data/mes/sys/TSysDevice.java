package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/4/18 11:00
 * @Description:
 */
@Entity
@ApiModel("设备")
@Data
@Table(name = "t_sys_device")
public class TSysDevice {
    @Id
    @Column(name = "device_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deviceId;
    @ApiModelProperty("设备名称")
    @Column(name = "device_name")
    private String deviceName;
    @ApiModelProperty("设备编号")
    @Column(name = "device_number")
    private String deviceNumber;
    @ApiModelProperty("车间")
    @Column(name = "workshop")
    private String workshop;
    @ApiModelProperty("车间")
    @Column(name = "workshop_no")
    private String workshopNo;
    @ApiModelProperty("状态")
    @Column(name = "status")
    private Integer status;
    @ApiModelProperty("是否启用 0：禁用 1：启用")
    private String enabled;
    @ApiModelProperty("创建人")
    @Column(name = "created_user")
    private String createdUser;
    @ApiModelProperty("创建日期")
    @Column(name = "created_time")
    private Date createdTime;
    @ApiModelProperty("修改日期")
    @Column(name = "updated_time")
    private Date updatedTime;
    @ApiModelProperty("修改人")
    @Column(name = "updated_user")
    private String updatedUser;
    @ApiModelProperty("启用人")
    @Column(name = "enable_user")
    private String enableUser;
    @ApiModelProperty("启用时间")
    @Column(name = "enable_time")
    private Date enableTime;
    @ApiModelProperty("禁用人")
    @Column(name = "disabled_user")
    private String disabledUser;
    @ApiModelProperty("禁用时间")
    @Column(name = "disabled_time")
    private Date disabledTime;
    @ApiModelProperty("备注")
    @Column(name = "note")
    private String note;

    @ApiModelProperty("所属工序id")
    @Column(name = "belong_process_id")
    private Integer belongProcessId;

    // 列表的生产组织改为基地，默认为当前登录用户的基地，隐藏生产车间 hhh 2025-07-23
    // 废弃字段：生产组织id、生产车间id；新增基地id
    @ApiModelProperty("生产组织id")
    @Column(name = "kd_org_id")
    private Integer kdOrgId;

    @ApiModelProperty("生产车间id")
    @Column(name = "kd_dept_id")
    private Integer kdDeptId;

    @ApiModelProperty("基地Id")
    @Column(name = "pk_org")
    private String pkOrg;
}
