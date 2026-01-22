package org.thingsboard.server.dao.mes.board;

import org.thingsboard.server.dao.mes.dto.DeviceRunBoardDto;
import org.thingsboard.server.dao.mes.vo.BoardDataDevice;
import org.thingsboard.server.dao.mes.vo.DeviceRunBoardTypeVo;
import org.thingsboard.server.dao.mes.vo.LineClVo;
import org.thingsboard.server.dao.mes.vo.ListYjVo;

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
}
