package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Auther: hhh
 * @Date: 2022/8/11 09:59
 * @Description:
 */
@Data
@ApiModel("关联任务订单列表")
@NoArgsConstructor
@AllArgsConstructor
public class TaskListRelationVo {
    @ApiModelProperty("orderId")
    private Integer orderId;

    @ApiModelProperty("工艺路线id")
    private Integer craftId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("明细-批号")
    private String bodyLot;

    @ApiModelProperty("物料id")
    private Integer materialId;

    @ApiModelProperty("下单时间")
//    @Temporal(TemporalType.DATE)
    private String billDate;

    @ApiModelProperty("物料名称")
    private String bodyMaterialName;

    @ApiModelProperty("预期产量")
    private BigDecimal billPlanQty;

    @ApiModelProperty("明细-单位-编码")
    private String bodyUnit;

    @ApiModelProperty("明细-单位")
    private String bodyUnitStr;

    @ApiModelProperty("订单状态：0=未开工、1=已开工、2=暂停、3=已完工 4=挂起")
    private String orderStatus;

    @ApiModelProperty("挂起备注")
    private String orderPendingDesc;

    @ApiModelProperty("完工时间")
    private String finishTime;

    @ApiModelProperty("类型，包括：1=正常订单、2=移交订单")
    private String type;

    @ApiModelProperty("明细-规格型号")
    private String bodyMaterialSpecification;

    @ApiModelProperty("工序执行表ID")
    private Integer orderProcessId;

    @ApiModelProperty("订单明细物料id")
    private Integer bodyMaterialId;

    @ApiModelProperty("明细-物料编码")
    private String bodyMaterialNumber;

    @ApiModelProperty("明细-计划完工时间")
    private String bodyPlanFinishDate;

    @ApiModelProperty("当前工序ID")
    private Integer processId;
    @ApiModelProperty("当前工序名称")
    private String processName;
    @ApiModelProperty("当前工序编码")
    private String processNumber;

    @ApiModelProperty("执行工序ID")
    private Integer executeProcessId;
    @ApiModelProperty("执行工序名称")
    private String executeProcessName;
    @ApiModelProperty("执行工序编码")
    private String executeProcessNumber;

    @ApiModelProperty("执行工序状态")
    private String executeProcessStatus;

    @ApiModelProperty("班别id")
    private Integer classId;
    @ApiModelProperty("班别名称")
    private String className;

    @ApiModelProperty(value = "处理人")
    private Integer personId;
    @ApiModelProperty("处理人名称")
    private String personName;

    @ApiModelProperty("盘点类型(当前):STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点")
    private String executeRecordTypePd;

    @ApiModelProperty("盘点类型（移交前）:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点")
    private String recordTypePd;


}
