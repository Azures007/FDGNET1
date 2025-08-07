package org.thingsboard.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.common.data.mes.sys.TSysUserDevices;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysPersonnelInfoDto;
import org.thingsboard.server.dao.dto.VerifyDevicesDto;
import org.thingsboard.server.dao.vo.GetPersonnelByDevicesVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.UserVo;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.tsysPersonnelExcel.TSysPersonnelExcelService;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Api(value = "人员接口", tags = "人员接口")
@RequestMapping("/api/tSysPersonnelInfo")
@RestController
public class TSysPersonnelInfoController extends BaseController {

    @Autowired
    TSysPersonnelExcelService downloadService;

    @ApiOperation("通过指纹获取人员信息")
    @PostMapping("/getPersonnelByDevices")
    public ResponseResult<TSysPersonnelInfo> getPersonnelByDevices(@RequestBody GetPersonnelByDevicesVo vo) throws ThingsboardException {
        String userId = getCurrentUser().getId().getId().toString();
        String content = vo.getContent();
        if (content.length() < 300) {
            throw new RuntimeException("当前指纹信息不合法");
        }
        TSysPersonnelInfo tSysPersonnelInfo = tSysPersonnelInfoService.getPersonnelByDevices(userId, content);
        return ResultUtil.success(tSysPersonnelInfo);
    }


    @ApiOperation("查询人员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)

    })
    @PostMapping("/personnelInfoList")
    public ResponseResult<PageVo<TSysPersonnelInfo>> personnelInfoList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                       @RequestBody TSysPersonnelInfoDto tSysPersonnelInfoDto) {
        Page<TSysPersonnelInfo> personnelList = tSysPersonnelInfoService.tSysPersonnelInfoList(current, size, tSysPersonnelInfoDto);
        PageVo<TSysPersonnelInfo> pageVo = new PageVo<>(personnelList);
        return ResultUtil.success(pageVo);
    }

    @ApiOperation("保存/修改人员信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/savePersonnelInfo")
    public ResponseResult savePersonnelInfo(@RequestBody TSysPersonnelInfo tSysPersonnelInfo) throws Exception {
        //需做校验，如果一个用户已经绑定了人员，那么不允许他绑定第二个
        Boolean flag = tSysPersonnelInfoService.judgeUserIdRepeat(tSysPersonnelInfo.getPersonnelId(), tSysPersonnelInfo.getUserId());
        if (!flag) {
            SecurityUser currentUser = getCurrentUser();
            tSysPersonnelInfo.setUpdateUser(currentUser.getName());
            tSysPersonnelInfo.setUpdateTime(new Date());
            tSysPersonnelInfoService.saveTSysPersonnelInfo(tSysPersonnelInfo);
            return ResultUtil.success();
        } else {
            throw new RuntimeException("选择的账号已被绑定至其他人员！保存失败");
        }

    }

    @ApiOperation("删除人员")
    @GetMapping("/deletePersonnelInfo")
    public ResponseResult deletePersonnelInfo(@RequestParam("personnelId") Integer personnelId) {
        tSysPersonnelInfoService.deleteTSysPersonnelInfo(personnelId);
        return ResultUtil.success();
    }

    @ApiOperation("根据ID查询明细")
    @GetMapping("/getPersonnelInfoById")
    public ResponseResult<TSysPersonnelInfo> getPersonnelInfoById(@RequestParam("personnelId") Integer personnelId) {
        TSysPersonnelInfo tSysPersonnelInfo = tSysPersonnelInfoService.getPersonnelInfoById(personnelId);
        if (tSysPersonnelInfo == null) {
            return ResultUtil.error("未找到该数据");
        } else {
            return ResultUtil.success(tSysPersonnelInfo);
        }

    }


//    @ApiOperation("下载导入模板")
//    @GetMapping("/downloadImportTemplate")
//    public void download(HttpServletResponse response){
//        // 以流的形式下载文件。
//        try {
//            //读取文件
//            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/tSysPersonnelInfoImportTemplate.xlsx");
//            InputStream fis = new BufferedInputStream(resourceAsStream);
//            byte[] buffer = new byte[fis.available()];
//            fis.read(buffer);
//            fis.close();
//            // 清空response
//            response.reset();
//            // 设置response的Header
//            response.setHeader("Content-Disposition", "attachment;filename="+java.net.URLEncoder.encode("tSysPersonnelInfoImportTemplate.xlsx", "utf-8"));
//            //response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
//            response.addHeader("Content-Length", "" + buffer.length);
//            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/octet-stream");
//            toClient.write(buffer);
//            toClient.flush();
//            toClient.close();
//        } catch (IOException e) {
////            log.info(e.getMessage());
//        }
//
//    }

    @ApiOperation("禁用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "personnelId", value = "人员ID", required = true),
            @ApiImplicitParam(name = "enabledSt", value = "禁用标识 0：禁用 1：启用", required = true)
    })
    @GetMapping("/isEnabled")
    public ResponseResult isEnabled(@RequestParam("personnelId") Integer personnelId, @RequestParam("enabledSt") Integer enabledSt) throws Exception {
        TSysPersonnelInfo tSysPersonnelInfo = tSysPersonnelInfoService.getPersonnelInfoById(personnelId);
        tSysPersonnelInfo.setEnabledSt(enabledSt == 1 ? GlobalConstant.enableTrue : GlobalConstant.enableFalse);
        this.savePersonnelInfo(tSysPersonnelInfo);
        return ResultUtil.success();
    }

    @ApiOperation("获取绑定用户的列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "nemeOrAccount", value = "用户名或账号", readOnly = false)
    })
    @PostMapping("/pageUserByNemeOrAccount")
    public ResponseResult<PageVo<UserVo>> pageUserByNemeOrAccount(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                  @RequestParam(value = "nemeOrAccount", defaultValue = "") String nemeOrAccount) throws JsonProcessingException {
        PageVo<UserVo> users = userService.pageUserByNemeOrAccount(current, size, nemeOrAccount);
        return ResultUtil.success(users);
    }

    @ApiOperation("下载导入模板")
    @GetMapping("/downloadImportTemplate")
    public ResponseResult downTemplate(HttpServletResponse response) throws Exception {
        downloadService.downTemplate(response);
        return ResultUtil.success();
    }

    @ApiOperation("导入")
    @PostMapping("/importDevices")
    public ResponseResult importDevices(@RequestParam MultipartFile file) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        downloadService.upload(file, currentUser.getName());
        return ResultUtil.success();
    }

    @ApiOperation("添加设备信息")
    @PostMapping("/addDevices")
    public ResponseResult addDevices(@RequestBody TSysUserDevices tSysUserDevices) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        tSysUserDevices.setCreatedName(currentUser.getName());
        tSysUserDevices.setCreatedTime(new Date());
        tSysUserDevices.setEnabled(GlobalConstant.enableTrue);
        tSysPersonnelInfoService.addDevices(tSysUserDevices);
        return ResultUtil.success();
    }

    @ApiOperation("验证指纹设备信息")
    @PostMapping("/verifyDevices")
    public ResponseResult<Boolean> verifyDevices(@RequestBody VerifyDevicesDto verifyDevicesDto) {
        Boolean flag = tSysPersonnelInfoService.verifyDevices(verifyDevicesDto);
        return ResultUtil.success(flag);
    }

}
