package org.thingsboard.server.dao.sql.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TBusOrderPPBom;
import org.thingsboard.server.common.data.TBusOrderUpdate;

import java.util.List;

public interface TBusOrderUpdateRepository extends JpaRepository<TBusOrderUpdate,Integer> {
    List<TBusOrderUpdate> findByOrderId(Integer orderId);


}
