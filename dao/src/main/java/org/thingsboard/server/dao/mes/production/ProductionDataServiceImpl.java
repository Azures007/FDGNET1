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

                // 计算实际产量：外包装数据的record_qty总和
                BigDecimal actualOutput = packagingHistories.stream()
                        .map(history -> history.getRecordQty() != null ? history.getRecordQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 净含量重量：同样取外包装数据的record_qty总和
                BigDecimal netContentWeight = packagingHistories.stream()
                        .map(history -> history.getRecordQty() != null ? history.getRecordQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 查询投入物料信息
                List<TBusOrderProcessHistory> inputMaterialHistories = productionDataRepository.findInputMaterials(orderNos);

                // 构建投入信息列表，按物料编码分组并合并相同物料的数据
                Map<String, List<TBusOrderProcessHistory>> groupedByMaterialNumber = inputMaterialHistories.stream()
                        .collect(Collectors.groupingBy(TBusOrderProcessHistory::getMaterialNumber));
                
                List<InputMaterialItem> inputMaterialItems = new ArrayList<>();
                for (Map.Entry<String, List<TBusOrderProcessHistory>> entry : groupedByMaterialNumber.entrySet()) {
                    List<TBusOrderProcessHistory> historiesWithSameMaterial = entry.getValue();
                    
                    // 获取第一个记录的基础信息
                    TBusOrderProcessHistory firstHistory = historiesWithSameMaterial.get(0);
                    
                    InputMaterialItem item = new InputMaterialItem();
                    item.setMaterialNumber(firstHistory.getMaterialNumber());
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
                    
                    // 计划投入先取0
                    item.setPlannedInput(BigDecimal.ZERO);
                    
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
                
                // 计算投入产出比 (实际产量 / 总投入量)，如果总投入量为0则设为0
                BigDecimal totalInput = inputMaterialItems.stream()
                        .map(item -> item.getActualInput() != null ? item.getActualInput() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal inputOutputRatio = BigDecimal.ZERO;
                if (totalInput.compareTo(BigDecimal.ZERO) > 0) {
                    inputOutputRatio = actualOutput.divide(totalInput, 4, BigDecimal.ROUND_HALF_UP);
                }
                productionData.setInputOutputRatio(inputOutputRatio);
                
                productionData.setMaterialConsumptionPerBox(BigDecimal.ZERO);
                
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