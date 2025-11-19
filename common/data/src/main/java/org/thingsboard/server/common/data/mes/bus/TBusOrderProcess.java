package org.thingsboard.server.common.data.mes.bus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;

import javax.persistence.*;
import java.sql.Timestamp;
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
@Table(name = "t_bus_order_process")
@ApiModel("订单工序执行类")
public class
TBusOrderProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_process_id")
    private Integer orderProcessId;

    @ApiModelProperty("订单号")
    @Column(name = "order_no")
    private String orderNo;

    @ApiModelProperty("接单时间")
    @Column(name = "receive_time")
//    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp receiveTime;

    @ApiModelProperty("完工时间")
    @Column(name = "finish_time")
//    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp finishTime;

    @OneToOne
    @ApiModelProperty("工序ID")
    @JoinColumn(name = "process_id")
    private TSysProcessInfo processId;

    @ApiModelProperty("工序执行序号")
    @Column(name = "process_seq")
    private Integer processSeq;

    @ApiModelProperty("耗时:完工时间-接单时间")
    @Column(name = "elapsed_time")
    private Integer elapsedTime;

    @ApiModelProperty("工序状态,包括：0=未开工、1=已开工、2=暂停、3=已完工、4=移交中、5=移交驳回")
    @Column(name = "process_status")
    private String processStatus = "0";

    @ApiModelProperty("原工序状态")
    @Column(name = "oldProcessStatus")
    private String oldProcessStatus;

    @ApiModelProperty("类型，包括：1=正常订单、2=移交订单")
    @Column(name = "type")
    private String type = "1";

    @ApiModelProperty("移交的订单工序ID(记录由哪个订单工序移交过来的,非移交订单该值为空)")
    @Column(name = "old_order_process_id")
    private Integer oldOrderProcessId = 0;

    @OneToOne
    @ApiModelProperty("班别id")
    @JoinColumn(name = "class_id")
    private TSysClass classId;

    @OneToOne
    @ApiModelProperty(value = "处理人")
    @JoinColumn(name = "person_id")
    private TSysPersonnelInfo personId;

//    @ApiModelProperty("报工记录表")
//    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinTable(name="t_bus_order_process_record_lk",joinColumns={@JoinColumn(name="order_process_id")}
//            ,inverseJoinColumns={@JoinColumn(name="order_process_record_id")})
//    private Set<TBusOrderProcessRecord> tBusOrderProcessRecordSet;

    @ApiModelProperty("暂停原因，包括：1=机械故障维修中 2.其他原因")
    @Column(name = "suspend_reason")
    private String suspendReason;

    @OneToOne
    @ApiModelProperty(value = "移交人")
    @JoinColumn(name = "hand_over_person_id")
    private TSysPersonnelInfo handOverPerSonId;

    @ApiModelProperty("移交时间")
    @Column(name = "hand_over_time")
    private Timestamp handOverTime;

    @OneToOne
    @ApiModelProperty(value = "移交前的移交人")
    @JoinColumn(name = "old_hand_over_person_id")
    private TSysPersonnelInfo oldHandOverPerSonId;

    @ApiModelProperty("移交前的移交时间")
    @Column(name = "old_hand_over_time")
    private Timestamp oldHandOverTime;

    @ApiModelProperty("移交前的盘点类型")
    @Column(name = "old_record_type_pd")
    private String oldRecordTypePd;

    @ApiModelProperty("移交前的盘点时间")
    @Column(name = "old_record_type_pd_time")
    private Date oldRecordTypePdTime;

//    @ApiModelProperty("当前的盘点类型")
//    @Column(name = "record_type_pd")
//    private String recordTypePd;
//
//    @ApiModelProperty("当前的盘点时间")
//    @Column(name = "record_type_pd_time")
//    private Date recordTypePdTime;

    @ApiModelProperty("订单id")
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "nc_cmoid")
    @ApiModelProperty("订单明细id")
    private String cmoid;

}
