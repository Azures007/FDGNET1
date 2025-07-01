package org.thingsboard.server.controller.ncManageApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.nc_org.NcOrganization;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.nc_org.NcOrganizationService;

import java.util.List;

@RestController
@RequestMapping("/manageapi/nc/org")
@Api(tags = "NC基地管理接口")
public class NcOrganizationManageController {
    @Autowired
    private NcOrganizationService service;

    @ApiOperation("查询所有基地")
    @GetMapping("/list")
    public ResponseResult listAll() {
        return ResultUtil.success(service.findAll());
    }
} 