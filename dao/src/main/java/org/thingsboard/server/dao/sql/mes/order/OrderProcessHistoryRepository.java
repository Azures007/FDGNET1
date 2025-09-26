//package org.thingsboard.server.dao.sql.orderbody;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.thingsboard.server.common.data.TBusOrderBody;
//
//public interface TBusOrderBodyRepository extends JpaRepository<TBusOrderBody, Long> {
//
//}

package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.order
 * @date 2022/4/13 11:01
 * @Description:
 */
public interface OrderProcessHistoryRepository extends JpaRepository<TBusOrderProcessHistory, Integer>, JpaSpecificationExecutor<TBusOrderProcessHistory> {
    @Query(value = "select record.order_process_history_id, record.order_no,record.body_lot,TO_CHAR(record.report_time,'YYYY-MM-DD HH24:MI:SS') as report_time,record.material_id,record.material_number,record.material_name, record.record_unit, info.name person_name,tsc.name class_name," +
            "record.record_qty,record.record_type_pd,record.stock_count,record.report_status from t_bus_order_process_history record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id left join t_sys_process_info tspi on record.process_id = tspi.process_id where 1=1 " +
            " and  record.order_process_id=:orderProcessId and record.report_status in ('0','1')  and record.bus_type<>'BG' order by created_time desc ", nativeQuery = true)
    List<Map> getRecord(@Param("orderProcessId") Integer orderProcessId);

//    @Query(value = "select record.order_process_history_id, record.order_no,record.body_lot,TO_CHAR(record.report_time,'YYYY-MM-DD HH24:MI:SS') as report_time,record.material_id,record.material_number,record.material_name, record.record_unit, info.name person_name,tsc.name class_name," +
//            "record.record_qty,record.record_type_pd,record.stock_count,record.report_status from t_bus_order_process_history record left join t_sys_class tsc on tsc.class_id = record.class_id " +
//            " left join t_sys_personnel_info info on record.person_id = info.personnel_id left join t_sys_process_info tspi on record.process_id = tspi.process_id where 1=1 " +
//            " and  record.order_no=:orderNo and record.process_id=:processId and record.bus_type<>'BG'and record.record_type_pd=:recordTypePd order by created_time desc " ,nativeQuery = true)
//    List<Map> getRecordByRecordTypePd(@Param("orderNo")String orderNo,@Param("processId")Integer processId,@Param("recordTypePd")String recordTypePd);

    @Query(value = "select max(record.stock_count) from t_bus_order_process_history record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            "left join t_sys_personnel_info info on record.person_id = info.personnel_id left join t_sys_process_info tspi on record.process_id = tspi.process_id where 1=1 " +
            "and  record.order_process_id=:orderProcessId and record.bus_type<>'BG' ", nativeQuery = true)
    Integer getSize(@Param("orderProcessId") Integer orderProcessId);

//    @Query(value = "select coalesce(max(record.stock_count),0) from t_bus_order_process_history record left join t_sys_class tsc on tsc.class_id = record.class_id " +
//            "left join t_sys_personnel_info info on record.person_id = info.personnel_id left join t_sys_process_info tspi on record.process_id = tspi.process_id where 1=1 " +
//            "and  record.order_no=:orderNo and record.process_id=:processId and record.bus_type<>'BG' and record.record_type_pd=:recordTypePd",nativeQuery = true)
//    Integer getSizeByRecordTypePd(@Param("orderNo")String orderNo,@Param("processId")Integer processId,@Param("recordTypePd")String recordTypePd);

