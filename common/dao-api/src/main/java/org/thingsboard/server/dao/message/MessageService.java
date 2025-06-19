package org.thingsboard.server.dao.message;

import org.thingsboard.server.common.data.TSysMessageOrder;
import org.thingsboard.server.dao.vo.MessageSizeVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.ProcessAlarmVo;
import org.thingsboard.server.dao.vo.ProcessEndVo;

import java.util.List;

public interface MessageService {
    /**
     * 消息列表
     *
     * @param current
     * @param size
     * @param userId
     * @return
     */
    PageVo<TSysMessageOrder> listMessage(Integer current, Integer size, String userId) throws Exception;

    /**
     * 未读消息列表
     * @param userId
     * @return
     */
    MessageSizeVo getMessageSizeVo(String userId);

    /**
     * 发送消息接口
     * @param userId
     * @param messageVo
     */
    void chatMessage(String userId, TSysMessageOrder messageVo);

    /**
     * 工序结束未操作提醒消息接口
     * @param userId
     * @return
     */
    List<ProcessEndVo> getProcessEndMessage(String userId);

    /**
     * 工序结束未操作提醒消息该订单下次不再提醒接口
     * @param userId
     * @param orderNo
     */
    void setProcessEndMessage(String userId, String orderNo);

    List<ProcessAlarmVo> alarmProcessStartAndEnd(String orderNo, Integer orderProcessId, String userId);

    void setProcessAlarmMessage(Integer orderProcessId);
}
