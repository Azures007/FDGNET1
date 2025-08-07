package org.thingsboard.server.common.data.mes.bus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_bus_order_bind_code")
@ApiModel("扫码绑定信息表")
public class TBusOrderBindCode {
    @ApiModelProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bind_code_id")
    private Integer bindCodeId;
    @ApiModelProperty("订单报工结果表ID")
    @Column(name = "order_process_record_id")
    private Integer orderProcessRecordId;
    @ApiModelProperty("订单报工历史记录表ID")
    @Column(name = "order_process_history_id")
    private Integer orderProcessHistoryId;
    @ApiModelProperty("订单工序执行表ID")
    @Column(name = "order_process_id")
    private Integer orderProcessId;
    @ApiModelProperty("扫码类型，包括：报工扫码=BIND0001、接单扫码=BIND0002")
    @Column(name = "bind_code_type")
    private String bindCodeType;
    @ApiModelProperty("本工序订单号")
    @Column(name = "order_no")
    private String orderNo;
    @ApiModelProperty("本工序id")
    @Column(name = "process_id")
    private Integer processId;
    @ApiModelProperty("本工序班组id")
    @Column(name = "classs_id")
    private Integer classsId;
    @ApiModelProperty("本工序处理人")
    @Column(name = "person_id")
    private Integer personId;
    @ApiModelProperty("绑定料框编码")
    @Column(name = "bind_code_number")
    private String bindCodeNumber;
    @ApiModelProperty("上道订单订单号")
    @Column(name = "prev_order_no")
    private String prevOrderNo;
    @ApiModelProperty("上道订单报工结果表ID")
    @Column(name = "prev_order_process_record_id")
    private Integer prevOrderProcessRecordId;
    @ApiModelProperty("上道订单报工历史记录表ID")
    @Column(name = "prev_order_process_history_id")
    private Integer prevOrderProcessHistoryId;
    @ApiModelProperty("上道订单工序执行表ID")
    @Column(name = "prev_order_process_id")
    private Integer prevOrderProcessId;
    @ApiModelProperty("绑定状态：1.已绑定，2.已解绑")
    @Column(name = "bind_code_status")
    private Integer bindCodeStatus;
    @ApiModelProperty("创建时间")
    @Column(name = "created_time")
    private Date createdTime;
    @ApiModelProperty("工艺路线id")
    @Transient
    private Integer craftId;
    @ApiModelProperty("报工扫码绑定的扫码绑定信息ID")
    @Transient
    private Integer prevBindCodeId;

}
