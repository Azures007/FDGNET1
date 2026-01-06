package org.thingsboard.server.dao.mes.productionBoard;

import org.thingsboard.server.dao.mes.vo.*;

import java.util.List;

/**
 * 生产看板服务接口
 */
public interface ProductionBoardService {

    /**
     * 获取生产统计数据
     */
    ProductionStatistics getProductionStatistics(String productionLine, String timeDimension, 
                                                 String startDate, String endDate);

    /**
     * 获取订单计划达成率分析数据
     */
    List<OrderPlanAnalysis> getOrderPlanAnalysis(String productionLine, String dateType);

    /**
     * 获取订单废料产出分析数据
     */
    List<WasteOutputAnalysis> getWasteOutputAnalysis(String productionLine, String dateType);

    /**
     * 获取订单进度完成情况
     */
    OrderProgressPageVo getOrderProgress(String productionLine, Integer current, Integer size);

    /**
     * 获取报工列表
     */
    PageVo<ProductionBgVo> getProductionBG(String productionLine, Integer current, Integer size);

    /**
     * 获取外包净含量实况
     */
    PageVo<OutsourcingNetContent> getOutsourcingNetContent(String productionLine, Integer current, Integer size);

    /**
     * 获取完整的生产看板数据（可选）
     */
    ProductionBoardData getAllProductionBoardData(String productionLine, String date, String dateType);
}
