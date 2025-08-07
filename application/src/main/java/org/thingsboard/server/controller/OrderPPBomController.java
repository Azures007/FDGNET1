package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPPBom;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.TBusOrderPPBomDto;
import org.thingsboard.server.service.security.model.SecurityUser;


@Api(value = "订单用料清单接口",tags = "订单用料清单接口")
@RequestMapping("/api/orderppbom")
@RestController
public class OrderPPBomController extends BaseController {


    @ApiOperation("查询订单用料清单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "页码(默认第0页,页码从0开始)",readOnly = false),
            @ApiImplicitParam(name = "size",value = "数量(默认10条)",readOnly = false)
    })
    @PostMapping("/list")
    public ResponseResult<Page<TBusOrderPPBom>> classList(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                     @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                     @RequestBody TBusOrderPPBomDto tBusOrderPPBomDto) {
        Page<TBusOrderPPBom> classList = orderPPBomService.tBusOrderPPBomList(current,size,tBusOrderPPBomDto);
        return ResultUtil.success(classList);
    }

    @ApiOperation("保存/修改订单用料清单（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/save")
    public ResponseResult saveClass(@RequestBody TBusOrderPPBom tBusOrderPPBom) throws Exception {
        SecurityUser currentUser = getCurrentUser();
//        tBusOrderPPBom.setUpdateUser(currentUser.getName());
//        tBusOrderPPBom.setUpdateTime(new Date());
        orderPPBomService.saveTBusOrderPPBom(tBusOrderPPBom);
        return ResultUtil.success();
    }

    @ApiOperation("删除订单用料清单")
    @GetMapping("/delete")
    public ResponseResult deleteClass(@RequestParam("orderPPBomId") Integer orderPPBomId){
        orderPPBomService.deleteTBusOrderPPBom(orderPPBomId);
        return ResultUtil.success();
    }
}
