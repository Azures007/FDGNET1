package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.mes.sys.TSysClassPersonnelRel;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.ImportParam.TSysClassImportParam;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.TSysClassDto;
import org.thingsboard.server.dao.mes.vo.ClassGroupLeaderRelVo;
import org.thingsboard.server.dao.mes.vo.ClassPersinnelRelVo;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.service.TSysClass.TSysClassExcelService;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@Api(value = "班别接口", tags = "班别接口")
@RequestMapping("/api/class")
@RestController
public class TSysClassController extends BaseController {

    @Autowired
    TSysClassExcelService downloadService;

    @ApiOperation("查询班别列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)

    })
    @PostMapping("/classList")
    public ResponseResult<PageVo<TSysClass>> classList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                       @RequestBody TSysClassDto tSysClassDto) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        Page<TSysClass> classList = tSysClassService.tSysClassList(currentUser.getId().toString(),current, size, tSysClassDto);
        PageVo<TSysClass> pageVo = new PageVo<>(classList);
        return ResultUtil.success(pageVo);
    }

    @ApiOperation("excel导出")
    @PostMapping("/export")
    public void download(@RequestParam(value = "current",defaultValue = "0") Integer current,
                         @RequestParam(value = "size",defaultValue = "10") Integer size,
                         @RequestBody TSysClassDto classDto, HttpServletResponse response) throws IOException {
        downloadService.download(current,size,classDto,response);
    }

//    public ResponseResult saveClass(@RequestBody TSysClass tSysClass) throws Exception {
//        SecurityUser currentUser = getCurrentUser();
//        tSysClass.setUpdateUser(currentUser.getName());
//        tSysClass.setUpdateTime(new Date());
//        tSysClassService.saveTSysClass(tSysClass);
//        return ResultUtil.success();
//    }

    @ApiOperation("保存/修改班别信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveClass")
    public ResponseResult saveClass(@RequestBody TSysClassImportParam tSysClassImportParam) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        TSysClass tSysClass = tSysClassImportParam.gettSysClass();
        tSysClass.setUpdateUser(currentUser.getName());
        tSysClass.setUpdateTime(new Date());
        tSysClassService.saveTSysClassAndGroupLeaderRel(tSysClass, tSysClassImportParam.getTSysClassGroupLeaderRelLits());
        return ResultUtil.success();
    }

    @ApiOperation("删除班别")
    @GetMapping("/deleteClass")
    public ResponseResult deleteClass(@RequestParam("classId") Integer classId) {
        tSysClassService.deleteTSysClass(classId);
        return ResultUtil.success();
    }

    @ApiOperation("根据ID查询明细")
    @GetMapping("/getClassById")
    public ResponseResult<TSysClass> getClassById(@RequestParam("classId") Integer classId) {
        TSysClass tSysClass = tSysClassService.getClassById(classId);
        if (tSysClass == null) {
            return ResultUtil.error("未找到该数据");
        } else {
            return ResultUtil.success(tSysClass);
        }

    }

    @ApiOperation("禁用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classId", value = "班别ID", required = true),
            @ApiImplicitParam(name = "enabledSt", value = "禁用标识 0：禁用 1：启用", required = true)
    })
    @GetMapping("/isEnabled")
    public ResponseResult isEnabled(@RequestParam("classId") Integer classId, @RequestParam("enabledSt") Integer enabledSt) throws Exception {
        TSysClass tSysClass = tSysClassService.getClassById(classId);
        tSysClass.setEnabledSt(enabledSt == 1 ? GlobalConstant.enableTrue : GlobalConstant.enableFalse);
//        this.saveClass(tSysClass);
        SecurityUser currentUser = getCurrentUser();
        tSysClass.setUpdateUser(currentUser.getName());
        tSysClass.setUpdateTime(new Date());
        tSysClassService.saveTSysClass(tSysClass);
        return ResultUtil.success();
    }

    @ApiOperation("保存组员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classId", value = "班别ID", required = true),
            @ApiImplicitParam(name = "personnelList", value = "人员列表", required = true)
    })
    @PostMapping("/saveTSysClassPersonnelRel")
    public ResponseResult saveTSysClassPersonnelRel(@RequestParam(value = "classId") Integer classId,
                                                    @RequestBody List<TSysClassPersonnelRel> personnelList
    ) throws Exception {
        tSysClassService.saveTSysClassPersonnelRel(classId, personnelList);
        return ResultUtil.success();
    }

    @ApiOperation("获取班组下分配的人员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classId", value = "班别ID", required = true)
    })
    @GetMapping("/getRelByClassId")
    public ResponseResult<List<ClassPersinnelRelVo>> getRelByClassId(@RequestParam("classId") Integer classId) throws Exception {
        List<ClassPersinnelRelVo> list = tSysClassService.getRelByClassId(classId);
        return ResultUtil.success(list);
    }

    @ApiOperation("获取班组下分配的组长列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classId", value = "班别ID", required = true)
    })
    @GetMapping("/getGroupLeaderRelByClassId")
    public ResponseResult<List<ClassGroupLeaderRelVo>> getGroupLeaderRelByClassId(@RequestParam("classId") Integer classId) throws Exception {
        List<ClassGroupLeaderRelVo> list = tSysClassService.getGroupLeaderRelByClassId(classId);
        return ResultUtil.success(list);
    }


}
