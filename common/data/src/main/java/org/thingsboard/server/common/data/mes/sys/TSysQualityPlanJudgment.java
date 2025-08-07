package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检方案判定表
 * @date 2025/6/27 11:41:10
 */
@Data
@Entity
@Table(name = "t_sys_quality_plan_judgment")
@ApiModel("质检方案判定表")
public class TSysQualityPlanJudgment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("质检方案ID")
    @Column(name = "plan_id")
    private Integer planId;

    @ApiModelProperty("字段名称")
    @Column(name = "field_name")
    private String fieldName;

    @ApiModelProperty("是否启用")
    @Column(name = "is_enabled")
    private String isEnabled;

    @ApiModelProperty("字段类型")
    @Column(name = "field_type")
    private String fieldType;

    @ApiModelProperty("下拉框字段")
    @Column(name = "dropdown_fields")
    private String dropdownFields;

    @ApiModelProperty("单位")
    @Column(name = "unit")
    private String unit;

    @ApiModelProperty("是否必填")
    @Column(name = "is_required")
    private String isRequired;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("创建人")
    @Column(name = "create_user")
    private String createUser;

    @ApiModelProperty("修改时间")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty("修改人")
    @Column(name = "update_user")
    private String updateUser;
}
