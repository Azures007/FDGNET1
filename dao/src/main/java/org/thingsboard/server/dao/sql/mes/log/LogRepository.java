package org.thingsboard.server.dao.sql.mes.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.sys.TSysLog;

public interface LogRepository extends JpaRepository<TSysLog,Integer> {
}
