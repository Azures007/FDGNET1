package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.mid.MidDept;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.dto.MidDeptDto;
import org.thingsboard.server.dao.midDetp.MidDeptService;
import org.thingsboard.server.dao.vo.PageVo;

@RestController
@Api(value = "车间接口",tags = "车间接口")
@RequestMapping("/api/midDept")
public class MidDeptController extends BaseController{

    @Autowired
    MidDeptService midDeptService;

    @ApiOperation("车间列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)

    })
    @PostMapping("/listMidDept")
    public ResponseResult<PageVo<MidDept>> listMessage(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                       @RequestBody MidDeptDto midDeptDto) throws Exception {
        PageVo<MidDept> pageVo = midDeptService.listMidDept(current,size,midDeptDto);
        return ResultUtil.success(pageVo);
    }



}
