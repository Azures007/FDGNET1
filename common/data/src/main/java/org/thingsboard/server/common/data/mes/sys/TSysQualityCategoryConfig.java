package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检类目配置表
 * @date 2025/6/27 10:45:28
 */

@Data
@Entity
@Table(name = "t_sys_quality_category_config")
@ApiModel("质检类目配置类")
public class TSysQualityCategoryConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("质检类目ID")
    @Column(name = "category_id")
    private Integer categoryId;

    @ApiModelProperty("物料ID")
    @Column(name = "material_id")
    private Integer materialId;

    @ApiModelProperty("品名")
    @Column(name = "material_Name")
    private String materialName;

    @ApiModelProperty("字段名称")
    @Column(name = "field_name")
    private String fieldName;

    @ApiModelProperty("是否启用")
    @Column(name = "is_enabled")
    private String isEnabled;

    @ApiModelProperty("字段类型")
    @Column(name = "field_type")
    private String fieldType;

    @ApiModelProperty("参数范围")
    @Column(name = "parameter_range")
    private String parameterRange;

    @ApiModelProperty("下拉框字段,多个字段用英文逗号隔开")
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

//    public QualityCategoryConfigEntity() {
//        super();
//    }
//
//    public QualityCategoryConfigEntity(QualityCategoryConfig config) {
//        if (config.getId() != null) {
//            this.setId(config.getId().getId());
//        }
//        this.tenantId = config.getTenantId() != null ? config.getTenantId().getId() : null;
//        this.categoryId = config.getCategoryId() != null ? config.getCategoryId().getId() : null;
//        this.materialId = config.getMaterialId();
//        this.fieldName = config.getFieldName();
//        this.isEnabled = config.getIsEnabled();
//        this.fieldType = config.getFieldType();
//        this.parameterRange = config.getParameterRange();
//        this.dropdownFields = config.getDropdownFields();
//        this.unit = config.getUnit();
//        this.isRequired = config.getIsRequired();
//        this.createTime = config.getCreateTime();
//        this.createUser = config.getCreateUser();
//        this.updateTime = config.getUpdateTime();
//        this.updateUser = config.getUpdateUser();
//    }
//
//    @Override
//    public QualityCategoryConfig toData() {
//        QualityCategoryConfig config = new QualityCategoryConfig();
//        config.setId(getId() == null ? null : toUUID(getId()));
//        config.setTenantId(tenantId == null ? null : new TenantId(toUUID(tenantId)));
//        config.setCategoryId(categoryId == null ? null : new UUID(categoryId));
//        config.setMaterialId(materialId);
//        config.setFieldName(fieldName);
//        config.setIsEnabled(isEnabled);
//        config.setFieldType(fieldType);
//        config.setParameterRange(parameterRange);
//        config.setDropdownFields(dropdownFields);
//        config.setUnit(unit);
//        config.setIsRequired(isRequired);
//        config.setCreateTime(createTime);
//        config.setCreateUser(createUser);
//        config.setUpdateTime(updateTime);
//        config.setUpdateUser(updateUser);
//        return config;
//    }


}
