package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPotCount;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPotCountRepository extends JpaRepository<TBusOrderPotCount, Integer> {

    Optional<TBusOrderPotCount> findByOrderProcessIdAndOrderPPBomIdAndDevicePersonGroupIdAndMaterialNumber(
            Integer orderProcessId, Integer orderPPBomId, String devicePersonGroupId, String materialNumber);

    List<TBusOrderPotCount> findAllByOrderProcessId(Integer orderProcessId);

    @Modifying
    @Query("update TBusOrderPotCount t set t.inputCount = t.inputCount + :delta where t.id = :id")
    void incrementInputCount(@Param("id") Integer id, @Param("delta") int delta);

    @Query("select coalesce(sum(t.inputCount),0) from TBusOrderPotCount t where t.orderProcessId = :orderProcessId and t.materialNumber = :materialNumber")
    int sumInputCountByOrderProcessAndMaterialNumber(@Param("orderProcessId") Integer orderProcessId, @Param("materialNumber") String materialNumber);

    @Query("select coalesce(min(t.inputCount),0) from TBusOrderPotCount t where t.orderProcessId = :orderProcessId")
    int getMinInputCountByOrderProcess(@Param("orderProcessId") Integer orderProcessId);

    /**
     * 获取所有需要投入物料的最小投入次数（包括未投入的物料）
     * 这个方法会考虑配方管理中定义的所有需要投入的物料
     */
    @Query("select coalesce(min(t.inputCount),0) from TBusOrderPotCount t where t.orderProcessId = :orderProcessId")
    int getMinInputCountByOrderProcessIncludingUnused(@Param("orderProcessId") Integer orderProcessId);

    @Modifying
    @Query("update TBusOrderPotCount t set t.potNumber = :potNumber where t.orderProcessId = :orderProcessId and t.materialNumber = :materialNumber")
    void updatePotNumberByOrderProcessAndMaterialNumber(@Param("orderProcessId") Integer orderProcessId, @Param("materialNumber") String materialNumber, @Param("potNumber") Integer potNumber);
}


