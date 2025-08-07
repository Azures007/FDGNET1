package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.OrderTaskSelectDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.TaskListVo;
import org.thingsboard.server.service.security.model.SecurityUser;

@RestController
@Api(value = "今日任务",tags = "今日任务")
@RequestMapping("/api/todayTask")
public class TodayTaskController extends BaseController{




    @ApiOperation("获取今日任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "页码(默认第0页,页码从0开始)",readOnly = false),
            @ApiImplicitParam(name = "size",value = "数量(默认10条)",readOnly = false),
            @ApiImplicitParam(name = "sort",value = "下单时间排序，desc倒叙，asc正序",readOnly = false)
    })
    @PostMapping("/getTodayTask")
    public ResponseResult<PageVo<TaskListVo>> list(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                   @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                   @RequestParam(value = "sort",defaultValue = "desc") String sort,
                                                   @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception{
        if ("desc".equals(sort) || "asc".equals(sort)){
            SecurityUser currentUser = getCurrentUser();
            PageVo<TaskListVo> classList = appOrderTaskService.getTodayTaskList(current,size,currentUser.getId().getId().toString(),sort,selectDto);
            return ResultUtil.success(classList);
        }else {
            return ResultUtil.error("排序参数值错误！");
        }

    }



}
