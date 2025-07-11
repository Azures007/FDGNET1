package org.thingsboard.server.dao.sql.TSysQualityReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysQualityReportItem;
import org.thingsboard.server.common.data.TSysQualityReportPlan;

/**
 * @Auther: l
 * @Date: 2022/4/21 10:19
 * @Description:
 */
public interface TSysQualityReportPlanRepository extends JpaRepository<TSysQualityReportPlan,Integer> {
}
