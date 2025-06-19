package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "mid_material")
@Data
@ApiModel("物料表")
@Entity
public class MidMaterial {
    @ApiModelProperty("金蝶的物料ID")
    @Column(name = "kd_material_id")
    private Integer kdMaterialId;
    @ApiModelProperty("编号")
    @Column(name = "kd_material_number")
    private String kdMaterialNumber;
    @ApiModelProperty("名称")
    @Column(name = "kd_material_name")
    private String kdMaterialName;
    @ApiModelProperty("规格")
    @Column(name = "kd_material_spec")
    private String kdMaterialSpec;
    @ApiModelProperty("金蝶物料变动时间")
    @Column(name = "kd_material_modify_date_time")
    private Date kdMaterialModifyDateTime;

    @Column(name = "gmt_create")
    private Date gmtCreate;
    @ApiModelProperty("修改时间")
    @Column(name = "gmt_modified")
    private Date gmtModified;

    @Column(name = "is_delete")
    private Integer isDelete;
    @ApiModelProperty("金蝶物料创建时间")
    @Column(name = "kd_material_modify_create_time")
    private Date kdMaterialModifyCreateTime;
    @ApiModelProperty("膜厚度")
    @Column(name = "kd_material_membrane_thickness")
    private Float kdMaterialMembraneThickness;
    @ApiModelProperty("膜宽度")
    @Column(name = "kd_material_membrane_width")
    private Float kdMaterialMembraneWidth;
    @ApiModelProperty("膜密度")
    @Column(name = "kd_material_membrane_density")
    private Float kdMaterialMembraneDensity;
    @ApiModelProperty("每支克重")
    @Column(name = "kd_material_per_weight")
    private Float kdMaterialPerWeight;
    @ApiModelProperty("一捆膜净重")
    @Column(name = "kd_material_net_weight")
    private Float kdMaterialNetWeight;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    @Column(name = "mid_material_id")
    private Integer midMaterialId;
    @ApiModelProperty("数据状态 C已审核")
    @Column(name = "kd_material_doc_status")
    private String kdMaterialDocStatus;
    @ApiModelProperty(" 禁用状态 A 正常")
    @Column(name = "kd_material_disable_status")
    private String kdMaterialDisableStatus;
    @ApiModelProperty("单位id")
    @Column(name = "kd_material_unit_id")
    private Integer kdMaterialUnitId;
    @ApiModelProperty("单位名称")
    @Column(name = "kd_material_unit_name")
    private String kdMaterialUnitName;
    @ApiModelProperty("单位编码")
    @Column(name = "kd_material_unit_number")
    private String kdMaterialUnitNumber;

    @ApiModelProperty("生产组织-id")
    @Column(name = "kd_material_use_org_id")
    private Integer kdMaterialUseOrgId;
    @ApiModelProperty("生产组织-编号")
    @Column(name = "kd_material_use_org_number")
    private String kdMaterialUseOrgNumber;
    @ApiModelProperty("生产组织-名称")
    @Column(name = "kd_material_use_org_name")
    private String kdMaterialUseOrgName;

    @ApiModelProperty("切断后重量")
    @Column(name = "kd_material_cut_weight")
    private Float kdMaterialCutWeight;
    @ApiModelProperty("扒皮后重量")
    @Column(name = "kd_material_peel_weight")
    private Float kdMaterialPeelWeight;
    @ApiModelProperty("拌料后重量")
    @Column(name = "kd_material_mixture_weight")
    private Float kdMaterialMixtureWeight;
    @ApiModelProperty("拉伸膜后重量、单支克重")
    @Column(name = "kd_material_stretch_weight")
    private Float kdMaterialStretchWeight;
    @ApiModelProperty("是否去皮 1=去皮 0=不去皮")
    @Column(name = "kd_material_is_peel")
    private Integer kdMaterialIsPeel;

    @ApiModelProperty("生产车间-id")
    @Column(name = "kd_material_workshop_id")
    private Integer kdMaterialWorkshopId;
    @ApiModelProperty("生产车间-编号")
    @Column(name = "kd_material_workshop_number")
    private String kdMaterialWorkshopNumber;
    @ApiModelProperty("生产车间-名称")
    @Column(name = "kd_material_workshop_name")
    private String kdMaterialWorkshopName;
    @ApiModelProperty("物料属性")
    @Column(name = "kd_material_props_id")
    private String kdMaterialPropsId;

    @ApiModelProperty("每件支数")
    @Column(name = "kd_material_each_piece_num")
    private Integer kdMaterialEachPieceNum;
}
