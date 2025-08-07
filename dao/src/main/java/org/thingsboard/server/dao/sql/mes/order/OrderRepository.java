package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;

public interface OrderRepository extends JpaRepository<TBusOrderHead, Long>, JpaSpecificationExecutor<TBusOrderHead> {


}
