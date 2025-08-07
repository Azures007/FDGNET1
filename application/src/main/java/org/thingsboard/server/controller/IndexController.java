package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.vo.GetOrderSizeVo;
import org.thingsboard.server.dao.mes.vo.UserClassVo;
import org.thingsboard.server.service.security.model.SecurityUser;
@RestController
@Api(value = "首页接口",tags = "首页接口")
@RequestMapping("/api/index")
public class IndexController extends BaseController {

    @ApiOperation("获取当前用户班别信息")
    @GetMapping("/getUserClass")
    public ResponseResult<UserClassVo> getUserClass() throws Exception{
        SecurityUser currentUser = getCurrentUser();
        UserClassVo userClass= tSysClassService.getUserClass(currentUser.getId().getId().toString());
        return ResultUtil.success(userClass);
    }

    @ApiOperation("获取当前用户订单信息")
    @GetMapping("/getOrderSize")
    public ResponseResult<GetOrderSizeVo> getOrderSize() throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        GetOrderSizeVo getOrderSizeVo= appOrderTaskService.getOrderSize(currentUser.getId().getId().toString());
        return ResultUtil.success(getOrderSizeVo);
    }

}