    @Query(value = "select info.name personName,class.name className,record.report_time,record.record_unit,record.record_qty,record.material_id " +
            " from thingsboard.public.t_bus_order_process_history record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
            "left join  t_sys_class class on record.class_id=class.class_id where 1=1 " +
            " and  record.order_no=:orderNo and record.process_id=:processId  and record.class_id=:classId and record.bus_type='BG' and record.report_status='0' order by record.report_time desc ", nativeQuery = true)
    List<Map> getBGHistoryRecord(@Param("orderNo") String orderNo, @Param("processId") Integer processId, @Param("classId") Integer classId);

//    @Query(value = "select info.name personname ,tsc.name classname ,record.report_time,record.record_unit,record.record_qty,record.material_id " +
//            "from thingsboard.public.t_bus_order_process_history record left join t_sys_class tsc on tsc.class_id = record.class_id " +
//            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
//            "left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
//            " where 1=1 " +
//            " and  record.order_no=:orderNo and record.process_id=:processId and record.body_lot=:bodyLot " +
//            "and record.class_id=:classId and record.bus_type='BG' and record.report_status='0'" ,nativeQuery = true)
//    List<Map> getBGHistoryRecords(@Param("orderNo")String orderNo,@Param("bodyLot")String bodyLot,@Param("processId")Integer processId,@Param("classId")Integer classId);

//    List<TBusOrderProcessHistory> findAllByOrderProcessIdAndProcessIdAndBusTypeAndReportStatusOrderByOrderProcessHistoryIdDesc(Integer orderProcessId, TSysProcessInfo processId, String busType,String status);


    List<TBusOrderProcessHistory> findAllByOrderProcessIdAndBusTypeOrderByOrderProcessHistoryIdDesc(Integer orderProcessId, String busType);

    //工序执行表id，业务类型，报工状态
    List<TBusOrderProcessHistory> findAllByOrderProcessIdAndBusTypeAndReportStatus(Integer orderProcessId, String busTypeBg, String recordStatus);

    //工序执行表id，业务类型，报工状态，类目类型
    List<TBusOrderProcessHistory> findAllByOrderProcessIdAndBusTypeAndReportStatusAndRecordType(Integer orderProcessId, String busType, String reportStatus, String recordType);

    //工序执行表id，业务类型，报工状态，类目类型，报工类型
    List<TBusOrderProcessHistory> findAllByOrderProcessIdAndBusTypeAndReportStatusAndRecordTypeAndRecordTypeBg(Integer orderProcessId, String busType, String reportStatus, String recordType, String recordTypeBg);

    //工序执行表id，业务类型，报工状态，类目类型，盘点类型
    List<TBusOrderProcessHistory> findAllByOrderProcessIdAndBusTypeAndReportStatusAndRecordTypeAndRecordTypePd(Integer orderProcessId, String busType, String reportStatus, String recordType, String recordTypePd);

    //工序执行表id，业务类型，报工状态，盘点类型
    List<TBusOrderProcessHistory> findAllByOrderProcessIdAndBusTypeAndReportStatusAndRecordTypePd(Integer orderProcessId, String busType, String reportStatus, String recordTypePd);

    //报工盘点历史记录表父级id，业务类型，报工状态
    List<TBusOrderProcessHistory> findAllByOrderProcessHistoryParentIdAndBusTypeAndReportStatus(Integer orderProcessHistoryParentId, String busType, String reportStatus);

    @Query(value = "select order_ppbom_id,material_id,material_number,material_name,'kg' record_unit ,cast(sum(cast(record_qty * (case when record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))) as DECIMAL(19, 3)) record_qty,max(report_time) report_time  \n" +
            "from t_bus_order_process_history \n" +
            "where order_process_id =?1 and order_ppbom_id =?2 and bus_type ='BG' and report_status<>'1' and import_pot =?3 and record_type_bg ='REPORTYPE0001' and device_person_group_id=?4 " +
            "group by order_ppbom_id,material_id,material_number,material_name", nativeQuery = true)
    Map getByPpbomLimitTwo(Integer orderProcessId, Integer orderPpbomId, int importPot,String groupId);

