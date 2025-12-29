package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.vo.ReportRecordVo;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.ReportRecordQueryDto;
import org.thingsboard.server.dao.mes.report.ReportRecordService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author
 * @version V1.0
 * @Package org.thingsboard.server.controller
 * @date 2025/12/29
 * @Description: 报工记录控制器
 */
@Api(value = "报工记录接口", tags = "报工记录接口")
@RequestMapping("/api/reportRecord")
@RestController
public class ReportRecordController extends BaseController {

    @Autowired
    private ReportRecordService reportRecordService;

//    @ApiOperation("查询报工记录列表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
//            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
//    })
//    @PostMapping("/list")
//    public ResponseResult<Page<TBusOrderProcessHistory>> getReportRecordList(
//            @RequestParam(value = "current", defaultValue = "0") Integer current,
//            @RequestParam(value = "size", defaultValue = "10") Integer size,
//            @RequestBody ReportRecordQueryDto queryDto) {
//
//        // 如果查询开始时间为空，默认为当月月初
//        if (queryDto.getReportTimeStart() == null) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//            queryDto.setReportTimeStart(calendar.getTime());
//        }
//
//        // 如果查询结束时间为空，默认为当前时间
//        if (queryDto.getReportTimeEnd() == null) {
//            queryDto.setReportTimeEnd(new Date());
//        }
//
//        Page<TBusOrderProcessHistory> reportRecordList = reportRecordService.getReportRecordList(current, size, queryDto);
//        return ResultUtil.success(reportRecordList);
//    }
    
    @ApiOperation("查询报工记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
    })
    @PostMapping("/list")
    public ResponseResult<Page<ReportRecordVo>> getReportRecordListVo(
            @RequestParam(value = "current", defaultValue = "0") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestBody ReportRecordQueryDto queryDto) {
        
        // 如果查询开始时间为空，默认为当月月初
        if (queryDto.getReportTimeStart() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            queryDto.setReportTimeStart(calendar.getTime());
        }
        
        // 如果查询结束时间为空，默认为当前时间
        if (queryDto.getReportTimeEnd() == null) {
            queryDto.setReportTimeEnd(new Date());
        }
        
        Page<ReportRecordVo> reportRecordList = reportRecordService.getReportRecordListVo(current, size, queryDto);
        return ResultUtil.success(reportRecordList);
    }
    
    @ApiOperation("导出报工记录列表")
    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> exportReportRecordList(@RequestBody ReportRecordQueryDto queryDto) {
        // 时间为空，默认当月月初
        if (queryDto.getReportTimeStart() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            queryDto.setReportTimeStart(calendar.getTime());
        }
        // 如果查询结束时间为空，默认为当前时间
        if (queryDto.getReportTimeEnd() == null) {
            queryDto.setReportTimeEnd(new Date());
        }
        List<ReportRecordVo> reportRecordList = reportRecordService.getReportRecordListForExport(queryDto);
        // 使用EasyExcel生成Excel文件
        try {
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            com.alibaba.excel.EasyExcel.write(outputStream, ReportRecordVo.class)
                    .sheet("报工记录")
                    .doWrite(reportRecordList);
            byte[] bytes = outputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=报工记录" + System.currentTimeMillis() + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(bytes.length)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}