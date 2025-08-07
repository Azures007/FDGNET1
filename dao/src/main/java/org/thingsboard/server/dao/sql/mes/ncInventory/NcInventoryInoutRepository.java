package org.thingsboard.server.dao.sql.mes.ncInventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventoryInOut;

import java.util.List;

public interface NcInventoryInoutRepository extends JpaRepository<NcInventoryInOut, String>, JpaSpecificationExecutor<NcInventoryInOut> {
    List<NcInventoryInOut> getAllByOrderProcessHistoryId(Integer orderProcessHistoryId);
}
