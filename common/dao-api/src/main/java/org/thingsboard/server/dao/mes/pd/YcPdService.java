package org.thingsboard.server.dao.mes.pd;

import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecord;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.dao.mes.dto.PdMaterialsDto;

import java.util.List;

public interface YcPdService {
    /**
     * 保存盘点记录
     * @param tSysPdRecord
     */
    TSysPdRecord savePd(TSysPdRecord tSysPdRecord, String userId);

    /**
     * 盘点物料列表
     * @param pdMaterialsDto
     * @param userId
     * @return
     */
    List<NcInventory> pdMaterials(PdMaterialsDto pdMaterialsDto, String userId);

    /**
     * 复盘记录列表
     * @param startDate
     * @param endDate
     * @return
     */
    List<TSysPdRecord> fpWorkshopRecord(String startDate, String endDate,String userId);

    List<TSysPdRecord> showWorkshopRecord(String pdTimeStr, String pdWorkshopNumber, String ncVwkname);

    /**
     * 自定义盘点物料列表
     * @param selectBy
     * @return
     */
    List<TSyncMaterial> listMaterial(String selectBy);
}
