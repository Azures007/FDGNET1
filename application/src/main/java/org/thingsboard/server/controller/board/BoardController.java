package org.thingsboard.server.controller.board;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.board.BoardService;
import org.thingsboard.server.dao.mes.dto.DeviceRunBoardDto;
import org.thingsboard.server.dao.mes.vo.BoardDevice;
import org.thingsboard.server.dao.mes.vo.DeviceRunBoardVo;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/board")
@Api(value = "大屏看板接口", tags = "大屏看板接口")
public class BoardController extends BaseController {

    @Autowired
    BoardService boardService;

//    @ApiOperation("内包机速度折线图数据")
    @PostMapping("/lineSellp")
    public ResponseResult<List<BoardDevice>> lineSellp(@ApiParam(name = "byDate",value = "日期 格式：yyyy-MM-dd",required = true)
                                        @RequestParam("byDate") String byDate){
        List<BoardDevice> boardDevices=boardService.lineSellp(byDate);
        return ResultUtil.success();
    }

    @ApiOperation("设备运行报表")
    @PostMapping("/deviceRunBoard")
    public ResponseResult<List<DeviceRunBoardVo>> deviceRunBoard(@RequestBody DeviceRunBoardDto deviceRunBoardDto) throws ParseException {
        List<DeviceRunBoardVo> deviceRunBoardVos=boardService.deviceRunBoard(deviceRunBoardDto);
        return ResultUtil.success(deviceRunBoardVos);
    }



}
