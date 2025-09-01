package org.thingsboard.server.controller.app.quality;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCtrl;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlan;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.ImportParam.TSysQualityCtrlImportParam;
import org.thingsboard.server.dao.mes.dto.TSysQualityCtrlDto;
import org.thingsboard.server.dao.mes.dto.TSysQualityPlanDto;
import org.thingsboard.server.dao.mes.tSysQualityCategory.TSysQualityCategoryService;
import org.thingsboard.server.dao.mes.tSysQualityCtrl.TSysQualityCtrlService;
import org.thingsboard.server.dao.mes.tSysQualityPlan.TSysQualityPlanService;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.TSysQualityCtrlVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 品质管控
 * @date 2025/7/17 15:25:08
 */
@Api(value = "质检管控接口", tags = "质检管控接口")
@RequestMapping("/api/tSysQualityCtrl")
@RestController
public class TSysQualityCtrlAppController extends BaseController {

    @Autowired
    private TSysQualityPlanService tSysQualityPlanService;

    @Autowired
    private TSysQualityCategoryService tSysQualityCategoryService;

    @Autowired
    private TSysQualityCtrlService tSysQualityCtrlService;

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
                                                                   @RequestParam(value = "sortField", defaultValue = "") String sortField,
                                                                   @RequestParam(value = "sortOrder", defaultValue = "") String sortOrder,
                                                                   @RequestBody TSysQualityPlanDto tSysQualityPlanDto) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        Page<TSysQualityPlan> qualityPlanList = tSysQualityPlanService.tSysQualityPlanList(currentUser.getId().toString(), current, size, sortField, sortOrder, tSysQualityPlanDto);
        PageVo<TSysQualityPlan> pageVo = new PageVo<>(qualityPlanList);
        return ResultUtil.success(pageVo);
    }


    @ApiOperation("查询质检管控列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sortField", value = "排序字段", readOnly = false),
            @ApiImplicitParam(name = "sortOrder", value = "排序方式（asc/desc）", readOnly = false)

    })
    @PostMapping("/qualityCtrlList")
    public ResponseResult<PageVo<TSysQualityCtrl>> qualityCtrlList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                   @RequestParam(value = "sortField", defaultValue = "") String sortField,
                                                                   @RequestParam(value = "sortOrder", defaultValue = "") String sortOrder,
                                                                   @RequestBody TSysQualityCtrlDto tSysQualityCtrlDto) {
        Page<TSysQualityCtrl> qualityCtrlList = tSysQualityCtrlService.
                tSysQualityCtrlList(current, size, sortField, sortOrder, tSysQualityCtrlDto);
        PageVo<TSysQualityCtrl> pageVo = new PageVo<>(qualityCtrlList);
        return ResultUtil.success(pageVo);
    }

    //查询方案明细
    @ApiOperation("根据ID查询明细")
    @GetMapping("/getQualityPlanById")
    public ResponseResult<TSysQualityCtrlVo> getQualityPlanById(@RequestParam("planId") Integer planId) {

        TSysQualityCtrlVo vo = tSysQualityCtrlService.getQualityPlanById(planId);
        if (vo == null) {
            return ResultUtil.error("未找到该数据");
        } else {
            return ResultUtil.success(vo);
        }
    }


    //保存品质管控表
    @ApiOperation("保存/修改质检方案信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveQualityCtrl")
    public ResponseResult<TSysQualityCtrlVo> saveQualityCtrl(@RequestBody TSysQualityCtrlImportParam tSysQualityCtrlImportParam) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        TSysQualityCtrl tSysQualityCtrl = tSysQualityCtrlImportParam.getTSysQualityCtrl();
        if(tSysQualityCtrl.getId() == null){
            tSysQualityCtrl.setCreateUser(currentUser.getFirstName());
            tSysQualityCtrl.setCreateTime(new Date());
        }
        tSysQualityCtrl.setUpdateUser(currentUser.getFirstName());
        tSysQualityCtrl.setUpdateTime(new Date());

        // 保存数据并获取返回的VO对象
        TSysQualityCtrlVo result = tSysQualityCtrlService.saveTSysQualityCtrlAndDetail(tSysQualityCtrl, tSysQualityCtrlImportParam.getTSysQualityCtrlDetailList());

        return ResultUtil.success(result);
    }

    //根据id查看品质管控
    @ApiOperation("根据id查看品质管控")
    @PostMapping("/getQualityCtrlById")
    public ResponseResult<TSysQualityCtrlVo> getQualityCtrlById(@RequestParam("id") Integer id) throws Exception {
        TSysQualityCtrlVo vo = tSysQualityCtrlService.getQualityCtrlById(id);
        if (vo == null) {
            return ResultUtil.error("未找到该数据");
        } else {
            return ResultUtil.success(vo);
        }
    }

    @ApiOperation("查询复核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sortField", value = "排序字段", readOnly = false),
            @ApiImplicitParam(name = "sortOrder", value = "排序方式（asc/desc）", readOnly = false)

    })
    @PostMapping("/qualityCtrlCheckList")
    public ResponseResult<PageVo<TSysQualityCtrl>> qualityCtrlCheckList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                        @RequestParam(value = "sortField", defaultValue = "") String sortField,
                                                                        @RequestParam(value = "sortOrder", defaultValue = "") String sortOrder) {

        Page<TSysQualityCtrl> qualityCtrlCheckList = tSysQualityCtrlService
                .tSysQualityCtrlCheckList(current, size, sortField, sortOrder);
        PageVo<TSysQualityCtrl> pageVo = new PageVo<>(qualityCtrlCheckList);
        return ResultUtil.success(pageVo);
    }


}
