package org.thingsboard.server.dao.mes.productionBoard;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.report.TimeDimensionUtils;
import org.thingsboard.server.dao.mes.vo.*;
import org.thingsboard.server.dao.sql.mes.productionBoard.ProductionBoardRepository;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产看板服务实现（Mock数据）
 */
@Service
public class ProductionBoardServiceImpl implements ProductionBoardService {

    @Autowired
    private ProductionBoardRepository productionBoardRepository;

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
        
        ProductionStatistics stats = new ProductionStatistics();
        
        try {
            // 将字符串日期转换为Date对象
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDateTime = sdf.parse(finalStartDate);
            Date endDateTime = sdf.parse(finalEndDate);
            
            // 设置时间为当天的开始和结束
            SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startDateTime = fullSdf.parse(finalStartDate + " 00:00:00");
            endDateTime = fullSdf.parse(finalEndDate + " 23:59:59");
            
            // 使用合并查询获取订单数量和计划生产数量
            List<Map> resultList;
            BigDecimal planMaterialWeight;
            BigDecimal packagingWeight;
            
            if (productionLine != null && !productionLine.trim().isEmpty()) {
                // 如果传了生产线,按生产线查询
                resultList = productionBoardRepository.getOrderStatisticsByDateRangeAndProductionLine(
                    startDateTime, endDateTime, productionLine);
                planMaterialWeight = productionBoardRepository.sumPlanMaterialWeightByDateRangeAndProductionLine(
                    startDateTime, endDateTime, productionLine);
                packagingWeight = productionBoardRepository.sumPackagingWeightByDateRangeAndProductionLine(
                    startDateTime, endDateTime, productionLine);
            } else {
                // 否则查询所有
                resultList = productionBoardRepository.getOrderStatisticsByDateRange(
                    startDateTime, endDateTime);
                planMaterialWeight = productionBoardRepository.sumPlanMaterialWeightByDateRange(
                    startDateTime, endDateTime);
                packagingWeight = productionBoardRepository.sumPackagingWeightByDateRange(
                    startDateTime, endDateTime);
            }
            
            // 解析查询结果
            Long orderCount = 0L;
            BigDecimal planProductionQuantity = BigDecimal.ZERO;
            
            if (resultList != null && !resultList.isEmpty()) {
                Map row = resultList.get(0);
                Object orderCountObj = row.get("ordercount");
                Object planQtyObj = row.get("planproductionquantity");
                
                orderCount = orderCountObj != null ? ((Number) orderCountObj).longValue() : 0L;
                planProductionQuantity = planQtyObj != null ? 
                    new BigDecimal(planQtyObj.toString()) : BigDecimal.ZERO;
            }
            
            // 订单数量
            stats.setOrderCount(orderCount.intValue());
            stats.setOrderCountUnit("个");
            
            // 计划生产数量（保留3位小数）
            stats.setPlanProductionQuantity(planProductionQuantity.setScale(3, BigDecimal.ROUND_HALF_UP));
            stats.setPlanProductionQuantityUnit("件");
            
            // 计划原辅料重量（保留3位小数）
            BigDecimal materialWeight = planMaterialWeight != null ? planMaterialWeight : BigDecimal.ZERO;
            stats.setPlanMaterialWeight(materialWeight.setScale(3, BigDecimal.ROUND_HALF_UP));
            stats.setPlanMaterialWeightUnit("kg");
            
            // 包材重量（保留3位小数）
            BigDecimal pkgWeight = packagingWeight != null ? packagingWeight : BigDecimal.ZERO;
            stats.setPackagingWeight(pkgWeight.setScale(3, BigDecimal.ROUND_HALF_UP));
            stats.setPackagingWeightUnit("kg");
            
        } catch (Exception e) {
            e.printStackTrace();
            // 查询失败时返回0
            stats.setOrderCount(0);
            stats.setOrderCountUnit("个");
            stats.setPlanProductionQuantity(BigDecimal.ZERO);
            stats.setPlanProductionQuantityUnit("件");
            stats.setPlanMaterialWeight(BigDecimal.ZERO);
            stats.setPlanMaterialWeightUnit("kg");
            stats.setPackagingWeight(BigDecimal.ZERO);
            stats.setPackagingWeightUnit("kg");
        }
        
