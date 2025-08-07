package org.thingsboard.server.dao.sql.mes.pd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecordSplit;

import java.util.List;

public interface TSysPdRecordSplitRepository extends JpaRepository<TSysPdRecordSplit, Integer>, JpaSpecificationExecutor<TSysPdRecordSplit> {

    List<TSysPdRecordSplit> findByRePdRecordIdIn(List<Integer> rePdRecordIds);
}
