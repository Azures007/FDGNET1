package org.thingsboard.server.common.data.mes.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检方案表
 * @date 2025/6/27 11:39:34
 */
@Data
@Entity
@Table(name = "t_sys_quality_plan")
@ApiModel("质检方案表")
public class TSysQualityPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("方案名称")
    @Column(name = "plan_name")
    private String planName;

//    @ApiModelProperty("生产部门ID")
//    @Column(name = "production_department_id")
//    private String productionDepartmentId;
//
//    private String productionDepartmentName;

    @ApiModelProperty("生产线ID")
    @Column(name = "production_line_id")
    private String productionLineId;

    @ApiModelProperty("生产线名称")
    @Column(name = "production_line_name")
    private String productionLineName;

    @ApiModelProperty("备注")
    @Column(name = "remarks")
    private String remarks;

    @ApiModelProperty("启用状态")
    @Column(name = "is_enabled")
    private String isEnabled;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("创建人")
    @Column(name = "create_user")
    private String createUser;

    @ApiModelProperty("修改时间")
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty("修改人")
    @Column(name = "update_user")
    private String updateUser;
}
