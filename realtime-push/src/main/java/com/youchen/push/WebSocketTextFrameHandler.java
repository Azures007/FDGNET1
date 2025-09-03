package com.youchen.push;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youchen.push.service.PushService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final PushService pushService;

    public WebSocketTextFrameHandler(PushService pushService) {
        this.pushService = pushService;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("ws handlerAdded: {}", ctx.channel().id());
        SessionRegistry.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        SessionRegistry.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        log.info("ws text received: {}", text);
        if ("ping".equalsIgnoreCase(text)) {
            ctx.writeAndFlush(new TextWebSocketFrame("pong"));
            return;
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(text);
            String action = node.path("action").asText();
            if ("bind".equalsIgnoreCase(action)) {
                String userId = firstNonEmpty(node, "userId", "UserId", "uid");
                String base = firstNonEmpty(node, "base", "baseId", "base_id");
                String line = firstNonEmpty(node, "line", "lineId", "line_id");
                Integer clazz = Integer.valueOf(firstNonEmpty(node, "clazz", "classId", "class", "class_id"));
                if (isEmpty(userId)) {
                    log.warn("bind failed: missing userId in message: {}", text);
                    ctx.writeAndFlush(new TextWebSocketFrame("bind_err:missing_userId"));
                    return;
                }
                SessionRegistry.bindUser(ctx.channel(), userId);
                SessionRegistry.bindToGroup(ctx.channel(), pushService.buildGroupKey(base, line, clazz));
                log.info("bind ok, userId={}, base={}, line={}, clazz={}", userId, base, line, clazz);
                ctx.writeAndFlush(new TextWebSocketFrame("bind_ok"));
            }
        } catch (Exception e) {
            log.warn("Invalid message: {}", text);
        }
    }

    private static boolean isEmpty(String v) {
        return v == null || v.isEmpty();
    }

    private static String firstNonEmpty(JsonNode node, String... keys) {
        for (String k : keys) {
            JsonNode v = node.get(k);
            if (v != null && !v.isNull()) {
                String s = v.asText();
                if (s != null && !s.isEmpty()) {
                    return s;
                }
            }
        }
        return null;
    }
}


