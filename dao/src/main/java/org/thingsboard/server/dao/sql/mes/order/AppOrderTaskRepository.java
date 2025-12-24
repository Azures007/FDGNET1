package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;

import java.util.List;
import java.util.Map;

/**
 * APP订单任务列表
 * @author hhh
 * @version V1.0
 * @date 2024-12-05
 */
public interface AppOrderTaskRepository extends JpaRepository<TBusOrderHead,Integer>, JpaSpecificationExecutor<TBusOrderHead> {

    /* 获取账号今日任务列表 */
    @Query(value = "select a.order_id as orderId,\n" +
            "a.craft_id as craftId, \n" +
            "t1.craft_name as craftName, \n" +
            "a.order_no as orderNo,\n" +
            "a.body_lot as bodyLot,\n" +
            "a.material_id as materialId,\n" +
            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
            "a.body_material_name as bodyMaterialName,\n" +
            "a.body_plan_prd_qty as billPlanQty,\n" +
            "a.body_unit as bodyUnit,\n" +
            "a.body_unit as bodyUnitStr,\n" +
            "a.body_pot_qty as bodyPotQty,\n" +
            "a.order_status as orderStatus,\n" +
            "a.order_pending_desc as orderPendingDesc,\n" +
            "a.mid_mo_customer_id as midMoCustomerId,\n" +
            "a.mid_mo_customer_name as midMoCustomerName,\n" +
            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
            "a.mid_mo_customer_type as midMoCustomerType,\n" +
            "a.mid_mo_desc as midMoDesc,\n" +
            "null as finishTime,\n" +
            "'1' as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "null as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "a.process_id processId,\n" +
            "a.process_name processName,\n" +
            "a.process_number processNumber, \n" +
            "c.process_id executeProcessId,\n" +
            "c.process_name executeProcessName,\n" +
            "c.process_number executeProcessNumber, \n" +
            "null executeProcessStatus \n" +
            "from (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process=m2.process_id ) as a\n" +
            "join t_sys_craft_info t1 on t1.craft_id=a.craft_id \n" +
            "join t_sys_craft_process_rel b on a.craft_id =b.craft_id \n" +
            "join t_sys_process_info c on b.process_id =c.process_id \n" +
            "join t_sys_process_class_rel d on c.process_id =d.process_id \n" +
            "-- 关联班别获取组长组员，改为视图关联，用班别id和产线id关联\n" +
            "JOIN class_personnel_view cpv on cpv.class_id = d.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            "where TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD')=?2 " +
//            "AND d.class_id IN (\n" +
//            "    SELECT class_id\n" +
//            "    FROM (\n" +
//            "        SELECT a.class_id\n" +
//            "        FROM t_sys_class_group_leader_rel a \n" +
//            "        JOIN t_sys_personnel_info b ON a.personnel_id = b.personnel_id \n" +
//            "        WHERE b.user_id = ?1\n" +
//            "        UNION ALL \n" +
//            "        SELECT class_id\n" +
//            "        FROM t_sys_class_personnel_rel a \n" +
//            "        JOIN t_sys_personnel_info b ON a.personnel_id = b.personnel_id \n" +
//            "        WHERE b.user_id = ?1\n" +
//            "    ) AS t\n" +
//            "    GROUP BY class_id\n" +
//            ")\n" +
            "and a.is_deleted='0' " +
            "AND cpv.user_id = ?1\n" +
            "and (c.process_number=?3 or ?3='' or ?3 is null) " +
            "and (a.body_lot=?4 or ?4='' or ?4 is null)" +
            "",nativeQuery = true)
    Page<Map> getTodayTaskList2(String userId, String currentDateStr, String processNumber, String bodyLot, PageRequest of);

    /* 获取账号今日任务列表 行数 */
    @Query(value = "select count(1)\n" +
            "from (select * from t_bus_order_head m left join t_sys_process_info m2 on m.current_process=m2.process_id ) as a\n" +
            "join t_sys_craft_process_rel b on a.craft_id =b.craft_id \n" +
            "join t_sys_process_info c on b.process_id =c.process_id \n" +
            "join t_sys_process_class_rel d on c.process_id =d.process_id \n" +
            "JOIN class_personnel_view cpv on cpv.class_id = d.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            "where TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD')=?2 " +
            "and a.is_deleted='0' " +
            "AND cpv.user_id = ?1\n" +
            "and (c.process_number=?3 or ?3='' or ?3 is null) " +
            "and (a.body_lot=?4 or ?4='' or ?4 is null)" +
            "",nativeQuery = true)
    int getCountCurrentTask2(String userId, String currentDateStr, String processNumber, String bodyLot);

    /* 获取待生产任务 行数 */
    @Query(value = "SELECT COUNT(1) AS cout\n" +
            "FROM t_bus_order_head a\n" +
            "LEFT JOIN t_sys_process_info m2 ON a.current_process = m2.process_id\n" +
            "JOIN t_bus_order_process_lk b ON a.order_id = b.order_id \n" +
            "JOIN t_bus_order_process c ON b.order_process_id = c.order_process_id \n" +
            "JOIN t_sys_process_info d ON c.process_id = d.process_id \n" +
            "JOIN t_sys_process_class_rel t2 ON t2.process_id = d.process_id \n" +
            "-- 关联班别获取组长组员，改为视图关联，用班别id和产线id关联\n" +
            "JOIN class_personnel_view cpv on cpv.class_id = t2.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            //"LEFT JOIN t_sys_personnel_info p ON c.person_id = p.personnel_id\n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id=?1 \n" +
            "WHERE c.process_status = '0'\n" +
            "AND a.is_deleted = '0'\n" +
            "AND cpv.user_id =?1\n" +
            "-- 合并两种情况的判断条件\n" +
            "AND (\n" +
            "    (c.person_id IS NULL AND (c.type = '1' OR c.type IS NULL))  -- 未分配任务的条件\n" +
            "    OR \n" +
            "    (c.person_id IS NOT NULL AND c.type = '1')  -- 已分配任务的条件\n" +
            ")\n" +
            "-- 公共筛选条件\n" +
            "AND (d.process_number = ?2 OR ?2 = '' OR ?2 IS NULL)\n" +
            "AND (a.body_lot = ?3 OR ?3 = '' OR ?3 IS NULL)\n" +
            "AND (?4 = '' OR \n" +
            "     a.order_no LIKE CONCAT('%', ?4, '%') OR \n" +
            "     a.body_lot LIKE CONCAT('%', ?4, '%') OR \n" +
            "     a.body_material_name LIKE CONCAT('%', ?4, '%') OR \n" +
            "     a.bill_no LIKE CONCAT('%', ?4, '%'))",nativeQuery = true)
    int getWaitTaskCountByUserId2(String userId, String processNumber, String bodyLot,String selectOrField);

