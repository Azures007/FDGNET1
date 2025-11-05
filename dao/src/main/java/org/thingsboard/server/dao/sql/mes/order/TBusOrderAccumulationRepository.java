package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.mes.bus.TBusOrderAccumulation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TBusOrderAccumulationRepository extends JpaRepository<TBusOrderAccumulation, Integer> {

    /**
     * 根据唯一键查找累计记录
     */
    Optional<TBusOrderAccumulation> findByOrderProcessIdAndMaterialNumberAndGroupCode(
            Integer orderProcessId, String materialNumber, String groupCode);


    /**
     * 清空累计数量
     */
    @Modifying
    @Query("UPDATE TBusOrderAccumulation t SET t.accumulatedQty = 0, t.lastUpdateTime = CURRENT_TIMESTAMP WHERE t.id = :id")
    void clearAccumulatedQty(@Param("id") Integer id);

    /**
     * 根据物料编码获取累计数量总和
     */
    @Query("SELECT COALESCE(SUM(t.accumulatedQty), 0) FROM TBusOrderAccumulation t WHERE t.orderProcessId = :orderProcessId AND t.materialNumber = :materialNumber AND t.groupCode= :groupCode")
    BigDecimal sumAccumulatedQtyByOrderProcessAndMaterialNumberAndGroupCode(@Param("orderProcessId") Integer orderProcessId, @Param("materialNumber") String materialNumber,@Param("groupCode") String groupCode);

}

