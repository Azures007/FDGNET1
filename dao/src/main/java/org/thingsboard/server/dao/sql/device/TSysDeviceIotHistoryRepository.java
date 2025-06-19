package org.thingsboard.server.dao.sql.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysDeviceIotHistory;

public interface TSysDeviceIotHistoryRepository extends JpaRepository<TSysDeviceIotHistory,Integer> {
}
