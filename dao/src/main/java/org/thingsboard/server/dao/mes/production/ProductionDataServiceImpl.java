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
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.mes.vo.InputMaterialItem;
import org.thingsboard.server.common.data.mes.vo.ProductionData;
import org.thingsboard.server.common.data.mes.vo.ProductionDataExcelVo;
import org.thingsboard.server.dao.mes.dto.ProductionDataQueryDto;
import org.thingsboard.server.dao.mes.tSysCodeDsc.TSysCodeDscService;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.sql.mes.production.ProductionDataRepository;
import org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscRepository;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ProductionDataServiceImpl implements ProductionDataService {

    @Autowired
    private ProductionDataRepository productionDataRepository;
    
    @Autowired
    private TSysCodeDscService tSysCodeDscService;
    
    @Autowired
    private TSysCodeDscRepository tSysCodeDscRepository;

    @Override
    public void exportProductionData(ProductionDataQueryDto queryDto, HttpServletResponse response) {
        try {
            // 获取所有数据
            PageVo<ProductionData> allData = queryProductionData(0, 10000, queryDto);
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
        List<ProductionDataExcelVo> excelDataList = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return excelDataList;
        }

        for (ProductionData data : list) {
            String groupKey = data.getProductionLine() + "_" + data.getDate();
            List<InputMaterialItem> materialItems = data.getInputMaterialItems();

            if (materialItems == null || materialItems.isEmpty()) {
                ProductionDataExcelVo vo = new ProductionDataExcelVo();
                fillBaseInfo(vo, data, groupKey);
                excelDataList.add(vo);
            } else {
                for (InputMaterialItem materialItem : materialItems) {
                    ProductionDataExcelVo vo = new ProductionDataExcelVo();
                    fillBaseInfo(vo, data, groupKey);
                    
                    // 投入信息
                    vo.setMaterialNumber(materialItem.getMaterialNumber());
                    vo.setMaterialName(materialItem.getMaterialName());
                    vo.setRecordType(materialItem.getRecordType());
                    vo.setRecordUnit(materialItem.getRecordUnit());
                    vo.setPlannedInput(materialItem.getPlannedInput() != null ? materialItem.getPlannedInput().toString() : "0");
                    vo.setActualInput(materialItem.getActualInput() != null ? materialItem.getActualInput().toString() : "0");
                    
                    excelDataList.add(vo);
                }
            }
        }
        return excelDataList;
    }

    private void fillBaseInfo(ProductionDataExcelVo vo, ProductionData data, String groupKey) {
        vo.setGroupKey(groupKey);
        // 基础信息
        vo.setDate(data.getDate());
        vo.setProductionLine(data.getProductionLine());
        
        // 损耗信息
        vo.setPackagingWasteWeight(data.getPackagingWasteWeight() != null ? data.getPackagingWasteWeight().toString() : "0");
        vo.setDefectiveWeight(data.getDefectiveWeight() != null ? data.getDefectiveWeight().toString() : "0");
        
        // 产出信息
        vo.setPlannedOutput(data.getPlannedOutput() != null ? data.getPlannedOutput().toString() : "0");
        vo.setActualOutput(data.getActualOutput() != null ? data.getActualOutput().toString() : "0");
        vo.setNetContentWeight(data.getNetContentWeight() != null ? data.getNetContentWeight().toString() : "0");
        
        // 投入产出比
        vo.setInputOutputRatio(data.getInputOutputRatio() != null ? data.getInputOutputRatio().toString() + "%" : "0%");
        vo.setMaterialConsumptionPerBox(data.getMaterialConsumptionPerBox() != null ? data.getMaterialConsumptionPerBox().toString() : "0");
        vo.setDefectiveRate(data.getDefectiveRate() != null ? data.getDefectiveRate().toString() : "0");
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
    public PageVo<ProductionData> queryProductionData(int current, int size, ProductionDataQueryDto queryDto) {
        // 处理时间范围，确保开始时间是当天00:00:00，结束时间是当天23:59:59
        Date startTime = adjustStartDate(queryDto.getStartTime());
        Date endTime = adjustEndDate(queryDto.getEndTime());
        
        // 根据时间范围查询订单头信息
        List<TBusOrderHead> orderHeads = productionDataRepository.findByTimeRange(startTime, endTime);
        if (orderHeads.isEmpty()) {
            PageVo<ProductionData> emptyPage = new PageVo<>();
            emptyPage.setList(new ArrayList<>());
            emptyPage.setTotal(0);
            emptyPage.setCurrent(current);
            emptyPage.setSize(size);
            return emptyPage;
        }

        // 收集所有订单号用于批量查询
        List<String> allOrderNos = orderHeads.stream()
                .map(TBusOrderHead::getOrderNo)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询所有相关的历史记录并按订单号分组缓存
        Map<String, List<TBusOrderProcessHistory>> allPackagingHistoriesMap = productionDataRepository.findPackagingProcesses(allOrderNos).stream()
                .collect(Collectors.groupingBy(TBusOrderProcessHistory::getOrderNo));
        
        Map<String, List<TBusOrderProcessHistory>> allInputMaterialHistoriesMap = productionDataRepository.findInputMaterials(allOrderNos).stream()
                .collect(Collectors.groupingBy(TBusOrderProcessHistory::getOrderNo));

        // 预加载字典数据以提高效率
        List<TSysCodeDsc> dictList = tSysCodeDscRepository.findByCodeClId("RECORDTYPE0000");
        Map<String, String> dictMap = dictList.stream()
                .collect(Collectors.toMap(TSysCodeDsc::getCodeValue, TSysCodeDsc::getCodeDsc, (v1, v2) -> v1));

        // 按产线和日期分组
        final SimpleDateFormat dateDaySdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Map<String, List<TBusOrderHead>>> groupedByProductionLineAndDate = orderHeads.stream()
                .filter(head -> head.getVwkname() != null) // 确保产线不为空
                .collect(Collectors.groupingBy(
                        TBusOrderHead::getVwkname, // 按产线分组
                        Collectors.groupingBy(head -> {
                            // 提取日期部分，格式化为yyyy-MM-dd
                            if (head.getBillDate() != null) {
                                return dateDaySdf.format(head.getBillDate());
                            } else {
                                return "未知日期";
                            }
                        })
                ));

        // 创建结果列表
        List<ProductionData> result = groupedByProductionLineAndDate.entrySet().parallelStream()
                .flatMap(lineEntry -> {
                    String productionLine = lineEntry.getKey();
                    Map<String, List<TBusOrderHead>> ordersByDate = lineEntry.getValue();
                    
                    return ordersByDate.entrySet().stream().map(dateEntry -> {
                        String date = dateEntry.getKey();
                        List<TBusOrderHead> ordersInLineAndDate = dateEntry.getValue();

                        // 收集该组的订单号
                        List<String> groupOrderNos = ordersInLineAndDate.stream()
                                .map(TBusOrderHead::getOrderNo)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        // 从缓存中获取外包装工序历史记录
                        List<TBusOrderProcessHistory> packagingHistories = new ArrayList<>();
                        groupOrderNos.forEach(no -> {
                            List<TBusOrderProcessHistory> histories = allPackagingHistoriesMap.get(no);
                            if (histories != null) packagingHistories.addAll(histories);
                        });

                        // 计算实际产量：外包装工序记录的条数总和
                        BigDecimal actualOutput = new BigDecimal(packagingHistories.size());

                        // 净含量重量：外包装工序记录的record_qty字段值的总和
                        BigDecimal netContentWeight = packagingHistories.stream()
                                .map(history -> history.getRecordQty() != null ? history.getRecordQty() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        // 从缓存中获取投入物料信息
                        List<TBusOrderProcessHistory> inputMaterialHistories = new ArrayList<>();
                        groupOrderNos.forEach(no -> {
                            List<TBusOrderProcessHistory> histories = allInputMaterialHistoriesMap.get(no);
                            if (histories != null) inputMaterialHistories.addAll(histories);
                        });

                        // 计算计划产量：同一产线同一天的body_plan_prd_qty之和
                        BigDecimal plannedOutput = ordersInLineAndDate.stream()
                                .map(order -> order.getBodyPlanPrdQty() != null ? order.getBodyPlanPrdQty() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        // 计算计划投入：从订单的BOM中获取并按物料编码聚合
                        Map<String, BigDecimal> materialPlannedInputMap = new HashMap<>();
                        Map<String, TBusOrderPPBom> materialInfoMap = new HashMap<>();
                        for (TBusOrderHead order : ordersInLineAndDate) {
                            if (order.getTBusOrderPPBomSet() != null) {
                                for (TBusOrderPPBom bom : order.getTBusOrderPPBomSet()) {
                                    String materialNumber = bom.getMaterialNumber();
                                    if (materialNumber != null) {
                                        BigDecimal qty = bom.getMustQty() != null ? bom.getMustQty() : BigDecimal.ZERO;
                                        materialPlannedInputMap.put(materialNumber, materialPlannedInputMap.getOrDefault(materialNumber, BigDecimal.ZERO).add(qty));
                                        materialInfoMap.putIfAbsent(materialNumber, bom);
                                    }
                                }
                            }
                        }

                        // 构建投入信息列表，按物料编码分组并合并相同物料的数据
                        Map<String, List<TBusOrderProcessHistory>> groupedByMaterialNumber = inputMaterialHistories.stream()
                                .filter(h -> h.getMaterialNumber() != null)
                                .collect(Collectors.groupingBy(TBusOrderProcessHistory::getMaterialNumber));

                        // 合并所有物料编码（包括只有BOM没有历史记录的）
                        Set<String> allMaterialNumbers = new HashSet<>(groupedByMaterialNumber.keySet());
                        allMaterialNumbers.addAll(materialPlannedInputMap.keySet());
                        
                        List<InputMaterialItem> inputMaterialItems = new ArrayList<>();
                        for (String materialNumber : allMaterialNumbers) {
                            List<TBusOrderProcessHistory> historiesWithSameMaterial = groupedByMaterialNumber.get(materialNumber);
                            
                            InputMaterialItem item = new InputMaterialItem();
                            item.setMaterialNumber(materialNumber);
                            
                            if (historiesWithSameMaterial != null && !historiesWithSameMaterial.isEmpty()) {
                                // 获取第一个记录的基础信息
                                TBusOrderProcessHistory firstHistory = historiesWithSameMaterial.get(0);
                                item.setMaterialName(firstHistory.getMaterialName());
                                item.setRecordUnit(firstHistory.getRecordUnit());
                                
                                // 使用缓存的字典数据设置label
                                String recordType = firstHistory.getRecordType();
                                String recordTypeLabel = dictMap.getOrDefault(recordType, getFallbackRecordTypeLabel(recordType));
                                item.setRecordType(recordTypeLabel);
                                
                                // 计算总的实际投入量
                                BigDecimal totalActualInput = historiesWithSameMaterial.stream()
                                        .map(history -> history.getRecordQty() != null ? history.getRecordQty() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                
                                item.setActualInput(totalActualInput);
                            } else {
                                // 只有BOM信息的情况
                                TBusOrderPPBom bom = materialInfoMap.get(materialNumber);
                                item.setMaterialName(bom != null ? bom.getMaterialName() : "");
                                item.setRecordUnit(bom != null ? bom.getUnit() : "");
                                item.setRecordType("-");
                                item.setActualInput(BigDecimal.ZERO);
                            }
                            
                            // 设置计划投入
                            item.setPlannedInput(materialPlannedInputMap.getOrDefault(materialNumber, BigDecimal.ZERO));
                            
                            inputMaterialItems.add(item);
                        }

                        // 创建主实体对象
                        ProductionData productionData = new ProductionData();
                        productionData.setProductionLine(productionLine);
                        productionData.setDate(date);
                        productionData.setPlannedOutput(plannedOutput);
                        productionData.setActualOutput(actualOutput);
                        productionData.setNetContentWeight(netContentWeight);
                        
                        productionData.setPackagingWasteWeight(BigDecimal.ZERO);
                        productionData.setDefectiveWeight(BigDecimal.ZERO);
                        
                        // 计算总投入量
                        BigDecimal totalInput = inputMaterialItems.stream()
                                .map(item -> item.getActualInput() != null ? item.getActualInput() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                        // 计算投入产出比 = 全部材料实际投入总和 / 净含量重量 * 100%
                        BigDecimal inputOutputRatio = BigDecimal.ZERO;
                        if (netContentWeight != null && netContentWeight.compareTo(BigDecimal.ZERO) > 0) {
                            inputOutputRatio = totalInput.divide(netContentWeight, 4, BigDecimal.ROUND_HALF_UP)
                                    .multiply(new BigDecimal("100"));
                        }
                        productionData.setInputOutputRatio(inputOutputRatio);
                        
                        // 计算单箱原辅料消耗 = 原辅料实际投入 / 实际产量
                        BigDecimal rawMaterialInput = inputMaterialHistories.stream()
                                .filter(h -> "1".equals(h.getRecordType()))
                                .map(h -> h.getRecordQty() != null ? h.getRecordQty() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                        BigDecimal materialConsumptionPerBox = BigDecimal.ZERO;
                        if (actualOutput != null && actualOutput.compareTo(BigDecimal.ZERO) > 0) {
                            materialConsumptionPerBox = rawMaterialInput.divide(actualOutput, 4, BigDecimal.ROUND_HALF_UP);
                        }
                        productionData.setMaterialConsumptionPerBox(materialConsumptionPerBox);
                        
                        // 废次品比率
                        BigDecimal defectiveRate = BigDecimal.ZERO;
                        if (actualOutput.compareTo(BigDecimal.ZERO) > 0 && productionData.getDefectiveWeight() != null) {
                            defectiveRate = productionData.getDefectiveWeight().divide(actualOutput, 4, BigDecimal.ROUND_HALF_UP);
                        }
                        productionData.setDefectiveRate(defectiveRate);
                        
                        productionData.setInputMaterialItems(inputMaterialItems);
                        return productionData;
                    });
                })
                .sorted(Comparator.comparing(ProductionData::getDate).reversed().thenComparing(ProductionData::getProductionLine))
                .collect(Collectors.toList());

        // 实现分页逻辑
        int total = result.size();
        int startIndex = current * size;
        int endIndex = Math.min(startIndex + size, total);

        List<ProductionData> pagedResult = new ArrayList<>();
        if (startIndex < total) {
            pagedResult = result.subList(startIndex, endIndex);
        }

        // 构建分页结果
        PageVo<ProductionData> pageResult = new PageVo<>();
        pageResult.setList(pagedResult);
        pageResult.setTotal(total);
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
}