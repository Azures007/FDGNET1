package org.thingsboard.server.dao.sql.order;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TBusOrderHead;
import org.thingsboard.server.dao.dto.TBusOrderDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.order
 * @date 2022/4/13 11:01
 * @Description:
 */
public interface OrderHeadRepository extends JpaRepository<TBusOrderHead,Integer>, JpaSpecificationExecutor<TBusOrderHead> {

    @Query(value = "select a.order_id,\n" +
            "a.bill_no,\n" +
            "a.body_lot,\n" +
            "TO_CHAR(a.bill_date,'YYYY-MM-DD HH24:MI:SS') as bill_date,\n" +
            "a.order_status,\n" +
            "a.body_prd_dept,\n" +
            "a.body_plan_prd_qty,\n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as body_plan_finish_date,\n" +
            "a.body_material_specification,\n" +
            "a.bill_type,\n" +
            "a.current_process,\n" +
            "f.name as class_id \n" +
            "from t_bus_order_head a \n" +
            "join t_sys_class f on a.class_id =f.class_id \n" +
            "where 1=1 \n" +
            "and (a.bill_no like %:#{#pageOrderDto.billNo}% or :#{#pageOrderDto.billNo} is null or :#{#pageOrderDto.billNo} ='') \n" +
            "and (a.order_status = :#{#pageOrderDto.orderStatus} or :#{#pageOrderDto.orderStatus} is null or :#{#pageOrderDto.orderStatus} ='') \n" +
            "and (a.current_process = :#{#pageOrderDto.currentProcess} or :#{#pageOrderDto.currentProcess} is null or :#{#pageOrderDto.currentProcess} =0) \n" +
            "and (a.class_id = :#{#pageOrderDto.classId} or :#{#pageOrderDto.classId} is null or :#{#pageOrderDto.classId} = 0) \n" +
            //单据日期
            "and (a.bill_date >= :#{#pageOrderDto.billDateStart} ) \n" +
            "and (a.bill_date <= :#{#pageOrderDto.billDateEnd} ) \n" +
            //计划完工日期为NULL待處理
            "and (a.body_plan_finish_date >= :#{#pageOrderDto.planStartDateStart} or :#{#pageOrderDto.planStartDateStart} = '1900-01-01 00:00:00.0') \n" +
            "and (a.body_plan_finish_date <= :#{#pageOrderDto.planStartDateEnd} or :#{#pageOrderDto.planStartDateEnd} = '9999-01-01 00:00:00.0') \n" +
            " order by a.bill_date desc \n"+
            " limit :size offset :current"+
            "",nativeQuery = true)
    List<Map> pageOrderHead(@Param("current") Integer current,
                            @Param("size") int size,
                            @Param("pageOrderDto") TBusOrderDto pageOrderDto);

    @Query(value = "select count(1) \n" +
            "from t_bus_order_head a \n" +
            "where 1=1 \n" +
            "and (a.bill_no like %:#{#pageOrderDto.billNo}% or :#{#pageOrderDto.billNo} is null or :#{#pageOrderDto.billNo} ='') \n" +
            "and (a.order_status like %:#{#pageOrderDto.orderStatus}% or :#{#pageOrderDto.orderStatus} is null or :#{#pageOrderDto.orderStatus} ='') \n" +
            "and (a.current_process = :#{#pageOrderDto.currentProcess} or :#{#pageOrderDto.currentProcess} is null or :#{#pageOrderDto.currentProcess} =0) \n" +
            "and (a.class_id = :#{#pageOrderDto.classId} or :#{#pageOrderDto.classId} is null or :#{#pageOrderDto.classId} = 0) \n" +
            //单据日期
            "and (a.bill_date >= :#{#pageOrderDto.billDateStart} ) \n" +
            "and (a.bill_date <= :#{#pageOrderDto.billDateEnd} ) \n" +
            //计划完工日期为NULL待處理
            "and (a.body_plan_finish_date >= :#{#pageOrderDto.planStartDateStart} or :#{#pageOrderDto.planStartDateStart} = '1900-01-01 00:00:00.0') \n" +
            "and (a.body_plan_finish_date <= :#{#pageOrderDto.planStartDateEnd} or :#{#pageOrderDto.planStartDateEnd} = '9999-01-01 00:00:00.0') \n" +
            "",nativeQuery = true)
    int pageOrderHeadCount(@Param("pageOrderDto") TBusOrderDto pageOrderDto);

