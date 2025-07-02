package org.thingsboard.server.controller.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.dto.TBusOrderDto;
import org.thingsboard.server.dao.order.OrderHeadService;
import org.thingsboard.server.dao.vo.OrderDetailSimpleVo;
import org.thingsboard.server.dao.vo.OrderSimpleListVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.service.order.OrderHeadExcelService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(value = "YC订单接口", tags = "YC订单接口")
@RequestMapping("/api/manage/orderhead")
@RestController
public class YcOrderManageController {
    @Autowired
    protected OrderHeadService orderHeadService;
    @Autowired
    OrderHeadExcelService orderHeadExcelService;
    @ApiOperation("查询订单列表(后台)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
    })
    @PostMapping("/query")
    public ResponseResult<PageVo<OrderSimpleListVo>> query(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                           @RequestBody TBusOrderDto orderDto) {
        return ResultUtil.success(orderHeadService.getSimpleOrderList(current, size, orderDto));
    }
    @ApiOperation("订单详情")
    @GetMapping("/get")
    public ResponseResult<OrderDetailSimpleVo> getTBusOrderHead(@RequestParam(value = "orderId") Integer orderId) {
        return ResultUtil.success(orderHeadService.getOrderDetailSimple(orderId));
    }
    @ApiOperation("excel导出")
    @PostMapping("/exportorder")
    public void download(@RequestParam(value = "current", defaultValue = "0") Integer current,
                         @RequestParam(value = "size", defaultValue = "10") Integer size, @RequestBody TBusOrderDto tBusOrderDto, HttpServletResponse response) throws IOException {
        orderHeadExcelService.download(current, size, tBusOrderDto, response);
    }
}
