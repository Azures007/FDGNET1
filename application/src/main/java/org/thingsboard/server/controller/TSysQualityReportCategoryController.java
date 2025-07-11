package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysProcessInfo;
import org.thingsboard.server.common.data.TSysQualityReportCategory;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.TSysProcessInfo.TSysProcessInfoService;
import org.thingsboard.server.dao.dto.SysQualityReportCategoryDto;
import org.thingsboard.server.dao.dto.TSysCraftSearchDto;
import org.thingsboard.server.dao.dto.TSysProcessInfoDto;
import org.thingsboard.server.dao.dto.TSysQualityReportCategorySearchDto;
import org.thingsboard.server.dao.tSysQualityReportCategory.TSysQualityReportCategoryService;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/20 17:09
 * @Description:工序管理
 */
@Api(value = "质量管理/日报检查项维护",tags = "质量管理/日报检查项维护")
@RequestMapping("/api/category")
@RestController
public class TSysQualityReportCategoryController extends BaseController{
    @Autowired
    TSysQualityReportCategoryService sysQualityReportCategoryService;

    @ApiOperation("保存/修改日报检查项信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveCategory")
    public ResponseResult saveCategory(@RequestBody SysQualityReportCategoryDto categoryDto) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        categoryDto.setUpdatedName(currentUser.getName());
        categoryDto.setUpdatedTime(new Date());
        sysQualityReportCategoryService.saveCategory(categoryDto);
        return ResultUtil.success();
    }

    @ApiOperation("工序详情")
    @GetMapping("/categoryDetail")
    public ResponseResult<SysQualityReportCategoryDto> processDetail(@RequestParam Integer id) throws Exception {
        SysQualityReportCategoryDto categoryDetail=sysQualityReportCategoryService.categoryDetail(id);
        return ResultUtil.success(categoryDetail);
    }


    @ApiOperation("工序删除")
    @GetMapping("/delete")
    public ResponseResult delete(@RequestParam Integer id) throws Exception {
        sysQualityReportCategoryService.delete(id);
        return ResultUtil.success();
    }

    @ApiOperation("工序列表")
    @GetMapping("/list")
    public ResponseResult<PageVo<TSysProcessInfo>> getCategoryList(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                    @RequestParam(value = "size",defaultValue = "10") Integer size,@RequestBody TSysQualityReportCategorySearchDto searchDto) throws Exception {
        PageVo<SysQualityReportCategoryDto> categoryList =sysQualityReportCategoryService.getCategoryList(current,size,searchDto);
        return ResultUtil.success(categoryList);
    }


    @ApiOperation("启用/禁用")
    @GetMapping("/enableProcess")
    public ResponseResult enableCategory(@RequestParam(value = "id") Integer processId,@ApiParam(value = "1：启用 0：禁用") @RequestParam(value = "enable") Integer enable) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        sysQualityReportCategoryService.enableCategory(processId,enable,currentUser.getName());
        return ResultUtil.success();
    }

}
