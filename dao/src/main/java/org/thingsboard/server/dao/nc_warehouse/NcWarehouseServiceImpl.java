package org.thingsboard.server.dao.nc_warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.nc_warehouse.NcWarehouse;
import org.thingsboard.server.dao.sql.nc_warehouse.NcWarehouseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
}
