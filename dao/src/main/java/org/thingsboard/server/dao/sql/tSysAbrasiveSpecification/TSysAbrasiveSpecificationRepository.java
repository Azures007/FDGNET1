package org.thingsboard.server.dao.sql.tSysAbrasiveSpecification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.TSysAbrasiveSpecification;

import java.util.Optional;

public interface TSysAbrasiveSpecificationRepository  extends JpaRepository<TSysAbrasiveSpecification,Integer>, JpaSpecificationExecutor<TSysAbrasiveSpecification> {

    TSysAbrasiveSpecification findByAbrasiveSpecificationIdAndVersion(Integer abrasiveSpecificationId, Integer version);
}
