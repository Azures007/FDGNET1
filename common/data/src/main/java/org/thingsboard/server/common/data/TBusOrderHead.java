package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @date 2022/4/21 20:37
 * @Description:
 */
@Data
@Entity
@Table(name = "t_bus_order_head")
@ApiModel("订单表头类")
public class TBusOrderHead  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    @Column(name = "order_no")
    private String orderNo;

    @ApiModelProperty("订单状态：0=未开工、1=已开工、2=暂停、3=已完工、4=已挂起")
    @Column(name = "order_status")
    private String orderStatus = "0";

    @ApiModelProperty("单据编号")
    @Column(name = "bill_no")
    private String billNo;

    @ApiModelProperty("计划员")
    @Column(name = "planner")
    private String planner;

    @ApiModelProperty("单据类型")
    @Column(name = "bill_type")
    private String billType;

    @ApiModelProperty("单据日期(下单时间)")
    @Column(name = "bill_date")
    @Temporal(TemporalType.DATE)
    private Date billDate;

    @ApiModelProperty("单据状态")
    @Column(name = "document_status")
    private String documentStatus;

    @ApiModelProperty("erp创建日期")
    @Column(name = "bill_created_time")
    @Temporal(TemporalType.DATE)
    private Date erpCreatedDate;

    @ApiModelProperty("预期产量-废弃")
    @Column(name = "bill_plan_qty")
    private Float billPlanQty;

    @ApiModelProperty("生产组织")
    @Column(name = "prd_org")
    private String prdOrg;

    @ApiModelProperty("b物料ID")
    @Column(name = "b_material_id")
    private Integer bMaterialId;

    @ApiModelProperty("b物料编码")
    @Column(name = "b_material_number")
    private String bMaterialNumber;

    @ApiModelProperty("b物料名称")
    @Column(name = "b_material_name")
    private String bMaterialName;

    @ApiModelProperty("a物料ID")
    @Column(name = "material_id")
    private Integer materialId;

    @ApiModelProperty("a物料编码")
    @Column(name = "material_number")
    private String materialNumber;

    @ApiModelProperty("a物料名称")
    @Column(name = "material_name")
    private String materialName;

    @ApiModelProperty("添加锅数-废弃")
    @Column(name = "add_guo_qty")
    private Float addGuoQty;

    @ApiModelProperty("添加单位重量-废弃")
    @Column(name = "add_unit_weight")
    private Float addUnitWeight;

    @ApiModelProperty("添加总重量-废弃")
    @Column(name = "add_sum_weight")
    private Float addSumWeight;

    @ApiModelProperty("创建人")
    @Column(name = "created_name")
    private String createdName;
    @ApiModelProperty("创建时间")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("修改人")
    @Column(name = "updated_name")
    private String updatedName;
    @ApiModelProperty("修改时间")
    @Column(name = "updated_time")
    private Date updatedTime;

    @OneToOne
    @ApiModelProperty("当前工序")
    @JoinColumn(name = "current_process")
    private TSysProcessInfo currentProcess;

    @OneToOne
    @ApiModelProperty("班别id（处理班别）")
    @JoinColumn(name = "class_id")
    private TSysClass classId;

    @OneToOne
    @ApiModelProperty(value = "处理人（操作人）")
    @JoinColumn(name = "current_person_id")
    private TSysPersonnelInfo currentPersonId;

    @OneToOne
    @ApiModelProperty("工艺路线id")
    @JoinColumn(name = "craft_id")
    private TSysCraftInfo craftId;

    @ApiModelProperty("备注说明(工艺路线)")
    @Column(name = "craft_desc")
    private String craftDesc;

    /*从订单明细迁移的字段*/
    @ApiModelProperty("明细-批号")
    @Column(name = "body_lot")
    private String bodyLot;

    @ApiModelProperty("明细-生产车间")
    @Column(name = "body_prd_dept")
    private String bodyPrdDept;

    @ApiModelProperty("明细-物料ID")
    @Column(name = "body_material_id")
    private Integer bodyMaterialId;

    @ApiModelProperty("明细-物料编码")
    @Column(name = "body_material_number")
    private String bodyMaterialNumber;

    @ApiModelProperty("明细-物料名称")
    @Column(name = "body_material_name")
    private String bodyMaterialName;

    @ApiModelProperty("明细-规格型号")
    @Column(name = "body_material_specification")
    private String bodyMaterialSpecification;

    @ApiModelProperty("明细-计划生产数量")
    @Column(name = "body_plan_prd_qty")
    private Float bodyPlanPrdQty;

    @ApiModelProperty("明细-单位")
    @Column(name = "body_unit")
    private String bodyUnit;

    @ApiModelProperty("明细-计划开工时间")
    @Column(name = "body_plan_start_date")
    @Temporal(TemporalType.DATE)
    private Date bodyPlanStartDate;

    @ApiModelProperty("明细-计划完工时间")
    @Column(name = "body_plan_finish_date")
    @Temporal(TemporalType.DATE)
    private Date bodyPlanFinishDate;

    @ApiModelProperty("ERP生产订单id")
    @Column(name = "erp_mo_fid")
    private Integer erpMoFid;

    @ApiModelProperty("ERP生产订单明细id")
    @Column(name = "erp_mo_entry_id")
    private Integer erpMoEntryId;

    @ApiModelProperty("明细-产品类型")
    @Column(name = "body_product_type")
    private String bodyProductType;

    @ApiModelProperty("明细-每锅数量")
    @Column(name = "body_one_pot_qty")
    private Float bodyOnePotQty;

    @ApiModelProperty("明细-锅数")
    @Column(name = "body_pot_qty")
    private Integer bodyPotQty;

    @ApiModelProperty("明细-班组（废弃）")
    @Column(name = "body_class_group")
    private String bodyClassGroup;

    @ApiModelProperty("erp修改日期")
    @Column(name = "mid_mo_modify_date")
    private Date midMoModifyDate;

    @ApiModelProperty("明细行号")
    @Column(name = "mid_mo_entry_seq")
    private Integer midMoEntrySeq;

    @ApiModelProperty("A料-物料名称")
    @Column(name = "mid_mo_entry_first_material_name")
    private String midMoEntryFirstMaterialName;

    @ApiModelProperty("B料-比例")
    @Column(name = "mid_mo_entry_second_material_proportion")
    private Float midMoEntrySecondMaterialProportion;

    @ApiModelProperty("A料-物料编码")
    @Column(name = "mid_mo_entry_first_material_number")
    private String midMoEntryFirstMaterialNumber;

    @ApiModelProperty("A料-比例")
    @Column(name = "mid_mo_entry_first_material_proportion")
    private Float midMoEntryFirstMaterialProportion;

    @ApiModelProperty("明细-用料清单")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "t_bus_order_ppbom_lk", joinColumns = {@JoinColumn(name = "order_id")}
            , inverseJoinColumns = {@JoinColumn(name = "order_ppbom_id")})
    private Set<TBusOrderPPBom> tBusOrderPPBomSet;

    @ApiModelProperty("工序执行表")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "t_bus_order_process_lk", joinColumns = {@JoinColumn(name = "order_id")}
            , inverseJoinColumns = {@JoinColumn(name = "order_process_id")})
    private Set<TBusOrderProcess> tBusOrderProcessSet;

    @Transient
    @ApiModelProperty("中间表订单明细与物料的关联id")
    private Integer erpMidMoEntryPpbomId;

    @ApiModelProperty("原订单状态，用于反挂起更新：0=未开工、1=已开工、2=暂停、3=已完工、4=已挂起")
    @Column(name = "old_order_status")
    private String oldOrderStatus;

    @ApiModelProperty("是否删除 0：删除 1：非删除")
    @Column(name = "is_deleted")
    private String isDeleted;

    @ApiModelProperty("每锅A料添加最大值")
    @Column(name = "mid_mo_entry_first_material_max_value")
    private Float midMoEntryFirstMaterialMaxValue;

    @ApiModelProperty("每锅B料添加最大值")
    @Column(name = "mid_mo_entry_second_material_max_value")
    private Float midMoEntrySecondMaterialMaxValue;

    @ApiModelProperty("bomId")
    @Column(name = "mid_mo_entry_bom_id")
    private Integer midMoEntryBomId;
    @ApiModelProperty("bom编号")
    @Column(name = "mid_mo_entry_bom_number")
    private String midMoEntryBomNumber;

    @ApiModelProperty("完工时间")
    @Column(name = "order_finish_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderFinishDate;

    @ApiModelProperty("挂起时间")
    @Column(name = "order_pending_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderPendingDate;

    @ApiModelProperty("挂起备注")
    @Column(name = "order_pending_desc")
    private String orderPendingDesc;

    @ApiModelProperty("明细-需求单据（废弃）")
    @Column(name = "mid_mo_entry_sale_order_no")
    private String midMoEntrySaleOrderNo;

    @ApiModelProperty("明细-需求单据")
    @Column(name = "mid_mo_sale_order_no")
    private String midMoSaleOrderNo;

    //废弃
    @ApiModelProperty("班组-ID")
    @Column(name = "mid_mo_team_id")
    private Integer midMoTeamId;

    @ApiModelProperty("班组-名字")
    @Column(name = "mid_mo_team_name")
    private String midMoteamName;

    @ApiModelProperty("班组-编码")
    @Column(name = "mid_mo_team_number")
    private String midMoteamNumber;

    //新增
    @ApiModelProperty("明细-班组-ID")
    @Column(name = "mid_mo_entry_team_id")
    private Integer midMoEntryTeamId;

    @ApiModelProperty("明细-班组-名字")
    @Column(name = "mid_mo_entry_team_name")
    private String midMoEntryTeamName;

    @ApiModelProperty("明细-班组-编码")
    @Column(name = "mid_mo_entry_team_number")
    private String midMoEntryTeamNumber;

    @ApiModelProperty("冰水最大值")
    @Column(name = "mid_mo_entry_ice_water")
    private Float midMoEntryIceWater;

    @ApiModelProperty("次品乳化浆最大值")
    @Column(name = "mid_mo_entry_emulsion")
    private Float midMoEntryEmulsion;

    @ApiModelProperty("匹配工艺路线: 0: 不匹配, 1:匹配")
    @Column(name = "order_matching")
    private String orderMatching = "0";

    @Column(name = "nc_cpmohid")
    @ApiModelProperty("生产订单id")
    private String cpmohid;

    @Column(name = "nc_cmoid")
    @ApiModelProperty("订单明细id")
    private String cmoid;

    @Column(name = "nc_cdeptid")
    @ApiModelProperty("生产部门id")
    private String cdeptid;

    @Column(name = "nc_vwkname")
    @ApiModelProperty("生产线")
    private String vwkname;

    @Column(name = "nc_cwkid")
    @ApiModelProperty("生产线id")
    private String cwkid;

    @Column(name = "nc_pk_material")
    @JsonProperty("pk_material")  // 指定JSON属性名
    @ApiModelProperty("物料id")
    private String pkMaterial;
}
