package org.thingsboard.server.controller.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.ncWarehouse.NcWarehouseService;

@RestController
@RequestMapping("/api/manage/warehouse")
@Api(tags = "NC仓库管理接口")
public class YcWarehouseManageController {
    @Autowired
    private NcWarehouseService service;

    @ApiOperation("查询所有仓库")
    @GetMapping("/list")
    public ResponseResult listAll() {
        return ResultUtil.success(service.findAll());
    }

    @ApiOperation("通过基地ID查询仓库")
    @GetMapping("/byOrg")
    public ResponseResult listByOrg(String pkOrg) {
        return ResultUtil.success(service.findByPkOrg(pkOrg));
    }
}