    /* 获取待生产任务 */
    @Query(value = "SELECT \n" +
            "    a.order_id AS orderId,\n" +
            "    a.craft_id AS craftId, \n" +
            "    t1.craft_name AS craftName, \n" +
            "    a.order_no AS orderNo, \n" +
            "    a.body_lot AS bodyLot,\n" +
            "    a.body_pot_qty AS bodyPotQty,\n" +
            "    a.material_id AS materialId,\n" +
            "    TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') AS billDate,\n" +
            "    a.body_material_name AS bodyMaterialName,\n" +
            "    a.body_plan_prd_qty AS billPlanQty,\n" +
            "    a.body_unit AS bodyUnit,\n" +
            "    a.body_unit AS bodyUnitStr,\n" +
            "    a.order_status AS orderStatus,\n" +
            "    a.order_pending_desc AS orderPendingDesc,\n" +
            "    a.mid_mo_customer_id AS midMoCustomerId,\n" +
            "    a.mid_mo_customer_name AS midMoCustomerName,\n" +
            "    a.mid_mo_customer_number AS midMoCustomerNumber,\n" +
            "    a.mid_mo_customer_type AS midMoCustomerType,\n" +
            "    a.mid_mo_desc AS midMoDesc,\n" +
            "    NULL AS finishTime,\n" +
            "    c.type AS type, \n" +
            "    a.body_material_specification AS bodyMaterialSpecification, \n" +
            "    c.order_process_id AS orderProcessId, \n" +
            "    a.body_material_id as bodyMaterialId, \n" +
            "    a.body_material_number bodyMaterialNumber, \n" +
            "    TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
            "    TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "    m2.process_id AS processId,\n" +
            "    m2.process_name AS processName,\n" +
            "    m2.process_number AS processNumber,\n" +
            "    d.process_id AS executeProcessId,\n" +
            "    d.process_name AS executeProcessName,\n" +
            "    d.process_number AS executeProcessNumber, \n" +
            "    c.process_status AS executeProcessStatus,\n" +
            "    NULL AS executeRecordTypePd,\n" +
            "    NULL AS recordTypePd\n" +
            "FROM t_bus_order_head a \n" +
            "LEFT JOIN t_sys_process_info m2 ON a.current_process = m2.process_id\n" +
            "JOIN t_sys_craft_info t1 ON t1.craft_id = a.craft_id \n" +
            "JOIN t_bus_order_process_lk b ON a.order_id = b.order_id \n" +
            "JOIN t_bus_order_process c ON b.order_process_id = c.order_process_id \n" +
            "JOIN t_sys_process_info d ON c.process_id = d.process_id \n" +
            "JOIN t_sys_process_class_rel t2 ON t2.process_id = d.process_id \n" +
            "-- 关联班别获取组长组员，改为视图关联，用班别id和产线id关联\n" +
            "JOIN class_personnel_view cpv on cpv.class_id = t2.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            //"LEFT JOIN t_sys_personnel_info f ON c.person_id = f.personnel_id\n" +
            "WHERE c.process_status = '0'\n" +
            "AND a.is_deleted = '0'\n" +
            "AND cpv.user_id = ?1\n" +
            "-- 合并两种情况的判断条件\n" +
            "AND (\n" +
            "    (c.person_id IS NULL AND (c.type = '1' OR c.type IS NULL))  -- 未分配任务的条件\n" +
            "    OR \n" +
            "    (c.person_id IS NOT NULL AND c.type = '1')  -- 已分配任务的条件\n" +
            ")\n" +
            "-- 公共筛选条件\n" +
            "AND (d.process_number = ?2 OR ?2 = '' OR ?2 IS NULL)\n" +
            "AND (a.body_lot = ?3 OR ?3 = '' OR ?3 IS NULL)\n" +
            "AND (\n" +
            "    a.order_no LIKE CONCAT('%', ?4, '%') \n" +
            "    OR a.body_lot LIKE CONCAT('%', ?4, '%') \n" +
            "    OR a.body_material_name LIKE CONCAT('%', ?4, '%') \n" +
            "    OR a.bill_no LIKE CONCAT('%', ?4, '%') \n" +
            "    OR ?4 = ''\n" +
            ")",nativeQuery = true)
    Page<Map> getWaitTaskUserId2(String userId, String processNumber, String bodyLot,String selectOrField, PageRequest of);

    /* 获取生产中任务 */
    @Query(value = "select count(1)\n" +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join t_bus_order_process a2 on a1.order_process_id =a2.order_process_id \n" +
            "left join t_sys_process_info g on g.process_id = a2.process_id \n" +
            "JOIN t_sys_process_class_rel t2 ON t2.process_id = a2.process_id \n" +
            "JOIN class_personnel_view cpv on cpv.class_id = t2.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "join t_bus_user_current_org_line e on e.workline=a.nc_cwkid and e.user_id=?1\n" +
            "where cpv.user_id=?1\n"+
            "and a2.process_status in (?2) and a2.type =?3 and a.is_deleted='0'\n" +
            "and (g.process_number=?4 or ?4='' or ?4 is null) \n" +
            "and (a.body_lot=?5 or ?5='' or ?5 is null) \n" +
            "and (a.order_no like CONCAT('%',?6,'%') or a.body_lot like CONCAT('%',?6,'%') or a.body_material_name like CONCAT('%',?6,'%') or a.bill_no like CONCAT('%',?6,'%') or ?6='')" +
            "",nativeQuery = true)
    int getTaskListCountByPersonIdAndProcessStatusAndOrderProcessType(String userId, List<String> processStatus, String orderProcessType, String processNumber, String bodyLot,String filed);

    /* 获取生产中任务 */
    @Query(value = "select a.order_id as orderId,\n" +
            "a.craft_id as craftId, \n" +
            "t1.craft_name as craftName, \n" +
            "a.order_no as orderNo, \n" +
            "a.body_lot as bodyLot,\n" +
            "a.material_id as materialId,\n" +
            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
            "a.body_material_name as bodyMaterialName,\n" +
            "a.body_plan_prd_qty as billPlanQty,\n" +
            "a.body_unit as bodyUnit,\n" +
            "a.body_unit as bodyUnitStr,\n" +
            "a.body_pot_qty as bodyPotQty,\n" +
            "a.order_status as orderStatus,\n" +
            "a.order_pending_desc as orderPendingDesc,\n" +
            "a.mid_mo_customer_id as midMoCustomerId,\n" +
            "a.mid_mo_customer_name as midMoCustomerName,\n" +
            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
            "a.mid_mo_customer_type as midMoCustomerType,\n" +
            "a.mid_mo_desc as midMoDesc,\n" +
            "null as finishTime,\n" +
            "a2.type as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "a2.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "g.process_id processId,\n" +
            "g.process_name processName,\n" +
            "g.process_number processNumber,\n" +
            "d.process_id executeProcessId,\n" +
            "d.process_name executeProcessName,\n" +
            "d.process_number executeProcessNumber, \n" +
            "a2.process_status executeProcessStatus, \n" +
            "h2.record_type_pd executeRecordTypePd, \n" +
            "a2.old_record_type_pd recordTypePd \n" +
            "from t_bus_order_head a \n" +
            "join t_sys_craft_info t1 on t1.craft_id=a.craft_id \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join t_bus_order_process a2 on a1.order_process_id =a2.order_process_id \n" +
            //"join t_sys_personnel_info b on a2.person_id =b.personnel_id  \n" +
            "JOIN t_sys_process_class_rel t2 ON t2.process_id = a2.process_id \n" +
            "-- 关联班别获取组长组员，改为视图关联，用班别id和产线id关联\n" +
            "JOIN class_personnel_view cpv on cpv.class_id = t2.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where 1=1 \n" +
//            "and t2.class_id in (select class_id  from \n" +
//            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1 \n" +
//            "union all \n" +
//            "select class_id from t_sys_class_personnel_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1) as t\n" +
//            "group by class_id)  " +
            "AND cpv.user_id = ?1\n" +
            "and a2.process_status in (?2) and a2.type =?3 and a.is_deleted='0'\n" +
            "and (d.process_number=?4 or ?4='' or ?4 is null) " +
            "and (a.body_lot=?5 or ?5='' or ?5 is null) \n" +
            "and (a.order_no like CONCAT('%',?6,'%') or a.body_lot like CONCAT('%',?6,'%') or a.body_material_name like CONCAT('%',?6,'%') or a.bill_no like CONCAT('%',?6,'%') or ?6='')" +
            "",nativeQuery = true)
    Page<Map> getTaskListByPersonIdAndProcessStatusAndOrderProcessType(String userId, List<String> processStatus, String orderProcessType, String processNumber, String bodyLot,String selectOrField, PageRequest sort);

