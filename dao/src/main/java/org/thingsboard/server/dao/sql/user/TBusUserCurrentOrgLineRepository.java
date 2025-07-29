package org.thingsboard.server.dao.sql.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TBusUserCurrentOrgLine;

public interface TBusUserCurrentOrgLineRepository extends JpaRepository<TBusUserCurrentOrgLine, String> {
}
