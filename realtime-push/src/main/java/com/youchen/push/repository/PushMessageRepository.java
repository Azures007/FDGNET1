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

    @Query("SELECT COUNT(m) FROM PushMessageEntity m WHERE m.userId = :userId AND m.isRead = false AND m.lineId IN :lineIds")
    int countUnreadByUserIdAndLineId(@Param("userId") String userId,@Param("lineIds") List<String> lineIds);

    List<PushMessageEntity> findByUserIdOrderByCreatedTimeDesc(String userId);

    @Query("SELECT m FROM PushMessageEntity m WHERE m.userId = :userId AND m.lineId IN :lineIds ORDER BY m.createdTime DESC")
    Page<PushMessageEntity> findByUserIdAndLineIdOrderByCreatedTimeDesc(@Param("userId") String userId,@Param("lineIds") List<String> lineIds, Pageable pageable);
    
    @Query("SELECT m FROM PushMessageEntity m WHERE m.userId = :userId AND m.isRead = :isRead AND m.lineId IN :lineIds ORDER BY m.createdTime DESC")
    Page<PushMessageEntity> findByUserIdAndIsReadAndLineIdOrderByCreatedTimeDesc(@Param("userId") String userId, @Param("isRead") Boolean isRead,@Param("lineIds") List<String> lineIds, Pageable pageable);

    @Query("SELECT m FROM PushMessageEntity m WHERE m.userId = :userId AND m.isPush = :isPush AND m.lineId IN :lineIds ORDER BY m.createdTime DESC")
    Page<PushMessageEntity> findByUserIdAndIsPushAndLineIdOrderByCreatedTimeDesc(@Param("userId") String userId, @Param("isPush") Boolean isPush,@Param("lineIds") List<String> lineIds, Pageable pageable);
    
    @Query("SELECT m FROM PushMessageEntity m WHERE m.userId = :userId AND m.isRead = :isRead AND m.isPush = :isPush AND m.lineId IN :lineIds ORDER BY m.createdTime DESC")
    Page<PushMessageEntity> findByUserIdAndIsReadAndIsPushAndLineIdOrderByCreatedTimeDesc(@Param("userId") String userId, @Param("isRead") Boolean isRead, @Param("isPush") Boolean isPush, @Param("lineIds") List<String> lineIds, Pageable pageable);
    
    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isPush = true WHERE m.userId = :userId AND m.isPush = false AND m.lineId IN :lineIds")
    int markAllPushedByUserIdAndLineId(@Param("userId") String userId, @Param("lineIds") List<String> lineIds);
    
    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isRead = true WHERE m.userId = :userId AND m.isRead = false AND m.lineId IN :lineIds")
    int markAllReadByUserIdAndLineId(@Param("userId") String userId,@Param("lineIds") List<String> lineIds);

    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isRead = true WHERE m.userId = :userId AND m.type= :type AND m.isRead = false AND m.lineId IN :lineIds")
    int markAllReadByUserIdAndTypeAndLineId(@Param("userId") String userId, @Param("type") String type,@Param("lineIds") List<String> lineIds);

    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isRead = true WHERE m.userId = :userId AND m.id= :id AND m.isRead = false")
    int markReadByUserIdAndId(@Param("userId") String userId, @Param("id") Long id);
    
    @Modifying
    @Query("UPDATE PushMessageEntity m SET m.isPush = true WHERE m.userId = :userId AND m.id = :id AND m.isPush = false")
    int markPushByUserIdAndId(@Param("userId") String userId, @Param("id") Long id);
}
