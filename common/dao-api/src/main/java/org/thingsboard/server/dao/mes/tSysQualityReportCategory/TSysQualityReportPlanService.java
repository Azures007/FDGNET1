package org.thingsboard.server.dao.mes.tSysQualityReportCategory;

import org.thingsboard.server.dao.mes.dto.TSysQualityReportPlanDto;
import org.thingsboard.server.dao.mes.dto.TSysQualityReportPlanSearchDto;
import org.thingsboard.server.dao.mes.dto.TSysQualityReportPlanVo;
import org.thingsboard.server.dao.mes.vo.PageVo;

/**
 * @Auther: l
 * @Date: 2022/4/20 18:02
 * @Description:工序管理接口
 */
public interface TSysQualityReportPlanService {

    void savePlan(TSysQualityReportPlanDto tSysQualityReportPlanDto);

    TSysQualityReportPlanVo planDetail(Integer id);

    void delete(Integer id);

    PageVo<TSysQualityReportPlanDto> getPlanList(String userId,Integer current, Integer size, TSysQualityReportPlanSearchDto searchDto,Integer enabled);

    void enablePlan(Integer planId, Integer enable, String name);
}
