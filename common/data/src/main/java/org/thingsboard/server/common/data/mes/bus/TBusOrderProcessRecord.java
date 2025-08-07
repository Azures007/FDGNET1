package org.thingsboard.server.common.data.mes.bus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;
import org.thingsboard.server.common.data.mes.sys.TSysDevice;

import javax.persistence.*;
import java.util.Date;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @date 2022/4/21 20:37
 * @Description:
 */
@Data
@Entity
@Table(name = "t_bus_order_process_record")
@ApiModel("订单报工/盘点结果表")
public class TBusOrderProcessRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_process_record_id")
    private Integer orderProcessRecordId;

    @ApiModelProperty("工序执行表ID")
    @Column(name = "order_process_id")
    private Integer orderProcessId;

    @ApiModelProperty("订单编号")
    @Column(name = "order_no")
    private String orderNo;

    @ApiModelProperty("业务类型，包括：报工、盘点，用于区分报工和盘点数据")
    @Column(name = "bus_type")
    private String busType;

    @OneToOne
    @ApiModelProperty("工序ID")
    @JoinColumn(name = "process_id")
    private TSysProcessInfo processId;

    @ApiModelProperty("工序编号")
    @Column(name = "process_number")
    private String processNumber;

    @ApiModelProperty("工序名称")
    @Column(name = "process_name")
    private String processName;

    @OneToOne
    @ApiModelProperty("班别id")
    @JoinColumn(name = "class_id")
    private TSysClass classId;

    @OneToOne
    @ApiModelProperty("报工/盘点人员")
    @JoinColumn(name = "person_id")
    private TSysPersonnelInfo personId;

    @OneToOne
    @ApiModelProperty("机台号（废弃）")
    @JoinColumn(name = "device_id")
    private TSysDevice deviceId;

    @OneToOne
    @ApiModelProperty("机排手（废弃）")
    @JoinColumn(name = "device_person_id")
    private TSysPersonnelInfo devicePersonId;

    @ApiModelProperty("类目类型（类目编码）:1=原辅料，2=二级品数量、3=产后数量、4=自定义报工")
    @Column(name = "record_type")
    private String recordType;

    @ApiModelProperty("报工单位")
    @Column(name = "record_unit")
    private String recordUnit;

    @ApiModelProperty("报工数量")
    @Column(name = "record_qty")
    private Float recordQty;

    @ApiModelProperty("报工数量(手工输入)")
    @Column(name = "record_manual_qty")
    private Float recordManualQty;

    @ApiModelProperty("物料ID")
    @Column(name = "material_id")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    @Column(name = "material_number")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    @Column(name = "material_name")
    private String materialName;

    @ApiModelProperty("报工时间")
    @Column(name = "report_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportTime;

    @ApiModelProperty("批次号")
    @Column(name = "body_lot")
    private String bodyLot;

    @ApiModelProperty("二级类目类型（二级类目编码）:1=废膜，2=剩余膜、3=袋装，4=桶装")
    @Column(name = "record_type_l2")
    private String recordTypeL2;

    @ApiModelProperty("产能单位")
    @Column(name = "capacity_unit")
    private String capacityUnit;

    @ApiModelProperty("产能数量")
    @Column(name = "capacity_qty")
    private Float capacityQty;

    @ApiModelProperty("投入锅数")
    @Column(name = "import_pot")
    private Float importPot;

    @ApiModelProperty("产出锅数")
    @Column(name = "export_pot")
    private Float exportPot;

    @ApiModelProperty("产出斗数")
    @Column(name = "export_pot_min")
    private Float exportPotMin;

    @ApiModelProperty("IOT开始时间")
    @Column(name = "iot_collection_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date iotCollectionStartTime;

    @ApiModelProperty("IOT最后取数时间(结束时间)")
    @Column(name = "iot_collection_last_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date iotCollectionLastTime;

    @ApiModelProperty("盘点类型:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点")
    @Column(name = "record_type_pd")
    private String recordTypePd;

    @ApiModelProperty("报工类型:REPORTYPE0001=正常，REPORTYPE0002=尾料")
    @Column(name = "record_type_bg")
    private String recordTypeBg;

    @ApiModelProperty("订单用料清单ID")
    @Column(name = "order_ppbom_id")
    private Integer orderPPBomId;

    @ApiModelProperty("多选机台号分组标识id")
    @Column(name = "device_group_id")
    private String deviceGroupId;

    @ApiModelProperty("多选操作员分组标识id")
    @Column(name = "device_person_group_id")
    private String devicePersonGroupId;

    @ApiModelProperty("当前组合锅数（0：未投满 1：已投满）")
    @Column(name = "import_pot_group")
    private Integer importPotGroup;

    @ApiModelProperty("拉伸膜物料ID")
    @Column(name = "lsm_material_id")
    private Integer lsmMaterialId;

}
