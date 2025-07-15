package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.TSysPersonnelInfo;
import org.thingsboard.server.common.data.TSysProcessInfo;

import java.util.List;

@Data
@ApiModel("订单工序信息")
public class OrderProcessVo {

    @ApiModelProperty("工序ID")
    private TSysProcessInfo processId;
    @ApiModelProperty("工序名称")
    private String processName;
    @ApiModelProperty("工序编号")
    private String processNumber;
    @ApiModelProperty("工序执行序号")
    private Integer processSeq;

    @ApiModelProperty("工序状态")
    private String processStatus;
    @ApiModelProperty("暂停原因")
    private String suspendReason;

    @ApiModelProperty(value = "处理人")
    private TSysPersonnelInfo personId;
    @ApiModelProperty(value = "处理人名称")
    private String personName;

    @ApiModelProperty("接单时间")
    private String receiveTime;
    @ApiModelProperty("完成时间")
    private String finishTime;

    @ApiModelProperty("耗时:完成时间-接单时间")
    private Integer elapsedTime;

    @ApiModelProperty("未完成数量：计划生产数量-工序实际产量")
    private Float unFinishQty;
    @ApiModelProperty("未完成单位-编码")
    private String unFinishUnit;
    @ApiModelProperty("未完成单位-名称")
    private String unFinishUnitStr;

    @ApiModelProperty(value = "报工记录集合")
    private List<OrderProcessRecordDtlVo> orderProcessRecordDtlVoList;

    @ApiModelProperty("合格完成率")
    private Float qualifiedRate;
    @ApiModelProperty("总的完成率(废弃)")
    private Float totalCompletionRate;

    @ApiModelProperty("明细-计划生产数量")
    private Float bodyPlanPrdQty;
    @ApiModelProperty("明细-单位")
    private String bodyUnit;
    @ApiModelProperty("明细-单位")
    private String bodyUnitStr;

    //12816 AB料累计数、废品累计数
    @ApiModelProperty("AB料累计数")
    private Float ungradedAbQty = 0f;
    @ApiModelProperty("AB料累计数-单位")
    private String ungradedAbUnit;
    @ApiModelProperty("AB料累计数-单位")
    private String ungradedAbUnitStr;

    @ApiModelProperty("废品累计数")
    private Float ungradedWasteQty = 0f;
    @ApiModelProperty("废品累计数-单位")
    private String ungradedWasteUnit;
    @ApiModelProperty("废品累计数-单位")
    private String ungradedWasteUnitStr;

    @ApiModelProperty("累计产后数量")
    private float totalProductQty = 0f;
}
