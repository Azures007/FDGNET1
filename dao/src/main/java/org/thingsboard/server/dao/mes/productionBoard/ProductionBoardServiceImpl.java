package org.thingsboard.server.dao.mes.productionBoard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.report.TimeDimensionUtils;
import org.thingsboard.server.dao.mes.vo.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 生产看板服务实现（Mock数据）
 */
@Service
public class ProductionBoardServiceImpl implements ProductionBoardService {

    @Override
    public ProductionStatistics getProductionStatistics(String productionLine, String timeDimension, 
                                                       String startDate, String endDate) {
        // 处理时间范围
        String finalStartDate = startDate;
        String finalEndDate = endDate;
        
        // 如果传了时间维度，使用TimeDimensionUtils转换
        if (timeDimension != null && !timeDimension.trim().isEmpty()) {
            TimeDimensionUtils.TimeRange timeRange = TimeDimensionUtils.getTimeRange(timeDimension);
            finalStartDate = timeRange.getStartDate();
            finalEndDate = timeRange.getEndDate();
        }
        
        // Mock数据
        ProductionStatistics stats = new ProductionStatistics();
        
        // 订单数量
        stats.setOrderCount(8);
        stats.setOrderCountUnit("个");
        
        // 计划生产数量
        stats.setPlanProductionQuantity(new BigDecimal("3000"));
        stats.setPlanProductionQuantityUnit("件");
        
        // 计划原辅料重量
        stats.setPlanMaterialWeight(new BigDecimal("8000"));
        stats.setPlanMaterialWeightUnit("kg");
        
        // 原辅料废品占比
        stats.setDefectiveQuantity(new BigDecimal("100"));
        stats.setDefectiveQuantityUnit("kg");
        stats.setWasteQuantity(new BigDecimal("80"));
        stats.setWasteQuantityUnit("kg");
        //stats.setDefectiveRatio(new BigDecimal("0.0125")); // 100/8000 = 1.25%
        //stats.setWasteRatio(new BigDecimal("0.01")); // 80/8000 = 1%
        
        // 包材重量
        stats.setPackagingWeight(new BigDecimal("1000"));
        stats.setPackagingWeightUnit("kg");
        
        // 包材废品重量
        stats.setPackagingBadProductWeight(new BigDecimal("80"));
        stats.setPackagingBadProductWeightUnit("kg");
        
        // 可以根据finalStartDate和finalEndDate查询实际数据
        // TODO: 实际实现时，使用finalStartDate和finalEndDate查询数据库
        
        return stats;
    }

    @Override
    public List<OrderPlanAnalysis> getOrderPlanAnalysis(String productionLine, String dateType) {
        String finalStartDate;
        String finalEndDate;
        if (dateType != null && !dateType.trim().isEmpty()) {
            TimeDimensionUtils.TimeRange timeRange = TimeDimensionUtils.getTimeRange(dateType);
            finalStartDate = timeRange.getStartDate();
            finalEndDate = timeRange.getEndDate();
        }
        // TODO: 实际实现时，使用finalStartDate和finalEndDate查询数据库
        // Mock数据 - 5个月的数据
        List<OrderPlanAnalysis> list = new ArrayList<>();


        list.add(OrderPlanAnalysis.builder()
                .timeX("1月")
                .planQuantity(new BigDecimal("100"))
                .completedQuantity(new BigDecimal("80"))
                .achievementRate(new BigDecimal("0.80"))
                .build());
        list.add(OrderPlanAnalysis.builder()
                .timeX("2月")
                .planQuantity(new BigDecimal("150"))
                .completedQuantity(new BigDecimal("120"))
                .achievementRate(new BigDecimal("0.80"))
                .build());
        list.add(OrderPlanAnalysis.builder()
                .timeX("3月")
                .planQuantity(new BigDecimal("220"))
                .completedQuantity(new BigDecimal("200"))
                .achievementRate(new BigDecimal("0.91"))
                .build());
        list.add(OrderPlanAnalysis.builder()
                .timeX("4月")
                .planQuantity(new BigDecimal("180"))
                .completedQuantity(new BigDecimal("150"))
                .achievementRate(new BigDecimal("0.83"))
                .build());
        list.add(OrderPlanAnalysis.builder()
                .timeX("5月")
                .planQuantity(new BigDecimal("160"))
                .completedQuantity(new BigDecimal("140"))
                .achievementRate(new BigDecimal("0.88"))
                .build());
        return list;
    }

