package com.youchen.push.service;

import com.youchen.push.PushMessage;

public interface PushService {
    void pushToGroup(PushMessage message, String base, String line, Integer clazz);
    String buildGroupKey(String base, String line, Integer clazz);
}
