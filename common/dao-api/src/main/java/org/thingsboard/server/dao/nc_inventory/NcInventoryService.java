package org.thingsboard.server.dao.nc_inventory;

import org.thingsboard.server.common.data.nc_inventory.NcInventory;

import java.util.List;

public interface NcInventoryService {
    void saveOrUpdateBatchByBillId(List<NcInventory> list);
}
