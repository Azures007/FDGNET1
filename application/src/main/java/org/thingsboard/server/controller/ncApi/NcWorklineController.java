package org.thingsboard.server.controller.ncApi;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.nc_workline.NcWorkline;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.nc_workline.NcWorklineService;

import java.util.List;

@RestController
@RequestMapping("/outapi/nc/workline")
@Api(tags = "NC生产线接口")
public class NcWorklineController {
    @Autowired
    private NcWorklineService service;

    @ApiOperation("批量新增/更新生产线")
    @PostMapping("/addbatch")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "同步成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public ResponseResult create(@RequestBody List<NcWorkline> entitys) {
        service.saveOrUpdateBatchByCwkid(entitys);
        return ResultUtil.success("同步成功");
    }
}
