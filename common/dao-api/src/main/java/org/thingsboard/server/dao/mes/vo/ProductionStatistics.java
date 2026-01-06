package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 生产统计数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("生产统计数据")
public class ProductionStatistics {

    @ApiModelProperty("订单数量")
    private Integer orderCount;

    @ApiModelProperty("订单数量单位")
    private String orderCountUnit;

    @ApiModelProperty("计划生产数量")
    private BigDecimal planProductionQuantity;

    @ApiModelProperty("计划生产数量单位")
    private String planProductionQuantityUnit;

    @ApiModelProperty("计划原辅料重量")
    private BigDecimal planMaterialWeight;

    @ApiModelProperty("计划原辅料重量单位")
    private String planMaterialWeightUnit;

    @ApiModelProperty("原辅料废品占比-次品数量")
    private BigDecimal defectiveQuantity;

    @ApiModelProperty("原辅料废品占比-次品数量单位")
    private String defectiveQuantityUnit;

    @ApiModelProperty("原辅料废品占比-废品数量")
    private BigDecimal wasteQuantity;

    @ApiModelProperty("原辅料废品占比-废品数量单位")
    private String wasteQuantityUnit;

/*    @ApiModelProperty("原辅料废品占比-次品占比")
    private BigDecimal defectiveRatio;

    @ApiModelProperty("原辅料废品占比-废品占比")
    private BigDecimal wasteRatio;*/

    @ApiModelProperty("包材重量")
    private BigDecimal packagingWeight;

    @ApiModelProperty("包材重量单位")
    private String packagingWeightUnit;

    @ApiModelProperty("包材废品重量")
    private BigDecimal packagingBadProductWeight;

    @ApiModelProperty("包材废品重量单位")
    private String packagingBadProductWeightUnit;
}