    @Query(value = "select coalesce(sum(cast(record_qty * (case when record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))),0) from (\n" +
            "select a.material_name ,a.order_ppbom_id,record_qty,report_time,record_unit ,ceil (cast(row_number()over(order by report_time asc) as decimal)/3) as group_id\n" +
            "from t_bus_order_process_history a \n" +
            "join t_bus_order_ppbom a2 on a.order_ppbom_id =a2.order_ppbom_id \n" +
            "left join mid_material a3 on a2.material_id =a3.kd_material_id \n" +
            "where device_person_group_id =?1 and bus_type ='BG' and order_process_id =?2 and record_type ='1' and record_type_bg='REPORTYPE0001'\n" +
            "and (mid_ppbom_entry_bom_number is not null and kd_material_props_id='2' )\n" +
            "union all \n" +
            "select a.material_name ,a.order_ppbom_id,record_qty,report_time,record_unit,\n" +
            "(select count(*)+1 from t_bus_order_process_history b \n" +
            "where b.order_ppbom_id =a.order_ppbom_id and b.report_time <a.report_time " +
            "and  device_person_group_id =?1 and bus_type ='BG' and order_process_id =?2 and record_type ='1' and record_type_bg='REPORTYPE0001'\n" +
            ") as group_id\n" +
            "from t_bus_order_process_history a\n" +
            "join t_bus_order_ppbom a2 on a.order_ppbom_id =a2.order_ppbom_id \n" +
            "left join mid_material a3 on a2.material_id =a3.kd_material_id \n" +
            "where device_person_group_id =?1 and bus_type ='BG' and order_process_id =?2 and record_type ='1' and record_type_bg='REPORTYPE0001'\n" +
            "and (a2.mid_ppbom_entry_bom_number is null and (a3.kd_material_props_id<>'2' or a3.kd_material_props_id is null))\n" +
            "and a.report_status<>'1'\n" +
            "order by report_time asc\n" +
            ") as t where t.group_id <= ?4 and t.group_id > ?3 ", nativeQuery = true)
    Float sumPot(String groupId, Integer orderProcessId, int exportPot, int importPotMin);

    /**
     * 获取积累投入/个人
     * @param groupId
     * @param orderProcessId
     * @return
     */
    @Query(value = "select coalesce(sum(cast(record_qty * (case when record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))),0) from (\n" +
            "select a.material_name ,a.order_ppbom_id,record_qty,report_time,record_unit ,ceil (cast(a.import_pot as decimal)/3) as import_pot\n" +
            "from t_bus_order_process_history a \n" +
            "join t_bus_order_ppbom a2 on a.order_ppbom_id =a2.order_ppbom_id \n" +
            "left join mid_material a3 on a2.material_id =a3.kd_material_id \n" +
            "where (device_person_group_id =?1 or ?1='' or ?1 is null)\n" +
            "and bus_type ='BG' and order_process_id =?2 and record_type ='1' and record_type_bg='REPORTYPE0001'\n" +
            "and (mid_ppbom_entry_bom_number is not null and kd_material_props_id='2' )\n" +
            "union all \n" +
            "select a.material_name ,a.order_ppbom_id,record_qty,report_time,record_unit,import_pot \n" +
            "from t_bus_order_process_history a\n" +
            "join t_bus_order_ppbom a2 on a.order_ppbom_id =a2.order_ppbom_id \n" +
            "left join mid_material a3 on a2.material_id =a3.kd_material_id \n" +
            "where (device_person_group_id =?1 or ?1='' or ?1 is null)\n" +
            " and bus_type ='BG' and order_process_id =?2 and record_type ='1' and record_type_bg='REPORTYPE0001'\n" +
            "and (a2.mid_ppbom_entry_bom_number is null and (a3.kd_material_props_id<>'2' or a3.kd_material_props_id is null))\n" +
            "and a.report_status<>'1' \n" +
            ") as t",nativeQuery = true)
    BigDecimal sumPot2(String groupId, Integer orderProcessId);

    @Transactional
    @Query(value = "select * from t_bus_order_process_history where bus_type='BG' and order_process_id = ?1 and order_ppbom_id = ?2 and record_type_bg = ?3 and device_person_group_id = ?4 and report_status = ?5 " +
            " GROUP BY order_process_history_id ", nativeQuery = true)
    List<TBusOrderProcessHistory> getOrderProcessHistoryBg(Integer orderProcessId, Integer orderPPBomId, String recordTypeBg, String orderProcessRecordId, String reportStatus);

    @Transactional
    @Query(value = "select * from t_bus_order_process_history " +
            "where bus_type='BG' and order_process_id = ?1 and order_ppbom_id = ?2 " +
            "and record_type_bg = ?3 and device_person_group_id = ?4 and report_status = ?5 " +
            " and import_pot_group = ?6 and import_pot = ?7 " +
            " GROUP BY order_process_history_id ", nativeQuery = true)
    List<TBusOrderProcessHistory> getOrderProcessHistoryBgGroup(Integer orderProcessId,
                                                                Integer orderPPBomId,
                                                                String recordTypeBg,
                                                                String orderProcessRecordId,
                                                                String reportStatus,
                                                                Integer importPotGroup,
                                                                Float importPot);

