package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @date 2022/4/21 20:37
 * @Description:
 */
@Data
@Entity
@Table(name = "t_bus_order_ppbom")
@ApiModel("订单用料清单类")
public class TBusOrderPPBom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_ppbom_id")
    private Integer orderPPBomId;

    @ApiModelProperty("订单id")
    @Column(name = "order_id")
    private Integer orderId;

    @ApiModelProperty("物料ID")
    @Column(name = "material_id")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    @Column(name = "material_number")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    @Column(name = "material_name")
    private String materialName;

    @ApiModelProperty("规格型号")
    @Column(name = "material_specification")
    private String materialSpecification;

    @ApiModelProperty("单位")
    @Column(name = "unit")
    private String unit;

    @Transient
    @ApiModelProperty("单位名称")
    private String unitName;

    @ApiModelProperty("应发数量，即为计划投入量")
    @Column(name = "must_qty")
    private Double mustQty;

    @ApiModelProperty("ERP生产用料清单id")
    @Column(name = "erp_ppbom_id")
    private Integer erpPpbomId;

    @ApiModelProperty("ERP生产用料清单明细id")
    @Column(name = "erp_ppbom_entry_id")
    private Integer erpPpbomEntryId;

    @ApiModelProperty("生产用料单据编号")
    @Column(name = "ppbom_bill_no")
    private String ppbomBillNo;

    @ApiModelProperty("生产订单单据编号")
    @Column(name = "mo_bill_no")
    private String mobillNo;

    @ApiModelProperty("正误差")
    @Column(name = "mid_ppbom_entry_material_positive_error")
    private Float midPpbomEntryMaterialPositiveError;

    @ApiModelProperty("差误差")
    @Column(name = "mid_ppbom_entry_material_negative_error")
    private Float midPpbomEntryMaterialNegativeError;

    @ApiModelProperty("标准值（分子）")
    @Column(name = "mid_ppbom_entry_material_standard")
    private Float midPpbomEntryMaterialStandard;

    @ApiModelProperty("mes单位")
    @Column(name = "mid_ppbom_entry_weigh_mes_unit")
    private String midPpbomEntryWeighMesUnit;
    @ApiModelProperty("mes数量")
    @Column(name = "mid_ppbom_entry_weigh_devept_unit")
    private String midPpbomEntryWeighDeveptUnit;
    @ApiModelProperty("开发单位")
    @Column(name = "mid_ppbom_entry_weigh_mes_qty")
    private Float midPpbomEntryWeighMesQty;
    @ApiModelProperty("开发数量")
    @Column(name = "mid_ppbom_entry_weigh_devept_qty")
    private Float midPpbomEntryWeighDeveptQty;

    @ApiModelProperty("是否需要投入0=不需要 1=需要")
    @Column(name = "mid_ppbom_entry_is_into")
    private Integer midPpbomEntryIsInto;

    @ApiModelProperty("是否需要投入(同步处理)")
    @Transient
    private Boolean midPpbomEntryIsIntoBool;

    @ApiModelProperty("投入工序（ERP工序标识）")
    @Column(name = "mid_ppbom_entry_input_process")
    private String midPpbomEntryInputProcess;

    @ApiModelProperty("子项类型")
    @Column(name = "mid_ppbom_entry_item_type")
    private Integer midPpbomEntryItemType;

    @ApiModelProperty("使用比例")
    @Column(name = "mid_ppbom_entry_use_rate")
    private Float midPpbomEntryUseRate;

    @ApiModelProperty("替换料组")
    @Column(name = "mid_ppbom_entry_replace_group")
    private Integer midPpbomEntryReplaceGroup;

    @ApiModelProperty("bom编码")
    @Column(name = "mid_ppbom_entry_bom_number")
    private String midPpbomEntryBomNumber;

    @ApiModelProperty("分母")
    @Column(name = "mid_ppbom_entry_denominator")
    private Float midPpbomEntryDenominator;

    @ApiModelProperty("操作分组")
    @Column(name = "mid_ppbom_entry_handle_group")
    private Integer midPpbomEntryHandleGroup;

    @ApiModelProperty("操作顺序")
    @Column(name = "mid_ppbom_entry_handle_sort")
    private Integer midPpbomEntryHandleSort;

//    @ApiModelProperty("是否主用料，如蟹肉棒，1=是 0=否")
//    @Column(name = "is_main_ppbom_material")
//    private Integer isMainPpbomMaterial;

    @ApiModelProperty("领料类型 1.专向领料 2.投入领料 3.共耗领料")
    @Column(name = "mid_ppbom_entry_pack_type")
    private Integer midPpbomEntryPackType;

    @Column(name = "nc_cmoid")
    @ApiModelProperty("订单明细id")
    private String cmoid;

    @Column(name = "nc_pk_material")
    @JsonProperty("pk_material")  // 指定JSON属性名
    @ApiModelProperty("物料id")
    private String pkMaterial;
}
