package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@ApiModel("物料基础信息")
@Entity
@Data
@Table(name = "t_sync_material")
public class TSyncMaterial {
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

    @Column(name = "group_code")
    @ApiModelProperty("分组代码")
    private String groupCode;

    @Column(name = "material_model")
    @ApiModelProperty("物料规格型号，规格")
    private String materialModel;

    @Column(name = "br")
    @ApiModelProperty("备注")
    private String br;

    @Column(name = "material_status")
    @ApiModelProperty("状态 1：启用 0：禁用")
    private String materialStatus;

    @Column(name = "created_time")
    @ApiModelProperty("创建时间")
    private Date createdTime;

    @Column(name = "created_name")
    @ApiModelProperty("创建人")
    private String createdName;

    @Column(name = "updated_time")
    @ApiModelProperty("修改时间")
    private Date updatedTime;

    @Column(name = "updated_name")
    @ApiModelProperty("修改人")
    private String updatedName;

    @ApiModelProperty("金蝶的物料ID")
    @Column(name = "kd_material_id")
    private Integer kdMaterialId;

    @ApiModelProperty("所属车间-id")
    @Column(name = "kd_material_workshop_id")
    private Integer kdMaterialWorkshopId;

    @ApiModelProperty("所属车间-名称")
    @Column(name = "kd_material_workshop_name")
    private String kdMaterialWorkshopName;

    @ApiModelProperty("所属车间-编码")
    @Column(name = "kd_material_workshop_number")
    private String kdMaterialWorkshopNumber;

    @ApiModelProperty("使用组织id")
    @Column(name = "kd_material_use_org_id")
    private Integer kdMaterialUseOrgId;

    @ApiModelProperty("使用组织编码")
    @Column(name = "kd_material_use_org_number")
    private String kdMaterialUseOrgNumber;

    @ApiModelProperty("使用组织名称")
    @Column(name = "kd_material_use_org_name")
    private String kdMaterialUseOrgName;

    @ApiModelProperty("拉伸膜后重量、单支克重")
    @Column(name = "kd_material_stretch_weight")
    private Float kdMaterialStretchWeight;

    @ApiModelProperty("每件支数")
    @Column(name = "kd_material_each_piece_num")
    private Integer kdMaterialEachPieceNum;

    // 2025-06-23 任务29732 物料id、物料分类、物料大类、材料分类、材料编码、材料名称、规格、单位、保质期（月）、状态
    @ApiModelProperty("NC物料ID")
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
