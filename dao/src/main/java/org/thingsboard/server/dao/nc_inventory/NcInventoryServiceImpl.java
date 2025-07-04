package org.thingsboard.server.dao.nc_inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.nc_inventory.NcInventory;
import org.thingsboard.server.dao.sql.nc_inventory.NcInventoryRepository;

import java.util.List;

@Service
public class NcInventoryServiceImpl implements NcInventoryService {
    @Autowired
    private NcInventoryRepository repository;

    @Override
    public void saveOrUpdateBatchByBillId(List<NcInventory> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        // 直接saveAll，JPA会根据主键billId自动新增或更新
        repository.saveAll(list);
    }
}