    @Modifying
    @Query("update TBusOrderHead set orderStatus = ?2,updatedName = ?3,updatedTime = ?4 where orderId = ?1")
    void updateBillStatus(Integer orderId, String orderStatus, String updatedName, Date updatedTime);

    @Modifying
    @Query("update TBusOrderHead set craftId = ?2,craftDesc = ?3,updatedName = ?4,updatedTime = ?5 where orderId = ?1")
    void updateRouteId(Integer orderId, Integer craftId, String craftDesc, String updatedName, Date updatedTime);

    @Query(value = "select * from t_bus_order_head a where a.class_id=?1 and TO_CHAR(a.body_plan_start_date,'YYYY-MM-DD')=?2 and a.is_deleted='1'", nativeQuery = true)
    List<TBusOrderHead> getByClassIdAndBodyPlanStartDate(Integer classId, String date);

     @Query(value = "select pp.order_ppbom_id ,h.order_id, h.order_no ,h.body_lot ,h.body_prd_dept , " +
            "h.body_material_id ,h.body_material_number ,h.body_material_name ," +
            "h.body_material_specification , h.body_plan_prd_qty ,pp.material_id ," +
            "pp.material_number ,pp.material_name ,pp.material_specification ," +
            "pp.unit unit, " +
            "pp.mid_ppbom_entry_weigh_mes_unit,pp.mid_ppbom_entry_weigh_devept_unit,pp.mid_ppbom_entry_weigh_mes_qty,pp.mid_ppbom_entry_weigh_devept_qty, " +
            "coalesce(mm.kd_material_is_peel,0) as kd_material_is_peel " +
            "from t_bus_order_head  h left join t_bus_order_ppbom_lk lk on h.order_id=lk.order_id" +
            " left join t_bus_order_ppbom pp on lk.order_ppbom_id=pp.order_ppbom_id " +
            " left join mid_material mm on pp.material_id=mm.kd_material_id " +
            " where h.order_id=:orderId and pp.mid_ppbom_entry_input_process=:midPpbomEntryInputProcess and h.is_deleted='1' ", nativeQuery = true)
    List<Map> getOrderPPbom(@Param("orderId") Integer orderId, @Param("midPpbomEntryInputProcess") String midPpbomEntryInputProcess);

//    @Query(value = "select pp.order_ppbom_id ,pp.material_id ," +
//            "pp.material_number ,pp.material_name ,pp.material_specification ," +
//            "pp.unit unit,pp.mid_ppbom_entry_material_negative_error,pp.mid_ppbom_entry_material_positive_error," +
//            "pp.mid_ppbom_entry_material_standard,pp.must_qty," +
//            "pp.mid_ppbom_entry_weigh_mes_unit,pp.mid_ppbom_entry_weigh_devept_unit,pp.mid_ppbom_entry_weigh_mes_qty,pp.mid_ppbom_entry_weigh_devept_qty, " +
//            "coalesce(mm.kd_material_is_peel,0) as kd_material_is_peel ,pp.mid_ppbom_entry_replace_group,pp.mid_ppbom_entry_item_type" +
//            " from t_bus_order_head  h left join t_bus_order_ppbom_lk lk on h.order_id=lk.order_id" +
//            " left join t_bus_order_ppbom pp on lk.order_ppbom_id=pp.order_ppbom_id " +
//            " left join mid_material mm on pp.material_id=mm.kd_material_id " +
//            " where h.order_id=:orderId and h.is_deleted='1' order by mid_ppbom_entry_replace_group asc", nativeQuery = true)
//    List<Map> getOrderPPbomByOrderId(@Param("orderId") Integer orderId);

