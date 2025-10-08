package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Auther: l
 * @Date: 2022/4/28 11:28
 * @Description:
 */
@Data
@ApiModel
public class OrderProcessRecordVo {



    @ApiModelProperty("id")
    private Integer orderProcessRecordId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("批次")
    private String bodyLot;

    @ApiModelProperty("班组名称")
    private String className;

    @ApiModelProperty("班组id")
    private Integer classId;

    @ApiModelProperty("处理人名称")
    private String personName;

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("盘点单位-编码")
    private String recordUnit;

    @ApiModelProperty("盘点单位-名称")
    private String recordUnitStr;

    @ApiModelProperty("盘点数量")
    private BigDecimal recordQty;

    @ApiModelProperty("报工数量(手工输入)")
    private BigDecimal recordManualQty = BigDecimal.ZERO;

    @ApiModelProperty("报工时间")
    private String reportTime;

    @ApiModelProperty("类目类型（类目编码）:1=原辅料，2=二级品数量、3=产后数量;交接班盘点，订单完工盘点")
    private String recordType;

    @ApiModelProperty("盘点类型:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点")
    private String recordTypePd;

    @ApiModelProperty("产能单位-编码")
    private String capacityUnit;

    @ApiModelProperty("产能单位-名称")
    private String capacityUnitStr;

    @ApiModelProperty("产能数量")
    private Float capacityQty = 0f;

    private Integer orderProcessHistoryId;

    @ApiModelProperty("机台号")
    private Integer deviceId;

    @ApiModelProperty("机排手")
    private Integer devicePersonId;

    @ApiModelProperty("投入锅数")
    private Float importPot;

    @ApiModelProperty("产出锅数")
    private Float exportPot;

    @ApiModelProperty("产出斗数")
    private Float exportPotMin;

    @ApiModelProperty("拉伸膜物料ID")
    private Integer lsmMaterialId;


}
