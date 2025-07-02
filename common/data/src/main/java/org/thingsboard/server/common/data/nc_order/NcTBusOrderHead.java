package org.thingsboard.server.common.data.nc_order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "t_bus_order_head")
@ApiModel("订单表头类")
public class NcTBusOrderHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    @ApiModelProperty("订单ID")
    @JsonIgnore
    private Integer orderId;

    @Column(name = "order_no")
    @ApiModelProperty("MES订单号")
    @JsonIgnore
    private String orderNo;

    @ApiModelProperty("单据类型")
    @Column(name = "bill_type")
    @JsonProperty("billtypename") 
    private String billType;
    
    @ApiModelProperty("订单状态：0=未开工、1=已开工、2=暂停、3=已完工、4=已挂起")
    @JsonIgnore
    @Column(name = "order_status")
    private String orderStatus;

    @ApiModelProperty("是否删除 1：删除 0：非删除")
    @Column(name = "is_deleted")
    @JsonIgnore
    private String isDeleted;

    @Column(name = "nc_cpmohid")
    @ApiModelProperty("生产订单id")
    private String cpmohid;

    @Column(name = "bill_no")
    @ApiModelProperty("生产订单号")
    private String vbillcode;

    @Column(name = "nc_cmoid")
    @ApiModelProperty("订单明细id")
    private String cmoid;

    @Column(name = "mid_mo_entry_seq")
    @ApiModelProperty("订单明细行号")
    private Integer seq;

    @Column(name = "bill_date")
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty("下单日期")
    private Date dbilldate;

    @Column(name = "body_prd_dept")
    @ApiModelProperty("生产部门")
    private String cdeptname;

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

    @Column(name = "body_material_number")
    @ApiModelProperty("产品编码")
    private String code;

    @Column(name = "body_material_name")
    @ApiModelProperty("产品名称")
    private String name;

    @Column(name = "body_material_specification")
    @ApiModelProperty("规格")
    private String materialspec;

    @Column(name = "body_plan_prd_qty")
    @ApiModelProperty("计划产量（件）")
    private Float nnum;

    @Column(name = "body_plan_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty("计划开工日期")
    private Date tplanstarttime;

    @Column(name = "body_plan_finish_date")
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty("计划完工日期")
    private Date tplanendtime;

    @ApiModelProperty("明细-用料清单")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "t_bus_order_ppbom_lk", joinColumns = {@JoinColumn(name = "order_id")}
            , inverseJoinColumns = {@JoinColumn(name = "order_ppbom_id")})
    private Set<NcTBusOrderPPBom> bomList;
}
