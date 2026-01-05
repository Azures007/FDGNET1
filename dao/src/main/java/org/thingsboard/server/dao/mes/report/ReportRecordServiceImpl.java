package org.thingsboard.server.dao.mes.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.LichengConstants;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.mes.vo.ReportRecordVo;
import org.thingsboard.server.dao.mes.dto.ReportRecordQueryDto;
import org.thingsboard.server.dao.mes.tSysCodeDsc.TSysCodeDscService;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.report.ReportRecordRepository;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author
 * @version V1.0
 * @Package org.thingsboard.server.dao.mes.report
 * @date 2025/12/29
 * @Description: 报工记录服务实现类
 */
@Service
@Slf4j
public class ReportRecordServiceImpl implements ReportRecordService {

    @Autowired
    private ReportRecordRepository reportRecordRepository;

    @Autowired
    private TSysCodeDscService sysCodeDscService;

    @Autowired
    private OrderHeadRepository orderHeadRepository;
    
    @Override
    public Page<TBusOrderProcessHistory> getReportRecordList(Integer current, Integer size, ReportRecordQueryDto queryDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "reportTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        // 创建查询条件 - 调用自定义方法
        Specification<TBusOrderProcessHistory> specification = createSpecification(queryDto);
        
        return reportRecordRepository.findAll(specification, pageable);
    }
    
    @Override
    public Page<ReportRecordVo> getReportRecordListVo(Integer current, Integer size, ReportRecordQueryDto queryDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "reportTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        
        // 创建查询条件 - 调用自定义方法
        Specification<TBusOrderProcessHistory> specification = createSpecification(queryDto);
        
        Page<TBusOrderProcessHistory> page = reportRecordRepository.findAll(specification, pageable);
        
        // 批量获取订单头信息
        List<String> orderNos = page.getContent().stream()
                .map(TBusOrderProcessHistory::getOrderNo)
                .distinct()
                .collect(Collectors.toList());
        // 获取订单头映射 - 调用自定义方法
        Map<String, TBusOrderHead> orderHeadMap = getOrderHeadMap(orderNos);

        List<ReportRecordVo> voList = new ArrayList<>();
        for (TBusOrderProcessHistory history : page.getContent()) {
            // 转换为VO对象 - 调用自定义方法
            ReportRecordVo vo = convertToVo(history, orderHeadMap);
            voList.add(vo);
        }
        
        return new org.springframework.data.domain.PageImpl<>(voList, pageable, page.getTotalElements());
    }
    
    @Override
    public List<ReportRecordVo> getReportRecordListForExport(ReportRecordQueryDto queryDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "reportTime");
        // 创建查询条件 - 调用自定义方法
        Specification<TBusOrderProcessHistory> specification = createSpecification(queryDto);
        List<TBusOrderProcessHistory> historyList = reportRecordRepository.findAll(specification, sort);
        
        if (historyList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 批量获取订单头信息和字典值
        List<String> orderNos = historyList.stream()
                .map(TBusOrderProcessHistory::getOrderNo)
                .distinct()
                .collect(Collectors.toList());
        // 获取订单头映射 - 调用自定义方法
        Map<String, TBusOrderHead> orderHeadMap = getOrderHeadMap(orderNos);
        
        List<String> recordTypes = historyList.stream()
                .map(TBusOrderProcessHistory::getRecordType)
                .filter(recordType -> recordType != null && !"1".equals(recordType) && !"3".equals(recordType))
                .distinct()
                .collect(Collectors.toList());
        // 获取记录类型字典映射 - 调用自定义方法
        Map<String, TSysCodeDsc> codeDscMap = getRecordTypeCodeMap(recordTypes);
        
        // 预先创建VO列表，避免动态扩容
        List<ReportRecordVo> voList = new ArrayList<>(historyList.size());

        for (TBusOrderProcessHistory history : historyList) {
            // 转换为VO对象 - 调用自定义方法
            ReportRecordVo vo = convertToVo(history, orderHeadMap, codeDscMap);
            voList.add(vo);
        }

        return voList;
    }
    
    @Override
    public List<ReportRecordVo> getReportRecordListForExportOptimized(ReportRecordQueryDto queryDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "reportTime");
        // 创建查询条件
        Specification<TBusOrderProcessHistory> specification = createSpecification(queryDto);
        
