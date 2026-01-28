package org.thingsboard.server.controller.board;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.vo.BoardDataDevice;
import org.thingsboard.server.dao.mes.vo.LineClVo;
import org.thingsboard.server.dao.mes.vo.ListDeviceIotVo;
import org.thingsboard.server.dao.mes.vo.ListDeviceTempDatsVo;

import java.util.List;

@RestController
@RequestMapping("/api/noauth/board/oven")
@Api(value = "大屏看板烤炉接口", tags = "大屏看板烤炉接口")
public class BoardOvenController extends BaseController {

    @ApiOperation("设备详情列表")
    @GetMapping("/listDeviceIot")
    @ApiImplicitParams(@ApiImplicitParam(name = "deviceType",value = "设备类型：Oven：烤炉 TANSensor：温湿度仪 OuterPack：外包机 Insourcing：内包机"))
    public ResponseResult<List<ListDeviceIotVo>> listDeviceIot(@RequestParam("deviceType") String deviceType){
        List<ListDeviceIotVo> listDeviceIotVos=boardService.listDeviceIot(deviceType);
        return ResultUtil.success(listDeviceIotVos);
    }

    @GetMapping("/listDeviceTempDats")
    @ApiOperation("温度趋势分析")
    public ResponseResult<List<ListDeviceTempDatsVo>> listDeviceTempDats(@RequestParam("deviceCode") String deviceCode,
                                                                         @RequestParam("deviceType") String deviceType){
        List<ListDeviceTempDatsVo> listDeviceTempDatsVos=boardService.listDeviceTempDats(deviceCode,deviceType);
        return ResultUtil.success(listDeviceTempDatsVos);
    }

    @GetMapping("/listDeviceTempDatsOrUpDown")
    @ApiOperation("温度趋势分析（上下分开）")
    public ResponseResult<List<BoardDataDevice>> listDeviceTempDatsOrUpDown(@RequestParam("deviceCode") String deviceCode,
                                                                         @RequestParam("deviceType") String deviceType){
        List<BoardDataDevice> boardDataDevices = boardService.lineSellp(deviceCode, deviceType,"dbl");
        return ResultUtil.success(boardDataDevices);
    }

    @ApiOperation("烤炉速度折线图数据(当天)")
    @GetMapping("/lineSellp")
    public ResponseResult<List<BoardDataDevice>> lineSellp(@RequestParam("deviceCode") String deviceCode) {
        List<BoardDataDevice> boardDevices = boardService.lineSellp(deviceCode, "速度","long");
        return ResultUtil.success(boardDevices);
    }

    @ApiOperation("烤炉热风分析折线图（当天）")
    @GetMapping("/lineRf")
    public ResponseResult<List<BoardDataDevice>> lineCl(@RequestParam("deviceCode") String deviceCode) {
        List<BoardDataDevice> boardDevices = boardService.lineSellp(deviceCode, "热风频率","long");
        return ResultUtil.success(boardDevices);
    }

    @ApiOperation("告警信息获取（当天）")
    @GetMapping("/getErrorDatas")
    public ResponseResult<List<BoardDataDevice>> getErrorDatas(@RequestParam("deviceCode") String deviceCode){
        List<BoardDataDevice> boardDataDevices=boardService.getErrorDatas(deviceCode);
        return ResultUtil.success(boardDataDevices);
    }



}
