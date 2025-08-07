package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.bus.TBusOrderUpdate;

import java.util.List;

public interface TBusOrderUpdateRepository extends JpaRepository<TBusOrderUpdate,Integer> {
    List<TBusOrderUpdate> findByOrderId(Integer orderId);


}
