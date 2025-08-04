package org.thingsboard.server.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TSysCraftProcessRel;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.TSysCraftinfo.TSysCraftInfoService;
import org.thingsboard.server.dao.dto.ListMaterialDto;
import org.thingsboard.server.dao.dto.TSysCraftInfoSaveDto;
import org.thingsboard.server.dao.dto.TSysCraftSearchDto;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: l
 * @Date: 2022/4/21 15:06
 * @Description:工艺路线
 */
@Api(value = "工艺路线管理",tags = "工艺路线管理")
@RequestMapping("/api/craft")
@RestController
public class TSysCraftInfoController extends BaseController{
    @Autowired
    TSysCraftInfoService tSysCraftInfoService;
    @ApiOperation("保存/修改工艺路线信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/save")
    public ResponseResult save(@RequestBody TSysCraftInfoSaveDto craftInfoSaveDto) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        craftInfoSaveDto.setUpdatedUser(currentUser.getName());
        craftInfoSaveDto.setUpdatedTime(new Date());
        tSysCraftInfoService.save(craftInfoSaveDto);
        return ResultUtil.success();
    }

    @ApiOperation("工艺路线列表")
    @PostMapping("/list")
    public ResponseResult<PageVo<TSysCraftInfoSaveDto>> list(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                             @RequestParam(value = "size",defaultValue = "10") Integer size, @RequestBody TSysCraftSearchDto searchDto) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        PageVo<TSysCraftInfoSaveDto> list=tSysCraftInfoService.list(currentUser.getId().toString(),current,size,searchDto);
        return ResultUtil.success(list);
    }

    @ApiOperation("工艺路线详情")
    @PostMapping("/detail")
    public ResponseResult<TSysCraftInfoSaveDto> detail(@RequestParam Integer craftId) throws Exception {
        TSysCraftInfoSaveDto craftDetail=tSysCraftInfoService.detail(craftId);
        return ResultUtil.success(craftDetail);
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public ResponseResult delete(@RequestParam Integer craftId) throws Exception {
        tSysCraftInfoService.delete(craftId);
        return ResultUtil.success();
    }

    @ApiOperation("启用/禁用 0：禁用 1：启用")
    @GetMapping("/enable")
    public ResponseResult enable(@RequestParam(value = "craftId") Integer craftId,@ApiParam(value = "1：启用 0：禁用") @RequestParam(value = "enable") Integer enable) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tSysCraftInfoService.enable(craftId,enable,currentUser.getName());
        return ResultUtil.success();
    }

    @ApiOperation("物料绑定工艺路线")
    @PostMapping("/setMaterial")
    public ResponseResult setMaterial(@RequestParam Integer craftId,@RequestBody List<Map> materialCodes) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        return tSysCraftInfoService.setMaterial(craftId,materialCodes,currentUser.getName());
    }

    @ApiOperation("物料列表")
    @PostMapping("/materialList")
    public ResponseResult materialList(@RequestParam(value = "craftId") Integer craftId,@RequestParam(value = "current", defaultValue = "0") Integer current,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                                       @RequestBody ListMaterialDto listMaterialDto) throws Exception {
        return tSysCraftInfoService.materialList(craftId,current,size,listMaterialDto);
    }

    @ApiOperation("获取工艺路线的工序关系信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "craftId", value = "工艺路线id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "processId", value = "工序id", required = true, dataTypeClass = Integer.class)
    })
    @GetMapping("/findByCraftIdAndProcessId")
    public ResponseResult<TSysCraftProcessRel> isBindEnabled(@RequestParam("craftId") Integer craftId,
                                                             @RequestParam("processId") Integer processId) {
        TSysCraftProcessRel tSysCraftProcessRel = tSysCraftInfoService.findByCraftIdAndProcessId(craftId, processId);
        return ResultUtil.success(tSysCraftProcessRel);
    }

}
