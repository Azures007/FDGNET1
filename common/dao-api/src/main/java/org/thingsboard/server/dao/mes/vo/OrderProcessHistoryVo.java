package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/4/28 11:28
 * @Description:
 */
@Data
public class OrderProcessHistoryVo {

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("批次")
    private String bodyLot;

    @ApiModelProperty("班组名称")
    private String className;

    @ApiModelProperty("班组id")
    private Integer classId;

    @ApiModelProperty("报工历史id")
    private Integer orderProcessHistoryId;

    @ApiModelProperty("盘点时间")
    private String reportTime;

    @ApiModelProperty("盘点时间")
    private String personName;

    @ApiModelProperty("工序名称")
    private String processName;

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("盘点单位")
    private String recordUnit;

    @ApiModelProperty("盘点单位")
    private String recordUnitStr;

    @ApiModelProperty("盘点数量")
    private Float recordQty;

    @ApiModelProperty("盘点类型:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点")
    private String recordTypePd;

    @ApiModelProperty("分组")
    private Integer stockCount;

    @ApiModelProperty("盘点时间")
    private String reportStatus;

}
