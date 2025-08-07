package org.thingsboard.server.dao.sql.mes.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.bus.TBusUserCurrentOrgLine;

public interface TBusUserCurrentOrgLineRepository extends JpaRepository<TBusUserCurrentOrgLine, String> {
}
