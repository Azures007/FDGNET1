package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/5/10 11:08
 * @Description:
 */
@Data
public class OrderRecordSaveDto {

    @ApiModelProperty("工序执行表ID")
    private Integer orderProcessId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("工序ID")
    private Integer processId;

    @ApiModelProperty("工序编号")
    private String processNumber;

    @ApiModelProperty("工序名称")
    private String processName;

    @ApiModelProperty("机台号")
    private Integer deviceId;

    @ApiModelProperty("机排手")
    private Integer devicePersonId;

    @ApiModelProperty("类目类型（类目编码）:1=原辅料，2=二级品数量、3=产后数量;交接班盘点，订单完工盘点、4=自定义报工")
    private String recordType;

    @ApiModelProperty("二级类目类型（二级类目编码）:1=废膜，2=剩余膜、3=袋装，4=桶装")
    private String recordTypeL2;

    @ApiModelProperty("报工单位")
    private String recordUnit;

    @ApiModelProperty("报工数量")
    private Float recordQty;

    @ApiModelProperty("报工数量(手工输入)")
    private Float recordManualQty = 0f;

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("批次号")
    private String bodyLot;

    @ApiModelProperty("IOT开始时间")
    private String iotCollectionStartTime;

    @ApiModelProperty("IOT最后取数时间(结束时间)")
    private String iotCollectionLastTime;

    @ApiModelProperty("产能单位")
    private String capacityUnit;

    @ApiModelProperty("产能数量")
    private Float capacityQty = 0f;

    @ApiModelProperty("订单用料清单ID")
    private Integer orderPPBomId;

    @ApiModelProperty("多选机台号")
    private List<Integer> deviceIds;

    @ApiModelProperty("多选机排手")
    private List<Integer> devicePersonIds;

    @ApiModelProperty("报工类型:REPORTYPE0001=正常，REPORTYPE0002=尾料")
    private String recordTypeBg="";

    @ApiModelProperty("报工盘点历史记录表父级id")
    private Integer orderProcessHistoryParentId;

    @ApiModelProperty("是否组合投料")
    private Integer isReplaceGroup;

    @ApiModelProperty("当前组合锅数（0：未投满 1：已投满）")
    private Integer importPotGroup;

    @ApiModelProperty("当前已投锅数")
    private Integer importPot;

    @ApiModelProperty("拉伸膜物料ID")
    private Integer lsmMaterialId;

    @ApiModelProperty("设备采集数量")
    private Integer iotQty;

    @ApiModelProperty("计算公式")
    private String iotMath;
}
