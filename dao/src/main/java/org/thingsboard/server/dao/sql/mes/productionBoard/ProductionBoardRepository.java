package org.thingsboard.server.dao.sql.mes.productionBoard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.ncOrder.NcTBusOrderHead;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产看板数据查询Repository
 */
public interface ProductionBoardRepository extends JpaRepository<NcTBusOrderHead, Integer> {

    /**
     * 查询指定时间范围内的订单数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单数量
     */
    @Query("SELECT COUNT(o) FROM NcTBusOrderHead o " +
           "WHERE o.isDeleted = '0' " +
           "AND o.billType = '普通流程生产订单' " +
           "AND o.tplanstarttime >= :startDate " +
           "AND o.tplanstarttime <= :endDate")
    Long countOrdersByDateRange(@Param("startDate") Date startDate, 
                                 @Param("endDate") Date endDate);

    /**
     * 查询指定时间范围和生产线的订单数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线名称
     * @return 订单数量
     */
    @Query("SELECT COUNT(o) FROM NcTBusOrderHead o " +
           "WHERE o.isDeleted = '0' " +
           "AND o.billType = '普通流程生产订单' " +
           "AND o.tplanstarttime >= :startDate " +
           "AND o.tplanstarttime <= :endDate " +
           "AND o.cwkid = :productionLine")
    Long countOrdersByDateRangeAndProductionLine(@Param("startDate") Date startDate, 
                                                  @Param("endDate") Date endDate,
                                                  @Param("productionLine") String productionLine);

    /**
     * 汇总指定时间范围内的计划生产数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 计划生产数量总和
     */
    @Query("SELECT COALESCE(SUM(o.nnum), 0) FROM NcTBusOrderHead o " +
           "WHERE o.isDeleted = '0' " +
           "AND o.billType = '普通流程生产订单' " +
           "AND o.tplanstarttime >= :startDate " +
           "AND o.tplanstarttime <= :endDate")
    java.math.BigDecimal sumPlanProductionQuantityByDateRange(@Param("startDate") Date startDate, 
                                                               @Param("endDate") Date endDate);

    /**
     * 汇总指定时间范围和生产线的计划生产数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线名称
     * @return 计划生产数量总和
     */
    @Query("SELECT COALESCE(SUM(o.nnum), 0) FROM NcTBusOrderHead o " +
           "WHERE o.isDeleted = '0' " +
           "AND o.billType = '普通流程生产订单' " +
           "AND o.tplanstarttime >= :startDate " +
           "AND o.tplanstarttime <= :endDate " +
           "AND o.cwkid = :productionLine")
    java.math.BigDecimal sumPlanProductionQuantityByDateRangeAndProductionLine(@Param("startDate") Date startDate, 
                                                                                @Param("endDate") Date endDate,
                                                                                @Param("productionLine") String productionLine);

