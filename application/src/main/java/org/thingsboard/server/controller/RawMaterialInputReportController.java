package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.RawMaterialInputQueryDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.RawMaterialInputReportVo;
import org.thingsboard.server.service.report.RawMaterialInputReportService;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 原料投入报表接口
 */
@Api(value = "原料投入报表接口", tags = "原料投入报表接口")
@RequestMapping("/api/rawmaterialinput")
@RestController
@Slf4j
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

    @ApiOperation("导出原料投入报表")
    @PostMapping("/export")
    public void export(
            @RequestBody RawMaterialInputQueryDto queryDto,
            javax.servlet.http.HttpServletResponse response) {
        try {
            String fileName = "原料投入报表_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            rawMaterialInputReportService.exportRawMaterialInputReport(0, 10000, queryDto, response);
        } catch (Exception e) {
            try {
                response.reset();
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
                response.getWriter().flush();
            } catch (Exception ex) {
                log.error("导出报表时发生异常", ex);
            }
        }
    }

}