    /**
     * 报工获取用料清单
     * @param orderId
     * @param midPpbomEntryInputProcess
     * @return
     */
    @Query(value = "select CASE WHEN (pp.mid_ppbom_entry_bom_number is not null and mm.kd_material_props_id='2') THEN '1' ELSE '0' END AS FLAG," +
            "pp.order_ppbom_id ,pp.material_id ," +
            "pp.material_number ,pp.material_name ,pp.material_specification ," +
            "pp.unit unit,pp.mid_ppbom_entry_material_negative_error,pp.mid_ppbom_entry_material_positive_error," +
            "pp.mid_ppbom_entry_material_standard,pp.must_qty," +
            "pp.mid_ppbom_entry_weigh_mes_unit,pp.mid_ppbom_entry_weigh_devept_unit,pp.mid_ppbom_entry_weigh_mes_qty,pp.mid_ppbom_entry_weigh_devept_qty, " +
            "coalesce(mm.kd_material_is_peel,0) as kd_material_is_peel ,pp.mid_ppbom_entry_replace_group,pp.mid_ppbom_entry_item_type" +
            " from t_bus_order_head  h left join t_bus_order_ppbom_lk lk on h.order_id=lk.order_id" +
            " left join t_bus_order_ppbom pp on lk.order_ppbom_id=pp.order_ppbom_id " +
            " left join mid_material mm on pp.material_id=mm.kd_material_id " +
            " where h.order_id=:orderId AND pp.mid_ppbom_entry_is_into=1 \n" +
            "AND pp.mid_ppbom_entry_input_process=:midPpbomEntryInputProcess and h.is_deleted='1'\n " +
            "and (:midPpbomEntryHandleGroup is null or :midPpbomEntryHandleGroup=-1 or mid_ppbom_entry_handle_group=:midPpbomEntryHandleGroup)" +
            "order by mid_ppbom_entry_handle_sort asc,mid_ppbom_entry_replace_group asc", nativeQuery = true)
    List<Map> getOrderPPbomByOrderIdAndMidPpbomEntryInputProcess(@Param("orderId") Integer orderId,
                                                                 @Param("midPpbomEntryInputProcess") String midPpbomEntryInputProcess,
                                                                 @Param("midPpbomEntryHandleGroup") Integer midPpbomEntryHandleGroup);

    /**
     * 报工获取拉伸膜的用料清单
     * @param orderId
     * @param midPpbomEntryInputProcess
     * @return
     */
    @Query(value = "select pp.order_ppbom_id ,pp.material_id ," +
            "pp.material_number ,pp.material_name ,pp.material_specification ," +
            "pp.unit unit,pp.mid_ppbom_entry_material_negative_error,pp.mid_ppbom_entry_material_positive_error," +
            "pp.mid_ppbom_entry_material_standard,pp.must_qty," +
            "pp.mid_ppbom_entry_weigh_mes_unit,pp.mid_ppbom_entry_weigh_devept_unit,pp.mid_ppbom_entry_weigh_mes_qty,pp.mid_ppbom_entry_weigh_devept_qty, " +
            "coalesce(mm.kd_material_is_peel,0) as kd_material_is_peel ,pp.mid_ppbom_entry_replace_group,pp.mid_ppbom_entry_item_type" +
            " from t_bus_order_head  h left join t_bus_order_ppbom_lk lk on h.order_id=lk.order_id" +
            " left join t_bus_order_ppbom pp on lk.order_ppbom_id=pp.order_ppbom_id " +
            " left join mid_material mm on pp.material_id=mm.kd_material_id " +
            " where h.order_id=:orderId AND pp.mid_ppbom_entry_input_process=:midPpbomEntryInputProcess and h.is_deleted='1' \n" +
            "and (pp.material_number like'02020101%' or pp.material_number like'02020102%') \n " +
            "order by mid_ppbom_entry_replace_group asc", nativeQuery = true)
    List<Map> getOrderPPbomLsmByOrderIdAndMidPpbomEntryInputProcess(@Param("orderId") Integer orderId, @Param("midPpbomEntryInputProcess") String midPpbomEntryInputProcess);

