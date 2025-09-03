package com.youchen.push;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {
    private static final ChannelGroup ALL = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final Map<String, ChannelGroup> groupKeyToChannels = new ConcurrentHashMap<>();
    private static final Map<String, ChannelGroup> userIdToChannels = new ConcurrentHashMap<>();

    public static final AttributeKey<String> ATTR_USER_ID = AttributeKey.valueOf("USER_ID");
    public static final AttributeKey<String> ATTR_GROUP_KEY = AttributeKey.valueOf("GROUP_KEY");

    public static void add(Channel channel) {
        ALL.add(channel);
    }

    public static void remove(Channel channel) {
        ALL.remove(channel);
        groupKeyToChannels.values().forEach(g -> g.remove(channel));
        userIdToChannels.values().forEach(g -> g.remove(channel));
    }

    public static void bindUser(Channel channel, String userId) {
        channel.attr(ATTR_USER_ID).set(userId);
        userIdToChannels.computeIfAbsent(userId, k -> new DefaultChannelGroup(GlobalEventExecutor.INSTANCE))
                .add(channel);
    }

    public static void bindToGroup(Channel channel, String groupKey) {
        channel.attr(ATTR_GROUP_KEY).set(groupKey);
        groupKeyToChannels.computeIfAbsent(groupKey, k -> new DefaultChannelGroup(GlobalEventExecutor.INSTANCE))
                .add(channel);
    }

    public static ChannelGroup getGroup(String groupKey) {
        return groupKeyToChannels.getOrDefault(groupKey, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    }

    public static ChannelGroup getUserChannels(String userId) {
        return userIdToChannels.getOrDefault(userId, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    }

    public static String getGroupKey(Channel channel) {
        return channel.attr(ATTR_GROUP_KEY).get();
    }

    public static String getUserId(Channel channel) {
        return channel.attr(ATTR_USER_ID).get();
    }

    public static ChannelGroup all() {
        return ALL;
    }
}


