package org.thingsboard.server.dao.mes.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.LichengConstants;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.mes.vo.ReportRecordVo;
import org.thingsboard.server.dao.mes.dto.ReportRecordQueryDto;
import org.thingsboard.server.dao.mes.report.ReportRecordService;
import org.thingsboard.server.dao.mes.tSysCodeDsc.TSysCodeDscService;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.report.ReportRecordRepository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        Specification<TBusOrderProcessHistory> specification = (root, query, criteriaBuilder) -> {
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
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return reportRecordRepository.findAll(specification, pageable);
    }
    
    @Override
    public Page<ReportRecordVo> getReportRecordListVo(Integer current, Integer size, ReportRecordQueryDto queryDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "reportTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        
        Specification<TBusOrderProcessHistory> specification = (root, query, criteriaBuilder) -> {
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
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<TBusOrderProcessHistory> page = reportRecordRepository.findAll(specification, pageable);
        
        // 批量获取订单头信息
        List<String> orderNos = page.getContent().stream()
                .map(TBusOrderProcessHistory::getOrderNo)
                .distinct()
                .collect(Collectors.toList());
        Map<String, TBusOrderHead> orderHeadMap = getOrderHeadMap(orderNos);

        List<ReportRecordVo> voList = new ArrayList<>();
        for (TBusOrderProcessHistory history : page.getContent()) {
            ReportRecordVo vo = new ReportRecordVo();
            vo.setOrderNo(history.getOrderNo());
            vo.setProcessName(history.getProcessName());
            vo.setReportTime(history.getReportTime());

            if (history.getPersonId() != null) {
                vo.setPersonName(history.getPersonId().getName());
            }

            if (history.getRecordType() != null) {
                if ("1".equals(history.getRecordType())) {
                    vo.setRecordTypeBgName("原辅料投入");
                } else if ("3".equals(history.getRecordType())) {
                    vo.setRecordTypeBgName("产成品");
                } else {

                    TSysCodeDsc codeDsc = sysCodeDscService.getCodeByCodeClAndCodeVale("RECORDTYPE0000", history.getRecordType());
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

            if (history.getClassId() != null) {
                vo.setClassName(history.getClassId().getName());
            }

            TBusOrderHead orderHead = orderHeadMap.get(history.getOrderNo());
            if (orderHead != null) {
                vo.setProductName(orderHead.getBodyMaterialName());
                vo.setProductNumber(orderHead.getBodyMaterialNumber());
            }
            
            voList.add(vo);
        }
        
        return new org.springframework.data.domain.PageImpl<>(voList, pageable, page.getTotalElements());
    }
    
    @Override
    public List<ReportRecordVo> getReportRecordListForExport(ReportRecordQueryDto queryDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "reportTime");

        Specification<TBusOrderProcessHistory> specification = (root, query, criteriaBuilder) -> {
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

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        List<TBusOrderProcessHistory> historyList = reportRecordRepository.findAll(specification, sort);
        // 批量获取订单头信息
        List<String> orderNos = historyList.stream()
                .map(TBusOrderProcessHistory::getOrderNo)
                .distinct()
                .collect(Collectors.toList());
        Map<String, TBusOrderHead> orderHeadMap = getOrderHeadMap(orderNos);

        List<ReportRecordVo> voList = new ArrayList<>();
        for (TBusOrderProcessHistory history : historyList) {
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
                    TSysCodeDsc codeDsc = sysCodeDscService.getCodeByCodeClAndCodeVale("RECORDTYPE0000", history.getRecordType());
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
            if (history.getClassId() != null) {
                vo.setClassName(history.getClassId().getName());
            }
            TBusOrderHead orderHead = orderHeadMap.get(history.getOrderNo());
            if (orderHead != null) {
                vo.setProductName(orderHead.getBodyMaterialName());
                vo.setProductNumber(orderHead.getBodyMaterialNumber());
            }

            voList.add(vo);
        }

        return voList;
    }

    private Map<String, TBusOrderHead> getOrderHeadMap(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return new HashMap<>();
        }
        List<String> uniqueOrderNos = orderNos.stream().distinct().collect(Collectors.toList());
        List<TBusOrderHead> orderHeads = orderHeadRepository.findAllByOrderNoIn(uniqueOrderNos);
        
        return orderHeads.stream().collect(Collectors.toMap(TBusOrderHead::getOrderNo, head -> head, (existing, replacement) -> existing));
    }
}