package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.sys.TSysRole;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.ListRoleDto;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.controller
 * @date 2022/4/7 10:01
 * @Description:
 */
@TbCoreComponent
@Api(value = "角色接口", tags = "角色接口")
@RequestMapping("/api/role")
@RestController
public class RoleController extends BaseController {

    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/test")
    public ResponseResult<String> test(){
        return ResultUtil.success("test");
    }

    @ApiOperation("查询角色列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", required = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", required = false)

    })
    @PostMapping("/listRole")
    public ResponseResult<PageVo<TSysRole>> listRole(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                     @RequestBody ListRoleDto listRoleDto) {
        Page<TSysRole> roleList = roleService.listRole(current, size, listRoleDto);
        PageVo<TSysRole> pageVo = new PageVo<>(roleList);
        return ResultUtil.success(pageVo);
    }

    @ApiOperation("保存/修改角色信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveRole")
    public ResponseResult saveRole(@RequestBody TSysRole role) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        role.setUpdatedName(currentUser.getName());
        role.setUpdatedTime(new Date());
        roleService.saveRole(role);
        return ResultUtil.success();
    }

    @ApiOperation("删除角色")
    @GetMapping("/deleteRole")
    public ResponseResult deleteRole(@RequestParam("roleId") Integer roleId) {
        roleService.deleteRole(roleId);
        return ResultUtil.success();
    }

    @ApiOperation("禁用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色Id", required = true),
            @ApiImplicitParam(name = "enabled", value = "禁用标识 0：不可用 1：可用", required = true)
    })
    @GetMapping("/isEnabled")
    public ResponseResult isEnabled(@RequestParam("roleId") Integer roleId, @RequestParam("enabled") Integer enabled) throws Exception {
        TSysRole tSysRole = roleService.getById(roleId);
        tSysRole.setEnabled(enabled == 0 ? GlobalConstant.enableFalse : GlobalConstant.enableTrue);
        this.saveRole(tSysRole);
        return ResultUtil.success();
    }

    @ApiOperation("数据权限设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色Id", required = true),
            @ApiImplicitParam(name = "byGroup", value = "是否有工序组长权限 0：有：1：无", required = true),
            @ApiImplicitParam(name = "byFactory", value = "是否有厂长/主任权限0：有 1：无", required = true)
    })
    @GetMapping("/byAuthority")
    public ResponseResult byAuthority(@RequestParam("roleId") Integer roleId,
                                      @RequestParam("byGroup") Integer byGroup,
                                      @RequestParam("byFactory") Integer byFactory) throws Exception {
        TSysRole tSysRole = roleService.getById(roleId);
        tSysRole.setByGroup(byGroup == 0 ? "0" : "1");
        tSysRole.setByFactory(byFactory == 0 ? "0" : "1");
        this.saveRole(tSysRole);
        return ResultUtil.success();
    }

}
