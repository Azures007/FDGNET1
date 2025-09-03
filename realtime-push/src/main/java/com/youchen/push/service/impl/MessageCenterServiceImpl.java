package com.youchen.push.service.impl;

import com.youchen.push.PushMessage;
import com.youchen.push.SessionRegistry;
import com.youchen.push.dto.MessageItem;
import com.youchen.push.dto.OrderNotificationDetail;
import com.youchen.push.dto.ReviewNotificationDetail;
import com.youchen.push.entity.OrderNotificationEntity;
import com.youchen.push.entity.PushMessageEntity;
import com.youchen.push.entity.ReviewNotificationEntity;
import com.youchen.push.repository.OrderNotificationRepository;
import com.youchen.push.repository.PushMessageRepository;
import com.youchen.push.repository.ReviewNotificationRepository;
import com.youchen.push.repository.UserClassRepository;
import com.youchen.push.service.MessageCenterService;
import com.youchen.push.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageCenterServiceImpl implements MessageCenterService {

    private final PushMessageRepository pushMessageRepository;
    private final OrderNotificationRepository orderNotificationRepository;
    private final ReviewNotificationRepository reviewNotificationRepository;
    private final PushService pushService;
    private final UserClassRepository userClassRepository;

    @Override
    public void appendForGroup(PushMessage message, String base, String line, Integer classId) {
        // 优先根据班别ID查询用户并逐个追加消息
        if (classId != null) {
            try {
                for (String uid : userClassRepository.findUserIdsByClassId(classId)) {
                    appendForUser(uid, message);
                }
                return;
            } catch (NumberFormatException ignored) {
                // fall back to session registry route
            }
        }
        // 回退：推送到当前连接在该组的在线用户,这个只有在线用户才记录消息，不符合需求
        /*SessionRegistry.getGroup(pushService.buildGroupKey(base, line, classId)).forEach(ch -> {
            String userId = SessionRegistry.getUserId(ch);
            if (userId != null) {
                appendForUser(userId, message);
            }
        });*/
    }

    

    @Override
    public void appendForUser(String userId, PushMessage msg) {
        PushMessageEntity entity = new PushMessageEntity();
        entity.setUserId(userId);
        entity.setType(msg.getType());
        entity.setTitle(msg.getTitle());
        entity.setBaseId(msg.getBaseId());
        entity.setLineId(msg.getLineId());
        entity.setClassId(msg.getClassId());
        entity.setIsRead(false);
        
        // 根据消息类型设置关联ID
        if (msg.getOrderNotificationId() != null) {
            entity.setOrderNotificationId(msg.getOrderNotificationId());
        }
        if (msg.getReviewNotificationId() != null) {
            entity.setReviewNotificationId(msg.getReviewNotificationId());
        }
        
        pushMessageRepository.save(entity);
    }

    @Override
    public int unreadCount(String userId) {
        return pushMessageRepository.countUnreadByUserId(userId);
    }

    @Override
    public List<MessageItem> list(String userId) {
        return pushMessageRepository.findByUserIdOrderByCreatedTimeDesc(userId)
                .stream()
                .map(this::toMessageItem)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAllRead(String userId) {
        pushMessageRepository.markAllReadByUserId(userId);
    }

    private MessageItem toMessageItem(PushMessageEntity entity) {
        MessageItem item = new MessageItem();
        item.setId(entity.getId());
        item.setType(entity.getType());
        item.setTitle(entity.getTitle());
        item.setTs(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        item.setRead(entity.getIsRead());
        
        // 加载订单通知详情
        if (entity.getOrderNotificationId() != null) {
            OrderNotificationEntity orderEntity = orderNotificationRepository.findById(entity.getOrderNotificationId()).orElse(null);
            if (orderEntity != null) {
                OrderNotificationDetail detail = new OrderNotificationDetail();
                detail.setOrderId(orderEntity.getOrderId());
                detail.setOrderNo(orderEntity.getOrderNo());
                detail.setOrderProcessId(orderEntity.getOrderProcessId());
                detail.setProductName(orderEntity.getProductName());
                detail.setEstimatedOutput(orderEntity.getEstimatedOutput());
                detail.setSpecification(orderEntity.getSpecification());
                detail.setPlannedStartTime(orderEntity.getPlannedStartTime() != null ? orderEntity.getPlannedStartTime().toString() : null);
                detail.setPlannedCompletionTime(orderEntity.getPlannedCompletionTime() != null ? orderEntity.getPlannedCompletionTime().toString() : null);
                item.setOrderDetail(detail);
            }
        }
        
        // 加载复核通知详情
        if (entity.getReviewNotificationId() != null) {
            ReviewNotificationEntity reviewEntity = reviewNotificationRepository.findById(entity.getReviewNotificationId()).orElse(null);
            if (reviewEntity != null) {
                ReviewNotificationDetail detail = new ReviewNotificationDetail();
                detail.setDocNo(reviewEntity.getDocNo());
                detail.setProductName(reviewEntity.getProductName());
                detail.setChecker(reviewEntity.getChecker());
                detail.setInspectionTime(reviewEntity.getInspectionTime() != null ? reviewEntity.getInspectionTime().toString() : null);
                item.setReviewDetail(detail);
            }
        }
        
        return item;
    }
}
