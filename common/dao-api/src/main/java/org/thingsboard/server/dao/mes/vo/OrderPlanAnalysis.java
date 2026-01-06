package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单计划达成率分析数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("订单计划达成率分析")
public class OrderPlanAnalysis {

    @ApiModelProperty("时间")
    private String timeX;

    @ApiModelProperty("计划量")
    private BigDecimal planQuantity;

    @ApiModelProperty("完成量")
    private BigDecimal completedQuantity;

    @ApiModelProperty("计划达成率(%)")
    private BigDecimal achievementRate;
}
