package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.mes.sys.TSysDevice;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.TSysDevice.TSysDeviceService;
import org.thingsboard.server.dao.mes.dto.TSysDeviceDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.service.TSysDevice.TSysDeviceExcelService;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/4/18 16:55
 * @Description:
 */
@Api(value = "设备管理接口",tags = "设备管理接口")
@RequestMapping("/api/device")
@RestController
public class TSysDeviceController extends BaseController{
    @Autowired
    TSysDeviceService deviceService;
    @Autowired
    TSysDeviceExcelService downloadService;

    @ApiOperation("查询设备信息列表")
    @PostMapping("/getDevice")
    public ResponseResult<PageVo<TSysDevice>> getDevice(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                        @RequestParam(value = "size",defaultValue = "10") Integer size, @RequestBody TSysDeviceDto deviceDto) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        Page<TSysDevice> devices =deviceService.tSysDeviceList(currentUser.getId().toString(),current,size,deviceDto,-1);
        PageVo<TSysDevice> pageVo=new PageVo<>(devices);
        return ResultUtil.success(pageVo);
    }

    @ApiOperation("保存/修改设备信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveDevice")
   public ResponseResult saveDevice(@RequestBody TSysDevice device) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        device.setUpdatedUser(currentUser.getName());
        device.setUpdatedTime(new Date());
        return deviceService.saveTSysDevice(device);
    }

    @ApiOperation("删除设备信息")
    @GetMapping("/deleteDevice")
    public ResponseResult saveDevice(@RequestParam Integer deviceId) throws Exception {
        deviceService.deleteTSysDevice(deviceId);
        return ResultUtil.success();
    }

    @ApiOperation("启用/禁用")
    @GetMapping("/enableDevice")
    public ResponseResult enableDevice(@RequestParam(value = "deviceId") Integer deviceId,@ApiParam(value = "1：启用 0：禁用") @RequestParam(value = "enable") String enable) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        deviceService.enableDevice(deviceId,enable,currentUser.getName());
        return ResultUtil.success();
    }

    @ApiOperation("设备详情")
    @GetMapping("/getTSysDevice")
    public ResponseResult getTSysDevice(@RequestParam(value = "deviceId") Integer deviceId) throws Exception {
        TSysDevice device=deviceService.getTSysDevice(deviceId);
        return ResultUtil.success(device);
    }

//    @ApiOperation("导出")
//    @PostMapping("/exportDevices")
//    public ResponseResult export(@RequestBody List<Integer> deviceIds) throws Exception {
//        List<TsysDevice> devices=deviceService.export(deviceIds);
//        return ResultUtil.success(devices);
//    }

    @ApiOperation("excel导出")
    @PostMapping("/exportDevices")
    public void download(@RequestParam(value = "current",defaultValue = "0") Integer current,
                         @RequestParam(value = "size",defaultValue = "10") Integer size,@RequestBody TSysDeviceDto deviceDto,HttpServletResponse response) throws IOException, ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        downloadService.download(currentUser.getId().toString(),current,size,deviceDto,response);
    }

    @ApiOperation("导入")
    @PostMapping("/importDevices")
    public ResponseResult importDevices(@RequestParam MultipartFile file) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        return downloadService.upload(file,currentUser.getName());
    }

    @ApiOperation("模板下载")
    @GetMapping("/downTemplate")
    public ResponseResult downTemplate(HttpServletResponse response) throws Exception {
        downloadService.downTemplate( response);
        return ResultUtil.success();
    }
}
