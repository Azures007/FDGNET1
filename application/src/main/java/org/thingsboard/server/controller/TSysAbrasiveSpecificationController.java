package org.thingsboard.server.controller;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.sys.TSysAbrasiveSpecification;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.dto.tSysAbrasiveSpecification.TSysAbrasiveSpecificationDto;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;

@Api(value = "磨具规格接口", tags = "磨具规格接口")
@RequestMapping("/api/TSysAbrasiveSpecification")
@RestController
public class TSysAbrasiveSpecificationController extends BaseController {

    @ApiOperation("获取列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
    })
    @PostMapping("/getList")
    public ResponseResult<PageVo<TSysAbrasiveSpecification>> getList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                     @RequestBody TSysAbrasiveSpecificationDto tSysAbrasiveSpecificationDto) {
        PageVo<TSysAbrasiveSpecification> tSysAbrasiveSpecificationPageVo = tSysAbrasiveSpecificationService.getList(current, size, tSysAbrasiveSpecificationDto);
        return ResultUtil.success(tSysAbrasiveSpecificationPageVo);
    }

    @ApiOperation("查看详情")
    @GetMapping("/getDetail")
    public ResponseResult getDetail(@ApiParam(value = "磨具id") @RequestParam("id") Integer abrasiveSpecificationId) throws Exception {
        TSysAbrasiveSpecification tSysAbrasiveSpecification = tSysAbrasiveSpecificationService.getDetail(abrasiveSpecificationId);
        return ResultUtil.success(tSysAbrasiveSpecification);
    }

    @ApiOperation("保存/修改")
    @PostMapping("/save")
    public ResponseResult save(@RequestBody TSysAbrasiveSpecification tSysAbrasiveSpecification) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tSysAbrasiveSpecification.setUpdatedName(currentUser.getName());
        tSysAbrasiveSpecification.setUpdatedTime(new Date());
        tSysAbrasiveSpecificationService.save(tSysAbrasiveSpecification);
        return ResultUtil.success();
    }

    @ApiOperation("删除")
    @GetMapping("/delete")
    public ResponseResult delete(@ApiParam(value = "磨具id") @RequestParam("id") Integer abrasiveSpecificationId,@ApiParam(value = "version") @RequestParam("version") Integer version) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tSysAbrasiveSpecificationService.delete(abrasiveSpecificationId,version, currentUser.getName());
        return ResultUtil.success();
    }

    @ApiOperation("磨具规格,状态:0:禁用, 1:启用")
    @GetMapping("/setStatus")
    public ResponseResult<String> setPickingStatus(@ApiParam(value = "磨具id") @RequestParam("id") Integer abrasiveSpecificationId, @ApiParam(value = "状态:0:禁用, 1:启用") @RequestParam("status") String abrasiveSpecificationStatus) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tSysAbrasiveSpecificationService.setPickingStatus(abrasiveSpecificationId, abrasiveSpecificationStatus, currentUser.getName());
        return ResultUtil.success();
    }

}
