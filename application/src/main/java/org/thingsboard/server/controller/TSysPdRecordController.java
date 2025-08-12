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
import org.thingsboard.server.service.pd.TSysPdRecordExcelService;
import org.thingsboard.server.dao.mes.TSysPdRecord.TSysPdRecordService;
import org.thingsboard.server.dao.mes.dto.TSysPdRecordDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.TSysPdRecordVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

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

    @Autowired
    private TSysPdRecordExcelService tSysPdRecordExcelService;

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
        if (tSysPdRecordDto.getEndTime() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tSysPdRecordDto.getEndTime());
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            tSysPdRecordDto.setEndTime(calendar.getTime());
        }
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
        if (tSysPdRecordDto.getEndTime() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tSysPdRecordDto.getEndTime());
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            tSysPdRecordDto.setEndTime(calendar.getTime());
        }
        Page<TSysPdRecordVo> pdRecordList = tSysPdRecordService.tSysPdRecordListWithSplit(currentUser.getId().toString(), current, size, sortField, sortOrder, tSysPdRecordDto);
        PageVo<TSysPdRecordVo> pageVo = new PageVo<>(pdRecordList);
        return ResultUtil.success(pageVo);
    }

    /**
     * excel导出
     */
    @ApiOperation("excel导出")
    @PostMapping("/exportPdRecord")
    public void download(@RequestParam(value = "current", defaultValue = "0") Integer current,
                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                         @RequestBody TSysPdRecordDto tSysPdRecordDto,
                         HttpServletResponse response) throws IOException, ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        tSysPdRecordExcelService.download(currentUser.getId().toString(), current, size, tSysPdRecordDto, response);
    }

    /**
     * excel导出（含还原材料）
     */
    @ApiOperation("excel导出（含还原材料）")
    @PostMapping("/exportPdRecordWithSplit")
    public void downloadWithSplit(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                  @RequestBody TSysPdRecordDto tSysPdRecordDto,
                                  HttpServletResponse response) throws IOException, ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        tSysPdRecordExcelService.downloadWithSplit(currentUser.getId().toString(), current, size, tSysPdRecordDto, response);
    }
}
