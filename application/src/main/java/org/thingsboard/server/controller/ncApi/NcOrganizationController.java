package org.thingsboard.server.controller.ncApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.nc_org.NcOrganization;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.nc_org.NcOrganizationService;

import java.util.List;

@RestController
@RequestMapping("/outapi/nc/org")
@Api(tags = "NC基地接口")
public class NcOrganizationController {
    @Autowired
    private NcOrganizationService service;

    @ApiOperation("(批量)新增/更新基地")
    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "同步成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public ResponseResult create(@RequestBody List<NcOrganization> entitys) {
        service.saveOrUpdateBatchByPkOrg(entitys);
        return ResultUtil.success("同步成功");
    }

    @ApiOperation("批量删除基地")
    @PostMapping("/deletebatch")
    public ResponseResult deleteBatch(@RequestBody List<String> ids) {
        service.deleteBatchByIds(ids);
        return ResultUtil.success("删除成功");
    }
} 