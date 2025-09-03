package com.youchen.push.repository;

import com.youchen.push.entity.OrderNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderNotificationRepository extends JpaRepository<OrderNotificationEntity, Long> {
}
