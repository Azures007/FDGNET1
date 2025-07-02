package org.thingsboard.server.dao.sql.nc_department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.nc_department.NcDepartment;

import java.util.List;

public interface NcDepartmentRepository extends JpaRepository<NcDepartment, Integer> {
    NcDepartment findByCdeptid(String cdeptid);

    @Query("SELECT d FROM NcDepartment d WHERE d.cdeptid IN :cdeptids")
    List<NcDepartment> findByCdeptids(@Param("cdeptids") List<String> cdeptids);

    @Modifying
    @Query("update NcDepartment d set d.isDelete = '1' where d.cdeptid in :ids")
    void softDeleteBatchByIds(@Param("ids") List<String> ids);
} 
