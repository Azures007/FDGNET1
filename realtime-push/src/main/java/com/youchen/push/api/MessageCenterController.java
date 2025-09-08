package com.youchen.push.api;

import com.youchen.push.dto.MessageItem;
import com.youchen.push.service.MessageCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.vo.PageVo;

import java.lang.reflect.Method;
import java.util.List;

@RestController
@RequestMapping("/api/message-center")
@Api(value = "消息中心接口",tags = "消息中心接口")
@RequiredArgsConstructor
public class MessageCenterController {

    private final MessageCenterService service;

    @GetMapping("/unread-count")
    @ApiOperation("获取未读消息数量")
    public ResponseResult<Integer> unreadCount() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object obj=authentication.getPrincipal();
        Method getIdMethod = obj.getClass().getMethod("getId");
        Object idValue = getIdMethod.invoke(obj);
        String userId=idValue.toString();
        return ResultUtil.success(service.unreadCount(userId));
    }

    @GetMapping("/list")
    @ApiOperation("获取消息列表")
    public ResponseResult<PageVo<MessageItem>> list(
            @ApiParam("页码，从0开始") @RequestParam(value = "current", defaultValue = "0") Integer current,
            @ApiParam("每页大小") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @ApiParam("消息状态：all(全部), unread(未读), read(已读)") @RequestParam(value = "readStatus", defaultValue = "all") String readStatus) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object obj=authentication.getPrincipal();
        Method getIdMethod = obj.getClass().getMethod("getId");
        Object idValue = getIdMethod.invoke(obj);
        String userId=idValue.toString();
        return ResultUtil.success(service.list(userId, current, size, readStatus));
    }

    @PostMapping("/mark-read")
    @ApiOperation("标记所有消息为已读")
    public ResponseResult<Void> markAllRead() throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object obj=authentication.getPrincipal();
        Method getIdMethod = obj.getClass().getMethod("getId");
        Object idValue = getIdMethod.invoke(obj);
        String userId=idValue.toString();
        service.markAllRead(userId);
        return ResultUtil.success();
    }
    @PostMapping("/mark-read-by-type")
    @ApiOperation("标记指定类型消息为已读")
    public ResponseResult<Void> markReadByType(@ApiParam("消息类型(order_today/order_tomorrow/order_cancelled/qc_daily/qc_review)")
                                                   @RequestParam(value = "type") String msgType) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object obj=authentication.getPrincipal();
        Method getIdMethod = obj.getClass().getMethod("getId");
        Object idValue = getIdMethod.invoke(obj);
        String userId=idValue.toString();
        service.markReadByType(userId,msgType);
        return ResultUtil.success();
    }
    @PostMapping("/mark-read-by-id")
    @ApiOperation("标记指定消息ID的消息为已读")
    public ResponseResult<Void> markReadById(@ApiParam("消息id")
                                               @RequestParam(value = "id") Long id) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object obj=authentication.getPrincipal();
        Method getIdMethod = obj.getClass().getMethod("getId");
        Object idValue = getIdMethod.invoke(obj);
        String userId=idValue.toString();
        service.markReadById(userId,id);
        return ResultUtil.success();
    }
}


