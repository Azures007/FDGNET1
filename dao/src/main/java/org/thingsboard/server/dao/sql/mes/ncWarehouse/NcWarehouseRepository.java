package org.thingsboard.server.dao.sql.mes.ncWarehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;

import java.util.List;

public interface NcWarehouseRepository extends JpaRepository<NcWarehouse, Integer> {

    List<NcWarehouse> findByPkOrg(String pkOrg);

    List<NcWarehouse> findByStatus(String status);

    List<NcWarehouse> findByPkStordocInAndStatus(List<String> pkStordocs, String status);
}
