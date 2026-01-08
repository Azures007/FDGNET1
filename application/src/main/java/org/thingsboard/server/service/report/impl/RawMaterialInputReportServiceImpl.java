package org.thingsboard.server.service.report.impl;

import com.alibaba.excel.EasyExcel;
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
import org.thingsboard.server.common.data.mes.sys.TSysRecipeInput;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeProductBinding;
import org.thingsboard.server.dao.mes.dto.RawMaterialInputQueryDto;
import org.thingsboard.server.dao.mes.vo.*;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessHistoryRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessRecordRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessRepository;
import org.thingsboard.server.service.report.RawMaterialInputReportService;
import org.thingsboard.server.vo.RawMaterialInputReportExcelVo;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Autowired
    private org.thingsboard.server.dao.sql.mes.recipe.TSysRecipeProductBindingRepository recipeProductBindingRepository;

    @Autowired
    private org.thingsboard.server.dao.sql.mes.recipe.TSysRecipeInputRepository recipeInputRepository;

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
    
    @Override
    public void exportRawMaterialInputReport(Integer current, Integer size, RawMaterialInputQueryDto queryDto, HttpServletResponse response) {
        try {
            // 获取所有数据（不分页）
            RawMaterialInputQueryDto allDataQuery = new RawMaterialInputQueryDto();
            allDataQuery.setProductName(queryDto.getProductName());
            allDataQuery.setOrderDateStart(queryDto.getOrderDateStart());
            allDataQuery.setOrderDateEnd(queryDto.getOrderDateEnd());
            
            // 查询所有订单数据
            List<RawMaterialInputReportVo> allDataList = getAllRawMaterialInputReportData(allDataQuery);
            
            // 转换为Excel导出格式
            List<RawMaterialInputReportExcelVo> excelDataList = convertToExcelData(allDataList);
            
            // 使用EasyExcel直接写入响应流
            String fileName = "原料投入报表_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            EasyExcel.write(response.getOutputStream(), RawMaterialInputReportExcelVo.class)
                .sheet("原料投入报表")
                .doWrite(excelDataList);
        } catch (Exception e) {
            log.error("导出原料投入报表失败: ", e);
            throw new RuntimeException("导出原料投入报表失败");
        }
    }
    
    /**
     * 获取所有原料投入报表数据（不分页）
     * @param queryDto 查询条件
     * @return 所有数据列表
     */
    private List<RawMaterialInputReportVo> getAllRawMaterialInputReportData(RawMaterialInputQueryDto queryDto) {
        // 创建排序规则：按下单日期降序
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "billDate"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "orderId"));
        Sort sort = Sort.by(orders);
        
        // 使用一个大页面大小来获取所有数据
        Pageable pageable = PageRequest.of(0, 10000, sort); // 假设不超过10000条记录
        
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
        
        return list;
    }
    
    /**
     * 转换为Excel导出数据格式
     * @param reportDataList 报表数据列表
     * @return Excel数据列表
     */
    private List<RawMaterialInputReportExcelVo> convertToExcelData(List<RawMaterialInputReportVo> reportDataList) {
        List<RawMaterialInputReportExcelVo> excelDataList = new ArrayList<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (RawMaterialInputReportVo reportVo : reportDataList) {
            // 为每个工序组创建一行数据
            if (reportVo.getProcessGroupInfoList() != null) {
                for (ProcessGroupInfoVo processGroup : reportVo.getProcessGroupInfoList()) {
                    if (processGroup.getMaterialInfoList() != null) {
                        for (ProcessMaterialInfoVo materialInfo : processGroup.getMaterialInfoList()) {
                            RawMaterialInputReportExcelVo excelVo = new RawMaterialInputReportExcelVo();
                            
                            // 设置订单基础信息
                            excelVo.setOrderNo(reportVo.getOrderNo());
                            excelVo.setOrderTime(reportVo.getOrderTime() != null ? sdf.format(reportVo.getOrderTime()) : "");
                            excelVo.setProductionLine(reportVo.getProductionLine());
                            excelVo.setProductName(reportVo.getProductName());
                            
                            // 设置合格品信息
                            excelVo.setPlannedOutput(reportVo.getPlannedOutput() != null ? reportVo.getPlannedOutput().toString() : "0");
                            excelVo.setActualOutput(reportVo.getActualOutput() != null ? reportVo.getActualOutput().toString() : "0");
                            
                            // 设置原料投入信息
                            excelVo.setProcessName(processGroup.getProcessName());
                            excelVo.setProcessStatus(processGroup.getProcessStatus());
                            excelVo.setMaterialCode(materialInfo.getMaterialCode());
                            excelVo.setMaterialName(materialInfo.getMaterialName());
                            excelVo.setUnit(materialInfo.getUnit());
                            excelVo.setPlannedInput(materialInfo.getPlannedInput() != null ? materialInfo.getPlannedInput().toString() : "0");
                            excelVo.setActualInput(materialInfo.getActualInput() != null ? materialInfo.getActualInput().toString() : "0");
                            excelVo.setPlannedPotCount(materialInfo.getPlannedPotCount() != null ? materialInfo.getPlannedPotCount().toString() : "0");
                            excelVo.setActualAccumulatedPotCount(materialInfo.getActualAccumulatedPotCount() != null ? materialInfo.getActualAccumulatedPotCount().toString() : "0");
                            excelVo.setDefectiveWeight(materialInfo.getDefectiveWeight() != null ? materialInfo.getDefectiveWeight().toString() : "0");
                            
                            excelDataList.add(excelVo);
                        }
                    }
                }
            }
        }
        
        return excelDataList;
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
            
            // 获取订单产品编码和工序信息，用于配方管理逻辑
            String orderProductCode = orderHeadList.get(0).getBodyMaterialNumber();
            Optional<TBusOrderProcess> orderProcessOpt = orderProcessRepository.findById(orderProcessId);
            String currentProcessNumber = null;
            if (orderProcessOpt.isPresent()) {
                currentProcessNumber = orderProcessOpt.get().getProcessId().getProcessNumber();
            }
            
            // 获取配方管理信息
            Map<String, TSysRecipeInput> recipeMaterialMap = new HashMap<>();
            if (orderProductCode != null && currentProcessNumber != null) {
                List<TSysRecipeProductBinding> productBindings = recipeProductBindingRepository.findByProductCodeAndRecipeStatusEnabled(orderProductCode);
                for (TSysRecipeProductBinding binding : productBindings) {
                    List<TSysRecipeInput> recipeInputs = recipeInputRepository.findByRecipeId(binding.getRecipeId());
                    for (TSysRecipeInput input : recipeInputs) {
                        if (currentProcessNumber.equals(input.getProcessNumber())) {
                            recipeMaterialMap.put(input.getSemiFinishedProductCode()+"_"+input.getMaterialCode(), input);
                        }
                    }
                }
            }
            
            // 首先查找锅数计算基准为'1'的物料，用于计算计划锅数
            Integer calculatedPlanPotCount = null;
            for (Map ppbom : orderPpbomList) {
                String ppbomMaterialNumber = (String) ppbom.get("material_number");
                Object potCalculationBasis = ppbom.get("pot_calculation_basis"); // 锅数计算基准
                
                // 优先从配方管理中获取锅数计算基准
                TSysRecipeInput recipeInputForBasis = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                if (recipeInputForBasis == null) {
                    recipeInputForBasis = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                }
                if (recipeInputForBasis == null) {
                    recipeInputForBasis = recipeMaterialMap.values().stream()
                            .filter(input -> input != null 
                                    && ppbomMaterialNumber.equals(input.getMaterialCode()))
                            .findFirst()
                            .orElse(null);
                }
                
                String effectivePotCalculationBasis = null;
                if (recipeInputForBasis != null && recipeInputForBasis.getPotCalculationBasis() != null) {
                    effectivePotCalculationBasis = recipeInputForBasis.getPotCalculationBasis();
                } else if (potCalculationBasis != null) {
                    effectivePotCalculationBasis = potCalculationBasis.toString();
                }
                
                // 如果当前物料是锅数计算基准='1'的物料，则计算计划锅数
                if (effectivePotCalculationBasis != null && "1".equals(effectivePotCalculationBasis)) {
                    Object mustQtyObj = ppbom.get("must_qty");
                    // 从配方管理中获取计划投入比例，如果没有则从用料清单获取
                    Object planInputRatioObj = null;
                    if (recipeInputForBasis != null && recipeInputForBasis.getPlanInputRatio() != null) {
                        planInputRatioObj = recipeInputForBasis.getPlanInputRatio();
                    } else {
                        planInputRatioObj = ppbom.get("plan_input_ratio");
                    }
                    BigDecimal mustQtyForCalculation = null;
                    
                    // 如果存在计划投入比例，则调整计划投入量用于计算锅数
                    if (mustQtyObj != null) {
                        BigDecimal originalMustQty = new BigDecimal(mustQtyObj.toString());
                        
                        if (planInputRatioObj != null) {
                            BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                            // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                            mustQtyForCalculation = originalMustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                        } else {
                            // 如果没有计划投入比例，则使用原始计划投入量
                            mustQtyForCalculation = originalMustQty;
                        }
                    }
                    
                    // 尝试从配方管理中获取每锅投入标准
                    TSysRecipeInput recipeInput = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                    }
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.values().stream()
                                .filter(input -> input != null 
                                        && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                .findFirst()
                                .orElse(null);
                    }
                    
                    Object standardInputObj = null;
                    if (recipeInput != null && recipeInput.getStandardInput() != null) {
                        standardInputObj = recipeInput.getStandardInput();
                    } else {
                        // 如果配方管理中没有，则使用用料清单中的标准
                        standardInputObj = ppbom.get("mid_ppbom_entry_material_standard");
                    }
                    
                    if (mustQtyForCalculation != null && standardInputObj != null) {
                        try {
                            BigDecimal standardInput = new BigDecimal(standardInputObj.toString());
                            if (standardInput.compareTo(BigDecimal.ZERO) > 0) {
                                // 使用向上取整的方式计算计划锅数
                                BigDecimal calculatedValue = mustQtyForCalculation.divide(standardInput, 2, BigDecimal.ROUND_UP);
                                calculatedPlanPotCount = calculatedValue.intValue();
                                log.debug("使用基准物料计算计划锅数: 基准物料={}, 计划投入量={}, 每锅标准={}, 计算结果={}", 
                                    ppbomMaterialNumber, mustQtyForCalculation, standardInput, calculatedPlanPotCount);
                                break; // 找到基准物料后就退出循环
                            }
                        } catch (Exception e) {
                            log.error("使用基准物料计算计划锅数失败: ", e);
                        }
                    }
                }
            }
            
            // 如果没有找到锅数计算基准为'1'的物料，尝试使用当前物料进行计算
            if (calculatedPlanPotCount == null) {
                for (Map ppbom : orderPpbomList) {
                    String ppbomMaterialNumber = (String) ppbom.get("material_number");
                    if (materialNumber != null && materialNumber.equals(ppbomMaterialNumber)) {
                        Object mustQtyObj = ppbom.get("must_qty");
                        // 从配方管理中获取计划投入比例，如果没有则从用料清单获取
                        TSysRecipeInput recipeInputForRatio = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                        if (recipeInputForRatio == null) {
                            recipeInputForRatio = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                        }
                        if (recipeInputForRatio == null) {
                            recipeInputForRatio = recipeMaterialMap.values().stream()
                                    .filter(input -> input != null 
                                            && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                    .findFirst()
                                    .orElse(null);
                        }
                        Object planInputRatioObj = null;
                        if (recipeInputForRatio != null && recipeInputForRatio.getPlanInputRatio() != null) {
                            planInputRatioObj = recipeInputForRatio.getPlanInputRatio();
                        } else {
                            planInputRatioObj = ppbom.get("plan_input_ratio");
                        }
                        BigDecimal mustQtyForCalculation = null;
                        
                        // 如果存在计划投入比例，则调整计划投入量用于计算锅数
                        if (mustQtyObj != null) {
                            BigDecimal originalMustQty = new BigDecimal(mustQtyObj.toString());
                            
                            if (planInputRatioObj != null) {
                                BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                                // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                                mustQtyForCalculation = originalMustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                            } else {
                                // 如果没有计划投入比例，则使用原始计划投入量
                                mustQtyForCalculation = originalMustQty;
                            }
                        }
                        
                        // 尝试从配方管理中获取每锅投入标准
                        TSysRecipeInput recipeInput = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                        if (recipeInput == null) {
                            recipeInput = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                        }
                        if (recipeInput == null) {
                            recipeInput = recipeMaterialMap.values().stream()
                                    .filter(input -> input != null 
                                            && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                    .findFirst()
                                    .orElse(null);
                        }
                        
                        Object standardInputObj = null;
                        if (recipeInput != null && recipeInput.getStandardInput() != null) {
                            standardInputObj = recipeInput.getStandardInput();
                        } else {
                            // 如果配方管理中没有，则使用用料清单中的标准
                            standardInputObj = ppbom.get("mid_ppbom_entry_material_standard");
                        }
                        
                        if (mustQtyForCalculation != null && standardInputObj != null) {
                            try {
                                BigDecimal standardInput = new BigDecimal(standardInputObj.toString());
                                if (standardInput.compareTo(BigDecimal.ZERO) > 0) {
                                    // 使用向上取整的方式计算计划锅数
                                    BigDecimal calculatedValue = mustQtyForCalculation.divide(standardInput, 2, BigDecimal.ROUND_UP);
                                    calculatedPlanPotCount = calculatedValue.intValue();
                                    log.debug("使用当前物料计算计划锅数: 物料={}, 计划投入量={}, 每锅标准={}, 计算结果={}", 
                                        ppbomMaterialNumber, mustQtyForCalculation, standardInput, calculatedPlanPotCount);
                                    break; // 使用当前物料计算后退出循环
                                }
                            } catch (Exception e) {
                                log.error("使用当前物料计算计划锅数失败: ", e);
                            }
                        } else {
                            log.debug("当前物料无法计算计划锅数: 物料={}, mustQtyForCalculation={}, standardInputObj={}", 
                                ppbomMaterialNumber, mustQtyForCalculation, standardInputObj);
                        }
                    }
                }
            }
            
            // 如果仍未计算出计划锅数，尝试使用第一个可用物料进行计算
            if (calculatedPlanPotCount == null) {
                for (Map ppbom : orderPpbomList) {
                    String ppbomMaterialNumber = (String) ppbom.get("material_number");
                    Object mustQtyObj = ppbom.get("must_qty");
                    // 从配方管理中获取计划投入比例，如果没有则从用料清单获取
                    TSysRecipeInput recipeInputForRatio = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                    if (recipeInputForRatio == null) {
                        recipeInputForRatio = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                    }
                    if (recipeInputForRatio == null) {
                        recipeInputForRatio = recipeMaterialMap.values().stream()
                                .filter(input -> input != null 
                                        && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                .findFirst()
                                .orElse(null);
                    }
                    Object planInputRatioObj = null;
                    if (recipeInputForRatio != null && recipeInputForRatio.getPlanInputRatio() != null) {
                        planInputRatioObj = recipeInputForRatio.getPlanInputRatio();
                    } else {
                        planInputRatioObj = ppbom.get("plan_input_ratio");
                    }
                    BigDecimal mustQtyForCalculation = null;
                    
                    // 如果存在计划投入比例，则调整计划投入量用于计算锅数
                    if (mustQtyObj != null) {
                        BigDecimal originalMustQty = new BigDecimal(mustQtyObj.toString());
                        
                        if (planInputRatioObj != null) {
                            BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                            // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                            mustQtyForCalculation = originalMustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                        } else {
                            // 如果没有计划投入比例，则使用原始计划投入量
                            mustQtyForCalculation = originalMustQty;
                        }
                    }
                    
                    // 尝试从配方管理中获取每锅投入标准
                    TSysRecipeInput recipeInput = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                    }
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.values().stream()
                                .filter(input -> input != null 
                                        && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                .findFirst()
                                .orElse(null);
                    }
                    
                    Object standardInputObj = null;
                    if (recipeInput != null && recipeInput.getStandardInput() != null) {
                        standardInputObj = recipeInput.getStandardInput();
                    } else {
                        // 如果配方管理中没有，则使用用料清单中的标准
                        standardInputObj = ppbom.get("mid_ppbom_entry_material_standard");
                    }
                    
                    if (mustQtyForCalculation != null && standardInputObj != null) {
                        try {
                            BigDecimal standardInput = new BigDecimal(standardInputObj.toString());
                            if (standardInput.compareTo(BigDecimal.ZERO) > 0) {
                                // 使用向上取整的方式计算计划锅数
                                BigDecimal calculatedValue = mustQtyForCalculation.divide(standardInput, 2, BigDecimal.ROUND_UP);
                                calculatedPlanPotCount = calculatedValue.intValue();
                                log.debug("使用任意物料计算计划锅数: 物料={}, 计划投入量={}, 每锅标准={}, 计算结果={}", 
                                    ppbomMaterialNumber, mustQtyForCalculation, standardInput, calculatedPlanPotCount);
                                break; // 使用任意物料计算后退出循环
                            }
                        } catch (Exception e) {
                            log.error("使用任意物料计算计划锅数失败: ", e);
                        }
                    }
                }
            }
            
            // 查找匹配的物料信息，设置计划投入量
            for (Map ppbom : orderPpbomList) {
                String ppbomMaterialNumber = (String) ppbom.get("material_number");
                if (materialNumber != null && materialNumber.equals(ppbomMaterialNumber)) {
                    // 设置计划投入量 (mustQty -> plannedInput)
                    Object mustQtyObj = ppbom.get("must_qty");
                    // 从配方管理中获取计划投入比例，如果没有则从用料清单获取
                    TSysRecipeInput recipeInput = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                    }
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.values().stream()
                                .filter(input -> input != null 
                                        && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                .findFirst()
                                .orElse(null);
                    }
                    Object planInputRatioObj = null;
                    if (recipeInput != null && recipeInput.getPlanInputRatio() != null) {
                        planInputRatioObj = recipeInput.getPlanInputRatio();
                    } else {
                        planInputRatioObj = ppbom.get("plan_input_ratio");
                    }
                    
                    if (mustQtyObj != null) {
                        BigDecimal mustQty = new BigDecimal(mustQtyObj.toString());
                        
                        // 如果存在计划投入比例，则调整计划投入量
                        if (planInputRatioObj != null) {
                            BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                            // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                            BigDecimal adjustedPlannedInput = mustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                            info.setPlannedInput(adjustedPlannedInput);
                            log.debug("使用计划投入比例计算计划投入量: 原始值={}, 比例={}, 调整后值={}", mustQty, planInputRatio, adjustedPlannedInput);
                        } else {
                            // 如果没有计划投入比例，则使用原始计划投入量
                            info.setPlannedInput(mustQty);
                            log.debug("未找到计划投入比例，使用原始计划投入量: {}", mustQty);
                        }
                    } else {
                        log.debug("未找到mustQty值，设置计划投入量为0");
                        info.setPlannedInput(BigDecimal.ZERO);
                    }
                    break;
                }
            }
            
            // 设置计划锅数：优先使用计算出的值，如果没有则使用订单头的值
            if (calculatedPlanPotCount != null) {
                info.setPlannedPotCount(calculatedPlanPotCount);
                log.debug("设置计划锅数: {}", calculatedPlanPotCount);
            } else {
                // 如果没有找到锅数计算基准为'1'的物料，使用订单头中的计划锅数
                if (orderHeadList != null && !orderHeadList.isEmpty()) {
                    TBusOrderHead orderHead = orderHeadList.get(0);
                    if (orderHead.getBodyPotQty() != null) {
                        info.setPlannedPotCount(orderHead.getBodyPotQty());
                        log.debug("使用订单头计划锅数: {}", orderHead.getBodyPotQty());
                    } else {
                        info.setPlannedPotCount(0); // 默认值
                        log.debug("未找到任何计划锅数，设置为默认值0");
                    }
                } else {
                    info.setPlannedPotCount(0); // 默认值
                    log.debug("未找到订单头信息，设置计划锅数为默认值0");
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
            
            // 获取订单产品编码和工序信息，用于配方管理逻辑
            String orderProductCode = orderHeadList.get(0).getBodyMaterialNumber();
            Optional<TBusOrderProcess> orderProcessOpt = orderProcessRepository.findById(orderProcessId);
            String currentProcessNumber = null;
            if (orderProcessOpt.isPresent()) {
                currentProcessNumber = orderProcessOpt.get().getProcessId().getProcessNumber();
            }
            
            // 获取配方管理信息
            Map<String, TSysRecipeInput> recipeMaterialMap = new HashMap<>();
            if (orderProductCode != null && currentProcessNumber != null) {
                List<TSysRecipeProductBinding> productBindings = recipeProductBindingRepository.findByProductCodeAndRecipeStatusEnabled(orderProductCode);
                for (TSysRecipeProductBinding binding : productBindings) {
                    List<TSysRecipeInput> recipeInputs = recipeInputRepository.findByRecipeId(binding.getRecipeId());
                    for (TSysRecipeInput input : recipeInputs) {
                        if (currentProcessNumber.equals(input.getProcessNumber())) {
                            recipeMaterialMap.put(input.getSemiFinishedProductCode()+"_"+input.getMaterialCode(), input);
                        }
                    }
                }
            }
            
            // 首先查找锅数计算基准为'1'的物料，用于计算计划锅数
            Integer calculatedPlanPotCount = null;
            for (Map ppbom : orderPpbomList) {
                String ppbomMaterialNumber = (String) ppbom.get("material_number");
                Object potCalculationBasis = ppbom.get("pot_calculation_basis"); // 锅数计算基准
                
                // 优先从配方管理中获取锅数计算基准
                TSysRecipeInput recipeInputForBasis = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                if (recipeInputForBasis == null) {
                    recipeInputForBasis = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                }
                if (recipeInputForBasis == null) {
                    recipeInputForBasis = recipeMaterialMap.values().stream()
                            .filter(input -> input != null 
                                    && ppbomMaterialNumber.equals(input.getMaterialCode()))
                            .findFirst()
                            .orElse(null);
                }
                
                String effectivePotCalculationBasis = null;
                if (recipeInputForBasis != null && recipeInputForBasis.getPotCalculationBasis() != null) {
                    effectivePotCalculationBasis = recipeInputForBasis.getPotCalculationBasis();
                } else if (potCalculationBasis != null) {
                    effectivePotCalculationBasis = potCalculationBasis.toString();
                }
                
                // 如果当前物料是锅数计算基准='1'的物料，则计算计划锅数
                if (effectivePotCalculationBasis != null && "1".equals(effectivePotCalculationBasis)) {
                    Object mustQtyObj = ppbom.get("must_qty");
                    // 从配方管理中获取计划投入比例，如果没有则从用料清单获取
                    Object planInputRatioObj = null;
                    if (recipeInputForBasis != null && recipeInputForBasis.getPlanInputRatio() != null) {
                        planInputRatioObj = recipeInputForBasis.getPlanInputRatio();
                    } else {
                        planInputRatioObj = ppbom.get("plan_input_ratio");
                    }
                    BigDecimal mustQtyForCalculation = null;
                    
                    // 如果存在计划投入比例，则调整计划投入量用于计算锅数
                    if (mustQtyObj != null) {
                        BigDecimal originalMustQty = new BigDecimal(mustQtyObj.toString());
                        
                        if (planInputRatioObj != null) {
                            BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                            // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                            mustQtyForCalculation = originalMustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                        } else {
                            // 如果没有计划投入比例，则使用原始计划投入量
                            mustQtyForCalculation = originalMustQty;
                        }
                    }
                    
                    // 尝试从配方管理中获取每锅投入标准
                    TSysRecipeInput recipeInput = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                    }
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.values().stream()
                                .filter(input -> input != null 
                                        && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                .findFirst()
                                .orElse(null);
                    }
                    
                    Object standardInputObj = null;
                    if (recipeInput != null && recipeInput.getStandardInput() != null) {
                        standardInputObj = recipeInput.getStandardInput();
                    } else {
                        // 如果配方管理中没有，则使用用料清单中的标准
                        standardInputObj = ppbom.get("mid_ppbom_entry_material_standard");
                    }
                    
                    if (mustQtyForCalculation != null && standardInputObj != null) {
                        try {
                            BigDecimal standardInput = new BigDecimal(standardInputObj.toString());
                            if (standardInput.compareTo(BigDecimal.ZERO) > 0) {
                                // 使用向上取整的方式计算计划锅数
                                BigDecimal calculatedValue = mustQtyForCalculation.divide(standardInput, 2, BigDecimal.ROUND_UP);
                                calculatedPlanPotCount = calculatedValue.intValue();
                                log.debug("使用基准物料计算计划锅数: 基准物料={}, 计划投入量={}, 每锅标准={}, 计算结果={}", 
                                    ppbomMaterialNumber, mustQtyForCalculation, standardInput, calculatedPlanPotCount);
                                break; // 找到基准物料后就退出循环
                            }
                        } catch (Exception e) {
                            log.error("使用基准物料计算计划锅数失败: ", e);
                        }
                    }
                }
            }
            
            // 如果没有找到锅数计算基准为'1'的物料，尝试使用当前物料进行计算
            if (calculatedPlanPotCount == null) {
                for (Map ppbom : orderPpbomList) {
                    String ppbomMaterialNumber = (String) ppbom.get("material_number");
                    if (materialNumber != null && materialNumber.equals(ppbomMaterialNumber)) {
                        Object mustQtyObj = ppbom.get("must_qty");
                        // 从配方管理中获取计划投入比例，如果没有则从用料清单获取
                        TSysRecipeInput recipeInputForRatio = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                        if (recipeInputForRatio == null) {
                            recipeInputForRatio = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                        }
                        if (recipeInputForRatio == null) {
                            recipeInputForRatio = recipeMaterialMap.values().stream()
                                    .filter(input -> input != null 
                                            && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                    .findFirst()
                                    .orElse(null);
                        }
                        Object planInputRatioObj = null;
                        if (recipeInputForRatio != null && recipeInputForRatio.getPlanInputRatio() != null) {
                            planInputRatioObj = recipeInputForRatio.getPlanInputRatio();
                        } else {
                            planInputRatioObj = ppbom.get("plan_input_ratio");
                        }
                        BigDecimal mustQtyForCalculation = null;
                        
                        // 如果存在计划投入比例，则调整计划投入量用于计算锅数
                        if (mustQtyObj != null) {
                            BigDecimal originalMustQty = new BigDecimal(mustQtyObj.toString());
                            
                            if (planInputRatioObj != null) {
                                BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                                // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                                mustQtyForCalculation = originalMustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                            } else {
                                // 如果没有计划投入比例，则使用原始计划投入量
                                mustQtyForCalculation = originalMustQty;
                            }
                        }
                        
                        // 尝试从配方管理中获取每锅投入标准
                        TSysRecipeInput recipeInput = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                        if (recipeInput == null) {
                            recipeInput = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                        }
                        if (recipeInput == null) {
                            recipeInput = recipeMaterialMap.values().stream()
                                    .filter(input -> input != null 
                                            && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                    .findFirst()
                                    .orElse(null);
                        }
                        
                        Object standardInputObj = null;
                        if (recipeInput != null && recipeInput.getStandardInput() != null) {
                            standardInputObj = recipeInput.getStandardInput();
                        } else {
                            // 如果配方管理中没有，则使用用料清单中的标准
                            standardInputObj = ppbom.get("mid_ppbom_entry_material_standard");
                        }
                        
                        if (mustQtyForCalculation != null && standardInputObj != null) {
                            try {
                                BigDecimal standardInput = new BigDecimal(standardInputObj.toString());
                                if (standardInput.compareTo(BigDecimal.ZERO) > 0) {
                                    // 使用向上取整的方式计算计划锅数
                                    BigDecimal calculatedValue = mustQtyForCalculation.divide(standardInput, 2, BigDecimal.ROUND_UP);
                                    calculatedPlanPotCount = calculatedValue.intValue();
                                    log.debug("使用当前物料计算计划锅数: 物料={}, 计划投入量={}, 每锅标准={}, 计算结果={}", 
                                        ppbomMaterialNumber, mustQtyForCalculation, standardInput, calculatedPlanPotCount);
                                    break; // 使用当前物料计算后退出循环
                                }
                            } catch (Exception e) {
                                log.error("使用当前物料计算计划锅数失败: ", e);
                            }
                        } else {
                            log.debug("当前物料无法计算计划锅数: 物料={}, mustQtyForCalculation={}, standardInputObj={}", 
                                ppbomMaterialNumber, mustQtyForCalculation, standardInputObj);
                        }
                    }
                }
            }
            
            // 如果仍未计算出计划锅数，尝试使用第一个可用物料进行计算
            if (calculatedPlanPotCount == null) {
                for (Map ppbom : orderPpbomList) {
                    String ppbomMaterialNumber = (String) ppbom.get("material_number");
                    Object mustQtyObj = ppbom.get("must_qty");
                    // 从配方管理中获取计划投入比例，如果没有则从用料清单获取
                    TSysRecipeInput recipeInputForRatio = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                    if (recipeInputForRatio == null) {
                        recipeInputForRatio = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                    }
                    if (recipeInputForRatio == null) {
                        recipeInputForRatio = recipeMaterialMap.values().stream()
                                .filter(input -> input != null 
                                        && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                .findFirst()
                                .orElse(null);
                    }
                    Object planInputRatioObj = null;
                    if (recipeInputForRatio != null && recipeInputForRatio.getPlanInputRatio() != null) {
                        planInputRatioObj = recipeInputForRatio.getPlanInputRatio();
                    } else {
                        planInputRatioObj = ppbom.get("plan_input_ratio");
                    }
                    BigDecimal mustQtyForCalculation = null;
                    
                    // 如果存在计划投入比例，则调整计划投入量用于计算锅数
                    if (mustQtyObj != null) {
                        BigDecimal originalMustQty = new BigDecimal(mustQtyObj.toString());
                        
                        if (planInputRatioObj != null) {
                            BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                            // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                            mustQtyForCalculation = originalMustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                        } else {
                            // 如果没有计划投入比例，则使用原始计划投入量
                            mustQtyForCalculation = originalMustQty;
                        }
                    }
                    
                    // 尝试从配方管理中获取每锅投入标准
                    TSysRecipeInput recipeInput = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                    }
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.values().stream()
                                .filter(input -> input != null 
                                        && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                .findFirst()
                                .orElse(null);
                    }
                    
                    Object standardInputObj = null;
                    if (recipeInput != null && recipeInput.getStandardInput() != null) {
                        standardInputObj = recipeInput.getStandardInput();
                    } else {
                        // 如果配方管理中没有，则使用用料清单中的标准
                        standardInputObj = ppbom.get("mid_ppbom_entry_material_standard");
                    }
                    
                    if (mustQtyForCalculation != null && standardInputObj != null) {
                        try {
                            BigDecimal standardInput = new BigDecimal(standardInputObj.toString());
                            if (standardInput.compareTo(BigDecimal.ZERO) > 0) {
                                // 使用向上取整的方式计算计划锅数
                                BigDecimal calculatedValue = mustQtyForCalculation.divide(standardInput, 2, BigDecimal.ROUND_UP);
                                calculatedPlanPotCount = calculatedValue.intValue();
                                log.debug("使用任意物料计算计划锅数: 物料={}, 计划投入量={}, 每锅标准={}, 计算结果={}", 
                                    ppbomMaterialNumber, mustQtyForCalculation, standardInput, calculatedPlanPotCount);
                                break; // 使用任意物料计算后退出循环
                            }
                        } catch (Exception e) {
                            log.error("使用任意物料计算计划锅数失败: ", e);
                        }
                    }
                }
            }
            
            // 查找匹配的物料信息，设置计划投入量
            for (Map ppbom : orderPpbomList) {
                String ppbomMaterialNumber = (String) ppbom.get("material_number");
                if (materialNumber != null && materialNumber.equals(ppbomMaterialNumber)) {
                    // 设置计划投入量 (mustQty -> plannedInput)
                    Object mustQtyObj = ppbom.get("must_qty");
                    // 从配方管理中获取计划投入比例，如果没有则从用料清单获取
                    TSysRecipeInput recipeInput = recipeMaterialMap.get("null_" + ppbomMaterialNumber);
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.get("_" + ppbomMaterialNumber);
                    }
                    if (recipeInput == null) {
                        recipeInput = recipeMaterialMap.values().stream()
                                .filter(input -> input != null 
                                        && ppbomMaterialNumber.equals(input.getMaterialCode()))
                                .findFirst()
                                .orElse(null);
                    }
                    Object planInputRatioObj = null;
                    if (recipeInput != null && recipeInput.getPlanInputRatio() != null) {
                        planInputRatioObj = recipeInput.getPlanInputRatio();
                    } else {
                        planInputRatioObj = ppbom.get("plan_input_ratio");
                    }
                    
                    if (mustQtyObj != null) {
                        BigDecimal mustQty = new BigDecimal(mustQtyObj.toString());
                        
                        // 如果存在计划投入比例，则调整计划投入量
                        if (planInputRatioObj != null) {
                            BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                            // 计算调整后的计划投入量 = 原始计划投入量 * 计划投入比例% / 100
                            BigDecimal adjustedPlannedInput = mustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                            materialInfo.setPlannedInput(adjustedPlannedInput);
                            log.debug("使用计划投入比例计算计划投入量: 原始值={}, 比例={}, 调整后值={}", mustQty, planInputRatio, adjustedPlannedInput);
                        } else {
                            // 如果没有计划投入比例，则使用原始计划投入量
                            materialInfo.setPlannedInput(mustQty);
                            log.debug("未找到计划投入比例，使用原始计划投入量: {}", mustQty);
                        }
                    } else {
                        log.debug("未找到mustQty值，设置计划投入量为0");
                        materialInfo.setPlannedInput(BigDecimal.ZERO);
                    }
                    break;
                }
            }
            
            // 设置计划锅数：优先使用计算出的值，如果没有则使用订单头的值
            if (calculatedPlanPotCount != null) {
                materialInfo.setPlannedPotCount(calculatedPlanPotCount);
                log.debug("设置计划锅数: {}", calculatedPlanPotCount);
            } else {
                // 如果没有找到锅数计算基准为'1'的物料，使用订单头中的计划锅数
                if (orderHeadList != null && !orderHeadList.isEmpty()) {
                    TBusOrderHead orderHead = orderHeadList.get(0);
                    if (orderHead.getBodyPotQty() != null) {
                        materialInfo.setPlannedPotCount(orderHead.getBodyPotQty());
                        log.debug("使用订单头计划锅数: {}", orderHead.getBodyPotQty());
                    } else {
                        materialInfo.setPlannedPotCount(0); // 默认值
                        log.debug("未找到任何计划锅数，设置为默认值0");
                    }
                } else {
                    materialInfo.setPlannedPotCount(0); // 默认值
                    log.debug("未找到订单头信息，设置计划锅数为默认值0");
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