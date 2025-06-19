package org.thingsboard.server.dao.sql.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysMessageOrder;

public interface MessageOrderRepository extends JpaRepository<TSysMessageOrder,Integer> {
    Page<TSysMessageOrder> findAllByUserId(PageRequest page);
}