    @Override
    public List<WasteOutputAnalysis> getWasteOutputAnalysis(String productionLine, String dateType) {
        String finalStartDate;
        String finalEndDate;
        if (dateType != null && !dateType.trim().isEmpty()) {
            TimeDimensionUtils.TimeRange timeRange = TimeDimensionUtils.getTimeRange(dateType);
            finalStartDate = timeRange.getStartDate();
            finalEndDate = timeRange.getEndDate();
        }
        // TODO: 实际实现时，使用finalStartDate和finalEndDate查询数据库
        // Mock数据 - 5个月的废料数据
        List<WasteOutputAnalysis> list = new ArrayList<>();
        list.add(new WasteOutputAnalysis("1月", new BigDecimal("100"), new BigDecimal("80"), new BigDecimal("50"), new BigDecimal("0.15")));
        list.add(new WasteOutputAnalysis("2月", new BigDecimal("120"), new BigDecimal("90"), new BigDecimal("60"), new BigDecimal("0.18")));
        list.add(new WasteOutputAnalysis("3月", new BigDecimal("150"), new BigDecimal("120"), new BigDecimal("80"), new BigDecimal("0.20")));
        list.add(new WasteOutputAnalysis("4月", new BigDecimal("180"), new BigDecimal("150"), new BigDecimal("100"), new BigDecimal("0.22")));
        list.add(new WasteOutputAnalysis("5月", new BigDecimal("200"), new BigDecimal("160"), new BigDecimal("120"), new BigDecimal("0.25")));
        return list;
    }

    @Override
    public OrderProgressPageVo getOrderProgress(String productionLine, Integer current, Integer size) {
        // Mock数据 - 订单进度列表（完整数据）
        List<OrderProgress> allOrders = new ArrayList<>();
        

        allOrders.add(OrderProgress.builder()
                .orderNo("55A22026010500130220-0.04")
                .productName("3kg袋装花椒酱")
                .unit("件")
                .planQuantity(new BigDecimal("460"))
                .completedQuantity(new BigDecimal("270"))
                .progressPercentage(new BigDecimal("59"))
                .build());
        allOrders.add(OrderProgress.builder()
                .orderNo("55A22026010500130220-0.03")
                .productName("3kg袋装花椒酱")
                .unit("件")
                .planQuantity(new BigDecimal("460"))
                .completedQuantity(new BigDecimal("530"))
                .progressPercentage(new BigDecimal("115"))
                .build());
        allOrders.add(OrderProgress.builder()
                .orderNo("55A22026010500130220-0.02")
                .productName("3kg袋装花椒酱")
                .unit("件")
                .planQuantity(new BigDecimal("460"))
                .completedQuantity(new BigDecimal("0"))
                .progressPercentage(new BigDecimal("0"))
                .build());
        allOrders.add(OrderProgress.builder()
                .orderNo("55A22026010500130220-0.01")
                .productName("3kg袋装花椒酱")
                .unit("件")
                .planQuantity(new BigDecimal("450"))
                .completedQuantity(new BigDecimal("270"))
                .progressPercentage(new BigDecimal("60"))
                .build());
        allOrders.add(OrderProgress.builder()
                .orderNo("55A22026010500130220-0.00")
                .productName("3kg袋装花椒酱")
                .unit("件")
                .planQuantity(new BigDecimal("460"))
                .completedQuantity(new BigDecimal("270"))
                .progressPercentage(new BigDecimal("59"))
                .build());
        allOrders.add(OrderProgress.builder()
                .orderNo("55A22026010500130220-0.11")
                .productName("3kg袋装花椒酱")
                .unit("件")
                .planQuantity(new BigDecimal("460"))
                .completedQuantity(new BigDecimal("270"))
                .progressPercentage(new BigDecimal("59"))
                .build());
        allOrders.add(OrderProgress.builder()
                .orderNo("55A22026010500130220-0.12")
                .productName("3kg袋装花椒酱")
                .unit("件")
                .planQuantity(new BigDecimal("460"))
                .completedQuantity(new BigDecimal("270"))
                .progressPercentage(new BigDecimal("59"))
                .build());
        allOrders.add(OrderProgress.builder()
                .orderNo("55A22026010500130220-0.13")
                .productName("3kg袋装花椒酱")
                .unit("件")
                .planQuantity(new BigDecimal("460"))
                .completedQuantity(new BigDecimal("136"))
                .progressPercentage(new BigDecimal("30"))
                .build());
        
        // 计算统计信息
        int orderCount = allOrders.size();
        long unfinishedCount = 3;
        
        // 分页处理
        Pageable pageable = PageRequest.of(current, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allOrders.size());
        
        List<OrderProgress> pageContent = allOrders.subList(start, end);
        Page<OrderProgress> page = new PageImpl<>(pageContent, pageable, allOrders.size());
        
        // 构建响应
        OrderProgressPageVo response = new OrderProgressPageVo(page, orderCount, (int) unfinishedCount);
        
        return response;
    }

