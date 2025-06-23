package org.thingsboard.server.controller.ncApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.nc.nc_material.NcTSyncMaterial;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.nc.nc_material.NcTSyncMaterialService;

import java.util.List;

@RestController
@RequestMapping("/outapi/nc/material")
@Api(tags = "NC物料接口")
public class NcTSyncMaterialController {
    @Autowired
    private NcTSyncMaterialService ncTSyncMaterialService;

    @ApiOperation("同步物料")
    @PostMapping("/syncMaterial")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "同步成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public ResponseResult syncMaterial(@RequestBody List<NcTSyncMaterial> ncTSyncMaterials) {
        ncTSyncMaterialService.syncMaterial(ncTSyncMaterials);
        return ResultUtil.success("同步成功！");
    }

}
