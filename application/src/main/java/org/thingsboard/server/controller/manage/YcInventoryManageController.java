package org.thingsboard.server.controller.manage;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.nc_inventory.NcInventory;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.nc_inventory.NcInventoryService;
import org.thingsboard.server.dao.vo.PageVo;

public class YcInventoryManageController {
    @Autowired
    private NcInventoryService service;

    @ApiOperation("分页模糊查询库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseName", value = "仓库名称，模糊查询"),
            @ApiImplicitParam(name = "materialName", value = "物料名称，模糊查询"),
            @ApiImplicitParam(name = "spec", value = "规格，模糊查询"),
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)"),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)")
    })
    @GetMapping("/query")
    public ResponseResult query(
            @RequestParam("warehouseName") String warehouseName,
            @RequestParam("materialName") String materialName,
            @RequestParam("spec") String spec,
            @RequestParam(value = "current", defaultValue = "0") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        PageVo<NcInventory> classList = service.queryInventory(warehouseName, materialName, spec, current, size);
        return ResultUtil.success(classList);
    }
}
