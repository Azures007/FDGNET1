package org.thingsboard.server.dao.nc_warehouse;

import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;
import java.util.List;

public interface NcWarehouseService {
    void saveOrUpdateBatchByPkStordoc(List<NcWarehouse> entitys);
    List<NcWarehouse> findAll();
    /**
     * 根据基地pk查询仓库
     * @param pkOrg
     * @return
     */
    List<NcWarehouse> findByPkOrg(String pkOrg);
    /**
     * 根据仓库ID集合查询仓库
     * @param warehourseId
     * @return
     */
    List<NcWarehouse> findAllByWarehouseIds(List<String> warehourseId);
}
