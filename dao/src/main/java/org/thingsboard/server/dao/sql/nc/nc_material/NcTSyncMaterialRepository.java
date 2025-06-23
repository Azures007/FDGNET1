package org.thingsboard.server.dao.sql.nc.nc_material;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.nc.nc_material.NcTSyncMaterial;

import java.util.List;

public interface NcTSyncMaterialRepository extends JpaRepository<NcTSyncMaterial,Integer> {

    // 根据nc物料id查询
    List<NcTSyncMaterial> findByNcMaterialId(String ncMaterialId);

    List<NcTSyncMaterial> findByNcMaterialIdIn(List<String> materialIds);
}
