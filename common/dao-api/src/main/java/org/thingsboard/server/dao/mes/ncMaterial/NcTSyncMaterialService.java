package org.thingsboard.server.dao.mes.ncMaterial;

import org.thingsboard.server.common.data.mes.ncMaterial.NcTSyncMaterial;

import java.util.List;

public interface NcTSyncMaterialService {

    NcTSyncMaterial save(NcTSyncMaterial ncTSyncMaterial);

    /**
     * 同步物料
     * @param ncTSyncMaterial
     * @return
     */
    void syncMaterial(List<NcTSyncMaterial> ncTSyncMaterial);
}
