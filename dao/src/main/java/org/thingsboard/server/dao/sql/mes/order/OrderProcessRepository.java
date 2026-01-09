package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;

import java.util.List;
import java.util.Map;

public interface OrderProcessRepository extends JpaRepository<TBusOrderProcess,Integer>, JpaSpecificationExecutor<TBusOrderProcess> {

    @Query(value = "select t.* from t_bus_order_process t where t.order_process_id=(\n" +
            "select a.order_process_id from t_bus_order_process_lk a where a.order_id = ?1\n" +
            ") and t.process_id=?1",nativeQuery = true)
    List<Map> getProcessByOrderId(Integer orderId, Integer processid);

    List<TBusOrderProcess> findByOrderNoAndProcessId(String orderNo, TSysProcessInfo processId);

    List<TBusOrderProcess> findAllByOrderNoIn(List<String> orderNos);

    TBusOrderProcess getByProcessId(Integer personId);

    // 根据工序查询汇总数量
    @Query(value = "select count(1) from t_bus_order_process a where a.process_id=?1",nativeQuery = true)
    int countByProcessId(Integer processId);

    /**
     * 获取尾料工序转移数据
     * @param orderProcessId
     * @return
     */
    @Query("select t from TBusOrderProcess t where orderProcessId=?1 and processStatus='3' and type='3'")
    TBusOrderProcess findTailingByOrderProcessId(Integer orderProcessId);

    /**
     * 获取当前工序数据
     * @param orderProcessId
     * @return
     */
    @Query(value = "select c.order_no,c.body_lot,c.body_unit,c.bill_no ,d.class_id,d.\"name\" as class_name,c.body_material_name ," +
            "c.body_material_number ,c.body_plan_prd_qty ,e.\"name\" as shift_name\n" +
            "from t_bus_order_process a join t_bus_order_process_lk b on a.order_process_id =b.order_process_id \n" +
            "join t_bus_order_head c on b.order_id =c.order_id \n" +
            "join t_sys_class d on a.class_id =d.class_id \n" +
            "join t_sys_personnel_info e on a.person_id=e.personnel_id \n" +
            "where a.order_process_id =?1",nativeQuery = true)
    Map getShiftRecordDetailVo(Integer orderProcessId);

    @Query(value = "select c.\"name\" accept_class_name,b.\"name\" accept_name,a.order_no to_order_no from t_bus_order_process a \n" +
            "join t_sys_class b on a.class_id =b.class_id \n" +
            "join t_sys_personnel_info c on a.person_id=c.personnel_id \n" +
            "where a.order_process_id =?1",nativeQuery = true)
    Map getAccept(Integer toOrderProcessId);

    @Query(value = "select t.process_name,t.process_number from T_sys_process_info as t where t.enabled=1",nativeQuery = true)
    List<Map> listProcess();

    @Query(value = "select a.order_id from t_t_bus_order_head a join t_bus_order_process_lk b on a.order_id=b.order_id " +
            "       join t_bus_order_process c on b.order_process_id=c.order_process_id where c.order_process_id=?1",nativeQuery = true)
    Integer getHead(Integer orderProcessId);

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
            "coalesce(c.type,'1') as type, \n" +
            "a.body_material_specification bodyMaterialSpecification, \n" +
            "c.order_process_id as orderProcessId, \n" +
            "a.body_material_id as bodyMaterialId, \n" +
            "a.body_material_number as bodyMaterialNumber, \n" +
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

    /**
     * 当前报工锅数数量积累
     * @param orderProcessId
     * @param orderPPBomId
     * @param orderProcessHistoryStatus0
     * @return
     */
    @Query(value = "select COALESCE(sum(import_pot),0) from t_bus_order_process_record tbopr \n" +
            "where order_process_id=?1 and order_ppbom_id =CAST(?2 AS INTEGER) and record_type ='1' and bus_type ='BG' and record_type_bg=?3",nativeQuery = true)
    int getImportByPPbomAndOrderProcessId(Integer orderProcessId, Integer orderPPBomId, String orderProcessHistoryStatus0);
}
