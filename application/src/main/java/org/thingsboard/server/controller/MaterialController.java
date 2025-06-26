package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TSyncMaterial;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.ListMaterialDto;
import org.thingsboard.server.dao.dto.TSyncMaterialSaveDto;
import org.thingsboard.server.dao.vo.ListMaterialFiterVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.TSyncMaterialVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;

@RestController
@RequestMapping("/api/material")
@Api(value = "物料基础信息接口", tags = "物料基础信息接口")
public class MaterialController extends BaseController {

    @ApiOperation("物料基础信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", required = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", required = false)

    })
    @PostMapping("/listMaterial")
    public ResponseResult<PageVo<TSyncMaterialVo>> listMaterial(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                @RequestBody ListMaterialDto listMaterialDto) {
        PageVo<TSyncMaterialVo> pageVo = materialService.listMaterial(current, size, listMaterialDto);
        return ResultUtil.success(pageVo);
    }

    // 查询物料详情
    @ApiOperation("物料详情")
    @GetMapping("/getById")
    public ResponseResult<TSyncMaterialVo> getById(@RequestParam("id") Integer id) {
        TSyncMaterialVo tSyncMaterialVo = materialService.getByIdWithBoms(id);
        return ResultUtil.success(tSyncMaterialVo);
    }

    @ApiOperation("修改/新增（通过物料id区分），包含bom")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody TSyncMaterialSaveDto tSyncMaterialSaveDto) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        tSyncMaterialSaveDto.setUpdatedName(currentUser.getName());
        tSyncMaterialSaveDto.setUpdatedTime(new Date());
        materialService.update(tSyncMaterialSaveDto);
        return ResultUtil.success();
    }

    @ApiOperation("禁用/启用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true),
            @ApiImplicitParam(name = "enabled", value = "禁用标识 0：不可用 1：可用", required = true)
    })
    @GetMapping("/isEnabled")
    public ResponseResult isEnabled(@RequestParam("id") Integer id, @RequestParam("enabled") Integer enabled) throws Exception {
        TSyncMaterial tSyncMaterial = materialService.getById(id);
        tSyncMaterial.setMaterialStatus(enabled == 0 ? GlobalConstant.enableFalse : GlobalConstant.enableTrue);
        // 更新
        SecurityUser currentUser = getCurrentUser();
        tSyncMaterial.setUpdatedName(currentUser.getName());
        tSyncMaterial.setUpdatedTime(new Date());
        materialService.update(tSyncMaterial);
        return ResultUtil.success();
    }

    @ApiOperation("删除接口")
    @GetMapping("/delete")
    public ResponseResult delete(@RequestParam("id") Integer id) {
        materialService.delete(id);
        return ResultUtil.success();
    }

    @ApiOperation("同步")
    @GetMapping("/sync")
    public ResponseResult sync() throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        materialService.sync(currentUser.getName());
        return ResultUtil.success();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "materialCode", value = "物料代码过滤条件", required = false),
            @ApiImplicitParam(name = "craftId", value = "工艺路线id", required = true),
            @ApiImplicitParam(name = "kdOrgId", value = "生产组织id", required = true),
            @ApiImplicitParam(name = "kdDeptId", value = "生产车间id", required = true),
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", required = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", required = false)
    })
    @ApiOperation("主产品列表过滤")
    @GetMapping("/listMaterialFiter")
    public ResponseResult<ListMaterialFiterVo> listMaterialFiter(@RequestParam(value = "materialCode",required = false) String materialCode,
                                                                 @RequestParam("craftId") Integer craftId,
                                                                 @RequestParam("kdOrgId") Integer kdOrgId,
                                                                 @RequestParam("kdDeptId") Integer kdDeptId,
                                                                 @RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                 @RequestParam(value = "size", defaultValue = "10") Integer size){
        ListMaterialFiterVo listMaterialFiterVo=materialService.listMaterialFiter(materialCode,craftId, kdOrgId, kdDeptId,current,size);
        return ResultUtil.success(listMaterialFiterVo);
    }


}
