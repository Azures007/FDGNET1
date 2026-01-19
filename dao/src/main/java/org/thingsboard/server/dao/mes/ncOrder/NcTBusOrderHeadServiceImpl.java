package org.thingsboard.server.dao.mes.ncOrder;

import com.youchen.push.service.DomainPushFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.ncOrder.NcTBusOrderHead;
import org.thingsboard.server.common.data.mes.ncOrder.NcTBusOrderPPBom;
import org.thingsboard.server.common.data.mes.sys.NcSyncLog;
import org.thingsboard.server.common.data.mes.sys.TSysCraftInfo;
import org.thingsboard.server.common.data.mes.sys.TSysProcessClassRel;
import org.thingsboard.server.dao.mes.ncWorkline.NcWorklineService;
import org.thingsboard.server.dao.mes.order.OrderBackendService;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftMaterialRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessClassRelRepository;
import org.thingsboard.server.dao.sql.mes.ncOrder.NcTBusOrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.ncOrder.NcTBusOrderPPBomRepository;
import org.thingsboard.server.dao.sql.mes.order.NcSyncLogRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NcTBusOrderHeadServiceImpl implements NcTBusOrderHeadService {
    private static final Logger log = LoggerFactory.getLogger(NcTBusOrderHeadServiceImpl.class);
    @Autowired
    private NcTBusOrderHeadRepository repository;

    @Autowired
    private NcTBusOrderPPBomRepository bomRepository;

    @Autowired
    TSysCraftInfoRepository craftInfoRepository;

    @Autowired
    TSysCraftMaterialRelRepository craftMaterialRelRepository;

    @Autowired
    OrderBackendService orderBackendService;

    @Autowired
    DomainPushFacade domainPushFacade;

    @Autowired
    NcWorklineService ncWorklineService;

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    TSysProcessClassRelRepository processClassRelRepository;

    @Autowired
    private NcSyncLogRepository ncSyncLogRepository;

    private static final String SYNC_TYPE_ORDER = "订单同步";

    @Override
    public List<NcTBusOrderHead> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<NcTBusOrderHead> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public NcTBusOrderHead save(NcTBusOrderHead entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
    @Override
    public NcTBusOrderHead findByCmoid(String cmoid) {
        return repository.findByCmoid(cmoid);
    }

    @Override
    @Transactional
    public NcTBusOrderHead updateByCmoid(String cmoid, NcTBusOrderHead entity) {
        long startTime = System.currentTimeMillis();
        String requestJson = toJsonSafe(entity);
        int dataCount = entity == null ? 0 : 1;
        try {
            String vbillcode = entity.getVbillcode();
//        Integer seq = entity.getSeq();
            String seq = entity.getSeq();
            if (vbillcode == null || seq == null) {
                throw new IllegalArgumentException("vbillcode or seq cannot be null when generating orderNo");
            }
            String orderNo = vbillcode + "-" + seq;
            entity.setOrderNo(orderNo);

            entity.setIsDeleted("0");
            //entity.setCreatedName("system");
            entity.setCreatedTime(new java.util.Date());
            //entity.setUnit("KG");
            String spec=entity.getMaterialspec();
            //Pattern pattern = Pattern.compile("\\*(\\d+(\\.\\d+)?)/");
            Pattern pattern = Pattern.compile("\\*(\\d+(\\.\\d+)?)[^/]*/");
            Matcher matcher = pattern.matcher(spec);
            BigDecimal qtyPerJian=new BigDecimal(0);
            if (matcher.find()) {
                String value = matcher.group(1);
                qtyPerJian=value.matches("\\d+(\\.\\d+)?") ? new BigDecimal(value) : new BigDecimal(0);
                entity.setQtyPerJian(qtyPerJian);
            } else {
                //System.out.printf("规格: %-20s -> 未匹配%n", "\"" + spec + "\"");
            }
            NcTBusOrderHead existingOrder = repository.findByCmoid(cmoid);
            if (existingOrder != null) {
                // 保留原有ID和状态
                Integer orderId = existingOrder.getOrderId();
                entity.setOrderId(orderId);
                entity.setCmoid(cmoid);
                entity.setOrderStatus(existingOrder.getOrderStatus());
                entity.setQtyPerJian(qtyPerJian);
                // 先更新BOM（此时关联关系还在）
                Set<NcTBusOrderPPBom> bomListToUpdate = entity.getBomList();
                if (bomListToUpdate != null && !bomListToUpdate.isEmpty()) {
                    updateBomList(orderId, cmoid, bomListToUpdate);
                }
                
                // 使用原生SQL更新订单基本信息，不触碰BOM关联
                repository.updateOrderBasicInfo(
                        entity.getOrderId(),
                        entity.getOrderNo(),
                        entity.getBillType(),
                        entity.getCpmohid(),
                        entity.getVbillcode(),
                        entity.getCmoid(),
                        entity.getSeq(),
                        entity.getDbilldate(),
                        entity.getCdeptname(),
                        entity.getCdeptid(),
                        entity.getVwkname(),
                        entity.getCwkid(),
                        entity.getPkMaterial(),
                        entity.getCode(),
                        entity.getName(),
                        entity.getMaterialspec(),
                        entity.getNnum(),
                        entity.getTplanstarttime(),
                        entity.getTplanendtime(),
                        entity.getNcReceiveTime(),
                        entity.getNcNote(),
                        entity.getUnit(),
                        entity.getIsDeleted(),
                        entity.getCreatedTime(),
                        entity.getQtyPerJian()
                );
                // 重新查询获取完整的实体（包含BOM）
                entity = repository.findById(orderId).orElse(entity);
            } else {
                entity.setOrderStatus("0");
                entity=repository.save(entity);
            }
            NcTBusOrderHead finalEntity = entity;
            // 注册事务同步回调
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            // 在事务提交后启动新线程
                            new Thread(() -> {
                                //订单自动绑定工艺路线
                                try {
                                    TSysCraftInfo craft=orderBackendService.getCraftInfoByMaterial(finalEntity.getCode());
                                    orderBackendService.startOrder(finalEntity.getOrderId(),craft.getCraftId(),craft.getCraftDetail());
                                } catch (Exception e) {
                                    log.info("订单自动绑定工艺路线失败OrderId："+ finalEntity.getOrderId()+"," + e.getMessage());
                                }
                            }).start();
                        }
                    }
            );


            String syncContent = "同步成功，订单号：" + entity.getOrderNo();
            saveSyncLog(SYNC_TYPE_ORDER, "0", syncContent, requestJson, dataCount, System.currentTimeMillis() - startTime, null);
            return entity;
        } catch (Exception ex) {
            saveSyncLog(SYNC_TYPE_ORDER, "1", "同步失败：" + ex.getMessage(), requestJson, dataCount,
                    System.currentTimeMillis() - startTime, ex.getMessage());
            throw ex;
        }
    }
    @Override
    @Transactional
    public void updateByCmoidBatch(List<NcTBusOrderHead> entitys) {
        long startTime = System.currentTimeMillis();
        String requestJson = toJsonSafe(entitys);
        int dataCount = entitys == null ? 0 : entitys.size();
        try {
            List<NcTBusOrderHead> toSave = new ArrayList<>();
            for (NcTBusOrderHead entity : entitys) {
                String vbillcode = entity.getVbillcode();
//            Integer seq = entity.getSeq();
                String seq = entity.getSeq();
                if (vbillcode == null || seq == null) {
                    throw new IllegalArgumentException("vbillcode or seq cannot be null when generating orderNo");
                }
                String orderNo = vbillcode + "-" + seq;
                entity.setOrderNo(orderNo);
                entity.setIsDeleted("0");
                //entity.setCreatedName("system");
                entity.setCreatedTime(new java.util.Date());
                //entity.setUnit("KG");
                String spec=entity.getMaterialspec();
                Pattern pattern = Pattern.compile("\\*(\\d+(\\.\\d+)?)[^/]*/");
                Matcher matcher = pattern.matcher(spec);
                BigDecimal qtyPerJian=new BigDecimal(0);
                if (matcher.find()) {
                    String value = matcher.group(1);
                    qtyPerJian=value.matches("\\d+(\\.\\d+)?") ? new BigDecimal(value) : new BigDecimal(0);
                    entity.setQtyPerJian(qtyPerJian);
                } else {
                    //System.out.printf("规格: %-20s -> 未匹配%n", "\"" + spec + "\"");
                }
                NcTBusOrderHead existingOrder = repository.findByCmoid(entity.getCmoid());
                if (existingOrder != null) {
                    Integer orderId = existingOrder.getOrderId();
                    entity.setOrderId(orderId);
                    entity.setCmoid(existingOrder.getCmoid());
                    entity.setOrderStatus(existingOrder.getOrderStatus());
                    
                    // 先更新BOM
                    Set<NcTBusOrderPPBom> bomListToUpdate = entity.getBomList();
                    if (bomListToUpdate != null && !bomListToUpdate.isEmpty()) {
                        updateBomList(orderId, entity.getCmoid(), bomListToUpdate);
                    }
                    
                    // 使用原生SQL更新订单基本信息
                    repository.updateOrderBasicInfo(
                            entity.getOrderId(),
                            entity.getOrderNo(),
                            entity.getBillType(),
                            entity.getCpmohid(),
                            entity.getVbillcode(),
                            entity.getCmoid(),
                            entity.getSeq(),
                            entity.getDbilldate(),
                            entity.getCdeptname(),
                            entity.getCdeptid(),
                            entity.getVwkname(),
                            entity.getCwkid(),
                            entity.getPkMaterial(),
                            entity.getCode(),
                            entity.getName(),
                            entity.getMaterialspec(),
                            entity.getNnum(),
                            entity.getTplanstarttime(),
                            entity.getTplanendtime(),
                            entity.getNcReceiveTime(),
                            entity.getNcNote(),
                            entity.getUnit(),
                            entity.getIsDeleted(),
                            entity.getCreatedTime(),
                            entity.getQtyPerJian()
                    );
                } else {
                    entity.setOrderStatus("0");
                    toSave.add(entity);
                }
            }
            
            // 保存新订单（如果有）
            if (!toSave.isEmpty()) {
                repository.saveAll(toSave);
            }
            
            // 重新查询所有订单获取完整数据
            List<NcTBusOrderHead> allOrders = new ArrayList<>();
            for (NcTBusOrderHead entity : entitys) {
                NcTBusOrderHead saved = repository.findByCmoid(entity.getCmoid());
                if (saved != null) {
                    allOrders.add(saved);
                }
            }
            entitys = allOrders;
            final List<NcTBusOrderHead> finalEntitys = entitys;
            // 注册事务同步回调
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            // 在事务提交后启动新线程
                            new Thread(() -> {
                                for (NcTBusOrderHead entity : finalEntitys) {
                                    try {
                                        TSysCraftInfo craft = orderBackendService.getCraftInfoByMaterial(entity.getCode());
                                        orderBackendService.startOrder(
                                                entity.getOrderId(),
                                                craft.getCraftId(),
                                                craft.getCraftDetail()
                                        );
                                    } catch (Exception e) {
                                        log.error("订单自动绑定工艺路线失败 OrderId: {}, 错误: {}",
                                                entity.getOrderId(), e.getMessage(), e);
                                    }
                                }
                            }).start();
                        }
                    }
            );

            String syncContent = String.format("批量同步成功，数量：%d", dataCount);
            saveSyncLog(SYNC_TYPE_ORDER, "0", syncContent, requestJson, dataCount, System.currentTimeMillis() - startTime, null);
        } catch (Exception ex) {
            saveSyncLog(SYNC_TYPE_ORDER, "1", "批量同步失败：" + ex.getMessage(), requestJson, dataCount,
                    System.currentTimeMillis() - startTime, ex.getMessage());
            throw ex;
        }
    }

    @Override
    @Transactional
    public void softDeleteBatchByCpmohids(List<String> cpmohids) {
        if (cpmohids == null || cpmohids.isEmpty()) {
            return;
        }
        List<String> vbillcodes = repository.findVbillcodeByCpmohids(cpmohids);

        if(vbillcodes!=null && !vbillcodes.isEmpty()){
            //逗号拼接vbillcodes
            String vbillcodeStr = String.join(",", vbillcodes);
            throw new IllegalArgumentException("订单已开工，请勿删除(单号："+vbillcodeStr+")");
        }
        // 推送订单取消消息
        List<TBusOrderHead> heads = orderHeadRepository.findAllByCpmohidIn(cpmohids);
        for (TBusOrderHead head : heads) {
            try {
                String lineId = head.getCwkid();
                String baseId = ncWorklineService.getBaseIdByLineId(lineId);
                List<Integer> classIds = new ArrayList<>();
                String orderNo = head.getOrderNo();
                String productName = head.getBodyMaterialName();
                String estimatedOutput = head.getBodyPlanPrdQty().toString();
                String unit = head.getBodyUnit();
                String specification = head.getBodyMaterialSpecification();
                LocalDateTime plannedStartTime = head.getBodyPlanStartDate() == null ? null :
                        Instant.ofEpochMilli(head.getBodyPlanStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime plannedCompletionTime = head.getBodyPlanFinishDate() == null ? null :
                        Instant.ofEpochMilli(head.getBodyPlanFinishDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();


                for(TBusOrderProcess pinfo:head.getTBusOrderProcessSet()){
                    List<TSysProcessClassRel> processClassRelList = processClassRelRepository.findByProcessId(pinfo.getProcessId().getProcessId());
                    classIds=(processClassRelList.stream()
                            .map(TSysProcessClassRel::getClassId)
                            .collect(Collectors.toList()));
                    classIds=classIds.stream().distinct().collect(Collectors.toList());
                    if (lineId != null && !classIds.isEmpty()) {
                        domainPushFacade.pushOrderCancelled(baseId, lineId, classIds, head.getOrderId(), orderNo, productName,
                                estimatedOutput, unit, specification, plannedStartTime, plannedCompletionTime, "订单取消", pinfo.getOrderProcessId());
                    }
                }


            } catch (Exception e) {
                log.warn("订单取消推送失败 orderId={}, err={}", head.getOrderId(), e.getMessage());
            }
        }

        repository.deleteByCpmohids(cpmohids);
    }

    private String toJsonSafe(Object obj) {
        try {
            return JacksonUtil.toString(obj);
        } catch (Exception e) {
            log.warn("序列化订单数据失败：{}", e.getMessage());
            return "序列化失败：" + e.getMessage();
        }
    }

    private void saveSyncLog(String syncType, String syncStatus, String syncContent,
                             String requestJson, Integer dataCount, Long durationMs, String errorMessage) {
        try {
            NcSyncLog logEntity = new NcSyncLog();
            logEntity.setSyncType(syncType);
            logEntity.setSyncTime(new java.util.Date());
            logEntity.setSyncStatus(syncStatus);
            logEntity.setSyncContent(syncContent);
            logEntity.setRequestJson(requestJson);
            logEntity.setDataCount(dataCount);
            logEntity.setDurationMs(durationMs);
            logEntity.setErrorMessage(errorMessage);
            ncSyncLogRepository.save(logEntity);
        } catch (Exception e) {
            log.error("保存NC订单同步日志失败", e);
        }
    }

    /**
     * 更新BOM列表：根据材料编码(code)进行智能更新
     * - 如果材料编码已存在，则更新该BOM记录
     * - 如果材料编码是新的，则新增BOM记录
     * - 如果原有的材料编码不在新列表中，则删除该BOM记录
     */
    private void updateBomList(Integer orderId, String cmoid, Set<NcTBusOrderPPBom> newBomList) {
        if (newBomList == null || newBomList.isEmpty()) {
            // 如果新BOM列表为空，删除所有关联的BOM
            bomRepository.deleteAllLinkByOrderId(orderId);
            bomRepository.deleteAllByOrderId(cmoid);
            return;
        }

        // 获取现有的BOM列表
        List<NcTBusOrderPPBom> existingBoms = bomRepository.findAllByOrderId(orderId);
        
        // 使用材料编码作为唯一标识，构建现有BOM的Map
        Map<String, NcTBusOrderPPBom> existingBomMap = new HashMap<>();
        for (NcTBusOrderPPBom bom : existingBoms) {
            if (bom.getCode() != null) {
                existingBomMap.put(bom.getCode(), bom);
            }
        }

        // 构建新BOM的材料编码集合
        Set<String> newBomCodes = new HashSet<>();
        for (NcTBusOrderPPBom newBom : newBomList) {
            if (newBom.getCode() != null) {
                newBomCodes.add(newBom.getCode());
                
                NcTBusOrderPPBom existingBom = existingBomMap.get(newBom.getCode());
                if (existingBom != null) {
                    // 更新现有BOM记录，保留原有ID
                    newBom.setOrderPPBomId(existingBom.getOrderPPBomId());
                }
                // 确保cmoid一致
                newBom.setCmoid(cmoid);
            }
        }

        // 找出需要删除的BOM（材料编码不在新列表中的）
        List<Integer> bomIdsToDelete = new ArrayList<>();
        for (NcTBusOrderPPBom existingBom : existingBoms) {
            if (existingBom.getCode() != null && !newBomCodes.contains(existingBom.getCode())) {
                bomIdsToDelete.add(existingBom.getOrderPPBomId());
            }
        }

        // 删除不再需要的BOM记录及其关联关系
        if (!bomIdsToDelete.isEmpty()) {
            bomRepository.deleteLinkByOrderPPBomIdIn(bomIdsToDelete);
            bomRepository.deleteByOrderPPBomIdIn(bomIdsToDelete);
        }

        // 保存或更新BOM记录（JPA会根据ID判断是insert还是update）
        List<NcTBusOrderPPBom> savedBoms = bomRepository.saveAll(newBomList);
        
        // 为新增的BOM创建关联关系
        for (NcTBusOrderPPBom savedBom : savedBoms) {
            if (savedBom.getOrderPPBomId() != null) {
                // 检查关联关系是否已存在
                int linkCount = bomRepository.countLink(orderId, savedBom.getOrderPPBomId());
                if (linkCount == 0) {
                    // 不存在则创建关联关系
                    bomRepository.insertLink(orderId, savedBom.getOrderPPBomId());
                }
            }
        }
    }
}