    @Query(value = "select h.body_lot , h.body_material_id ,h.body_material_number ,h.body_material_name ,h.body_material_specification, h.body_plan_prd_qty,h.body_unit " +
            "from t_bus_order_head  h where h.order_no=:orderNo and h.is_deleted='1'", nativeQuery = true)
    List<Map> getBatchList(@Param("orderNo") String orderNo);

    List<TBusOrderHead> findAllByOrderNoAndBodyLot(String orderNo, String bodyLot);

    @Query(value = "select * from t_bus_order_head h left join t_sys_personnel_info p on  h.current_process=p.personnel_id" +
            " where 1=1 and p.user_id=:userId and h.is_deleted='1'", nativeQuery = true)
    List<Map> getHandOverList(String userId);

    //通过订单id获取当前工序，当前班组，当前处理人
    @Query(value = "select a.order_id,a2.process_id,a2.process_seq,a2.class_id,a2.person_id,d.process_name,e.name as class_name,f.name as person_name\n" +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk a1 on a1.order_id=a.order_id\n" +
            "join t_bus_order_process a2 on a2.order_process_id=a1.order_process_id\n" +
            "join (\n" +
            "select max(aa2.process_seq) as process_seq_max,aa.order_id\n" +
            "from t_bus_order_head aa\n" +
            "join t_bus_order_process_lk aa1 on aa1.order_id=aa.order_id\n" +
            "join t_bus_order_process aa2 on aa2.order_process_id=aa1.order_process_id\n" +
            "where aa.order_id = 1 and aa2.process_status = '1'\n" +
            "group by aa.order_id\n" +
            ") m on m.order_id=a.order_id and m.process_seq_max = a2.process_seq\n" +
            "left join t_sys_process_info d on a2.process_id=d.process_id\n" +
            "left join t_sys_class e on a2.class_id=e.class_id\n" +
            "left join t_sys_personnel_info f on a2.person_id=f.personnel_id\n" +
            "where a.order_id =:orderId and a2.process_status = '1' and a.is_deleted='1'\n", nativeQuery = true)
    List<Map> getCurrentProcessInfo(@Param("orderId") Integer orderId);

    @Query(value = "count(1) \n" +
            "from t_bus_order_head a \n" +
//            "join t_bus_order_process_lk a1 on a1.order_id=a.order_id \n" +
//            "join t_bus_order_process a2 on a2.order_process_id=a1.order_process_id \n" +
            "join t_sys_craft_info b on a.craft_id =b.craft_id \n" +
            "join t_sys_craft_process_rel c on b.craft_id =c.craft_id \n" +
            "join t_sys_process_info d on c.process_id =d.process_id\n" +
            "join t_sys_process_class_rel e on d.process_id =e.process_id \n" +
            "join CLASS_GROUP_LEADER_VIEW f on e.class_id =f.class_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "where group_leader_id=?1 and TO_CHAR(body_plan_start_date,'YYYY-MM-DD') =?2 and a.is_deleted='1'\n" +
            "", nativeQuery = true)
    int getTomorrowTask(String userId, String format);

    TBusOrderHead getByOrderNo(String orderNo);

    List<TBusOrderHead> findByOrderNo(String orderNo);

    @Query(value = "select * from t_bus_order_head a where a.mid_mo_sale_order_no=?1 and a.craft_id=?2 and a.is_deleted='1'\n", nativeQuery = true)
    List<TBusOrderHead> getByMidMoSaleOrderNoAndCraftId(String midMoSaleOrderNo, Integer craftId);

    @Query(value = "select a.bill_no from t_bus_order_head a where a.order_no=?1 and a.is_deleted='1'\n", nativeQuery = true)
    String getBillNoByOrderNo(String orderNo);

    //获取订单关联工艺路线下的工序列表
    @Query(value = "select t.process_id,\n" +
            "t.process_name,\n" +
            "t.process_number,\n" +
            "t.process_detail,\n" +
            "t.enabled,\n" +
            "t.created_user,\n" +
            "t.created_time,\n" +
            "t.updated_time,\n" +
            "t.updated_user, \n" +
            "t1.sort \n" +
            "from t_sys_process_info t \n" +
            "left join t_sys_craft_process_rel t1 on t1.process_id=t.process_id\n" +
            "left join t_bus_order_head t2 on t2.craft_id = t1.craft_id\n" +
            "where t2.order_id =?1 and t.enabled=1" +
            "", nativeQuery = true)
    List<Map> getProcessInfoList(Integer orderID,PageRequest sort);

