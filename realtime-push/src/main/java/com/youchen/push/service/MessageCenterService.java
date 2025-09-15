package com.youchen.push.service;

import com.youchen.push.PushMessage;
import com.youchen.push.dto.MessageItem;
import org.thingsboard.server.dao.mes.vo.PageVo;

import java.util.List;

public interface MessageCenterService {

    void appendForGroup(PushMessage message, String base, String line, Integer clazz);
    void appendForUser(String userId, PushMessage msg);
    int unreadCount(String userId);
    PageVo<MessageItem> list(String userId, Integer current, Integer size, String readStatus,String pushStatus);
    void markAllRead(String userId);
    void markReadByType(String userId,String msgType);

    void markReadById(String userId, Long id);
}
