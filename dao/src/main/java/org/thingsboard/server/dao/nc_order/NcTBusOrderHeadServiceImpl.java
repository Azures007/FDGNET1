package org.thingsboard.server.dao.nc_order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.TBusOrderHead;
import org.thingsboard.server.common.data.nc_order.NcTBusOrderHead;
import org.thingsboard.server.dao.sql.nc_order.NcTBusOrderHeadRepository;
import org.thingsboard.server.dao.sql.nc_order.NcTBusOrderPPBomRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NcTBusOrderHeadServiceImpl implements NcTBusOrderHeadService {
    @Autowired
    private NcTBusOrderHeadRepository repository;

    @Autowired
    private NcTBusOrderPPBomRepository bomRepository;

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
    public NcTBusOrderHead updateByCmoid(String cmoid, NcTBusOrderHead entity) {
        NcTBusOrderHead existingOrder = repository.findByCmoid(cmoid);
        if (existingOrder != null) {
            // 先删除原有 bomList
            Integer orderId = existingOrder.getOrderId();
            bomRepository.deleteAllLinkByOrderId(orderId);
            bomRepository.deleteAllByOrderId(orderId);
            // 保留原有ID
            entity.setOrderId(orderId);
            // 确保cmoid一致
            entity.setCmoid(cmoid);
            // 设置每个 bom 的 orderId
            if (entity.getBomList() != null) {
                for (var bom : entity.getBomList()) {
                    bom.setOrderId(orderId);
                }
            }
            return repository.save(entity);
        } else {
            // 新增时也要设置 bom 的 orderId
            if (entity.getBomList() != null) {
                for (var bom : entity.getBomList()) {
                    bom.setOrderId(entity.getOrderId());
                }
            }
            return repository.save(entity);
        }
    }
}
