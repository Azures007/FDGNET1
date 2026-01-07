package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.RawMaterialInputQueryDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.RawMaterialInputReportVo;
import org.thingsboard.server.service.report.RawMaterialInputReportService;

/**
 * 原料投入报表接口
 */
@Api(value = "原料投入报表接口", tags = "原料投入报表接口")
@RequestMapping("/api/rawmaterialinput")
@RestController
public class RawMaterialInputReportController extends BaseController {

    @Autowired
    private RawMaterialInputReportService rawMaterialInputReportService;

    @ApiOperation("查询原料投入报表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
    })
    @PostMapping("/query")
    public ResponseResult<PageVo<RawMaterialInputReportVo>> query(
            @RequestParam(value = "current", defaultValue = "0") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestBody RawMaterialInputQueryDto queryDto) throws ThingsboardException {
        return ResultUtil.success(rawMaterialInputReportService.queryRawMaterialInputReport(current, size, queryDto));
    }
}
