package org.thingsboard.server.controller;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.sys.TSysMenu;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.SetMenuDto;
import org.thingsboard.server.dao.dto.SetRoleMenuDto;
import org.thingsboard.server.dao.vo.MenuListVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.controller
 * @date 2022/4/11 16:54
 * @Description:
 */
@RestController
@Api(value = "菜单模块接口", tags = "菜单模块接口")
@RequestMapping("/api/menu")
public class MenuController extends BaseController {

    @ApiOperation("新增/修改菜单，menuId为null：新增，menuId不为null：修改")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody TSysMenu tSysMenu) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        tSysMenu.setUpdatedName(currentUser.getName());
        tSysMenu.setUpdatedTime(new Date());
        menuService.update(tSysMenu);
        return ResultUtil.success();
    }

    @ApiImplicitParam(name = "roleId", value = "角色id", readOnly = true)
    @GetMapping("/listRoleMenu")
    public ResponseResult<List<Integer>> listRoleMenu(@RequestParam("roleId") Integer roleId) {
        List<Integer> list = menuService.listRoleMenu(roleId);
        return ResultUtil.success(list);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", readOnly = true)
    })
    @ApiOperation("返回所选角色菜单集合")
    @GetMapping("/listMenu")
    public ResponseResult<MenuListVo> listMenu(@RequestParam("roleId") Integer roleId) {
        MenuListVo menuListVo = menuService.list(1, roleId);
        return ResultUtil.success(menuListVo);
    }

    @ApiOperation("返回树形式菜单列表")
    @GetMapping("/getMenuListVoByTree")
    public ResponseResult<MenuListVo> getMenuListVoByTree() {
        MenuListVo menuListVo = menuService.getMenuListVoByTree();
        return ResultUtil.success(menuListVo);
    }

    @ApiOperation("禁用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", value = "菜单Id", required = true),
            @ApiImplicitParam(name = "enabled", value = "禁用标识 0：不可用 1：可用", required = true)
    })
    @GetMapping("/isEnabled")
    public ResponseResult isEnabled(@RequestParam("menuId") Integer menuId, @RequestParam("enabled") Integer enabled) throws Exception {
        TSysMenu tSysMenu = menuService.getById(menuId);
        tSysMenu.setEnabled(enabled == 1 ? GlobalConstant.enableTrue : GlobalConstant.enableFalse);
        this.update(tSysMenu);
        return ResultUtil.success();
    }


    @PostMapping("/setRoleMenu")
//    @ApiOperation("设置角色菜单权限")
    public ResponseResult setRoleMenu(@RequestBody SetRoleMenuDto setRoleMenuDto) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        setRoleMenuDto.setCreatedName(currentUser.getName());
        setRoleMenuDto.setCreatedTime(new Date());
        menuService.setRoleMenu(setRoleMenuDto);
        return ResultUtil.success();
    }

    @PostMapping("/setMenu")
    @ApiOperation("设置角色菜单")
    public ResponseResult setMenu(@RequestBody SetMenuDto setMenuDto) throws Exception {
        List<TSysMenu> tSysMenus = setMenuDto.getMenuListVo().getTsysMenus();
        List<Integer> ids = listByMenus(tSysMenus);
        SetRoleMenuDto setRoleMenuDto = new SetRoleMenuDto();
        setRoleMenuDto.setRoleId(setMenuDto.getRoleId());
        setRoleMenuDto.setMenuIds(ids);
        setRoleMenu(setRoleMenuDto);
        return ResultUtil.success();
    }

    @PostMapping("/pageMenu")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", required = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", required = false)

    })
    @ApiOperation("菜单列表（非树结构）")
    public ResponseResult<PageVo<TSysMenu>> pageMenu(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                     @RequestBody TSysMenu tSysMenu) {
//        PageVo<TSysMenu> tSysMenuPageVo = menuService.pageMenu(current, size);
        PageVo<TSysMenu> tSysMenuPageVo = menuService.pageMenu(current, size, tSysMenu);
        return ResultUtil.success(tSysMenuPageVo);
    }

    @ApiOperation("返回当前用户报表菜单")
    @GetMapping("/listMenuByUser")
    public ResponseResult<List<TSysMenu>> listMenuByUser() throws ThingsboardException {
        String userId = getCurrentUser().getId().getId().toString();
        List<TSysMenu> tSysMenus = menuService.listMenuByUser(userId);
        return ResultUtil.success(tSysMenus);
    }
    @ApiModelProperty("删除菜单")
    @GetMapping("/delete")
    public ResponseResult delete(@RequestParam("menuId") Integer menuId){
        menuService.delete(menuId);
        return ResultUtil.success();
    }

    /**
     * 递归返回id列表
     *
     * @param tSysMenus
     * @return
     */
    private List<Integer> listByMenus(List<TSysMenu> tSysMenus) {
        List<Integer> ids = new ArrayList<>();
        for (TSysMenu tSysMenu : tSysMenus) {
            if (tSysMenu.getIsFlag().equals("0")) {
                ids.add(tSysMenu.getMenuId());
            }
            if (tSysMenu.getTSysMenus() != null) {
                recurve(tSysMenu.getTSysMenus(), ids);
            } else {
                continue;
            }
        }

        return ids;
    }

    //递归
    private void recurve(List<TSysMenu> tSysMenus, List<Integer> ids) {
        for (TSysMenu tSysMenu : tSysMenus) {
            if (tSysMenu.getTSysMenus() != null && tSysMenu.getTSysMenus().size() > 0) {
                if (tSysMenu.getIsFlag().equals("0")) {
                    ids.add(tSysMenu.getMenuId());
                }
                recurve(tSysMenu.getTSysMenus(), ids);
            } else {
                if (tSysMenu.getIsFlag().equals("0")) {
                    ids.add(tSysMenu.getMenuId());
                }
            }
        }
    }

    @ApiOperation("返回当前用户的菜单列表")
    @GetMapping("/getMyMenu")
    public ResponseResult<List<TSysMenu>> getMyMenu() throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        List<TSysMenu> menuList = menuService.getMyMenu(currentUser.getId().toString());
        return ResultUtil.success(menuList);
    }

    @ApiOperation("返回当前用户有权限的path列表")
    @GetMapping("/listPath")
    public ResponseResult<List<String>> listPath() throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        List<String> list = menuService.listPath(currentUser.getId().getId().toString());
        return ResultUtil.success(list);
    }

}
