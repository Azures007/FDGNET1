package org.thingsboard.server.controller.board;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.board.BoardService;
import org.thingsboard.server.dao.mes.dto.DeviceRunBoardDto;
import org.thingsboard.server.dao.mes.vo.*;
import org.thingsboard.server.service.TSysDevice.TSysDeviceExcelService;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/board")
@Api(value = "大屏看板接口", tags = "大屏看板接口")
public class BoardController extends BaseController {



    @Autowired
    TSysDeviceExcelService downloadService;

    @ApiOperation("内包机速度折线图数据(当天)")
    @GetMapping("/lineSellp")
    public ResponseResult<List<BoardDataDevice>> lineSellp(@RequestParam("deviceCode") String deviceCode) {
        List<BoardDataDevice> boardDevices = boardService.lineSellp(deviceCode, "速度","long");
        return ResultUtil.success(boardDevices);
    }

    @ApiOperation("内包机产量分析折线图（当天）")
    @GetMapping("/lineCl")
    public ResponseResult<List<LineClVo>> lineCl(@RequestParam("deviceCodes") List<String> deviceCodes) {
        List<LineClVo> lineClVos = boardService.lineCl(deviceCodes, "包装件数");
        return ResultUtil.success(lineClVos);
    }

    @ApiOperation("监测告警（当天）")
    @GetMapping("/listYj")
    public ResponseResult<ListYjVo> listYj() {
        ListYjVo listYjVo = boardService.listYj();
        return ResultUtil.success(listYjVo);
    }

    @GetMapping("/listTemp")
    @ApiOperation("车间温度检测（当天）")
    public ResponseResult<List<ListDeviceData>> listTemp(@RequestParam("deviceCodes") List<String> deviceCodes) {
        List<ListDeviceData> listDeviceDatas = new ArrayList<>();
        ListDeviceData listDeviceData;
        for (String deviceCode : deviceCodes) {
            List<BoardDataDevice> boardDevices = boardService.lineSellp(deviceCode, "温度","dbl");
            listDeviceData = ListDeviceData.builder()
                    .deviceCode(deviceCode)
                    .boardDataDeviceList(boardDevices)
                    .build();
            listDeviceDatas.add(listDeviceData);
        }
        return ResultUtil.success(listDeviceDatas);
    }

    @GetMapping("/listHemp")
    @ApiOperation("车间湿度检测（当天）")
    public ResponseResult<List<ListDeviceData>> listHemp(@RequestParam("deviceCodes") List<String> deviceCodes) {
        List<ListDeviceData> listDeviceDatas = new ArrayList<>();
        ListDeviceData listDeviceData;
        for (String deviceCode : deviceCodes) {
            List<BoardDataDevice> boardDevices = boardService.lineSellp(deviceCode, "湿度","dbl");
            listDeviceData = ListDeviceData.builder()
                    .deviceCode(deviceCode)
                    .boardDataDeviceList(boardDevices)
                    .build();
            listDeviceDatas.add(listDeviceData);
        }
        return ResultUtil.success(listDeviceDatas);
    }


    @ApiOperation("设备运行报表")
    @PostMapping("/deviceRunBoard")
    public ResponseResult<List<DeviceRunBoardTypeVo>> deviceRunBoard(@RequestBody DeviceRunBoardDto deviceRunBoardDto) throws ParseException {
        List<DeviceRunBoardTypeVo> deviceRunBoardVos = boardService.deviceRunBoard(deviceRunBoardDto);
        return ResultUtil.success(deviceRunBoardVos);
    }

