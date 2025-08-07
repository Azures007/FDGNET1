package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.sys.TSysMessageOrder;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.vo.*;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.List;

@RestController
@Api(value = "消息接口",tags = "消息接口")
@RequestMapping("/api/message")
public class MesController extends BaseController{

    @ApiOperation("全部信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)

    })
    @GetMapping("/listMessage")
    public ResponseResult<PageVo<TSysMessageOrder>> listMessage(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size) throws Exception {
        String userId=String.valueOf(getCurrentUser().getId().getId());
        PageVo<TSysMessageOrder> messageVos=messageService.listMessage(current,size,userId);
        return ResultUtil.success(messageVos);
    }

    @ApiOperation("获取未读读消息接口")
    @GetMapping("/getMessageSizeVo")
    public ResponseResult<MessageSizeVo> getMessageSizeVo() throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        String userId=String.valueOf(currentUser.getId().getId());
        MessageSizeVo messageSizeVo=messageService.getMessageSizeVo(userId);
        GetOrderSizeVo getOrderSizeVo= appOrderTaskService.getOrderSize(currentUser.getId().getId().toString());
        messageSizeVo.setGetOrderSizeVo(getOrderSizeVo);
        return ResultUtil.success(messageSizeVo);
    }

    @ApiOperation("发送消息接口")
    @PostMapping("/chatMessage")
    public ResponseResult chatMessage(@RequestBody TSysMessageOrder tSysMessageOrder,@RequestParam("userId") String userId) throws ThingsboardException {
        messageService.chatMessage(userId,tSysMessageOrder);
        return ResultUtil.success();
    }

    @ApiOperation("工序结束未操作提醒消息接口")
    @GetMapping("/getProcessEndMessage")
    public ResponseResult<List<ProcessEndVo>> getProcessEndMessage() throws ThingsboardException {
        String userId = String.valueOf(getCurrentUser().getId().getId());
        List<ProcessEndVo> messageSizeVo = messageService.getProcessEndMessage(userId);
        return ResultUtil.success(messageSizeVo);
    }

    @ApiOperation("工序结束未操作提醒消息该订单下次不再提醒接口")
    @GetMapping("/setProcessEndMessage")
    public ResponseResult<String> setProcessEndMessage(@RequestParam(value = "orderNo") String orderNo) throws ThingsboardException {
        String userId = String.valueOf(getCurrentUser().getId().getId());
        messageService.setProcessEndMessage(userId, orderNo);
        return ResultUtil.success("");
    }

    @ApiOperation("膜利用率工序开始与结束告警")
    @GetMapping("/alarmProcessStartAndEnd")
    public ResponseResult<List<ProcessAlarmVo>> alarmProcessStartAndEnd(@RequestParam(value = "orderNo",required = false) String orderNo,
                                                                  @RequestParam(value = "orderProcessId",required = false)Integer orderProcessId) throws ThingsboardException {
        String userId = String.valueOf(getCurrentUser().getId().getId());
        List<ProcessAlarmVo> processAlarmVos=messageService.alarmProcessStartAndEnd(orderNo,orderProcessId,userId);
        return ResultUtil.success(processAlarmVos);
    }

    @ApiOperation("工序开始告警该订单下次不再提醒接口")
    @GetMapping("/setProcessAlarmMessage")
    public ResponseResult<String> setProcessAlarmMessage(@RequestParam(value = "orderProcessId") Integer orderProcessId) throws ThingsboardException {
        messageService.setProcessAlarmMessage(orderProcessId);
        return ResultUtil.success("");
    }



}
