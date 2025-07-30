package org.thingsboard.server.dao.sql.nc_warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.nc_warehouse.NcWarehouse;

import java.util.Collection;
import java.util.List;

public interface NcWarehouseRepository extends JpaRepository<NcWarehouse, Integer> {

    List<NcWarehouse> findByPkOrg(String pkOrg);

    List<NcWarehouse> findByStatus(String status);

    List<NcWarehouse> findByPkStordocInAndStatus(List<String> pkStordocs, String status);
}
