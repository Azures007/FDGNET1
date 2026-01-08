package org.thingsboard.server.dao.mes.board;

import org.thingsboard.server.dao.mes.dto.DeviceRunBoardDto;
import org.thingsboard.server.dao.mes.vo.BoardDevice;
import org.thingsboard.server.dao.mes.vo.DeviceRunBoardVo;

import java.text.ParseException;
import java.util.List;

public interface BoardService {
    /**
     * 内包机速度折线图数据
     * @param byDate
     * @return
     */
    List<BoardDevice> lineSellp(String byDate);

    /**
     * 设备运行报表
     * @param deviceRunBoardDto
     * @return
     */
    List<DeviceRunBoardVo> deviceRunBoard(DeviceRunBoardDto deviceRunBoardDto) throws ParseException;
}
