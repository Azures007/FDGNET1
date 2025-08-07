package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.bus.TBusOrderBindCode;

import java.util.List;

public interface OrderBindCodeRepository extends JpaRepository<TBusOrderBindCode,Integer> {

    TBusOrderBindCode getByBindCodeNumber(String bindCodeNumber);

    TBusOrderBindCode getByBindCodeNumberAndBindCodeType(String bindCodeNumber, String bindCodeType);

    TBusOrderBindCode getByBindCodeNumberAndBindCodeTypeAndBindCodeStatus(String bindCodeNumber, String bindCodeType, Integer bindCodeStatus);

    TBusOrderBindCode findByOrderProcessHistoryId(Integer orderProcessHistoryId);

    List<TBusOrderBindCode> findByBindCodeNumberAndBindCodeTypeAndBindCodeStatus(String bindCodeNumber, String bindCodeType, int bindCodeStatus);
}