    List<TBusOrderProcessHistory> findAllByOrderProcessIdAndBusType(Integer orderProcessId, String bg);

    @Query("select t from TBusOrderProcessHistory as t where orderProcessId=?1 and recordType=?2 and materialNumber like ?3")
    List<TBusOrderProcessHistory> getBgOrderProcessRecordsAndMaterialNumberLike(Integer orderProcessId, String type, String number);

    /**
     * 修改删除用料记录为未投满
     * @param ppbomIds
     * @param orderProcessId
     * @param importPot
     */
    @Modifying
    @Query(value = "update t_bus_order_process_history set import_pot_group=?4 " +
            "where order_process_id=?2 and order_ppbom_id in (?1) and import_pot=?3",nativeQuery = true)
    void updateHistoryByJoinPPbomId(List<Integer> ppbomIds, Integer orderProcessId, Float importPot,Integer importPotGroup);

    @Query(value = "select * from t_bus_order_process_history tboph " +
            "where order_process_id =?1 and order_ppbom_id in ?2 " +
            "and (device_person_group_id =?3 or ?3='' or ?3 is null)" +
            "and import_pot_group =?4 " +
            "and bus_type='BG' and report_status ='0' and record_type='1'\n" +
            "order by import_pot asc ",nativeQuery = true)
    List<TBusOrderProcessHistory> findAnd(Integer orderProcessId, List<Integer> orderPpbomId, String groupId,Integer importPotGroup);

    /***
     * 获取第一条历史记录数据
     *
     * @param orderProcessId
     * @param orderPPBomId
     * @return
     */
    @Query(value = "(select  min(all_import_pot) from t_bus_order_process_history \n" +
            "where order_process_id =?1 and record_type ='1' and bus_type ='BG' and order_ppbom_id =?2  and report_status='0')",nativeQuery = true)
    Integer getOneHistory(Integer orderProcessId, Integer orderPPBomId);

    /***
     * 求all_pot 间断点
     * @param orderProcessId
     * @param orderPPBomId
     * @return
     */
    @Query(value = "select all_import_pot+1 from (select  all_import_pot from t_bus_order_process_history \n" +
            "where order_process_id =?1 and record_type ='1' and bus_type ='BG' and order_ppbom_id =?2  and report_status='0') a\n" +
            "where not exists (select 1 from (select  all_import_pot from t_bus_order_process_history \n" +
            "where order_process_id =?1 and record_type ='1' and bus_type ='BG' and order_ppbom_id =?2  and report_status='0') b where b.all_import_pot=a.all_import_pot+1)\n" +
            "and all_import_pot<=(select max(all_import_pot) from (select  all_import_pot from t_bus_order_process_history \n" +
            "where order_process_id =?1 and record_type ='1' and bus_type ='BG' and order_ppbom_id =?2  and report_status='0') as t )\n" +
            "limit 1 offset 0",nativeQuery = true)
    Integer breakHistory(Integer orderProcessId, Integer orderPPBomId);

    List<TBusOrderProcessHistory> findAllByOrderProcessIdAndPotNumberAndReportStatusAndOrderProcessHistoryIdIsNot(Integer orderProcessId, Integer potNumber, String reportStatus, Integer orderProcessHistoryId);

    List<TBusOrderProcessHistory> findByOrderProcessIdAndIsSupplement(Integer orderProcessId, String isSupplement);
//    @Transactional
//    @Query(value = "select * from t_bus_order_process_history where bus_type='BG' and order_process_id = ?1 and import_pot_group = ?2 and record_type_bg = ?3 and device_person_group_id = ?4 and report_status = ?5 and import_pot = ?6 " +
//            " GROUP BY order_process_history_id ", nativeQuery = true)
//    List<TBusOrderProcessHistory> getOrderProcessHistoryBg(Integer orderProcessId, Integer importPotGroup, String recordTypeBg, String orderProcessRecordId, String reportStatus, Float importPot);

}
