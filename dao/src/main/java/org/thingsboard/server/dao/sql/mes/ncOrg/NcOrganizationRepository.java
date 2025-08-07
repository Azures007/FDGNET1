package org.thingsboard.server.dao.sql.mes.ncOrg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.ncOrg.NcOrganization;

import java.util.List;

public interface NcOrganizationRepository extends JpaRepository<NcOrganization, String> {
    NcOrganization findByPkOrg(String pkOrg);

    @Modifying
    @Query("update NcOrganization o set o.status = '失效' where o.pkOrg in :ids")
    void softDeleteBatchByIds(@Param("ids") List<String> ids);

    List<NcOrganization> findByStatus(String status);
    List<NcOrganization> findByPkOrgInAndStatus(List<String> pkOrg, String status);
}
