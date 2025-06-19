package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/4/27 19:43
 * @Description:盘点提交模型
 */
@Data
public class OrderProcessRecordSaveDto {

    @ApiModelProperty("工序执行id")
    private Integer orderProcessId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("业务类型，包括：报工、盘点，用于区分报工和盘点数据")
    private String busType;

    @ApiModelProperty("工序ID")
    private Integer processId;

    @ApiModelProperty("工序编号")
    private String processNumber;

    @ApiModelProperty("工序名称")
    private String processName;

    @ApiModelProperty("班别id")
    private Integer classId;

    @ApiModelProperty("报工/盘点人员")
    private Integer personId;

    @ApiModelProperty("盘点单位")
    private String recordUnit;

    @ApiModelProperty("盘点数量")
    private Float recordQty;

//    @ApiModelProperty("盘点数量(手工输入)")
//    private Float recordManualQty = 0f;

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("盘点时间")
    private Date reportTime;

    @ApiModelProperty("批次号")
    private String bodyLot;

    @ApiModelProperty("盘点类型:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点")
    private String recordTypePd;

    @ApiModelProperty("订单用料清单ID")
    private Integer orderPPBomId;

    @ApiModelProperty("类目类型（类目编码）:1=原辅料，2=二级品数量、3=产后数量;交接班盘点，订单完工盘点、4=自定义报工或盘点、投入前道数量")
    private String recordType;


}
