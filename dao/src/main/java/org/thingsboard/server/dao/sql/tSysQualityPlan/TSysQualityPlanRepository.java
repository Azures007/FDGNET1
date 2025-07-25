package org.thingsboard.server.dao.sql.tSysQualityPlan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysQualityPlan;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/1 17:47:43
 */
public interface TSysQualityPlanRepository extends JpaRepository<TSysQualityPlan,Integer> {

    @Query(value = "SELECT t.id, t.plan_name, t.production_line_id, t.remarks, \n" +
            "t.is_enabled, t.create_time, t.create_user, t.update_time, t.update_user,t.production_line_name \n" +
            "FROM t_sys_quality_plan t \n" +
            "left join t_sys_workline t1 \n" +
            "on t.production_line_id =t1.nc_cwkid \n" +
            "where 1=1 \n" +
            "and (t.plan_name like %:#{#tSysQualityPlan.planName}% or :#{#tSysQualityPlan.planName} is null or :#{#tSysQualityPlan.planName} ='') \n" +
            "and (t.production_line_id = :#{#tSysQualityPlan.productionLineId} or :#{#tSysQualityPlan.productionLineId} is null or :#{#tSysQualityPlan.productionLineId} ='') \n" +
            "and (t1.nc_vwkname like %:#{#tSysQualityPlan.productionLineName}% or :#{#tSysQualityPlan.productionLineName} is null or :#{#tSysQualityPlan.productionLineName} ='') \n" +
            "and (t.is_enabled=:#{#tSysQualityPlan.isEnabled} or :#{#tSysQualityPlan.isEnabled} is null or :#{#tSysQualityPlan.isEnabled} ='')",nativeQuery = true)
    Page<TSysQualityPlan> findAllBy(@Param("tSysQualityPlan") TSysQualityPlan tSysQualityPlan, Pageable pageable);


    List<TSysQualityPlan> findByPlanName(String planName);
}
