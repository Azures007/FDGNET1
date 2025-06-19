package org.thingsboard.server.dao.sql.licheng;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.MidOrg;

import java.util.HashSet;

public interface MidOrgRepository extends JpaRepository<MidOrg,Integer> {
    @Modifying
    @Query("delete from MidOrg where kdOrgNum in (:nums)")
    void deleteByKdOrgNums(@Param("nums") HashSet<String> nums);

    MidOrg findByKdOrgId(Integer kdOrgId);
}
