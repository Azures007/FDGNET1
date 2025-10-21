package org.thingsboard.server.controller.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.ncWorkline.NcWorklineService;
import org.thingsboard.server.service.security.model.SecurityUser;

@RestController
@RequestMapping("/api/manage/workline")
@Api(tags = "NC生产线管理接口")
public class YcWorklineManageController extends BaseController {
    @Autowired
    private NcWorklineService service;

    @ApiOperation("查询登录基地所有有权限的生产线")
    @GetMapping("/list")
    public ResponseResult listAll() throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        return ResultUtil.success(service.findAll(user.getId().toString()));
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
}
