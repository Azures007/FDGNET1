package org.thingsboard.server.dao.sql.licheng;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.MidDept;

import java.util.HashSet;

public interface MidDeptRepository extends JpaRepository<MidDept,Integer> {
    @Modifying
    @Query("delete from MidDept where kdDeptNum in (:nums)")
    void deleteByKdDeptNums(@Param("nums") HashSet<String> nums);

    MidDept findByKdDeptId(Integer kdWorkshopId);
}
