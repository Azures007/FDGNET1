package org.thingsboard.server.common.data.nc.nc_material;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("物料基础信息")
@Entity
@Data
@Table(name = "t_sync_material")
public class NcTSyncMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("ID")
    private Integer id;

    @Column(name = "material_code")
    @ApiModelProperty("物料代码")
    private String materialCode;

    @Column(name = "material_name")
    @ApiModelProperty("物料名称")
    private String materialName;

    @Column(name = "material_unit")
    @ApiModelProperty("单位")
    private String materialUnit;

    @Column(name = "material_model")
    @ApiModelProperty("规格")
    private String materialModel;

    @Column(name = "material_status")
    @ApiModelProperty("状态 1：启用 0：禁用")
    private String materialStatus;

    // 2025-06-23 任务29732 物料id、物料分类、物料大类、材料分类、材料编码、材料名称、规格、单位、保质期（月）、状态
    @ApiModelProperty("nc物料ID")
    @Column(name = "nc_material_id")
    private String ncMaterialId;

    @ApiModelProperty("物料分类")
    @Column(name = "nc_material_category")
    private String ncMaterialCategory;

    @ApiModelProperty("物料大类")
    @Column(name = "nc_material_main_category")
    private String ncMaterialMainCategory;

    @ApiModelProperty("材料分类")
    @Column(name = "nc_material_classification")
    private String ncMaterialClassification;

    @ApiModelProperty("保质期")
    @Column(name = "nc_material_quality_num")
    private Integer ncMaterialQualityNum;

    @ApiModelProperty("保质期单位，0=年，1=月，2=日")
    @Column(name = "nc_material_quality_unit")
    private String ncMaterialQualityUnit;

    @ApiModelProperty("nc状态")
    @Column(name = "nc_material_status")
    private String ncMaterialStatus;

}
