package org.thingsboard.server.service.report.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPotCount;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;
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
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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

    @Autowired
    private org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessInfoRepository processInfoRepository;

    @Autowired
    private UserService userService;
    
    /**
     * 获取当前登录用户ID
     */
    private void adjustDates(RawMaterialInputQueryDto queryDto) {
        if (queryDto == null) return;
        if (queryDto.getOrderDateStart() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(queryDto.getOrderDateStart());
            cal.add(Calendar.HOUR_OF_DAY, 8);
            queryDto.setOrderDateStart(cal.getTime());
        }
        if (queryDto.getOrderDateEnd() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(queryDto.getOrderDateEnd());
            cal.add(Calendar.HOUR_OF_DAY, 8);
            queryDto.setOrderDateEnd(cal.getTime());
        }
    }

    /**
     * 为时间添加8小时以修正时区显示问题
     */
    private Date add8HoursToTime(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, 8);
        return cal.getTime();
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            return securityUser.getId().getId().toString();
        } else {
            throw new RuntimeException("无法获取当前登录用户信息");
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageVo<RawMaterialInputReportVo> queryRawMaterialInputReport(Integer current, Integer size, RawMaterialInputQueryDto queryDto) {
        adjustDates(queryDto);
        
        // 获取当前登录用户ID
        String currentUserId = getCurrentUserId();
        // 获取用户绑定的产线ID列表
        List<String> userCwkids = userService.getUserCurrentCwkid(currentUserId);
        
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
            
            // 订单号（模糊查询）
            if (!StringUtils.isEmpty(queryDto.getOrderNo())) {
                predicates.add(criteriaBuilder.like(root.get("orderNo"), "%" + queryDto.getOrderNo() + "%"));
            }
            
            // 下单日期-开始时间
            if (queryDto.getOrderDateStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("billDate"), queryDto.getOrderDateStart()));
            }
            
            // 下单日期-结束时间
            if (queryDto.getOrderDateEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("billDate"), queryDto.getOrderDateEnd()));
            }
            
            // 根据用户绑定的产线ID过滤
            if (userCwkids != null && !userCwkids.isEmpty()) {
                predicates.add(root.get("cwkid").in(userCwkids));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        
        // 转换为返回结果
        PageVo<RawMaterialInputReportVo> result = new PageVo<>();
        result.setTotal((int) orderHeadPage.getTotalElements());
        
        if (orderHeadPage.hasContent()) {
            List<TBusOrderHead> orderHeads = orderHeadPage.getContent();
            // 预取工序映射以加速查询
            Map<String, TSysProcessInfo> allProcessMap = processInfoRepository.findAll().stream()
                    .collect(Collectors.toMap(TSysProcessInfo::getProcessNumber, p -> p, (a, b) -> a));
            
            // 预取所有需要的数据，解决N+1问题
            Map<String, Object> prefetchContext = prefetchData(orderHeads, allProcessMap);
            
            // 使用并行流显著提升数据组装速度
            List<RawMaterialInputReportVo> list = orderHeads.parallelStream().map(order -> {
                RawMaterialInputReportVo vo = new RawMaterialInputReportVo();
                vo.setOrderNo(order.getOrderNo());
                // 修正时区显示问题
                Date adjustedOrderTime = add8HoursToTime(order.getBillDate());
                vo.setOrderTime(adjustedOrderTime);
                vo.setProductName(order.getBodyMaterialName());
                vo.setProductionLine(order.getVwkname());
                calculateOutputDataWithContext(vo, order.getOrderNo(), prefetchContext);
                vo.setProcessGroupInfoList(getProcessGroupInfoListWithContext(order.getOrderNo(), prefetchContext));
                return vo;
            }).collect(Collectors.toList());
            result.setList(list);
        } else {
            result.setList(new ArrayList<>());
        }
        return result;
    }

    private Map<String, Object> prefetchData(List<TBusOrderHead> orderHeads, Map<String, TSysProcessInfo> allProcessMap) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> context = new ConcurrentHashMap<>();
        
        List<String> orderNos = orderHeads.stream().map(TBusOrderHead::getOrderNo).collect(Collectors.toList());
        List<Integer> orderIds = orderHeads.stream().map(TBusOrderHead::getOrderId).collect(Collectors.toList());
        List<String> productCodes = orderHeads.stream().map(TBusOrderHead::getBodyMaterialNumber)
                .filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        
        context.put("orderHeads", orderHeads.stream().collect(Collectors.groupingBy(TBusOrderHead::getOrderNo)));
        
        // 将数据库查询改为顺序执行，以避免异步线程导致的 Transaction/Session 丢失问题
        
        // 1. 查询实际产量记录
        List<TBusOrderProcessHistory> actualOutputRecords = orderProcessHistoryRepository.findAllByOrderNoInAndProcessName(orderNos, "外包装");
        context.put("actualOutputRecords", actualOutputRecords.stream().collect(Collectors.groupingBy(TBusOrderProcessHistory::getOrderNo)));

        // 1.1 查询净含量记录 (process_number = 'GX-011')
        List<TBusOrderProcessHistory> netContentRecords = orderProcessHistoryRepository.findAllByOrderNoInAndProcessNumber(orderNos, "GX-011");
        context.put("netContentRecords", netContentRecords.stream().collect(Collectors.groupingBy(TBusOrderProcessHistory::getOrderNo)));

        // 2. 查询订单工序和锅数记录
        List<TBusOrderProcess> orderProcesses = orderProcessRepository.findAllByOrderNoIn(orderNos);
        context.put("orderProcesses", orderProcesses.stream().collect(Collectors.groupingBy(TBusOrderProcess::getOrderNo)));
        context.put("orderProcessesById", orderProcesses.stream().collect(Collectors.toMap(TBusOrderProcess::getOrderProcessId, p -> p, (a, b) -> a)));
        
        List<Integer> orderProcessIds = orderProcesses.stream().map(TBusOrderProcess::getOrderProcessId).collect(Collectors.toList());
        if (!orderProcessIds.isEmpty()) {
            List<TBusOrderPotCount> potCounts = orderPotCountRepository.findAllByOrderProcessIdIn(orderProcessIds);
            context.put("potCounts", potCounts.stream().collect(Collectors.groupingBy(TBusOrderPotCount::getOrderProcessId)));
        }

        // 3. 查询报工历史记录
        List<TBusOrderProcessHistory> historyRecords = orderProcessHistoryRepository.findAllByOrderNoInAndBusTypeAndReportStatusNot(
            orderNos, 
            org.thingsboard.server.common.data.mes.LichengConstants.ORDER_BUS_TYPE_BG,
            org.thingsboard.server.common.data.mes.LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1
        );
        context.put("historyRecords", historyRecords.stream().collect(Collectors.groupingBy(TBusOrderProcessHistory::getOrderNo)));

        // 4. 查询用料清单并构建物料编码索引
        List<Map> allPpboms = orderHeadRepository.findAllOrderPPbomByOrderIds(orderIds);
        Map<String, List<Map>> orderPpbomMap = allPpboms.stream().collect(Collectors.groupingBy(m -> String.valueOf(m.get("order_id"))));
        context.put("orderPpboms", orderPpbomMap);
        
        Map<String, Map<String, Map>> orderPpbomsByMaterial = new HashMap<>();
        orderPpbomMap.forEach((orderId, list) -> {
            Map<String, Map> materialMap = list.stream()
                    .filter(m -> m.get("material_number") != null)
                    .collect(Collectors.toMap(m -> String.valueOf(m.get("material_number")), m -> m, (a, b) -> a));
            orderPpbomsByMaterial.put(orderId, materialMap);
        });
        context.put("orderPpbomsByMaterial", orderPpbomsByMaterial);

        // 5. 查询配方绑定并构建高效嵌套索引
        if (!productCodes.isEmpty()) {
            List<TSysRecipeProductBinding> bindings = recipeProductBindingRepository.findAllByProductCodeInAndRecipeStatusEnabled(productCodes);
            List<Integer> recipeIds = bindings.stream().map(TSysRecipeProductBinding::getRecipeId).distinct().collect(Collectors.toList());
            if (!recipeIds.isEmpty()) {
                List<TSysRecipeInput> recipeInputs = recipeInputRepository.findAllByRecipeIdIn(recipeIds);
                
                Map<String, Map<String, Map<String, TSysRecipeInput>>> productRecipeProcessMap = new HashMap<>();
                Map<Integer, List<TSysRecipeInput>> recipeInputMap = recipeInputs.stream().collect(Collectors.groupingBy(TSysRecipeInput::getRecipeId));
                
                bindings.forEach(binding -> {
                    String productCode = binding.getProductCode();
                    List<TSysRecipeInput> inputs = recipeInputMap.getOrDefault(binding.getRecipeId(), Collections.emptyList());
                    Map<String, Map<String, TSysRecipeInput>> processMap = productRecipeProcessMap.computeIfAbsent(productCode, k -> new HashMap<>());
                    for (TSysRecipeInput input : inputs) {
                        String processNumber = input.getProcessNumber();
                        if (processNumber != null) {
                            Map<String, TSysRecipeInput> materialMap = processMap.computeIfAbsent(processNumber, k -> new HashMap<>());
                            materialMap.put(input.getSemiFinishedProductCode() + "_" + input.getMaterialCode(), input);
                        }
                    }
                });
                context.put("productRecipeProcessMap", productRecipeProcessMap);
            }
        }

        // 6. 使用传入的所有工序信息
        context.put("allProcessMap", allProcessMap);
        
        log.info("预取 {} 条订单数据耗时 {} ms", orderHeads.size(), System.currentTimeMillis() - startTime);
        return context;
    }

    private void calculateOutputDataWithContext(RawMaterialInputReportVo vo, String orderNo, Map<String, Object> context) {
        // 计划产量从预取的订单头获取
        Map<String, List<TBusOrderHead>> orderHeadMap = (Map<String, List<TBusOrderHead>>) context.get("orderHeads");
        List<TBusOrderHead> heads = orderHeadMap.get(orderNo);
        TBusOrderHead orderHead = (heads != null && !heads.isEmpty()) ? heads.get(0) : null;
        BigDecimal plannedOutput = (orderHead != null && orderHead.getBodyPlanPrdQty() != null) ? 
            orderHead.getBodyPlanPrdQty() : BigDecimal.ZERO;
        
        // 实际产量从预取的历史记录获取
        Map<String, List<TBusOrderProcessHistory>> actualOutputMap = (Map<String, List<TBusOrderProcessHistory>>) context.get("actualOutputRecords");
        List<TBusOrderProcessHistory> records = actualOutputMap.get(orderNo);
        BigDecimal actualOutput = new BigDecimal(records != null ? records.size() : 0);
        
        // 实际产量需要除以特定值
        BigDecimal divisionValue = getDivisionValue(orderHead);
        if (divisionValue.compareTo(BigDecimal.ZERO) > 0) {
            actualOutput = actualOutput.divide(divisionValue, 2, BigDecimal.ROUND_HALF_UP);
        }
        
        // 净含量从预取的历史记录获取 (process_number = 'GX-011')
        Map<String, List<TBusOrderProcessHistory>> netContentMap = (Map<String, List<TBusOrderProcessHistory>>) context.get("netContentRecords");
        List<TBusOrderProcessHistory> netContentRecords = netContentMap.get(orderNo);
        BigDecimal netContent = BigDecimal.ZERO;
        if (netContentRecords != null) {
            for (TBusOrderProcessHistory r : netContentRecords) {
                if (r.getRecordQty() != null) {
                    netContent = netContent.add(r.getRecordQty());
                }
            }
        }
        
        vo.setPlannedOutput(plannedOutput);
        vo.setActualOutput(actualOutput);
        vo.setNetContent(netContent);
    }
    
    /**
     * 获取实际产量的除数
     * 优先从qty_per_jian字段获取，如果没有则从body_material_specification中解析*
     */
    private BigDecimal getDivisionValue(TBusOrderHead orderHead) {
        if (orderHead == null) {
            return BigDecimal.ONE;
        }
        
        // 1. 优先使用qty_per_jian
        if (orderHead.getQtyPerJian() != null && orderHead.getQtyPerJian().compareTo(BigDecimal.ZERO) > 0) {
            return orderHead.getQtyPerJian();
        }
        
        // 2. 尝试从body_material_specification中解析
        String spec = orderHead.getBodyMaterialSpecification();
        if (spec != null && spec.contains("*") && spec.contains("/")) {
            try {
                int startIndex = spec.indexOf("*") + 1;
                int endIndex = spec.indexOf("/", startIndex);
                if (endIndex > startIndex) {
                    String numberStr = spec.substring(startIndex, endIndex).replaceAll("[^0-9.]", "");
                    if (!numberStr.isEmpty()) {
                        return new BigDecimal(numberStr);
                    }
                }
            } catch (Exception e) {
                log.warn("解析body_material_specification失败: {}", spec, e);
            }
        }
        
        // 3. 默认返回1
        return BigDecimal.ONE;
    }

    private BigDecimal calculatePlannedInput(Map<String, TSysRecipeInput> recipeMaterialMap, Map materialPpbom, String materialNumber, String groupCode) {
        if (materialPpbom == null) return BigDecimal.ZERO;
        
        Object mustQtyObj = materialPpbom.get("must_qty");
        if (mustQtyObj == null) return BigDecimal.ZERO;
        BigDecimal mustQty = new BigDecimal(mustQtyObj.toString());
        
        if (recipeMaterialMap == null || recipeMaterialMap.isEmpty()) {
            return mustQty;
        }

        TSysRecipeInput recipeInput = null;
        if (StringUtils.isNotEmpty(groupCode)) {
            recipeInput = recipeMaterialMap.get(groupCode + "_" + materialNumber);
        }
        if (recipeInput == null) {
            recipeInput = recipeMaterialMap.get("null_" + materialNumber);
        }
        if (recipeInput == null) {
            recipeInput = recipeMaterialMap.get("_" + materialNumber);
        }
        if (recipeInput == null) {
            recipeInput = recipeMaterialMap.values().stream()
                    .filter(in -> in != null && StringUtils.equals(in.getMaterialCode(), materialNumber))
                    .findFirst().orElse(null);
        }

        BigDecimal planInputRatio = (recipeInput != null && recipeInput.getPlanInputRatio() != null) 
                ? recipeInput.getPlanInputRatio() 
                : new BigDecimal("100.00");
        
        return mustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
    }

    private Integer calculatePlannedPotCount(Map<String, TSysRecipeInput> recipeMaterialMap, List<Map> orderPpbomList, String groupCode, TBusOrderHead orderHead) {
        if (recipeMaterialMap == null || recipeMaterialMap.isEmpty() || orderPpbomList == null || orderPpbomList.isEmpty()) {
            return orderHead != null && orderHead.getBodyPotQty() != null ? orderHead.getBodyPotQty() : 0;
        }

        // 查找锅数计算基准='1'的物料
        for (Map ppbom : orderPpbomList) {
            String materialNumber = (String) ppbom.get("material_number");
            if (materialNumber == null) continue;

            TSysRecipeInput recipeInput = null;
            // 1. 优先使用分组编码匹配
            if (StringUtils.isNotEmpty(groupCode)) {
                recipeInput = recipeMaterialMap.get(groupCode + "_" + materialNumber);
            }
            
            // 2. 如果没找到或基准不匹配，尝试各种 fallback 匹配方式
            if (recipeInput == null || !"1".equals(recipeInput.getPotCalculationBasis())) {
                // 尝试 null_ 前缀
                recipeInput = recipeMaterialMap.get("null_" + materialNumber);
                
                // 尝试 ""_ 前缀
                if (recipeInput == null || !"1".equals(recipeInput.getPotCalculationBasis())) {
                    recipeInput = recipeMaterialMap.get("_" + materialNumber);
                }
                
                // 3. 遍历查找匹配的物料编码（同时匹配锅数计算基准='1'，且必须匹配当前分组编码）
                if (recipeInput == null || !"1".equals(recipeInput.getPotCalculationBasis())) {
                    final String targetGroupCode = groupCode;
                    recipeInput = recipeMaterialMap.values().stream()
                            .filter(input -> input != null 
                                    && materialNumber.equals(input.getMaterialCode())
                                    && "1".equals(input.getPotCalculationBasis())
                                    // 关键修正：确保找到的配方物料属于当前分组
                                    && (Objects.equals(targetGroupCode, input.getSemiFinishedProductCode()) 
                                        || (StringUtils.isEmpty(targetGroupCode) && StringUtils.isEmpty(input.getSemiFinishedProductCode()))))
                            .findFirst()
                            .orElse(null);
                }
            }

            // 4. 检查是否满足锅数计算基准='1'
            if (recipeInput != null && "1".equals(recipeInput.getPotCalculationBasis())) {
                Object mustQtyObj = ppbom.get("must_qty");
                BigDecimal standardInput = recipeInput.getStandardInput();
                
                if (mustQtyObj != null && standardInput != null && standardInput.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal mustQty = new BigDecimal(mustQtyObj.toString());
                    BigDecimal planInputRatio = recipeInput.getPlanInputRatio();
                    if (planInputRatio == null) planInputRatio = new BigDecimal("100.00");
                    
                    // 计算调整后的计划投入量 = 用料清单的需求量 * 计划投入比例% / 100
                    BigDecimal adjustedMustQty = mustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                    
                    if (adjustedMustQty.compareTo(BigDecimal.ZERO) > 0) {
                        // 计算计划锅数 = 计划投入 / 每锅投入标准，向上取整
                        BigDecimal planPotCount = adjustedMustQty.divide(standardInput, 2, BigDecimal.ROUND_HALF_UP);
                        return planPotCount.setScale(0, BigDecimal.ROUND_UP).intValue();
                    }
                }
            }
        }
        
        // 如果没找到计算基准物料，默认返回订单头的计划锅数
        return orderHead != null && orderHead.getBodyPotQty() != null ? orderHead.getBodyPotQty() : 0;
    }

    private List<ProcessGroupInfoVo> getProcessGroupInfoListWithContext(String orderNo, Map<String, Object> context) {
        // 1. 获取基础数据
        Map<String, List<TBusOrderHead>> orderHeadMap = (Map<String, List<TBusOrderHead>>) context.get("orderHeads");
        TBusOrderHead orderHead = (orderHeadMap.get(orderNo) != null && !orderHeadMap.get(orderNo).isEmpty()) ? orderHeadMap.get(orderNo).get(0) : null;
        if (orderHead == null) return new ArrayList<>();

        Integer orderId = orderHead.getOrderId();
        String orderProductCode = orderHead.getBodyMaterialNumber();
        
        // 2. 获取报工历史记录并按工序执行ID和分组编码(半成品)分组
        Map<String, List<TBusOrderProcessHistory>> historyRecordMap = (Map<String, List<TBusOrderProcessHistory>>) context.get("historyRecords");
        List<TBusOrderProcessHistory> historyRecords = historyRecordMap.getOrDefault(orderNo, new ArrayList<>());
        
        // 按 orderProcessId + groupCode 分组报工记录，支持同个工序下不同半成品的拆分
        Map<String, List<TBusOrderProcessHistory>> reportingProcessMap = historyRecords.stream()
            .filter(r -> r.getOrderProcessId() != null)
            .collect(Collectors.groupingBy(r -> r.getOrderProcessId() + "_" + (r.getGroupCode() != null ? r.getGroupCode() : ""), LinkedHashMap::new, Collectors.toList()));

        // 3. 获取配方默认工序信息
        Map<String, Map<String, Map<String, TSysRecipeInput>>> productRecipeProcessMap = (Map<String, Map<String, Map<String, TSysRecipeInput>>>) context.get("productRecipeProcessMap");
        Map<String, Map<String, TSysRecipeInput>> recipeProcessMap = productRecipeProcessMap != null ? productRecipeProcessMap.getOrDefault(orderProductCode, Collections.emptyMap()) : Collections.emptyMap();
        
        // 4. 获取工序定义信息
        Map<String, TSysProcessInfo> allProcessMap = (Map<String, TSysProcessInfo>) context.get("allProcessMap");

        // 5. 其它辅助数据
        Map<String, Map<String, Map>> orderPpbomsByMaterial = (Map<String, Map<String, Map>>) context.get("orderPpbomsByMaterial");
        Map<String, Map> materialPpbomMap = orderPpbomsByMaterial != null ? orderPpbomsByMaterial.getOrDefault(String.valueOf(orderId), Collections.emptyMap()) : Collections.emptyMap();
        Map<Integer, TBusOrderProcess> orderProcessByIdMap = (Map<Integer, TBusOrderProcess>) context.get("orderProcessesById");
        List<Map> orderPpbomList = (List<Map>) ((Map)context.get("orderPpboms")).getOrDefault(String.valueOf(orderId), Collections.emptyList());

        List<ProcessGroupInfoVo> finalResult = new ArrayList<>();
        Set<String> handledProcessNumbers = new HashSet<>();

        // 6. 首先处理有报工记录的工序
        for (Map.Entry<String, List<TBusOrderProcessHistory>> entry : reportingProcessMap.entrySet()) {
            List<TBusOrderProcessHistory> records = entry.getValue();
            if (records.isEmpty()) continue;

            TBusOrderProcessHistory firstHistory = records.get(0);
            Integer orderProcessId = firstHistory.getOrderProcessId();
            String processNumber = firstHistory.getProcessNumber();
            String groupCode = firstHistory.getGroupCode();
            TBusOrderProcess orderProcess = orderProcessByIdMap.get(orderProcessId);
            
            ProcessGroupInfoVo groupVo = new ProcessGroupInfoVo();
            groupVo.setProcessName(firstHistory.getProcessName());
            groupVo.setProcessStatus(orderProcess != null ? getProcessStatusDescription(orderProcess.getProcessStatus()) : "未知");
            groupVo.setGroupKey(orderProcessId + "_" + (groupCode != null ? groupCode : ""));
            
            // 处理该工序下的物料
            List<ProcessMaterialInfoVo> materialInfoList = processReportingMaterials(records, orderHead, materialPpbomMap, recipeProcessMap.get(processNumber), orderPpbomList, groupCode);
            groupVo.setMaterialInfoList(materialInfoList);
            
            finalResult.add(groupVo);
            if (processNumber != null) {
                handledProcessNumbers.add(processNumber + "_" + (groupCode != null ? groupCode : ""));
            }
        }

        // 7. 处理没有报工记录但配方中存在的默认工序
        for (Map.Entry<String, Map<String, TSysRecipeInput>> entry : recipeProcessMap.entrySet()) {
            String processNumber = entry.getKey();
            Map<String, TSysRecipeInput> recipeMaterials = entry.getValue();
            if (recipeMaterials.isEmpty()) continue;

            // 根据 semi_finished_product_code 拆分工序展示
            Map<String, List<TSysRecipeInput>> splitGroups = recipeMaterials.values().stream()
                .collect(Collectors.groupingBy(input -> input.getSemiFinishedProductCode() == null ? "" : input.getSemiFinishedProductCode(), LinkedHashMap::new, Collectors.toList()));

            for (List<TSysRecipeInput> subGroupInputs : splitGroups.values()) {
                TSysRecipeInput firstInput = subGroupInputs.get(0);
                String semiFinishedProductCode = firstInput.getSemiFinishedProductCode() == null ? "" : firstInput.getSemiFinishedProductCode();
                
                // 如果该工序+半成品已经有报工记录，则跳过默认展示
                if (handledProcessNumbers.contains(processNumber + "_" + semiFinishedProductCode)) continue;

                // 获取工序名称
                TSysProcessInfo processInfo = allProcessMap.get(processNumber);
                String processName = processInfo != null ? processInfo.getProcessName() : firstInput.getProcessName();

                ProcessGroupInfoVo groupVo = new ProcessGroupInfoVo();
                groupVo.setProcessName(processName);
                groupVo.setProcessStatus("未开工");
                groupVo.setGroupKey(processNumber + "_" + semiFinishedProductCode);

                // 计算该分组的计划锅数
                Integer planPotCount = calculatePlannedPotCount(recipeMaterials, orderPpbomList, semiFinishedProductCode, orderHead);

                List<ProcessMaterialInfoVo> materialInfoList = new ArrayList<>();
                for (TSysRecipeInput recipeInput : subGroupInputs) {
                    ProcessMaterialInfoVo materialVo = new ProcessMaterialInfoVo();
                    materialVo.setMaterialCode(recipeInput.getMaterialCode());
                    materialVo.setMaterialName(recipeInput.getMaterialName());
                    materialVo.setUnit(recipeInput.getUnit());
                    
                    // 设置计划投入：从BOM获取并按配方比例调整
                    Map ppbom = materialPpbomMap.get(recipeInput.getMaterialCode());
                    materialVo.setPlannedInput(calculatePlannedInput(recipeMaterials, ppbom, recipeInput.getMaterialCode(), semiFinishedProductCode));
                    
                    materialVo.setActualInput(BigDecimal.ZERO);
                    materialVo.setPlannedPotCount(planPotCount);
                    materialVo.setActualAccumulatedPotCount(0);
                    materialVo.setDefectiveWeight(BigDecimal.ZERO);
                    materialInfoList.add(materialVo);
                }
                groupVo.setMaterialInfoList(materialInfoList);
                finalResult.add(groupVo);
            }
        }

        return finalResult;
    }

    private List<ProcessMaterialInfoVo> processReportingMaterials(List<TBusOrderProcessHistory> records, TBusOrderHead orderHead, 
                                                                 Map<String, Map> materialPpbomMap, Map<String, TSysRecipeInput> recipeMaterialMap,
                                                                 List<Map> orderPpbomList, String groupCode) {
        if (records == null || records.isEmpty()) return new ArrayList<>();
        
        List<ProcessMaterialInfoVo> materialInfoList = new ArrayList<>();
        Map<String, List<TBusOrderProcessHistory>> materialGroupMap = records.stream()
            .collect(Collectors.groupingBy(TBusOrderProcessHistory::getMaterialNumber));

        // 如果该工序没有报工记录对应的配方，创建一个空的Map避免空指针
        if (recipeMaterialMap == null) recipeMaterialMap = Collections.emptyMap();

        // 计算计划锅数 (一次分组计算一次)
        Integer calculatedPlanPotCount = calculatePlannedPotCount(recipeMaterialMap, orderPpbomList, groupCode, orderHead);

        for (Map.Entry<String, List<TBusOrderProcessHistory>> materialEntry : materialGroupMap.entrySet()) {
            String materialNumber = materialEntry.getKey();
            List<TBusOrderProcessHistory> materialRecords = materialEntry.getValue();
            TBusOrderProcessHistory firstRecord = materialRecords.get(0);
            
            ProcessMaterialInfoVo materialInfo = new ProcessMaterialInfoVo();
            materialInfo.setMaterialCode(materialNumber);
            materialInfo.setMaterialName(firstRecord.getMaterialName());
            materialInfo.setUnit(firstRecord.getRecordUnit());
            materialInfo.setPlannedPotCount(calculatedPlanPotCount);
            
            // 实际累计锅数 - 优化：避免在循环中重复 stream
            int actualAccumulatedPotCount = 0;
            BigDecimal actualInput = BigDecimal.ZERO;
            BigDecimal defectiveWeight = BigDecimal.ZERO;
            
            for (TBusOrderProcessHistory r : materialRecords) {
                BigDecimal qty = r.getRecordQty() != null ? r.getRecordQty() : BigDecimal.ZERO;
                if ("1".equals(r.getRecordType())) {
                    actualAccumulatedPotCount++;
                    actualInput = actualInput.add(qty);
                } else if ("2".equals(r.getRecordType())) {
                    defectiveWeight = defectiveWeight.add(qty);
                }
            }
            
            materialInfo.setActualAccumulatedPotCount(actualAccumulatedPotCount);
            materialInfo.setActualInput(actualInput);
            materialInfo.setDefectiveWeight(defectiveWeight);
            
            // 设置计划投入：从BOM获取并按配方比例调整
            Map ppbom = materialPpbomMap.get(materialNumber);
            materialInfo.setPlannedInput(calculatePlannedInput(recipeMaterialMap, ppbom, materialNumber, groupCode));

            materialInfoList.add(materialInfo);
        }
        return materialInfoList;
    }

    @Override
    public void exportRawMaterialInputReport(Integer current, Integer size, RawMaterialInputQueryDto queryDto, HttpServletResponse response) {
        // 根据要求，自动加上8小时时差调整
        adjustDates(queryDto);
        
        try {
            // 获取所有数据（不分页）
            RawMaterialInputQueryDto allDataQuery = new RawMaterialInputQueryDto();
            allDataQuery.setProductName(queryDto.getProductName());
            allDataQuery.setOrderDateStart(queryDto.getOrderDateStart());
            allDataQuery.setOrderDateEnd(queryDto.getOrderDateEnd());
            
            // 查询所有订单数据
            List<RawMaterialInputReportVo> allDataList = getAllRawMaterialInputReportDataOptimized(allDataQuery);
            
            // 转换为Excel导出格式
            List<RawMaterialInputReportExcelVo> excelDataList = convertToExcelData(allDataList);
            
            // 设置样式（水平和垂直居中）
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            HorizontalCellStyleStrategy styleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

            // 使用EasyExcel直接写入响应流
            String fileName = "原料投入报表_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            EasyExcel.write(response.getOutputStream(), RawMaterialInputReportExcelVo.class)
                .registerWriteHandler(styleStrategy)
                .registerWriteHandler(new CustomMergeStrategy(excelDataList))
                .sheet("原料投入报表")
                .doWrite(excelDataList);
        } catch (Exception e) {
            log.error("导出原料投入报表失败: ", e);
            throw new RuntimeException("导出原料投入报表失败");
        }
    }

    private List<RawMaterialInputReportVo> getAllRawMaterialInputReportDataOptimized(RawMaterialInputQueryDto queryDto) {
        // 获取当前登录用户ID
        String currentUserId = getCurrentUserId();
        // 获取用户绑定的产线ID列表
        List<String> userCwkids = userService.getUserCurrentCwkid(currentUserId);
        
        // 创建排序规则：按下单日期降序
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "billDate"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "orderId"));
        Sort sort = Sort.by(orders);
        
        // 分页获取数据，避免一次性查询太多导致内存问题，但增加批次大小
        int batchSize = 500;
        int current = 0;
        List<RawMaterialInputReportVo> allList = new ArrayList<>();
        
        // 获取所有工序映射以加速查询
        Map<String, TSysProcessInfo> allProcessMap = processInfoRepository.findAll().stream()
                .collect(Collectors.toMap(TSysProcessInfo::getProcessNumber, p -> p, (a, b) -> a));
        
        while (true) {
            Pageable pageable = PageRequest.of(current, batchSize, sort);
            Page<TBusOrderHead> orderHeadPage = orderHeadRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(queryDto.getProductName())) {
                    predicates.add(criteriaBuilder.like(root.get("bodyMaterialName"), "%" + queryDto.getProductName() + "%"));
                }
                // 订单号（模糊查询）
                if (!StringUtils.isEmpty(queryDto.getOrderNo())) {
                    predicates.add(criteriaBuilder.like(root.get("orderNo"), "%" + queryDto.getOrderNo() + "%"));
                }
                if (queryDto.getOrderDateStart() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("billDate"), queryDto.getOrderDateStart()));
                }
                if (queryDto.getOrderDateEnd() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("billDate"), queryDto.getOrderDateEnd()));
                }
                
                // 根据用户绑定的产线ID过滤
                if (userCwkids != null && !userCwkids.isEmpty()) {
                    predicates.add(root.get("cwkid").in(userCwkids));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }, pageable);
            
            if (!orderHeadPage.hasContent()) break;
            
            List<TBusOrderHead> heads = orderHeadPage.getContent();
            Map<String, Object> context = prefetchData(heads, allProcessMap);
            
            // 使用并行流显著提升大数据量下的数据组装速度
            List<RawMaterialInputReportVo> batchResults = heads.parallelStream().map(order -> {
                RawMaterialInputReportVo vo = new RawMaterialInputReportVo();
                vo.setOrderNo(order.getOrderNo());
                // 修正时区显示问题
                Date adjustedOrderTime = add8HoursToTime(order.getBillDate());
                vo.setOrderTime(adjustedOrderTime);
                vo.setProductName(order.getBodyMaterialName());
                vo.setProductionLine(order.getVwkname());
                calculateOutputDataWithContext(vo, order.getOrderNo(), context);
                vo.setProcessGroupInfoList(getProcessGroupInfoListWithContext(order.getOrderNo(), context));
                return vo;
            }).collect(Collectors.toList());
            allList.addAll(batchResults);
            
            if (orderHeadPage.isLast()) break;
            current++;
            // 安全限制，防止无限循环
            if (allList.size() >= 10000) break;
        }
        
        return allList;
    }
    private List<RawMaterialInputReportExcelVo> convertToExcelData(List<RawMaterialInputReportVo> allDataList) {
        List<RawMaterialInputReportExcelVo> excelDataList = new ArrayList<>();
        if (allDataList == null) {
            return excelDataList;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (RawMaterialInputReportVo reportVo : allDataList) {
            List<ProcessGroupInfoVo> processGroups = reportVo.getProcessGroupInfoList();
            if (processGroups == null || processGroups.isEmpty()) {
                // 如果没有工序信息，至少导出订单基础信息
                RawMaterialInputReportExcelVo excelVo = new RawMaterialInputReportExcelVo();
                fillBaseOrderInfo(excelVo, reportVo, sdf);
                excelDataList.add(excelVo);
            } else {
                for (ProcessGroupInfoVo processGroup : processGroups) {
                    List<ProcessMaterialInfoVo> materialInfos = processGroup.getMaterialInfoList();
                    if (materialInfos == null || materialInfos.isEmpty()) {
                        // 如果有工序但没有物料信息，导出订单+工序信息
                        RawMaterialInputReportExcelVo excelVo = new RawMaterialInputReportExcelVo();
                        fillBaseOrderInfo(excelVo, reportVo, sdf);
                        excelVo.setProcessName(processGroup.getProcessName());
                        excelVo.setProcessStatus(processGroup.getProcessStatus());
                        excelDataList.add(excelVo);
                    } else {
                        for (ProcessMaterialInfoVo materialInfo : materialInfos) {
                            RawMaterialInputReportExcelVo excelVo = new RawMaterialInputReportExcelVo();
                            fillBaseOrderInfo(excelVo, reportVo, sdf);
                            
                            // 设置工序信息
                            excelVo.setProcessName(processGroup.getProcessName());
                            excelVo.setProcessStatus(processGroup.getProcessStatus());
                            excelVo.setProcessGroupKey(processGroup.getGroupKey());
                            
                            // 设置物料信息
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

    private void fillBaseOrderInfo(RawMaterialInputReportExcelVo excelVo, RawMaterialInputReportVo reportVo, SimpleDateFormat sdf) {
        excelVo.setOrderNo(reportVo.getOrderNo());
        excelVo.setOrderTime(reportVo.getOrderTime() != null ? sdf.format(reportVo.getOrderTime()) : "");
        excelVo.setProductionLine(reportVo.getProductionLine());
        excelVo.setProductName(reportVo.getProductName());
        excelVo.setPlannedOutput(reportVo.getPlannedOutput() != null ? reportVo.getPlannedOutput().toString() : "0");
        excelVo.setActualOutput(reportVo.getActualOutput() != null ? reportVo.getActualOutput().toString() : "0");
        excelVo.setNetContent(reportVo.getNetContent() != null ? reportVo.getNetContent().toString() : "0");
    }

    /**
     * 设置生产相关值（计划投入、实际投入、计划锅数、实际累计锅数）
     * @param materialInfo 物料信息对象
     * @param orderNo 订单号
     * @param orderProcessId 工序执行表ID
     * @param materialNumber 物料编号
     */
    private void setProductionValuesForMaterialInfo(ProcessMaterialInfoVo materialInfo, String orderNo, Integer orderProcessId, String materialNumber, String groupCode) {
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
            
            // 查找匹配的物料信息，设置计划投入量和计划锅数
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
                    
                    // 设置计划锅数：严格按照getOrderPpbom接口的calculatePlanPotCount逻辑进行计算
                    // 关键点：必须在当前分组内查找锅数计算基准='1'的物料，不能跨分组查找
                    Integer calculatedPlanPotCount = null;
                    
                    // 1. 先查找当前分组内锅数计算基准='1'的物料
                    for (Map otherPpbom : orderPpbomList) {
                        String otherMaterialNumber = (String) otherPpbom.get("material_number");
                        
                        // 从配方管理中获取锅数计算基准，严格按分组编码匹配
                        TSysRecipeInput otherRecipeInput = null;
                        if (groupCode != null && StringUtils.isNotEmpty(groupCode)) {
                            // 有分组编码时，只在当前分组内查找
                            otherRecipeInput = recipeMaterialMap.get(groupCode + "_" + otherMaterialNumber);
                        } else {
                            // 无分组编码时，使用null或空字符串作为前缀查找
                            otherRecipeInput = recipeMaterialMap.get("null_" + otherMaterialNumber);
                            if (otherRecipeInput == null) {
                                otherRecipeInput = recipeMaterialMap.get("_" + otherMaterialNumber);
                            }
                        }
                        
                        // 如果在当前分组内找不到，且有分组编码，则尝试使用通用配方（fallback到无分组配方）
                        if (otherRecipeInput == null && groupCode != null && StringUtils.isNotEmpty(groupCode)) {
                            otherRecipeInput = recipeMaterialMap.get("null_" + otherMaterialNumber);
                            if (otherRecipeInput == null) {
                                otherRecipeInput = recipeMaterialMap.get("_" + otherMaterialNumber);
                            }
                        }
                        
                        // 最后尝试遍历查找（但要确保锅数计算基准='1'且必须匹配当前分组编码）
                        if (otherRecipeInput == null || !"1".equals(otherRecipeInput.getPotCalculationBasis())) {
                            final String targetGroupCode = groupCode;
                            otherRecipeInput = recipeMaterialMap.values().stream()
                                    .filter(input -> input != null 
                                            && otherMaterialNumber.equals(input.getMaterialCode())
                                            && "1".equals(input.getPotCalculationBasis())
                                            // 关键修正：确保找到的配方物料属于当前分组
                                            && (Objects.equals(targetGroupCode, input.getSemiFinishedProductCode()) 
                                                || (StringUtils.isEmpty(targetGroupCode) && StringUtils.isEmpty(input.getSemiFinishedProductCode()))))
                                    .findFirst()
                                    .orElse(null);
                        }
                        
                        // 如果找到锅数计算基准='1'的物料
                        if (otherRecipeInput != null && "1".equals(otherRecipeInput.getPotCalculationBasis())) {
                            mustQtyObj = otherPpbom.get("must_qty");
                            BigDecimal standardInput = otherRecipeInput.getStandardInput();
                            
                            if (mustQtyObj != null && standardInput != null && standardInput.compareTo(BigDecimal.ZERO) > 0) {
                                BigDecimal mustQty = new BigDecimal(mustQtyObj.toString());
                                
                                // 应用计划投入比例
                                planInputRatioObj = null;
                                if (otherRecipeInput.getPlanInputRatio() != null) {
                                    planInputRatioObj = otherRecipeInput.getPlanInputRatio();
                                } else {
                                    planInputRatioObj = otherPpbom.get("plan_input_ratio");
                                }
                                
                                if (planInputRatioObj != null) {
                                    BigDecimal planInputRatio = new BigDecimal(planInputRatioObj.toString());
                                    mustQty = mustQty.multiply(planInputRatio).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
                                }
                                
                                // 计算计划锅数 = 计划投入 / 每锅投入标准，向上取整
                                BigDecimal planPotCount = mustQty.divide(standardInput, 2, BigDecimal.ROUND_HALF_UP);
                                calculatedPlanPotCount = planPotCount.setScale(0, BigDecimal.ROUND_UP).intValue();
                                log.debug("使用锅数计算基准物料计算计划锅数: 物料={}, 计划投入={}, 每锅标准={}, 计划锅数={}, 分组编码={}", 
                                    otherMaterialNumber, mustQty, standardInput, calculatedPlanPotCount, groupCode);
                                break;
                            }
                        }
                    }
                    
                    // 2. 如果没有找到锅数计算基准='1'的物料，使用订单头的计划锅数
                    if (calculatedPlanPotCount != null) {
                        materialInfo.setPlannedPotCount(calculatedPlanPotCount);
                    } else if (orderHeadList != null && !orderHeadList.isEmpty()) {
                        TBusOrderHead orderHead = orderHeadList.get(0);
                        if (orderHead.getBodyPotQty() != null) {
                            materialInfo.setPlannedPotCount(orderHead.getBodyPotQty());
                            log.debug("使用订单头计划锅数: {}", orderHead.getBodyPotQty());
                        } else {
                            materialInfo.setPlannedPotCount(0);
                            log.debug("未找到任何计划锅数，设置为默认值0");
                        }
                    } else {
                        materialInfo.setPlannedPotCount(0);
                        log.debug("未找到订单头信息，设置计划锅数为默认值0");
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
        // 对应getOrderPpbom接口中的personalCount字段，需要获取分组内所有物料的最小投入次数
        // 需要按分组编码进行区分
        if (orderProcessId != null && materialNumber != null) {
            // 获取当前订单工序指定分组的所有物料编码（用于计算分组内最小投入次数）
            List<TBusOrderProcessRecord> allRecords = orderProcessRecordRepository
                .findAllByOrderProcessIdAndRecordType(orderProcessId, "1");
            
            // 如果有分组编码,只获取对应分组的物料
            List<String> groupMaterialCodes;
            if (groupCode != null && StringUtils.isNotEmpty(groupCode)) {
                groupMaterialCodes = allRecords.stream()
                    .filter(r -> groupCode.equals(r.getGroupCode()))
                    .map(TBusOrderProcessRecord::getMaterialNumber)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
            } else {
                groupMaterialCodes = allRecords.stream()
                    .map(TBusOrderProcessRecord::getMaterialNumber)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 使用getMinInputCountByGroup方法获取分组内物料的最小投入次数
            // 这与getOrderPpbom接口的逻辑保持一致,传入分组编码
            int actualAccumulatedPotCount = getMinInputCountByGroup(orderProcessId, groupMaterialCodes, groupCode);
            materialInfo.setActualAccumulatedPotCount(actualAccumulatedPotCount);
            log.debug("使用分组最小投入次数作为实际累计锅数: {}, 分组编码: {}", actualAccumulatedPotCount, groupCode);
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

    /**
     * 自定义合并策略
     */
    private static class CustomMergeStrategy implements SheetWriteHandler {
        private final List<RawMaterialInputReportExcelVo> excelDataList;

        public CustomMergeStrategy(List<RawMaterialInputReportExcelVo> excelDataList) {
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
            // 表头占据2行（因为我们定义了二级表头），所以数据从第3行开始（索引2）
            int startRow = 2;
            
            // 按订单号合并基础信息（0-6列）
            int i = 0;
            while (i < excelDataList.size()) {
                int j = i + 1;
                while (j < excelDataList.size() && Objects.equals(excelDataList.get(i).getOrderNo(), excelDataList.get(j).getOrderNo())) {
                    j++;
                }
                if (j - i > 1) {
                    // 合并订单基础信息和合格品信息 (0-6列)
                    for (int col = 0; col <= 6; col++) {
                        sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + j - 1, col, col));
                    }
                }
                
                // 在当前订单范围内，按工序分组标识合并工序信息（7-8列）
                int k = i;
                while (k < j) {
                    int m = k + 1;
                    while (m < j && Objects.equals(excelDataList.get(k).getProcessGroupKey(), excelDataList.get(m).getProcessGroupKey())) {
                        m++;
                    }
                    if (m - k > 1) {
                        // 合并工序名称和工序状态 (7-8列)
                        sheet.addMergedRegion(new CellRangeAddress(startRow + k, startRow + m - 1, 7, 7));
                        sheet.addMergedRegion(new CellRangeAddress(startRow + k, startRow + m - 1, 8, 8));
                    }
                    k = m;
                }
                i = j;
            }
        }
    }
}