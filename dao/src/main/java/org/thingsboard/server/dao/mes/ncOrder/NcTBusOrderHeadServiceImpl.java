package org.thingsboard.server.dao.mes.ncOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.youchen.push.service.DomainPushFacade;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.sys.TSysProcessClassRel;
import org.thingsboard.server.dao.mes.ncWorkline.NcWorklineService;
import org.thingsboard.server.common.data.mes.sys.TSysCraftInfo;
import org.thingsboard.server.common.data.mes.ncOrder.NcTBusOrderHead;
import org.thingsboard.server.dao.mes.order.OrderBackendService;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftMaterialRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessClassRelRepository;
import org.thingsboard.server.dao.sql.mes.ncOrder.NcTBusOrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.ncOrder.NcTBusOrderPPBomRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        NcTBusOrderHead existingOrder = repository.findByCmoid(cmoid);
        if (existingOrder != null) {
            // 先删除原有 bomList
            Integer orderId = existingOrder.getOrderId();
            bomRepository.deleteAllLinkByOrderId(orderId);
            bomRepository.deleteAllByOrderId(cmoid);

            // 保留原有ID
            entity.setOrderId(orderId);
            // 确保cmoid一致
            entity.setCmoid(cmoid);
            entity=repository.save(entity);
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


        return entity;
    }
    @Override
    @Transactional
    public void updateByCmoidBatch(List<NcTBusOrderHead> entitys) {
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
            NcTBusOrderHead existingOrder = repository.findByCmoid(entity.getCmoid());
            if (existingOrder != null) {
                Integer orderId = existingOrder.getOrderId();
                bomRepository.deleteAllLinkByOrderId(orderId);
                bomRepository.deleteAllByOrderId(entity.getCmoid());
                entity.setOrderId(orderId);
                entity.setCmoid(existingOrder.getCmoid());
            }else{
                entity.setOrderStatus("0");
            }
            toSave.add(entity);
        }
        entitys=repository.saveAll(toSave);
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
}