        // 首先获取总数量，确定需要分页查询的次数
        long total = reportRecordRepository.count(specification);
        if (total == 0) {
            return new ArrayList<>();
        }
        
        int pageSize = 1000; // 每页大小
        int totalPages = (int) Math.ceil((double) total / pageSize);
        List<ReportRecordVo> allResults = new ArrayList<>();
        
        // 分批查询数据
        for (int page = 0; page < totalPages; page++) {
            Pageable pageable = PageRequest.of(page, pageSize, sort);
            Page<TBusOrderProcessHistory> pageResult = reportRecordRepository.findAll(specification, pageable);
            
            List<TBusOrderProcessHistory> historyList = pageResult.getContent();
            
            if (!historyList.isEmpty()) {
                // 批量获取订单头信息
                List<String> orderNos = historyList.stream()
                        .map(TBusOrderProcessHistory::getOrderNo)
                        .distinct()
                        .collect(Collectors.toList());
                // 获取订单头映射
                Map<String, TBusOrderHead> orderHeadMap = getOrderHeadMap(orderNos);
                
                // 批量获取所有相关的字典值
                List<String> recordTypes = historyList.stream()
                        .map(TBusOrderProcessHistory::getRecordType)
                        .filter(recordType -> recordType != null && !"1".equals(recordType) && !"3".equals(recordType))
                        .distinct()
                        .collect(Collectors.toList());
                // 获取记录类型字典映射
                Map<String, TSysCodeDsc> codeDscMap = getRecordTypeCodeMap(recordTypes);
                
                // 转换为VO对象
                List<ReportRecordVo> voList = new ArrayList<>(historyList.size());
                for (TBusOrderProcessHistory history : historyList) {
                    // 转换为VO对象
                    ReportRecordVo vo = convertToVo(history, orderHeadMap, codeDscMap);
                    voList.add(vo);
                }
                
                allResults.addAll(voList);
            }
        }
        
