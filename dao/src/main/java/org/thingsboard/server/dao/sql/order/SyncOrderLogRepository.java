package org.thingsboard.server.dao.sql.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSyncOrderLog;

public interface SyncOrderLogRepository extends JpaRepository<TSyncOrderLog,Integer> {
}
