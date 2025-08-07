package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.sys.TSyncOrderLog;

public interface SyncOrderLogRepository extends JpaRepository<TSyncOrderLog,Integer> {
}
