package org.thingsboard.server.dao.sql.mes.TSysNetContentRange;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.mes.sys.TSysNetContentRange;

public interface TSysNetContentRangeRepository extends JpaRepository<TSysNetContentRange, Integer>, JpaSpecificationExecutor<TSysNetContentRange> {

}
