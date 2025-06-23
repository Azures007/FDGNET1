package org.thingsboard.server.dao.sql.nc_order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TBusOrderHead;
import org.thingsboard.server.common.data.nc_order.NcTBusOrderHead;

public interface NcTBusOrderHeadRepository extends JpaRepository<NcTBusOrderHead, Integer> {
    NcTBusOrderHead findByCmoid(String cmoid);
}
