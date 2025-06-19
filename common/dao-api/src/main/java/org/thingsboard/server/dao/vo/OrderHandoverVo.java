package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/29 14:46
 * @Description:
 */
@Data
@ApiModel("移交详情")
public class OrderHandoverVo {
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

    @ApiModelProperty("预期产量")
    private Float bodyPlanPrdQty;

    @ApiModelProperty("实际产量")
    private Float  actualQty;

    @ApiModelProperty("未生产")
    private Float unProduceQty;

    @ApiModelProperty("单位-编码")
    private String unit;

    @ApiModelProperty("单位-名称")
    private String unitStr;

    @ApiModelProperty("类目类型（类目编码）:数据字典维护，内容包括：原辅料，二级品数量、产后数量;交接班盘点，订单完工盘点")
    private String recordType;

    @ApiModelProperty("汇总列表")
    List<OrderProcessRecordVo> recordVos;

    @ApiModelProperty("时间")
    private String reportTime;

    @ApiModelProperty("明细-锅数")
    private String bodyPotQty;

    @ApiModelProperty("投入锅数")
    private String importPot;

    @ApiModelProperty("合格完成率")
    private String qualifiedRate;

    // AB料累计数、废品累计数
    @ApiModelProperty("AB料累计数")
    private String ungradedAbQty ;


    @ApiModelProperty("废品累计数")
    private String ungradedWasteQty;

    @ApiModelProperty("盘点类型:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点")
    private String recordTypePd;

    @ApiModelProperty("处理人")
    private String recordName;

    @ApiModelProperty("移交人")
    private String handOverName;

    //报工历史记录表记录
    @ApiModelProperty("报工历史记录表记录")
    List<OrderProcessHistoryVo> orderProcessHistoryVoList;
}