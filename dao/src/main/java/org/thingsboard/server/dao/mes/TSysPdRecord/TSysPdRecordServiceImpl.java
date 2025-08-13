package org.thingsboard.server.dao.mes.TSysPdRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecord;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecordSplit;
import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;
import org.thingsboard.server.dao.mes.dto.TSysPdRecordDto;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordRepository;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordSplitRepository;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.mes.vo.TSysPdRecordVo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
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

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;
    /**
     * 盘点记录列表
     * @param userId
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysPdRecordDto
     * @return
     */
    @Override
    public Page<TSysPdRecordVo> tSysPdRecordList(String userId, Integer current, Integer size, String sortField, String sortOrder, TSysPdRecordDto tSysPdRecordDto) {
        String cwkid =userService.getUserCurrentCwkid(userId);
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrgAndWorkline(userId,pkOrg,cwkid);

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
            if (tSysPdRecordDto.getMaterialName() != null && !tSysPdRecordDto.getMaterialName().isEmpty()) {
                predicates.add(cb.like(root.get("materialName"), "%" + tSysPdRecordDto.getMaterialName() + "%"));
            }
            if(ncWarehouses!=null && !ncWarehouses.isEmpty()){
                List<String> warehouseIds = ncWarehouses.stream().map(NcWarehouse::getPkStordoc).distinct().collect(Collectors.toList());
                predicates.add(root.get("pdWorkshopNumber").in(warehouseIds));
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
     * @param userId
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysPdRecordDto
     * @return
     */
    @Override
    public Page<TSysPdRecordVo> tSysPdRecordListWithSplit(String userId, Integer current, Integer size, String sortField, String sortOrder, TSysPdRecordDto tSysPdRecordDto) {
        String cwkid = userService.getUserCurrentCwkid(userId);
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrgAndWorkline(userId, pkOrg, cwkid);
        Sort sort = Sort.by(Sort.Direction.ASC, "createdTime");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        Pageable pageable = PageRequest.of(current, size, sort);

        // 构建基础查询条件（不包含物料名称条件）
        Specification<TSysPdRecord> baseSpecification = (Root<TSysPdRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
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
            if (ncWarehouses != null && !ncWarehouses.isEmpty()) {
                List<String> warehouseIds = ncWarehouses.stream().map(NcWarehouse::getPkStordoc).distinct().collect(Collectors.toList());
                predicates.add(root.get("pdWorkshopNumber").in(warehouseIds));
            }
            predicates.add(cb.equal(root.get("byDeleted"), "0"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 先查询所有符合条件的主表记录
        Page<TSysPdRecord> pdRecordPage = tSysPdRecordRepository.findAll(baseSpecification, pageable);

        // 获取所有主表记录的ID
        List<Integer> recordIds = pdRecordPage.getContent().stream()
                .map(TSysPdRecord::getRePdRecordId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 查询所有相关的拆分记录
        List<TSysPdRecordSplit> allSplitRecords = new ArrayList<>();
        if (!recordIds.isEmpty()) {
            allSplitRecords = tSysPdRecordSplitRepository.findByRePdRecordIdIn(recordIds);
        }

        // 如果有物料名称筛选条件
        List<TSysPdRecordSplit> filteredSplitRecords = allSplitRecords;
        if (tSysPdRecordDto.getMaterialName() != null && !tSysPdRecordDto.getMaterialName().isEmpty()) {
            // 筛选主表中符合条件的记录
            List<TSysPdRecord> materialFilteredMainRecords = pdRecordPage.getContent().stream()
                    .filter(record -> record.getMaterialName() != null &&
                            record.getMaterialName().contains(tSysPdRecordDto.getMaterialName()))
                    .collect(Collectors.toList());

            // 筛选拆分表中符合条件的记录
            filteredSplitRecords = allSplitRecords.stream()
                    .filter(split -> split.getMaterialName() != null &&
                            split.getMaterialName().contains(tSysPdRecordDto.getMaterialName()))
                    .collect(Collectors.toList());

            // 获取包含符合条件拆分记录的主表记录ID
            Set<Integer> mainRecordIdsWithMatchingSplits = filteredSplitRecords.stream()
                    .map(TSysPdRecordSplit::getRePdRecordId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 合并主表匹配记录和有匹配拆分记录的主表记录
            Set<Integer> finalRecordIdsToShow = new HashSet<>();

            // 添加主表直接匹配的记录ID
            materialFilteredMainRecords.stream()
                    .map(TSysPdRecord::getRePdRecordId)
                    .filter(Objects::nonNull)
                    .forEach(finalRecordIdsToShow::add);

            // 添加有匹配拆分记录的主表记录ID
            finalRecordIdsToShow.addAll(mainRecordIdsWithMatchingSplits);

            // 重新过滤主表记录，只保留需要显示的记录
            List<TSysPdRecord> finalMainRecords = pdRecordPage.getContent().stream()
                    .filter(record -> {
                        Integer rePdRecordId = record.getRePdRecordId();
                        // 保留没有关联ID的记录或者在需要显示集合中的记录
                        return rePdRecordId == null || finalRecordIdsToShow.contains(rePdRecordId);
                    })
                    .collect(Collectors.toList());

            // 更新主表记录列表
            pdRecordPage = new PageImpl<>(finalMainRecords, pageable, finalMainRecords.size());
        }

        // 按主表记录ID分组拆分记录
        Map<Integer, List<TSysPdRecordSplit>> splitRecordsGroupedByRePdId = filteredSplitRecords.stream()
                .collect(Collectors.groupingBy(
                        TSysPdRecordSplit::getRePdRecordId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 构建返回结果
        List<TSysPdRecordVo> voList = new ArrayList<>();
        for (TSysPdRecord record : pdRecordPage.getContent()) {
            Integer recordRePdId = record.getRePdRecordId();

            // 如果该主记录有匹配的拆分记录
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
                    if (splitRecord.getPdQty() != null) {
                        vo.setPdQty(BigDecimal.valueOf(splitRecord.getPdQty()));
                    } else {
                        vo.setPdQty(null);
                    }
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
            }
            // 显示主表记录（如果没有物料筛选或者主表记录本身符合条件）
            else {
                // 如果有物料名称筛选条件，需要检查主表记录是否符合条件
                boolean shouldShowMainRecord = true;
                if (tSysPdRecordDto.getMaterialName() != null && !tSysPdRecordDto.getMaterialName().isEmpty()) {
                    shouldShowMainRecord = record.getMaterialName() != null &&
                            record.getMaterialName().contains(tSysPdRecordDto.getMaterialName());
                }

                if (shouldShowMainRecord) {
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
        }

        // 计算总数
        long totalElements = pdRecordPage.getTotalElements();
        if (tSysPdRecordDto.getMaterialName() != null && !tSysPdRecordDto.getMaterialName().isEmpty()) {
            totalElements = voList.size();
        }

        return new PageImpl<>(voList, pageable, totalElements);
    }
}
