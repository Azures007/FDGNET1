package org.thingsboard.server.controller.ncApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.nc_order.NcTBusOrderHead;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.nc_order.NcTBusOrderHeadService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/outapi/nc/order")
@Api(tags = "NC订单接口")
public class NcTBusOrderHeadController {
    @Autowired
    private NcTBusOrderHeadService service;
    @ApiOperation("新增/更新订单")
    @PostMapping("/add")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "同步成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public ResponseResult create(@RequestBody NcTBusOrderHead entity) {
        service.updateByCmoid(entity.getCmoid(), entity);
        return ResultUtil.success();
    }
    @ApiOperation("(批量)新增/更新订单")
    @PostMapping("/addbatch")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "同步成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public ResponseResult create(@RequestBody List<NcTBusOrderHead> entitys) {
        service.updateByCmoidBatch(entitys);
        return ResultUtil.success();
    }

}
