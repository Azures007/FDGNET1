package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.dto.TBusOrderProcessHistoryDto;
import org.thingsboard.server.service.security.model.SecurityUser;


@Api(value = "订单报工/盘点历史接口",tags = "订单报工/盘点历史接口")
@RequestMapping("/api/orderprocesshistory")
@RestController
public class OrderProcessHistoryController extends BaseController {


    @ApiOperation("查询订单报工/盘点历史列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "页码(默认第0页,页码从0开始)",readOnly = false),
            @ApiImplicitParam(name = "size",value = "数量(默认10条)",readOnly = false)
    })
    @PostMapping("/list")
    public ResponseResult<Page<TBusOrderProcessHistory>> classList(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                     @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                     @RequestBody TBusOrderProcessHistoryDto tBusOrderProcessHistoryDto) {
        Page<TBusOrderProcessHistory> classList = orderProcessHistoryService.tBusOrderProcessHistoryList(current,size,tBusOrderProcessHistoryDto);
        return ResultUtil.success(classList);
    }

    @ApiOperation("保存/修改订单报工/盘点历史（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/save")
    public ResponseResult saveClass(@RequestBody TBusOrderProcessHistory tBusOrderProcessHistory) throws Exception {
        SecurityUser currentUser = getCurrentUser();
//        tBusOrderProcessHistory.setUpdateUser(currentUser.getName());
//        tBusOrderProcessHistory.setUpdateTime(new Date());
        orderProcessHistoryService.saveTBusOrderProcessHistory(tBusOrderProcessHistory);
        return ResultUtil.success();
    }

    @ApiOperation("删除订单报工/盘点历史")
    @GetMapping("/delete")
    public ResponseResult deleteClass(@RequestParam("orderProcessHistoryId") Integer orderProcessHistoryId){
        orderProcessHistoryService.deleteTBusOrderProcessHistory(orderProcessHistoryId);
        return ResultUtil.success();
    }
}
