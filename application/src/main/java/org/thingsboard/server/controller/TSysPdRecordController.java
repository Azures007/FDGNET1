package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.TSysPdRecord.TSysPdRecordService;
import org.thingsboard.server.dao.dto.TSysPdRecordDto;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.TSysPdRecordVo;
import org.thingsboard.server.service.security.model.SecurityUser;

/**
 * @author 许文言
 * @project youchen_IOTServer
 * @description 盘点记录
 * @date 2025/7/29 15:36:35
 */

@Api(value = "盘点记录接口", tags = "盘点记录接口")
@RequestMapping("/api/tSysPdRecord")
@RestController
public class TSysPdRecordController extends BaseController {

    @Autowired
    private TSysPdRecordService tSysPdRecordService;

    @ApiOperation("查询盘点记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sortField", value = "排序字段", readOnly = false),
            @ApiImplicitParam(name = "sortOrder", value = "排序方式（asc/desc）", readOnly = false)

    })
    @PostMapping("/pdRecordList")
    public ResponseResult<PageVo<TSysPdRecordVo>> qualityPlanList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                  @RequestParam(value = "sortField", defaultValue = "") String sortField,
                                                                  @RequestParam(value = "sortOrder", defaultValue = "") String sortOrder,
                                                                  @RequestBody TSysPdRecordDto tSysPdRecordDto) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        Page<TSysPdRecordVo> pdRecordList = tSysPdRecordService.tSysPdRecordList(currentUser.getId().toString(),current, size,sortField,sortOrder, tSysPdRecordDto);
        PageVo<TSysPdRecordVo> pageVo = new PageVo<>(pdRecordList);
        return ResultUtil.success(pageVo);

    }

    @ApiOperation("查询盘点记录列表（含还原材料）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sortField", value = "排序字段", readOnly = false),
            @ApiImplicitParam(name = "sortOrder", value = "排序方式（asc/desc）", readOnly = false)
    })
    @PostMapping("/pdRecordListWithSplit")
    public ResponseResult<PageVo<TSysPdRecordVo>> pdRecordListWithSplit(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                        @RequestParam(value = "sortField", defaultValue = "") String sortField,
                                                                        @RequestParam(value = "sortOrder", defaultValue = "") String sortOrder,
                                                                        @RequestBody TSysPdRecordDto tSysPdRecordDto) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        Page<TSysPdRecordVo> pdRecordList = tSysPdRecordService.tSysPdRecordListWithSplit(currentUser.getId().toString(), current, size, sortField, sortOrder, tSysPdRecordDto);
        PageVo<TSysPdRecordVo> pageVo = new PageVo<>(pdRecordList);
        return ResultUtil.success(pageVo);
    }
}
