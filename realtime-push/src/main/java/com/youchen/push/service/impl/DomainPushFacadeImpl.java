package com.youchen.push.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youchen.push.PushMessage;
import com.youchen.push.SessionRegistry;

import java.util.Arrays;
import com.youchen.push.entity.OrderNotificationEntity;
import com.youchen.push.entity.ReviewNotificationEntity;
import com.youchen.push.repository.OrderNotificationRepository;
import com.youchen.push.repository.ReviewNotificationRepository;
import com.youchen.push.service.DomainPushFacade;
import com.youchen.push.service.MessageCenterService;
import com.youchen.push.repository.UserClassRepository;
import com.youchen.push.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DomainPushFacadeImpl implements DomainPushFacade {

    private final PushService pushService;
    private final MessageCenterService messageCenterService;
    private final OrderNotificationRepository orderNotificationRepository;
    private final ReviewNotificationRepository reviewNotificationRepository;
    private final UserClassRepository userClassRepository;

    @Override
    @Transactional
    public void pushOrderToday(String base, String line, List<Integer> clazz, Integer orderId, String orderNo, String productName,
                               String estimatedOutput, String unit, String specification, LocalDateTime plannedStartTime, LocalDateTime plannedCompletionTime, Integer orderProcessId) {
        // 创建订单通知详情
        OrderNotificationEntity orderEntity = new OrderNotificationEntity();
        orderEntity.setOrderId(orderId);
        orderEntity.setOrderNo(orderNo);
        orderEntity.setProductName(productName);
        orderEntity.setEstimatedOutput(estimatedOutput);
        orderEntity.setSpecification(specification);
        orderEntity.setUnit(unit);
        orderEntity.setPlannedStartTime(plannedStartTime);
        orderEntity.setPlannedCompletionTime(plannedCompletionTime);
        orderEntity.setOrderProcessId(orderProcessId);
        orderEntity = orderNotificationRepository.save(orderEntity);

        for (Integer clazzId : clazz) {
            PushMessage msg = PushMessage.builder()
                    .type("order_today")
                    .title("你有一个今日任务，可点击查看详情！")
                    .content("订单:" + orderNo + " 产品:" + productName)
                    .baseId(base).lineId(line).classId(clazzId)
                    .orderNotificationId(orderEntity.getId())
                    .build();
            pushService.pushToGroup(msg, base, line, clazzId);
            messageCenterService.appendForGroup(msg, base, line, clazzId);
        }
    }

    @Override
    @Transactional
    public void pushOrderTomorrow(String base, String line, List<Integer> clazz, Integer orderId, String orderNo, String productName,
                                 String estimatedOutput, String unit, String specification, LocalDateTime plannedStartTime, LocalDateTime plannedCompletionTime, Integer orderProcessId) {
        // 创建订单通知详情
        OrderNotificationEntity orderEntity = new OrderNotificationEntity();
        orderEntity.setOrderId(orderId);
        orderEntity.setOrderNo(orderNo);
        orderEntity.setProductName(productName);
        orderEntity.setEstimatedOutput(estimatedOutput);
        orderEntity.setSpecification(specification);
        orderEntity.setUnit(unit);
        orderEntity.setPlannedStartTime(plannedStartTime);
        orderEntity.setPlannedCompletionTime(plannedCompletionTime);
        orderEntity.setOrderProcessId(orderProcessId);
        orderEntity = orderNotificationRepository.save(orderEntity);
        for (Integer clazzId : clazz) {
            PushMessage msg = PushMessage.builder()
                    .type("order_tomorrow")
                    .title("你有一个明日任务，可点击查看详情！")
                    .content("订单:" + orderNo + " 产品:" + productName)
                    .baseId(base).lineId(line).classId(clazzId)
                    .orderNotificationId(orderEntity.getId())
                    .build();
            pushService.pushToGroup(msg, base, line, clazzId);
            messageCenterService.appendForGroup(msg, base, line, clazzId);
        }
    }

    @Override
    @Transactional
    public void pushOrderCancelled(String base, String line, List<Integer> clazz, Integer orderId, String orderNo, String productName,
                                  String estimatedOutput, String unit, String specification, LocalDateTime plannedStartTime, LocalDateTime plannedCompletionTime, String reason, Integer orderProcessId) {
        // 创建订单通知详情
        OrderNotificationEntity orderEntity = new OrderNotificationEntity();
        orderEntity.setOrderId(orderId);
        orderEntity.setOrderNo(orderNo);
        orderEntity.setProductName(productName);
        orderEntity.setEstimatedOutput(estimatedOutput);
        orderEntity.setSpecification(specification);
        orderEntity.setUnit(unit);
        orderEntity.setPlannedStartTime(plannedStartTime);
        orderEntity.setPlannedCompletionTime(plannedCompletionTime);
        orderEntity.setOrderProcessId(orderProcessId);
        orderEntity = orderNotificationRepository.save(orderEntity);

        for (Integer clazzId : clazz) {
            PushMessage msg = PushMessage.builder()
                    .type("order_cancelled")
                    .title("你有一个订单已被取消，可点击查看详情！")
                    .content("订单:" + orderNo + (reason == null ? "" : (" 原因:" + reason)))
                    .baseId(base).lineId(line).classId(clazzId)
                    .orderNotificationId(orderEntity.getId())
                    .build();
            pushService.pushToGroup(msg, base, line, clazzId);
            messageCenterService.appendForGroup(msg, base, line, clazzId);
        }
    }

    @Override
    @Transactional
    public void pushQcReview(String base, String line, Integer clazz, String docNo, String productName, String checker, LocalDateTime inspectionTime) throws JsonProcessingException {
        // 创建复核通知详情
        ReviewNotificationEntity reviewEntity = new ReviewNotificationEntity();
        reviewEntity.setDocNo(docNo);
        reviewEntity.setProductName(productName);
        reviewEntity.setChecker(checker);
        reviewEntity.setInspectionTime(inspectionTime);
        reviewEntity = reviewNotificationRepository.save(reviewEntity);
        PushMessage msg = PushMessage.builder()
                .type("qc_review")
                .title("你有一个品质管控复核通知，可点击进行复核！")
                .content("单据:" + docNo)
                .baseId(base).lineId(line).classId(clazz)
                .reviewNotificationId(reviewEntity.getId())
                .build();
        // 发送到指定基地、产线、角色(JSBM-1004)的用户
        for (String userId : userClassRepository.findUserIdsByBaseLineAndRole(base, line, "JSBM-1004")) {
            messageCenterService.appendForUser(userId, msg);
            // 同时若在线则推送
            SessionRegistry.getUserChannels(userId).writeAndFlush(new io.netty.handler.codec.http.websocketx.TextWebSocketFrame(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(msg)));
        }
    }

    @Override
    @Transactional
    public void pushQcDaily(String base, String line, Integer clazz, String docNo, String productName, String checker, LocalDateTime inspectionTime) throws JsonProcessingException {
        // 创建复核通知详情
        ReviewNotificationEntity reviewEntity = new ReviewNotificationEntity();
        reviewEntity.setDocNo(docNo);
        reviewEntity.setProductName(productName);
        reviewEntity.setChecker(checker);
        reviewEntity.setInspectionTime(inspectionTime);
        reviewEntity = reviewNotificationRepository.save(reviewEntity);
        PushMessage msg = PushMessage.builder()
                .type("qc_daily")
                .title("你有一个品管日报表复核通知，可点击进行复核！")
                .content("单据:" + docNo)
                .baseId(base).lineId(line).classId(clazz)
                .reviewNotificationId(reviewEntity.getId())
                .build();
        for (String userId : userClassRepository.findUserIdsByBaseLineAndRole(base, line, "JSBM-1004")) {
            messageCenterService.appendForUser(userId, msg);
            SessionRegistry.getUserChannels(userId).writeAndFlush(new io.netty.handler.codec.http.websocketx.TextWebSocketFrame(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(msg)));
        }
    }
}
