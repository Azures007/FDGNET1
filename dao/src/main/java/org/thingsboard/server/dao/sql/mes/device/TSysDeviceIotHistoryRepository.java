package org.thingsboard.server.dao.sql.mes.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.sys.TSysDeviceIotHistory;

public interface TSysDeviceIotHistoryRepository extends JpaRepository<TSysDeviceIotHistory,Integer> {
}