        return allResults;
    }
    
    private ReportRecordVo convertToVo(TBusOrderProcessHistory history, Map<String, TBusOrderHead> orderHeadMap) {
        List<String> recordTypes = new ArrayList<>();
        if (history.getRecordType() != null && !"1".equals(history.getRecordType()) && !"3".equals(history.getRecordType())) {
            recordTypes.add(history.getRecordType());
        }
        // 获取记录类型字典映射
        Map<String, TSysCodeDsc> codeDscMap = getRecordTypeCodeMap(recordTypes);
        // 转换为VO对象
        return convertToVo(history, orderHeadMap, codeDscMap);
    }
    
    private ReportRecordVo convertToVo(TBusOrderProcessHistory history, Map<String, TBusOrderHead> orderHeadMap, Map<String, TSysCodeDsc> codeDscMap) {
        ReportRecordVo vo = new ReportRecordVo();
        vo.setOrderNo(history.getOrderNo());
        vo.setProcessName(history.getProcessName());
        vo.setReportTime(history.getReportTime());
        if (history.getPersonId() != null) {
            vo.setPersonName(history.getPersonId().getName());
        }
        // 从字典获取record_type的标签值，并按要求进行特殊处理
        if (history.getRecordType() != null) {
            if ("1".equals(history.getRecordType())) {
                vo.setRecordTypeBgName("原辅料投入");
            } else if ("3".equals(history.getRecordType())) {
                vo.setRecordTypeBgName("产成品");
            } else {
                // 对于其他值，从字典获取标签值
                TSysCodeDsc codeDsc = codeDscMap.get(history.getRecordType());
                if (codeDsc != null && codeDsc.getCodeDsc() != null) {
                    vo.setRecordTypeBgName(codeDsc.getCodeDsc());
                } else {
                    vo.setRecordTypeBgName(history.getRecordType());
                }
            }
        }
        vo.setMaterialName(history.getMaterialName());
        vo.setMaterialNumber(history.getMaterialNumber());
        vo.setPotNumber(history.getPotNumber());
        vo.setRecordQty(history.getRecordQty());
        vo.setRecordUnit(history.getRecordUnit());
        TBusOrderHead orderHead = orderHeadMap.get(history.getOrderNo());
        if (orderHead != null) {
            vo.setProductName(orderHead.getBodyMaterialName());
            vo.setProductNumber(orderHead.getBodyMaterialNumber());
            vo.setCwkLine(orderHead.getVwkname()); // 设置产线名称
        }
        return vo;
    }
    
    private Specification<TBusOrderProcessHistory> createSpecification(ReportRecordQueryDto queryDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("busType"), LichengConstants.ORDER_BUS_TYPE_BG));
            predicates.add(criteriaBuilder.notEqual(root.get("reportStatus"), LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1));
            if (queryDto.getReportTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reportTime"), queryDto.getReportTimeStart()));
            }
            if (queryDto.getReportTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("reportTime"), queryDto.getReportTimeEnd()));
            }
            if (queryDto.getOrderNo() != null && !queryDto.getOrderNo().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("orderNo"), "%" + queryDto.getOrderNo().trim() + "%"));
            }
            if (queryDto.getMaterialName() != null && !queryDto.getMaterialName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("materialName"), "%" + queryDto.getMaterialName().trim() + "%"));
            }
            if (queryDto.getMaterialNumber() != null && !queryDto.getMaterialNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("materialNumber"), "%" + queryDto.getMaterialNumber().trim() + "%"));
            }
            
            if (queryDto.getProductName() != null && !queryDto.getProductName().trim().isEmpty()) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<TBusOrderHead> orderHeadRoot = subquery.from(TBusOrderHead.class);
                subquery.select(orderHeadRoot.get("orderNo"))
                        .where(criteriaBuilder.and(
                            criteriaBuilder.like(orderHeadRoot.get("bodyMaterialName"), "%" + queryDto.getProductName().trim() + "%"),
                            criteriaBuilder.equal(orderHeadRoot.get("orderNo"), root.get("orderNo"))
                        ));
                predicates.add(criteriaBuilder.exists(subquery));
            }
            if (queryDto.getProductNumber() != null && !queryDto.getProductNumber().trim().isEmpty()) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<TBusOrderHead> orderHeadRoot = subquery.from(TBusOrderHead.class);
                subquery.select(orderHeadRoot.get("orderNo"))
                        .where(criteriaBuilder.and(
                            criteriaBuilder.like(orderHeadRoot.get("bodyMaterialNumber"), "%" + queryDto.getProductNumber().trim() + "%"),
                            criteriaBuilder.equal(orderHeadRoot.get("orderNo"), root.get("orderNo"))
                        ));
                predicates.add(criteriaBuilder.exists(subquery));
            }
            
            if (queryDto.getProcessName() != null && !queryDto.getProcessName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("processName"), "%" + queryDto.getProcessName().trim() + "%"));
            }
            
            if (queryDto.getCwkLine() != null && !queryDto.getCwkLine().trim().isEmpty()) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<TBusOrderHead> orderHeadRoot = subquery.from(TBusOrderHead.class);
                subquery.select(orderHeadRoot.get("orderNo"))
                        .where(criteriaBuilder.and(
                            criteriaBuilder.equal(orderHeadRoot.get("cwkid"), queryDto.getCwkLine().trim()),
                            criteriaBuilder.equal(orderHeadRoot.get("orderNo"), root.get("orderNo"))
                        ));
                predicates.add(criteriaBuilder.exists(subquery));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    private Map<String, TSysCodeDsc> getRecordTypeCodeMap(List<String> recordTypes) {
        if (recordTypes == null || recordTypes.isEmpty()) {
            return new HashMap<>();
        }
        
        // 批量获取字典值
        return recordTypes.stream().collect(Collectors.toMap(
            recordType -> recordType,
            recordType -> sysCodeDscService.getCodeByCodeClAndCodeVale("RECORDTYPE0000", recordType)
        ));
    }

    private Map<String, TBusOrderHead> getOrderHeadMap(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return new HashMap<>();
        }
        List<String> uniqueOrderNos = orderNos.stream().distinct().collect(Collectors.toList());
        List<TBusOrderHead> orderHeads = orderHeadRepository.findAllByOrderNoIn(uniqueOrderNos);
        
        // 返回订单头映射
        return orderHeads.stream().collect(Collectors.toMap(TBusOrderHead::getOrderNo, head -> head, (existing, replacement) -> existing));
    }
}