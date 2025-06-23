package org.thingsboard.server.dao.nc_order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.TBusOrderHead;
import org.thingsboard.server.common.data.nc_order.NcTBusOrderHead;
import org.thingsboard.server.dao.sql.nc_order.NcTBusOrderHeadRepository;
import org.thingsboard.server.dao.sql.nc_order.NcTBusOrderPPBomRepository;

import java.util.ArrayList;
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
            bomRepository.deleteAllByOrderId(orderId);
            bomRepository.deleteAllLinkByOrderId(orderId);
            // 保留原有ID
            entity.setOrderId(orderId);
            // 确保cmoid一致
            entity.setCmoid(cmoid);
            return repository.save(entity);
        } else {
            return repository.save(entity);
        }
    }

    @Override
    @Transactional
    public void updateByCmoidBatch(List<NcTBusOrderHead> entitys) {
        List<NcTBusOrderHead> toSave = new ArrayList<>();
        for (NcTBusOrderHead entity : entitys) {
            NcTBusOrderHead existingOrder = repository.findByCmoid(entity.getCmoid());
            if (existingOrder != null) {
                Integer orderId = existingOrder.getOrderId();
                bomRepository.deleteAllByOrderId(orderId);
                bomRepository.deleteAllLinkByOrderId(orderId);
                entity.setOrderId(orderId);
                entity.setCmoid(existingOrder.getCmoid());
            }
            toSave.add(entity);
        }
        repository.saveAll(toSave);
    }
}
