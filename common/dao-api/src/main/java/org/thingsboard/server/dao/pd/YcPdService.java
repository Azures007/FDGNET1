package org.thingsboard.server.dao.pd;

import org.thingsboard.server.common.data.TSysPdRecord;
import org.thingsboard.server.common.data.nc_inventory.NcInventory;
import org.thingsboard.server.dao.dto.PdMaterialsDto;

import java.util.List;

public interface YcPdService {
    /**
     * 保存盘点记录
     * @param tSysPdRecord
     */
    void savePd(TSysPdRecord tSysPdRecord);

    /**
     * 盘点物料列表
     * @param pdMaterialsDto
     * @return
     */
    List<NcInventory> pdMaterials(PdMaterialsDto pdMaterialsDto);

    /**
     * 复盘记录列表
     * @param startDate
     * @param endDate
     * @return
     */
    List<TSysPdRecord> fpWorkshopRecord(String startDate, String endDate);
}