    @Query(value = "select count(1) \n" +
            "from t_sys_process_info t \n" +
            "left join t_sys_craft_process_rel t1 on t1.process_id=t.process_id\n" +
            "left join t_bus_order_head t2 on t2.craft_id = t1.craft_id\n" +
            "where t2.order_id =?1 and t.enabled=1" +
            "", nativeQuery = true)
    Integer getCountProcessInfoList(Integer orderID);

    @Query(value = "select TO_CHAR(p.receive_time,'YYYY-MM-DD HH24:MI:SS:MS') starttime,TO_CHAR(p.finish_time,'YYYY-MM-DD HH24:MI:SS') endtime from t_bus_order_head head left join t_bus_order_process_lk lk" +
            " on head.order_id=lk.order_id left join t_bus_order_process p  on lk.order_process_id=p.order_process_id where head.order_id=:orderId and p.process_id=:processId",nativeQuery = true)
    List<Map> getProcessTime(@Param("orderId") Integer orderId,@Param("processId")Integer processId);

    /**
     * 待生产列表取数
     * @param userId
     * @param processStatusList1
     * @return
     */
    @Query(value = "select count(1) from t_bus_order_head a \n" +
            "join (select a.craft_id,process_id  from t_sys_craft_process_rel a join (select craft_id,min(sort) as sort from t_sys_craft_process_rel a  group by craft_id) b on a.craft_id =b.craft_id and a.sort=b.sort) b\n" +
            "on a.craft_id =b.craft_id\n" +
            "join t_bus_order_process c on\n" +
            "b.process_id=c.process_id \n" +
            "join  t_sys_personnel_info d on c.person_id =d.personnel_id\n" +
            "where d.user_id =?1 and c.process_status in (?2) and a.is_deleted='1'",nativeQuery = true)
    int getWaitTaskCount(String userId, List<String> processStatusList1);

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
            "null as finishTime,\n" +
            "a2.type as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "a2.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "g.process_id processId,\n" +
            "g.process_name processName,\n" +
            "g.process_number processNumber,\n" +
            "d.process_id executeProcessId,\n" +
            "d.process_name executeProcessName,\n" +
            "d.process_number executeProcessNumber, \n" +
            "a2.process_status executeProcessStatus \n" +
            ",null executeRecordTypePd \n" +
            ",null recordTypePd \n" +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join t_bus_order_process a2 on a1.order_process_id =a2.order_process_id \n" +
            "join class_group_leader_view b on a2.class_id =b.class_id \n" +
            "join t_sys_class c on b.class_id =c.class_id \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "where a2.person_id is null \n" +
            "and b.group_leader_id =?1 and c.scheduling = (select code from v_scheduling where my_date=?2) and a.is_deleted='1'",nativeQuery = true)
    List<Map> getWaitTaskUserId(String userId,String myDate, PageRequest sort);

    @Query(value = "select count(1) from t_bus_order_head a join t_bus_order_process_lk  b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join class_group_leader_view d on c.class_id =d.class_id \n" +
            "join t_sys_class e on d.class_id =e.class_id \n" +
            "left join t_sys_process_info t1 on c.process_id =t1.process_id \n" +
            "where c.person_id is null and d.group_leader_id =?1 and e.scheduling = (select code from v_scheduling where my_date=?2) and a.is_deleted='1'",nativeQuery = true)
    int  getWaitTaskCountByUserId(String userId,String myDate);

    TBusOrderHead findByErpMoEntryId(Integer erpMoEntryId);

    List<TBusOrderHead> findByBillNo(String billNo);

    List<TBusOrderHead> findByBillNoAndIsDeleted(String billNo, String isDeleted);

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
            "null as finishTime,\n" +
            "a2.type as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "a2.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
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
            "where b.user_id =?1  and a.is_deleted='1'  \n" +
            "and a2.type='2' ",nativeQuery = true)
    List<Map> getHandOverRecordsByPersonId(String userId, PageRequest of);

    @Query(value = "select count(distinct a.order_no)\n" +
            "from t_bus_order_process a2 \n" +
            "left join t_bus_order_process_lk a1 on a1.order_process_id =a2.order_process_id \n" +
            "left join t_bus_order_head a on a.order_id=a1.order_id \n" +
            "left join t_sys_personnel_info b on  a2.hand_over_person_id =b.personnel_id \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where b.user_id =?1  and a.is_deleted='1'  \n" +
            "and a2.type='2' ",nativeQuery = true)
    int getHandOverRecordsByPersonId(String userId);

    //获取拌料蟹肉棒
    @Query(value = "select '1' as flag,pp.order_ppbom_id ,pp.material_id ," +
            "pp.material_number ,pp.material_name ,pp.material_specification ," +
            "pp.unit unit,pp.mid_ppbom_entry_material_negative_error,pp.mid_ppbom_entry_material_positive_error," +
            "pp.mid_ppbom_entry_material_standard,pp.must_qty," +
            "pp.mid_ppbom_entry_weigh_mes_unit,pp.mid_ppbom_entry_weigh_devept_unit,pp.mid_ppbom_entry_weigh_mes_qty,pp.mid_ppbom_entry_weigh_devept_qty, " +
            "coalesce(mm.kd_material_is_peel,0) as kd_material_is_peel ,pp.mid_ppbom_entry_replace_group,pp.mid_ppbom_entry_item_type" +
            " from t_bus_order_head  h left join t_bus_order_ppbom_lk lk on h.order_id=lk.order_id" +
            " left join t_bus_order_ppbom pp on lk.order_ppbom_id=pp.order_ppbom_id " +
            " left join mid_material mm on pp.material_id=mm.kd_material_id " +
            " where h.order_id=:orderId and pp.mid_ppbom_entry_input_process=:midPpbomEntryInputProcess and pp.mid_ppbom_entry_is_into=1 and h.is_deleted='1' \n " +
            "and pp.mid_ppbom_entry_bom_number is not null and mm.kd_material_props_id='2' \n" +
            "order by mid_ppbom_entry_replace_group asc limit 1 offset 0", nativeQuery = true)
    Map getBLXRB(@Param("orderId") Integer orderId,@Param("midPpbomEntryInputProcess") String midPpbomEntryInputProcess);

    //获取拌料用料列表
    @Query(value = "select pp.order_ppbom_id ,pp.material_id ," +
            "pp.material_number ,pp.material_name ,pp.material_specification ," +
            "pp.unit unit,pp.mid_ppbom_entry_material_negative_error,pp.mid_ppbom_entry_material_positive_error," +
            "pp.mid_ppbom_entry_material_standard,pp.must_qty," +
            "pp.mid_ppbom_entry_weigh_mes_unit,pp.mid_ppbom_entry_weigh_devept_unit,pp.mid_ppbom_entry_weigh_mes_qty,pp.mid_ppbom_entry_weigh_devept_qty, " +
            "coalesce(mm.kd_material_is_peel,0) as kd_material_is_peel ,pp.mid_ppbom_entry_replace_group,pp.mid_ppbom_entry_item_type" +
            " from t_bus_order_head  h left join t_bus_order_ppbom_lk lk on h.order_id=lk.order_id" +
            " join t_bus_order_ppbom pp on lk.order_ppbom_id=pp.order_ppbom_id " +
            " left join mid_material mm on pp.material_id=mm.kd_material_id " +
            " where h.order_id=:orderId and pp.mid_ppbom_entry_input_process=:midPpbomEntryInputProcess and pp.mid_ppbom_entry_is_into=1 and h.is_deleted='1' \n " +
            "and (pp.mid_ppbom_entry_bom_number is null and (kd_material_props_id<>'2' or kd_material_props_id is null)) \n" +
            "order by mid_ppbom_entry_replace_group asc ", nativeQuery = true)
    List<Map> listBLPPboms(@Param("orderId") Integer orderId,@Param("midPpbomEntryInputProcess") String midPpbomEntryInputProcess);

    //根据订单号获取关联的订单信息
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
            "null as finishTime,\n" +
            "a2.type as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "a2.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "g.process_id processId,\n" +
            "g.process_name processName,\n" +
            "g.process_number processNumber,\n" +
            "d.process_id executeProcessId,\n" +
            "d.process_name executeProcessName,\n" +
            "d.process_number executeProcessNumber, \n" +
            "a2.process_status executeProcessStatus, \n" +
            "i.class_id classId, \n" +
            "i.name className, \n" +
            "b2.personnel_id personId, \n" +
            "b2.name personName, \n" +
            "null executeRecordTypePd, \n" +
            "null recordTypePd \n" +
            "from t_bus_order_process a2 \n" +
            "left join t_bus_order_process_lk a1 on a1.order_process_id =a2.order_process_id \n" +
            "left join t_bus_order_head a on a.order_id=a1.order_id \n" +
            "left join t_sys_personnel_info b on  a2.hand_over_person_id =b.personnel_id \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "left join t_sys_class i on a2.class_id = i.class_id \n" +
            "left join t_sys_personnel_info b2 on a2.person_id = b2.personnel_id \n" +
