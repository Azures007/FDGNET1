package org.thingsboard.server.dao.TSysPdRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.TSysPdRecord;
import org.thingsboard.server.common.data.TSysPdRecordSplit;
import org.thingsboard.server.dao.dto.TSysPdRecordDto;
import org.thingsboard.server.dao.sql.pd.TSysPdRecordRepository;
import org.thingsboard.server.dao.sql.pd.TSysPdRecordSplitRepository;
import org.thingsboard.server.dao.vo.TSysPdRecordVo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    private TSysPdRecordRepository tSysPdRecordRepository;

    @Autowired
    private TSysPdRecordSplitRepository tSysPdRecordSplitRepository;
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
        Sort sort = Sort.by(Sort.Direction.ASC, "createdTime");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        Pageable pageable = PageRequest.of(current, size, sort);
        Specification<TSysPdRecord> specification = (Root<TSysPdRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (tSysPdRecordDto.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pdTime"), tSysPdRecordDto.getStartTime()));
            }
            if (tSysPdRecordDto.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pdTime"), tSysPdRecordDto.getEndTime()));
            }
            if (tSysPdRecordDto.getPdWorkshopName() != null && !tSysPdRecordDto.getPdWorkshopName().isEmpty()) {
                predicates.add(cb.like(root.get("pdWorkshopName"), "%" + tSysPdRecordDto.getPdWorkshopName() + "%"));
            }
            predicates.add(cb.equal(root.get("byDeleted"), "0"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<TSysPdRecord> pdRecordPage = tSysPdRecordRepository.findAll(specification, pageable);
        List<TSysPdRecordVo> voList = new ArrayList<>();
        for (TSysPdRecord record : pdRecordPage.getContent()) {
            TSysPdRecordVo vo = new TSysPdRecordVo();
            BeanUtils.copyProperties(record, vo);
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

    /**
     * 盘点记录列表（含复盘）
     * @param toString
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysPdRecordDto
     * @return
     */
    @Override
    public Page<TSysPdRecordVo> tSysPdRecordListWithSplit(String toString, Integer current, Integer size, String sortField, String sortOrder, TSysPdRecordDto tSysPdRecordDto) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdTime");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        Pageable pageable = PageRequest.of(current, size, sort);
        Specification<TSysPdRecord> specification = (Root<TSysPdRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (tSysPdRecordDto.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pdTime"), tSysPdRecordDto.getStartTime()));
            }
            if (tSysPdRecordDto.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pdTime"), tSysPdRecordDto.getEndTime()));
            }
            if (tSysPdRecordDto.getPdWorkshopName() != null && !tSysPdRecordDto.getPdWorkshopName().isEmpty()) {
                predicates.add(cb.like(root.get("pdWorkshopName"), "%" + tSysPdRecordDto.getPdWorkshopName() + "%"));
            }
            predicates.add(cb.equal(root.get("byDeleted"), "0"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<TSysPdRecord> pdRecordPage = tSysPdRecordRepository.findAll(specification, pageable);
        List<Integer> recordIds = pdRecordPage.getContent().stream()
                .map(TSysPdRecord::getRePdRecordId)
                .filter(Objects::nonNull) // 过滤掉null值
                .collect(Collectors.toList());
        List<TSysPdRecordSplit> allSplitRecords = new ArrayList<>();
        if (!recordIds.isEmpty()) {
            allSplitRecords = tSysPdRecordSplitRepository.findByRePdRecordIdIn(recordIds);
        }
        Map<Integer, List<TSysPdRecordSplit>> splitRecordsGroupedByRePdId = allSplitRecords.stream()
                .collect(Collectors.groupingBy(
                        TSysPdRecordSplit::getRePdRecordId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        List<TSysPdRecordVo> voList = new ArrayList<>();
        for (TSysPdRecord record : pdRecordPage.getContent()) {
            Integer recordRePdId = record.getRePdRecordId();
            if (recordRePdId != null && splitRecordsGroupedByRePdId.containsKey(recordRePdId)) {
                List<TSysPdRecordSplit> splitRecords = splitRecordsGroupedByRePdId.get(recordRePdId);
                for (TSysPdRecordSplit splitRecord : splitRecords) {
                    TSysPdRecordVo vo = new TSysPdRecordVo();
                    vo.setPdRecordId(splitRecord.getPdRecordSplitId());
                    vo.setPdTime(splitRecord.getPdTime() != null ? formatTimestampToString((Timestamp) splitRecord.getPdTime()) : null);
                    vo.setMaterialNumber(splitRecord.getMaterialNumber());
                    vo.setMaterialName(splitRecord.getMaterialName());
                    vo.setMaterialSpecifications(splitRecord.getMaterialSpecifications());
                    vo.setPdUnit(splitRecord.getPdUnit());
                    vo.setPdUnitStr(splitRecord.getPdUnitStr());
                    vo.setPdQty(splitRecord.getPdQty());
                    vo.setPdCreatedName(splitRecord.getPdCreatedName());
                    vo.setPdCreatedId(splitRecord.getPdCreatedId());
                    vo.setPdWorkshopName(splitRecord.getPdWorkshopName());
                    vo.setPdWorkshopNumber(splitRecord.getPdWorkshopNumber());
                    vo.setPdWorkshopLeaderName(splitRecord.getPdWorkshopLeaderName());
                    vo.setPdWorkshopLeaderId(splitRecord.getPdWorkshopLeaderId());
                    vo.setPdClassName(splitRecord.getPdClassName());
                    vo.setPdClassNumber(splitRecord.getPdClassNumber());
                    vo.setByDeleted(splitRecord.getByDeleted());
                    vo.setCreatedTime(splitRecord.getCreatedTime() != null ? formatTimestampToString((Timestamp) splitRecord.getCreatedTime()) : null);
                    vo.setCreatedName(splitRecord.getCreatedName());
                    vo.setByFp(splitRecord.getByFp());
                    vo.setPdType(splitRecord.getPdType());
                    vo.setPdBr(splitRecord.getPdBr());
                    vo.setRePdRecordId(splitRecord.getRePdRecordId());
                    vo.setPdTimeStr(splitRecord.getPdTimeStr());
                    vo.setIsReturn("是");
                    voList.add(vo);
                }
            } else {
                TSysPdRecordVo vo = new TSysPdRecordVo();
                BeanUtils.copyProperties(record, vo);
                if (record.getPdTime() != null) {
                    vo.setPdTime(formatTimestampToString((Timestamp) record.getPdTime()));
                }
                if (record.getCreatedTime() != null) {
                    vo.setCreatedTime(formatTimestampToString((Timestamp) record.getCreatedTime()));
                }
                vo.setIsReturn("否");
                voList.add(vo);
            }
        }
        return new PageImpl<>(voList, pageable, pdRecordPage.getTotalElements());
    }


}
