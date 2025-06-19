package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单工序结果信息")
public class OrderProcessRecordDtlVo {

    @ApiModelProperty("班别名称")
    private String className;

    @ApiModelProperty("处理人(责任人)")
    private String personName;

    @ApiModelProperty("批次号")
    private String bodyLot;

    @ApiModelProperty("原辅料数量")
    private float rowQty;

    @ApiModelProperty("原辅料单位-编码")
    private String rowUnit;

    @ApiModelProperty("原辅料单位-名称")
    private String rowUnitStr;

    @ApiModelProperty("二级品数量")
    private float ungradedQty;

    @ApiModelProperty("二级品单位-编码")
    private String ungradedUnit;

    @ApiModelProperty("二级品单位-名称")
    private String ungradedUnitStr;

    @ApiModelProperty("产后数量")
    private float productQty;

    @ApiModelProperty("产后单位-编码")
    private String productUnit;

    @ApiModelProperty("产后单位-名称")
    private String productUnitStr;

    @ApiModelProperty("产能数量(产后支数用这个字段取值)")
    private float capacityQty;

    @ApiModelProperty("产能单位-编码")
    private String capacityUnit;

    @ApiModelProperty("产能单位-名称")
    private String capacityUnitStr;

    //特殊字段单独处理
    @ApiModelProperty("拉伸膜重量")
    private float reportLsmQty;
    @ApiModelProperty("拉伸膜重量单位-编码")
    private String reportLsmUnit;
    @ApiModelProperty("拉伸膜重量单位-名称")
    private String reportLsmUnitStr;
    @ApiModelProperty("废膜重量")
    private float reportFmQty;
    @ApiModelProperty("废膜重量单位-编码")
    private String reportFmUnit;
    @ApiModelProperty("废膜重量单位-名称")
    private String reportFmUnitStr;
    @ApiModelProperty("剩余膜重量")
    private float reportSymQty;
    @ApiModelProperty("剩余膜重量单位-编码")
    private String reportSymUnit;
    @ApiModelProperty("剩余膜重量单位-名称")
    private String reportSymUnitStr;
    @ApiModelProperty("投入锅数（斩拌）=原辅料累计投入重量合计/每锅重量")
    private Integer putInPotQty;

    @ApiModelProperty("产后数量（手动输入）")
    private float productManualQty;

    @ApiModelProperty("产后单位（手动输入）-编码")
    private String productManualUnit;

    @ApiModelProperty("产后单位（手动输入）-名称")
    private String productManualUnitStr;


}
