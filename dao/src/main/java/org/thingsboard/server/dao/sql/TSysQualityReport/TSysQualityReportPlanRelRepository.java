package org.thingsboard.server.dao.sql.TSysQualityReport;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.TSysQualityReportPlan;
import org.thingsboard.server.common.data.TSysQualityReportPlanRel;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/21 10:19
 * @Description:
 */
public interface TSysQualityReportPlanRelRepository extends JpaRepository<TSysQualityReportPlanRel,Integer> {
    @Modifying
    @Transactional
    void deleteByCategoryId(Integer id);

    List<TSysQualityReportPlanRel> findByCategoryId(Integer id, Sort sort);
}
