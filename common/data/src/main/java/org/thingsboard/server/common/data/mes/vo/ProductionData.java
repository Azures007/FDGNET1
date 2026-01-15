package org.thingsboard.server.common.data.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 投入产出比报表主实体类
 */
@Data
@ApiModel(value = "ProductionData", description = "投入产出比报表主实体")
public class ProductionData {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "产线")
    private String productionLine;

    @ApiModelProperty(value = "计划产量")
    private String plannedOutput;

    @ApiModelProperty(value = "实际产量")
    private String actualOutput;

    @ApiModelProperty(value = "净含量重量")
    private String netContentWeight;

    @ApiModelProperty(value = "包膜废品重量")
    private String packagingWasteWeight;

    @ApiModelProperty(value = "废次品重量")
    private String defectiveWeight;

    @ApiModelProperty(value = "投入产出比")
    private String inputOutputRatio;

    @ApiModelProperty(value = "单箱原辅料消耗")
    private String materialConsumptionPerBox;

    @ApiModelProperty(value = "废次品比率")
    private String defectiveRate;

    @ApiModelProperty(value = "投入信息列表")
    private List<InputMaterialItem> inputMaterialItems;
}