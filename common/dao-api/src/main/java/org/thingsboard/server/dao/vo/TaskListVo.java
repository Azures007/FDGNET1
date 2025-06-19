package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel("任务订单列表")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskListVo {

    @ApiModelProperty("orderId")
    private Integer orderId;

    @ApiModelProperty("工艺路线id")
    private Integer craftId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("明细-批号")
    private String bodyLot;

//    @ApiModelProperty("盘点类型")
//    private String recordTypePd;

    @ApiModelProperty("物料id")
    private Integer materialId;

    @ApiModelProperty("下单时间")
//    @Temporal(TemporalType.DATE)
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

//    @ApiModelProperty("计划生产数量")
//    private BigDecimal bodyPlanPrdQty;

    @ApiModelProperty("类型，包括：1=正常订单、2=移交订单")
    private String type;

    @ApiModelProperty("明细-规格型号")
    private String bodyMaterialSpecification;

    @ApiModelProperty("工序执行表ID")
    private Integer orderProcessId;

    @ApiModelProperty("订单明细物料id")
    private Integer bodyMaterialId;

    @ApiModelProperty("锅数")
    private  BigDecimal bodyPotQty;

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

    @ApiModelProperty("盘点类型(当前)")
    private String executeRecordTypePd;

    @ApiModelProperty("盘点类型（移交前）")
    private String recordTypePd;

    @ApiModelProperty("转移记录列表用的目标工序执行表id")
    private Integer toOrderProcessId = -1;

    public Integer getCraftId() {
        return craftId;
    }

    public void setCraftId(Integer craftId) {
        this.craftId = craftId;
    }

    public String getBodyUnitStr() {
        return bodyUnitStr;
    }

    public void setBodyUnitStr(String bodyUnitStr) {
        this.bodyUnitStr = bodyUnitStr;
    }

    public String getRecordTypePd() {
        return recordTypePd;
    }

    public void setRecordTypePd(String recordTypePd) {
        this.recordTypePd = recordTypePd;
    }

    public Integer getToOrderProcessId() {
        return toOrderProcessId;
    }

    public void setToOrderProcessId(Integer toOrderProcessId) {
        this.toOrderProcessId = toOrderProcessId;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getBodyMaterialSpecification() {
        return bodyMaterialSpecification;
    }

    public void setBodyMaterialSpecification(String bodyMaterialSpecification) {
        this.bodyMaterialSpecification = bodyMaterialSpecification;
    }

    public Integer getBodyMaterialId() {
        return bodyMaterialId;
    }

    public void setBodyMaterialId(Integer bodyMaterialId) {
        this.bodyMaterialId = bodyMaterialId;
    }

    public String getBodyMaterialNumber() {
        return bodyMaterialNumber;
    }

    public void setBodyMaterialNumber(String bodyMaterialNumber) {
        this.bodyMaterialNumber = bodyMaterialNumber;
    }

    public String getBodyPlanFinishDate() {
        return bodyPlanFinishDate;
    }

    public void setBodyPlanFinishDate(String bodyPlanFinishDate) {
        this.bodyPlanFinishDate = bodyPlanFinishDate;
    }

    public String getBodyLot() {
        return bodyLot;
    }

    public void setBodyLot(String bodyLot) {
        this.bodyLot = bodyLot;
    }

    public String getBodyUnit() {
        return bodyUnit;
    }

    public void setBodyUnit(String bodyUnit) {
        this.bodyUnit = bodyUnit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBodyMaterialName() {
        return bodyMaterialName;
    }

    public void setBodyMaterialName(String bodyMaterialName) {
        this.bodyMaterialName = bodyMaterialName;
    }

    public Float getBillPlanQty() {
        return billPlanQty;
    }

    public void setBillPlanQty(Float billPlanQty) {
        this.billPlanQty = billPlanQty;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getOrderProcessId() {
        return orderProcessId;
    }

    public void setOrderProcessId(Integer orderProcessId) {
        this.orderProcessId = orderProcessId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessNumber() {
        return processNumber;
    }

    public void setProcessNumber(String processNumber) {
        this.processNumber = processNumber;
    }

    public Integer getExecuteProcessId() {
        return executeProcessId;
    }

    public void setExecuteProcessId(Integer executeProcessId) {
        this.executeProcessId = executeProcessId;
    }

    public String getExecuteProcessName() {
        return executeProcessName;
    }

    public void setExecuteProcessName(String executeProcessName) {
        this.executeProcessName = executeProcessName;
    }

    public String getExecuteProcessNumber() {
        return executeProcessNumber;
    }

    public void setExecuteProcessNumber(String executeProcessNumber) {
        this.executeProcessNumber = executeProcessNumber;
    }

    public String getExecuteProcessStatus() {
        return executeProcessStatus;
    }

    public void setExecuteProcessStatus(String executeProcessStatus) {
        this.executeProcessStatus = executeProcessStatus;
    }

    public String getExecuteRecordTypePd() {
        return executeRecordTypePd;
    }

    public void setExecuteRecordTypePd(String executeRecordTypePd) {
        this.executeRecordTypePd = executeRecordTypePd;
    }

    public String getOrderPendingDesc() {
        return orderPendingDesc;
    }

    public void setOrderPendingDesc(String orderPendingDesc) {
        this.orderPendingDesc = orderPendingDesc;
    }
}
