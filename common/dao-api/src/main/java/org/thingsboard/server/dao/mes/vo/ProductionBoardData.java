package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 生产看板完整数据（可选：用于一次性返回所有数据）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("生产看板完整数据")
public class ProductionBoardData {

    @ApiModelProperty("生产统计数据")
    private ProductionStatistics statistics;

    @ApiModelProperty("订单计划达成率分析")
    private List<OrderPlanAnalysis> orderPlanAnalysis;

    @ApiModelProperty("订单废料产出分析")
    private List<WasteOutputAnalysis> wasteOutputAnalysis;

    @ApiModelProperty("订单进度完成情况")
    private OrderProgressPageVo orderProgressPageVo;

    @ApiModelProperty("生产线任务列表")
    private PageVo<ProductionBgVo> productionLineTasksPage;

    @ApiModelProperty("外包净含量实况")
    private PageVo<OutsourcingNetContent> outsourcingNetContentPage;
}
