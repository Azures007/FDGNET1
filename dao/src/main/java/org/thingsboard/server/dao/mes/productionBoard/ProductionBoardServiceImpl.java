package org.thingsboard.server.dao.mes.productionBoard;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.report.TimeDimensionUtils;
import org.thingsboard.server.dao.constant.GlobalConstant;

import java.math.RoundingMode;
import java.util.Map;
import org.thingsboard.server.dao.mes.vo.*;
import org.thingsboard.server.dao.sql.mes.productionBoard.ProductionBoardRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialRepository;
import org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.dao.util.NcApiClient;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生产看板服务实现（Mock数据）
 */
@Slf4j
@Service
public class ProductionBoardServiceImpl implements ProductionBoardService {

    @Autowired
    private ProductionBoardRepository productionBoardRepository;
    
    @Autowired
    private TSysCodeDscRepository tSysCodeDscRepository;
    
    @Autowired
    private SyncMaterialRepository syncMaterialRepository;
    
    @Autowired
    private NcApiClient ncApiClient;
    
    private static final String BAD_STOCK_DATA_PATH = "/api/ycnc/mes/mm/bad/stock/data/list";
    
    /**
     * 从字典获取允许的生产线ID列表
     * @return 生产线ID列表
     */
    private List<String> getAllowedWorklineIds() {
        List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscRepository.findByCodeClIdAndEnabledSt(
            "production_board_line", GlobalConstant.enableTrue);
        return tSysCodeDscList.stream()
            .map(TSysCodeDsc::getCodeValue)
            .collect(Collectors.toList());
    }
    
    /**
     * 从NC接口获取报废料数据（按物料分类筛选）
     * @param productionLine 生产线ID
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @param materialClassification 物料分类（如：返工料、废品）
     * @return 指定分类的总数量
     */
    private BigDecimal getBadStockDataByClassification(String productionLine, String startDate, String endDate, String materialClassification) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            if (productionLine != null && !productionLine.trim().isEmpty()) {
                requestBody.put("cwkid", productionLine);
            }
            requestBody.put("startDate", startDate);
            requestBody.put("endDate", endDate);
            
            // 调用NC接口
            JsonNode dataArray = ncApiClient.postAndGetData(BAD_STOCK_DATA_PATH, requestBody);
            
            if (dataArray != null && dataArray.isArray()) {
                BigDecimal totalQuantity = BigDecimal.ZERO;
                
                // 遍历数据，筛选指定分类的物料
                for (JsonNode item : dataArray) {
                    String code_str = item.has("code") ? item.get("code").asText() : "";
                    double nnum = item.has("nnum") ? item.get("nnum").asDouble() : 0.0;
                    
                    // 根据物料编码查找物料分类
                    if (code_str != null && !code_str.isEmpty()) {
                        List<TSyncMaterial> materials = syncMaterialRepository.getByMaterialCode(code_str);
                        if (materials != null && !materials.isEmpty()) {
                            TSyncMaterial material = materials.get(0);
                            // 判断是否为指定分类
                            if (materialClassification.equals(material.getNcMaterialCategory())) {
                                totalQuantity = totalQuantity.add(BigDecimal.valueOf(nnum));
                            }
                        }
                    }
                }
                
                log.info("成功获取报废料数据，{}总数量: {}", materialClassification, totalQuantity);
                return totalQuantity;
            }
            
