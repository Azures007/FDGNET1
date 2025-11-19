package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.sys.NcSyncLog;

public interface NcSyncLogRepository extends JpaRepository<NcSyncLog, Integer> {
}

