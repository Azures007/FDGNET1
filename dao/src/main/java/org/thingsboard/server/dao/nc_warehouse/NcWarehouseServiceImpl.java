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
        List<String> pkStordocs = entitys.stream()
                .map(NcWarehouse::getPkStordoc)
                .collect(Collectors.toList());
        List<NcWarehouse> existingEntities = repository.findByPkStordocs(pkStordocs);
        Map<String, NcWarehouse> existingMap = existingEntities.stream()
                .collect(Collectors.toMap(NcWarehouse::getPkStordoc, entity -> entity));
        List<NcWarehouse> entitiesToSave = new ArrayList<>();
        for (NcWarehouse entity : entitys) {
            NcWarehouse existing = existingMap.get(entity.getPkStordoc());
            if (existing != null) {
                entity.setId(existing.getId());
            }
            entitiesToSave.add(entity);
        }
        repository.saveAll(entitiesToSave);
    }
} 