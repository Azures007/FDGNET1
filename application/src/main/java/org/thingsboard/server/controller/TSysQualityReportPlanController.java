
package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TSysProcessInfo;
import org.thingsboard.server.common.data.TSysQualityReportCategory;
import org.thingsboard.server.common.data.TSysQualityReportPlan;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.dto.*;
import org.thingsboard.server.dao.tSysQualityReportCategory.TSysQualityReportPlanService;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/4/20 17:09
 * @Description:工序管理
 */
@Api(value = "质量管理/日报方案管理",tags = "质量管理/日报方案管理")
@RequestMapping("/api/plan")
@RestController
public class TSysQualityReportPlanController extends BaseController{
    @Autowired
    TSysQualityReportPlanService tSysQualityReportPlanService;

    @ApiOperation("保存/日报方案（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/savePlan")
    public ResponseResult savePlan(@RequestBody TSysQualityReportPlanDto tSysQualityReportPlanDto) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tSysQualityReportPlanDto.setUpdatedName(currentUser.getName());
        tSysQualityReportPlanDto.setUpdatedTime(new Date());
        tSysQualityReportPlanService.savePlan(tSysQualityReportPlanDto);
        return ResultUtil.success();
    }

    @ApiOperation("方案详情")
    @GetMapping("/detail")
    public ResponseResult<TSysQualityReportPlanVo> planDetail(@RequestParam Integer id) throws Exception {
        TSysQualityReportPlanVo planDetail= tSysQualityReportPlanService.planDetail(id);
        return ResultUtil.success(planDetail);
    }


    @ApiOperation("方案删除")
    @GetMapping("/delete")
    public ResponseResult delete(@RequestParam Integer id) throws Exception {
        tSysQualityReportPlanService.delete(id);
        return ResultUtil.success();
    }

    @ApiOperation("方案列表")
    @GetMapping("/list")
    public ResponseResult<PageVo<TSysQualityReportPlanDto>> getPlanList(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                    @RequestParam(value = "size",defaultValue = "10") Integer size,@RequestBody TSysQualityReportPlanSearchDto searchDto) throws Exception {
        PageVo<TSysQualityReportPlanDto> categoryList = tSysQualityReportPlanService.getPlanList(current,size,searchDto);
        return ResultUtil.success(categoryList);
    }


    @ApiOperation("启用/禁用")
    @GetMapping("/enable")
    public ResponseResult enablePlan(@RequestParam(value = "id") Integer planId,@ApiParam(value = "1：启用 0：禁用") @RequestParam(value = "enable") Integer enable) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tSysQualityReportPlanService.enablePlan(planId,enable,currentUser.getName());
        return ResultUtil.success();
    }

}
