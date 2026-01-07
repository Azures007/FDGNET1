package org.thingsboard.server.service.report.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;
import org.thingsboard.server.dao.mes.dto.RawMaterialInputQueryDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.RawMaterialInputInfoVo;
import org.thingsboard.server.dao.mes.vo.RawMaterialInputReportVo;
import org.thingsboard.server.dao.mes.vo.ProcessGroupInfoVo;
import org.thingsboard.server.dao.mes.vo.ProcessMaterialInfoVo;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessHistoryRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessRecordRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessRepository;
import org.thingsboard.server.service.report.RawMaterialInputReportService;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 原料投入报表服务实现
 */
@Service
@Slf4j
public class RawMaterialInputReportServiceImpl implements RawMaterialInputReportService {

    @Autowired
    private OrderHeadRepository orderHeadRepository;
    
    @Autowired
    private OrderProcessRecordRepository orderProcessRecordRepository;

    @Autowired
    private OrderProcessRepository orderProcessRepository;

    @Autowired
    private OrderProcessHistoryRepository orderProcessHistoryRepository;
    
    @Autowired
    private org.thingsboard.server.dao.sql.mes.order.OrderPotCountRepository orderPotCountRepository;


    @Override
    public PageVo<RawMaterialInputReportVo> queryRawMaterialInputReport(Integer current, Integer size, RawMaterialInputQueryDto queryDto) {
        // 创建排序规则：按下单日期降序
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "billDate"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "orderId"));
        Sort sort = Sort.by(orders);
        
        Pageable pageable = PageRequest.of(current, size, sort);
        
        // 查询订单数据
        Page<TBusOrderHead> orderHeadPage = orderHeadRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 产品名称（模糊查询）
            if (!StringUtils.isEmpty(queryDto.getProductName())) {
                predicates.add(criteriaBuilder.like(root.get("bodyMaterialName"), "%" + queryDto.getProductName() + "%"));
            }
            
            // 下单日期-开始时间
            if (queryDto.getOrderDateStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("billDate"), queryDto.getOrderDateStart()));
            }
            
            // 下单日期-结束时间
            if (queryDto.getOrderDateEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("billDate"), queryDto.getOrderDateEnd()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        
        // 转换为返回结果
        PageVo<RawMaterialInputReportVo> result = new PageVo<>();
        result.setTotal((int) orderHeadPage.getTotalElements());
        
        List<RawMaterialInputReportVo> list = new ArrayList<>();
        for (TBusOrderHead order : orderHeadPage.getContent()) {
            RawMaterialInputReportVo vo = new RawMaterialInputReportVo();
            // 查询指定字段
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderTime(order.getBillDate());
            vo.setProductName(order.getBodyMaterialName());
            vo.setProductionLine(order.getVwkname());
            
            // 查询计划产量和实际产量
            calculateOutputData(vo, order.getOrderNo());
            
            // 查询原料投入信息列表（按工序分组）
            vo.setProcessGroupInfoList(getProcessGroupInfoList(order.getOrderNo()));
            
            list.add(vo);
        }
        
        result.setList(list);
        return result;
    }
    
    /**
     * 计算计划产量和实际产量
     * @param vo 报表VO对象
     * @param orderNo 订单号
     */
    private void calculateOutputData(RawMaterialInputReportVo vo, String orderNo) {
        // 从t_bus_order_head表中获取计划产量
        List<TBusOrderHead> orderHeadList = orderHeadRepository.findByOrderNo(orderNo);
        TBusOrderHead orderHead = (orderHeadList != null && !orderHeadList.isEmpty()) ? orderHeadList.get(0) : null;
        BigDecimal plannedOutput = (orderHead != null && orderHead.getBodyPlanPrdQty() != null) ? 
            orderHead.getBodyPlanPrdQty() : BigDecimal.ZERO;
        
        // 查询t_bus_order_process_history表中process_name为'外包装'的记录数量作为实际产量
        List<TBusOrderProcessHistory> actualOutputRecords = orderProcessHistoryRepository
            .findByOrderNoAndProcessName(orderNo, "外包装");
        BigDecimal actualOutput = new BigDecimal(actualOutputRecords.size());
        
        vo.setPlannedOutput(plannedOutput);
        vo.setActualOutput(actualOutput);
    }
    

    
    /**
     * 获取原料投入信息列表（按工序分组）
     * @param orderNo 订单号
     * @return 工序分组信息列表
     */
    private List<ProcessGroupInfoVo> getProcessGroupInfoList(String orderNo) {
        // 查询t_bus_order_process_record表中对应order_no的数据
        List<TBusOrderProcessRecord> processRecords = orderProcessRecordRepository.findAllByOrderNo(orderNo);
        
        // 按order_process_id分组处理
        Map<Integer, List<TBusOrderProcessRecord>> groupedRecords = processRecords.stream()
            .collect(Collectors.groupingBy(TBusOrderProcessRecord::getOrderProcessId));
            
        // 按工序名称和工序状态分组
        Map<String, List<ProcessMaterialInfoVo>> processMaterialGroupMap = new LinkedHashMap<>();
        
        for (Map.Entry<Integer, List<TBusOrderProcessRecord>> entry : groupedRecords.entrySet()) {
            Integer orderProcessId = entry.getKey();
            List<TBusOrderProcessRecord> records = entry.getValue();
            
            // 根据order_process_id查询t_bus_order_process表获取工序状态
            Optional<TBusOrderProcess> orderProcessOpt = orderProcessRepository.findById(orderProcessId);
            
            if (orderProcessOpt.isPresent()) {
                TBusOrderProcess orderProcess = orderProcessOpt.get();
                String processStatus = getProcessStatusDescription(orderProcess.getProcessStatus());
                
                // 为每个记录创建ProcessMaterialInfoVo
                for (TBusOrderProcessRecord record : records) {
                    ProcessMaterialInfoVo materialInfo = new ProcessMaterialInfoVo();
                    materialInfo.setMaterialCode(record.getMaterialNumber());
                    materialInfo.setMaterialName(record.getMaterialName());
                    materialInfo.setUnit(record.getRecordUnit());
//                    materialInfo.setOrderNo(orderNo);
                    
                    // 查询计划投入、实际投入、计划锅数和实际累计锅数
                    setProductionValuesForMaterialInfo(materialInfo, orderNo, orderProcessId, record.getMaterialNumber());
                    
                    // 使用工序名称和工序状态作为key进行分组
                    String groupKey = record.getProcessName() + "|" + processStatus;
                    processMaterialGroupMap.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(materialInfo);
                }
            }
        }
        
        // 构建ProcessGroupInfoVo列表
        List<ProcessGroupInfoVo> result = new ArrayList<>();
        for (Map.Entry<String, List<ProcessMaterialInfoVo>> entry : processMaterialGroupMap.entrySet()) {
            String[] keyParts = entry.getKey().split("\\|");
            String processName = keyParts[0];
            String processStatus = keyParts[1];
            
            ProcessGroupInfoVo groupInfo = new ProcessGroupInfoVo();
            groupInfo.setProcessName(processName);
            groupInfo.setProcessStatus(processStatus);
            groupInfo.setMaterialInfoList(entry.getValue());
            
            result.add(groupInfo);
        }
        
        return result;
    }
    
    /**
     * 设置生产相关值（计划投入、实际投入、计划锅数、实际累计锅数）
     * @param info 原料投入信息对象
     * @param orderNo 订单号
     * @param orderProcessId 工序执行表ID
     * @param materialNumber 物料编号
     */
    private void setProductionValues(RawMaterialInputInfoVo info, String orderNo, Integer orderProcessId, String materialNumber) {
        // 查询订单用料清单，获取计划投入量（mustQty -> plannedInput）
        List<TBusOrderHead> orderHeadList = orderHeadRepository.findByOrderNo(orderNo);
        List<Map> orderPpbomList = null;
        if (orderHeadList != null && !orderHeadList.isEmpty()) {
            Integer orderId = orderHeadList.get(0).getOrderId();
            orderPpbomList = orderHeadRepository.getOrderPPbomByOrderIdAndMidPpbomEntryInputProcess(orderId);
            
            // 查找匹配的物料信息
            for (Map ppbom : orderPpbomList) {
                String ppbomMaterialNumber = (String) ppbom.get("material_number");
                if (materialNumber != null && materialNumber.equals(ppbomMaterialNumber)) {
                    // 设置计划投入量 (mustQty -> plannedInput)
                    Object mustQtyObj = ppbom.get("must_qty");
                    Object planInputRatioObj = ppbom.get("plan_input_ratio"); // 计划投入比例
                    
                    if (mustQtyObj != null) {
                        BigDecimal mustQty = new BigDecimal(mustQtyObj.toString());
                        
                        // 如果存在计划投入比例，则调整计划投入量
                        if (planInputRatioObj != null) {
                            BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                            // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                            BigDecimal adjustedPlannedInput = mustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                            info.setPlannedInput(adjustedPlannedInput);
                        } else {
                            // 如果没有计划投入比例，则使用原始计划投入量
                            info.setPlannedInput(mustQty);
                        }
                    }
                    
                    // 设置计划锅数 (planPotCount -> plannedPotCount)
                    // 根据配方管理逻辑，计划锅数应该是该工序下所有物料中以锅数计算基准='1'的物料为准
                    Object standardInputObj = ppbom.get("mid_ppbom_entry_material_standard");
                    Object mustQty = ppbom.get("must_qty");
                    Object potCalculationBasis = ppbom.get("pot_calculation_basis"); // 锅数计算基准
                    
                    // 如果当前物料是锅数计算基准='1'的物料，则计算计划锅数
                    if (mustQty != null && standardInputObj != null && potCalculationBasis != null 
                        && "1".equals(potCalculationBasis.toString())) {
                        try {
                            BigDecimal mustQtyDecimal = new BigDecimal(mustQty.toString());
                            BigDecimal standardInput = new BigDecimal(standardInputObj.toString());
                            if (standardInput.compareTo(BigDecimal.ZERO) > 0) {
                                int calculatedPlanPotCount = mustQtyDecimal.divide(standardInput, 2, BigDecimal.ROUND_UP).intValue();
                                info.setPlannedPotCount(calculatedPlanPotCount);
                            }
                        } catch (Exception e) {
                            // 如果计算失败，跳过
                        }
                    } else if (info.getPlannedPotCount() == null || info.getPlannedPotCount() == 0) {
                        // 如果当前物料不是锅数计算基准='1'的物料，或者计算失败，则尝试使用订单头中的计划锅数
                        if (orderHeadList != null && !orderHeadList.isEmpty()) {
                            TBusOrderHead orderHead = orderHeadList.get(0);
                            if (orderHead.getBodyPotQty() != null) {
                                info.setPlannedPotCount(orderHead.getBodyPotQty());
                            }
                        }
                    }
                    
                    break;
                }
            }
        }
        
        // 获取累计投入量（实际投入量）(recordQtyTotal -> actualInput)
        // 需要通过orderProcessId获取对应的processId
        Integer processId = null;
        Optional<TBusOrderProcess> orderProcessOpt = orderProcessRepository.findById(orderProcessId);
        if (orderProcessOpt.isPresent()) {
            processId = orderProcessOpt.get().getProcessId().getProcessId();
        }
        
        // 使用getPpbomRecordQtyTotal方法，该方法返回订单工序的累计投入量
        if (processId != null && orderNo != null) {
            List<Map> recordQtyTotalRecords = orderProcessRecordRepository
                .getPpbomRecordQtyTotal(orderNo, processId, -1); // 使用orderNo和processId，-1表示不过滤materialId
            if (recordQtyTotalRecords != null) {
                for (Map record : recordQtyTotalRecords) {
                    String recordMaterialNumber = (String) record.get("material_number");
                    if (materialNumber != null && materialNumber.equals(recordMaterialNumber)) {
                        Object recordQtyObj = record.get("record_qty");
                        if (recordQtyObj != null) {
                            // 设置实际投入量 (recordQtyTotal -> actualInput)
                            info.setActualInput(new BigDecimal(recordQtyObj.toString()));
                            
                            // 设置单位
                            Object recordUnitObj = record.get("record_unit");
                            if (recordUnitObj != null) {
                                info.setUnit(recordUnitObj.toString());
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        // 如果上面的方法没有找到数据，回退到按orderProcessId查询并求和
        if (info.getActualInput() == null || info.getActualInput().compareTo(BigDecimal.ZERO) == 0) {
            List<TBusOrderProcessRecord> records = orderProcessRecordRepository
                .findAllByOrderProcessIdAndMaterialNumberAndRecordType(orderProcessId, materialNumber, "1");
            BigDecimal actualInput = BigDecimal.ZERO;
            for (TBusOrderProcessRecord record : records) {
                if (record.getRecordQty() != null) {
                    actualInput = actualInput.add(record.getRecordQty());
                }
            }
            info.setActualInput(actualInput);
        }
        
        // 设置实际累计锅数 (personalCount -> actualAccumulatedPotCount)
        if (orderProcessId != null && materialNumber != null) {
            // 从锅数记录表获取投入次数
            int cnt = orderPotCountRepository.sumInputCountByOrderProcessAndMaterialNumber(orderProcessId, materialNumber);
            info.setActualAccumulatedPotCount(cnt);
            
            // 如果获取不到，则使用getMinInputCountByOrderProcess方法
            if (info.getActualAccumulatedPotCount() == null || info.getActualAccumulatedPotCount() == 0) {
                int actualAccumulatedPotCount = orderPotCountRepository.getMinInputCountByOrderProcess(orderProcessId);
                info.setActualAccumulatedPotCount(actualAccumulatedPotCount);
            }
        }
        
        // 如果通过用料清单计算失败或未找到计划锅数，则从订单头获取
        if (info.getPlannedPotCount() == null || info.getPlannedPotCount() == 0) {
            if (orderHeadList != null && !orderHeadList.isEmpty()) {
                TBusOrderHead orderHead = orderHeadList.get(0);
                if (orderHead.getBodyPotQty() != null) {
                    info.setPlannedPotCount(orderHead.getBodyPotQty());
                }
            }
        }
    }
    
    /**
     * 设置生产相关值（计划投入、实际投入、计划锅数、实际累计锅数）
     * @param materialInfo 物料信息对象
     * @param orderNo 订单号
     * @param orderProcessId 工序执行表ID
     * @param materialNumber 物料编号
     */
    private void setProductionValuesForMaterialInfo(ProcessMaterialInfoVo materialInfo, String orderNo, Integer orderProcessId, String materialNumber) {
        // 查询订单用料清单，获取计划投入量（mustQty -> plannedInput）
        List<TBusOrderHead> orderHeadList = orderHeadRepository.findByOrderNo(orderNo);
        List<Map> orderPpbomList = null;
        if (orderHeadList != null && !orderHeadList.isEmpty()) {
            Integer orderId = orderHeadList.get(0).getOrderId();
            orderPpbomList = orderHeadRepository.getOrderPPbomByOrderIdAndMidPpbomEntryInputProcess(orderId);
            
            // 查找匹配的物料信息
            for (Map ppbom : orderPpbomList) {
                String ppbomMaterialNumber = (String) ppbom.get("material_number");
                if (materialNumber != null && materialNumber.equals(ppbomMaterialNumber)) {
                    // 设置计划投入量 (mustQty -> plannedInput)
                    Object mustQtyObj = ppbom.get("must_qty");
                    Object planInputRatioObj = ppbom.get("plan_input_ratio"); // 计划投入比例
                    
                    if (mustQtyObj != null) {
                        BigDecimal mustQty = new BigDecimal(mustQtyObj.toString());
                        
                        // 如果存在计划投入比例，则调整计划投入量
                        if (planInputRatioObj != null) {
                            BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                            // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                            BigDecimal adjustedPlannedInput = mustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                            materialInfo.setPlannedInput(adjustedPlannedInput);
                        } else {
                            // 如果没有计划投入比例，则使用原始计划投入量
                            materialInfo.setPlannedInput(mustQty);
                        }
                    }
                    
                    // 设置计划锅数 (planPotCount -> plannedPotCount)
                    // 根据配方管理逻辑，计划锅数应该是该工序下所有物料中以锅数计算基准='1'的物料为准
                    Object standardInputObj = ppbom.get("mid_ppbom_entry_material_standard");
                    Object mustQty = ppbom.get("must_qty");
                    Object potCalculationBasis = ppbom.get("pot_calculation_basis"); // 锅数计算基准
                    
                    // 如果当前物料是锅数计算基准='1'的物料，则计算计划锅数
                    if (mustQty != null && standardInputObj != null && potCalculationBasis != null 
                        && "1".equals(potCalculationBasis.toString())) {
                        try {
                            BigDecimal mustQtyDecimal = new BigDecimal(mustQty.toString());
                            BigDecimal standardInput = new BigDecimal(standardInputObj.toString());
                            if (standardInput.compareTo(BigDecimal.ZERO) > 0) {
                                int calculatedPlanPotCount = mustQtyDecimal.divide(standardInput, 2, BigDecimal.ROUND_UP).intValue();
                                materialInfo.setPlannedPotCount(calculatedPlanPotCount);
                            }
                        } catch (Exception e) {
                            // 如果计算失败，跳过
                        }
                    } else if (materialInfo.getPlannedPotCount() == null || materialInfo.getPlannedPotCount() == 0) {
                        // 如果当前物料不是锅数计算基准='1'的物料，或者计算失败，则尝试使用订单头中的计划锅数
                        if (orderHeadList != null && !orderHeadList.isEmpty()) {
                            TBusOrderHead orderHead = orderHeadList.get(0);
                            if (orderHead.getBodyPotQty() != null) {
                                materialInfo.setPlannedPotCount(orderHead.getBodyPotQty());
                            }
                        }
                    }
                    
                    break;
                }
            }
        }
        
        // 获取累计投入量（实际投入量）(recordQtyTotal -> actualInput)
        // 需要通过orderProcessId获取对应的processId
        Integer processId = null;
        Optional<TBusOrderProcess> orderProcessOpt = orderProcessRepository.findById(orderProcessId);
        if (orderProcessOpt.isPresent()) {
            processId = orderProcessOpt.get().getProcessId().getProcessId();
        }
        
        // 使用getPpbomRecordQtyTotal方法，该方法返回订单工序的累计投入量
        if (processId != null && orderNo != null) {
            List<Map> recordQtyTotalRecords = orderProcessRecordRepository
                .getPpbomRecordQtyTotal(orderNo, processId, -1); // 使用orderNo和processId，-1表示不过滤materialId
            if (recordQtyTotalRecords != null) {
                for (Map record : recordQtyTotalRecords) {
                    String recordMaterialNumber = (String) record.get("material_number");
                    if (materialNumber != null && materialNumber.equals(recordMaterialNumber)) {
                        Object recordQtyObj = record.get("record_qty");
                        if (recordQtyObj != null) {
                            // 设置实际投入量 (recordQtyTotal -> actualInput)
                            materialInfo.setActualInput(new BigDecimal(recordQtyObj.toString()));
                        }
                        break;
                    }
                }
            }
        }
        
        // 如果上面的方法没有找到数据，回退到按orderProcessId查询并求和
        if (materialInfo.getActualInput() == null || materialInfo.getActualInput().compareTo(BigDecimal.ZERO) == 0) {
            List<TBusOrderProcessRecord> records = orderProcessRecordRepository
                .findAllByOrderProcessIdAndMaterialNumberAndRecordType(orderProcessId, materialNumber, "1");
            BigDecimal actualInput = BigDecimal.ZERO;
            for (TBusOrderProcessRecord record : records) {
                if (record.getRecordQty() != null) {
                    actualInput = actualInput.add(record.getRecordQty());
                }
            }
            materialInfo.setActualInput(actualInput);
        }
        
        // 设置实际累计锅数 (personalCount -> actualAccumulatedPotCount)
        if (orderProcessId != null && materialNumber != null) {
            // 从锅数记录表获取投入次数
            int cnt = orderPotCountRepository.sumInputCountByOrderProcessAndMaterialNumber(orderProcessId, materialNumber);
            materialInfo.setActualAccumulatedPotCount(cnt);
            
            // 如果获取不到，则使用getMinInputCountByOrderProcess方法
            if (materialInfo.getActualAccumulatedPotCount() == null || materialInfo.getActualAccumulatedPotCount() == 0) {
                int actualAccumulatedPotCount = orderPotCountRepository.getMinInputCountByOrderProcess(orderProcessId);
                materialInfo.setActualAccumulatedPotCount(actualAccumulatedPotCount);
            }
        }
        
        // 如果通过用料清单计算失败或未找到计划锅数，则从订单头获取
        if (materialInfo.getPlannedPotCount() == null || materialInfo.getPlannedPotCount() == 0) {
            if (orderHeadList != null && !orderHeadList.isEmpty()) {
                TBusOrderHead orderHead = orderHeadList.get(0);
                if (orderHead.getBodyPotQty() != null) {
                    materialInfo.setPlannedPotCount(orderHead.getBodyPotQty());
                }
            }
        }
    }
    
    /**
     * 获取分组内物料的最小投入次数
     * @param orderProcessId 工序执行ID
     * @param groupMaterialCodes 分组内的物料编码列表
     * @param groupCode 分组编码
     * @return 分组内物料的最小投入次数
     */
    public int getMinInputCountByGroup(Integer orderProcessId, List<String> groupMaterialCodes, String groupCode) {
        if (groupMaterialCodes == null || groupMaterialCodes.isEmpty()) {
            return 0;
        }

        int minInputCount = Integer.MAX_VALUE;
        boolean hasUnusedMaterials = false;

        for (String materialCode : groupMaterialCodes) {
            int inputCount = orderPotCountRepository.sumInputCountByOrderProcessAndMaterialNumberAndGroup(orderProcessId, materialCode, groupCode);
            if (inputCount == 0) {
                hasUnusedMaterials = true;
                break;
            }
            if (inputCount < minInputCount) {
                minInputCount = inputCount;
            }
        }

        // 如果有未投入的物料，返回0
        if (hasUnusedMaterials) {
            return 0;
        }

        // 返回分组内物料的最小投入次数
        return minInputCount == Integer.MAX_VALUE ? 0 : minInputCount;
    }
    
    /**
     * 将工序状态代码转换为具体描述
     * @param statusCode 状态代码
     * @return 状态描述
     */
    private String getProcessStatusDescription(String statusCode) {
        if (statusCode == null) {
            return "未知状态";
        }
        switch (statusCode) {
            case "0":
                return "未开工";
            case "1":
                return "已开工";
            case "2":
                return "暂停";
            case "3":
                return "已完工";
            case "4":
                return "移交中";
            case "5":
                return "移交驳回";
            default:
                return "未知状态";
        }
    }
}