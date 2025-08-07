package org.thingsboard.server.dao.sql.mes.tSysAbrasiveSpecification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.mes.sys.TSysAbrasiveSpecification;

public interface TSysAbrasiveSpecificationRepository  extends JpaRepository<TSysAbrasiveSpecification,Integer>, JpaSpecificationExecutor<TSysAbrasiveSpecification> {

    TSysAbrasiveSpecification findByAbrasiveSpecificationIdAndVersion(Integer abrasiveSpecificationId, Integer version);
}
