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
     * 根据产线、时间范围（接单时间最小值）和工艺ID查询订单头信息
     * 优化方案：采用更高效的查询结构，避免在 SELECT 和 WHERE 中重复执行子查询
     */
    /**
     * 根据产线、时间范围（接单时间最小值）和工艺ID查询订单基本信息
     * 优化方案：利用 receive_time 索引进行范围扫描，通过 GROUP BY 计算最小接单时间，避免关联查询时的全表扫描。
     */
    @Query(value = "SELECT h.nc_vwkname, p_min.min_rt, h.order_no " +
            "FROM ( " +
            "    SELECT order_no, MIN(receive_time) as min_rt " +
            "    FROM t_bus_order_process " +
            "    WHERE receive_time <= :endTime " +
            "    GROUP BY order_no " +
            "    HAVING MIN(receive_time) >= :startTime " +
            ") p_min " +
            "JOIN t_bus_order_head h ON h.order_no = p_min.order_no " +
            "WHERE ((:cwkids) IS NULL OR h.nc_cwkid IN (:cwkids)) " +
            "AND (" +
            "  (h.bill_type = '周转饼流程生产单' OR (:craftId1 IS NOT NULL AND h.craft_id = :craftId1)) " +
            "  OR " +
            "  (h.bill_type = '普通流程生产订单' OR (:craftId2 IS NOT NULL AND h.craft_id = :craftId2)) " +
            ")", nativeQuery = true)
    List<Object[]> findAllMatchingOrderBasics(@Param("startTime") Date startTime, 
                                             @Param("endTime") Date endTime, 
                                             @Param("cwkids") List<String> cwkids,
                                             @Param("craftId1") Integer craftId1,
                                             @Param("craftId2") Integer craftId2);

    /**
     * 聚合查询：批量获取订单的投入明细（按物料和类型汇总）
     * 增加时间范围暗示（hint），利用索引加速查询
     */
    @Query("SELECT t.orderNo, t.materialNumber, t.materialName, t.recordUnit, t.recordType, SUM(t.recordQty) " +
           "FROM TBusOrderProcessHistory t " +
           "WHERE t.orderNo IN :orderNos " +
           "AND t.processNumber NOT IN ('GX-007', 'GX-011') " +
           "AND t.reportTime >= :hintStart " +
           "GROUP BY t.orderNo, t.materialNumber, t.materialName, t.recordUnit, t.recordType")
    List<Object[]> findActualInputAggregated(@Param("orderNos") List<String> orderNos, @Param("hintStart") Date hintStart);

    /**
     * 聚合查询：批量获取成品的报工条数
     */
    @Query("SELECT t.orderNo, COUNT(t) " +
           "FROM TBusOrderProcessHistory t " +
           "WHERE t.orderNo IN :orderNos " +
           "AND t.processNumber = 'GX-011' " +
           "AND t.reportTime >= :hintStart " +
           "GROUP BY t.orderNo")
    List<Object[]> findActualOutputCountAggregated(@Param("orderNos") List<String> orderNos, @Param("hintStart") Date hintStart);

    /**
     * 聚合查询：批量获取成品的净含量重量
     */
    @Query("SELECT t.orderNo, SUM(t.recordQty) " +
           "FROM TBusOrderProcessRecord t " +
           "WHERE t.orderNo IN :orderNos " +
           "AND t.processNumber = 'GX-011' " +
           "AND t.reportTime >= :hintStart " +
           "GROUP BY t.orderNo")
    List<Object[]> findNetWeightAggregated(@Param("orderNos") List<String> orderNos, @Param("hintStart") Date hintStart);

    /**
     * 根据订单号查询订单及其相关的最小接单时间
     */
    @Query("SELECT t, (SELECT MIN(p.receiveTime) FROM TBusOrderProcess p WHERE p.orderNo = t.orderNo) " +
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