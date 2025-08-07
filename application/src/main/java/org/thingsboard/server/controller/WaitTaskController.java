package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.OrderTaskSelectDto;
import org.thingsboard.server.dao.mes.dto.ShiftRecordDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.TaskListVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(value = "待生产任务", tags = "待生产任务")
@RequestMapping("/api/waitTask")
public class WaitTaskController extends BaseController {

    @ApiOperation("获取待生产任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @PostMapping("/getWaitTaskList")
    public ResponseResult<PageVo<TaskListVo>> getWaitTaskList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                   @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                              @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            List<String> processStatusList = new ArrayList<>();
            processStatusList.add("0");
            PageVo<TaskListVo> classList = appOrderTaskService.getWaitTaskUserId2(current, size, processStatusList, currentUser.getId().getId().toString(), sort,selectDto);
            return ResultUtil.success(classList);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("接单开工接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", required = true),
            @ApiImplicitParam(name = "orderProcessId", value = "工序执行表ID", required = true)
    })
    @GetMapping("/orderProcessStart")
    public ResponseResult orderProcessStart(@RequestParam("orderId") Integer orderId, @RequestParam("orderProcessId") Integer orderProcessId) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        return orderHeadService.orderProcessStart(orderId, orderProcessId, currentUser.getId().getId().toString());
    }

    @ApiOperation("接收移交接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderProcessId", value = "工序执行表ID", required = true)
    })
    @GetMapping("/receiveHandover")
    public ResponseResult receiveHandover(@RequestParam("orderProcessId") Integer orderProcessId) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        return orderHeadService.receiveHandover(orderProcessId,currentUser.getId().getId().toString());
    }

    @ApiOperation("驳回移交")
    @PostMapping("/rejectedShiftRecord")
    public ResponseResult rejectedShiftRecord(@RequestBody ShiftRecordDto shiftRecordDto) throws ThingsboardException, ParseException {
        SecurityUser currentUser = getCurrentUser();
        orderHeadService.rejectedShiftRecord(shiftRecordDto, currentUser.getId().getId().toString());
        return ResultUtil.success();
    }

}
