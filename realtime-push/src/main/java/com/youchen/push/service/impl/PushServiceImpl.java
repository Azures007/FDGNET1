package com.youchen.push.service.impl;

import com.youchen.push.PushMessage;
import com.youchen.push.SessionRegistry;
import com.youchen.push.service.PushService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PushServiceImpl implements PushService {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void pushToGroup(PushMessage message, String base, String line, Integer clazz) {
        String groupKey = buildGroupKey(base, line, clazz);
        ChannelGroup group = SessionRegistry.getGroup(groupKey);
        try {
            group.writeAndFlush(new io.netty.handler.codec.http.websocketx.TextWebSocketFrame(MAPPER.writeValueAsString(message)));
            log.info("Pushed message to group {}", groupKey);
        } catch (JsonProcessingException e) {
            log.error("Serialize push message error", e);
        }
    }

    @Override
    public String buildGroupKey(String base, String line, Integer clazz) {
        return String.join(":", base == null ? "*" : base, line == null ? "*" : line, clazz == null ? "*" : clazz.toString());
    }
}
