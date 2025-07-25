package org.thingsboard.server.controller.quality;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TSysQualityCategory;
import org.thingsboard.server.common.data.TSysQualityPlan;
import org.thingsboard.server.common.data.TSysQualityPlanConfig;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.ImportParam.TSysQualityPlanImportParam;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysQualityCategoryDto;
import org.thingsboard.server.dao.dto.TSysQualityPlanDto;
import org.thingsboard.server.dao.tSysQualityCategory.TSysQualityCategoryService;
import org.thingsboard.server.dao.tSysQualityPlan.TSysQualityPlanService;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.TSysQualityPlanVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检方案
 * @date 2025/6/27 15:12:11
 */
@Api(value = "质检方案接口", tags = "质检方案接口")
@RequestMapping("/api/tSysQualityPlan")
@RestController
public class TSysQualityPlanController extends BaseController {

    @Autowired
    private TSysQualityPlanService tSysQualityPlanService;

    @Autowired
    private TSysQualityCategoryService tSysQualityCategoryService;

    @ApiOperation("查询质检方案列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sortField", value = "排序字段", readOnly = false),
            @ApiImplicitParam(name = "sortOrder", value = "排序方式（asc/desc）", readOnly = false)

    })
    @PostMapping("/qualityPlanList")
    public ResponseResult<PageVo<TSysQualityPlan>> qualityPlanList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                   @RequestParam(value = "sortField", defaultValue = "createTime") String sortField,
                                                                   @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
                                                                   @RequestBody TSysQualityPlanDto tSysQualityPlanDto) throws ThingsboardException {
        sortField = sortField.equals("") ? sortField : "createTime";
        sortOrder = sortOrder.equals("") ? sortOrder : "asc" ;
        SecurityUser currentUser = getCurrentUser();
        Page<TSysQualityPlan> qualityPlanList = tSysQualityPlanService.tSysQualityPlanList(currentUser.getId().toString(),current, size,sortField,sortOrder, tSysQualityPlanDto);
        PageVo<TSysQualityPlan> pageVo = new PageVo<>(qualityPlanList);
        return ResultUtil.success(pageVo);
    }




    @ApiOperation("保存/修改质检方案信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveQualityPlan")
    public ResponseResult saveQualityPlan(@RequestBody TSysQualityPlanImportParam tSysQualityPlanImportParam) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        TSysQualityPlan tSysQualityPlan = tSysQualityPlanImportParam.gettSysQualityPlan();
        tSysQualityPlan.setUpdateUser(currentUser.getName());
        tSysQualityPlan.setUpdateTime(new Date());
        tSysQualityPlanService.saveTSysQualityPlanDetail(tSysQualityPlan,tSysQualityPlanImportParam.gettSysQualityPlanJudgmentList(), tSysQualityPlanImportParam.gettSysQualityPlanConfigList());
        return ResultUtil.success();
    }

    @ApiOperation("删除质检方案")
    @GetMapping("/deleteQualityPlan")
    public ResponseResult deleteQualityPlan(@RequestParam("planId") Integer planId) {
        tSysQualityPlanService.deleteTSysQualityPlan(planId);
        return ResultUtil.success();
    }

    @ApiOperation("根据ID查询明细")
    @GetMapping("/getQualityPlanById")
    public ResponseResult<TSysQualityPlanVo> getQualityPlanById(@RequestParam("planId") Integer planId) {

        TSysQualityPlanVo vo = tSysQualityPlanService.getQualityPlanById(planId);

        if (vo == null) {
            return ResultUtil.error("未找到该数据");
        } else {
            return ResultUtil.success(vo);
        }

    }

    @ApiOperation("禁用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "planId", value = "方案ID", required = true),
            @ApiImplicitParam(name = "enabledSt", value = "禁用标识 0：禁用 1：启用", required = true)
    })
    @PostMapping("/isEnabled")
    public ResponseResult isEnabled(@RequestParam("planId") Integer planId, @RequestParam("enabledSt") Integer isEnabled) throws Exception {
        TSysQualityPlanVo tSysQualityPlanVo = tSysQualityPlanService.getQualityPlanById(planId);
        tSysQualityPlanVo.setIsEnabled(isEnabled == 1 ? GlobalConstant.enableTrue : GlobalConstant.enableFalse);
//        this.saveClass(tSysClass);
        SecurityUser currentUser = getCurrentUser();
        tSysQualityPlanVo.setUpdateUser(currentUser.getName());
        tSysQualityPlanVo.setUpdateTime(new Date());

        TSysQualityPlan tSysQualityPlan = new TSysQualityPlan();
        BeanUtils.copyProperties(tSysQualityPlanVo,tSysQualityPlan);
        tSysQualityPlanService.saveTSysQualityPlan(tSysQualityPlan);
        return ResultUtil.success();
    }



    @ApiOperation("查询质检类目列表（新增配置表数据使用）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
//            @ApiImplicitParam(name = "sortField", value = "排序字段", readOnly = false),
//            @ApiImplicitParam(name = "sortOrder", value = "排序方式（asc/desc）", readOnly = false)

    })
    @PostMapping("/qualityCategoryList")
    public ResponseResult<PageVo<TSysQualityPlanConfig>> qualityCategoryList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
//                                                                           @RequestParam(value = "sortField", defaultValue = "") String sortField,
//                                                                           @RequestParam(value = "sortOrder", defaultValue = "") String sortOrder,
                                                                           @RequestBody TSysQualityCategoryDto tSysQualityCategoryDto) {
        tSysQualityCategoryDto.setIsEnabled("1");

        //todo  查询类目信息之后将其转换成json串返回


        Page<TSysQualityPlanConfig> qualityCategoryList =
                tSysQualityCategoryService.getTSysQualityCategoryListToPlan(current, size,tSysQualityCategoryDto);
        PageVo<TSysQualityPlanConfig> pageVo = new PageVo<>(qualityCategoryList);
        return ResultUtil.success(pageVo);
//        return null;
    }

}
