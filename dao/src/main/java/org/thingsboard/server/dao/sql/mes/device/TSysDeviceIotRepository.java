package org.thingsboard.server.dao.sql.mes.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.sys.TSysDeviceIot;

public interface TSysDeviceIotRepository extends JpaRepository<TSysDeviceIot,Integer> {
    TSysDeviceIot findByDeviceCode(String devicesCode);

    TSysDeviceIot findByDeviceId(Integer deviceId);

    TSysDeviceIot findByDeviceIdAndRecordType(Integer deviceId, String recordType);

    TSysDeviceIot findByDeviceCodeAndRecordType(String devicesCode, String recordType);
}
