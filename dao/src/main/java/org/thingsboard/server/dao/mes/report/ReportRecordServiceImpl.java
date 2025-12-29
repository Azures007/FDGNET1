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
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.vo.ReportRecordVo;
import org.thingsboard.server.dao.mes.dto.ReportRecordQueryDto;
import org.thingsboard.server.dao.mes.report.ReportRecordService;
import org.thingsboard.server.dao.sql.mes.report.ReportRecordRepository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @Override
    public Page<TBusOrderProcessHistory> getReportRecordList(Integer current, Integer size, ReportRecordQueryDto queryDto) {
        // 按报工时间倒序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "reportTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        
        Specification<TBusOrderProcessHistory> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 业务类型为报工
            predicates.add(criteriaBuilder.equal(root.get("busType"), LichengConstants.ORDER_BUS_TYPE_BG));
            
            // 报工状态不为删除状态
            predicates.add(criteriaBuilder.notEqual(root.get("reportStatus"), LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1));
            
            // 报工时间范围查询
            if (queryDto.getReportTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reportTime"), queryDto.getReportTimeStart()));
            }
            if (queryDto.getReportTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("reportTime"), queryDto.getReportTimeEnd()));
            }
            
            // 订单号模糊查询
            if (queryDto.getOrderNo() != null && !queryDto.getOrderNo().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("orderNo"), "%" + queryDto.getOrderNo().trim() + "%"));
            }
            
            // 物料名称模糊查询
            if (queryDto.getMaterialName() != null && !queryDto.getMaterialName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("materialName"), "%" + queryDto.getMaterialName().trim() + "%"));
            }
            
            // 物料编码模糊查询
            if (queryDto.getMaterialNumber() != null && !queryDto.getMaterialNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("materialNumber"), "%" + queryDto.getMaterialNumber().trim() + "%"));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return reportRecordRepository.findAll(specification, pageable);
    }
    
    @Override
    public Page<ReportRecordVo> getReportRecordListVo(Integer current, Integer size, ReportRecordQueryDto queryDto) {
        // 按报工时间倒序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "reportTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        
        Specification<TBusOrderProcessHistory> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 业务类型为报工
            predicates.add(criteriaBuilder.equal(root.get("busType"), LichengConstants.ORDER_BUS_TYPE_BG));
            
            // 报工状态不为删除状态
            predicates.add(criteriaBuilder.notEqual(root.get("reportStatus"), LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1));
            
            // 报工时间范围查询
            if (queryDto.getReportTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reportTime"), queryDto.getReportTimeStart()));
            }
            if (queryDto.getReportTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("reportTime"), queryDto.getReportTimeEnd()));
            }
            
            // 订单号模糊查询
            if (queryDto.getOrderNo() != null && !queryDto.getOrderNo().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("orderNo"), "%" + queryDto.getOrderNo().trim() + "%"));
            }
            
            // 物料名称模糊查询
            if (queryDto.getMaterialName() != null && !queryDto.getMaterialName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("materialName"), "%" + queryDto.getMaterialName().trim() + "%"));
            }
            
            // 物料编码模糊查询
            if (queryDto.getMaterialNumber() != null && !queryDto.getMaterialNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("materialNumber"), "%" + queryDto.getMaterialNumber().trim() + "%"));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<TBusOrderProcessHistory> page = reportRecordRepository.findAll(specification, pageable);
        
        // 将实体对象转换为VO对象
        List<ReportRecordVo> voList = new ArrayList<>();
        for (TBusOrderProcessHistory history : page.getContent()) {
            ReportRecordVo vo = new ReportRecordVo();
            vo.setOrderNo(history.getOrderNo());
            vo.setProcessName(history.getProcessName());
            vo.setReportTime(history.getReportTime());
            
            // 设置报工人员名称
            if (history.getPersonId() != null) {
                vo.setPersonName(history.getPersonId().getName());
            }
            
            // 设置报工类型名称
            if (LichengConstants.REPORTYPE0001.equals(history.getRecordTypeBg())) {
                vo.setRecordTypeBgName("正常");
            } else if (LichengConstants.REPORTYPE0002.equals(history.getRecordTypeBg())) {
                vo.setRecordTypeBgName("尾料");
            } else {
                vo.setRecordTypeBgName(history.getRecordTypeBg());
            }
            
            vo.setMaterialName(history.getMaterialName());
            vo.setMaterialNumber(history.getMaterialNumber());
            vo.setPotNumber(history.getPotNumber()); // 使用potNumber字段
            vo.setRecordQty(history.getRecordQty());
            vo.setRecordUnit(history.getRecordUnit());
            
            // 设置班别名称
            if (history.getClassId() != null) {
                vo.setClassName(history.getClassId().getName());
            }
            
            voList.add(vo);
        }
        
        return new org.springframework.data.domain.PageImpl<>(voList, pageable, page.getTotalElements());
    }
    
    @Override
    public List<ReportRecordVo> getReportRecordListForExport(ReportRecordQueryDto queryDto) {
        // 按报工时间倒序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "reportTime");
        
        Specification<TBusOrderProcessHistory> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 业务类型为报工
            predicates.add(criteriaBuilder.equal(root.get("busType"), LichengConstants.ORDER_BUS_TYPE_BG));
            // 报工状态不为删除状态
            predicates.add(criteriaBuilder.notEqual(root.get("reportStatus"), LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1));
            // 报工时间范围查询
            if (queryDto.getReportTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reportTime"), queryDto.getReportTimeStart()));
            }
            if (queryDto.getReportTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("reportTime"), queryDto.getReportTimeEnd()));
            }
            // 订单号模糊查询
            if (queryDto.getOrderNo() != null && !queryDto.getOrderNo().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("orderNo"), "%" + queryDto.getOrderNo().trim() + "%"));
            }
            // 物料名称模糊查询
            if (queryDto.getMaterialName() != null && !queryDto.getMaterialName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("materialName"), "%" + queryDto.getMaterialName().trim() + "%"));
            }
            // 物料编码模糊查询
            if (queryDto.getMaterialNumber() != null && !queryDto.getMaterialNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("materialNumber"), "%" + queryDto.getMaterialNumber().trim() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        List<TBusOrderProcessHistory> historyList = reportRecordRepository.findAll(specification, sort);
        List<ReportRecordVo> voList = new ArrayList<>();
        for (TBusOrderProcessHistory history : historyList) {
            ReportRecordVo vo = new ReportRecordVo();
            vo.setOrderNo(history.getOrderNo());
            vo.setProcessName(history.getProcessName());
            vo.setReportTime(history.getReportTime());
            if (history.getPersonId() != null) {
                vo.setPersonName(history.getPersonId().getName());
            }
            // 设置报工类型名称
            if (LichengConstants.REPORTYPE0001.equals(history.getRecordTypeBg())) {
                vo.setRecordTypeBgName("正常");
            } else if (LichengConstants.REPORTYPE0002.equals(history.getRecordTypeBg())) {
                vo.setRecordTypeBgName("尾料");
            } else {
                vo.setRecordTypeBgName(history.getRecordTypeBg());
            }
            vo.setMaterialName(history.getMaterialName());
            vo.setMaterialNumber(history.getMaterialNumber());
            vo.setPotNumber(history.getPotNumber()); // 使用potNumber字段
            vo.setRecordQty(history.getRecordQty());
            vo.setRecordUnit(history.getRecordUnit());
            // 设置班别名称
            if (history.getClassId() != null) {
                vo.setClassName(history.getClassId().getName());
            }
            voList.add(vo);
        }
        
        return voList;
    }
}