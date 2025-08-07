package org.thingsboard.server.controller.ncApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.ncDepartment.NcDepartment;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.ncDepartment.NcDepartmentService;

import java.util.List;

@RestController
@RequestMapping("/outapi/nc/department")
@Api(tags = "NC生产部门接口")
public class NcDepartmentController {
    @Autowired
    private NcDepartmentService service;

    @ApiOperation("批量新增/更新生产部门")
    @PostMapping("/addbatch")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "同步成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public ResponseResult create(@RequestBody List<NcDepartment> entitys) {
        service.saveOrUpdateBatchByCdeptid(entitys);
        return ResultUtil.success("同步成功");
    }
}
