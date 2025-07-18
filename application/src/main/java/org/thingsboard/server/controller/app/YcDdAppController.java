package org.thingsboard.server.controller.app;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.TSysPdRecord;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.order.AppOrderTaskService;
import org.thingsboard.server.dao.vo.OrderDetailSimpleVo;
import org.thingsboard.server.dao.vo.OrderDetailVo;

import java.util.List;

@RestController
@RequestMapping("/api/app/dd")
@Api(value = "app订单模块", tags = "app订单模块")
public class YcDdAppController extends BaseController {

    @ApiOperation("订单详情")
    @GetMapping("/getDetailInfo")
    public ResponseResult<OrderDetailSimpleVo> getDetailInfo(@RequestParam(value = "orderId") Integer orderId) {
        return ResultUtil.success(appOrderTaskService.getAppOrderDetailSimple(orderId));
    }

}
