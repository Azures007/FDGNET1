package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @Auther: l
 * @Date: 2022/4/25 19:48
 * @Description:
 */
@Data
@ApiModel("订单用料清单")
public class OrderPPbomResult {

    @ApiModelProperty("订单用料清单ID")
    private Integer orderPPBomId;

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("规格型号")
    private String materialSpecification;

    @ApiModelProperty("单位编码")
    private String unit;

    @ApiModelProperty("单位")
    private String unitStr;

    @ApiModelProperty("正误差")
    @Column(name = "mid_ppbom_entry_material_positive_error")
    private Float midPpbomEntryMaterialPositiveError;

    @ApiModelProperty("差误差")
    @Column(name = "mid_ppbom_entry_material_negative_error")
    private Float midPpbomEntryMaterialNegativeError;

    @ApiModelProperty("标准值")
    @Column(name = "mid_ppbom_entry_material_standard")
    private Float midPpbomEntryMaterialStandard;

    @ApiModelProperty("应发数量，即为计划投入量")
    private Double mustQty;

    @ApiModelProperty("mes单位")
    private String midPpbomEntryWeighMesUnit;
    @ApiModelProperty("mes数量")
    private String midPpbomEntryWeighDeveptUnit;
    @ApiModelProperty("开发单位")
    private Float midPpbomEntryWeighMesQty;
    @ApiModelProperty("开发数量")
    private Float midPpbomEntryWeighDeveptQty;

    @ApiModelProperty("是否去皮 1=去皮 0=不去皮")
    private Integer kdMaterialIsPeel;

    @ApiModelProperty("组合标记，相同为同个组合")
    private Integer midPpbomEntryReplaceGroup;

    @ApiModelProperty("组合标签")
    private String groupFlag;

    @ApiModelProperty("子项类型")
    private String midPpbomEntryItemType;

    @ApiModelProperty("累计投入(累计重量)")
    private Float recordQtyTotal = 0f;

    @ApiModelProperty("个人投入")
    private Float recordQtyPersonal = 0f;

    @ApiModelProperty("投入次数（累计锅数）")
    private String personalCount;

    @ApiModelProperty("投入单位编码")
    private String recordUnit;

    @ApiModelProperty("投入单位")
    private String recordUnitStr;

    @ApiModelProperty("标记 0:普通用料 1：蟹肉棒")
    private String flag = "0";

    private String personalCountBak;

    @ApiModelProperty("未完成重量")
    private Float unCompleteQty;

    @ApiModelProperty("累计完成率")
    private String completeRate;

    @ApiModelProperty("未完成率")
    private String unCompleteRate;
}
