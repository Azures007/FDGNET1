package org.thingsboard.server.dao.sql.pd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.TSysPdRecordSplit;

import java.util.List;

public interface TSysPdRecordSplitRepository extends JpaRepository<TSysPdRecordSplit, Integer>, JpaSpecificationExecutor<TSysPdRecordSplit> {

    List<TSysPdRecordSplit> findByRePdRecordIdIn(List<Integer> rePdRecordIds);
}
