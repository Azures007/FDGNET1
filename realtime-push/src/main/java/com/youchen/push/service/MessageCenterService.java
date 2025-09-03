package com.youchen.push.service;

import com.youchen.push.PushMessage;
import com.youchen.push.dto.MessageItem;

import java.util.List;

public interface MessageCenterService {

    void appendForGroup(PushMessage message, String base, String line, Integer clazz);
    void appendForUser(String userId, PushMessage msg);
    int unreadCount(String userId);
    List<MessageItem> list(String userId);
    void markAllRead(String userId);
}
