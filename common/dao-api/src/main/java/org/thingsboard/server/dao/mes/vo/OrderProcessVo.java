package org.thingsboard.server.dao.mes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;

import java.math.BigDecimal;
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

    @ApiModelProperty(value = "责任人id")
    private TSysPersonnelInfo personId;
    @ApiModelProperty(value = "责任人名称")
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
    private BigDecimal bodyPlanPrdQty;
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

    @ApiModelProperty("报工类别")
    private String processType;

    @ApiModelProperty("报工物料名称")
    private String materialName;

    @ApiModelProperty("报工规格")
    private String materialSpec;

    @ApiModelProperty("报工批次")
    private String lot;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("报工数量")
    private BigDecimal qty;

    @ApiModelProperty("报工班组")
    private String className;

    @ApiModelProperty("锅数/批次数")
    private Integer potCount;

    @ApiModelProperty("报工时间，格式：yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String reportTime;
}
