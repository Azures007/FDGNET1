package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysProcessInfo;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.TSysProcessInfo.TSysProcessInfoService;
import org.thingsboard.server.dao.dto.TSysProcessInfoDto;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/20 17:09
 * @Description:工序管理
 */
@Api(value = "工序管理/工序设置管理",tags = "工序管理/工序设置管理")
@RequestMapping("/api/process")
@RestController
public class TSysProcessController extends BaseController{
    @Autowired
    TSysProcessInfoService tSysProcessInfoService;

    @ApiOperation("保存/修改工序信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveProcess")
    public ResponseResult saveProcess(@RequestBody TSysProcessInfo processInfo) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        processInfo.setUpdatedUser(currentUser.getName());
        processInfo.setUpdatedTime(new Date());
        tSysProcessInfoService.saveProcess(processInfo);
        return ResultUtil.success();
    }

    @ApiOperation("工序详情")
    @GetMapping("/processDetail")
    public ResponseResult<TSysProcessInfo> processDetail(@RequestParam Integer processId) throws Exception {
        TSysProcessInfo processInfo=tSysProcessInfoService.processDetail(processId);
        return ResultUtil.success(processInfo);
    }

    @ApiOperation("工序详情")
    @GetMapping("/processDetailByOrderProcessId")
    public ResponseResult<TSysProcessInfo> processDetailByOrderProcessId(@RequestParam Integer orderProcessId) throws Exception {
        TSysProcessInfo processInfo=tSysProcessInfoService.processDetailByOrderProcessId(orderProcessId);
        return ResultUtil.success(processInfo);
    }

    @ApiOperation("工序删除")
    @GetMapping("/delete")
    public ResponseResult delete(@RequestParam Integer processId) throws Exception {
        tSysProcessInfoService.delete(processId);
        return ResultUtil.success();
    }

    @ApiOperation("工序列表")
    @GetMapping("/getProcessList")
    public ResponseResult<Page<TSysProcessInfo>> getProcessList(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                    @RequestParam(value = "size",defaultValue = "10") Integer size) throws Exception {
        Page<TSysProcessInfo> processList =tSysProcessInfoService.getProcessList(current,size);
        return ResultUtil.success(processList);
    }

    @ApiOperation("工序列表")
    @PostMapping("/getProcessInfoList")
    public ResponseResult<Page<TSysProcessInfo>> getProcessInfoList(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                                @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                                 @RequestBody TSysProcessInfoDto tSysProcessInfoDto) throws Exception {
        Page<TSysProcessInfo> processList =tSysProcessInfoService.getProcessList(current,size,tSysProcessInfoDto);
        return ResultUtil.success(processList);
    }

    @ApiOperation("启用/禁用")
    @GetMapping("/enableProcess")
    public ResponseResult enableProcess(@RequestParam(value = "processId") Integer processId,@ApiParam(value = "1：启用 0：禁用") @RequestParam(value = "enable") Integer enable) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tSysProcessInfoService.enableProcess(processId,enable,currentUser.getName());
        return ResultUtil.success();
    }

    @ApiOperation("工序设置")
    @PostMapping("/processSetting")
    public ResponseResult processSetting(@RequestParam Integer processId,@RequestBody List<Integer> classIds) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        return tSysProcessInfoService.processSetting(processId,classIds,currentUser.getName());
    }

    @ApiOperation("工序设置-加工班别列表")
    @PostMapping("/classList")
    public ResponseResult<List<TSysClass>> classList(@RequestParam Integer processId) throws Exception {
        List<TSysClass> classList=tSysProcessInfoService.classList(processId);
        return ResultUtil.success(classList);
    }
}