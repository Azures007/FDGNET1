package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/7/21 14:00
 * @Description:
 */
@Data
@ApiModel("移交记录模型")
public class HandOverRecordVo {
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
    private String billDate;

    @ApiModelProperty("物料名称")
    private String bodyMaterialName;

    @ApiModelProperty("预期产量")
    private Float billPlanQty;

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

    @ApiModelProperty("盘点类型")
    private String recordTypePd;
}