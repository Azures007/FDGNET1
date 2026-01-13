package org.thingsboard.server.dao.mes.production;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.vo.ProductionData;
import org.thingsboard.server.common.data.mes.vo.InputMaterialItem;
import org.thingsboard.server.dao.mes.dto.ProductionDataQueryDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.sql.mes.production.ProductionDataRepository;
import org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPPBom;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.dao.mes.tSysCodeDsc.TSysCodeDscService;

import java.math.BigDecimal;
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
    public PageVo<ProductionData> queryProductionData(int current, int size, ProductionDataQueryDto queryDto) {
        // 处理时间范围，确保开始时间是当天00:00:00，结束时间是当天23:59:59
        Date startTime = adjustStartDate(queryDto.getStartTime());
        Date endTime = adjustEndDate(queryDto.getEndTime());
        
        // 根据时间范围查询订单头信息
        List<TBusOrderHead> orderHeads = productionDataRepository.findByTimeRange(startTime, endTime);

        // 按产线和日期分组
        Map<String, Map<String, List<TBusOrderHead>>> groupedByProductionLineAndDate = orderHeads.stream()
                .filter(head -> head.getVwkname() != null) // 确保产线不为空
                .collect(Collectors.groupingBy(
                        TBusOrderHead::getVwkname, // 按产线分组
                        Collectors.groupingBy(head -> {
                            // 提取日期部分，格式化为yyyy-MM-dd
                            if (head.getBillDate() != null) {
                                return new java.text.SimpleDateFormat("yyyy-MM-dd").format(head.getBillDate());
                            } else {
                                return "未知日期";
                            }
                        })
                ));

        // 创建结果列表
        List<ProductionData> result = new ArrayList<>();

        // 处理每个产线和日期的组合
        for (Map.Entry<String, Map<String, List<TBusOrderHead>>> lineEntry : groupedByProductionLineAndDate.entrySet()) {
            String productionLine = lineEntry.getKey();
            Map<String, List<TBusOrderHead>> ordersByDate = lineEntry.getValue();
            
            // 对每个日期进行处理
            for (Map.Entry<String, List<TBusOrderHead>> dateEntry : ordersByDate.entrySet()) {
                String date = dateEntry.getKey();
                List<TBusOrderHead> ordersInLineAndDate = dateEntry.getValue();

                // 收集订单号
                List<String> orderNos = ordersInLineAndDate.stream()
                        .map(TBusOrderHead::getOrderNo)
                        .collect(Collectors.toList());

                // 计算计划产量：同一产线同一天的body_plan_prd_qty之和
                BigDecimal plannedOutput = ordersInLineAndDate.stream()
                        .map(order -> order.getBodyPlanPrdQty() != null ? order.getBodyPlanPrdQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 查询外包装工序的历史记录（process_number为GX-011）
                List<TBusOrderProcessHistory> packagingHistories = productionDataRepository.findPackagingProcesses(orderNos);

                // 计算实际产量：外包装工序记录的条数总和
                BigDecimal actualOutput = new BigDecimal(packagingHistories.size());

                // 净含量重量：外包装工序记录的record_qty字段值的总和
                BigDecimal netContentWeight = packagingHistories.stream()
                        .map(history -> history.getRecordQty() != null ? history.getRecordQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 查询投入物料信息
                List<TBusOrderProcessHistory> inputMaterialHistories = productionDataRepository.findInputMaterials(orderNos);

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
                        
                        // 根据recordType到字典中寻找对应的值，设置label
                        String recordTypeLabel = getRecordTypeLabelFromDict(firstHistory.getRecordType());
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
                        item.setRecordType("计划物料");
                        item.setActualInput(BigDecimal.ZERO);
                    }
                    
                    // 设置计划投入
                    item.setPlannedInput(materialPlannedInputMap.getOrDefault(materialNumber, BigDecimal.ZERO));
                    
                    inputMaterialItems.add(item);
                }

                // 创建主实体对象
                ProductionData productionData = new ProductionData();
                productionData.setProductionLine(productionLine);
                
                // 设置具体的日期
                productionData.setDate(date);
                
                productionData.setPlannedOutput(plannedOutput);
                productionData.setActualOutput(actualOutput);
                productionData.setNetContentWeight(netContentWeight);
                
                // 其他字段暂时设为0，可根据业务需求进一步完善
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
                // 原辅料就是 record_type 为 1 的记录
                BigDecimal rawMaterialInput = inputMaterialHistories.stream()
                        .filter(h -> "1".equals(h.getRecordType()))
                        .map(h -> h.getRecordQty() != null ? h.getRecordQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal materialConsumptionPerBox = BigDecimal.ZERO;
                if (actualOutput != null && actualOutput.compareTo(BigDecimal.ZERO) > 0) {
                    materialConsumptionPerBox = rawMaterialInput.divide(actualOutput, 4, BigDecimal.ROUND_HALF_UP);
                }
                productionData.setMaterialConsumptionPerBox(materialConsumptionPerBox);
                
                // 废次品比率：如果实际产量大于0，则为废次品重量/实际产量
                BigDecimal defectiveRate = BigDecimal.ZERO;
                if (actualOutput.compareTo(BigDecimal.ZERO) > 0 && productionData.getDefectiveWeight() != null) {
                    defectiveRate = productionData.getDefectiveWeight().divide(actualOutput, 4, BigDecimal.ROUND_HALF_UP);
                }
                productionData.setDefectiveRate(defectiveRate);
                
                productionData.setInputMaterialItems(inputMaterialItems);

                result.add(productionData);
            }
        }

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
            // 由于Repository中没有直接通过codeValue查询的方法，我们使用findByCodeClIdAndCodeDsc方法
            // 或者通过其他方式查询，这里我们使用一个更通用的方法
            List<TSysCodeDsc> allRecords = tSysCodeDscRepository.findByCodeClId("recordtype0000");
            for (TSysCodeDsc codeDsc : allRecords) {
                if (recordType.equals(codeDsc.getCodeValue())) {
                    return codeDsc.getCodeDsc();
                }
            }
        } catch (Exception e) {
            // 如果查询字典失败，使用默认值
            System.out.println("查询字典失败: " + e.getMessage());
        }
        
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