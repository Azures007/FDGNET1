package org.thingsboard.server.dao.mes.ncOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.thingsboard.server.common.data.mes.sys.TSysCraftInfo;
import org.thingsboard.server.common.data.mes.ncOrder.NcTBusOrderHead;
import org.thingsboard.server.dao.mes.order.OrderBackendService;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftMaterialRelRepository;
import org.thingsboard.server.dao.sql.mes.ncOrder.NcTBusOrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.ncOrder.NcTBusOrderPPBomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Integer seq = entity.getSeq();
        if (vbillcode == null || seq == null) {
            throw new IllegalArgumentException("vbillcode or seq cannot be null when generating orderNo");
        }
        String orderNo = vbillcode + "-" + String.format("%02d", seq);
        entity.setOrderNo(orderNo);
        entity.setOrderStatus("0");
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
            entity=repository.save(entity);
        }
        NcTBusOrderHead finalEntity = entity;
        new Thread(() -> {
            //订单自动绑定工艺路线
            try {
                TSysCraftInfo craft=orderBackendService.getCraftInfoByMaterial(finalEntity.getCode());
                orderBackendService.startOrder(finalEntity.getOrderId(),craft.getCraftId(),craft.getCraftDetail());
            } catch (Exception e) {
                log.info("订单自动绑定工艺路线失败OrderId："+ finalEntity.getOrderId()+"," + e.getMessage());
            }
        }).start();

        return entity;
    }
    @Override
    @Transactional
    public void updateByCmoidBatch(List<NcTBusOrderHead> entitys) {
        List<NcTBusOrderHead> toSave = new ArrayList<>();
        for (NcTBusOrderHead entity : entitys) {
            String vbillcode = entity.getVbillcode();
            Integer seq = entity.getSeq();
            if (vbillcode == null || seq == null) {
                throw new IllegalArgumentException("vbillcode or seq cannot be null when generating orderNo");
            }
            String orderNo = vbillcode + "-" + String.format("%02d", seq);
            entity.setOrderNo(orderNo);
            entity.setOrderStatus("0");
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
        repository.deleteByCpmohids(cpmohids);
    }
}
