package org.thingsboard.server.dao.sql.nc_org;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.nc_org.NcOrganization;

public interface NcOrganizationRepository extends JpaRepository<NcOrganization, Integer> {
    NcOrganization findByPkOrg(String pkOrg);
} 