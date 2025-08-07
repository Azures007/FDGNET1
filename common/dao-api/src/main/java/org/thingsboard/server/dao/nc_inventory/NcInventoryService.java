package org.thingsboard.server.dao.nc_inventory;

import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.dao.vo.PageVo;

import java.util.List;

public interface NcInventoryService {
    void saveOrUpdateBatchByBillId(List<NcInventory> list);
    PageVo<NcInventory> queryInventory(String userId,String warehouseName, String materialName, String spec,Integer current, Integer size);
}
