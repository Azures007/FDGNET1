package org.thingsboard.server.dao.mes.ncInventory;

import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.dao.mes.dto.NcInventorySyncRequest;
import org.thingsboard.server.dao.mes.vo.PageVo;

public interface NcInventoryService {
    void saveOrUpdateBatchByBillId(NcInventorySyncRequest request);
    PageVo<NcInventory> queryInventory(String userId, String warehouseName, String materialName, String spec, Integer current, Integer size);
}
