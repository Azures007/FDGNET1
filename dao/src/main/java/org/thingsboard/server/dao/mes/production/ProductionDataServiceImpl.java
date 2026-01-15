package org.thingsboard.server.dao.mes.production;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPPBom;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.mes.sys.TSysCraftInfo;
import org.thingsboard.server.common.data.mes.vo.InputMaterialItem;
import org.thingsboard.server.common.data.mes.vo.ProductionData;
import org.thingsboard.server.common.data.mes.vo.ProductionDataExcelVo;
import org.thingsboard.server.dao.mes.dto.ProductionDataQueryDto;
import org.thingsboard.server.dao.mes.tSysCodeDsc.TSysCodeDscService;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.mes.production.ProductionDataRepository;
import org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.dao.user.UserService;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Collections;
import java.util.stream.Collectors;


@Service
public class ProductionDataServiceImpl implements ProductionDataService {

    @Autowired
    private ProductionDataRepository productionDataRepository;
    
    @Autowired
    private TSysCodeDscService tSysCodeDscService;
    
    @Autowired
    private TSysCodeDscRepository tSysCodeDscRepository;

    @Autowired
    private TSysCraftInfoRepository tSysCraftInfoRepository;

    @Autowired
    private UserService userService;

    @Override
    public void exportProductionData(List<String> userCwkids, ProductionDataQueryDto queryDto, HttpServletResponse response) {
        try {
            // 获取所有数据
            PageVo<ProductionData> allData = queryProductionData(userCwkids, 0, 10000, queryDto);
            List<ProductionData> list = allData.getList();

            // 转换为Excel格式
            List<ProductionDataExcelVo> excelDataList = convertToExcelData(list);

            // 设置样式
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            HorizontalCellStyleStrategy styleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

            // 使用EasyExcel写入响应流
            EasyExcel.write(response.getOutputStream(), ProductionDataExcelVo.class)
                    .registerWriteHandler(styleStrategy)
                    .registerWriteHandler(new CustomMergeStrategy(excelDataList))
                    .sheet("投入产出比报表")
                    .doWrite(excelDataList);
        } catch (Exception e) {
            throw new RuntimeException("导出投入产出比报表失败: " + e.getMessage());
        }
    }

    private List<ProductionDataExcelVo> convertToExcelData(List<ProductionData> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        return list.parallelStream().flatMap(data -> {
            String groupKey = data.getProductionLine() + "_" + data.getDate();
            List<InputMaterialItem> materialItems = data.getInputMaterialItems();
            List<ProductionDataExcelVo> voList = new ArrayList<>();

            if (materialItems == null || materialItems.isEmpty()) {
                ProductionDataExcelVo vo = new ProductionDataExcelVo();
                fillBaseInfo(vo, data, groupKey);
                voList.add(vo);
            } else {
                for (InputMaterialItem materialItem : materialItems) {
                    ProductionDataExcelVo vo = new ProductionDataExcelVo();
                    fillBaseInfo(vo, data, groupKey);
                    
                    // 投入信息
                    vo.setMaterialNumber(materialItem.getMaterialNumber());
                    vo.setMaterialName(materialItem.getMaterialName());
                    vo.setRecordType(materialItem.getRecordType());
                    vo.setRecordUnit(materialItem.getRecordUnit());
                    vo.setPlannedInput(materialItem.getPlannedInput() != null ? materialItem.getPlannedInput() : "0.00");
                    vo.setActualInput(materialItem.getActualInput() != null ? materialItem.getActualInput() : "0.00");
                    
                    voList.add(vo);
                }
            }
            return voList.stream();
        }).collect(Collectors.toList());
    }

