package org.thingsboard.server.dao.nc_warehouse;

import org.thingsboard.server.common.data.nc_warehouse.NcWarehouse;
import java.util.List;

public interface NcWarehouseService {
    void saveOrUpdateBatchByPkStordoc(List<NcWarehouse> entitys);
} 