    /* 获取生产中任务 行数 */
    @Query(value = "select count(1)\n" +
            "from t_bus_order_head a \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join t_bus_order_process a2 on a1.order_process_id =a2.order_process_id \n" +
            "join t_sys_personnel_info b on a2.person_id =b.personnel_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where 1=1 " +

            "and b.user_id =?1 and a2.process_status in (?2) and a.is_deleted='0' and h.record_type_pd is not null",nativeQuery = true)
    int getTaskListHandOverCountByPersonIdAndProcessStatus(String userId, List<String> processStatus);

    /* 获取未生产任务 行数 */
    @Query(value = "select \n" +
            "count(1) \n" +
            "from  (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process =m2.process_id ) as a\n" +
            "join t_sys_craft_process_rel b on a.craft_id=b.craft_id \n" +
            "join t_sys_process_info c on b.process_id =c.process_id \n" +
            "join t_sys_process_class_rel d on c.process_id =d.process_id \n" +
            "join t_sys_class_group_leader_rel e on d.class_id =e.class_id \n" +
            "join t_sys_personnel_info f on e.personnel_id =f.personnel_id \n" +
            "left join (select c2.process_id,a2.order_id,c2.order_process_id,c2.class_id \n" +
            "from t_bus_order_head a2\n" +
            "join t_bus_order_process_lk b2 on a2.order_id=b2.order_id\n" +
            "join t_bus_order_process c2 on b2.order_process_id =c2.order_process_id\n" +
            ") as p on a.order_id =p.order_id and c.process_id =p.process_id\n" +
            "JOIN class_personnel_view cpv on cpv.class_id = d.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "join t_bus_user_current_org_line e1 on e1.workline =a.nc_cwkid and e1.user_id=?1 \n" +
            "where 1=1 " +
            "AND cpv.user_id =?1\n" +
            "and a.is_deleted='0' and p.order_process_id is null \n" +
            "and (c.process_number=?2 or ?2='' or ?2 is null)\n" +
            "and (a.body_lot=?3 or ?3='' or ?3 is null)" +
            "and ( a.order_no like CONCAT('%',?4,'%') or a.body_lot like CONCAT('%',?4,'%') or a.body_material_name like CONCAT('%',?4,'%') or a.bill_no like CONCAT('%',?4,'%') or ?4='') \n" +
            "",nativeQuery = true)
    int getOffTask2(String userId,String processNumber,String bodyIot,String filed);

    /* 获取未生产任务 */
    @Query(value = "select \n" +
            "a.order_id as orderId,\n" +
            "a.craft_id as craftId, \n" +
            "a.order_no as orderNo,\n" +
            "a.body_pot_qty as bodyPotQty,\n" +
            "a.body_lot as bodyLot,\n" +
            "a.body_material_id as materialId,\n" +
            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
            "a.body_material_name as bodyMaterialName,\n" +
            "a.body_plan_prd_qty as billPlanQty,\n" +
            "a.body_unit as bodyUnit,\n" +
            "a.body_unit as bodyUnitStr,\n" +
            "a.order_status as orderStatus,\n" +
            "a.order_pending_desc as orderPendingDesc,\n" +
            "a.mid_mo_customer_id as midMoCustomerId,\n" +
            "a.mid_mo_customer_name as midMoCustomerName,\n" +
            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
            "a.mid_mo_customer_type as midMoCustomerType,\n" +
            "a.mid_mo_desc as midMoDesc,\n" +
            "null as finishTime,\n" +
            "null as type, \n" +
            "null as bodyMaterialSpecification, \n" +
            "null as orderProcessId, \n" +

            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +

            "a.process_id processId,\n" +
            "a.process_name processName,\n" +
            "a.process_number processNumber, \n" +
            "c.process_id as executeProcessId,\n" +
            "c.process_name as executeProcessName,\n" +
            "c.process_number as executeProcessNumber, \n" +
            "null as executeProcessStatus \n" +
            ",null executeRecordTypePd \n" +
            ",null recordTypePd \n" +
            "from  (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process =m2.process_id ) as a\n" +
            "join t_sys_craft_process_rel b on a.craft_id=b.craft_id \n" +
            "join t_sys_process_info c on b.process_id =c.process_id \n" +
            "join t_sys_process_class_rel d on c.process_id =d.process_id \n" +
            "join t_sys_class_group_leader_rel e on d.class_id =e.class_id \n" +
            "join t_sys_personnel_info f on e.personnel_id =f.personnel_id \n" +
            "left join (select c2.process_id,a2.order_id,c2.order_process_id,c2.class_id \n" +
            "from t_bus_order_head a2\n" +
            "join t_bus_order_process_lk b2 on a2.order_id=b2.order_id\n" +
            "join t_bus_order_process c2 on b2.order_process_id =c2.order_process_id\n" +
            ") as p on a.order_id =p.order_id and c.process_id =p.process_id\n" +
            "where 1=1 \n" +
            "and p.class_id in (\n" +
            "select class_id  from \n" +
            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1 \n" +
            "union all \n" +
            "select class_id from t_sys_class_personnel_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1) as t\n" +
            "group by class_id\n" +
            ")" +
            "and a.is_deleted='0' and p.order_process_id is null \n" +
            "and (c.process_number=?2 or ?2='' or ?2 is null)\n" +
            "and (a.body_lot=?3 or ?3='' or ?3 is null)\n" +
            "and ( a.order_no like CONCAT('%',?4,'%') or a.body_lot like CONCAT('%',?4,'%') or a.body_material_name like CONCAT('%',?4,'%') or a.bill_no like CONCAT('%',?4,'%') or ?4='') \n" +
            "",nativeQuery = true)
    List<Map> getOffTaskList2(String userId, String processNumber, String bodyLot,String selectOrField, PageRequest of);

    /* 已完工任务 */
    @Query(value = "select distinct a.order_id as orderId,\n" +
            "a.craft_id as craftId, \n" +
            "a.order_no as orderNo, \n" +
            "a.body_lot as bodyLot,\n" +
            "a.material_id as materialId,\n" +
            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
            "a.body_material_name as bodyMaterialName,\n" +
            "a.body_plan_prd_qty as billPlanQty,\n" +
            "a.body_unit as bodyUnit,\n" +
            "a.body_unit as bodyUnitStr,\n" +
            "a.order_status as orderStatus,\n" +
            "a.order_pending_desc as orderPendingDesc,\n" +
            "a.mid_mo_customer_id as midMoCustomerId,\n" +
            "a.mid_mo_customer_name as midMoCustomerName,\n" +
            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
            "a.mid_mo_customer_type as midMoCustomerType,\n" +
            "a.mid_mo_desc as midMoDesc,\n" +
            "null as finishTime,\n" +
            "a2.type as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "a2.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "g.process_id processId,\n" +
            "g.process_name processName,\n" +
            "g.process_number processNumber,\n" +
            "d.process_id executeProcessId,\n" +
            "d.process_name executeProcessName,\n" +
            "d.process_number executeProcessNumber, \n" +
            "a2.process_status executeProcessStatus, \n" +
            "h2.record_type_pd executeRecordTypePd, \n" +
            "a2.old_record_type_pd recordTypePd \n" +
            "from t_bus_order_head a \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join t_bus_order_process a2 on a1.order_process_id =a2.order_process_id \n" +
            "join t_sys_personnel_info b on a2.person_id =b.personnel_id  \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD'  and record_type_pd is not null GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where 1=1 \n" +
            "and (b.user_id =?1 or \n" +
            "b.user_id in (select b.user_id \n" +
            "from  (select * from t_sys_personnel_info b1 join t_sys_class_personnel_rel b2 on b1.personnel_id=b2.personnel_id) a\n" +
            "join (select a1.personnel_id,a2.user_id,a1.class_id from t_sys_class_group_leader_rel a1 join t_sys_personnel_info a2 on a1.personnel_id=a2.personnel_id ) b \n" +
            "on a.class_id =b.class_id\n" +
            "where b.user_id is not null and b.user_id <> '' and a.user_id=?1\n" +
            "group by b.user_id ) )\n" +
            "and a2.process_status in (?2) and a.is_deleted='0'",nativeQuery = true)
    List<Map> getTaskListHandOverByPersonIdAndProcessStatus(String userId, List<String> processStatusList, PageRequest of);

