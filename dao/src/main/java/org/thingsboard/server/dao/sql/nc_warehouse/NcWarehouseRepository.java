package org.thingsboard.server.dao.sql.nc_warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.nc_warehouse.NcWarehouse;

import java.util.List;

public interface NcWarehouseRepository extends JpaRepository<NcWarehouse, Integer> {
    NcWarehouse findByPkStordoc(String pkStordoc);

    @Query("SELECT w FROM NcWarehouse w WHERE w.pkStordoc IN :pkStordocs")
    List<NcWarehouse> findByPkStordocs(@Param("pkStordocs") List<String> pkStordocs);

    List<NcWarehouse> findByPkOrg(String pkOrg);

    @Modifying
    @Query("update NcWarehouse w set w.isDelete = '1' where w.pkStordoc in :ids")
    void softDeleteBatchByIds(@Param("ids") List<String> ids);
}