    private void fillBaseInfo(ProductionDataExcelVo vo, ProductionData data, String groupKey) {
        vo.setGroupKey(groupKey);
        // 基础信息
        vo.setDate(data.getDate());
        vo.setProductionLine(data.getProductionLine());
        
        // 损耗信息
        vo.setPackagingWasteWeight(data.getPackagingWasteWeight() != null ? data.getPackagingWasteWeight() : "0");
        vo.setDefectiveWeight(data.getDefectiveWeight() != null ? data.getDefectiveWeight() : "0");
        
        // 产出信息
        vo.setPlannedOutput(data.getPlannedOutput() != null ? data.getPlannedOutput() : "0");
        vo.setActualOutput(data.getActualOutput() != null ? data.getActualOutput() : "0");
        vo.setNetContentWeight(data.getNetContentWeight() != null ? data.getNetContentWeight() : "0");
        
        // 投入产出比
        String ratio = data.getInputOutputRatio();
        if ("-".equals(ratio)) {
            vo.setInputOutputRatio("-");
        } else {
            vo.setInputOutputRatio(ratio != null ? ratio + "%" : "0%");
        }
        
        vo.setMaterialConsumptionPerBox(data.getMaterialConsumptionPerBox() != null ? data.getMaterialConsumptionPerBox() : "0");
        vo.setDefectiveRate(data.getDefectiveRate() != null ? data.getDefectiveRate() : "0");
    }

    /**
     * 自定义合并策略
     */
    private static class CustomMergeStrategy implements SheetWriteHandler {
        private final List<ProductionDataExcelVo> excelDataList;

        public CustomMergeStrategy(List<ProductionDataExcelVo> excelDataList) {
            this.excelDataList = excelDataList;
        }

        @Override
        public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        }

        @Override
        public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
            if (excelDataList == null || excelDataList.isEmpty()) {
                return;
            }
            org.apache.poi.ss.usermodel.Sheet sheet = writeSheetHolder.getSheet();
            // 表头占据2行，索引从2开始
            int startRow = 2;
            
