package org.thingsboard.server.dao.sql.nc_org;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.nc_department.NcDepartment;
import org.thingsboard.server.common.data.nc_org.NcOrganization;

import java.util.List;

public interface NcOrganizationRepository extends JpaRepository<NcOrganization, Integer> {
    NcOrganization findByPkOrg(String pkOrg);

    @Modifying
    @Query("update NcOrganization o set o.isDelete = '1' where o.pkOrg in :ids")
    void softDeleteBatchByIds(@Param("ids") List<String> ids);
}