    @ApiOperation("导出设备运行报表")
    @PostMapping("/exportDeviceRunBoard")
    public ResponseResult exportDeviceRunBoard(@RequestBody DeviceRunBoardDto deviceRunBoardDto,
                                               HttpServletResponse response) throws ParseException {
        List<DeviceRunBoardTypeVo> deviceRunBoardTypeVoList = boardService.deviceRunBoard(deviceRunBoardDto);
        //("内包机列表集合")
        List<InsourcingDeviceRunVo> insourcingDeviceRunVoList = new ArrayList<>();

        //("烤炉列表集合")
        List<OvenDeviceRunVo> ovenDeviceRunVos = new ArrayList<>();

        //("温湿度列表集合")
        List<TanSensorDeviceRunVo> tanSensorDeviceRunVos = new ArrayList<>();
        if (deviceRunBoardTypeVoList != null && deviceRunBoardTypeVoList.size() > 0) {
            if (deviceRunBoardTypeVoList != null && deviceRunBoardTypeVoList.size() > 0) {
                for (DeviceRunBoardTypeVo deviceRunBoardTypeVo : deviceRunBoardTypeVoList) {
                    if (deviceRunBoardTypeVo.getInsourcingDeviceRunVoList() != null
                            && deviceRunBoardTypeVo.getInsourcingDeviceRunVoList().size() > 0) {
                        List<InsourcingDeviceRunVo> insourcingDeviceRunVoList1 = deviceRunBoardTypeVo.getInsourcingDeviceRunVoList();
                        for (InsourcingDeviceRunVo insourcingDeviceRunVo : insourcingDeviceRunVoList1) {
                            insourcingDeviceRunVo.setRunSeund(insourcingDeviceRunVo.getRunSeund().divide(new BigDecimal("3600"), 1, RoundingMode.HALF_UP));

                        }
                        insourcingDeviceRunVoList.addAll(insourcingDeviceRunVoList1);
                    }
                    if (deviceRunBoardTypeVo.getOvenDeviceRunVos() != null
                            && deviceRunBoardTypeVo.getOvenDeviceRunVos().size() > 0) {
                        List<OvenDeviceRunVo> ovenDeviceRunVos1 = deviceRunBoardTypeVo.getOvenDeviceRunVos();
                        for (OvenDeviceRunVo ovenDeviceRunVo : ovenDeviceRunVos1) {
                            ovenDeviceRunVo.setRunSeund(ovenDeviceRunVo.getRunSeund().divide(new BigDecimal("3600"), 1, RoundingMode.HALF_UP));
                            ovenDeviceRunVo.setAvgSpeedExport(ovenDeviceRunVo.getAvgSpeed()+" " );
                            ovenDeviceRunVo.setAvgTempExport(ovenDeviceRunVo.getAvgTemp()+" ");
                            ovenDeviceRunVo.setTempSuccessExport(ovenDeviceRunVo.getTempSuccess() + "%");
                            ovenDeviceRunVo.setAvgHotWindExport(ovenDeviceRunVo.getAvgHotWind() + " ");
                        }
                        ovenDeviceRunVos.addAll(ovenDeviceRunVos1);
                    }
                    if (deviceRunBoardTypeVo.getTanSensorDeviceRunVos() != null
                            && deviceRunBoardTypeVo.getTanSensorDeviceRunVos().size() > 0) {
                        List<TanSensorDeviceRunVo> tanSensorDeviceRunVos1 = deviceRunBoardTypeVo.getTanSensorDeviceRunVos();
                        for (TanSensorDeviceRunVo tanSensorDeviceRunVo : tanSensorDeviceRunVos1) {
                            tanSensorDeviceRunVo.setRunSeund(tanSensorDeviceRunVo.getRunSeund().divide(new BigDecimal("3600"), 1, RoundingMode.HALF_UP));
                            tanSensorDeviceRunVo.setAvgHempExport(tanSensorDeviceRunVo.getAvgHemp() + " ");
                            tanSensorDeviceRunVo.setHempSuccessExport(tanSensorDeviceRunVo.getHempSuccess() + "%");
                            tanSensorDeviceRunVo.setTempSuccessExport(tanSensorDeviceRunVo.getTempSuccess() + "%");
                        }
                        tanSensorDeviceRunVos.addAll(tanSensorDeviceRunVos1);
                    }
                }
            }

        }

        downloadService.exportDeviceRunBoard(response, insourcingDeviceRunVoList, tanSensorDeviceRunVos, ovenDeviceRunVos);

        return ResultUtil.success();
    }


}