    /**
     * 查询指定时间范围内的订单统计数据（订单数量和计划生产数量）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param worklineIds 允许的生产线ID列表
     * @return List<Map> [orderCount: 订单数量, planProductionQuantity: 计划生产数量总和]
     */
    @Query(value = "SELECT COUNT(h.order_id) as orderCount, COALESCE(SUM(h.body_plan_prd_qty), 0) as planProductionQuantity " +
           "FROM t_bus_order_head h " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds)",
           nativeQuery = true)
    List<Map> getOrderStatisticsByDateRange(@Param("startDate") Date startDate, 
                                           @Param("endDate") Date endDate,
                                           @Param("worklineIds") List<String> worklineIds);

    /**
     * 查询指定时间范围和生产线的订单统计数据（订单数量和计划生产数量）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID
     * @param worklineIds 允许的生产线ID列表
     * @return List<Map> [orderCount: 订单数量, planProductionQuantity: 计划生产数量总和]
     */
    @Query(value = "SELECT COUNT(h.order_id) as orderCount, COALESCE(SUM(h.body_plan_prd_qty), 0) as planProductionQuantity " +
           "FROM t_bus_order_head h " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid = :productionLine " +
           "AND h.nc_cwkid IN (:worklineIds)",
           nativeQuery = true)
    List<Map> getOrderStatisticsByDateRangeAndProductionLine(@Param("startDate") Date startDate, 
                                                            @Param("endDate") Date endDate,
                                                            @Param("productionLine") String productionLine,
                                                            @Param("worklineIds") List<String> worklineIds);

    /**
     * 查询指定时间范围内的计划原辅材重量（排除外包材料和包膜）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param worklineIds 允许的生产线ID列表
     * @return 原辅材重量总和
     */
    @Query(value = "SELECT COALESCE(SUM(bom.must_qty), 0) " +
           "FROM t_bus_order_head h " +
           "LEFT JOIN t_bus_order_ppbom_lk lk ON lk.order_id = h.order_id " +
           "LEFT JOIN t_bus_order_ppbom bom ON bom.order_ppbom_id = lk.order_ppbom_id " +
           "LEFT JOIN t_sync_material m ON m.material_code = bom.material_number " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (m.nc_material_classification IS NULL " +
           "     OR (m.nc_material_classification <> '外包材料' " +
           "         AND m.nc_material_classification <> '包膜'))", 
           nativeQuery = true)
    BigDecimal sumPlanMaterialWeightByDateRange(@Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                @Param("worklineIds") List<String> worklineIds);

    /**
     * 查询指定时间范围和生产线的计划原辅材重量（排除外包材料和包膜）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID
     * @param worklineIds 允许的生产线ID列表
     * @return 原辅材重量总和
     */
    @Query(value = "SELECT COALESCE(SUM(bom.must_qty), 0) " +
           "FROM t_bus_order_head h " +
           "LEFT JOIN t_bus_order_ppbom_lk lk ON lk.order_id = h.order_id " +
           "LEFT JOIN t_bus_order_ppbom bom ON bom.order_ppbom_id = lk.order_ppbom_id " +
           "LEFT JOIN t_sync_material m ON m.material_code = bom.material_number " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid = :productionLine " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (m.nc_material_classification IS NULL " +
           "     OR (m.nc_material_classification <> '外包材料' " +
           "         AND m.nc_material_classification <> '包膜'))", 
           nativeQuery = true)
    BigDecimal sumPlanMaterialWeightByDateRangeAndProductionLine(@Param("startDate") Date startDate, 
                                                              @Param("endDate") Date endDate,
                                                              @Param("productionLine") String productionLine,
                                                              @Param("worklineIds") List<String> worklineIds);

    /**
     * 查询指定时间范围内的包材重量（外包材料和包膜）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param worklineIds 允许的生产线ID列表
     * @return 包材重量总和
     */
    @Query(value = "SELECT COALESCE(SUM(bom.must_qty), 0) " +
           "FROM t_bus_order_head h " +
           "LEFT JOIN t_bus_order_ppbom_lk lk ON lk.order_id = h.order_id " +
           "LEFT JOIN t_bus_order_ppbom bom ON bom.order_ppbom_id = lk.order_ppbom_id " +
           "LEFT JOIN t_sync_material m ON m.material_code = bom.material_number " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (m.nc_material_classification = '外包材料' " +
           "     OR m.nc_material_classification = '包膜')", 
           nativeQuery = true)
    BigDecimal sumPackagingWeightByDateRange(@Param("startDate") Date startDate, 
                                         @Param("endDate") Date endDate,
                                         @Param("worklineIds") List<String> worklineIds);

    /**
     * 查询指定时间范围和生产线的包材重量（外包材料和包膜）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID
     * @param worklineIds 允许的生产线ID列表
     * @return 包材重量总和
     */
    @Query(value = "SELECT COALESCE(SUM(bom.must_qty), 0) " +
           "FROM t_bus_order_head h " +
           "LEFT JOIN t_bus_order_ppbom_lk lk ON lk.order_id = h.order_id " +
           "LEFT JOIN t_bus_order_ppbom bom ON bom.order_ppbom_id = lk.order_ppbom_id " +
           "LEFT JOIN t_sync_material m ON m.material_code = bom.material_number " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid = :productionLine " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (m.nc_material_classification = '外包材料' " +
           "     OR m.nc_material_classification = '包膜')", 
           nativeQuery = true)
    BigDecimal sumPackagingWeightByDateRangeAndProductionLine(@Param("startDate") Date startDate, 
                                                          @Param("endDate") Date endDate,
                                                          @Param("productionLine") String productionLine,
                                                          @Param("worklineIds") List<String> worklineIds);

    /**
     * 查询订单进度数据（包含报工数量）- 返回分页Map
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @param pageable 分页参数
     * @return Page<Map> 分页结果
     */
    @Query(value = "SELECT h.nc_vwkname as productionLine, h.order_no as orderNo, h.body_material_name as productName, " +
           "h.body_plan_prd_qty as planQuantity, '件' as unit, " +
           "CASE WHEN h.qty_per_jian > 0 " +
           "THEN FLOOR(COUNT(r.record_qty) / h.qty_per_jian) " +
           "ELSE COUNT(r.record_qty) END as completedQuantity ,(CASE h.order_status WHEN '0'  then '未开工' when '1' then '已开工' else '已完工' end) orderStatus " +
           "FROM t_bus_order_head h " +
           "LEFT JOIN t_bus_order_process_history r ON h.order_no = r.order_no " +
           "  AND r.process_number = 'GX-011' " +
           "  AND r.bus_type = 'BG' " +
           "  AND r.record_type = '3' " +
           "  AND r.report_status = '0' " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "GROUP BY h.order_status,h.nc_vwkname,h.order_no, h.body_material_name, h.body_plan_prd_qty, h.qty_per_jian, h.body_plan_start_date " +
           "ORDER BY h.body_plan_start_date DESC",
           countQuery = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no " +
           "FROM t_bus_order_head h " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "GROUP BY h.nc_vwkname,h.order_no, h.body_material_name, h.body_plan_prd_qty, h.body_plan_start_date" +
           ") as total",
           nativeQuery = true)
    Page<Map> findOrderProgressByDateRangeAndOrg(@Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate,
                                                  @Param("productionLine") String productionLine,
                                                  @Param("worklineIds") List<String> worklineIds,
                                                  Pageable pageable);

    /**
     * 查询未完成订单数量（进度 < 100%）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @return 未完成订单数量
     */
    @Query(value = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no, h.body_plan_prd_qty, h.qty_per_jian, " +
           "CASE WHEN h.qty_per_jian > 0 " +
           "THEN FLOOR(COUNT(r.record_qty) / h.qty_per_jian) " +
           "ELSE COUNT(r.record_qty) END as completed_count " +
           "FROM t_bus_order_head h " +
           "LEFT JOIN t_bus_order_process_history r ON h.order_no = r.order_no " +
           "  AND r.process_number = 'GX-011' " +
           "  AND r.bus_type = 'BG' " +
           "  AND r.record_type = '3' " +
           "  AND r.report_status = '0' " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "GROUP BY h.order_no, h.body_plan_prd_qty, h.qty_per_jian " +
           "HAVING (CASE WHEN h.qty_per_jian > 0 " +
           "THEN FLOOR(COUNT(r.record_qty) / h.qty_per_jian) " +
           "ELSE COUNT(r.record_qty) END) < h.body_plan_prd_qty OR COUNT(r.record_qty) = 0" +
           ") as unfinished",
           nativeQuery = true)
    Long countUnfinishedOrders(@Param("startDate") Date startDate,
                                @Param("endDate") Date endDate,
                                @Param("productionLine") String productionLine,
                                @Param("worklineIds") List<String> worklineIds);

    /**
     * 按日分组查询订单计划达成率分析数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @return List<Map> [时间, 计划量, 完成量]
     */
    @Query(value = "SELECT TO_CHAR(h.body_plan_start_date, 'MM-DD') as timeX, " +
           "COALESCE(SUM(h.body_plan_prd_qty), 0) as planQuantity, " +
           "COALESCE(SUM(CASE WHEN h.qty_per_jian > 0 " +
           "THEN FLOOR(completed.completed_qty / h.qty_per_jian) " +
           "ELSE completed.completed_qty END), 0) as completedQuantity " +
           "FROM t_bus_order_head h " +
           "LEFT JOIN (" +
           "  SELECT r.order_no, COUNT(r.record_qty) as completed_qty " +
           "  FROM t_bus_order_process_history r " +
           "  WHERE r.process_number = 'GX-011' " +
           "    AND r.bus_type = 'BG' " +
           "    AND r.record_type = '3' " +
           "    AND r.report_status = '0' " +
           "  GROUP BY r.order_no" +
           ") completed ON h.order_no = completed.order_no " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "GROUP BY TO_CHAR(h.body_plan_start_date, 'MM-DD') " +
           "ORDER BY TO_CHAR(h.body_plan_start_date, 'MM-DD')",
           nativeQuery = true)
    List<Map> findOrderPlanAnalysisByDay(@Param("startDate") Date startDate,
                                         @Param("endDate") Date endDate,
                                         @Param("productionLine") String productionLine,
                                         @Param("worklineIds") List<String> worklineIds);

    /**
     * 按月分组查询订单计划达成率分析数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @return List<Map> [时间, 计划量, 完成量]
     */
    @Query(value = "SELECT TO_CHAR(h.body_plan_start_date, 'YYYY-MM') as timeX, " +
           "COALESCE(SUM(h.body_plan_prd_qty), 0) as planQuantity, " +
           "COALESCE(SUM(CASE WHEN h.qty_per_jian > 0 " +
           "THEN FLOOR(completed.completed_qty / h.qty_per_jian) " +
           "ELSE completed.completed_qty END), 0) as completedQuantity " +
           "FROM t_bus_order_head h " +
           "LEFT JOIN (" +
           "  SELECT r.order_no, COUNT(r.record_qty) as completed_qty " +
           "  FROM t_bus_order_process_history r " +
           "  WHERE r.process_number = 'GX-011' " +
           "    AND r.bus_type = 'BG' " +
           "    AND r.record_type = '3' " +
           "    AND r.report_status = '0' " +
           "  GROUP BY r.order_no" +
           ") completed ON h.order_no = completed.order_no " +
            "JOIN t_sys_craft_material_rel rel on rel.material_code=h.body_material_number and rel.craft_id=9 " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "GROUP BY TO_CHAR(h.body_plan_start_date, 'YYYY-MM') " +
           "ORDER BY TO_CHAR(h.body_plan_start_date, 'YYYY-MM')",
           nativeQuery = true)
    List<Map> findOrderPlanAnalysisByMonth(@Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate,
                                           @Param("productionLine") String productionLine,
                                           @Param("worklineIds") List<String> worklineIds);

    /**
     * 查询生产报工数据（生产态势监察）- 正常范围 - 返回分页Map
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @param pageable 分页参数
     * @return Page<Map> 分页结果
     */
    @Query(value = "SELECT h.nc_vwkname as productionLine, r.process_name as process, " +
           "r.material_name as materialName, " +
           "CONCAT(TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))), '-', " +
           "TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))) as standard, " +
           "SUM(r.record_qty) as recordQuantity, " +
           "CASE WHEN r.pot_number IS NOT NULL " +
           "THEN CONCAT('第', r.pot_number, '锅') ELSE '' END as potStr, " +
           "MAX(r.report_time) as recordTime " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_recipe_product_binding b ON b.product_code = h.body_material_number " +
           "LEFT JOIN t_sys_recipe_input i ON i.recipe_id = b.recipe_id " +
           "  AND i.material_code = r.material_number " +
           "  AND i.process_number = r.process_number " +
           "  AND (r.group_code = i.semi_finished_product_code OR r.group_code IS NULL) " +
           "WHERE h.is_deleted = '0' and r.process_number<>'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND (i.standard_input IS NULL OR i.allowable_deviation IS NULL " +
           "     OR (r.record_qty >= (i.standard_input - i.allowable_deviation) " +
           "         AND r.record_qty <= (i.standard_input + i.allowable_deviation))) " +
           "GROUP BY \n" +
            "    h.order_no,\n" +
            "    h.nc_vwkname,\n" +
            "    r.process_name,\n" +
            "    r.material_name,\n" +
            "    CONCAT(\n" +
            "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))),\n" +
            "        '-',\n" +
            "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))\n" +
            "    ),\n" +
            "    CASE \n" +
            "        WHEN r.pot_number IS NOT NULL THEN CONCAT('第', r.pot_number, '锅') \n" +
            "        ELSE '' \n" +
            "    END\n" +
            "ORDER BY MAX(r.report_time) DESC",
           countQuery = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_recipe_product_binding b ON b.product_code = h.body_material_number " +
           "LEFT JOIN t_sys_recipe_input i ON i.recipe_id = b.recipe_id " +
           "  AND i.material_code = r.material_number " +
           "  AND i.process_number = r.process_number " +
           "  AND (r.group_code = i.semi_finished_product_code OR r.group_code IS NULL) " +
           "WHERE h.is_deleted = '0' and r.process_number<>'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND (i.standard_input IS NULL OR i.allowable_deviation IS NULL " +
           "     OR (r.record_qty >= (i.standard_input - i.allowable_deviation) " +
           "         AND r.record_qty <= (i.standard_input + i.allowable_deviation)))" +
                   "GROUP BY \n" +
                   "    h.order_no,\n" +
                   "    h.nc_vwkname,\n" +
                   "    r.process_name,\n" +
                   "    r.material_name,\n" +
                   "    CONCAT(\n" +
                   "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))),\n" +
                   "        '-',\n" +
                   "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))\n" +
                   "    ),\n" +
                   "    CASE \n" +
                   "        WHEN r.pot_number IS NOT NULL THEN CONCAT('第', r.pot_number, '锅') \n" +
                   "        ELSE '' \n" +
                   "    END\n" +
           ") as total",
           nativeQuery = true)
    Page<Map> findProductionBgData(@Param("productionLine") String productionLine,
                                   @Param("worklineIds") List<String> worklineIds,
                                   Pageable pageable);

    /**
     * 查询生产报工数据（生产态势监察）- 正常范围 - 带日期范围 - 返回分页Map
     * @param productionLine 生产线ID（可选）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param worklineIds 允许的生产线ID列表
     * @param pageable 分页参数
     * @return Page<Map> 分页结果
     */
    @Query(value = "SELECT h.nc_vwkname as productionLine, r.process_name as process, " +
           "r.material_name as materialName, " +
           "CONCAT(TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))), '-', " +
           "TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))) as standard, " +
           "SUM(r.record_qty) as recordQuantity, " +
           "CASE WHEN r.pot_number IS NOT NULL " +
           "THEN CONCAT('第', r.pot_number, '锅') ELSE '' END as potStr, " +
           "MAX(r.report_time) as recordTime " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_recipe_product_binding b ON b.product_code = h.body_material_number " +
           "LEFT JOIN t_sys_recipe_input i ON i.recipe_id = b.recipe_id " +
           "  AND i.material_code = r.material_number " +
           "  AND i.process_number = r.process_number " +
           "  AND (r.group_code = i.semi_finished_product_code OR r.group_code IS NULL) " +
           "WHERE h.is_deleted = '0' and r.process_number<>'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND r.report_time >= :startDate " +
           "AND r.report_time <= :endDate " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND (i.standard_input IS NULL OR i.allowable_deviation IS NULL " +
           "     OR (r.record_qty >= (i.standard_input - i.allowable_deviation) " +
           "         AND r.record_qty <= (i.standard_input + i.allowable_deviation))) " +
           "GROUP BY \n" +
            "    h.order_no,\n" +
            "    h.nc_vwkname,\n" +
            "    r.process_name,\n" +
            "    r.material_name,\n" +
            "    CONCAT(\n" +
            "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))),\n" +
            "        '-',\n" +
            "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))\n" +
            "    ),\n" +
            "    CASE \n" +
            "        WHEN r.pot_number IS NOT NULL THEN CONCAT('第', r.pot_number, '锅') \n" +
            "        ELSE '' \n" +
            "    END\n" +
            "ORDER BY MAX(r.report_time) DESC",
           countQuery = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_recipe_product_binding b ON b.product_code = h.body_material_number " +
           "LEFT JOIN t_sys_recipe_input i ON i.recipe_id = b.recipe_id " +
           "  AND i.material_code = r.material_number " +
           "  AND i.process_number = r.process_number " +
           "  AND (r.group_code = i.semi_finished_product_code OR r.group_code IS NULL) " +
           "WHERE h.is_deleted = '0' and r.process_number<>'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND r.report_time >= :startDate " +
           "AND r.report_time <= :endDate " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND (i.standard_input IS NULL OR i.allowable_deviation IS NULL " +
           "     OR (r.record_qty >= (i.standard_input - i.allowable_deviation) " +
           "         AND r.record_qty <= (i.standard_input + i.allowable_deviation)))" +
                   "GROUP BY \n" +
                   "    h.order_no,\n" +
                   "    h.nc_vwkname,\n" +
                   "    r.process_name,\n" +
                   "    r.material_name,\n" +
                   "    CONCAT(\n" +
                   "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))),\n" +
                   "        '-',\n" +
                   "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))\n" +
                   "    ),\n" +
                   "    CASE \n" +
                   "        WHEN r.pot_number IS NOT NULL THEN CONCAT('第', r.pot_number, '锅') \n" +
                   "        ELSE '' \n" +
                   "    END\n" +
           ") as total",
           nativeQuery = true)
    Page<Map> findProductionBgDataByDateRange(@Param("productionLine") String productionLine,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate,
                                              @Param("worklineIds") List<String> worklineIds,
                                              Pageable pageable);

    /**
     * 查询生产报工数据（生产态势监察）- 异常范围 - 返回分页Map
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @param pageable 分页参数
     * @return Page<Map> 分页结果
     */
    @Query(value = "SELECT h.nc_vwkname as productionLine, r.process_name as process, " +
           "r.material_name as materialName, " +
           "CONCAT(TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))), '-', " +
           "TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))) as standard, " +
           "SUM(r.record_qty) as recordQuantity, " +
           "CASE WHEN r.pot_number IS NOT NULL " +
           "THEN CONCAT('第', r.pot_number, '锅') ELSE '' END as potStr, " +
           "MAX(r.report_time) as recordTime " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_recipe_product_binding b ON b.product_code = h.body_material_number " +
           "LEFT JOIN t_sys_recipe_input i ON i.recipe_id = b.recipe_id " +
           "  AND i.material_code = r.material_number " +
           "  AND i.process_number = r.process_number " +
           "  AND (r.group_code = i.semi_finished_product_code OR r.group_code IS NULL) " +
           "WHERE h.is_deleted = '0' and r.process_number<>'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND i.standard_input IS NOT NULL AND i.allowable_deviation IS NOT NULL " +
           "AND (r.record_qty < (i.standard_input - i.allowable_deviation) " +
           "     OR r.record_qty > (i.standard_input + i.allowable_deviation)) " +
           "GROUP BY \n" +
            "    h.order_no,\n" +
            "    h.nc_vwkname,\n" +
            "    r.process_name,\n" +
            "    r.material_name,\n" +
            "    CONCAT(\n" +
            "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))),\n" +
            "        '-',\n" +
            "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))\n" +
            "    ),\n" +
            "    CASE \n" +
            "        WHEN r.pot_number IS NOT NULL THEN CONCAT('第', r.pot_number, '锅') \n" +
            "        ELSE '' \n" +
            "    END\n" +
            "ORDER BY MAX(r.report_time) DESC",
           countQuery = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_recipe_product_binding b ON b.product_code = h.body_material_number " +
           "LEFT JOIN t_sys_recipe_input i ON i.recipe_id = b.recipe_id " +
           "  AND i.material_code = r.material_number " +
           "  AND i.process_number = r.process_number " +
           "  AND (r.group_code = i.semi_finished_product_code OR r.group_code IS NULL) " +
           "WHERE h.is_deleted = '0' and r.process_number<>'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND i.standard_input IS NOT NULL AND i.allowable_deviation IS NOT NULL " +
           "AND (r.record_qty < (i.standard_input - i.allowable_deviation) " +
           "     OR r.record_qty > (i.standard_input + i.allowable_deviation))" +
                   "GROUP BY \n" +
                   "    h.order_no,\n" +
                   "    h.nc_vwkname,\n" +
                   "    r.process_name,\n" +
                   "    r.material_name,\n" +
                   "    CONCAT(\n" +
                   "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))),\n" +
                   "        '-',\n" +
                   "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))\n" +
                   "    ),\n" +
                   "    CASE \n" +
                   "        WHEN r.pot_number IS NOT NULL THEN CONCAT('第', r.pot_number, '锅') \n" +
                   "        ELSE '' \n" +
                   "    END\n" +
           ") as total",
           nativeQuery = true)
    Page<Map> findProductionBgDataAbnormal(@Param("productionLine") String productionLine,
                                           @Param("worklineIds") List<String> worklineIds,
                                           Pageable pageable);

    /**
     * 查询生产报工数据（生产态势监察）- 异常范围 - 带日期范围 - 返回分页Map
     * @param productionLine 生产线ID（可选）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param worklineIds 允许的生产线ID列表
     * @param pageable 分页参数
     * @return Page<Map> 分页结果
     */
    @Query(value = "SELECT h.nc_vwkname as productionLine, r.process_name as process, " +
           "r.material_name as materialName, " +
           "CONCAT(TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))), '-', " +
           "TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))) as standard, " +
           "SUM(r.record_qty) as recordQuantity, " +
           "CASE WHEN r.pot_number IS NOT NULL " +
           "THEN CONCAT('第', r.pot_number, '锅') ELSE '' END as potStr, " +
           "MAX(r.report_time) as recordTime " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_recipe_product_binding b ON b.product_code = h.body_material_number " +
           "LEFT JOIN t_sys_recipe_input i ON i.recipe_id = b.recipe_id " +
           "  AND i.material_code = r.material_number " +
           "  AND i.process_number = r.process_number " +
           "  AND (r.group_code = i.semi_finished_product_code OR r.group_code IS NULL) " +
           "WHERE h.is_deleted = '0' and r.process_number<>'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND r.report_time >= :startDate " +
           "AND r.report_time <= :endDate " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND i.standard_input IS NOT NULL AND i.allowable_deviation IS NOT NULL " +
           "AND (r.record_qty < (i.standard_input - i.allowable_deviation) " +
           "     OR r.record_qty > (i.standard_input + i.allowable_deviation)) " +
           "GROUP BY \n" +
            "    h.nc_vwkname,\n" +
            "    r.process_name,\n" +
            "    r.material_name,\n" +
            "    CONCAT(\n" +
            "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))),\n" +
            "        '-',\n" +
            "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))\n" +
            "    ),\n" +
            "    CASE \n" +
            "        WHEN r.pot_number IS NOT NULL THEN CONCAT('第', r.pot_number, '锅') \n" +
            "        ELSE '' \n" +
            "    END\n" +
            "ORDER BY MAX(r.report_time) DESC",
           countQuery = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_recipe_product_binding b ON b.product_code = h.body_material_number " +
           "LEFT JOIN t_sys_recipe_input i ON i.recipe_id = b.recipe_id " +
           "  AND i.material_code = r.material_number " +
           "  AND i.process_number = r.process_number " +
           "  AND (r.group_code = i.semi_finished_product_code OR r.group_code IS NULL) " +
           "WHERE h.is_deleted = '0' and r.process_number<>'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND r.report_time >= :startDate " +
           "AND r.report_time <= :endDate " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND i.standard_input IS NOT NULL AND i.allowable_deviation IS NOT NULL " +
           "AND (r.record_qty < (i.standard_input - i.allowable_deviation) " +
           "     OR r.record_qty > (i.standard_input + i.allowable_deviation))" +
                   "GROUP BY \n" +
                   "    h.order_no,\n" +
                   "    h.nc_vwkname,\n" +
                   "    r.process_name,\n" +
                   "    r.material_name,\n" +
                   "    CONCAT(\n" +
                   "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input - i.allowable_deviation) AS text))),\n" +
                   "        '-',\n" +
                   "        TRIM(TRAILING '0' FROM TRIM(TRAILING '.' FROM CAST((i.standard_input + i.allowable_deviation) AS text)))\n" +
                   "    ),\n" +
                   "    CASE \n" +
                   "        WHEN r.pot_number IS NOT NULL THEN CONCAT('第', r.pot_number, '锅') \n" +
                   "        ELSE '' \n" +
                   "    END\n" +
           ") as total",
           nativeQuery = true)
    Page<Map> findProductionBgDataAbnormalByDateRange(@Param("productionLine") String productionLine,
                                                      @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate,
                                                      @Param("worklineIds") List<String> worklineIds,
                                                      Pageable pageable);

    /**
     * 查询外包净含量实况数据 - 正常范围 - 返回分页Map
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @param pageable 分页参数
     * @return Page<Map> 分页结果
     */
    @Query(value = "SELECT h.nc_vwkname as productionLine, " +
           "h.body_material_name as productName, " +
           "CONCAT(TRIM(TRAILING '.' FROM TRIM(TRAILING '0' FROM CAST(b.lower_limit AS text))), '-', " +
           "TRIM(TRAILING '.' FROM TRIM(TRAILING '0' FROM CAST(b.upper_limit AS text)))) as netContentStandardStr, " +
           "b.upper_limit as netContentStandardUpper, " +
           "b.lower_limit as netContentStandardLower, " +
           "r.record_qty * 1000 as actualNetContent, " +
           "r.report_time as recordTime " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_net_content_range b ON b.material_code = h.body_material_number " +
           "WHERE h.is_deleted = '0' " +
           "AND r.process_number = 'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND (b.lower_limit IS NULL OR b.upper_limit IS NULL " +
           "     OR (r.record_qty * 1000 >= b.lower_limit AND r.record_qty * 1000 <= b.upper_limit)) " +
           "ORDER BY r.report_time DESC",
           countQuery = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_net_content_range b ON b.material_code = h.body_material_number " +
           "WHERE h.is_deleted = '0' " +
           "AND r.process_number = 'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND (b.lower_limit IS NULL OR b.upper_limit IS NULL " +
           "     OR (r.record_qty * 1000 >= b.lower_limit AND r.record_qty * 1000 <= b.upper_limit))" +
           ") as total",
           nativeQuery = true)
    Page<Map> findOutsourcingNetContentData(@Param("productionLine") String productionLine,
                                            @Param("worklineIds") List<String> worklineIds,
                                            Pageable pageable);

    /**
     * 查询外包净含量实况数据 - 正常范围 - 带日期范围 - 返回分页Map
     * @param productionLine 生产线ID（可选）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param worklineIds 允许的生产线ID列表
     * @param pageable 分页参数
     * @return Page<Map> 分页结果
     */
    @Query(value = "SELECT h.nc_vwkname as productionLine, " +
           "h.body_material_name as productName, " +
           "CONCAT(TRIM(TRAILING '.' FROM TRIM(TRAILING '0' FROM CAST(b.lower_limit AS text))), '-', " +
           "TRIM(TRAILING '.' FROM TRIM(TRAILING '0' FROM CAST(b.upper_limit AS text)))) as netContentStandardStr, " +
           "b.upper_limit as netContentStandardUpper, " +
           "b.lower_limit as netContentStandardLower, " +
           "r.record_qty * 1000 as actualNetContent, " +
           "r.report_time as recordTime " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_net_content_range b ON b.material_code = h.body_material_number " +
           "WHERE h.is_deleted = '0' " +
           "AND r.process_number = 'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND r.report_time >= :startDate " +
           "AND r.report_time <= :endDate " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND (b.lower_limit IS NULL OR b.upper_limit IS NULL " +
           "     OR (r.record_qty * 1000 >= b.lower_limit AND r.record_qty * 1000 <= b.upper_limit)) " +
           "ORDER BY r.report_time DESC",
           countQuery = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_net_content_range b ON b.material_code = h.body_material_number " +
           "WHERE h.is_deleted = '0' " +
           "AND r.process_number = 'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND r.report_time >= :startDate " +
           "AND r.report_time <= :endDate " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND (b.lower_limit IS NULL OR b.upper_limit IS NULL " +
           "     OR (r.record_qty * 1000 >= b.lower_limit AND r.record_qty * 1000 <= b.upper_limit))" +
           ") as total",
           nativeQuery = true)
    Page<Map> findOutsourcingNetContentDataByDateRange(@Param("productionLine") String productionLine,
                                                       @Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate,
                                                       @Param("worklineIds") List<String> worklineIds,
                                                       Pageable pageable);

    /**
     * 查询外包净含量实况数据 - 异常范围 - 返回分页Map
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @param pageable 分页参数
     * @return Page<Map> 分页结果
     */
    @Query(value = "SELECT h.nc_vwkname as productionLine, " +
           "h.body_material_name as productName, " +
           "CONCAT(TRIM(TRAILING '.' FROM TRIM(TRAILING '0' FROM CAST(b.lower_limit AS text))), '-', " +
           "TRIM(TRAILING '.' FROM TRIM(TRAILING '0' FROM CAST(b.upper_limit AS text)))) as netContentStandardStr, " +
           "b.upper_limit as netContentStandardUpper, " +
           "b.lower_limit as netContentStandardLower, " +
           "r.record_qty * 1000 as actualNetContent, " +
           "r.report_time as recordTime " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_net_content_range b ON b.material_code = h.body_material_number " +
           "WHERE h.is_deleted = '0' " +
           "AND r.process_number = 'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND b.lower_limit IS NOT NULL AND b.upper_limit IS NOT NULL " +
           "AND (r.record_qty * 1000 < b.lower_limit OR r.record_qty * 1000 > b.upper_limit) " +
           "ORDER BY r.report_time DESC",
           countQuery = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_net_content_range b ON b.material_code = h.body_material_number " +
           "WHERE h.is_deleted = '0' " +
           "AND r.process_number = 'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND b.lower_limit IS NOT NULL AND b.upper_limit IS NOT NULL " +
           "AND (r.record_qty * 1000 < b.lower_limit OR r.record_qty * 1000 > b.upper_limit)" +
           ") as total",
           nativeQuery = true)
    Page<Map> findOutsourcingNetContentDataAbnormal(@Param("productionLine") String productionLine,
                                                    @Param("worklineIds") List<String> worklineIds,
                                                    Pageable pageable);

    /**
     * 查询外包净含量实况数据 - 异常范围 - 带日期范围 - 返回分页Map
     * @param productionLine 生产线ID（可选）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param worklineIds 允许的生产线ID列表
     * @param pageable 分页参数
     * @return Page<Map> 分页结果
     */
    @Query(value = "SELECT h.nc_vwkname as productionLine, " +
           "h.body_material_name as productName, " +
           "CONCAT(TRIM(TRAILING '.' FROM TRIM(TRAILING '0' FROM CAST(b.lower_limit AS text))), '-', " +
           "TRIM(TRAILING '.' FROM TRIM(TRAILING '0' FROM CAST(b.upper_limit AS text)))) as netContentStandardStr, " +
           "b.upper_limit as netContentStandardUpper, " +
           "b.lower_limit as netContentStandardLower, " +
           "r.record_qty * 1000 as actualNetContent, " +
           "r.report_time as recordTime " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_net_content_range b ON b.material_code = h.body_material_number " +
           "WHERE h.is_deleted = '0' " +
           "AND r.process_number = 'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND r.report_time >= :startDate " +
           "AND r.report_time <= :endDate " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND b.lower_limit IS NOT NULL AND b.upper_limit IS NOT NULL " +
           "AND (r.record_qty * 1000 < b.lower_limit OR r.record_qty * 1000 > b.upper_limit) " +
           "ORDER BY r.report_time DESC",
           countQuery = "SELECT COUNT(*) FROM (" +
           "SELECT h.order_no " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no AND r.report_status = '0' " +
           "LEFT JOIN t_sys_net_content_range b ON b.material_code = h.body_material_number " +
           "WHERE h.is_deleted = '0' " +
           "AND r.process_number = 'GX-011' " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND r.report_time >= :startDate " +
           "AND r.report_time <= :endDate " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "AND b.lower_limit IS NOT NULL AND b.upper_limit IS NOT NULL " +
           "AND (r.record_qty * 1000 < b.lower_limit OR r.record_qty * 1000 > b.upper_limit)" +
           ") as total",
           nativeQuery = true)
    Page<Map> findOutsourcingNetContentDataAbnormalByDateRange(@Param("productionLine") String productionLine,
                                                               @Param("startDate") Date startDate,
                                                               @Param("endDate") Date endDate,
                                                               @Param("worklineIds") List<String> worklineIds,
                                                               Pageable pageable);

    /**
     * 汇总指定时间范围内的净含量报工总重量（process_number='GX-011'）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @return 净含量报工总重量
     */
    @Query(value = "SELECT COALESCE(SUM(r.record_qty), 0) " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND r.process_number = 'GX-011' " +
           "AND r.bus_type = 'BG' " +
           "AND r.record_type = '3' " +
           "AND r.report_status = '0' " +
           "AND r.report_time >= :startDate " +
           "AND r.report_time <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR))",
           nativeQuery = true)
    BigDecimal sumNetContentWeightByDateRange(@Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate,
                                              @Param("productionLine") String productionLine,
                                              @Param("worklineIds") List<String> worklineIds);

    /**
     * 按日分组查询废料产出分析数据（净含量报工总重量）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @return List<Map> [时间, 净含量报工总重量]
     */
    @Query(value = "SELECT TO_CHAR(h.body_plan_start_date, 'MM-DD') as timeX, " +
           "COALESCE(SUM(r.record_qty), 0) as netContentWeight " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND r.process_number = 'GX-011' " +
           "AND r.bus_type = 'BG' " +
           "AND r.record_type = '3' " +
           "AND r.report_status = '0' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "GROUP BY TO_CHAR(h.body_plan_start_date, 'MM-DD') " +
           "ORDER BY TO_CHAR(h.body_plan_start_date, 'MM-DD')",
           nativeQuery = true)
    List<Map> findNetContentWeightByDay(@Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate,
                                        @Param("productionLine") String productionLine,
                                        @Param("worklineIds") List<String> worklineIds);

    /**
     * 按月分组查询废料产出分析数据（净含量报工总重量）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @return List<Map> [时间, 净含量报工总重量]
     */
    @Query(value = "SELECT TO_CHAR(h.body_plan_start_date, 'YYYY-MM') as timeX, " +
           "COALESCE(SUM(r.record_qty), 0) as netContentWeight " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND r.process_number = 'GX-011' " +
           "AND r.bus_type = 'BG' " +
           "AND r.record_type = '3' " +
           "AND r.report_status = '0' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "GROUP BY TO_CHAR(h.body_plan_start_date, 'YYYY-MM') " +
           "ORDER BY TO_CHAR(h.body_plan_start_date, 'YYYY-MM')",
           nativeQuery = true)
    List<Map> findNetContentWeightByMonth(@Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate,
                                          @Param("productionLine") String productionLine,
                                          @Param("worklineIds") List<String> worklineIds);

    /**
     * 按周分组查询废料产出分析数据（净含量报工总重量）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param productionLine 生产线ID（可选）
     * @param worklineIds 允许的生产线ID列表
     * @return List<Map> [时间, 净含量报工总重量]
     */
    @Query(value = "SELECT CONCAT('第', TO_CHAR(h.body_plan_start_date, 'WW'), '周') as timeX, " +
           "COALESCE(SUM(r.record_qty), 0) as netContentWeight " +
           "FROM t_bus_order_head h " +
           "JOIN t_bus_order_process_history r ON h.order_no = r.order_no " +
           "WHERE h.is_deleted = '0' " +
           "AND h.bill_type = '普通流程生产订单' " +
           "AND r.process_number = 'GX-011' " +
           "AND r.bus_type = 'BG' " +
           "AND r.record_type = '3' " +
           "AND r.report_status = '0' " +
           "AND h.body_plan_start_date >= :startDate " +
           "AND h.body_plan_start_date <= :endDate " +
           "AND h.nc_cwkid IN (:worklineIds) " +
           "AND (CAST(:productionLine AS VARCHAR) IS NULL OR h.nc_cwkid = CAST(:productionLine AS VARCHAR)) " +
           "GROUP BY TO_CHAR(h.body_plan_start_date, 'WW') " +
           "ORDER BY TO_CHAR(h.body_plan_start_date, 'WW')",
           nativeQuery = true)
    List<Map> findNetContentWeightByWeek(@Param("startDate") Date startDate,
                                         @Param("endDate") Date endDate,
                                         @Param("productionLine") String productionLine,
                                         @Param("worklineIds") List<String> worklineIds);
}
