package org.thingsboard.server.dao.mes.board;

import org.thingsboard.server.dao.mes.dto.DeviceRunBoardDto;
import org.thingsboard.server.dao.mes.dto.IotDeviceDto;
import org.thingsboard.server.dao.mes.vo.*;

import java.text.ParseException;
import java.util.List;

public interface BoardService {
    /**
     * 内包机速度折线图数据
     * @return
     */
    List<BoardDataDevice> lineSellp(String deviceCode,String key,String type);

    /**
     * 设备运行报表
     * @param deviceRunBoardDto
     * @return
     */
    List<DeviceRunBoardTypeVo> deviceRunBoard(DeviceRunBoardDto deviceRunBoardDto) throws ParseException;

    /**
     * 内包机产量分析折线图（当天）
     * @param deviceCodes
     * @return
     */
    List<LineClVo> lineCl(List<String> deviceCodes,String key);

    /**
     * 监测告警（当天）列表
     * @return
     */
    ListYjVo listYj();

    /**
     * 设备iot信息
     * @param deviceType
     * @return
     */
    List<ListDeviceIotVo> listDeviceIot(String deviceType);

    /**
     * 温度趋势分析折线图
     * @param deviceCode
     * @return
     */
    List<ListDeviceTempDatsVo> listDeviceTempDats(String deviceCode,String type);

    /**
     * 告警信息获取（当天）
     * @param deviceCode
     * @return
     */
    List<BoardDataDevice> getErrorDatas(String deviceCode);

    /**
     * 数采报表---烤炉
     * @param current
     * @param size
     * @param iotDeviceDto
     * @return
     */
    PageVo<IotDeviceAndOvenVo> listIotDeviceAndOven(Integer current, Integer size, IotDeviceDto iotDeviceDto);
}
