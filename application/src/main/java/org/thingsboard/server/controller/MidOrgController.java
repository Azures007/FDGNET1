package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.mid.MidOrg;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.MidOrgDto;
import org.thingsboard.server.dao.mes.midOrg.MidOrgService;
import org.thingsboard.server.dao.mes.vo.PageVo;

@RestController
@Api(value = "组织接口",tags = "组织接口")
@RequestMapping("/api/midOrg")
public class MidOrgController extends BaseController{

    @Autowired
    MidOrgService midOrgService;

    @ApiOperation("组织列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)

    })
    @PostMapping("/listMidOrg")
    public ResponseResult<PageVo<MidOrg>> listMessage(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                      @RequestBody MidOrgDto midOrgDto) throws Exception {
        PageVo<MidOrg> pageVo = midOrgService.listMidOrg(current,size,midOrgDto);
        return ResultUtil.success(pageVo);
    }


}
