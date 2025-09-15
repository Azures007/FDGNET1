package com.youchen.push.service.impl;

import com.youchen.push.PushMessage;
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
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thingsboard.server.dao.user.UserService;

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
    private final UserService userService;
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
        entity.setIsPush(false);
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
        String cwkid =userService.getUserCurrentCwkid(userId);//登录的产线
        return pushMessageRepository.countUnreadByUserIdAndLineId(userId,cwkid);
    }

    @Override
    @Transactional
    public PageVo<MessageItem> list(String userId, Integer current, Integer size, String readStatus, String pushStatus) {
        Pageable pageable = PageRequest.of(Math.max(0, current == null ? 0 : current), Math.max(1, size == null ? 10 : size));
        Page<PushMessageEntity> page;
        String cwkid = userService.getUserCurrentCwkid(userId);//登录的产线

        // 根据readStatus和pushStatus参数决定查询条件
        Boolean isRead = null;
        Boolean isPush = null;
        
        if ("unread".equalsIgnoreCase(readStatus)) {
            isRead = false;
        } else if ("read".equalsIgnoreCase(readStatus)) {
            isRead = true;
        }
        
        if ("unpush".equalsIgnoreCase(pushStatus)) {
            isPush = false;
        } else if ("push".equalsIgnoreCase(pushStatus)) {
            isPush = true;
        }
        
        // 根据组合条件查询
        if (isRead != null && isPush != null) {
            page = pushMessageRepository.findByUserIdAndIsReadAndIsPushAndLineIdOrderByCreatedTimeDesc(userId, isRead, isPush, cwkid, pageable);
        } else if (isRead != null) {
            page = pushMessageRepository.findByUserIdAndIsReadAndLineIdOrderByCreatedTimeDesc(userId, isRead, cwkid, pageable);
        } else if (isPush != null) {
            page = pushMessageRepository.findByUserIdAndIsPushAndLineIdOrderByCreatedTimeDesc(userId, isPush, cwkid, pageable);
        } else {
            // 查询全部消息（默认或"all"）
            page = pushMessageRepository.findByUserIdAndLineIdOrderByCreatedTimeDesc(userId, cwkid, pageable);
        }
        
        List<MessageItem> items = page.getContent().stream().map(this::toMessageItem).collect(Collectors.toList());
        
        // 标记返回的消息为已推送
        if (!items.isEmpty()) {
            List<Long> messageIds = items.stream().map(MessageItem::getId).collect(Collectors.toList());
            markMessagesAsPushed(userId, messageIds, cwkid);
        }
        
        PageVo<MessageItem> pageVo = new PageVo<>();
        pageVo.setList(items);
        pageVo.setCurrent(current);
        pageVo.setSize(size);
        pageVo.setTotal((int) page.getTotalElements());
        return pageVo;
    }

    @Override
    @Transactional
    public void markAllRead(String userId) {
        String cwkid =userService.getUserCurrentCwkid(userId);//登录的产线
        pushMessageRepository.markAllReadByUserIdAndLineId(userId,cwkid);
    }

    @Override
    @Transactional
    public void markReadByType(String userId, String msgType) {
        String cwkid =userService.getUserCurrentCwkid(userId);//登录的产线
        pushMessageRepository.markAllReadByUserIdAndTypeAndLineId(userId, msgType,cwkid);
    }

    @Override
    @Transactional
    public void markReadById(String userId, Long id) {
        pushMessageRepository.markReadByUserIdAndId(userId,id);
    }
    
    /**
     * 标记指定消息为已推送
     */
    @Transactional
    public void markMessagesAsPushed(String userId, List<Long> messageIds, String lineId) {
        if (messageIds != null && !messageIds.isEmpty()) {
            for (Long messageId : messageIds) {
                pushMessageRepository.markPushByUserIdAndId(userId, messageId);
            }
        }
    }

    private MessageItem toMessageItem(PushMessageEntity entity) {
        MessageItem item = new MessageItem();
        item.setId(entity.getId());
        item.setType(entity.getType());
        item.setTitle(entity.getTitle());
        item.setTs(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        item.setRead(entity.getIsRead());
        item.setPush(entity.getIsPush());
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
                detail.setUnit(orderEntity.getUnit());
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
