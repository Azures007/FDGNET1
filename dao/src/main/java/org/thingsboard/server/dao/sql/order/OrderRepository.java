package org.thingsboard.server.dao.sql.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.TBusOrderHead;

public interface OrderRepository extends JpaRepository<TBusOrderHead, Long>, JpaSpecificationExecutor<TBusOrderHead> {


}