            return BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("调用报废料数据接口异常，物料分类: {}", materialClassification, e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 从NC接口获取返工料数据
     * @param productionLine 生产线ID
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @return 返工料总数量
     */
    private BigDecimal getBadStockDataFromNC(String productionLine, String startDate, String endDate) {
        return getBadStockDataByClassification(productionLine, startDate, endDate, "返工料");
    }
    
    /**
     * 标准化日期字符串，提取日期部分（去除时间部分）
     * @param dateStr 日期字符串，可能包含时间部分
     * @return 标准化后的日期字符串 yyyy-MM-dd
     */
    private String normalizeDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return dateStr;
        }
        // 如果包含空格（说明有时间部分），只取日期部分
        if (dateStr.contains(" ")) {
            return dateStr.split(" ")[0];
        }
        return dateStr;
    }
    
    /**
     * 从NC接口获取废品数据
     * @param productionLine 生产线ID
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @return 废品总数量
     */
    private BigDecimal getDefectiveProductDataFromNC(String productionLine, String startDate, String endDate) {
        return getBadStockDataByClassification(productionLine, startDate, endDate, "废品");
    }

    /**
     * 从NC接口获取按时间分组的废品和次品数据
     * @param productionLine 生产线ID
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @param isDay 是否按日分组
     * @param isWeek 是否按周分组
     * @return Map<String, Map<String, BigDecimal>> 外层key为时间，内层key为物料分类，value为数量
     */
    private Map<String, Map<String, BigDecimal>> getBadStockDataGroupedByTime(String productionLine, String startDate, String endDate, boolean isDay, boolean isWeek) {
        Map<String, Map<String, BigDecimal>> result = new HashMap<>();
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            if (productionLine != null && !productionLine.trim().isEmpty()) {
                requestBody.put("cwkid", productionLine);
            }
            requestBody.put("startDate", startDate);
            requestBody.put("endDate", endDate);
            
            // 调用NC接口
            JsonNode dataArray = ncApiClient.postAndGetData(BAD_STOCK_DATA_PATH, requestBody);
            
            if (dataArray != null && dataArray.isArray()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                
                // 遍历数据，按时间和物料分类分组
                for (JsonNode item : dataArray) {
                    String code_str = item.has("code") ? item.get("code").asText() : "";
                    String billdate = item.has("billdate") ? item.get("billdate").asText() : "";
                    double nnum = item.has("nnum") ? item.get("nnum").asDouble() : 0.0;
                    
                    if (code_str != null && !code_str.isEmpty() && billdate != null && !billdate.isEmpty()) {
                        // 根据物料编码查找物料分类
                        List<TSyncMaterial> materials = syncMaterialRepository.getByMaterialCode(code_str);
                        if (materials != null && !materials.isEmpty()) {
                            TSyncMaterial material = materials.get(0);
                            String ncMaterialCategory = material.getNcMaterialCategory();
                            
                            // 只处理废品和返工料
                            if ("废品".equals(ncMaterialCategory) || "返工料".equals(ncMaterialCategory)) {
                                // 根据时间维度生成时间键
                                String timeKey = generateTimeKey(billdate, isDay, isWeek, sdf);
                                
                                if (timeKey != null) {
                                    // 初始化时间键对应的Map
                                    result.computeIfAbsent(timeKey, k -> new HashMap<>());
                                    
                                    // 累加数量
                                    BigDecimal currentQuantity = result.get(timeKey).getOrDefault(ncMaterialCategory, BigDecimal.ZERO);
                                    result.get(timeKey).put(ncMaterialCategory, currentQuantity.add(BigDecimal.valueOf(nnum)));
                                }
                            }
                        }
                    }
                }
                
                log.info("成功获取按时间分组的报废料数据，共{}个时间点", result.size());
            }
            
            return result;
        } catch (Exception e) {
            log.error("调用按时间分组报废料数据接口异常", e);
            return result;
        }
    }
    
    /**
     * 根据时间维度生成时间键
     * @param billdate 账单日期 yyyy-MM-dd
     * @param isDay 是否按日分组
     * @param isWeek 是否按周分组
     * @param sdf 日期格式化器
     * @return 时间键
     */
    private String generateTimeKey(String billdate, boolean isDay, boolean isWeek, SimpleDateFormat sdf) {
        try {
            Date date = sdf.parse(billdate);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(date);
            
            if (isDay) {
                // 按日：返回 MM-dd 格式
                SimpleDateFormat dayFormat = new SimpleDateFormat("MM-dd");
                return dayFormat.format(date);
            } else if (isWeek) {
                // 按周：返回 "第X周" 格式
                int weekOfYear = cal.get(java.util.Calendar.WEEK_OF_YEAR);
                return "第" + weekOfYear + "周";
            } else {
                // 按月：返回 yyyy-MM 格式
                SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
                return monthFormat.format(date);
            }
        } catch (Exception e) {
            log.error("生成时间键异常，日期: {}", billdate, e);
            return null;
        }
    }

    /**
     * 获取指定时间段的废料数据
     * @param productionLine 生产线ID
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @param worklineIds 允许的生产线ID列表
     * @return 废料产出分析数据
     */
    private WasteOutputAnalysis getWasteDataForPeriod(String productionLine, String startDate, String endDate, List<String> worklineIds) {
        try {
            // 1. 从NC接口获取废品重量（物料分类=废品）
            BigDecimal defectiveWeight = getDefectiveProductDataFromNC(productionLine, startDate, endDate);
            
            // 2. 从NC接口获取次品重量（物料分类=返工料）
            BigDecimal wasteWeight = getBadStockDataFromNC(productionLine, startDate, endDate);
            
            // 3. 从数据库获取净含量报工总重量
            SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDateTime = fullSdf.parse(startDate + " 00:00:00");
            Date endDateTime = fullSdf.parse(endDate + " 23:59:59");
            
            BigDecimal netContentWeight = productionBoardRepository.sumNetContentWeightByDateRange(
                startDateTime, endDateTime, productionLine, worklineIds);
            
            // 4. 计算废品率 = 原辅材废品重量 / 净含量报工总重量 * 100%
            BigDecimal wasteRate = BigDecimal.ZERO;
            if (netContentWeight != null && netContentWeight.compareTo(BigDecimal.ZERO) > 0) {
                wasteRate = defectiveWeight.divide(netContentWeight, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
            }
            
            return new WasteOutputAnalysis(
                "",  // timeX will be set by caller
                wasteWeight,  // 次品重量（返工料）
                defectiveWeight,  // 废料重量（废品）
                wasteRate  // 废料率（百分比，保留2位小数）
            );
            
        } catch (Exception e) {
            log.error("获取时间段废料数据异常，开始日期: {}, 结束日期: {}", startDate, endDate, e);
            return new WasteOutputAnalysis(
                "",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
            );
        }
    }

    /**
     * 获取指定时间段的废料数据（用于完整时间轴生成）
     * @param productionLine 生产线ID
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @param worklineIds 允许的生产线ID列表
     * @param timeKey 时间键（用于匹配数据库查询结果）
     * @param netContentMap 净含量数据Map（key为时间，value为净含量）
     * @return 废料产出分析数据
     */
    private WasteOutputAnalysis getWasteDataForTimeAxis(String productionLine, String startDate, String endDate, 
                                                       List<String> worklineIds, String timeKey, 
                                                       Map<String, BigDecimal> netContentMap) {
        try {
            // 1. 从NC接口获取废品重量（物料分类=废品）
            // 注意：NC接口不支持按时间分组，这里获取整个时间段的数据
            // 在实际应用中，可能需要根据业务需求调整
            BigDecimal defectiveWeight = getDefectiveProductDataFromNC(productionLine, startDate, endDate);
            
            // 2. 从NC接口获取次品重量（物料分类=返工料）
            BigDecimal wasteWeight = getBadStockDataFromNC(productionLine, startDate, endDate);
            
            // 3. 从净含量Map中获取对应时间的净含量
            BigDecimal netContentWeight = netContentMap.getOrDefault(timeKey, BigDecimal.ZERO);
            
            // 4. 计算废品率 = 原辅材废品重量 / 净含量报工总重量 * 100%
            BigDecimal wasteRate = BigDecimal.ZERO;
            if (netContentWeight != null && netContentWeight.compareTo(BigDecimal.ZERO) > 0) {
                wasteRate = defectiveWeight.divide(netContentWeight, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
            }
            
            return new WasteOutputAnalysis(
                "",  // timeX will be set by caller
                wasteWeight,  // 次品重量（返工料）
                defectiveWeight,  // 废料重量（废品）
                wasteRate  // 废料率（百分比，保留2位小数）
            );
            
        } catch (Exception e) {
            log.error("获取时间轴废料数据异常，时间键: {}", timeKey, e);
            return new WasteOutputAnalysis(
                "",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
            );
        }
    }

    @Override
    public ProductionStatistics getProductionStatistics(String productionLine, String timeDimension, 
                                                       String startDate, String endDate) {
        // 获取允许的生产线ID列表
        List<String> worklineIds = getAllowedWorklineIds();
        if (worklineIds.isEmpty()) {
            // 如果没有配置生产线，返回空数据
            return createEmptyStatistics();
        }
        
        // 处理时间范围
        String finalStartDate = startDate;
        String finalEndDate = endDate;
        
        // 如果传了时间维度，使用TimeDimensionUtils转换
        if (timeDimension != null && !timeDimension.trim().isEmpty()) {
            TimeDimensionUtils.TimeRange timeRange = TimeDimensionUtils.getTimeRange(timeDimension);
            finalStartDate = timeRange.getStartDate();
            finalEndDate = timeRange.getEndDate();
        }
        
        // 标准化日期字符串，去除可能的时间部分
        finalStartDate = normalizeDateString(finalStartDate);
        finalEndDate = normalizeDateString(finalEndDate);
        
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
            BigDecimal packagingBadProductWeight;
            
            if (productionLine != null && !productionLine.trim().isEmpty()) {
                // 如果传了生产线,按生产线查询
                resultList = productionBoardRepository.getOrderStatisticsByDateRangeAndProductionLine(
                    startDateTime, endDateTime, productionLine, worklineIds);
                planMaterialWeight = productionBoardRepository.sumPlanMaterialWeightByDateRangeAndProductionLine(
                    startDateTime, endDateTime, productionLine, worklineIds);
                // 从NC接口获取返工料数据作为包材重量
                packagingWeight = getBadStockDataFromNC(productionLine, finalStartDate, finalEndDate);
                // 从NC接口获取废品数据作为包材废品重量
                packagingBadProductWeight = getDefectiveProductDataFromNC(productionLine, finalStartDate, finalEndDate);
            } else {
                // 否则查询所有
                resultList = productionBoardRepository.getOrderStatisticsByDateRange(
                    startDateTime, endDateTime, worklineIds);
                planMaterialWeight = productionBoardRepository.sumPlanMaterialWeightByDateRange(
                    startDateTime, endDateTime, worklineIds);
                // 从NC接口获取返工料数据作为包材重量
                packagingWeight = getBadStockDataFromNC(null, finalStartDate, finalEndDate);
                // 从NC接口获取废品数据作为包材废品重量
                packagingBadProductWeight = getDefectiveProductDataFromNC(null, finalStartDate, finalEndDate);
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
            
            // 包材重量（返工料，保留3位小数）
            BigDecimal pkgWeight = packagingWeight != null ? packagingWeight : BigDecimal.ZERO;
            stats.setPackagingWeight(pkgWeight.setScale(3, BigDecimal.ROUND_HALF_UP));
            stats.setPackagingWeightUnit("kg");
            
            // 包材废品重量（废品，保留3位小数）
            BigDecimal badProductWeight = packagingBadProductWeight != null ? packagingBadProductWeight : BigDecimal.ZERO;
            stats.setPackagingBadProductWeight(badProductWeight.setScale(3, BigDecimal.ROUND_HALF_UP));
            stats.setPackagingBadProductWeightUnit("kg");
            
        } catch (Exception e) {
            e.printStackTrace();
            // 查询失败时返回0
            return createEmptyStatistics();
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
        
        return stats;
    }
    
    /**
     * 创建空的统计数据
     */
    private ProductionStatistics createEmptyStatistics() {
        ProductionStatistics stats = new ProductionStatistics();
        stats.setOrderCount(0);
        stats.setOrderCountUnit("个");
        stats.setPlanProductionQuantity(BigDecimal.ZERO);
        stats.setPlanProductionQuantityUnit("件");
        stats.setPlanMaterialWeight(BigDecimal.ZERO);
        stats.setPlanMaterialWeightUnit("kg");
        stats.setPackagingWeight(BigDecimal.ZERO);
        stats.setPackagingWeightUnit("kg");
        stats.setDefectiveQuantity(BigDecimal.ZERO);
        stats.setDefectiveQuantityUnit("kg");
        stats.setWasteQuantity(BigDecimal.ZERO);
        stats.setWasteQuantityUnit("kg");
        stats.setPackagingBadProductWeight(BigDecimal.ZERO);
        stats.setPackagingBadProductWeightUnit("kg");
        return stats;
    }

    @Override
    public List<OrderPlanAnalysis> getOrderPlanAnalysis(String productionLine, String dateType,String startDate, String endDate) {
        // 获取允许的生产线ID列表
        List<String> worklineIds = getAllowedWorklineIds();
        if (worklineIds.isEmpty()) {
            return new ArrayList<>();
        }
        
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
        
        // 标准化日期字符串，去除可能的时间部分
        finalStartDate = normalizeDateString(finalStartDate);
        finalEndDate = normalizeDateString(finalEndDate);
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
                    startDateTime, endDateTime, productionLine, worklineIds);
            } else {
                // 按月分组查询
                results = productionBoardRepository.findOrderPlanAnalysisByMonth(
                    startDateTime, endDateTime, productionLine, worklineIds);
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
        // 获取允许的生产线ID列表
        List<String> worklineIds = getAllowedWorklineIds();
        if (worklineIds.isEmpty()) {
            log.warn("用户没有权限访问任何生产线");
            return new ArrayList<>();
        }
        
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
        
        // 优先使用传入的日期范围
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            finalStartDate = startDate;
            finalEndDate = endDate;
        }
        
        // 标准化日期字符串，去除可能的时间部分
        finalStartDate = normalizeDateString(finalStartDate);
        finalEndDate = normalizeDateString(finalEndDate);
        
        try {
            // 将字符串日期转换为Date对象
            SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDateTime = fullSdf.parse(finalStartDate + " 00:00:00");
            Date endDateTime = fullSdf.parse(finalEndDate + " 23:59:59");
            
            // 判断时间维度
            boolean isDay = dateType != null && (dateType.contains("日") || dateType.equals("TODAY") || dateType.equals("YESTERDAY"));
            boolean isWeek = dateType != null && dateType.contains("周");
            
            // 查询净含量数据（按时间分组）
            List<Map> netContentResults;
            if (isDay) {
                netContentResults = productionBoardRepository.findNetContentWeightByDay(
                    startDateTime, endDateTime, productionLine, worklineIds);
            } else if (isWeek) {
                netContentResults = productionBoardRepository.findNetContentWeightByWeek(
                    startDateTime, endDateTime, productionLine, worklineIds);
            } else {
                netContentResults = productionBoardRepository.findNetContentWeightByMonth(
                    startDateTime, endDateTime, productionLine, worklineIds);
            }
            
            // 将净含量查询结果转换为Map，key为时间，value为净含量
            Map<String, BigDecimal> netContentMap = new java.util.HashMap<>();
            if (netContentResults != null && !netContentResults.isEmpty()) {
                for (Map row : netContentResults) {
                    String timeX = (String) row.get("timeX");
                    BigDecimal netContentWeight = row.get("netContentWeight") != null ? 
                        new BigDecimal(row.get("netContentWeight").toString()) : BigDecimal.ZERO;
                    netContentMap.put(timeX, netContentWeight);
                }
            }
            
            // 获取按时间分组的NC接口数据（废品和次品）
            Map<String, Map<String, BigDecimal>> ncDataMap = getBadStockDataGroupedByTime(
                productionLine, finalStartDate, finalEndDate, isDay, isWeek);
            
            // 生成完整的时间轴数据
            List<WasteOutputAnalysis> list = new ArrayList<>();
            
            if (isDay) {
                // 按日生成时间轴
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(startDateTime);
                
                while (!cal.getTime().after(endDateTime)) {
                    String timeKey = sdf.format(cal.getTime());
                    
                    // 获取该时间点的数据
                    BigDecimal netContentWeight = netContentMap.getOrDefault(timeKey, BigDecimal.ZERO);
                    Map<String, BigDecimal> ncData = ncDataMap.getOrDefault(timeKey, new HashMap<>());
                    BigDecimal defectiveWeight = ncData.getOrDefault("废品", BigDecimal.ZERO);
                    BigDecimal wasteWeight = ncData.getOrDefault("返工料", BigDecimal.ZERO);
                    
                    // 计算废品率
                    BigDecimal wasteRate = BigDecimal.ZERO;
                    if (netContentWeight.compareTo(BigDecimal.ZERO) > 0) {
                        wasteRate = defectiveWeight.divide(netContentWeight, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
                            .setScale(2, RoundingMode.HALF_UP);
                    }
                    
                    WasteOutputAnalysis analysis = new WasteOutputAnalysis(
                        timeKey,
                        wasteWeight,  // 次品重量（返工料）
                        defectiveWeight,  // 废料重量（废品）
                        wasteRate  // 废料率
                    );
                    
                    list.add(analysis);
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                }
            } else if (isWeek) {
                // 按周生成时间轴
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(startDateTime);
                
                while (!cal.getTime().after(endDateTime)) {
                    // 获取该周的周数
                    int weekOfYear = cal.get(java.util.Calendar.WEEK_OF_YEAR);
                    String timeKey = "第" + weekOfYear + "周";
                    
                    // 获取该时间点的数据
                    BigDecimal netContentWeight = netContentMap.getOrDefault(timeKey, BigDecimal.ZERO);
                    Map<String, BigDecimal> ncData = ncDataMap.getOrDefault(timeKey, new HashMap<>());
                    BigDecimal defectiveWeight = ncData.getOrDefault("废品", BigDecimal.ZERO);
                    BigDecimal wasteWeight = ncData.getOrDefault("返工料", BigDecimal.ZERO);
                    
                    // 计算废品率
                    BigDecimal wasteRate = BigDecimal.ZERO;
                    if (netContentWeight.compareTo(BigDecimal.ZERO) > 0) {
                        wasteRate = defectiveWeight.divide(netContentWeight, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
                            .setScale(2, RoundingMode.HALF_UP);
                    }
                    
                    WasteOutputAnalysis analysis = new WasteOutputAnalysis(
                        timeKey,
                        wasteWeight,  // 次品重量（返工料）
                        defectiveWeight,  // 废料重量（废品）
                        wasteRate  // 废料率
                    );
                    
                    list.add(analysis);
                    cal.add(java.util.Calendar.WEEK_OF_YEAR, 1);
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
                    
                    // 获取该时间点的数据
                    BigDecimal netContentWeight = netContentMap.getOrDefault(timeKey, BigDecimal.ZERO);
                    Map<String, BigDecimal> ncData = ncDataMap.getOrDefault(timeKey, new HashMap<>());
                    BigDecimal defectiveWeight = ncData.getOrDefault("废品", BigDecimal.ZERO);
                    BigDecimal wasteWeight = ncData.getOrDefault("返工料", BigDecimal.ZERO);
                    
                    // 计算废品率
                    BigDecimal wasteRate = BigDecimal.ZERO;
                    if (netContentWeight.compareTo(BigDecimal.ZERO) > 0) {
                        wasteRate = defectiveWeight.divide(netContentWeight, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
                            .setScale(2, RoundingMode.HALF_UP);
                    }
                    
                    WasteOutputAnalysis analysis = new WasteOutputAnalysis(
                        displayTime,
                        wasteWeight,  // 次品重量（返工料）
                        defectiveWeight,  // 废料重量（废品）
                        wasteRate  // 废料率
                    );
                    
                    list.add(analysis);
                    cal.add(java.util.Calendar.MONTH, 1);
                }
            }
            
            return list;
            
        } catch (Exception e) {
            log.error("获取废料产出分析数据异常", e);
            return new ArrayList<>();
        }
    }

    @Override
    public OrderProgressPageVo getOrderProgress(String productionLine, Integer current, Integer size, String startDate, String endDate) {
        // 获取允许的生产线ID列表
        List<String> worklineIds = getAllowedWorklineIds();
        if (worklineIds.isEmpty()) {
            Page<OrderProgress> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(current, size), 0);
            return new OrderProgressPageVo(emptyPage, 0, 0);
        }
        
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
                worklineIds,
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
                productionLine,
                worklineIds
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
        // 获取允许的生产线ID列表
        List<String> worklineIds = getAllowedWorklineIds();
        if (worklineIds.isEmpty()) {
            PageVo<ProductionBgVo> emptyPageVo = new PageVo<>(size, current);
            emptyPageVo.setTotal(0);
            emptyPageVo.setList(new ArrayList<>());
            return emptyPageVo;
        }
        
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
            
            // 查询生产报工数据（正常范围）- 返回Page<Map>
            Page<Map> select;
            if (startDateTime != null && endDateTime != null) {
                select = productionBoardRepository.findProductionBgDataByDateRange(
                    productionLine,
                    startDateTime,
                    endDateTime,
                    worklineIds,
                    pageable
                );
            } else {
                select = productionBoardRepository.findProductionBgData(
                    productionLine,
                    worklineIds,
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
    public PageVo<ProductionBgVo> getProductionBGAbnormal(String productionLine, Integer current, Integer size, String startDate, String endDate) {
        // 获取允许的生产线ID列表
        List<String> worklineIds = getAllowedWorklineIds();
        if (worklineIds.isEmpty()) {
            PageVo<ProductionBgVo> emptyPageVo = new PageVo<>(size, current);
            emptyPageVo.setTotal(0);
            emptyPageVo.setList(new ArrayList<>());
            return emptyPageVo;
        }
        
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
            
            // 查询生产报工数据（异常范围）- 返回Page<Map>
            Page<Map> select;
            if (startDateTime != null && endDateTime != null) {
                select = productionBoardRepository.findProductionBgDataAbnormalByDateRange(
                    productionLine,
                    startDateTime,
                    endDateTime,
                    worklineIds,
                    pageable
                );
            } else {
                select = productionBoardRepository.findProductionBgDataAbnormal(
                    productionLine,
                    worklineIds,
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
        // 获取允许的生产线ID列表
        List<String> worklineIds = getAllowedWorklineIds();
        if (worklineIds.isEmpty()) {
            PageVo<OutsourcingNetContent> emptyPageVo = new PageVo<>(size, current);
            emptyPageVo.setTotal(0);
            emptyPageVo.setList(new ArrayList<>());
            return emptyPageVo;
        }
        
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
            
            // 查询外包净含量数据（正常范围）- 返回Page<Map>
            Page<Map> select;
            if (startDateTime != null && endDateTime != null) {
                select = productionBoardRepository.findOutsourcingNetContentDataByDateRange(
                    productionLine,
                    startDateTime,
                    endDateTime,
                    worklineIds,
                    pageable
                );
            } else {
                select = productionBoardRepository.findOutsourcingNetContentData(
                    productionLine,
                    worklineIds,
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

    @Override
    public PageVo<OutsourcingNetContent> getOutsourcingNetContentAbnormal(String productionLine, Integer current, Integer size, String startDate, String endDate) {
        // 获取允许的生产线ID列表
        List<String> worklineIds = getAllowedWorklineIds();
        if (worklineIds.isEmpty()) {
            PageVo<OutsourcingNetContent> emptyPageVo = new PageVo<>(size, current);
            emptyPageVo.setTotal(0);
            emptyPageVo.setList(new ArrayList<>());
            return emptyPageVo;
        }
        
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
            
            // 查询外包净含量数据（异常范围）- 返回Page<Map>
            Page<Map> select;
            if (startDateTime != null && endDateTime != null) {
                select = productionBoardRepository.findOutsourcingNetContentDataAbnormalByDateRange(
                    productionLine,
                    startDateTime,
                    endDateTime,
                    worklineIds,
                    pageable
                );
            } else {
                select = productionBoardRepository.findOutsourcingNetContentDataAbnormal(
                    productionLine,
                    worklineIds,
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
