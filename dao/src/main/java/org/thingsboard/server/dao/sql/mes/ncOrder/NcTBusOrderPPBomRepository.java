package org.thingsboard.server.dao.sql.mes.ncOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.ncOrder.NcTBusOrderPPBom;

import java.util.List;

public interface NcTBusOrderPPBomRepository extends JpaRepository<NcTBusOrderPPBom, Integer> {
    // 根据 orderId 查询所有 bom
    @Query(value = "SELECT b.* FROM t_bus_order_ppbom b JOIN t_bus_order_ppbom_lk lk ON b.order_ppbom_id = lk.order_ppbom_id WHERE lk.order_id = ?1", nativeQuery = true)
    List<NcTBusOrderPPBom> findAllByOrderId(Integer orderId);

    // 根据 orderId 删除所有 bom 及关联
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM t_bus_order_ppbom WHERE nc_cmoid = ?1", nativeQuery = true)
    void deleteAllByOrderId(String cmoid);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM t_bus_order_ppbom_lk WHERE order_id = ?1", nativeQuery = true)
    void deleteAllLinkByOrderId(Integer orderId);

    // 根据 BOM ID 列表批量删除
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM t_bus_order_ppbom WHERE order_ppbom_id IN (?1)", nativeQuery = true)
    void deleteByOrderPPBomIdIn(List<Integer> orderPPBomIds);

    // 根据 BOM ID 列表删除关联关系
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM t_bus_order_ppbom_lk WHERE order_ppbom_id IN (?1)", nativeQuery = true)
    void deleteLinkByOrderPPBomIdIn(List<Integer> orderPPBomIds);
    
    // 插入订单和BOM的关联关系
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO t_bus_order_ppbom_lk (order_id, order_ppbom_id) VALUES (?1, ?2)", nativeQuery = true)
    void insertLink(Integer orderId, Integer orderPPBomId);
    
    // 检查关联关系是否存在
    @Query(value = "SELECT COUNT(*) FROM t_bus_order_ppbom_lk WHERE order_id = ?1 AND order_ppbom_id = ?2", nativeQuery = true)
    int countLink(Integer orderId, Integer orderPPBomId);
}
