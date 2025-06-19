package org.thingsboard.server.dao.sql.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysLog;

public interface LogRepository extends JpaRepository<TSysLog,Integer> {
}
