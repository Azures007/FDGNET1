package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.LichengConstants;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.dto.OrderTaskSelectDto;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.TaskListVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.ArrayList;
import java.util.List;

@RestController
@Api(value = "生产中任务",tags = "生产中任务")
@RequestMapping("/api/startTask")
public class StartTaskController extends BaseController{

    @ApiOperation("获取生产中任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "页码(默认第0页,页码从0开始)",readOnly = false),
            @ApiImplicitParam(name = "size",value = "数量(默认10条)",readOnly = false),
            @ApiImplicitParam(name = "sort",value = "下单时间排序，desc倒叙，asc正序",readOnly = false)
    })
    @PostMapping("/getStartTaskList")
    public ResponseResult<PageVo<TaskListVo>> getStartTaskList(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                               @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                               @RequestParam(value = "sort",defaultValue = "desc") String sort,
                                                               @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception{
        if ("desc".equals(sort) || "asc".equals(sort)){
            SecurityUser currentUser = getCurrentUser();
            List<String> processStatusList = new ArrayList<>();
            processStatusList.add("1");
            processStatusList.add("2");
            String orderProcessType = LichengConstants.ORDER_PROCESS_TYPE_1;
            PageVo<TaskListVo> classList = appOrderTaskService.getTaskListByPersonIdAndProcessStatus(current,size,processStatusList,orderProcessType,currentUser.getId().getId().toString(),sort,selectDto);
            return ResultUtil.success(classList);
        }else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("工序暂停接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId",value = "订单ID",required = true),
            @ApiImplicitParam(name = "orderProcessId",value = "订单工序执行ID",required = true),
            @ApiImplicitParam(name = "suspendReason",value = "暂停原因（字典值）",required = true)
    })
    @GetMapping("/suspendProcess")
    public ResponseResult suspendProcess(@RequestParam("orderId") Integer orderId,@RequestParam("orderProcessId") Integer orderProcessId,@RequestParam("suspendReason") String suspendReason) throws Exception {
        return orderHeadService.suspendProcess(orderId, orderProcessId, suspendReason);
    }

    @ApiOperation("工序恢复接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId",value = "订单ID",required = true),
            @ApiImplicitParam(name = "orderProcessId",value = "订单工序执行ID",required = true)
    })
    @GetMapping("/recoverProcess")
    public ResponseResult recoverProcess(@RequestParam("orderId") Integer orderId,@RequestParam("orderProcessId") Integer orderProcessId) throws Exception {
        return orderHeadService.recoverProcess(orderId, orderProcessId);
    }

    @ApiOperation("工序结束接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId",value = "订单id",required = true),
            @ApiImplicitParam(name = "orderProcessId",value = "订单工序执行ID",required = true)
    })
    @GetMapping("/stopProcess")
    public ResponseResult stopProcess(@RequestParam("orderId") Integer orderId, @RequestParam("orderProcessId") Integer orderProcessId) throws Exception {
        return orderHeadService.stopProcess(orderId, orderProcessId);
    }
}
