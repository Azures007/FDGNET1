package org.thingsboard.server.dao.sql.nc_department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.nc_department.NcDepartment;

public interface NcDepartmentRepository extends JpaRepository<NcDepartment, Integer> {
    NcDepartment findByCdeptid(String cdeptid);
} 