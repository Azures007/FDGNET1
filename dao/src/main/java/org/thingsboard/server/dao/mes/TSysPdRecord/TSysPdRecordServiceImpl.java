package org.thingsboard.server.dao.mes.TSysPdRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.server.dao.sql.mes.ncWorkline.NcWorklineRepository;
import org.thingsboard.server.common.data.mes.ncWorkline.NcWorkline;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecord;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecordSplit;
import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;
import org.thingsboard.server.dao.mes.dto.TSysPdRecordDto;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordRepository;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordSplitRepository;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.mes.vo.TSysPdRecordVo;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialRepository;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.dao.sql.mes.order.NcSyncLogRepository;
import org.thingsboard.server.common.data.mes.sys.NcSyncLog;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
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

    @Autowired
    private SyncMaterialRepository syncMaterialRepository;

    @Autowired
    private NcWorklineRepository ncWorklineRepository;

    @Value("${nc.base-url:http://172.88.0.150:8077}")
    private String inventoryBaseUrl;

    @Value("${nc.app-id:yc9t8188f4e0j2ce13}")
    private String ncAppId;

    @Value("${nc.app-secret:e6eed684852d619a5292c4753628ed56}")
    private String ncAppSecret;

    private static final String INVENTORY_SUBMIT_PATH = "/api/ycnc/mes/mm/inventory/data/submit";
    private static final String GET_TOKEN_PATH = "/api/ycnc/mes/config/gettoken";
    private static final String SYNC_TYPE_PD_SUBMIT = "盘点提交";

    private RestTemplate restTemplate = new RestTemplate();
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    // Token缓存
    private String cachedToken;
    private long tokenExpireTime;

    @Autowired
    private NcSyncLogRepository ncSyncLogRepository;
    /**
     * 盘点记录列表
     * @param currentUserId
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysPdRecordDto
     * @return
     */
    @Override
    public Page<TSysPdRecordVo> tSysPdRecordList(String currentUserId, List<String> userCwkids, Integer current, Integer size, String sortField, String sortOrder, TSysPdRecordDto tSysPdRecordDto) {
        String pkOrg = userService.getUserCurrentPkOrg(currentUserId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrg(currentUserId, pkOrg);

        // 1. 解析产线 ID 为产线名称 (nc_vwkname)
        String filterVwkName = null;
        List<String> filterVwkNames = null;
        if (tSysPdRecordDto.getCwkid() != null && !tSysPdRecordDto.getCwkid().isEmpty()) {
            NcWorkline workline = ncWorklineRepository.findByCwkid(tSysPdRecordDto.getCwkid());
            if (workline != null) {
                filterVwkName = workline.getVwkname();
            } else {
                filterVwkName = "___invalid_cwkid___";
            }
        } else {
            // 如果未指定产线，则使用用户绑定的产线列表进行过滤
            if (userCwkids != null && !userCwkids.isEmpty()) {
                filterVwkNames = ncWorklineRepository.findByCwkids(userCwkids).stream()
                        .map(NcWorkline::getVwkname)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            if (filterVwkNames == null) {
                filterVwkNames = new ArrayList<>();
            }
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "createdTime");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        Pageable pageable = PageRequest.of(current, size, sort);
        final String finalFilterVwkName = filterVwkName;
        final List<String> finalFilterVwkNames = filterVwkNames;
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
            // 2. 使用 nc_vwkname 进行查询
            if (finalFilterVwkName != null) {
                predicates.add(cb.equal(root.get("ncVwkname"), finalFilterVwkName));
            } else if (finalFilterVwkNames != null && !finalFilterVwkNames.isEmpty()) {
                predicates.add(root.get("ncVwkname").in(finalFilterVwkNames));
            } else {
                // 既没有选择产线，该用户也没有权限产线，则不应查出任何数据
                predicates.add(cb.disjunction());
            }
            if (ncWarehouses != null && !ncWarehouses.isEmpty()) {
                List<String> warehouseIds = ncWarehouses.stream().map(NcWarehouse::getPkStordoc).distinct().collect(Collectors.toList());
                predicates.add(root.get("pdWorkshopNcId").in(warehouseIds));
            }
            predicates.add(cb.equal(root.get("byDeleted"), "0"));
            // 添加过滤条件：不查询pd_type为2的数据
            predicates.add(cb.notEqual(root.get("pdType"), "2"));
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
     * @param currentUserId
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysPdRecordDto
     * @return
     */
    @Override
    public Page<TSysPdRecordVo> tSysPdRecordListWithSplit(String currentUserId, List<String> userCwkids, Integer current, Integer size, String sortField, String sortOrder, TSysPdRecordDto tSysPdRecordDto) {
        String pkOrg = userService.getUserCurrentPkOrg(currentUserId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrg(currentUserId, pkOrg);

        // 1. 解析产线 ID 为产线名称 (nc_vwkname)
        String filterVwkName = null;
        List<String> filterVwkNames = null;
        if (tSysPdRecordDto.getCwkid() != null && !tSysPdRecordDto.getCwkid().isEmpty()) {
            NcWorkline workline = ncWorklineRepository.findByCwkid(tSysPdRecordDto.getCwkid());
            if (workline != null) {
                filterVwkName = workline.getVwkname();
            } else {
                filterVwkName = "___invalid_cwkid___";
            }
        } else {
            // 如果未指定产线，则使用用户绑定的产线列表进行过滤
            if (userCwkids != null && !userCwkids.isEmpty()) {
                filterVwkNames = ncWorklineRepository.findByCwkids(userCwkids).stream()
                        .map(NcWorkline::getVwkname)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            if (filterVwkNames == null) {
                filterVwkNames = new ArrayList<>();
            }
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "createdTime");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        Pageable pageable = PageRequest.of(current, size, sort);
        final String finalFilterVwkName = filterVwkName;
        final List<String> finalFilterVwkNames = filterVwkNames;
        // 构建基础查询条件
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
            
            // 物料名称筛选 (主表或子表匹配)
            if (tSysPdRecordDto.getMaterialName() != null && !tSysPdRecordDto.getMaterialName().isEmpty()) {
                String pattern = "%" + tSysPdRecordDto.getMaterialName() + "%";
                Predicate nameInMain = cb.like(root.get("materialName"), pattern);
                
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<TSysPdRecordSplit> splitRoot = subquery.from(TSysPdRecordSplit.class);
                subquery.select(splitRoot.get("rePdRecordId"));
                subquery.where(cb.and(
                        cb.like(splitRoot.get("materialName"), pattern),
                        cb.equal(splitRoot.get("byDeleted"), "0")
                ));
                
                Predicate nameInSplit = root.get("pdRecordId").in(subquery);
                predicates.add(cb.or(nameInMain, nameInSplit));
            }

            // 2. 使用 nc_vwkname 进行查询
            if (finalFilterVwkName != null) {
                predicates.add(cb.equal(root.get("ncVwkname"), finalFilterVwkName));
            } else if (finalFilterVwkNames != null && !finalFilterVwkNames.isEmpty()) {
                predicates.add(root.get("ncVwkname").in(finalFilterVwkNames));
            } else {
                // 既没有选择产线，该用户也没有权限产线，则不应查出任何数据
                predicates.add(cb.disjunction());
            }
            if (ncWarehouses != null && !ncWarehouses.isEmpty()) {
                List<String> warehouseIds = ncWarehouses.stream().map(NcWarehouse::getPkStordoc).distinct().collect(Collectors.toList());
                predicates.add(root.get("pdWorkshopNcId").in(warehouseIds));
            }
            predicates.add(cb.equal(root.get("byDeleted"), "0"));
            // 添加过滤条件：不查询pd_type为2的数据
            predicates.add(cb.notEqual(root.get("pdType"), "2"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<TSysPdRecord> pdRecordPage = tSysPdRecordRepository.findAll(baseSpecification, pageable);
        
        List<Integer> recordIds = pdRecordPage.getContent().stream()
                .map(TSysPdRecord::getPdRecordId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        // 查询所有相关的拆分记录
        Map<Integer, List<TSysPdRecordSplit>> splitRecordsGroupedByPdId = new HashMap<>();
        if (!recordIds.isEmpty()) {
            List<TSysPdRecordSplit> allSplitRecords = tSysPdRecordSplitRepository.findByRePdRecordIdIn(recordIds);
            
            // 如果有物料名称筛选条件，也需要筛选拆分记录
            if (tSysPdRecordDto.getMaterialName() != null && !tSysPdRecordDto.getMaterialName().isEmpty()) {
                allSplitRecords = allSplitRecords.stream()
                        .filter(split -> split.getMaterialName() != null && 
                                split.getMaterialName().contains(tSysPdRecordDto.getMaterialName()))
                        .collect(Collectors.toList());
            }
            
            splitRecordsGroupedByPdId = allSplitRecords.stream()
                    .collect(Collectors.groupingBy(TSysPdRecordSplit::getRePdRecordId));
        }

        // 构建返回结果
        List<TSysPdRecordVo> voList = new ArrayList<>();
        for (TSysPdRecord record : pdRecordPage.getContent()) {
            Integer recordPdId = record.getPdRecordId();
            
            // 如果该主记录有匹配的拆分记录，显示拆分记录
            if (recordPdId != null && splitRecordsGroupedByPdId.containsKey(recordPdId)) {
                List<TSysPdRecordSplit> splitRecords = splitRecordsGroupedByPdId.get(recordPdId);
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
                    vo.setPdWorkshopNcId(splitRecord.getPdWorkshopNcId());
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
                    vo.setNcVwkname(splitRecord.getNcVwkname());
                    vo.setReviewStatus(splitRecord.getReviewStatus());
                    voList.add(vo);
                }
            }
            // 否则显示主表记录 (如果它符合条件)
            else {
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
                    vo.setReviewStatus(record.getReviewStatus());
                    voList.add(vo);
                }
            }
        }

        return new PageImpl<>(voList, pageable, pdRecordPage.getTotalElements());
    }
    
    /**
     * 审核盘点记录
     * @param ids 盘点记录ID列表
     * @return 更新记录数
     */
    @Override
    @Transactional
    public int reviewPdRecords(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        List<TSysPdRecord> records = tSysPdRecordRepository.findAllById(ids);
        // 校验是否已审核，禁止重复审核
        List<Integer> reviewedIds = records.stream()
                .filter(r -> "1".equals(r.getReviewStatus()))
                .map(TSysPdRecord::getPdRecordId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!reviewedIds.isEmpty()) {
            throw new RuntimeException("存在已审核的盘点记录，禁止重复审核，记录ID：" + reviewedIds);
        }

        for (TSysPdRecord record : records) {
            record.setReviewStatus("1");
        }
        List<TSysPdRecord> savedRecords = tSysPdRecordRepository.saveAll(records);
        
        // 同时更新对应的还原盘点记录的审核状态
        List<TSysPdRecordSplit> splitRecords = tSysPdRecordSplitRepository.findByRePdRecordIdIn(ids);
        for (TSysPdRecordSplit splitRecord : splitRecords) {
            splitRecord.setReviewStatus("1");
        }
        tSysPdRecordSplitRepository.saveAll(splitRecords);
        
        // 找出主表记录中不存在于拆分记录的主记录ID集合
        Set<Integer> splitRecordMainIds = splitRecords.stream()
                .map(TSysPdRecordSplit::getRePdRecordId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        // 筛选出主表记录中不存在于拆分记录的主记录
        List<TSysPdRecord> mainRecordsWithoutSplit = savedRecords.stream()
                .filter(record -> record.getPdRecordId() != null && !splitRecordMainIds.contains(record.getPdRecordId()))
                .collect(Collectors.toList());
        
        // 审核后调用外部接口提交盘点数据（包括拆分记录和没有拆分记录的主表记录）
        if (!splitRecords.isEmpty() || !mainRecordsWithoutSplit.isEmpty()) {
            try {
                submitInventoryData(splitRecords, mainRecordsWithoutSplit);
            } catch (Exception e) {
                log.error("提交盘点数据到外部接口失败", e);
                throw new RuntimeException("提交盘点数据到NC接口失败"+e.getMessage());
            }
        }
        
        return savedRecords.size();
    }

    /**
     * 提交盘点数据到外部接口
     * @param splitRecords 拆分记录列表
     * @param mainRecords 主表记录列表（不存在于拆分记录的主记录）
     */
    private void submitInventoryData(List<TSysPdRecordSplit> splitRecords, List<TSysPdRecord> mainRecords) {
        // 仅提交未生成盘点单号的拆分记录，并按仓库分组
        Map<String, List<TSysPdRecordSplit>> splitRecordsByWarehouse = splitRecords.stream()
                .filter(split -> (split.getNcOrderNo() == null || split.getNcOrderNo().isEmpty()))
                .filter(split -> split.getPdWorkshopNcId() != null && !split.getPdWorkshopNcId().isEmpty())
                .collect(Collectors.groupingBy(TSysPdRecordSplit::getPdWorkshopNcId));
        
        // 按仓库分组主表记录
        Map<String, List<TSysPdRecord>> mainRecordsByWarehouse = mainRecords.stream()
                .filter(record -> record.getPdWorkshopNcId() != null && !record.getPdWorkshopNcId().isEmpty())
                .collect(Collectors.groupingBy(TSysPdRecord::getPdWorkshopNcId));
        
        // 合并所有仓库ID
        Set<String> allWarehouseIds = new HashSet<>();
        allWarehouseIds.addAll(splitRecordsByWarehouse.keySet());
        allWarehouseIds.addAll(mainRecordsByWarehouse.keySet());
        
        // 为每个仓库构建请求并调用接口
        for (String pkStordoc : allWarehouseIds) {
            List<TSysPdRecordSplit> warehouseSplitRecords = splitRecordsByWarehouse.getOrDefault(pkStordoc, new ArrayList<>());
            List<TSysPdRecord> warehouseMainRecords = mainRecordsByWarehouse.getOrDefault(pkStordoc, new ArrayList<>());

            // 构建产品列表
            List<Map<String, Object>> productList = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            String requestJson = "";
            
            // 处理拆分记录
            for (TSysPdRecordSplit split : warehouseSplitRecords) {
                // 根据物料编码查找物料的ncMaterialId（pk_material）
                String pkMaterial = null;
                if (split.getMaterialNumber() != null && !split.getMaterialNumber().isEmpty()) {
                    List<TSyncMaterial> materials = syncMaterialRepository.getByMaterialCode(split.getMaterialNumber());
                    if (materials != null && !materials.isEmpty()) {
                        TSyncMaterial material = materials.get(0);
                        pkMaterial = material.getNcMaterialId();
                    }
                }

                // 如果找不到pk_material，跳过该记录或使用默认值
                if (pkMaterial == null || pkMaterial.isEmpty()) {
                    log.warn("未找到物料编码 {} 对应的pk_material，跳过该记录", split.getMaterialNumber());
                    continue;
                }

                Map<String, Object> product = new HashMap<>();
                product.put("pk_material", pkMaterial);
                product.put("name", split.getMaterialName() != null ? split.getMaterialName() : "");
                product.put("code", split.getMaterialNumber() != null ? split.getMaterialNumber() : "");
                // 数量转换为字符串
                String number = split.getPdQty() != null ? String.valueOf(split.getPdQty()) : "0";
                product.put("number", number);
                productList.add(product);
            }
            
            // 处理主表记录
            for (TSysPdRecord record : warehouseMainRecords) {
                // 根据物料编码查找物料的ncMaterialId（pk_material）
                String pkMaterial = null;
                if (record.getMaterialNumber() != null && !record.getMaterialNumber().isEmpty()) {
                    List<TSyncMaterial> materials = syncMaterialRepository.getByMaterialCode(record.getMaterialNumber());
                    if (materials != null && !materials.isEmpty()) {
                        TSyncMaterial material = materials.get(0);
                        pkMaterial = material.getNcMaterialId();
                    }
                }

                // 如果找不到pk_material，跳过该记录
                if (pkMaterial == null || pkMaterial.isEmpty()) {
                    log.warn("未找到物料编码 {} 对应的pk_material，跳过该记录", record.getMaterialNumber());
                    continue;
                }

                Map<String, Object> product = new HashMap<>();
                product.put("pk_material", pkMaterial);
                product.put("name", record.getMaterialName() != null ? record.getMaterialName() : "");
                product.put("code", record.getMaterialNumber() != null ? record.getMaterialNumber() : "");
                // 数量转换为字符串
                String number = record.getPdQty() != null ? String.valueOf(record.getPdQty()) : "0";
                product.put("number", number);
                productList.add(product);
            }

            // 如果产品列表为空，跳过该仓库
            if (productList.isEmpty()) {
                log.warn("仓库 {} 没有有效的产品数据，跳过提交", pkStordoc);
                continue;
            }

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("pk_stordoc", pkStordoc);
            requestBody.put("productList", productList);

            try {
                try {
                    requestJson = objectMapper.writeValueAsString(requestBody);
                } catch (Exception ex) {
                    log.warn("序列化盘点提交请求体失败", ex);
                    requestJson = "序列化失败: " + ex.getMessage();
                }

                // 获取token
                String token = getToken();
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("token", token);
                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                // 调用接口
                String submitUrl = inventoryBaseUrl + INVENTORY_SUBMIT_PATH;
                ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                        submitUrl,
                        requestEntity,
                        JsonNode.class
                );

                // 处理响应
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode responseBody = response.getBody();
                    int code = responseBody.has("code") ? response.getBody().get("code").asInt() : -1;
                    String msg = responseBody.has("msg") ? responseBody.get("msg").asText() : "";
                    
                    if (code == 1) {
                        String orderno = responseBody.has("data") && responseBody.get("data").has("orderno") 
                                ? responseBody.get("data").get("orderno").asText() : "";
                        log.info("盘点数据提交成功，仓库: {}, ERP盘点单号: {}", pkStordoc, orderno);
                        
                        // 更新该仓库所有记录的盘点单号
                        if (orderno != null && !orderno.isEmpty()) {
                            // 更新拆分记录的盘点单号
                            if (!warehouseSplitRecords.isEmpty()) {
                                for (TSysPdRecordSplit split : warehouseSplitRecords) {
                                    split.setNcOrderNo(orderno);
                                }
                                tSysPdRecordSplitRepository.saveAll(warehouseSplitRecords);
                            }
                            // 注意：主表记录没有ncOrderNo字段，如果需要保存盘点单号，需要添加字段
                            log.info("已更新仓库 {} 的 {} 条拆分记录的盘点单号为: {}", pkStordoc, warehouseSplitRecords.size(), orderno);
                        }
                        saveNcSyncLog(SYNC_TYPE_PD_SUBMIT, "0",
                                String.format("仓库:%s 提交成功, ERP单号:%s", pkStordoc, orderno),
                                requestJson, productList.size(), System.currentTimeMillis() - startTime, null);
                    } else {
                        String warnMsg = String.format("盘点数据提交失败，仓库: %s, 错误信息: %s", pkStordoc, msg);
                        log.warn(warnMsg);
                        saveNcSyncLog(SYNC_TYPE_PD_SUBMIT, "1",
                                String.format("仓库:%s 提交失败", pkStordoc),
                                requestJson, productList.size(), System.currentTimeMillis() - startTime, warnMsg);
                        throw new RuntimeException(warnMsg);
                    }
                } else {
                    String warnMsg = String.format("盘点数据提交失败，仓库: %s, HTTP状态码: %s", pkStordoc, response.getStatusCode());
                    log.warn(warnMsg);
                    saveNcSyncLog(SYNC_TYPE_PD_SUBMIT, "1",
                            String.format("仓库:%s 提交失败", pkStordoc),
                            requestJson, productList.size(), System.currentTimeMillis() - startTime, warnMsg);
                    throw new RuntimeException(warnMsg);
                }
            } catch (Exception e) {
                log.error("调用盘点数据提交接口异常，仓库: {}", pkStordoc, e);
                saveNcSyncLog(SYNC_TYPE_PD_SUBMIT, "1",
                        String.format("仓库:%s 提交异常", pkStordoc),
                        requestJson, productList.size(), System.currentTimeMillis() - startTime, e.getMessage());
                throw new RuntimeException("调用盘点数据提交接口异常，仓库: " + pkStordoc + "，原因: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 保存NC同步日志
     */
    private void saveNcSyncLog(String syncType, String syncStatus, String syncContent,
                               String requestJson, Integer dataCount, long durationMs, String errorMessage) {
        try {
            NcSyncLog logEntity = new NcSyncLog();
            logEntity.setSyncType(syncType);
            logEntity.setSyncTime(new Date());
            logEntity.setSyncStatus(syncStatus);
            logEntity.setSyncContent(syncContent);
            logEntity.setRequestJson(requestJson);
            logEntity.setDataCount(dataCount);
            logEntity.setDurationMs(durationMs);
            logEntity.setErrorMessage(errorMessage);
            ncSyncLogRepository.save(logEntity);
        } catch (Exception e) {
            log.error("保存NC同步日志失败", e);
        }
    }

    /**
     * 获取NC接口token（带缓存机制）
     * @return token
     */
    private String getToken() {
        // 检查缓存的token是否有效
        if (cachedToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return cachedToken;
        }

        try {
            // 构建获取token的请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("appId", ncAppId);
            requestBody.put("appSecret", ncAppSecret);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 调用获取token接口
            String tokenUrl = inventoryBaseUrl + GET_TOKEN_PATH;
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    tokenUrl,
                    requestEntity,
                    JsonNode.class
            );

            // 处理响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode responseBody = response.getBody();
                int code = responseBody.has("code") ? responseBody.get("code").asInt() : -1;
                String msg = responseBody.has("msg") ? responseBody.get("msg").asText() : "";

                if (code == 1 && responseBody.has("data")) {
                    JsonNode data = responseBody.get("data");
                    if (data.has("token")) {
                        String token = data.get("token").asText();
                        // 获取有效期（秒），默认7200秒，提前5分钟过期
                        int expiresIn = data.has("expires_in") ? data.get("expires_in").asInt() : 7200;
                        // 计算过期时间（提前5分钟过期，避免边界情况）
                        tokenExpireTime = System.currentTimeMillis() + (expiresIn - 300) * 1000L;
                        cachedToken = token;
                        log.info("成功获取NC接口token，有效期: {}秒", expiresIn);
                        return token;
                    }
                }
                String errorMsg = String.format("获取NC接口token失败，错误信息: %s", msg);
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            } else {
                String errorMsg = String.format("获取NC接口token失败，HTTP状态码: %s", response.getStatusCode());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        } catch (Exception e) {
            log.error("调用获取NC接口token异常", e);
            throw new RuntimeException("调用获取NC接口token异常: " + e.getMessage(), e);
        }
    }
}