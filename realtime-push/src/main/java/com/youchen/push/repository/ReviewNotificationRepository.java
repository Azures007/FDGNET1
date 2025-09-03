package com.youchen.push.repository;

import com.youchen.push.entity.ReviewNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewNotificationRepository extends JpaRepository<ReviewNotificationEntity, Long> {
}
