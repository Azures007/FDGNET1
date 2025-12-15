package org.thingsboard.server.dao.mes.TSysPdRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SyncMaterialRepository syncMaterialRepository;

    @Value("${nc.base-url:http://172.88.0.150:8077}")
    private String inventoryBaseUrl;

    @Value("${nc.app-id:yc9t8188f4e0j2ce13}")
    private String ncAppId;

    @Value("${nc.app-secret:e6eed684852d619a5292c4753628ed56}")
    private String ncAppSecret;

    private static final String INVENTORY_SUBMIT_PATH = "/api/ycnc/mes/mm/inventory/data/submit";
    private static final String GET_TOKEN_PATH = "/api/ycnc/mes/config/gettoken";

    private RestTemplate restTemplate = new RestTemplate();
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    // Token缓存
    private String cachedToken;
    private long tokenExpireTime;
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
        //List<String> cwkids =userService.getUserCurrentCwkid(userId);
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrg(userId,pkOrg);

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
        //List<String> cwkids = userService.getUserCurrentCwkid(userId);
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrg(userId, pkOrg);
        Sort sort = Sort.by(Sort.Direction.ASC, "createdTime");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        Pageable pageable = PageRequest.of(current, size, sort);
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
                .filter(record -> !"1".equals(record.getByDeleted()))
                .map(TSysPdRecord::getPdRecordId)
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
                    .filter(record -> !"1".equals(record.getByDeleted()))
                    .map(TSysPdRecord::getPdRecordId)
                    .filter(Objects::nonNull)
                    .forEach(finalRecordIdsToShow::add);
            // 添加有匹配拆分记录的主表记录ID
            finalRecordIdsToShow.addAll(mainRecordIdsWithMatchingSplits);
            // 重新过滤主表记录，只保留需要显示的记录
            List<TSysPdRecord> finalMainRecords = pdRecordPage.getContent().stream()
                    .filter(record -> {
                        if ("1".equals(record.getByDeleted())) {
                            return false;
                        }
                        Integer pdRecordId = record.getPdRecordId();
                        // 保留没有关联ID的记录或者在需要显示集合中的记录
                        return pdRecordId == null || finalRecordIdsToShow.contains(pdRecordId);
                    })
                    .collect(Collectors.toList());
            // 更新主表记录列表
            pdRecordPage = new PageImpl<>(finalMainRecords, pageable, finalMainRecords.size());
        }
        // 如果没有物料名称筛选条件，但仍需要过滤掉by_deleted为"1"的记录
        else {
            List<TSysPdRecord> filteredRecords = pdRecordPage.getContent().stream()
                    .filter(record -> !"1".equals(record.getByDeleted()))
                    .collect(Collectors.toList());
            pdRecordPage = new PageImpl<>(filteredRecords, pageable, filteredRecords.size());
        }
        // 按主表记录ID分组拆分记录
        Map<Integer, List<TSysPdRecordSplit>> splitRecordsGroupedByPdId = filteredSplitRecords.stream()
                .collect(Collectors.groupingBy(
                        TSysPdRecordSplit::getRePdRecordId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        // 构建返回结果
        List<TSysPdRecordVo> voList = new ArrayList<>();
        for (TSysPdRecord record : pdRecordPage.getContent()) {
            // 如果by_deleted为"1"，则跳过不处理
            if ("1".equals(record.getByDeleted())) {
                continue;
            }
            Integer recordPdId = record.getPdRecordId();
            // 如果该主记录有匹配的拆分记录
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
            // 显示主表记录
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
                    vo.setReviewStatus(record.getReviewStatus());
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
        // 按仓库分组拆分记录
        Map<String, List<TSysPdRecordSplit>> splitRecordsByWarehouse = splitRecords.stream()
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
                    } else {
                        String warnMsg = String.format("盘点数据提交失败，仓库: %s, 错误信息: %s", pkStordoc, msg);
                        log.warn(warnMsg);
                        throw new RuntimeException(warnMsg);
                    }
                } else {
                    String warnMsg = String.format("盘点数据提交失败，仓库: %s, HTTP状态码: %s", pkStordoc, response.getStatusCode());
                    log.warn(warnMsg);
                    throw new RuntimeException(warnMsg);
                }
            } catch (Exception e) {
                log.error("调用盘点数据提交接口异常，仓库: {}", pkStordoc, e);
                throw new RuntimeException("调用盘点数据提交接口异常，仓库: " + pkStordoc + "，原因: " + e.getMessage(), e);
            }
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