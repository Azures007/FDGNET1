package org.thingsboard.server.dao.TSysPdRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.TSysPdRecord;
import org.thingsboard.server.dao.dto.TSysPdRecordDto;
import org.thingsboard.server.dao.sql.TSysPdRecord.tSysPdRecordRepository;
import org.thingsboard.server.dao.vo.TSysPdRecordVo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 许文言
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/30 10:22:03
 */

@Service
@Slf4j
public class TSysPdRecordServiceImpl implements TSysPdRecordService {


    @Autowired
    private org.thingsboard.server.dao.sql.TSysPdRecord.tSysPdRecordRepository tSysPdRecordRepository;
    /**
     * 盘点记录列表
     * @param toString
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysPdRecordDto
     * @return
     */
    @Override
    public Page<TSysPdRecordVo> tSysPdRecordList(String toString, Integer current, Integer size, String sortField, String sortOrder, TSysPdRecordDto tSysPdRecordDto) {
        // 创建排序规则，默认按照创建时间升序排序
        Sort sort = Sort.by(Sort.Direction.ASC, "createdTime");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        // 创建分页请求
        Pageable pageable = PageRequest.of(current, size, sort);
        // 构建动态查询条件
        Specification<TSysPdRecord> specification = (Root<TSysPdRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 时间范围查询
            if (tSysPdRecordDto.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pdTime"), tSysPdRecordDto.getStartTime()));
            }
            if (tSysPdRecordDto.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pdTime"), tSysPdRecordDto.getEndTime()));
            }
            // 车间名称模糊查询
            if (tSysPdRecordDto.getPdWorkshopName() != null && !tSysPdRecordDto.getPdWorkshopName().isEmpty()) {
                predicates.add(cb.like(root.get("pdWorkshopName"), "%" + tSysPdRecordDto.getPdWorkshopName() + "%"));
            }
            // 只查询未删除的记录
            predicates.add(cb.equal(root.get("byDeleted"), "0"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        // 查询数据
        Page<TSysPdRecord> pdRecordPage = tSysPdRecordRepository.findAll(specification, pageable);
        // 转换为VO对象
        List<TSysPdRecordVo> voList = new ArrayList<>();
        for (TSysPdRecord record : pdRecordPage.getContent()) {
            TSysPdRecordVo vo = new TSysPdRecordVo();
            BeanUtils.copyProperties(record, vo);
            // 处理时间字段格式化
            if (record.getPdTime() != null) {
                vo.setPdTime(formatTimestampToString((Timestamp) record.getPdTime()));
            }
            if (record.getCreatedTime() != null) {
                vo.setCreatedTime(formatTimestampToString((Timestamp) record.getCreatedTime()));
            }
            voList.add(vo);
        }
        return new PageImpl<>(voList, pageable, pdRecordPage.getTotalElements());
    }

    /**
     * 时间戳格式化为字符串
     * @param timestamp
     * @return
     */
    private String formatTimestampToString(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp.getTime()));
    }
}