    @Override
    public PageVo<ProductionBgVo> getProductionBG(String productionLine, Integer current, Integer size) {
        // Mock数据 - 生产报工数据列表（完整数据）
        List<ProductionBgVo> allTasks = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        
        try {
            // 第1条：泉州3#3接B线B班 - 配料-面团 - 白砂糖
            allTasks.add(ProductionBgVo.builder()
                .productionLine("泉州3#3接B线B班")
                .process("配料-面团")
                .materialName("白砂糖")
                .standard("13.1-13.3")
                .recordQuantity(new BigDecimal("13.1"))
                .potStr("第1锅")
                .recordTime(sdf.parse("2025.12.19 00:00"))
                .build());
            
            // 第2条：泉州3#3接B线B班 - 配料-面团 - 中筋粉01（红色异常数据）
            allTasks.add(ProductionBgVo.builder()
                .productionLine("泉州3#3接B线B班")
                .process("配料-面团")
                .materialName("中筋粉01")
                .standard("24.9-25.1")
                .recordQuantity(new BigDecimal("26"))
                .potStr("第1锅")
                .recordTime(sdf.parse("2025.12.19 00:00"))
                .build());
            
            // 第3条：泉州3#3接B线B班 - 配料-面团 - 中筋粉02（红色异常数据）
            allTasks.add(ProductionBgVo.builder()
                .productionLine("泉州3#3接B线B班")
                .process("配料-面团")
                .materialName("中筋粉02")
                .standard("24.9-25.1")
                .recordQuantity(new BigDecimal("26"))
                .potStr("第1锅")
                .recordTime(sdf.parse("2025.12.19 00:00"))
                .build());
            
            // 第4条：泉州3#3接B线B班 - 成型 - 面团
            allTasks.add(ProductionBgVo.builder()
                .productionLine("泉州3#3接B线B班")
                .process("成型")
                .materialName("面团")
                .standard("4.49-4.52")
                .recordQuantity(new BigDecimal("4.501"))
                .potStr("")
                .recordTime(sdf.parse("2025.12.19 00:00"))
                .build());
            
            // 第5条：泉州3#3接B线B班 - 配料-酥 - 散装大豆油
            allTasks.add(ProductionBgVo.builder()
                .productionLine("泉州3#3接B线B班")
                .process("配料-酥")
                .materialName("散装大豆油")
                .standard("26.495-26.51")
                .recordQuantity(new BigDecimal("26.5"))
                .potStr("第1锅")
                .recordTime(sdf.parse("2025.12.19 00:00"))
                .build());
            
            // 第6条：泉州3#3接B线B班 - 配料-馅料 - 调配油
            allTasks.add(ProductionBgVo.builder()
                .productionLine("泉州3#3接B线B班")
                .process("配料-馅料")
                .materialName("调配油")
                .standard("2.49-2.51")
                .recordQuantity(new BigDecimal("2.5"))
                .potStr("第2锅")
                .recordTime(sdf.parse("2025.12.19 00:00"))
                .build());
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        // 分页处理
        Pageable pageable = PageRequest.of(current, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allTasks.size());
        
        List<ProductionBgVo> pageContent = allTasks.subList(start, end);
        Page<ProductionBgVo> page = new PageImpl<>(pageContent, pageable, allTasks.size());
        
        // 构建响应
        return new PageVo<>(page);
    }

    @Override
    public PageVo<OutsourcingNetContent> getOutsourcingNetContent(String productionLine, Integer current, Integer size) {
        // Mock数据 - 外包净含量实况（完整数据）
        List<OutsourcingNetContent> allContents = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        
        try {
            // 第1条：泉州3#3接B线B班 - 2.5kg*1 友臣肉松饼(原味)
            allContents.add(new OutsourcingNetContent(
                "泉州3#3接B线B班", 
                "2.5kg*1 友臣肉松饼(原味)", 
                "2500-2535",
                new BigDecimal("2535"),
                new BigDecimal("2500"),
                new BigDecimal("2532"), 
                sdf.parse("2025.12.19 00:00")
            ));
            
            // 第2条：泉州3#3接B线B班 - 2.5kg*1 友臣肉松饼(原味)（红色异常数据）
            allContents.add(new OutsourcingNetContent(
                "泉州3#3接B线B班", 
                "2.5kg*1 友臣肉松饼(原味)", 
                "2500-2535",
                new BigDecimal("2535"),
                new BigDecimal("2500"),
                new BigDecimal("2555"), 
                sdf.parse("2025.12.19 00:00")
            ));
            
            // 第3条：泉州3#3接B线B班 - 2.5kg*1 友臣肉松饼(原味)（红色异常数据）
            allContents.add(new OutsourcingNetContent(
                "泉州3#3接B线B班", 
                "2.5kg*1 友臣肉松饼(原味)", 
                "2500-2535",
                new BigDecimal("2535"),
                new BigDecimal("2500"),
                new BigDecimal("2550"), 
                sdf.parse("2025.12.19 00:00")
            ));
            
            // 第4条：泉州3#3接B线B班 - 1kg友臣原味肉松饼电商版
            allContents.add(new OutsourcingNetContent(
                "泉州3#3接B线B班", 
                "1kg友臣原味肉松饼电商版", 
                "1017-1052",
                new BigDecimal("1052"),
                new BigDecimal("1017"),
                new BigDecimal("1050"), 
                sdf.parse("2025.12.19 00:00")
            ));
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        // 分页处理
        Pageable pageable = PageRequest.of(current, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allContents.size());
        
        List<OutsourcingNetContent> pageContent = allContents.subList(start, end);
        Page<OutsourcingNetContent> page = new PageImpl<>(pageContent, pageable, allContents.size());
        
        // 构建响应
        return new PageVo<>(page);
    }

    @Override
    public ProductionBoardData getAllProductionBoardData(String productionLine, String date, String dateType) {
        ProductionBoardData data = new ProductionBoardData();
        data.setStatistics(getProductionStatistics(productionLine, date, null, null));
        data.setOrderPlanAnalysis(getOrderPlanAnalysis(productionLine, dateType));
        data.setWasteOutputAnalysis(getWasteOutputAnalysis(productionLine, dateType));
        data.setOrderProgressPageVo(getOrderProgress(productionLine, 0, 10));
        data.setProductionLineTasksPage(getProductionBG(productionLine, 0, 10));
        data.setOutsourcingNetContentPage(getOutsourcingNetContent(productionLine, 0, 10));
        return data;
    }
}
