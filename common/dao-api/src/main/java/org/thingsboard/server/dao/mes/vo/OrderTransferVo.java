package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysClass;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.List;

@Data
@ApiModel("订单移交信息")
public class OrderTransferVo {

    @ApiModelProperty("订单编码")
    private String orderNo;

    @ApiModelProperty("批次")
    private String bodyLot;

    @ApiModelProperty("班别id")
    private TSysClass classId;
    @ApiModelProperty("班别名称")
    private String className;

    //盘点结果表没有该字段
    @ApiModelProperty("盘点时间")
    @Temporal(TemporalType.DATE)
    private String reportTime;

    @ApiModelProperty("明细-物料ID")
    private Integer bodyMaterialId;
    @ApiModelProperty("明细-物料编码")
    private String bodyMaterialNumber;
    @ApiModelProperty("明细-物料名称")
    private String bodyMaterialName;

    @ApiModelProperty("预期产量:各批号累计计划生产数量")
    private float prdQty;
    @ApiModelProperty("实际产量:报工/盘点结果表的产后报工数量")
    private float actualQty;
    @ApiModelProperty("未生产:预期产量-实际产量")
    private float unPrdQty;

    @ApiModelProperty("单位-编码")
    private String unit;
    @ApiModelProperty("单位-名称")
    private String unitStr;

    //原辅料盘点情况:统计汇总用料清单分项盘点数据
    @ApiModelProperty("原辅料盘点情况")
    List<OrderTransferRecordVo> orderTransferRecordVoList;

    @ApiModelProperty("移交时间")
    private String transferDate;
    @ApiModelProperty("盘点类型Code")
    private String recordTypePd;
    @ApiModelProperty("盘点类型name")
    private String recordTypePdName;

    @ApiModelProperty("移交人")
    private String handOverPersonName;

    @ApiModelProperty("接收人")
    private String personName;

}
