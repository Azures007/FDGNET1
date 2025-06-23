package org.thingsboard.server.dao.nc_order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.TBusOrderHead;
import org.thingsboard.server.common.data.nc_order.NcTBusOrderHead;
import org.thingsboard.server.dao.sql.nc_order.NcTBusOrderHeadRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NcTBusOrderHeadServiceImpl implements NcTBusOrderHeadService {
    @Autowired
    private NcTBusOrderHeadRepository repository;

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
            // 保留原有ID
            entity.setOrderId(existingOrder.getOrderId());
            // 确保cmoid一致
            entity.setCmoid(cmoid);
            return repository.save(entity);
        }else{
            return repository.save(entity);
        }
    }
}
