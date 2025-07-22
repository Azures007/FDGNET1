package org.thingsboard.server.dao.nc_material;

import org.thingsboard.server.common.data.nc_material.NcTSyncMaterial;

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
