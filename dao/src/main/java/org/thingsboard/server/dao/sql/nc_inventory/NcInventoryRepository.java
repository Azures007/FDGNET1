package org.thingsboard.server.dao.sql.nc_inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.nc_inventory.NcInventory;

public interface NcInventoryRepository extends JpaRepository<NcInventory, String> {
}
