package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 配方投入设置实体类
 * @author: system
 * @date: 2025-01-10
 * @description: 配方投入设置表实体
 */
@Data
@Entity
@Table(name = "t_sys_recipe_input")
@ApiModel("配方投入设置类")
@NoArgsConstructor
@AllArgsConstructor
public class TSysRecipeInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "input_id")
    @ApiModelProperty("投入设置ID")
    private Integer inputId;

    @ApiModelProperty("配方ID")
    @Column(name = "recipe_id", nullable = false)
    private Integer recipeId;

    @ApiModelProperty("物料名称")
    @Column(name = "material_name", nullable = false, length = 100)
    private String materialName;

    @ApiModelProperty("物料编码")
    @Column(name = "material_code", nullable = false, length = 50)
    private String materialCode;

    @ApiModelProperty("每锅投入标准")
    @Column(name = "standard_input", nullable = false, precision = 10, scale = 6)
    private BigDecimal standardInput;

    @ApiModelProperty("单位编码")
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @ApiModelProperty("投入下限比例%")
    @Column(name = "lower_limit_ratio", precision = 5, scale = 2)
    private BigDecimal lowerLimitRatio = new BigDecimal("100.00");

    @ApiModelProperty("投入上限比例%")
    @Column(name = "upper_limit_ratio", precision = 5, scale = 2)
    private BigDecimal upperLimitRatio = new BigDecimal("110.00");

    @ApiModelProperty("工序名称")
    @Column(name = "process_name", length = 50)
    private String processName;

    @ApiModelProperty("工序编码")
    @Column(name = "process_number", length = 50)
    private String processNumber;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty("半成品")
    @Column(name = "semi_finished_product_name")
    private String semiFinishedProductName;

    @ApiModelProperty("半成品编码")
    @Column(name = "semi_finished_product_code")
    private String semiFinishedProductCode;

    @ApiModelProperty("计划投入比例")
    @Column(name = "plan_input_ratio", precision = 10, scale = 6)
    private BigDecimal planInputRatio;

    @ApiModelProperty("锅数计算基准（复选框）")
    @Column(name = "pot_calculation_basis", length = 1)
    private String potCalculationBasis;

    @ApiModelProperty("显示默认值（复选框）")
    @Column(name = "display_default_value", length = 1)
    private String displayDefaultValue;

    @ApiModelProperty("允许偏差")
    @Column(name = "allowable_deviation", nullable = false, precision = 10, scale = 6)
    private BigDecimal allowableDeviation;

    // 多对一关系：配方（为避免详情序列化时出现循环/冗余，忽略该字段）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private TSysRecipe recipe;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}