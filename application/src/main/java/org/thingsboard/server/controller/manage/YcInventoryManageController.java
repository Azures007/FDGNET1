package org.thingsboard.server.controller.manage;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.nc_inventory.NcInventoryService;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.service.security.model.SecurityUser;

@Api(value = "YC库存管理", tags = "YC库存管理")
@RequestMapping("/api/manage/invetory")
@RestController
public class YcInventoryManageController extends BaseController {
    @Autowired
    private NcInventoryService service;

    @ApiOperation("分页模糊查询库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseName", value = "仓库名称，模糊查询",required = false),
            @ApiImplicitParam(name = "materialName", value = "物料名称，模糊查询",required = false),
            @ApiImplicitParam(name = "spec", value = "规格，模糊查询",required = false),
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)",required = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)",required = false)
    })
    @GetMapping("/query")
    public ResponseResult<PageVo<NcInventory>> query(
            @RequestParam(value = "warehouseName", required = false) String warehouseName,
            @RequestParam(value ="materialName", required = false) String materialName,
            @RequestParam(value ="spec", required = false) String spec,
            @RequestParam(value = "current", defaultValue = "0") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        PageVo<NcInventory> classList = service.queryInventory(currentUser.getId().toString(),warehouseName, materialName, spec, current, size);
        return ResultUtil.success(classList);
    }
}
