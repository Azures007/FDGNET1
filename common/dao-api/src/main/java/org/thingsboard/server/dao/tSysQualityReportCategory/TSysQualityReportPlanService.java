package org.thingsboard.server.dao.tSysQualityReportCategory;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.TSysQualityReportCategory;
import org.thingsboard.server.common.data.TSysQualityReportPlan;
import org.thingsboard.server.dao.dto.*;
import org.thingsboard.server.dao.vo.PageVo;

/**
 * @Auther: l
 * @Date: 2022/4/20 18:02
 * @Description:工序管理接口
 */
public interface TSysQualityReportPlanService {

    void savePlan(TSysQualityReportPlanDto tSysQualityReportPlanDto);

    TSysQualityReportPlanVo planDetail(Integer id);

    void delete(Integer id);

    PageVo<TSysQualityReportPlanDto> getPlanList(Integer current, Integer size, TSysQualityReportPlanSearchDto searchDto);

    void enablePlan(Integer planId, Integer enable, String name);
}
