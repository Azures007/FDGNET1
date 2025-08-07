package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.TSysCodeDscDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;
import java.util.List;

@Api(value = "字典接口", tags = "字典接口")
@RequestMapping("/api/tSysCodeDsc")
@RestController
public class TSysCodeDscController extends BaseController {

    @ApiOperation("查询字典组信息")
    @GetMapping("/getGroupCode")
    public ResponseResult getGroupCode() throws Exception {
        List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscService.tSysCodeDscGroupList();
        return ResultUtil.success(tSysCodeDscList);
    }

    @ApiOperation("查询字典列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)

    })
    @PostMapping("/codeList")
    public ResponseResult<PageVo<TSysCodeDsc>> codeList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                        @RequestBody TSysCodeDscDto tSysCodeDscDto) {
//        if (tSysCodeDscDto.getCodeClId()!=null){
//            Page<TSysCodeDsc> classList = tSysCodeDscService.tSysClassList(current,size,tSysCodeDscDto);
//            return ResultUtil.success(classList);
//        }else{
//            return ResultUtil.error("字典分类编码不能为空");
//        }
        Page<TSysCodeDsc> classList = tSysCodeDscService.tSysCodeDscList(current, size, tSysCodeDscDto);
        PageVo<TSysCodeDsc> pageVo = new PageVo<>(classList);
        return ResultUtil.success(pageVo);

    }

    @ApiOperation("根据字典分类编码获取字典列表")
    @GetMapping("/getCodeByCodeCl")
    public ResponseResult<PageVo<TSysCodeDsc>> getCodeByCodeCl(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                               @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                               @RequestParam("codeClId") String codeClId,
                                                               @RequestParam(value = "enabledSt", defaultValue = "1") String enabledSt) throws Exception {
        Page<TSysCodeDsc> tSysCodeDscList = tSysCodeDscService.getCodeByCodeCl(current, size, codeClId, enabledSt);
        PageVo<TSysCodeDsc> pageVo = new PageVo<>(tSysCodeDscList);
        return ResultUtil.success(pageVo);

    }

    @ApiOperation("根据字典分类编码获取字典列表（不过滤启停状态）")
    @GetMapping("/getCodeByCodeClNotJudEt")
    public ResponseResult<PageVo<TSysCodeDsc>> getCodeByCodeClNotJudEt(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                               @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                               @RequestParam("codeClId") String codeClId) throws Exception {
        Page<TSysCodeDsc> tSysCodeDscList = tSysCodeDscService.getCodeByCodeClNotJudEt(current, size, codeClId);
        PageVo<TSysCodeDsc> pageVo = new PageVo<>(tSysCodeDscList);
        return ResultUtil.success(pageVo);

    }

    @ApiOperation("保存/修改字典分类信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveCodeCl")
    public ResponseResult saveCodeCl(@RequestBody TSysCodeDsc tSysCodeDsc) throws Exception {
        if ("".equals(tSysCodeDsc.getCodeValue()) || tSysCodeDsc.getCodeValue() == null) {
            return ResultUtil.error("编码不能为空值或空字符串");
        }
        SecurityUser currentUser = getCurrentUser();
        tSysCodeDsc.setUpdateUser(currentUser.getName());
        tSysCodeDsc.setUpdateTime(new Date());
        tSysCodeDscService.saveCodeCl(tSysCodeDsc);
        return ResultUtil.success();

    }

    @ApiOperation("保存/修改字典信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveCode")
    public ResponseResult saveCode(@RequestBody TSysCodeDsc tSysCodeDsc) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tSysCodeDsc.setUpdateUser(currentUser.getName());
        tSysCodeDsc.setUpdateTime(new Date());
        tSysCodeDscService.saveCode(tSysCodeDsc);
        return ResultUtil.success();
    }

    @ApiOperation("删除字典")
    @GetMapping("/deleteTSysCodeDsc")
    public ResponseResult deleteTSysCodeDsc(@RequestParam("codeId") Integer codeId) {
        tSysCodeDscService.deleteCode(codeId);
        return ResultUtil.success();
    }


    @ApiOperation("根据ID查询明细")
    @GetMapping("/getCodeById")
    public ResponseResult<TSysCodeDsc> getCodeById(@RequestParam("codeId") Integer codeId) {
        TSysCodeDsc tSysCodeDsc = tSysCodeDscService.getCodeById(codeId);
        if (tSysCodeDsc == null) {
            return ResultUtil.error("未找到该数据");
        } else {
            return ResultUtil.success(tSysCodeDsc);
        }

    }

    @ApiOperation("禁用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "codeId", value = "代码ID", required = true),
            @ApiImplicitParam(name = "enabledSt", value = "禁用标识 0：禁用 1：启用", required = true)
    })
    @GetMapping("/isEnabled")
    public ResponseResult isEnabled(@RequestParam("codeId") Integer codeId, @RequestParam("enabledSt") Integer enabledSt) throws Exception {
        TSysCodeDsc tSysCodeDsc = tSysCodeDscService.getCodeById(codeId);
        tSysCodeDsc.setEnabledSt(enabledSt == 1 ? GlobalConstant.enableTrue: GlobalConstant.enableFalse);
        this.saveCode(tSysCodeDsc);
        return ResultUtil.success();
    }



    //========================以下是历史版本控制接口（非常规）==========


//    @ApiOperation("根据字典分类编码及版本号获取字典列表")
//    @GetMapping("/getCodeByCodeClAndVersion")
//    public ResponseResult<PageVo<TSysCodeDsc>> getCodeByCodeClAndVersion(@RequestParam(value = "current", defaultValue = "0") Integer current,
//                                                               @RequestParam(value = "size", defaultValue = "10") Integer size,
//                                                               @RequestParam("codeClId") String codeClId,
//                                                               @RequestParam("versionNo") String versionNo,
//                                                               @RequestParam(value = "enabledSt", defaultValue = "1") String enabledSt) throws Exception {
//        //根据版本号从t_sys_code_dsc_version表获取数据，如果没有找到则去t_sys_code_dsc字典表获取最新版本
//        Page<TSysCodeDsc> tSysCodeDscVersionList = tSysCodeDscVersionService.getCodeByCodeCl(current, size, codeClId,versionNo, enabledSt);
//        PageVo<TSysCodeDsc> pageVo = new PageVo<>(tSysCodeDscVersionList);
//        if (tSysCodeDscVersionList.getContent().size()==0){
//
//            Page<TSysCodeDsc> tSysCodeDscList = tSysCodeDscService.getCodeByCodeCl(current, size, codeClId, enabledSt);
//            pageVo = new PageVo<>(tSysCodeDscList);
//
//        }
//        return ResultUtil.success(pageVo);
//    }

}
