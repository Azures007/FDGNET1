package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterialBom;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/5/25 15:40
 * @Description:
 */
@ApiModel("物料信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TSyncMaterialVo {

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("物料代码")
    private String materialCode;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("单位")
    private String materialUnit;

    @ApiModelProperty("分组代码")
    private String groupCode;

    @ApiModelProperty("物料规格型号")
    private String materialModel;
    @Column(name = "br")
    @ApiModelProperty("备注")
    private String br;

    @ApiModelProperty("状态 1：启用 0：禁用")
    private String materialStatus;

    @ApiModelProperty("创建时间")
    private Date createdTime;

    @ApiModelProperty("创建人")
    private String createdName;

    @ApiModelProperty("修改时间")
    private Date updatedTime;

    @ApiModelProperty("修改人")
    private String updatedName;

    @ApiModelProperty("金蝶的物料ID")
    private Integer kdMaterialId;

    @ApiModelProperty("所属车间-id")
    private Integer kdMaterialWorkshopId;

    @ApiModelProperty("所属车间-名称")
    private String kdMaterialWorkshopName;

    @ApiModelProperty("所属车间-编码")
    private String kdMaterialWorkshopNumber;

    @ApiModelProperty("使用组织id")
    private Integer kdMaterialUseOrgId;

    @ApiModelProperty("使用组织编码")
    private String kdMaterialUseOrgNumber;

    @ApiModelProperty("使用组织名称")
    private String kdMaterialUseOrgName;

    @ApiModelProperty("拉伸膜后重量、单支克重")
    private Float kdMaterialStretchWeight;

    @ApiModelProperty("每件支数")
    private Integer kdMaterialEachPieceNum;

    @ApiModelProperty("膜厚度")
    private Float kdMaterialMembraneThickness;
    @ApiModelProperty("膜宽度")
    private Float kdMaterialMembraneWidth;
    @ApiModelProperty("膜密度")
    private Float kdMaterialMembraneDensity;

    @ApiModelProperty("NC物料ID")
    private String ncMaterialId;
    @ApiModelProperty("物料分类")
    private String ncMaterialCategory;

    @ApiModelProperty("物料大类")
    private String ncMaterialMainCategory;

    @ApiModelProperty("材料分类")
    private String ncMaterialClassification;

    @ApiModelProperty("保质期")
    private Integer ncMaterialQualityNum;

    @ApiModelProperty("保质期单位，0=年，1=月，2=日")
    private String ncMaterialQualityUnit;

    @ApiModelProperty("nc状态")
    private String ncMaterialStatus;

    @ApiModelProperty("还原材料")
    private List<TSyncMaterialBom> materialBoms;

}
