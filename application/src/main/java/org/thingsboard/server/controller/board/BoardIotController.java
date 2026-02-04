package org.thingsboard.server.controller.board;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.dto.IotDeviceDto;
import org.thingsboard.server.dao.mes.vo.IotDeviceAndOvenVo;
import org.thingsboard.server.dao.mes.vo.IotDeviceAndTANSVo;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.service.TSysDevice.TSysDeviceExcelService;

import javax.servlet.http.HttpServletResponse;
import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping("/api/noauth/board/byiots")
@Api(value = "设备数采相关接口", tags = "设备数采相关接口")
public class BoardIotController extends BaseController {

    @Autowired
    TSysDeviceExcelService downloadService;

    @ApiOperation("设备数采报表---烤炉")
    @PostMapping("/listIotDeviceAndOven")
    public ResponseResult<PageVo<IotDeviceAndOvenVo>> listIotDeviceAndOven(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                           @RequestBody IotDeviceDto iotDeviceDto){
        PageVo<IotDeviceAndOvenVo> pageVo=boardService.listIotDeviceAndOven(current,size,iotDeviceDto);
        return ResultUtil.success(pageVo);
    }


    @ApiOperation("设备数采报表---烤炉(导出)")
    @PostMapping("/exportIotDeviceAndOven")
    public ResponseResult exportIotDeviceAndOven(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                 @RequestBody IotDeviceDto iotDeviceDto,
                                                 HttpServletResponse response){
        PageVo<IotDeviceAndOvenVo> pageVo=boardService.listIotDeviceAndOven(current,size,iotDeviceDto);
        List<IotDeviceAndOvenVo> list = pageVo.getList();
        downloadService.exportIotDeviceAndOven(response,list);
        return ResultUtil.success();
    }


    @ApiOperation("设备数采报表---温湿仪")
    @PostMapping("/listIotDeviceAndTANS")
    public ResponseResult<PageVo<IotDeviceAndTANSVo>> listIotDeviceAndTANS(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                           @RequestBody IotDeviceDto iotDeviceDto){
        PageVo<IotDeviceAndTANSVo> pageVo=boardService.listIotDeviceAndTANS(current,size,iotDeviceDto);
        return ResultUtil.success(pageVo);
    }


    @ApiOperation("设备数采报表---温湿仪")
    @PostMapping("/exportIotDeviceAndTANS")
    public ResponseResult exportIotDeviceAndTANS(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                           @RequestBody IotDeviceDto iotDeviceDto,
                                               HttpServletResponse response){
        PageVo<IotDeviceAndTANSVo> pageVo=boardService.listIotDeviceAndTANS(current,size,iotDeviceDto);
        List<IotDeviceAndTANSVo> list = pageVo.getList();
        downloadService.exportIotDeviceAndTANS(response,list);
        return ResultUtil.success();
    }


}