        // 其他字段暂时使用Mock数据
        // TODO: 后续根据实际业务需求实现其他字段的查询逻辑
        
        // 原辅料废品占比
        stats.setDefectiveQuantity(new BigDecimal("100"));
        stats.setDefectiveQuantityUnit("kg");
        stats.setWasteQuantity(new BigDecimal("80"));
        stats.setWasteQuantityUnit("kg");
        //stats.setDefectiveRatio(new BigDecimal("0.0125")); // 100/8000 = 1.25%
        //stats.setWasteRatio(new BigDecimal("0.01")); // 80/8000 = 1%
        
        // 包材废品重量
        stats.setPackagingBadProductWeight(new BigDecimal("80"));
        stats.setPackagingBadProductWeightUnit("kg");
        
        return stats;
    }

    @Override
    public List<OrderPlanAnalysis> getOrderPlanAnalysis(String productionLine, String dateType,String startDate, String endDate) {
        String finalStartDate;
        String finalEndDate;
        
        // 解析时间维度
        if (dateType != null && !dateType.trim().isEmpty()) {
            TimeDimensionUtils.TimeRange timeRange = TimeDimensionUtils.getTimeRange(dateType);
            finalStartDate = timeRange.getStartDate();
            finalEndDate = timeRange.getEndDate();
        } else {
            // 默认查询最近30天
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            finalEndDate = sdf.format(new Date());
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
            finalStartDate = sdf.format(cal.getTime());
        }
        if (startDate != null && !startDate.trim().isEmpty()&&endDate != null && !endDate.trim().isEmpty()) {
            finalStartDate = startDate;
            finalEndDate = endDate;
        }
        try {
            // 将字符串日期转换为Date对象
            SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDateTime = fullSdf.parse(finalStartDate + " 00:00:00");
            Date endDateTime = fullSdf.parse(finalEndDate + " 23:59:59");
            
            // 判断时间维度：如果是"近5日"或包含"日"，按日分组；否则按月分组
            boolean groupByDay = dateType != null && (dateType.contains("日") || dateType.equals("TODAY") || dateType.equals("YESTERDAY"));
            
            List<Map> results;
            if (groupByDay) {
                // 按日分组查询
                results = productionBoardRepository.findOrderPlanAnalysisByDay(
                    startDateTime, endDateTime, productionLine);
            } else {
                // 按月分组查询
                results = productionBoardRepository.findOrderPlanAnalysisByMonth(
                    startDateTime, endDateTime, productionLine);
            }
            
            // 将查询结果转换为Map，key为时间，value为数据
            java.util.Map<String, OrderPlanAnalysis> dataMap = new java.util.HashMap<>();
            if (results != null && !results.isEmpty()) {
                for (Map row : results) {
                    String timeX = (String) row.get("timex");
                    BigDecimal planQuantity = row.get("planquantity") != null ? 
                        new BigDecimal(row.get("planquantity").toString()) : BigDecimal.ZERO;
                    BigDecimal completedQuantity = row.get("completedquantity") != null ? 
                        new BigDecimal(row.get("completedquantity").toString()) : BigDecimal.ZERO;
                    
                    // 计算达成率
                    BigDecimal achievementRate = BigDecimal.ZERO;
                    if (planQuantity.compareTo(BigDecimal.ZERO) > 0) {
                        achievementRate = completedQuantity.divide(planQuantity, 4, BigDecimal.ROUND_HALF_UP);
                    }
                    
                    OrderPlanAnalysis analysis = OrderPlanAnalysis.builder()
                        .timeX(timeX)
                        .planQuantity(planQuantity)
                        .completedQuantity(completedQuantity)
                        .achievementRate(achievementRate)
                        .build();
                    
                    dataMap.put(timeX, analysis);
                }
            }
            
            // 生成完整的时间轴数据
            List<OrderPlanAnalysis> list = new ArrayList<>();
            
            if (groupByDay) {
                // 按日生成时间轴
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(startDateTime);
                
                while (!cal.getTime().after(endDateTime)) {
                    String timeKey = sdf.format(cal.getTime());
                    
                    // 如果有数据则使用查询到的数据，否则使用0
                    OrderPlanAnalysis analysis = dataMap.get(timeKey);
                    if (analysis != null) {
                        // 格式化时间显示
                        analysis.setTimeX(timeKey);
                        list.add(analysis);
                    } else {
                        // 没有数据，返回0
                        analysis = OrderPlanAnalysis.builder()
                            .timeX(timeKey)
                            .planQuantity(BigDecimal.ZERO)
                            .completedQuantity(BigDecimal.ZERO)
                            .achievementRate(BigDecimal.ZERO)
                            .build();
                        list.add(analysis);
                    }
                    
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                }
            } else {
                // 按月生成时间轴
                SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM");
                SimpleDateFormat displayFormat = new SimpleDateFormat("M");
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(startDateTime);
                
                while (!cal.getTime().after(endDateTime)) {
                    String timeKey = keyFormat.format(cal.getTime());
                    String displayTime = displayFormat.format(cal.getTime()) + "月";
                    
                    // 如果有数据则使用查询到的数据，否则使用0
                    OrderPlanAnalysis analysis = dataMap.get(timeKey);
                    if (analysis != null) {
                        // 格式化时间显示
                        analysis.setTimeX(displayTime);
                        list.add(analysis);
                    } else {
                        // 没有数据，返回0
                        analysis = OrderPlanAnalysis.builder()
                            .timeX(displayTime)
                            .planQuantity(BigDecimal.ZERO)
                            .completedQuantity(BigDecimal.ZERO)
                            .achievementRate(BigDecimal.ZERO)
                            .build();
                        list.add(analysis);
                    }
                    
                    cal.add(java.util.Calendar.MONTH, 1);
                }
            }
            
            return list;
            
        } catch (Exception e) {
            e.printStackTrace();
            // 查询失败时返回空列表
            return new ArrayList<>();
        }
    }

    @Override
    public List<WasteOutputAnalysis> getWasteOutputAnalysis(String productionLine, String dateType, String startDate, String endDate) {
        String finalStartDate;
        String finalEndDate;
        
        // 优先使用传入的日期范围
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            finalStartDate = startDate;
            finalEndDate = endDate;
        } else if (dateType != null && !dateType.trim().isEmpty()) {
            // 否则使用时间维度
            TimeDimensionUtils.TimeRange timeRange = TimeDimensionUtils.getTimeRange(dateType);
            finalStartDate = timeRange.getStartDate();
            finalEndDate = timeRange.getEndDate();
        } else {
            // 默认查询最近30天
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            finalEndDate = sdf.format(new Date());
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
            finalStartDate = sdf.format(cal.getTime());
        }
        
        // TODO: 实际实现时，使用finalStartDate和finalEndDate查询数据库
        // 根据dateType返回不同维度的Mock数据
        List<WasteOutputAnalysis> list = new ArrayList<>();
        
        // 判断时间维度
        boolean isDay = dateType != null && (dateType.contains("日") || dateType.equals("TODAY") || dateType.equals("YESTERDAY"));
        boolean isWeek = dateType != null && dateType.contains("周");
        
        if (isDay) {
            // 按日维度 - 返回最近5天的数据
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            java.util.Calendar cal = java.util.Calendar.getInstance();
            for (int i = 4; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(java.util.Calendar.DAY_OF_MONTH, -i);
                String dayLabel = sdf.format(cal.getTime());
                
                // 生成模拟数据（每天数据有波动）
                BigDecimal base = new BigDecimal(15 + i * 2);
                list.add(new WasteOutputAnalysis(
                    dayLabel,
                    base.add(new BigDecimal("5")),  // 次品重量
                    base.add(new BigDecimal("3")),  // 废料重量
                    base,                            // 包材废品重量
                    new BigDecimal("0.12").add(new BigDecimal(i * 0.01))  // 废料率
                ));
            }
        } else if (isWeek) {
            // 按周维度 - 返回最近5周的数据
            java.util.Calendar cal = java.util.Calendar.getInstance();
            for (int i = 4; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(java.util.Calendar.WEEK_OF_YEAR, -i);
                
                // 获取该周的周数
                int weekOfYear = cal.get(java.util.Calendar.WEEK_OF_YEAR);
                String weekLabel = "第" + weekOfYear + "周";
                
                // 生成模拟数据（每周数据有波动）
                BigDecimal base = new BigDecimal(80 + i * 10);
                list.add(new WasteOutputAnalysis(
                    weekLabel,
                    base.add(new BigDecimal("20")),  // 次品重量
                    base.add(new BigDecimal("15")),  // 废料重量
                    base,                             // 包材废品重量
                    new BigDecimal("0.15").add(new BigDecimal(i * 0.01))  // 废料率
                ));
            }
        } else {
            // 按月维度 - 返回最近5个月的数据（默认）
            SimpleDateFormat sdf = new SimpleDateFormat("M");
            java.util.Calendar cal = java.util.Calendar.getInstance();
            for (int i = 4; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(java.util.Calendar.MONTH, -i);
                String monthLabel = sdf.format(cal.getTime()) + "月";
                
                // 生成模拟数据（每月数据递增）
                BigDecimal base = new BigDecimal(100 + (4 - i) * 20);
                list.add(new WasteOutputAnalysis(
                    monthLabel,
                    base.add(new BigDecimal("30")),  // 次品重量
                    base.add(new BigDecimal("20")),  // 废料重量
                    base,                             // 包材废品重量
                    new BigDecimal("0.15").add(new BigDecimal((4 - i) * 0.02))  // 废料率
                ));
            }
        }
        
        return list;
    }

    @Override
    public OrderProgressPageVo getOrderProgress(String productionLine, Integer current, Integer size, String startDate, String endDate) {
        // 使用传入的日期范围，如果没有传则使用默认范围
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String finalStartDate;
        String finalEndDate;
        
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            finalStartDate = startDate;
            finalEndDate = endDate;
        } else {
            // 默认查询最近30天
            finalEndDate = sdf.format(new Date());
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
            finalStartDate = sdf.format(cal.getTime());
        }
        
        try {
            // 设置查询时间范围
            SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDateTime = fullSdf.parse(finalStartDate + " 00:00:00");
            Date endDateTime = fullSdf.parse(finalEndDate + " 23:59:59");
            
            // 创建分页参数
            Pageable pageable = PageRequest.of(current, size);
            
            // 查询订单进度数据（返回Page<Map>）
            Page<Map> select = productionBoardRepository.findOrderProgressByDateRangeAndOrg(
                startDateTime, 
                endDateTime, 
                productionLine,
                pageable
            );
            
            // 使用JSON转换为OrderProgress对象列表
            List<OrderProgress> castEntity = JSON.parseArray(JSON.toJSONString(select.getContent()), OrderProgress.class);
            
            // 计算进度百分比
            for (OrderProgress order : castEntity) {
                if (order.getPlanQuantity() != null && order.getPlanQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal completedQty = order.getCompletedQuantity() != null ? order.getCompletedQuantity() : BigDecimal.ZERO;
                    BigDecimal progressPercentage = completedQty.divide(order.getPlanQuantity(), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(0, BigDecimal.ROUND_HALF_UP);
                    order.setProgressPercentage(progressPercentage);
                } else {
                    order.setProgressPercentage(BigDecimal.ZERO);
                }
            }
            
            // 获取订单总数
            int orderCount = (int) select.getTotalElements();
            
            // 通过SQL查询未完成订单数（效率更高）
            Long unfinishedCount = productionBoardRepository.countUnfinishedOrders(
                startDateTime, 
                endDateTime, 
                productionLine
            );
            
            // 构建响应
            Page<OrderProgress> page = new PageImpl<>(castEntity, pageable, orderCount);
            return new OrderProgressPageVo(page, orderCount, unfinishedCount != null ? unfinishedCount.intValue() : 0);
            
        } catch (Exception e) {
            e.printStackTrace();
            // 查询失败时返回空数据
            Page<OrderProgress> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(current, size), 0);
            return new OrderProgressPageVo(emptyPage, 0, 0);
        }
    }

    @Override
    public PageVo<ProductionBgVo> getProductionBG(String productionLine, Integer current, Integer size, String startDate, String endDate) {
        try {
            // 处理日期范围
            Date startDateTime = null;
            Date endDateTime = null;
            
            if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
                SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startDateTime = fullSdf.parse(startDate + " 00:00:00");
                endDateTime = fullSdf.parse(endDate + " 23:59:59");
            }
            
            // 创建分页参数
            Pageable pageable = PageRequest.of(current, size);
            
            // 查询生产报工数据（返回Page<Map>）
            Page<Map> select;
            if (startDateTime != null && endDateTime != null) {
                select = productionBoardRepository.findProductionBgDataByDateRange(
                    productionLine,
                    startDateTime,
                    endDateTime,
                    pageable
                );
            } else {
                select = productionBoardRepository.findProductionBgData(
                    productionLine,
                    pageable
                );
            }
            
            // 使用JSON转换为ProductionBgVo对象列表
            List<ProductionBgVo> castEntity = JSON.parseArray(JSON.toJSONString(select.getContent()), ProductionBgVo.class);
            
            // 获取总数
            int total = (int) select.getTotalElements();
            
            // 构建响应
            PageVo<ProductionBgVo> pageVo = new PageVo<>(size, current);
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            
            return pageVo;
            
        } catch (Exception e) {
            e.printStackTrace();
            // 查询失败时返回空数据
            PageVo<ProductionBgVo> emptyPageVo = new PageVo<>(size, current);
            emptyPageVo.setTotal(0);
            emptyPageVo.setList(new ArrayList<>());
            return emptyPageVo;
        }
    }

    @Override
    public PageVo<OutsourcingNetContent> getOutsourcingNetContent(String productionLine, Integer current, Integer size, String startDate, String endDate) {
        try {
            // 处理日期范围
            Date startDateTime = null;
            Date endDateTime = null;
            
            if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
                SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startDateTime = fullSdf.parse(startDate + " 00:00:00");
                endDateTime = fullSdf.parse(endDate + " 23:59:59");
            }
            
            // 创建分页参数
            Pageable pageable = PageRequest.of(current, size);
            
            // 查询外包净含量数据（返回Page<Map>）
            Page<Map> select;
            if (startDateTime != null && endDateTime != null) {
                select = productionBoardRepository.findOutsourcingNetContentDataByDateRange(
                    productionLine,
                    startDateTime,
                    endDateTime,
                    pageable
                );
            } else {
                select = productionBoardRepository.findOutsourcingNetContentData(
                    productionLine,
                    pageable
                );
            }
            
            // 使用JSON转换为OutsourcingNetContent对象列表
            List<OutsourcingNetContent> castEntity = JSON.parseArray(JSON.toJSONString(select.getContent()), OutsourcingNetContent.class);
            
            // 获取总数
            int total = (int) select.getTotalElements();
            
            // 构建响应
            PageVo<OutsourcingNetContent> pageVo = new PageVo<>(size, current);
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            
            return pageVo;
            
        } catch (Exception e) {
            e.printStackTrace();
            // 查询失败时返回空数据
            PageVo<OutsourcingNetContent> emptyPageVo = new PageVo<>(size, current);
            emptyPageVo.setTotal(0);
            emptyPageVo.setList(new ArrayList<>());
            return emptyPageVo;
        }
    }

    /*@Override
    public ProductionBoardData getAllProductionBoardData(String productionLine, String date, String dateType) {
        ProductionBoardData data = new ProductionBoardData();
        data.setStatistics(getProductionStatistics(productionLine, date, null, null));
        data.setOrderPlanAnalysis(getOrderPlanAnalysis(productionLine, dateType));
        data.setWasteOutputAnalysis(getWasteOutputAnalysis(productionLine, dateType));
        data.setOrderProgressPageVo(getOrderProgress(productionLine, 0, 10));
        data.setProductionLineTasksPage(getProductionBG(productionLine, 0, 10));
        data.setOutsourcingNetContentPage(getOutsourcingNetContent(productionLine, 0, 10));
        return data;
    }*/
}
