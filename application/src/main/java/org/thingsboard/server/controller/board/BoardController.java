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
    BoardService boardService;

    @Autowired
    TSysDeviceExcelService downloadService;

    //    @ApiOperation("内包机速度折线图数据")
    @PostMapping("/lineSellp")
    public ResponseResult<List<BoardDevice>> lineSellp(@ApiParam(name = "byDate", value = "日期 格式：yyyy-MM-dd", required = true)
                                                       @RequestParam("byDate") String byDate) {
        List<BoardDevice> boardDevices = boardService.lineSellp(byDate);
        return ResultUtil.success();
    }

    @ApiOperation("设备运行报表")
    @PostMapping("/deviceRunBoard")
    public ResponseResult<List<DeviceRunBoardVo>> deviceRunBoard(@RequestBody DeviceRunBoardDto deviceRunBoardDto) throws ParseException {
        List<DeviceRunBoardVo> deviceRunBoardVos = boardService.deviceRunBoard(deviceRunBoardDto);
        return ResultUtil.success(deviceRunBoardVos);
    }

    @ApiOperation("导出设备运行报表")
    @PostMapping("/exportDeviceRunBoard")
    public ResponseResult<List<DeviceRunBoardVo>> exportDeviceRunBoard(@RequestBody DeviceRunBoardDto deviceRunBoardDto,
                                                                       HttpServletResponse response) throws ParseException {
        List<DeviceRunBoardVo> deviceRunBoardVos = boardService.deviceRunBoard(deviceRunBoardDto);
        //("内包机列表集合")
        List<InsourcingDeviceRunVo> insourcingDeviceRunVoList = new ArrayList<>();

        //("烤炉列表集合")
        List<OvenDeviceRunVo> ovenDeviceRunVos = new ArrayList<>();

        //("温湿度列表集合")
        List<TanSensorDeviceRunVo> tanSensorDeviceRunVos = new ArrayList<>();
        if (deviceRunBoardVos != null && deviceRunBoardVos.size() > 0) {
            for (DeviceRunBoardVo deviceRunBoardVo : deviceRunBoardVos) {
                List<DeviceRunBoardTypeVo> deviceRunBoardTypeVoList = deviceRunBoardVo.getDeviceRunBoardTypeVoList();
                if (deviceRunBoardTypeVoList != null && deviceRunBoardTypeVoList.size() > 0) {
                    for (DeviceRunBoardTypeVo deviceRunBoardTypeVo : deviceRunBoardTypeVoList) {
                        if (deviceRunBoardTypeVo.getInsourcingDeviceRunVoList() != null
                                && deviceRunBoardTypeVo.getInsourcingDeviceRunVoList().size() > 0) {
                            List<InsourcingDeviceRunVo> insourcingDeviceRunVoList1 = deviceRunBoardTypeVo.getInsourcingDeviceRunVoList();
                            for (InsourcingDeviceRunVo insourcingDeviceRunVo : insourcingDeviceRunVoList1) {
                                insourcingDeviceRunVo.setRunSeund(insourcingDeviceRunVo.getRunSeund().divide(new BigDecimal("3600"),1, RoundingMode.HALF_UP));
                            }
                            insourcingDeviceRunVoList.addAll(insourcingDeviceRunVoList1);
                        }
                        if (deviceRunBoardTypeVo.getOvenDeviceRunVos() != null
                                && deviceRunBoardTypeVo.getOvenDeviceRunVos().size() > 0) {
                            List<OvenDeviceRunVo> ovenDeviceRunVos1 = deviceRunBoardTypeVo.getOvenDeviceRunVos();
                            for (OvenDeviceRunVo ovenDeviceRunVo : ovenDeviceRunVos1) {
                                ovenDeviceRunVo.setRunSeund(ovenDeviceRunVo.getRunSeund().divide(new BigDecimal("3600"),1, RoundingMode.HALF_UP));

                            }
                            ovenDeviceRunVos.addAll(ovenDeviceRunVos1);
                        }
                        if (deviceRunBoardTypeVo.getTanSensorDeviceRunVos() != null
                                && deviceRunBoardTypeVo.getTanSensorDeviceRunVos().size() > 0) {
                            List<TanSensorDeviceRunVo> tanSensorDeviceRunVos1 = deviceRunBoardTypeVo.getTanSensorDeviceRunVos();
                            for (TanSensorDeviceRunVo tanSensorDeviceRunVo : tanSensorDeviceRunVos1) {
                                tanSensorDeviceRunVo.setRunSeund(tanSensorDeviceRunVo.getRunSeund().divide(new BigDecimal("3600"),1, RoundingMode.HALF_UP));
                            }
                            tanSensorDeviceRunVos.addAll(tanSensorDeviceRunVos1);
                        }
                    }
                }
            }
        }

        downloadService.exportDeviceRunBoard(response, insourcingDeviceRunVoList, tanSensorDeviceRunVos, ovenDeviceRunVos);

        return ResultUtil.success();
    }


}
