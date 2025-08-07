package org.thingsboard.server.dao.sql.mes.ncMaterial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.ncMaterial.NcTSyncMaterial;

import java.util.List;

public interface NcTSyncMaterialRepository extends JpaRepository<NcTSyncMaterial,Integer> {

    // 根据nc物料id查询
    List<NcTSyncMaterial> findByNcMaterialId(String ncMaterialId);

    List<NcTSyncMaterial> findByNcMaterialIdIn(List<String> materialIds);
}
