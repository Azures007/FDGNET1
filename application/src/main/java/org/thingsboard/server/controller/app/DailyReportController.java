package org.thingsboard.server.controller.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysPersonnelInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.dailyreport.DailyReportService;
import org.thingsboard.server.dao.dto.*;
import org.thingsboard.server.dao.sync.MaterialService;
import org.thingsboard.server.dao.tSysQualityReportCategory.TSysQualityReportPlanService;
import org.thingsboard.server.dao.vo.DailyReportVo;
import org.thingsboard.server.dao.vo.OrderSimpleListVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.TSyncMaterialVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(value = "YC每日报表", tags = "YC每日报表")
@RequestMapping("/api/app/day")
@RestController
public class DailyReportController extends BaseController {
    @Autowired
    protected DailyReportService dailyReportService;

    @Autowired
    MaterialService materialService;

    @Autowired
    TSysQualityReportPlanService tSysQualityReportPlanService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
    })

    @ApiOperation("每日报表品名")
    @PostMapping("/dailyMaterial")
    public ResponseResult<PageVo<OrderSimpleListVo>> dailyMaterial(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                           @RequestBody ListMaterialDto listMaterialDto) throws ThingsboardException {
        PageVo<TSyncMaterialVo> pageVo = materialService.listMaterial(current, size, listMaterialDto);
        return ResultUtil.success(pageVo);
    }


    @ApiOperation("每日报表方案")
    @PostMapping("/dailyProgram")
    public ResponseResult<PageVo<TSysQualityReportPlanDto>> dailyProgram(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                                        @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                                         @RequestBody TSysQualityReportPlanSearchDto searchDto) throws Exception {
        PageVo<TSysQualityReportPlanDto> categoryList = tSysQualityReportPlanService.getPlanList(current,size,searchDto);
        return ResultUtil.success(categoryList);
    }

    @ApiOperation("每日报表车间主任")
    @PostMapping("/shopPerson")
    public ResponseResult<PageVo<Map<String,Object>>> shopPerson(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                                         @RequestParam(value = "size",defaultValue = "10") Integer size) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        Map<String,Object> ShopPersonMap=dailyReportService.selectShopPerson(currentUser.getName(),current,size);
        return ResultUtil.success(ShopPersonMap);
    }

    @ApiOperation("每日报表获取流水号")
    @PostMapping("/dailyBillNo")
    public ResponseResult<PageVo<DailyReportVo>> dailyBillNo(@RequestBody DailyReportVo dailyReportVo) throws Exception {
        String billNo= dailyReportService.getBillNo(dailyReportVo);
        return ResultUtil.success(billNo);
    }

    @ApiOperation("每日报表明细数据")
    @PostMapping("/dailyEntryDetail")
    public ResponseResult<PageVo<DailyReportVo>> dailyDetail(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                                        @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                                        @RequestBody DailyReportVo dailyReportVo) {
        DailyReportVo detail= dailyReportService.getDailyDetail(dailyReportVo);
        return ResultUtil.success(detail);
    }

    @ApiOperation("每日报表提交保存")
    @PostMapping("/dailySave")
    public ResponseResult dailySave(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                             @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                             @RequestBody DailyReportVo dailyReportVo)throws Exception {
        SecurityUser currentUser = getCurrentUser();
        dailyReportVo.setUpdatedName(currentUser.getName());
        dailyReportVo.setUpdatedTime(LocalDate.now());
        dailyReportService.saveDaily(dailyReportVo);
        return ResultUtil.success();
    }

    @ApiOperation("每日报表详情")
    @GetMapping("/DailyDetailList")
    public ResponseResult<DailyReportVo> DailyDetail(@RequestParam Integer id) throws Exception {
        DailyReportVo planDetail= dailyReportService.DailyDetail(id);
        return ResultUtil.success(planDetail);
    }

    @ApiOperation("每日报表列表")
    @PostMapping("/getDailyList")
    public ResponseResult<PageVo<DailyReportVo>> getDailyList(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                              @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime) throws Exception {
        PageVo<DailyReportVo> categoryList = dailyReportService.getDailyList(current,size,startTime,endTime);
        return ResultUtil.success(categoryList);
    }
}
