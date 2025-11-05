package org.thingsboard.server.common.data.mes.bus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "t_bus_order_pot_count")
@ApiModel("锅数记录表（按物料累计投入次数与锅数）")
public class TBusOrderPotCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("工序执行表ID")
    @Column(name = "order_process_id")
    private Integer orderProcessId;

    @ApiModelProperty("订单用料清单ID")
    @Column(name = "order_ppbom_id")
    private Integer orderPPBomId;

    @ApiModelProperty("人员分组ID（机排手分组）")
    @Column(name = "device_person_group_id")
    private String devicePersonGroupId;

    @ApiModelProperty("物料ID")
    @Column(name = "material_id")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    @Column(name = "material_number")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    @Column(name = "material_name")
    private String materialName;

    @ApiModelProperty("投入次数")
    @Column(name = "input_count")
    private Integer inputCount = 0;

    @ApiModelProperty("锅数")
    @Column(name = "pot_number")
    private Integer potNumber = 0;

    @ApiModelProperty("工序编码")
    @Column(name = "process_number")
    private String processNumber;

    @ApiModelProperty("工序名称")
    @Column(name = "process_name")
    private String processName;

    @ApiModelProperty("分组编码（半成品分组标识）")
    @Column(name = "group_code")
    private String groupCode;

    @ApiModelProperty("分组名称")
    @Column(name = "group_name")
    private String groupName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderProcessId() {
        return orderProcessId;
    }

    public void setOrderProcessId(Integer orderProcessId) {
        this.orderProcessId = orderProcessId;
    }

    public Integer getOrderPPBomId() {
        return orderPPBomId;
    }

    public void setOrderPPBomId(Integer orderPPBomId) {
        this.orderPPBomId = orderPPBomId;
    }

    public String getDevicePersonGroupId() {
        return devicePersonGroupId;
    }

    public void setDevicePersonGroupId(String devicePersonGroupId) {
        this.devicePersonGroupId = devicePersonGroupId;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public Integer getInputCount() {
        return inputCount == null ? 0 : inputCount;
    }

    public void setInputCount(Integer inputCount) {
        this.inputCount = inputCount;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getPotNumber() {
        return potNumber == null ? 0 : potNumber;
    }

    public void setPotNumber(Integer potNumber) {
        this.potNumber = potNumber;
    }

    public String getProcessNumber() {
        return processNumber;
    }

    public void setProcessNumber(String processNumber) {
        this.processNumber = processNumber;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}



