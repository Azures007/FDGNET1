package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.tSysCodeDsc.TSysCodeDscService;
import org.thingsboard.server.dao.vo.AppVersionVo;

/**
 * @Auther: l
 * @Date: 2022/7/22 17:54
 * @Description:
 */
@RestController
@RequestMapping("/api/app")
@Api(value = "app版本管理",tags = "app版本管理")
public class APPVersionController {
    @Autowired
    TSysCodeDscService tSysCodeDscService;
    @ApiOperation("app版本信息")
    @PostMapping("/detail")
    public ResponseResult<AppVersionVo> detail(){
        return tSysCodeDscService.getAPPVersion();
    }
}