//            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
//            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where a.mid_mo_sale_order_no =?1 and a2.process_id =?2 and a.is_deleted='1' ",nativeQuery = true)
    List<Map> getRelationRecords(String midMoSaleOrderNo, Integer processId, PageRequest of);

    @Query(value = "select count(distinct a.order_no)\n" +
            "from t_bus_order_process a2 \n" +
            "left join t_bus_order_process_lk a1 on a1.order_process_id =a2.order_process_id \n" +
            "left join t_bus_order_head a on a.order_id=a1.order_id \n" +
            "left join t_sys_personnel_info b on  a2.hand_over_person_id =b.personnel_id \n" +
//            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
//            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
//            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h2 on a2.order_process_id = h2.order_process_id \n" +
//            "left join (select order_process_id,max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' GROUP BY order_process_id) h on a2.old_order_process_id = h.order_process_id \n" +
            "where a.mid_mo_sale_order_no =?1 and a2.process_id =?2 and a.is_deleted='1' ",nativeQuery = true)
    int getRelationRecords(String midMoEntrySaleOrderNo, Integer processId);


    @Query(value = "select d.user_id,a.order_id as orderId,\n" +
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
            "null as finishTime,\n" +
            "c.type as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "c.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "g.process_id processId,\n" +
            "g.process_name processName,\n" +
            "g.process_number processNumber,\n" +
            "a.process_id executeProcessId,\n" +
            "a.process_name executeProcessName,\n" +
            "a.process_number executeProcessNumber, \n" +
            "null executeProcessStatus \n" +
            ",null executeRecordTypePd \n" +
            ",null recordTypePd ,\n" +
            "c.to_orderProcess_id  to_order_process_id\n" +
            "from (select * from t_bus_order_head a left join t_sys_process_info a2 on a.current_process=a2.process_id ) as a \n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join (select a.*,b.order_process_id as to_orderProcess_id \n" +
            "from t_bus_order_process a join t_bus_order_process b on a.order_process_id =b.old_order_process_id \n" +
            "where a.type='3') c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_personnel_info d on c.person_id =d.personnel_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "where  d.user_id =?1\n" +
            "and (a.body_lot=?3 or ?3='' or ?3 is null)\n " +
            "and(g.process_number=?2 or ?2='' or ?2 is null)" +
            "",nativeQuery = true)
    List<Map> listShiftTaskList(String userId, String processNumber, String bodyLot, PageRequest of);

    @Query(value = "select count(1)\n" +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join (select a.*,b.order_process_id as to_orderProcess_id \n" +
            "from t_bus_order_process a join t_bus_order_process b on a.order_process_id =b.old_order_process_id \n" +
            "where a.type='3') c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_personnel_info d on c.person_id =d.personnel_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "where  d.user_id =?1 " +
            "and (a.body_lot=?3 or ?3='' or ?3 is null)\n " +
            "and(g.process_number=?2 or ?2='' or ?2 is null)" +
            "",nativeQuery = true)
    int countShiftTaskList(String userId, String processNumber, String bodyLot);

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
            "null as finishTime,\n" +
            "c.type as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "c.old_order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
            "TO_CHAR(a.body_plan_finish_date,'YYYY-MM-DD HH24:MI:SS') as bodyPlanFinishDate, \n" +
            "g.process_id processId,\n" +
            "g.process_name processName,\n" +
            "g.process_number processNumber,\n" +
            "a.process_id executeProcessId,\n" +
            "a.process_name executeProcessName,\n" +
            "a.process_number executeProcessNumber, \n" +
            "null executeProcessStatus \n" +
            ",null executeRecordTypePd \n" +
            ",null recordTypePd ,\n" +
            "c.order_process_id  to_order_process_id\n" +
            "from (select * from t_bus_order_head a left join t_sys_process_info a2 on a.current_process=a2.process_id ) as a \n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_personnel_info d on c.person_id =d.personnel_id \n" +
            "left join t_sys_process_info g on c.process_id = g.process_id \n" +
            "where c.process_status='0' and c.type='3' and c.old_order_process_id is not null and d.user_id =?1\n" +
            "and (g.process_number=?2 or ?2='' or ?2 is null)\n" +
            "and(a.body_lot=?3 or ?3='' or ?3 is null)" +
            "",nativeQuery = true)
    List<Map> listShiftNoAcceptTaskList(String userId, String processNumber, String bodyLot, PageRequest of);

    @Query(value = "select count(1)\n" +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_personnel_info d on c.person_id =d.personnel_id \n" +
            "left join t_sys_process_info g on g.process_id = a.current_process \n" +
            "where c.process_status='0' and c.type='3' and c.old_order_process_id is not null and d.user_id =?1 " +
            "and (g.process_number=?2 or ?2='' or ?2 is null)\n" +
            "and(a.body_lot=?3 or ?3='' or ?3 is null)" +
            "",nativeQuery = true)
    int countShiftNoAcceptTaskList(String userId,String processNumber,String bodyIot);

    @Query(value = "select \n" +
            "a2.receive_time as receiveTime,\n" +
            "a.order_no as orderNo, \n" +
            "a.order_id as orderId, \n" +
            "a.body_lot as bodyLot,\n" +
            "a.body_material_name as bodyMaterialName,\n" +
            "a2.type as type,\n" +
            "a2.order_process_id as orderProcessId, \n" +
            "-1 toOrderProcessId \n" +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join t_bus_order_process a2 on a1.order_process_id =a2.order_process_id  \n" +
            "where 1=1 \n" +
            "and a2.class_id in (select class_id  from \n" +
            "(select a.class_id from t_sys_class_group_leader_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1 \n" +
            "union all \n" +
            "select class_id from t_sys_class_personnel_rel a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where b.user_id =?1) as t\n" +
            "group by class_id)  \n" +
            "and a2.process_status in(?2)\n" +
            "and a2.type =?3 \n" +
            "and a.is_deleted='1'\n" +
            "and (date_part('day', now() - a2.receive_time)*24+ date_part('hour', now() - a2.receive_time)) > ?4 \n" +
            "order by a2.receive_time,a.bill_date desc, orderNo desc ",nativeQuery = true)
    List<Map> getProcessEndByPersonIdAndProcessStatusAndOrderProcessTypeAndreceiveTime(String userId, List<String> processStatusList, String orderProcessType1, Integer dateValue);

    List<TBusOrderHead> findByOrderStatus(String orderStatus);

    // 通过单据编号获取需求单号（首行记录）
    @Query(value = "select a.mid_mo_sale_order_no from t_bus_order_head a where a.bill_no=?1 and mid_mo_sale_order_no!='' and a.is_deleted='1' limit 1 \n", nativeQuery = true)
    String getMidMoSaleOrderNoByBillNo(String billNo);
}