            int i = 0;
            while (i < excelDataList.size()) {
                int j = i + 1;
                while (j < excelDataList.size() && Objects.equals(excelDataList.get(i).getGroupKey(), excelDataList.get(j).getGroupKey())) {
                    j++;
                }
                if (j - i > 1) {
                    // 需要合并的列：0-1 (基础信息), 8-15 (损耗、产出、比率信息)
                    // 0: 日期, 1: 产线
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 0, 0));
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 1, 1));
                    
                    // 8: 包膜废品重量, 9: 废次品重量
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 8, 8));
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 9, 9));
                    
                    // 10: 计划产量, 11: 实际产量, 12: 净含量重量
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 10, 10));
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 11, 11));
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 12, 12));
                    
                    // 13: 投入产出比, 14: 单箱原辅料消耗, 15: 废次品比率
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 13, 13));
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 14, 14));
                    sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, 15, 15));
                }
                i = j;
            }
        }
    }
    @Override
    public PageVo<ProductionData> queryProductionData(List<String> userCwkids, int current, int size, ProductionDataQueryDto queryDto) {
        // 获取“周转肉松饼”的工艺ID
        TSysCraftInfo turnoverCraftInfo = tSysCraftInfoRepository.findByCraftName("周转肉松饼");
        Integer turnoverCraftId = turnoverCraftInfo != null ? turnoverCraftInfo.getCraftId() : null;
    
        // 获取“肉松饼成品”的工艺ID
        TSysCraftInfo finishedCraftInfo = tSysCraftInfoRepository.findByCraftName("肉松饼成品");
        Integer finishedCraftId = finishedCraftInfo != null ? finishedCraftInfo.getCraftId() : null;
    
        // 处理时间范围，确保开始时间是当天00:00:00，结束时间是当天23:59:59
        Date startTime = adjustStartDate(queryDto.getStartTime());
        Date endTime = adjustEndDate(queryDto.getEndTime());
            
        // 确定最终过滤的产线ID列表
        List<String> targetCwkids = new ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(queryDto.getCwkid())) {
            // 如果指定了产线，则使用指定的产线
            targetCwkids.add(queryDto.getCwkid());
        } else if (userCwkids != null && !userCwkids.isEmpty()) {
            // 如果未指定产线，则默认使用用户绑定的所有产线
            targetCwkids.addAll(userCwkids);
        }
            
        // 如果既没有指定产线，该用户也没有绑定的权限产线，则直接返回空结果，防止查出全量数据
        if (targetCwkids.isEmpty()) {
            PageVo<ProductionData> emptyPage = new PageVo<>();
            emptyPage.setList(new ArrayList<>());
            emptyPage.setTotal(0);
            emptyPage.setCurrent(current);
            emptyPage.setSize(size);
            return emptyPage;
        }
    
        // 第一步：初步查询所有符合条件的订单基本信息（产线、最小接单时间、订单号）
        List<Object[]> matchingBasics = productionDataRepository.findAllMatchingOrderBasics(startTime, endTime, targetCwkids, turnoverCraftId, finishedCraftId);
            
        if (matchingBasics.isEmpty()) {
            PageVo<ProductionData> emptyPage = new PageVo<>();
            emptyPage.setList(new ArrayList<>());
            emptyPage.setTotal(0);
            emptyPage.setCurrent(current);
            emptyPage.setSize(size);
            return emptyPage;
        }
    
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
        // 第二步：在内存中对订单进行预分组（按产线和日期），用于分页
        // Map<"Line_Date", List<orderNo>>
        Map<String, List<String>> groupToOrderNos = new LinkedHashMap<>();
    
        for (Object[] row : matchingBasics) {
            String line = (String) row[0];
            Date minRT = (Date) row[1];
            String orderNo = (String) row[2];
                
            String dateStr = minRT != null ? minRT.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter) : "未知日期";
            String key = line + "_" + dateStr;
                
            groupToOrderNos.computeIfAbsent(key, k -> new ArrayList<>()).add(orderNo);
        }
    
        // 第三步：对分组进行排序和分页
        List<String> sortedGroupKeys = groupToOrderNos.keySet().stream()
                .sorted((k1, k2) -> {
                    String d1 = k1.split("_")[1];
                    String d2 = k2.split("_")[1];
                    int dateComp = d2.compareTo(d1); // 日期降序
                    if (dateComp != 0) return dateComp;
                    return k1.split("_")[0].compareTo(k2.split("_")[0]); // 产线升序
                })
                .collect(Collectors.toList());
    
        int totalGroups = sortedGroupKeys.size();
        int startIndex = current * size;
        int endIndex = Math.min(startIndex + size, totalGroups);
    
        if (startIndex >= totalGroups) {
            PageVo<ProductionData> emptyPage = new PageVo<>();
            emptyPage.setList(new ArrayList<>());
            emptyPage.setTotal(totalGroups);
            emptyPage.setCurrent(current);
            emptyPage.setSize(size);
            return emptyPage;
        }
    
        List<String> pagedGroupKeys = sortedGroupKeys.subList(startIndex, endIndex);
    
        // 第四步：仅针对当前页的分组，收集需要的订单号并拉取详细数据
        List<String> pagedOrderNos = pagedGroupKeys.stream()
                .flatMap(key -> groupToOrderNos.get(key).stream())
                .collect(Collectors.toList());
        
        // 分批获取详细数据以避免 SQL 参数过多导致的超时或参数限制错误
        List<TBusOrderHead> orderHeads = new ArrayList<>();
        Map<String, List<Object[]>> actualInputAggMap = new HashMap<>(); // orderNo -> List<[materialNumber, name, unit, type, sumQty]>
        Map<String, Long> actualOutputCountMap = new HashMap<>(); // orderNo -> count
        Map<String, BigDecimal> netWeightAggMap = new HashMap<>(); // orderNo -> sumQty
                
        // 使用更小的时间暗示 hint，进一步加速索引扫描
        Calendar calHint = Calendar.getInstance();
        calHint.setTime(startTime);
        calHint.add(Calendar.DAY_OF_MONTH, -1);
        Date hintStart = calHint.getTime();
        
        int chunkSize = 200; // 减小 chunkSize 以降低单次查询负载
        for (int i = 0; i < pagedOrderNos.size(); i += chunkSize) {
            List<String> chunk = pagedOrderNos.subList(i, Math.min(i + chunkSize, pagedOrderNos.size()));
                    
            // 批量获取头信息
            List<Object[]> headResultsChunk = productionDataRepository.findByOrderNoInWithMinRT(chunk);
            for (Object[] row : headResultsChunk) {
                orderHeads.add((TBusOrderHead) row[0]);
            }
                    
            // 使用数据库聚合查询，极大减少传输数据量
            List<Object[]> inputAgg = productionDataRepository.findActualInputAggregated(chunk, hintStart);
            for (Object[] row : inputAgg) {
                String oNo = (String) row[0];
                actualInputAggMap.computeIfAbsent(oNo, k -> new ArrayList<>()).add(new Object[]{row[1], row[2], row[3], row[4], row[5]});
            }
                    
            List<Object[]> outputAgg = productionDataRepository.findActualOutputCountAggregated(chunk, hintStart);
            for (Object[] row : outputAgg) {
                actualOutputCountMap.put((String) row[0], (Long) row[1]);
            }
                    
            List<Object[]> weightAgg = productionDataRepository.findNetWeightAggregated(chunk, hintStart);
            for (Object[] row : weightAgg) {
                netWeightAggMap.put((String) row[0], (BigDecimal) row[1]);
            }
        }
        
        Map<String, Integer> orderTypeMap = new HashMap<>();
        for (TBusOrderHead head : orderHeads) {
            // 判断订单类型 (工艺优先)
            Integer type = 2; // 默认
            Integer cid = (head.getCraftId() != null) ? head.getCraftId().getCraftId() : null;
            if (turnoverCraftId != null && turnoverCraftId.equals(cid)) {
                type = 1;
            } else if (finishedCraftId != null && finishedCraftId.equals(cid)) {
                type = 2;
            } else if ("周转饼流程生产单".equals(head.getBillType())) {
                type = 1;
            } else if ("普通流程生产订单".equals(head.getBillType())) {
                type = 2;
            }
            orderTypeMap.put(head.getOrderNo(), type);
        }
        
        // 预加载字典
        List<TSysCodeDsc> dictList = tSysCodeDscRepository.findByCodeClId("RECORDTYPE0000");
        Map<String, String> dictMap = dictList.stream()
                .collect(Collectors.toMap(TSysCodeDsc::getCodeValue, TSysCodeDsc::getCodeDsc, (v1, v2) -> v1));
        
        // 预聚合当前页的BOM信息
        Map<String, Map<String, BigDecimal>> orderPlannedInputMap = new HashMap<>();
        Map<String, TBusOrderPPBom> allMaterialInfoMap = new HashMap<>();
        for (TBusOrderHead order : orderHeads) {
            Map<String, BigDecimal> bomMap = new HashMap<>();
            if (order.getTBusOrderPPBomSet() != null) {
                for (TBusOrderPPBom bom : order.getTBusOrderPPBomSet()) {
                    String matNum = bom.getMaterialNumber();
                    if (matNum != null) {
                        BigDecimal qty = bom.getMustQty() != null ? bom.getMustQty() : BigDecimal.ZERO;
                        bomMap.merge(matNum, qty, BigDecimal::add);
                        allMaterialInfoMap.putIfAbsent(matNum, bom);
                    }
                }
            }
            orderPlannedInputMap.put(order.getOrderNo(), bomMap);
        }
        
        // 第五步：执行最终的聚合计算（仅针对当前页的数据）
        List<ProductionData> pagedResult = pagedGroupKeys.stream()
                .map(groupKey -> {
                    String[] parts = groupKey.split("_");
                    String productionLine = parts[0];
                    String date = parts[1];
                            
                    List<String> orderNosInGroup = groupToOrderNos.get(groupKey);
                    List<TBusOrderHead> ordersInGroup = orderHeads.stream()
                            .filter(o -> orderNosInGroup.contains(o.getOrderNo()))
                            .collect(Collectors.toList());
        
                    // 拆分周转饼订单和成品订单
                    List<TBusOrderHead> turnoverOrders = ordersInGroup.stream()
                            .filter(o -> orderTypeMap.getOrDefault(o.getOrderNo(), 2) == 1)
                            .collect(Collectors.toList());
                    List<TBusOrderHead> finishedOrders = ordersInGroup.stream()
                            .filter(o -> orderTypeMap.getOrDefault(o.getOrderNo(), 2) == 2)
                            .collect(Collectors.toList());
        
                    ProductionData productionData = new ProductionData();
                    productionData.setProductionLine(productionLine);
                    productionData.setDate(date);
        
                    // --- 1. 处理投入信息 (周转订单) ---
                    List<InputMaterialItem> inputMaterialItems = new ArrayList<>();
                    BigDecimal totalActualInputSum = BigDecimal.ZERO;
                    BigDecimal rawMaterialInputSum = BigDecimal.ZERO;
        
                    if (!turnoverOrders.isEmpty()) {
                        // 计划投入聚合
                        Map<String, BigDecimal> materialPlannedInputMap = new HashMap<>();
                        // 实际投入聚合 (从 DB 聚合结果中取)
                        Map<String, BigDecimal> materialActualInputMap = new HashMap<>();
                        Map<String, String[]> matMetadataMap = new HashMap<>(); // matNum -> [name, unit, type]
        
                        for (TBusOrderHead o : turnoverOrders) {
                            String oNo = o.getOrderNo();
                            // 计划
                            Map<String, BigDecimal> boms = orderPlannedInputMap.get(oNo);
                            if (boms != null) boms.forEach((num, q) -> materialPlannedInputMap.merge(num, q, BigDecimal::add));
                                    
                            // 实际
                            List<Object[]> aggs = actualInputAggMap.get(oNo);
                            if (aggs != null) {
                                for (Object[] row : aggs) {
                                    String matNum = (String) row[0];
                                    BigDecimal qty = (BigDecimal) row[4];
                                    materialActualInputMap.merge(matNum, qty, BigDecimal::add);
                                    matMetadataMap.putIfAbsent(matNum, new String[]{(String)row[1], (String)row[2], (String)row[3]});
                                            
                                    totalActualInputSum = totalActualInputSum.add(qty);
                                    if ("1".equals(row[3])) rawMaterialInputSum = rawMaterialInputSum.add(qty);
                                }
                            }
                        }
        
                        Set<String> allMatNums = new HashSet<>(materialActualInputMap.keySet());
                        allMatNums.addAll(materialPlannedInputMap.keySet());
        
                        for (String matNum : allMatNums) {
                            InputMaterialItem item = new InputMaterialItem();
                            item.setMaterialNumber(matNum);
                                    
                            String[] meta = matMetadataMap.get(matNum);
                            if (meta != null) {
                                item.setMaterialName(meta[0]);
                                item.setRecordUnit(meta[1]);
                                item.setRecordType(dictMap.getOrDefault(meta[2], getFallbackRecordTypeLabel(meta[2])));
                            } else {
                                TBusOrderPPBom bom = allMaterialInfoMap.get(matNum);
                                item.setMaterialName(bom != null ? bom.getMaterialName() : "");
                                item.setRecordUnit(bom != null ? bom.getUnit() : "");
                                item.setRecordType("-");
                            }
                                    
                            item.setActualInput(materialActualInputMap.getOrDefault(matNum, BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            item.setPlannedInput(materialPlannedInputMap.getOrDefault(matNum, BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            inputMaterialItems.add(item);
                        }
                    } else {
                        // 占位符
                        InputMaterialItem p = new InputMaterialItem();
                        p.setMaterialNumber("-"); p.setMaterialName("-"); p.setRecordType("-"); p.setRecordUnit("-"); p.setPlannedInput("-"); p.setActualInput("-");
                        inputMaterialItems.add(p);
                    }
                    productionData.setInputMaterialItems(inputMaterialItems);
        
                    // --- 2. 处理产出信息 (成品订单) ---
                    if (!finishedOrders.isEmpty()) {
                        BigDecimal totalPlannedOut = BigDecimal.ZERO;
                        BigDecimal totalActualOut = BigDecimal.ZERO;
                        BigDecimal totalNetWeight = BigDecimal.ZERO;
        
                        for (TBusOrderHead order : finishedOrders) {
                            String oNo = order.getOrderNo();
                            totalPlannedOut = totalPlannedOut.add(order.getBodyPlanPrdQty() != null ? order.getBodyPlanPrdQty() : BigDecimal.ZERO);
                                    
                            Long count = actualOutputCountMap.get(oNo);
                            if (count != null && count > 0) {
                                totalActualOut = totalActualOut.add(new BigDecimal(count).divide(getAValue(order), 2, BigDecimal.ROUND_HALF_UP));
                            }
                                    
                            BigDecimal weight = netWeightAggMap.get(oNo);
                            if (weight != null) totalNetWeight = totalNetWeight.add(weight);
                        }
        
                        productionData.setPlannedOutput(totalPlannedOut.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        productionData.setActualOutput(totalActualOut.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        productionData.setNetContentWeight(totalNetWeight.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        productionData.setPackagingWasteWeight("0.00");
                        productionData.setDefectiveWeight("0.00");
                        productionData.setDefectiveRate("0.00");
        
                        if (totalNetWeight.compareTo(BigDecimal.ZERO) > 0) {
                            productionData.setInputOutputRatio(totalActualInputSum.divide(totalNetWeight, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        } else productionData.setInputOutputRatio("-");
        
                        if (totalActualOut.compareTo(BigDecimal.ZERO) > 0) {
                            productionData.setMaterialConsumptionPerBox(rawMaterialInputSum.divide(totalActualOut, 4, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        } else productionData.setMaterialConsumptionPerBox("-");
                    } else {
                        productionData.setPlannedOutput("-"); productionData.setActualOutput("-"); productionData.setNetContentWeight("-");
                        productionData.setPackagingWasteWeight("-"); productionData.setDefectiveWeight("-"); productionData.setInputOutputRatio("-");
                        productionData.setMaterialConsumptionPerBox("-"); productionData.setDefectiveRate("-");
                    }
        
                    return productionData;
                })
                .collect(Collectors.toList());
    
        // 构建分页结果
        PageVo<ProductionData> pageResult = new PageVo<>();
        pageResult.setList(pagedResult);
        pageResult.setTotal(totalGroups);
        pageResult.setCurrent(current);
        pageResult.setSize(size);
    
        return pageResult;
    }
    
    /**
     * 调整开始日期为当天的00:00:00
     */
    private Date adjustStartDate(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * 调整结束日期为当天的23:59:59
     */
    private Date adjustEndDate(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * 根据recordType获取对应的标签（从字典中获取）
     */
    private String getRecordTypeLabelFromDict(String recordType) {
        if (recordType == null) {
            return "未知类型";
        }
        
        try {
            // 查询字典表获取对应的描述，使用Repository直接查询
            List<TSysCodeDsc> allRecords = tSysCodeDscRepository.findByCodeClId("RECORDTYPE0000");
            for (TSysCodeDsc codeDsc : allRecords) {
                if (recordType.equals(codeDsc.getCodeValue())) {
                    return codeDsc.getCodeDsc();
                }
            }
        } catch (Exception e) {
            // 如果查询字典失败，使用默认值
            System.out.println("查询字典失败: " + e.getMessage());
        }
        
        return getFallbackRecordTypeLabel(recordType);
    }

    private String getFallbackRecordTypeLabel(String recordType) {
        if (recordType == null) return "未知类型";
        // 如果字典中没有找到对应的值，使用默认逻辑
        switch (recordType) {
            case "1":
                return "原辅料投入";
            case "3":
                return "产成品";
            default:
                return "其他";
        }
    }

    /**
     * 计算实际产量中的A值
     */
    private BigDecimal getAValue(TBusOrderHead head) {
        if (head.getQtyPerJian() != null && head.getQtyPerJian().compareTo(BigDecimal.ZERO) > 0) {
            return head.getQtyPerJian();
        }
        String spec = head.getBodyMaterialSpecification();
        if (spec != null && spec.contains("*") && spec.contains("/")) {
            try {
                int start = spec.indexOf("*") + 1;
                int end = spec.indexOf("/", start);
                if (end > start) {
                    String sub = spec.substring(start, end).replaceAll("[^0-9.]", "");
                    if (!sub.isEmpty()) {
                        return new BigDecimal(sub);
                    }
                }
            } catch (Exception e) {
                // 解析失败忽略
            }
        }
        return BigDecimal.ONE;
    }
}