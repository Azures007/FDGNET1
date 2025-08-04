package org.thingsboard.server.dao.sql.pd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysPdRecordSplit;

public interface TSysPdRecordSplitRepository extends JpaRepository<TSysPdRecordSplit,Integer> {
}
