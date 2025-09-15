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

    @Query("SELECT COUNT(m) FROM PushMessageEntity m WHERE m.userId = :userId AND m.isRead = false AND m.lineId=:lineId")
    int countUnreadByUserIdAndLineId(@Param("userId") String userId,@Param("lineId") String lineId);

    List<PushMessageEntity> findByUserIdOrderByCreatedTimeDesc(String userId);

    Page<PushMessageEntity> findByUserIdAndLineIdOrderByCreatedTimeDesc(String userId,String lineId, Pageable pageable);
    
    Page<PushMessageEntity> findByUserIdAndIsReadAndLineIdOrderByCreatedTimeDesc(String userId, Boolean isRead,String lineId, Pageable pageable);

    Page<PushMessageEntity> findByUserIdAndIsPushAndLineIdOrderByCreatedTimeDesc(String userId, Boolean isPush,String lineId, Pageable pageable);
    
    Page<PushMessageEntity> findByUserIdAndIsReadAndIsPushAndLineIdOrderByCreatedTimeDesc(String userId, Boolean isRead, Boolean isPush, String lineId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isPush = true WHERE m.userId = :userId AND m.isPush = false AND m.lineId = :lineId")
    int markAllPushedByUserIdAndLineId(@Param("userId") String userId, @Param("lineId") String lineId);
    
    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isPush = true WHERE m.userId = :userId AND m.isRead = false AND m.lineId=:lineId")
    int markAllReadByUserIdAndLineId(@Param("userId") String userId,@Param("lineId") String lineId);

    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isRead = true WHERE m.userId = :userId AND m.type= :type AND m.isRead = false AND m.lineId=:lineId")
    int markAllReadByUserIdAndTypeAndLineId(@Param("userId") String userId, @Param("type") String type,@Param("lineId") String lineId);

    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isRead = true WHERE m.userId = :userId AND m.id= :id AND m.isRead = false")
    int markReadByUserIdAndId(@Param("userId") String userId, @Param("id") Long id);
    
    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isPush = true WHERE m.userId = :userId AND m.id = :id AND m.isPush = false")
    int markPushByUserIdAndId(@Param("userId") String userId, @Param("id") Long id);
}
