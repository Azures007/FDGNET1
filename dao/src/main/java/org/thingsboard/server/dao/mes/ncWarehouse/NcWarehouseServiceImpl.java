package org.thingsboard.server.dao.mes.ncWarehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;
import org.thingsboard.server.dao.sql.mes.ncWarehouse.NcWarehouseRepository;

import java.util.List;

@Service
public class NcWarehouseServiceImpl implements NcWarehouseService {
    @Autowired
    private NcWarehouseRepository repository;

    @Override
    @Transactional
    public void saveOrUpdateBatchByPkStordoc(List<NcWarehouse> entitys) {
        if (entitys == null || entitys.isEmpty()) {
            return;
        }
        repository.saveAll(entitys);
    }

    @Override
    public List<NcWarehouse> findAll() {
        return repository.findByStatus("生效");
    }

    @Override
    public List<NcWarehouse> findByPkOrg(String pkOrg) {
        return repository.findByPkOrg(pkOrg);
    }

    @Override
    public List<NcWarehouse> findAllByWarehouseIds(List<String> warehourseId) {
        return repository.findByPkStordocInAndStatus(warehourseId, "生效");
    }
}
