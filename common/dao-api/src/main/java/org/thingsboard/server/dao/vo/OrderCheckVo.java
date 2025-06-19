package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/28 10:41
 * @Description:
 */
@Data
public class OrderCheckVo {
    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("批次号")
    private String bodyLot;

    @ApiModelProperty("报工/盘点人员")
    private Integer personId;

    @ApiModelProperty("报工/盘点人员")
    private String personName;

    @ApiModelProperty("班别id")
    private Integer classId;

    @ApiModelProperty("班组名称")
    private String className;


    @ApiModelProperty("类目类型（类目编码）:数据字典维护，内容包括：原辅料，二级品数量、产后数量;交接班盘点，订单完工盘点")
    private String recordType;

    @ApiModelProperty("盘点类型:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点")
    private String recordTypePd;

    @ApiModelProperty("汇总列表")
    List<OrderProcessRecordVo> recordVos;

//    @ApiModelProperty("盘点历史列表")
//    List<OrderProcessHistoryVo> historyVos;

    @ApiModelProperty("盘点时间")
    private String reportTime;


}