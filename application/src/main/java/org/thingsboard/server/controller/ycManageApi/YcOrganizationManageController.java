package org.thingsboard.server.controller.ycManageApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.nc_org.NcOrganizationService;

@RestController
@RequestMapping("/api/manage/org")
@Api(tags = "NC基地管理接口")
public class YcOrganizationManageController {
    @Autowired
    private NcOrganizationService service;

    @ApiOperation("查询所有基地")
    @GetMapping("/list")
    public ResponseResult listAll() {
        return ResultUtil.success(service.findAll());
    }
}
