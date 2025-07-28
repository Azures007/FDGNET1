package org.thingsboard.server.dao.sql.tSysQualityPlan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysQualityPlanConfig;
import org.thingsboard.server.common.data.TSysQualityPlanJudgment;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/1 17:53:36
 */
public interface TSysQualityPlanJudgmentRepository extends JpaRepository<TSysQualityPlanJudgment,Integer> {

    void deleteByPlanId(Integer planId);

    List<TSysQualityPlanJudgment> findByPlanId(Integer planId);
}
