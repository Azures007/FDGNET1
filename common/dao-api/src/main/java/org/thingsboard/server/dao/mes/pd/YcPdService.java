package org.thingsboard.server.dao.mes.pd;

import org.thingsboard.server.common.data.mes.sys.TSysPdRecord;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.dao.mes.dto.PdMaterialsDto;

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

    List<TSysPdRecord> showWorkshopRecord(String pdTimeStr, String pdWorkshopNumber);
}