    /* 已完工任务 行数 */
    @Query(value = "select COUNT(1)\n" +
            "from t_bus_order_head a \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join (select a.*,b.order_process_id  to_order_process_id " +
            "from t_bus_order_process a left join (select order_process_id ,old_order_process_id from t_bus_order_process where type='3') as  b " +
            "on a.order_process_id =b.old_order_process_id  ) a2 " +
            "on a1.order_process_id =a2.order_process_id \n" +
            "join t_sys_personnel_info b on a2.person_id =b.personnel_id  \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "JOIN t_sys_process_class_rel t2 ON t2.process_id = a2.process_id \n" +
            "-- 关联班别获取组长组员，改为视图关联，用班别id和产线id关联\n" +
            "JOIN class_personnel_view cpv on cpv.class_id = t2.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "where 1=1 " +

//            "and t2.class_id in (\n" +
//            "select class_id  from \n" +
//            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1 \n" +
//            "union all \n" +
//            "select class_id from t_sys_class_personnel_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1) as t\n" +
//            "group by class_id\n" +
//            ")" +
            "AND cpv.user_id = ?1\n" +
            "and a2.process_status in (?2) and a.is_deleted='0'\n" +
            "and (d.process_number=?3 or ?3='' or ?3 is null) \n" +
            "and (a.body_lot=?4 or ?4='' or ?4 is null) " +
            "and ( a.order_no like CONCAT('%',?5,'%') or a.body_lot like CONCAT('%',?5,'%') or a.body_material_name like CONCAT('%',?5,'%') or a.bill_no like CONCAT('%',?5,'%') or ?5='') \n" +
            "",nativeQuery = true)
    int getTaskListCountByPersonIdAndProcessStatus(String userId, List<String> processStatus,String processNumber,String bodyIot,String fileds);

