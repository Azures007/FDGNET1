package org.thingsboard.server.dao.mes.tSysQualityPlan;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlan;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlanConfig;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlanJudgment;
import org.thingsboard.server.dao.mes.dto.TSysQualityPlanDto;
import org.thingsboard.server.dao.mes.vo.TSysQualityPlanVo;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/1 17:01:31
 */
public interface TSysQualityPlanService {

    /**
     * 返回质检方案列表
     * @return
     */
    Page<TSysQualityPlan> tSysQualityPlanList(String userId, Integer current, Integer size, String sortField, String sortOrder, TSysQualityPlanDto tSysQualityPlanDto);


    /**
     * 保存质检方案信息
     * @param tSysQualityPlan
     * @param tSysQualityPlanConfigList
     */
    void saveTSysQualityPlanDetail(TSysQualityPlan tSysQualityPlan, List<TSysQualityPlanJudgment> tSysQualityPlanJudgmentList, List<TSysQualityPlanConfig> tSysQualityPlanConfigList);

    /**
     * 保存质检方案表
     * @param tSysQualityPlan
     */
    void saveTSysQualityPlan(TSysQualityPlan tSysQualityPlan);

    /**
     * 保存质检方案配置表
     * @param categoryId
     * @param tSysQualityPlanConfigList
     */
    void saveTSysQualityPlanConfig(Integer categoryId, List<TSysQualityPlanConfig> tSysQualityPlanConfigList);

    /**
     * 根据ID删除质检方案
     * @param planId
     */
    void deleteTSysQualityPlan(Integer planId);

    /**
     * 根据ID获取质检方案详情
     * @param planId
     * @return
     */
    TSysQualityPlanVo getQualityPlanById(Integer planId);


    void saveTSysQualityPlanJudgment(Integer id, List<TSysQualityPlanJudgment> tSysQualityPlanJudgmentList);

    Page<TSysQualityPlan> tSysQualityPlanListWithEnable(String userId, Integer current, Integer size, String sortField, String sortOrder, TSysQualityPlanDto tSysQualityPlanDto);

}
