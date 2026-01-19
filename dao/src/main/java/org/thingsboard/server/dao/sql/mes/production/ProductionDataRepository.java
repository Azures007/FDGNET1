package org.thingsboard.server.dao.sql.mes.production;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPPBom;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;

import java.util.Date;
import java.util.List;

/**
 * 投入产出比报表数据访问接口
 */
@Repository
public interface ProductionDataRepository extends CrudRepository<TBusOrderHead, Integer> {

    /**
     * 根据产线、时间范围（计划开工时间）和工艺ID查询订单基本信息
     * 优化查询逻辑，确保即使工艺ID为空也能通过单据类型匹配，并过滤已删除订单
     */
    @Query(value = "SELECT h.nc_vwkname, h.body_plan_start_date, h.order_no " +
            "FROM t_bus_order_head h " +
            "WHERE h.body_plan_start_date >= :startTime " +
            "AND h.body_plan_start_date <= :endTime " +
            "AND ((:cwkids) IS NULL OR h.nc_cwkid IN (:cwkids)) " +
            "AND (" +
            "  h.bill_type = '周转饼流程生产单' " +
            "  OR h.bill_type = '普通流程生产订单' " +
            "  OR h.bill_type LIKE '%周转%' " +
            "  OR h.bill_type LIKE '%成品%' " +
            "  OR ((:craftIds1) IS NOT NULL AND CAST(h.craft_id AS text) IN (:craftIds1)) " +
            "  OR ((:craftIds2) IS NOT NULL AND CAST(h.craft_id AS text) IN (:craftIds2)) " +
            ") " +
            "AND (h.is_deleted = '0')", nativeQuery = true)
    List<Object[]> findAllMatchingOrderBasics(@Param("startTime") Date startTime, 
                                             @Param("endTime") Date endTime, 
                                             @Param("cwkids") List<String> cwkids,
                                             @Param("craftIds1") List<String> craftIds1,
                                             @Param("craftIds2") List<String> craftIds2);

    /**
     * 聚合查询：批量获取订单的投入明细（按物料和类型汇总）
     * 增加时间范围暗示（hint），利用索引加速查询
     */
    @Query("SELECT t.orderNo, t.materialNumber, t.materialName, t.recordUnit, t.recordType, SUM(t.recordQty) " +
           "FROM TBusOrderProcessHistory t " +
           "WHERE t.orderNo IN :orderNos " +
           "AND t.processNumber NOT IN ('GX-011') " +
           "GROUP BY t.orderNo, t.materialNumber, t.materialName, t.recordUnit, t.recordType")
    List<Object[]> findActualInputAggregated(@Param("orderNos") List<String> orderNos);

    /**
     * 聚合查询：批量获取成品的报工条数
     */
    @Query("SELECT t.orderNo, COUNT(t) " +
           "FROM TBusOrderProcessHistory t " +
           "WHERE t.orderNo IN :orderNos " +
           "AND t.processNumber = 'GX-011' " +
           "GROUP BY t.orderNo")
    List<Object[]> findActualOutputCountAggregated(@Param("orderNos") List<String> orderNos);

    /**
     * 聚合查询：批量获取成品的净含量重量
     */
    @Query("SELECT t.orderNo, SUM(t.recordQty) " +
           "FROM TBusOrderProcessRecord t " +
           "WHERE t.orderNo IN :orderNos " +
           "AND t.processNumber = 'GX-011' " +
           "GROUP BY t.orderNo")
    List<Object[]> findNetWeightAggregated(@Param("orderNos") List<String> orderNos);

    /**
     * 根据订单号查询订单及其相关的计划开工时间
     */
    @Query("SELECT t, t.bodyPlanStartDate " +
            "FROM TBusOrderHead t WHERE t.orderNo IN :orderNos")
    List<Object[]> findByOrderNoInWithMinRT(@Param("orderNos") List<String> orderNos);

    /**
     * 原有的查询方法（保留但标记为可能过时，或者直接重构）
     */
    @Query(value = "SELECT t, (SELECT MIN(p.receiveTime) FROM TBusOrderProcess p WHERE p.orderNo = t.orderNo) as minRT " +
            "FROM TBusOrderHead t " +
            "WHERE ((:cwkids) IS NULL OR t.cwkid IN (:cwkids)) " +
            "AND (" +
            "  (t.billType = '周转饼流程生产单' OR (:craftId1 IS NOT NULL AND t.craftId.craftId = :craftId1)) " +
            "  OR " +
            "  (t.billType = '普通流程生产订单' OR (:craftId2 IS NOT NULL AND t.craftId.craftId = :craftId2)) " +
            ") " +
            "AND (SELECT MIN(p.receiveTime) FROM TBusOrderProcess p WHERE p.orderNo = t.orderNo) >= :startTime " +
            "AND (SELECT MIN(p.receiveTime) FROM TBusOrderProcess p WHERE p.orderNo = t.orderNo) <= :endTime " +
            "ORDER BY minRT DESC")
    List<Object[]> findByMinReceiveTimeRange(@Param("startTime") Date startTime, 
                                            @Param("endTime") Date endTime, 
                                            @Param("cwkids") List<String> cwkids,
                                            @Param("craftId1") Integer craftId1,
                                            @Param("craftId2") Integer craftId2);

    /**
     * 根据订单号查询外包装工序的历史记录
     */
    @Query("SELECT t FROM TBusOrderProcessHistory t WHERE t.orderNo IN :orderNos AND t.processNumber = 'GX-011'")
    List<TBusOrderProcessHistory> findPackagingProcesses(@Param("orderNos") List<String> orderNos);

    /**
     * 根据订单号查询所有工序历史记录
     */
    @Query("SELECT t FROM TBusOrderProcessHistory t WHERE t.orderNo IN :orderNos")
    List<TBusOrderProcessHistory> findAllHistories(@Param("orderNos") List<String> orderNos);

    /**
     * 根据订单号批量查询BOM信息
     */
    @Query("SELECT b FROM TBusOrderHead h JOIN h.tBusOrderPPBomSet b WHERE h.orderNo IN :orderNos")
    List<TBusOrderPPBom> findBomsByOrderNos(@Param("orderNos") List<String> orderNos);

    /**
     * 根据订单号查询成品报工记录
     */
    @Query("SELECT t FROM TBusOrderProcessRecord t WHERE t.orderNo IN :orderNos AND t.processNumber = 'GX-011'")
    List<TBusOrderProcessRecord> findRecordsByOrderNos(@Param("orderNos") List<String> orderNos);
}