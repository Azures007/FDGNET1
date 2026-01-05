package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.vo.ReportRecordVo;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.ReportRecordQueryDto;
import org.thingsboard.server.dao.mes.report.ReportRecordService;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        setQueryTime(queryDto);

        Page<ReportRecordVo> reportRecordList = reportRecordService.getReportRecordListVo(current, size, queryDto);
        return ResultUtil.success(reportRecordList);
    }



    @ApiOperation("导出报工记录列表")
    @PostMapping("/export")
    public ResponseEntity<InputStreamResource> exportReportRecordList(@RequestBody ReportRecordQueryDto queryDto) {
        setQueryTime(queryDto);
        
        try {
            // 使用临时文件方式处理大数据量导出，避免内存溢出
            File tempFile = java.nio.file.Files.createTempFile("report_export_", ".xlsx").toFile();
            tempFile.deleteOnExit(); // 确保程序退出时删除临时文件
            
            // 使用EasyExcel直接写入临时文件，避免将大数据加载到内存
            List<ReportRecordVo> reportRecordList = reportRecordService.getReportRecordListForExportOptimized(queryDto);
            if (reportRecordList == null || reportRecordList.isEmpty()) {
                com.alibaba.excel.EasyExcel.write(tempFile, ReportRecordVo.class)
                        .sheet("报工记录")
                        .doWrite(new ArrayList<>());
            } else {
                com.alibaba.excel.EasyExcel.write(tempFile, ReportRecordVo.class)
                        .sheet("报工记录")
                        .doWrite(reportRecordList);
            }
            
            // 检查文件是否生成成功
            if (tempFile.length() == 0) {
                try {
                    java.lang.reflect.Field logField = BaseController.class.getDeclaredField("log");
                    logField.setAccessible(true);
                    org.slf4j.Logger logger = (org.slf4j.Logger) logField.get(this);
                    logger.error("生成的Excel文件为空");
                } catch (Exception logE) {
                    System.err.println("生成的Excel文件为空");
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            
            // 创建输入流资源
            FileInputStream fileInputStream = new FileInputStream(tempFile);
            InputStreamResource resource = new InputStreamResource(fileInputStream);
            
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String filename = "报工记录_" + timestamp + ".xlsx";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=" + java.net.URLEncoder.encode(filename, "UTF-8"))
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(tempFile.length())
                    .body(resource);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field logField = BaseController.class.getDeclaredField("log");
                logField.setAccessible(true);
                org.slf4j.Logger logger = (org.slf4j.Logger) logField.get(this);
                logger.error("导出Excel文件时发生异常", e);
            } catch (Exception logE) {
                System.err.println("导出Excel文件时发生异常: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private static void setQueryTime(ReportRecordQueryDto queryDto) {
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

        // 如果开始时间和结束时间是同一天，则设置开始时间为当天00:00:00，结束时间为当天23:59:59
        if (queryDto.getReportTimeStart() != null && queryDto.getReportTimeEnd() != null) {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(queryDto.getReportTimeStart());
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(queryDto.getReportTimeEnd());

            // 检查是否是同一天
            if (startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)
                    && startCal.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR)) {
                // 设置开始时间为当天的00:00:00
                startCal.set(Calendar.HOUR_OF_DAY, 0);
                startCal.set(Calendar.MINUTE, 0);
                startCal.set(Calendar.SECOND, 0);
                startCal.set(Calendar.MILLISECOND, 0);
                queryDto.setReportTimeStart(startCal.getTime());

                // 设置结束时间为当天的23:59:59
                endCal.set(Calendar.HOUR_OF_DAY, 23);
                endCal.set(Calendar.MINUTE, 59);
                endCal.set(Calendar.SECOND, 59);
                endCal.set(Calendar.MILLISECOND, 999);
                queryDto.setReportTimeEnd(endCal.getTime());
            }
        }
    }
}