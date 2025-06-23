package org.thingsboard.server.dao.sql.nc_workline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.nc_workline.NcWorkline;

public interface NcWorklineRepository extends JpaRepository<NcWorkline, Integer> {
    NcWorkline findByCwkid(String cwkid);
} 