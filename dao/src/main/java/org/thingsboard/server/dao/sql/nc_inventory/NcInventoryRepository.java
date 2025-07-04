package org.thingsboard.server.dao.sql.nc_inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.TBusOrderHead;
import org.thingsboard.server.common.data.nc_inventory.NcInventory;

public interface NcInventoryRepository extends JpaRepository<NcInventory, String>, JpaSpecificationExecutor<NcInventory> {
    Page<NcInventory> findByWarehouseNameContainingAndMaterialNameContainingAndSpecContaining(
            String warehouseName, String materialName, String spec, Pageable pageable);
}
