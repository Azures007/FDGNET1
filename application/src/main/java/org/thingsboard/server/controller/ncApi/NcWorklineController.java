package org.thingsboard.server.controller.ncApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation("查询所有生产线")
    @GetMapping("/list")
    public ResponseResult listAll() {
        return ResultUtil.success(service.findAll());
    }

    @ApiOperation("通过基地ID查询生产线")
    @GetMapping("/byOrg")
    public ResponseResult listByOrg(@RequestParam String pkOrg) {
        return ResultUtil.success(service.findByPkOrg(pkOrg));
    }

    @ApiOperation("通过生产线编码或名称模糊查询生产线")
    @GetMapping("/search")
    public ResponseResult search(@RequestParam String keyword) {
        return ResultUtil.success(service.findByVwkcodeOrVwknameLike(keyword));
    }

    @ApiOperation("批量删除生产线")
    @PostMapping("/deletebatch")
    public ResponseResult deleteBatch(@RequestBody List<String> ids) {
        service.deleteBatchByIds(ids);
        return ResultUtil.success("删除成功");
    }
} 