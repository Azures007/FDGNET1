package org.thingsboard.server.dao.sql.TSysPdRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TSysPdRecord;

import java.util.Date;

public interface tSysPdRecordRepository extends JpaRepository<TSysPdRecord, Integer>, JpaSpecificationExecutor<TSysPdRecord> {

}