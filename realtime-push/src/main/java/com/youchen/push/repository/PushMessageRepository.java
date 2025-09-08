package com.youchen.push.repository;

import com.youchen.push.entity.PushMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PushMessageRepository extends JpaRepository<PushMessageEntity, Long> {

    @Query("SELECT COUNT(m) FROM PushMessageEntity m WHERE m.userId = :userId AND m.isRead = false")
    int countUnreadByUserId(@Param("userId") String userId);

    List<PushMessageEntity> findByUserIdOrderByCreatedTimeDesc(String userId);

    Page<PushMessageEntity> findByUserIdOrderByCreatedTimeDesc(String userId, Pageable pageable);
    
    Page<PushMessageEntity> findByUserIdAndIsReadOrderByCreatedTimeDesc(String userId, Boolean isRead, Pageable pageable);

    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isRead = true WHERE m.userId = :userId AND m.isRead = false")
    int markAllReadByUserId(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isRead = true WHERE m.userId = :userId AND m.type= :type AND m.isRead = false")
    int markAllReadByUserIdAndType(@Param("userId") String userId, @Param("type") String type);

    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isRead = true WHERE m.userId = :userId AND m.id= :id AND m.isRead = false")
    int markReadByUserIdAndId(@Param("userId") String userId, @Param("id") Long id);
}
