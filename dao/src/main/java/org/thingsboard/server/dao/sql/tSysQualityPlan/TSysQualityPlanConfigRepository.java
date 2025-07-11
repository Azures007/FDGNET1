package org.thingsboard.server.dao.sql.tSysQualityPlan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysQualityPlanConfig;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/1 17:53:24
 */
public interface TSysQualityPlanConfigRepository extends JpaRepository<TSysQualityPlanConfig,Integer> {
    
    

    void deleteByPlanId(Integer planId);

    List<TSysQualityPlanConfig> findByPlanId(Integer categoryId);
}