    /* 订单列表（移交） */
    @Query(value = "select a.order_id as orderId,\n" +
            "a.craft_id as craftId, \n" +
            "t1.craft_name as craftName, \n" +
            "a.order_no as orderNo, \n" +
            "a.body_lot as bodyLot,\n" +
            "a.body_pot_qty as bodyPotQty,\n" +
            "a.material_id as materialId,\n" +
            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
            "a.body_material_name as bodyMaterialName,\n" +
            "a.body_plan_prd_qty as billPlanQty,\n" +
            "a.body_unit as bodyUnit,\n" +
            "a.body_unit as bodyUnitStr,\n" +
            "a.order_status as orderStatus,\n" +
            "a.order_pending_desc as orderPendingDesc,\n" +
            "a.mid_mo_customer_id as midMoCustomerId,\n" +
            "a.mid_mo_customer_name as midMoCustomerName,\n" +
            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
            "a.mid_mo_customer_type as midMoCustomerType,\n" +
            "a.mid_mo_desc as midMoDesc,\n" +
            "a.body_plan_prd_qty as bodyPlanPrdQty,\n" +
            "TO_CHAR(a2.finish_time,'YYYY-MM-DD HH24:MI:SS') as finishTime,\n" +
            "a2.type as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "a2.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "g.process_id processId,\n" +
            "g.process_name processName,\n" +
            "g.process_number processNumber,\n" +
            "d.process_id executeProcessId,\n" +
            "d.process_name executeProcessName,\n" +
            "d.process_number executeProcessNumber, \n" +
            "a2.process_status executeProcessStatus, \n" +
            "h2.record_type_pd executeRecordTypePd, \n" +
            "a2.old_record_type_pd recordTypePd \n" +
            ",r3.record_qty as recordT3Qty \n" +
            ",r3.record_manual_qty as recordT3ManualQty \n" +
            ",r3.record_unit as recordT3Unit \n" +
            ",r3.record_unit as recordT3UnitStr \n" +
            ",a2.to_order_process_id toOrderProcessId \n" +
            ",b.name personName \n" +
            ",b2.name as handOverPersonName \n" +
            ",TO_CHAR(a2.old_hand_over_time,'YYYY-MM-DD HH24:MI:SS') as transferTime \n" +
            "from t_bus_order_head a \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            "join t_sys_craft_info t1 on t1.craft_id=a.craft_id \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join (select a.*,b.order_process_id  to_order_process_id from t_bus_order_process a " +
            "left join (select order_process_id ,old_order_process_id from t_bus_order_process where type='3') as  b " +
            "on a.order_process_id =b.old_order_process_id \n ) a2 " +
            "on a1.order_process_id =a2.order_process_id \n" +
            "left join t_sys_personnel_info b on a2.person_id =b.personnel_id  \n" +
            "left join t_sys_personnel_info b2 on a2.old_hand_over_person_id =b2.personnel_id \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "JOIN t_sys_process_class_rel t2 ON t2.process_id = a2.process_id \n" +
            "left join (select r1.record_type_pd,sum(record_qty) as record_qty,sum(record_manual_qty) as record_manual_qty,max(record_unit) as record_unit,order_process_id \n" +
            "from t_bus_order_process_record r1 where r1.record_type='3' and r1.bus_type='BG' \n" +
            "group by order_process_id,record_type_pd) r3 on a1.order_process_id=r3.order_process_id\n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
//            "left join (select * from t_bus_order_process_record tbopr where bus_type ='PD') pd on a1.order_process_id=pd.order_process_id \n" +
            "-- 关联班别获取组长组员，改为视图关联，用班别id和产线id关联\n" +
            "JOIN class_personnel_view cpv on cpv.class_id = t2.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "where 1=1 " +
//            "and t2.class_id in (\n" +
//            "select class_id  from \n" +
//            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1 \n" +
//            "union all \n" +
//            "select class_id from t_sys_class_personnel_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1) as t\n" +
//            "group by class_id\n" +
//            ")" +
            "AND cpv.user_id = ?1\n" +
            "and a2.process_status in (?2) and a.is_deleted='0'\n" +
            "and (d.process_number=?3 or ?3='' or ?3 is null) \n" +
            "and (a.body_lot=?4 or ?4='' or ?4 is null) " +
            "and ( a.order_no like CONCAT('%',?5,'%') or a.body_lot like CONCAT('%',?5,'%') or a.body_material_name like CONCAT('%',?5,'%') or a.bill_no like CONCAT('%',?5,'%') or ?5='') \n" +
            "",nativeQuery = true)
    Page<Map> getTaskListByPersonIdAndProcessStatus(String userId, List<String> processStatus,String processNumber,String bodyLot,String sele, PageRequest sort);

    /* 获取明日任务 行数 */
    @Query(value = "select count(1) \n" +
            "from t_bus_order_head a \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            "join t_sys_craft_info t1 on t1.craft_id=a.craft_id \n" +
            "JOIN t_bus_order_process_lk b ON a.order_id = b.order_id \n" +
            "JOIN t_bus_order_process c ON b.order_process_id = c.order_process_id \n" +
            "JOIN t_sys_process_info d ON c.process_id = d.process_id \n" +
            "JOIN t_sys_process_class_rel t2 ON t2.process_id = d.process_id \n" +
            "-- 关联班别获取组长组员，改为视图关联，用班别id和产线id关联\n" +
            "JOIN class_personnel_view cpv on cpv.class_id = t2.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "where 1=1 \n" +

            "and TO_CHAR(body_plan_start_date,'YYYY-MM-DD') =?2 and a.is_deleted='0' " +
            "and (body_lot=?3 or ?3='' or ?3 is null)" +
//            "and t2.class_id in (\n" +
//            "select class_id  from \n" +
//            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "JOIN t_sys_process_class_rel t2 ON t2.process_id = d.process_id \n" +
//            "where b.user_id =?1 \n" +
//            "union all \n" +
//            "select class_id from t_sys_class_personnel_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1) as t\n" +
//            "group by class_id\n" +
//            ")" +
            "AND cpv.user_id = ?1\n" +
            "", nativeQuery = true)
    Integer getCountNextDayTask(String userId,String currentDateStr,String bodyLot);

    /* 获取明日任务 */
    //查询订单表“计划开工时间”是明日日期（取系统日期）的订单
    @Query(value = "select a.order_id as orderId,\n" +
            "a.craft_id as craftId, \n" +
            "t1.craft_name as craftName, \n" +
            "a.order_no as orderNo,\n" +
            "a.body_lot as bodyLot,\n" +
            "a.material_id as materialId,\n" +
            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
            "a.body_material_name as bodyMaterialName,\n" +
            "a.body_plan_prd_qty as billPlanQty,\n" +
            "a.body_unit as bodyUnit,\n" +
            "a.body_unit as bodyUnitStr,\n" +
            "a.body_pot_qty as bodyPotQty,\n" +
            "a.order_status as orderStatus,\n" +
            "a.order_pending_desc as orderPendingDesc,\n" +
            "a.mid_mo_customer_id as midMoCustomerId,\n" +
            "a.mid_mo_customer_name as midMoCustomerName,\n" +
            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
            "a.mid_mo_customer_type as midMoCustomerType,\n" +
            "a.mid_mo_desc as midMoDesc,\n" +
            "null as finishTime,\n" +
            "'1' as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "null as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "a.process_id processId,\n" +
            "a.process_name processName,\n" +
            "a.process_number processNumber, \n" +
            "c.process_id executeProcessId,\n" +
            "c.process_name executeProcessName,\n" +
            "c.process_number executeProcessNumber, \n" +
            "null executeProcessStatus \n" +
            "from (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process=m2.process_id ) as a\n" +
            "join t_sys_craft_info t1 on t1.craft_id=a.craft_id \n" +
            "join t_sys_craft_process_rel b on a.craft_id =b.craft_id \n" +
            "join t_sys_process_info c on b.process_id =c.process_id \n" +
            "join t_sys_process_class_rel d on c.process_id =d.process_id \n" +
            "-- 关联班别获取组长组员，改为视图关联，用班别id和产线id关联\n" +
            "JOIN class_personnel_view cpv on cpv.class_id = d.class_id and cpv.nc_cwkid=a.nc_cwkid \n" +
            "join t_bus_user_current_org_line e on e.workline =a.nc_cwkid and e.user_id= ?1 \n" +
            "where TO_CHAR(body_plan_start_date,'YYYY-MM-DD') =?2  " +
//            "AND d.class_id IN (\n" +
//            "    SELECT class_id\n" +
//            "    FROM (\n" +
//            "        SELECT a.class_id\n" +
//            "        FROM t_sys_class_group_leader_rel a \n" +
//            "        JOIN t_sys_personnel_info b ON a.personnel_id = b.personnel_id \n" +
//            "        WHERE b.user_id = ?1\n" +
//            "        UNION ALL \n" +
//            "        SELECT class_id\n" +
//            "        FROM t_sys_class_personnel_rel a \n" +
//            "        JOIN t_sys_personnel_info b ON a.personnel_id = b.personnel_id \n" +
//            "        WHERE b.user_id = ?1\n" +
//            "    ) AS t\n" +
//            "    GROUP BY class_id\n" +
//            ")\n" +
            "AND cpv.user_id = ?1\n" +
            "and a.is_deleted='0' " +
            "and (a.body_lot=?3 or ?3='' or ?3 is null)" +
            "",nativeQuery = true)
    Page<Map> getNextDayTaskList(String userId,String currentDateStr,String bodyLot, PageRequest sort);

//    /* 移交待生产任务 行数 */
//    @Query(value = "select sum(cout) from (\n" +
//            "select count(1) cout from (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process =m2.process_id ) as a " +
//            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
//            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
//            "join t_sys_personnel_info d on c.person_id =d.personnel_id\n" +
//            "join t_sys_process_info f on c.process_id =f.process_id \n" +
//            "where c.process_status='0' and c.type='2' " +
//            "and c.class_id in (\n" +
//            "select class_id  from \n" +
//            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1 \n" +
//            "union all \n" +
//            "select class_id from t_sys_class_personnel_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1) as t\n" +
//            "group by class_id\n" +
//            ")" +
//            "and a.is_deleted='0'\n" +
//            "and (f.process_number=?2 or ?2='' or ?2 is null) " +
//            "and (a.body_lot=?3 or ?3='' or ?3 is null) " +
//            "and ( a.order_no like CONCAT('%',?4,'%') or a.body_lot like CONCAT('%',?4,'%') or a.body_material_name like CONCAT('%',?4,'%') or a.bill_no like CONCAT('%',?4,'%') or ?4='') \n" +
//            ") as t",nativeQuery = true)
//    int getWaitTaskCountByUserIdHandOver(String userId,String processNumber,String bodyIot,String flies);
//
//    /* 移交待生产任务 */
//    @Query(value = "select a.order_id as orderId,\n" +
//            "a.craft_id as craftId, \n" +
//            "a.order_no as orderNo, \n" +
//            "a.body_lot as bodyLot,\n" +
//            "a.material_id as materialId,\n" +
//            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
//            "a.body_material_name as bodyMaterialName,\n" +
//            "a.body_plan_prd_qty as billPlanQty,\n" +
//            "a.body_unit as bodyUnit,\n" +
//            "a.body_unit as bodyUnitStr,\n" +
//            "a.order_status as orderStatus,\n" +
//            "a.order_pending_desc as orderPendingDesc,\n" +
//            "a.mid_mo_customer_id as midMoCustomerId,\n" +
//            "a.mid_mo_customer_name as midMoCustomerName,\n" +
//            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
//            "a.mid_mo_customer_type as midMoCustomerType,\n" +
//            "a.mid_mo_desc as midMoDesc,\n" +
//            "null as finishTime,\n" +
//            "c.type as type, \n" +
//            "a.body_material_specification bodyMaterialSpecification, \n" +
//            "c.order_process_id as orderProcessId, \n" +
//            "null as bodyMaterialId, \n" +
//            "null as bodyMaterialNumber, \n" +
//            "null as bodyPlanStartDate, \n" +
//            "null as bodyPlanFinishDate, \n" +
//            "a.process_id processId,\n" +
//            "a.process_name processName,\n" +
//            "a.process_number processNumber,\n" +
//            "f.process_id executeProcessId,\n" +
//            "f.process_name executeProcessName,\n" +
//            "f.process_number executeProcessNumber, \n" +
//            "c.process_status executeProcessStatus, \n" +
//            "h2.record_type_pd executeRecordTypePd, \n" +
//            "c.old_record_type_pd recordTypePd \n" +
//            "from (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process =m2.process_id ) as a\n" +
//            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
//            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
//            "join t_sys_process_info f on c.process_id =f.process_id \n" +
//            "join t_sys_personnel_info d on c.person_id =d.personnel_id\n" +
//            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on c.order_process_id = h2.order_process_id \n" +
//            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on c.old_order_process_id = h.order_process_id \n" +
//            "where c.process_status='0' and c.type='2' " +
//            "and c.class_id in (\n" +
//            "select class_id  from \n" +
//            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1 \n" +
//            "union all \n" +
//            "select class_id from t_sys_class_personnel_rel a \n" +
//            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
//            "where b.user_id =?1) as t\n" +
//            "group by class_id\n" +
//            ")" +
//            "and a.is_deleted='0'\n" +
//            "and (f.process_number=?2 or ?2='' or ?2 is null) " +
//            "and (a.body_lot=?3 or ?3='' or ?3 is null) " +
//            "and ( a.order_no like CONCAT('%',?4,'%') or a.body_lot like CONCAT('%',?4,'%') or a.body_material_name like CONCAT('%',?4,'%') or a.bill_no like CONCAT('%',?4,'%') or ?4='') \n" +
//            "",nativeQuery = true)
//    List<Map> getWaitTaskUserIdHandOver(String userId, String processNumber, String bodyLot,String fileds, PageRequest of);

    /* 转移列表 */
//    @Query(value = "select a.order_id as orderId,\n" +
//            "a.craft_id as craftId, \n" +
//            "a.order_no as orderNo, \n" +
//            "a.body_lot as bodyLot,\n" +
//            "a.material_id as materialId,\n" +
//            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
//            "a.body_material_name as bodyMaterialName,\n" +
//            "a.body_plan_prd_qty as billPlanQty,\n" +
//            "a.body_unit as bodyUnit,\n" +
//            "a.body_unit as bodyUnitStr,\n" +
//            "a.order_status as orderStatus,\n" +
//            "a.order_pending_desc as orderPendingDesc,\n" +
//            "a.mid_mo_customer_id as midMoCustomerId,\n" +
//            "a.mid_mo_customer_name as midMoCustomerName,\n" +
//            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
//            "a.mid_mo_customer_type as midMoCustomerType,\n" +
//            "a.mid_mo_desc as midMoDesc,\n" +
//            "null as finishTime,\n" +
//            "c.type as type, \n" +
//            "a.body_material_specification bodyMaterialSpecification, \n" +
//            "c.old_order_process_id as orderProcessId, \n" +
//            "a.body_material_id as bodyMaterialId, \n" +
//            "a.body_material_number as bodyMaterialNumber, \n" +
//            "TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
//            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
//            "g.process_id processId,\n" +
//            "g.process_name processName,\n" +
//            "g.process_number processNumber,\n" +
//            "a.process_id executeProcessId,\n" +
//            "a.process_name executeProcessName,\n" +
//            "a.process_number executeProcessNumber, \n" +
//            "null executeProcessStatus \n" +
//            ",null executeRecordTypePd \n" +
//            ",null recordTypePd ,\n" +
//            "c.order_process_id  to_order_process_id\n" +
//            "from (select * from t_bus_order_head a left join t_sys_process_info a2 on a.current_process=a2.process_id ) as a \n" +
//            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
//            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
//            "join t_sys_personnel_info d on c.person_id =d.personnel_id \n" +
//            "left join t_sys_process_info g on c.process_id = g.process_id \n" +
//            "where c.process_status='0' and c.type='3' and c.old_order_process_id is not null and d.user_id =?1\n" +
//            "and (g.process_number=?2 or ?2='' or ?2 is null)\n" +
//            "and(a.body_lot=?3 or ?3='' or ?3 is null)" +
//            "",nativeQuery = true)
//    List<Map> listShiftNoAcceptTaskList(String userId, String processNumber, String bodyLot, PageRequest of);
//
//    /* 转移列表 行数 */
//    @Query(value = "select count(1)\n" +
//            "from t_bus_order_head a \n" +
//            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
//            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
//            "join t_sys_personnel_info d on c.person_id =d.personnel_id \n" +
//            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
//            "where c.process_status='0' and c.type='3' and c.old_order_process_id is not null and d.user_id =?1 " +
//            "and (g.process_number=?2 or ?2='' or ?2 is null)\n" +
//            "and(a.body_lot=?3 or ?3='' or ?3 is null)" +
//            "",nativeQuery = true)
//    int countShiftNoAcceptTaskList(String userId,String processNumber,String bodyIot);

//    /* 转移记录列表 */
//    @Query(value = "select d.user_id,a.order_id as orderId,\n" +
//            "a.craft_id as craftId, \n" +
//            "a.order_no as orderNo, \n" +
//            "a.body_lot as bodyLot,\n" +
//            "a.material_id as materialId,\n" +
//            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
//            "a.body_material_name as bodyMaterialName,\n" +
//            "a.body_plan_prd_qty as billPlanQty,\n" +
//            "a.body_unit as bodyUnit,\n" +
//            "a.body_unit as bodyUnitStr,\n" +
//            "a.order_status as orderStatus,\n" +
//            "a.order_pending_desc as orderPendingDesc,\n" +
//            "a.mid_mo_customer_id as midMoCustomerId,\n" +
//            "a.mid_mo_customer_name as midMoCustomerName,\n" +
//            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
//            "a.mid_mo_customer_type as midMoCustomerType,\n" +
//            "a.mid_mo_desc as midMoDesc,\n" +
//            "null as finishTime,\n" +
//            "c.type as type, \n" +
//            "a.body_material_specification bodyMaterialSpecification, \n" +
//            "c.order_process_id as orderProcessId, \n" +
//            "a.body_material_id as bodyMaterialId, \n" +
//            "a.body_material_number as bodyMaterialNumber, \n" +
//            "TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
//            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
//            "g.process_id processId,\n" +
//            "g.process_name processName,\n" +
//            "g.process_number processNumber,\n" +
//            "a.process_id executeProcessId,\n" +
//            "a.process_name executeProcessName,\n" +
//            "a.process_number executeProcessNumber, \n" +
//            "null executeProcessStatus \n" +
//            ",null executeRecordTypePd \n" +
//            ",null recordTypePd ,\n" +
//            "c.to_orderProcess_id  to_order_process_id\n" +
//            "from (select * from t_bus_order_head a left join t_sys_process_info a2 on a.current_process=a2.process_id ) as a \n" +
//            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
//            "join (select a.*,b.order_process_id as to_orderProcess_id \n" +
//            "from t_bus_order_process a join t_bus_order_process b on a.order_process_id =b.old_order_process_id \n" +
//            "where a.type='3') c on b.order_process_id =c.order_process_id \n" +
//            "join t_sys_personnel_info d on c.person_id =d.personnel_id \n" +
//            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
//            "where  d.user_id =?1\n" +
//            "and (a.body_lot=?3 or ?3='' or ?3 is null)\n " +
//            "and(g.process_number=?2 or ?2='' or ?2 is null)" +
//            "",nativeQuery = true)
//    List<Map> listShiftTaskList(String userId, String processNumber, String bodyLot, PageRequest of);
//
//    /* 转移记录列表 行数 */
//    @Query(value = "select count(1)\n" +
//            "from t_bus_order_head a \n" +
//            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
//            "join (select a.*,b.order_process_id as to_orderProcess_id \n" +
//            "from t_bus_order_process a join t_bus_order_process b on a.order_process_id =b.old_order_process_id \n" +
//            "where a.type='3') c on b.order_process_id =c.order_process_id \n" +
//            "join t_sys_personnel_info d on c.person_id =d.personnel_id \n" +
//            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
//            "where  d.user_id =?1 " +
//            "and (a.body_lot=?3 or ?3='' or ?3 is null)\n " +
//            "and(g.process_number=?2 or ?2='' or ?2 is null)" +
//            "",nativeQuery = true)
//    int countShiftTaskList(String userId, String processNumber, String bodyLot);

//    /* 获取已完工任务(拌料工序) */
//    @Query(value = "select a.order_id as orderId,\n" +
//            "a.craft_id as craftId, \n" +
//            "a.order_no as orderNo,\n" +
//            "a.body_lot as bodyLot,\n" +
//            "a.body_material_id as materialId,\n" +
//            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
//            "a.body_material_name as bodyMaterialName,\n" +
//            "a.body_plan_prd_qty as billPlanQty,\n" +
//            "a.body_unit as bodyUnit,\n" +
//            "a.body_unit as bodyUnitStr,\n" +
//            "a.order_status as orderStatus,\n" +
//            "a.order_pending_desc as orderPendingDesc,\n" +
//            "a.mid_mo_customer_id as midMoCustomerId,\n" +
//            "a.mid_mo_customer_name as midMoCustomerName,\n" +
//            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
//            "a.mid_mo_customer_type as midMoCustomerType,\n" +
//            "a.mid_mo_desc as midMoDesc,\n" +
//            "null as finishTime,\n" +
//            "null as type, \n" +
//            "null as bodyMaterialSpecification, \n" +
//            "c.order_process_id as orderProcessId, \n" +
//            "null as bodyMaterialId, \n" +
//            "null as bodyMaterialNumber, \n" +
//            "null as bodyPlanStartDate, \n" +
//            "null as bodyPlanFinishDate, \n" +
//            "d.process_id processId,\n" +
//            "d.process_name processName,\n" +
//            "d.process_number processNumber, \n" +
//            "null as executeProcessId,\n" +
//            "null as executeProcessName,\n" +
//            "null as executeProcessNumber, \n" +
//            "null as executeProcessStatus \n" +
//            ",null executeRecordTypePd \n" +
//            ",null recordTypePd \n" +
//            "from t_bus_order_head as a\n" +
//            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
//            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
//            "join t_sys_process_info d on c.process_id =d.process_id \n" +
//            "where a.order_status='1' \n" +
//            "and c.process_status ='1' and d.process_number ='GX004' and c.order_process_id<>?1" ,nativeQuery = true)
//    List<Map> listFinishProcessTaskList(Integer orderProcessId,PageRequest of);

    /* 获取用户的移交记录 */
    @Query(value = "select  distinct a.order_id as orderId,\n" +
            "a.craft_id as craftId, \n" +
            "a.order_no as orderNo, \n" +
            "a.body_lot as bodyLot,\n" +
            "a.material_id as materialId,\n" +
            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
            "a.body_material_name as bodyMaterialName,\n" +
            "a.body_plan_prd_qty as billPlanQty,\n" +
            "a.body_unit as bodyUnit,\n" +
            "a.body_unit as bodyUnitStr,\n" +
            "a.order_status as orderStatus,\n" +
            "a.order_pending_desc as orderPendingDesc,\n" +
            "a.mid_mo_customer_id as midMoCustomerId,\n" +
            "a.mid_mo_customer_name as midMoCustomerName,\n" +
            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
            "a.mid_mo_customer_type as midMoCustomerType,\n" +
            "a.mid_mo_desc as midMoDesc,\n" +
            "null as finishTime,\n" +
            "a2.type as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "a2.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanStartDate, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "g.process_id processId,\n" +
            "g.process_name processName,\n" +
            "g.process_number processNumber,\n" +
            "d.process_id executeProcessId,\n" +
            "d.process_name executeProcessName,\n" +
            "d.process_number executeProcessNumber, \n" +
            "a2.process_status executeProcessStatus, \n" +
            "h2.record_type_pd executeRecordTypePd, \n" +
            "a2.old_record_type_pd recordTypePd \n" +
            "from t_bus_order_process a2 \n" +
            "left join t_bus_order_process_lk a1 on a1.order_process_id =a2.order_process_id \n" +
            "left join t_bus_order_head a on a.order_id=a1.order_id \n" +
            "left join t_sys_personnel_info b on  a2.hand_over_person_id =b.personnel_id \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where b.user_id =?1  and a.is_deleted='0'  \n" +
            "and a2.type='2' ",nativeQuery = true)
    List<Map> getHandOverRecordsByPersonId(String userId, PageRequest of);

    /* 获取用户的移交记录 行数 */
    @Query(value = "select count(distinct a.order_no)\n" +
            "from t_bus_order_process a2 \n" +
            "left join t_bus_order_process_lk a1 on a1.order_process_id =a2.order_process_id \n" +
            "left join t_bus_order_head a on a.order_id=a1.order_id \n" +
            "left join t_sys_personnel_info b on  a2.hand_over_person_id =b.personnel_id \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where b.user_id =?1  and a.is_deleted='0'  \n" +
            "and a2.type='2' ",nativeQuery = true)
    int getHandOverRecordsByPersonId(String userId);

    /* 获取已完工任务(拌料工序) 行数 */
    @Query(value = "select count(1)\n" +
            "from t_bus_order_head as a\n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info d on c.process_id =d.process_id \n" +
            "where a.order_status='1' \n" +
            "and c.process_status ='1' and d.process_number ='GX004' and c.order_process_id<>?1",nativeQuery = true)
    int countFinishProcessTaskList(Integer orderProcessId);

    /* 获取订单任务详情 */
    @Query(value = "select a.order_id as orderId,\n" +
            "a.craft_id as craftId, \n" +
            "a.order_no as orderNo, \n" +
            "a.body_lot as bodyLot,\n" +
            "a.material_id as materialId,\n" +
            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as billDate,\n" +
            "a.body_material_name as bodyMaterialName,\n" +
            "a.body_plan_prd_qty as billPlanQty,\n" +
            "a.body_unit as bodyUnit,\n" +
            "a.body_unit as bodyUnitStr,\n" +
            "a.order_status as orderStatus,\n" +
            "a.order_pending_desc as orderPendingDesc,\n" +
            "a.mid_mo_customer_id as midMoCustomerId,\n" +
            "a.mid_mo_customer_name as midMoCustomerName,\n" +
            "a.mid_mo_customer_number as midMoCustomerNumber,\n" +
            "a.mid_mo_customer_type as midMoCustomerType,\n" +
            "a.mid_mo_desc as midMoDesc,\n" +
            "null as finishTime,\n" +
            "coalesce(c.type,'1') as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "c.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
            "a.body_plan_start_date as bodyPlanStartDate, \n" +
            "a.body_plan_finish_date as bodyPlanFinishDate, \n" +
            "a.process_id processId,\n" +
            "a.process_name processName,\n" +
            "a.process_number processNumber,\n" +
            "f.process_id executeProcessId,\n" +
            "f.process_name executeProcessName,\n" +
            "f.process_number executeProcessNumber, \n" +
            "c.process_status executeProcessStatus, \n" +
            "h2.record_type_pd executeRecordTypePd, \n" +
            "h.record_type_pd recordTypePd \n" +
            "from (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process =m2.process_id ) as a\n" +
            "left join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info f on c.process_id =f.process_id \n" +
            "join t_sys_personnel_info d on c.person_id =d.personnel_id\n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on c.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on c.old_order_process_id = h.order_process_id \n" +
            "where c.order_process_id =?1",nativeQuery = true)
    Map getTaskListVo(Integer orderProcessId);

    // 获取任务列表的批号
    @Query(value = "select body_lot\n" +
            "from (select * from t_bus_order_head m left join t_sys_process_info m2 on m.current_process=m2.process_id ) as a\n" +
            "join t_sys_craft_process_rel b on a.craft_id =b.craft_id \n" +
            "join t_sys_process_info c on b.process_id =c.process_id \n" +
            "join t_sys_process_class_rel d on c.process_id =d.process_id \n" +
            "join t_sys_class_group_leader_rel e on d.class_id =e.class_id \n" +
            "join t_sys_personnel_info f on e.personnel_id =f.personnel_id \n" +
            "where TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD')=?2 and f.user_id =?1 and a.is_deleted='0' and  body_lot is not null \n" +
            "group by body_lot order by body_lot desc",nativeQuery = true)
    List<String> listTaskType1(String userId, String currentDateStr);

    @Query(value = "select body_lot from (\n" +
            "select body_lot \n" +
            "from (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process =m2.process_id ) as a\n" +
            "join t_bus_order_process_lk b on a.order_id=b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info d on c.process_id =d.process_id \n" +
            "where c.process_status='0' and person_id is null " +
            "and  c.class_id in (\n" +
            "select class_id  from \n" +
            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1 \n" +
            "union all \n" +
            "select class_id from t_sys_class_personnel_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1) as t\n" +
            "group by class_id\n" +
            ") " +
            "and (c.type='1' or c.type is null) and a.is_deleted='0'\n" +
            "union \n" +
            "select body_lot \n" +
            "from (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process =m2.process_id ) as a\n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info f on c.process_id =f.process_id \n" +
            "join t_sys_personnel_info d on c.person_id =d.personnel_id\n" +
            "where c.process_status='0' and c.type='1' " +
            "and c.class_id in (\n" +
            "select class_id  from \n" +
            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1 \n" +
            "union all \n" +
            "select class_id from t_sys_class_personnel_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1) as t\n" +
            "group by class_id\n" +
            ")\n" +
            "and a.is_deleted='0'\n" +
            ") as a  group by body_lot  order by body_lot desc",nativeQuery = true)
    List<String> listTaskType2(String userId);

    @Query(value = "select body_lot \n" +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join t_bus_order_process a2 on a1.order_process_id =a2.order_process_id \n" +
            "join t_sys_personnel_info b on a2.person_id =b.personnel_id  \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where 1=1 \n" +
            "and a2.class_id in (select class_id  from \n" +
            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1 \n" +
            "union all \n" +
            "select class_id from t_sys_class_personnel_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1) as t\n" +
            "group by class_id)  " +
            "and a2.process_status in (?2) and a2.type =?3 and a.is_deleted='0' \n" +
            "group by body_lot order by  body_lot desc" +
            "",nativeQuery = true)
    List<String> listTaskType3(String userId, List<String> processStatusList, String orderProcessType);
    @Query(value = "select \n" +
            "body_lot \n" +
            "from  (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process =m2.process_id ) as a\n" +
            "join t_sys_craft_process_rel b on a.craft_id=b.craft_id \n" +
            "join t_sys_process_info c on b.process_id =c.process_id \n" +
            "join t_sys_process_class_rel d on c.process_id =d.process_id \n" +
            "join t_sys_class_group_leader_rel e on d.class_id =e.class_id \n" +
            "join t_sys_personnel_info f on e.personnel_id =f.personnel_id \n" +
            "left join (select c2.process_id,a2.order_id,c2.order_process_id,c2.class_id \n" +
            "from t_bus_order_head a2\n" +
            "join t_bus_order_process_lk b2 on a2.order_id=b2.order_id\n" +
            "join t_bus_order_process c2 on b2.order_process_id =c2.order_process_id\n" +
            ") as p on a.order_id =p.order_id and c.process_id =p.process_id\n" +
            "where 1=1 \n" +
            "and p.class_id in (\n" +
            "select class_id  from \n" +
            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1 \n" +
            "union all \n" +
            "select class_id from t_sys_class_personnel_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1) as t\n" +
            "group by class_id\n" +
            ")" +
            "and a.is_deleted='0' and p.order_process_id is null \n" +
            "",nativeQuery = true)
    List<String> listTaskType4(String userId);
    @Query(value = "select body_lot \n" +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join (select a.*,b.order_process_id  to_order_process_id from t_bus_order_process a " +
            "left join (select order_process_id ,old_order_process_id from t_bus_order_process where type='3') as  b " +
            "on a.order_process_id =b.old_order_process_id \n ) a2 " +
            "on a1.order_process_id =a2.order_process_id \n" +
            "join t_sys_personnel_info b on a2.person_id =b.personnel_id  \n" +
            "left join t_sys_personnel_info b2 on a2.old_hand_over_person_id =b2.personnel_id \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "left join (select r1.record_type_pd,sum(record_qty) as record_qty,sum(record_manual_qty) as record_manual_qty,max(record_unit) as record_unit,order_process_id \n" +
            "from t_bus_order_process_record r1 where r1.record_type='3' and r1.bus_type='BG' \n" +
            "group by order_process_id,record_type_pd) r3 on a1.order_process_id=r3.order_process_id\n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
//            "left join (select * from t_bus_order_process_record tbopr where bus_type ='PD') pd on a1.order_process_id=pd.order_process_id \n" +
            "where 1=1 " +
            "and a2.class_id in (\n" +
            "select class_id  from \n" +
            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1 \n" +
            "union all \n" +
            "select class_id from t_sys_class_personnel_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1) as t\n" +
            "group by class_id\n" +
            ")" +
            "and a2.process_status in (?2) and a.is_deleted='0' group by body_lot  order by body_lot desc\n" +
            "",nativeQuery = true)
    List<String> listTaskType5(String userId, List<String> processStatusList2);
    @Query(value = "select body_lot \n" +
            "from (select * from t_bus_order_head a left join t_sys_process_info m2 on a.current_process =m2.process_id ) as a\n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info f on c.process_id =f.process_id \n" +
            "join t_sys_personnel_info d on c.person_id =d.personnel_id\n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on c.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on c.old_order_process_id = h.order_process_id \n" +
            "where c.process_status='0' and c.type='2' " +
            "and c.class_id in (\n" +
            "select class_id  from \n" +
            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1 \n" +
            "union all \n" +
            "select class_id from t_sys_class_personnel_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1) as t\n" +
            "group by class_id\n" +
            ")" +
            "and a.is_deleted='0'  group by body_lot order by body_lot desc\n" +
            "",nativeQuery = true)
    List<String> listTaskType6(String userId);
    @Query(value = "select body_lot \n" +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join t_bus_order_process a2 on a1.order_process_id =a2.order_process_id \n" +
            "join t_sys_personnel_info b on a2.person_id =b.personnel_id  \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where 1=1 \n" +
            "and a2.class_id in (select class_id  from \n" +
            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1 \n" +
            "union all \n" +
            "select class_id from t_sys_class_personnel_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1) as t\n" +
            "group by class_id)  " +
            "and a2.process_status in (?2) and a2.type =?3 and a.is_deleted='0'\n" +
            "group by body_lot order by  body_lot desc" +
            "",nativeQuery = true)
    List<String> listTaskType7(String s, List<String> processStatusList,String type);
    @Query(value = "select body_lot " +
            "from t_bus_order_head a \n" +
            "where TO_CHAR(body_plan_start_date,'YYYY-MM-DD') =?1 and a.is_deleted='0' and  body_lot is not null " +
            "group by body_lot order by body_lot desc", nativeQuery = true)
    List<String> listTaskType8(String format);
}
