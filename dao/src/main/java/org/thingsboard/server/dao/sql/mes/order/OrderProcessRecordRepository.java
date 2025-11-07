package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;
import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;
import org.thingsboard.server.dao.mes.dto.OrderProcessRecordSaveDto;
import org.thingsboard.server.dao.mes.dto.OrderProcessTimeDto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.order
 * @date 2022/4/13 11:01
 * @Description:
 */
public interface OrderProcessRecordRepository extends JpaRepository<TBusOrderProcessRecord, Integer>, JpaSpecificationExecutor<TBusOrderProcessRecord> {

    @Query(value = "select * from t_bus_order_process_record " +
            "where bus_type='BG' and order_process_id = ?1 and order_ppbom_id = CAST(?2 AS INTEGER) and record_type_bg = ?3 and device_person_group_id = ?4 " +
            " and import_pot_group = ?5 and import_pot = ?6 " +
            " GROUP BY order_process_record_id ", nativeQuery = true)
    List<TBusOrderProcessRecord> getOrderProcessRecordBgGroup(Integer orderProcessId, Integer orderPPBomId, String recordTypeBg, String orderProcessRecordId
            , Integer importPotGroup, Float importPot);

    @Query(value = "select * from t_bus_order_process_record record left join  t_sys_class tsc on record.class_id = tsc.class_id " +
            " left join t_sys_process_info info on record.process_id = info.process_id " +
            "left join t_sys_personnel_info t on t.personnel_id = record.person_id " +
            "where 1=1 and record.bus_type='PD' and (record.order_no =:#{#saveDto.orderNo} or :#{#saveDto.orderNo} is null or :#{#saveDto.orderNo} ='')  " +
            "  and (record.body_lot =:#{#saveDto.bodyLot} or :#{#saveDto.bodyLot} is null or :#{#saveDto.bodyLot} ='')  ", nativeQuery = true)
    List<TBusOrderProcessRecord> getRecord(@Param("saveDto") OrderProcessRecordSaveDto saveDto);
//    List<TBusOrderProcessRecord> findAllByOrderNoAndBodyLotAndMaterialIdAAndProcessIdAndPersonIdAndClassId(String orderNo,String bodyLot,Integer materialId,Integer processId,Integer personId,Integer classId);

//    List<TBusOrderProcessRecord> findAllByOrderProcessId(Integer orderProcessId);

    List<TBusOrderProcessRecord> findAllByOrderProcessIdAndBusType(Integer orderProcessId, String busType);

    List<TBusOrderProcessRecord> findAllByOrderNoAndBusType(String orderNo, String busType);

    List<TBusOrderProcessRecord> findAllByOrderProcessIdAndBusTypeAndRecordType(Integer orderProcessId, String busType, String recordType);

    List<TBusOrderProcessRecord> findAllByOrderProcessIdAndBusTypeAndRecordTypePd(Integer orderProcessId, String busType, String recordTypePd);

    List<TBusOrderProcessRecord> findAllByOrderNoAndProcessIdAndClassIdAndBusType(String orderNo, TSysProcessInfo sysProcessInfo, TSysClass sysClass, String busType);

    @Query(value = "select record.order_no,record.body_lot,record.material_id,record.material_number,record.material_name, record.record_unit, info.name person_name,tsc.name class_name," +
            "record.record_qty,record.report_time from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
            " where 1=1 and record.bus_type='PD' " +
            " and record.order_no=:orderNo and tspi.process_id=:processId and tsc.class_id=:classId", nativeQuery = true)
    List<Map> getDetails(@Param("orderNo") String orderNo, @Param("processId") Integer processId, @Param("classId") Integer classId);

    @Query(value = "select record.order_process_record_id, info.name personname ,tsc.name classname ,TO_CHAR(record.report_time,'YYYY-MM-DD HH24:MI:SS') as report_time,tspi.process_name ,record.record_unit,record.record_qty,record.material_id," +
            "record.device_id,record.device_person_id, " +
            "record.import_pot,record.export_pot,record.export_pot_min, " +
            "record.material_number,record.material_name,record.record_type,record.capacity_unit,record.capacity_qty " +
            "from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
            " left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
            " where 1=1 " +
            " and record.order_process_id=:orderProcessId" +
            " and (record.device_group_id=:deviceGroupId or ''=:deviceGroupId) " +
            " and (record.device_person_group_id=:devicePersonGroupId or ''=:devicePersonGroupId) " +
            " and record.bus_type='BG' order by tsc.update_time desc,created_time desc ", nativeQuery = true)
    List<Map> getBGRecord(@Param("orderProcessId") Integer orderProcessId
            , @Param("deviceGroupId") String deviceGroupId, @Param("devicePersonGroupId") String devicePersonGroupId);

//    @Query(value = "select record.order_process_record_id, info.name personname ,tsc.name classname ,TO_CHAR(record.report_time,'YYYY-MM-DD HH24:MI:SS') as report_time,tspi.process_name ,record.record_unit,record.record_qty,record.record_manual_qty,record.material_id," +
//            "record.device_id,record.device_person_id, " +
//            "record.import_pot,record.export_pot,record.export_pot_min, " +
//            "record.material_number,record.material_name,record.record_type,record.capacity_unit,record.capacity_qty " +
//            "from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
//            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
//            " left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
//            " where 1=1 " +
//            " and record.order_process_id=:orderProcessId" +
//            " and (record.device_group_id=:deviceGroupId or ''=:deviceGroupId) " +
//            " and (record.device_person_group_id=:devicePersonGroupId or ''=:devicePersonGroupId) " +
//            " and (record.record_type_bg=:recordTypeBg or ''=:recordTypeBg) " +
//            " and record.bus_type='BG' order by tsc.update_time desc,created_time desc  ", nativeQuery = true)
//    List<Map> getBGRecord(@Param("orderProcessId") Integer orderProcessId
//            , @Param("deviceGroupId") String deviceGroupId, @Param("devicePersonGroupId") String devicePersonGroupId, @Param("recordTypeBg") String recordTypeBg);

    @Query(value = "select record.order_process_record_id, info.name personname ,tsc.name classname ,TO_CHAR(record.report_time,'YYYY-MM-DD HH24:MI:SS') as report_time,tspi.process_name ,record.record_unit,record.record_qty,record.record_manual_qty,record.material_id," +
            "record.device_id,record.device_person_id, " +
            "record.import_pot,record.export_pot,record.export_pot_min, " +
            "record.material_number,record.material_name,record.record_type,record.capacity_unit,record.capacity_qty " +
            "from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
            " left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
            " where 1=1 " +
            " and record.order_process_id=:orderProcessId" +
            " and record.record_unit=:recordUnit" +
            " and (record.device_group_id=:deviceGroupId or ''=:deviceGroupId) " +
            " and (record.device_person_group_id=:devicePersonGroupId or ''=:devicePersonGroupId) " +
            " and (record.record_type_bg=:recordTypeBg or ''=:recordTypeBg) " +
            " and record.bus_type='BG' order by tsc.update_time desc,created_time desc  ", nativeQuery = true)
    List<Map> getBGRecord(@Param("orderProcessId") Integer orderProcessId
            , @Param("deviceGroupId") String deviceGroupId, @Param("devicePersonGroupId") String devicePersonGroupId, @Param("recordTypeBg") String recordTypeBg, @Param("recordUnit") String recordUnit);

    @Query(value = "select record.group_code,record.order_process_record_id, info.name personname ,tsc.name classname ,TO_CHAR(record.report_time,'YYYY-MM-DD HH24:MI:SS') as report_time,tspi.process_name ,record.record_unit,record.record_qty,record.record_manual_qty,record.material_id," +
            "record.device_id,record.device_person_id, " +
            "record.import_pot,record.export_pot,record.export_pot_min, " +
            "record.material_number,record.material_name,record.record_type,record.capacity_unit,record.capacity_qty " +
            "from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
            " left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
            " where 1=1 " +
            " and record.order_process_id=:orderProcessId" +
            " and record.record_unit=:recordUnit" +
            " and record.material_number=:materialNumber " +
            " and record.group_code=:groupCode " +
            " and (record.device_group_id=:deviceGroupId or ''=:deviceGroupId) " +
            " and (record.device_person_group_id=:devicePersonGroupId or ''=:devicePersonGroupId) " +
            " and (record.record_type_bg=:recordTypeBg or ''=:recordTypeBg) " +
            " and record.bus_type='BG' order by tsc.update_time desc,created_time desc  ", nativeQuery = true)
    List<Map> getBGRecordYl(@Param("orderProcessId") Integer orderProcessId
            , @Param("deviceGroupId") String deviceGroupId, @Param("devicePersonGroupId") String devicePersonGroupId, @Param("recordTypeBg") String recordTypeBg, @Param("recordUnit") String recordUnit, @Param("materialNumber") String materialNumber, @Param("groupCode") String groupCode);

    //获取工序和用料id对应的累计投入数量
    @Query(value = "select coalesce(sum(record.record_qty),0) as record_qty " +
            "from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
            " left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
            " where 1=1 " +
            " and record.process_id=:processId" +
            " and record.order_ppbom_id=:orderPPbomId" +
            " and (record.record_type_bg=:recordTypeBg or ''=:recordTypeBg) " +
            " and record.bus_type='BG' ", nativeQuery = true)
    Float getBGSumRecordQty(@Param("processId") Integer processId, @Param("orderPPbomId") Integer orderPPbomId, @Param("recordTypeBg") String recordTypeBg);

    //获取工序全部用料对应的累计投入数量
    @Query(value = "select coalesce(sum(record.record_qty),0) as record_qty " +
            "from t_bus_order_process_record record " +
            " left join t_bus_order_head head on head.order_no = record.order_no " +
            " where 1=1 " +
            " and head.mid_mo_sale_order_no=:midMoSaleOrderNo" +
            " and record.process_id=:processId" +
            " and (record.record_type_bg=:recordTypeBg or ''=:recordTypeBg) " +
            " and record.bus_type='BG' ", nativeQuery = true)
    Float getBGSumRecordQty(@Param("processId") Integer processId, @Param("midMoSaleOrderNo") String midMoSaleOrderNo, @Param("recordTypeBg") String recordTypeBg);

    //获取拌料的前道订单全部用料累计报工数量
    @Query(value = "select coalesce(sum(record.record_qty),0) as record_qty " +
            "from t_bus_order_process_record record " +
            " left join t_bus_order_head head on head.order_no = record.order_no " +
            " where 1=1 " +
            " and head.order_no=:orderNo" +
            " and head.mid_mo_sale_order_no=:midMoSaleOrderNo" +
            " and record.process_id=:processId" +
            " and (record.record_type=:recordType or ''=:recordType) " +
            " and (record.record_type_bg=:recordTypeBg or ''=:recordTypeBg) " +
            " and record.bus_type='BG' ", nativeQuery = true)
    Float getBGSumRecordQtyPrevOrder(@Param("processId") Integer processId, @Param("midMoSaleOrderNo") String midMoSaleOrderNo, @Param("recordType") String recordType, @Param("recordTypeBg") String recordTypeBg);

    //获取拌料的前道订单全部用料累计报工数量
    @Query(value = "select coalesce(sum(record.record_qty),0) as record_qty " +
            "from t_bus_order_process_record record " +
            " left join t_bus_order_head head on head.order_no = record.order_no " +
            " where 1=1 " +
            " and head.order_no=:orderNo" +
            " and head.mid_mo_sale_order_no=:midMoSaleOrderNo" +
            " and record.process_id=:processId" +
            " and (record.record_type=:recordType or ''=:recordType) " +
            " and (record.record_type_bg=:recordTypeBg or ''=:recordTypeBg) " +
            " and record.bus_type='BG' ", nativeQuery = true)
    Float getBGSumRecordQtyPrevOrder(@Param("processId") Integer processId, @Param("midMoSaleOrderNo") String midMoSaleOrderNo, @Param("orderNo") String orderNo, @Param("recordType") String recordType, @Param("recordTypeBg") String recordTypeBg);


//    @Query(value = "select record.order_process_record_id, info.name personname ,tsc.name classname ,TO_CHAR(record.report_time,'YYYY-MM-DD HH24:MI:SS') as report_time,tspi.process_name ,record.record_unit,record.record_qty,record.material_id," +
//            "record.device_id,record.device_person_id, " +
//            "record.import_pot,record.export_pot,record.export_pot_min, " +
//            "record.material_number,record.material_name,record.record_type,record.capacity_unit,record.capacity_qty " +
//            "from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
//            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
//            "left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
//            " where 1=1 " +
//            " and  record.order_no=:orderNo and record.process_id=:processId  " +
//            "and record.class_id=:classId and record.bus_type='BG' order by tsc.update_time desc ,created_time desc ", nativeQuery = true)
//    List<Map> getBGRecord(@Param("orderNo") String orderNo, @Param("processId") Integer processId, @Param("classId") Integer classId);
//
//    @Query(value = "select record.order_process_record_id, info.name personname ,tsc.name classname ,TO_CHAR(record.report_time,'YYYY-MM-DD HH24:MI:SS') as report_time,tspi.process_name ,record.record_unit,record.record_qty,record.material_id," +
//            "record.device_id,record.device_person_id, " +
//            "record.import_pot,record.export_pot,record.export_pot_min, " +
//            "record.material_number,record.material_name,record.record_type,record.capacity_unit,record.capacity_qty " +
//            "from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
//            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
//            " left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
//            " where 1=1 " +
//            " and record.order_no=:orderNo and record.process_id=:processId  " +
//            " and record.class_id=:classId and record.device_id=:deviceId and record.device_person_id=:devicePersonId and record.bus_type='BG' order by tsc.update_time desc ,created_time desc ", nativeQuery = true)
//    List<Map> getBGRecord(@Param("orderNo") String orderNo, @Param("processId") Integer processId, @Param("classId") Integer classId
//            , @Param("deviceId") Integer deviceId, @Param("devicePersonId") Integer devicePersonId);
//
//    @Query(value = "select record.order_process_record_id, info.name personname ,tsc.name classname ,TO_CHAR(record.report_time,'YYYY-MM-DD HH24:MI:SS') as report_time,tspi.process_name ,record.record_unit,record.record_qty,record.material_id," +
//            "record.device_id,record.device_person_id, " +
//            "record.import_pot,record.export_pot,record.export_pot_min, " +
//            "record.material_number,record.material_name,record.record_type,record.capacity_unit,record.capacity_qty " +
//            "from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
//            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
//            "left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
//            " where 1=1 " +
//            " and  record.order_no=:orderNo and record.process_id=:processId  " +
//            " and record.class_id=:classId  and record.device_person_id=:devicePersonId and record.bus_type='BG' order by tsc.update_time desc ,created_time desc ", nativeQuery = true)
//    List<Map> getBGRecord(@Param("orderNo") String orderNo, @Param("processId") Integer processId, @Param("classId") Integer classId
//            , @Param("devicePersonId") Integer devicePersonId);


    @Query(value = "select " +
            "a.kd_material_name as lsm_material_name,record.order_process_history_id,info.name personname ,tsc.name class_name ,record.report_time||'' as report_time,tspi.process_name ,record.record_unit,record.record_qty,record.material_id," +
            "record.material_number,record.material_name,record.record_type,COALESCE(record.record_manual_qty,0) record_manual_qty ," +
            "record.iot_qty,record.iot_math,record.device_person_group_id,record.device_group_id,record.pot_number,record.record_type_bg " +
            "from t_bus_order_process_history record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            "left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
            "left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
            "left join mid_material a on record.lsm_material_id=a.kd_material_id\n" +
//            "left join ( select a.*,b.station,b.name from t_bus_order_process_person_rel a join t_sys_personnel_info b on a.device_person_id =b.personnel_id) a on record.device_person_group_id =a.device_person_group_id \n" +
//            " left join ( select a.*,b.device_name from t_bus_order_process_device_rel a join t_sys_device b on a.device_id =b.device_id ) b on record.device_group_id =b.device_group_id \n" +
            "where 1=1 " +
            "and  record.order_no=:orderNo " +
            "and record.process_id=:processId  " +
            "and record.class_id=:classId " +
            "and record.bus_type='BG' " +
            "and record.record_type in(:recordTypeList) " +
            "and record.report_status='0' " +
            "and (import_pot >=0 or record_type <>'1' or import_pot is null) " +
            "and (record.material_name=:materialName or :materialName='' or :materialName is null )\n" +
            " and exists (select a.*,b.station,b.name from t_bus_order_process_person_rel a join t_sys_personnel_info b on a.device_person_id =b.personnel_id where record.device_person_group_id =a.device_person_group_id " +
            "  and (b.station=:station or :station='')" +
            "  and (a.device_person_id=:devicePersonId or :devicePersonId=-1)\n" +
            ")\n" +
            " and (exists (select a.*,b.device_name from t_bus_order_process_device_rel a join t_sys_device b on a.device_id =b.device_id where record.device_group_id =a.device_group_id " +
            "  and (a.device_id=:deviceId or :deviceId=-1)\n" +
            ") or :deviceId=-1)\n" +
            "order by record.report_time desc ", nativeQuery = true)
    List<Map> getBGRecordByRecordtype(@Param("orderNo") String orderNo,
                                      @Param("processId") Integer processId,
                                      @Param("classId") Integer classId,
                                      @Param("recordTypeList") List<String> recordTypeList,
                                      @Param("materialName") String materialName,
                                      @Param("devicePersonId") Integer devicePersonId,
                                      @Param("deviceId") Integer deviceId,
                                      @Param("station") String station);

    /**
     * 设备开始时间和结束时间，按订单获取，当IOT报工结束时间为空时，取值订单工序的接单时间
     * AB料产出和合格品产出传条件：订单号+工序id+一级类目+二级类目
     */
    @Query(value = "select TO_CHAR(MAX(record.iot_collection_last_time),'YYYY-MM-DD HH24:MI:SS:MS') as iot_collection_last_time " +
            " from t_bus_order_process_history record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
            " left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
            " where 1=1 " +
            " and record.order_no=:#{#timeDto.orderNo} and record.process_id=:#{#timeDto.processId} " +
            " and record.bus_type = 'BG'" +
            " and record.record_type=:#{#timeDto.recordType}" +
            " and record.record_type_l2=:#{#timeDto.recordTypeL2}" +
            " and record.report_status = '0'\n" +
            " and record.record_qty > 0", nativeQuery = true)
    List<String> getBGRecordTime(@Param("timeDto") OrderProcessTimeDto timeDto);

    /**
     * 设备开始时间和结束时间，按设备获取，不按订单
     */
    @Query(value = "select TO_CHAR(max(record.iot_collection_last_time),'YYYY-MM-DD HH24:MI:SS:MS') as iot_collection_last_time " +
            "from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id " +
            "left join t_sys_process_info tspi on record.process_id = tspi.process_id " +
            "left join t_bus_order_process_device_rel tbopdr on record.device_group_id=tbopdr.device_group_id " +
            " where 1=1 " +
            " and tbopdr.device_id in :deviceIds  ", nativeQuery = true)
    List<String> getBGRecordTime(@Param("deviceIds") List<Integer> deviceIds);

    //没有过滤类目类型
    @Query(value = "select record.record_qty,record.record_unit  from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id left join t_sys_process_info tspi on record.process_id = tspi.process_id where 1=1 " +
            " and  record.order_no=:orderNo and record.process_id=:processId  and record.class_id=:classId and record.bus_type='BG'" +
            " and record.material_id=:materialId limit 1", nativeQuery = true)
    List<Map> getBGRecordWeight(@Param("orderNo") String orderNo, @Param("processId") Integer processId, @Param("classId") Integer classId, @Param("materialId") Integer materialId);

    //类目类型为产后报工
    @Query(value = "select cast(record.record_qty as float) from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id left join t_sys_process_info tspi on record.process_id = tspi.process_id where 1=1 " +
            " and  record.order_no=:orderNo and record.process_id=:processId  and record.class_id=:classId and record.bus_type='BG'" +
            " and record.record_type='3' limit 1", nativeQuery = true)
    Float getBGRecordWeight(@Param("orderNo") String orderNo, @Param("processId") Integer processId, @Param("classId") Integer classId);

    //累计产出
    @Query(value = "select sum(record.record_qty ) recordQty,sum(record.export_pot) exportPot from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.person_id = info.personnel_id left join t_sys_process_info tspi on record.process_id = tspi.process_id where 1=1 " +
            " and  record.order_no=:orderNo and record.process_id=:processId and record.bus_type='BG'" +
            " and record.record_type='3' ", nativeQuery = true)
    List<Map> getBGRecordTotalData(@Param("orderNo") String orderNo, @Param("processId") Integer processId);

    //个人产出
    @Query(value = "select sum(record.record_qty ) recordQty,sum(record.export_pot) exportPot from t_bus_order_process_record record left join t_sys_class tsc on tsc.class_id = record.class_id " +
            " left join t_sys_personnel_info info on record.device_person_id = info.personnel_id " +
            " where 1=1 and record.order_process_id=:orderProcessId and record.process_id=:processId and record.device_person_group_id=:devicePersonGroupId and record.bus_type='BG'" +
            " and record.record_type='3' ", nativeQuery = true)
    List<Map> getBGRecordPersonData(@Param("orderProcessId") Integer orderProcessId, @Param("processId") Integer processId, @Param("devicePersonGroupId") String devicePersonGroupId);


    @Transactional//事务的注解
    @Query(value = "select MAX(long_v) \n" +
            "from ts_kv \n" +
            "where entity_id =?3 \n" +
            "and key = ?4 \n" +
            "and ts >=?1 \n" +
            "and ts<=?2", nativeQuery = true)
    BigInteger getIotMaxValueByKeyAndDeviceId(long startTs, long endTs, String deviceId, Integer key);

    @Transactional//事务的注解
    @Query(value = "select MIN(long_v) \n" +
            "from ts_kv \n" +
            "where entity_id =?3 \n" +
            "and key = ?4 \n" +
            "and ts >=?1 \n" +
            "and ts<=?2", nativeQuery = true)
    BigInteger getIotMinValueByKeyAndDeviceId(long startTs, long endTs, String deviceId, Integer key);

    @Transactional//事务的注解
    @Query(value = "select \n" +
            "sum(case when t2.flag=1 \n" +
            "then t2.run_num-t2.b_run_num\n" +
            "else t2.run_num end) \n" +
            "from (select t1.*,CASE\n" +
            "                     WHEN cast(t1.b_run_num as bigint) = cast(t1.run_num as bigint) THEN\n" +
            "                      0\n" +
            "                     WHEN cast(t1.b_run_num as bigint) > cast(t1.run_num as bigint) then\n" +
            "                      2\n" +
            "                     ELSE\n" +
            "                      1\n" +
            "                   END flag from (\n" +
            "select t.ts,t.long_v run_num,\n" +
            "lag (t.long_v, 1, t.long_v) over(partition by t.entity_id order by t.entity_id,t.ts ) b_run_num\n" +
            "from ts_kv t\n" +
            "where t.entity_id =?3 \n" +
            "and t.key = ?4 \n" +
            "and t.ts >=?1 \n" +
            "and t.ts<=?2\n" +
            ") t1) t2\n" +
            "where t2.flag in (1,2);", nativeQuery = true)
    BigInteger getIotDiffValueByKeyAndDeviceId(long startTs, long endTs, UUID deviceId, Integer key);

    @Transactional//事务的注解
    @Query(value = "select CAST(id AS VARCHAR) from device where name = ?1 ", nativeQuery = true)
    UUID getDeviceIdByName(String deviceName);

    @Transactional//事务的注解
    @Query(value = "SELECT key_id FROM ts_kv_dictionary where key =  ?1 ", nativeQuery = true)
    Integer getDeviceKeyIdByKey(String keyName);

    List<TBusOrderProcessRecord> findByOrderNoAndProcessId(Integer orderNo, Integer processId);

    List<TBusOrderProcessRecord> findByOrderNoAndProcessIdAndBusType(String orderNo, Integer processId, String bg);

//    TBusOrderProcessRecord findByOrderProcessIdAndBusTypeAndRecordTypeAndMaterialNameAndDeviceIdAndDevicePersonId(Integer orderProcessId, String busType, String recordType, String materialName, String deviceId, String devicePersonId);

    TBusOrderProcessRecord findByOrderProcessRecordId(Integer orderProcessRecordId);

    @Query(value = "select * from t_bus_order_process_record tbopr \n" +
            "where order_process_id =?1 and record_type_pd <> ?2 ", nativeQuery = true)
    List<TBusOrderProcessRecord> findByOrderProcessAndRecordTypePd(Integer orderProcessId, String recordTypePd);

    @Query(value = "select * from t_bus_order_process_record tbopr \n" +
            "where order_process_id =?1 and record_type_pd <> ?2 and record_type_pd <> 'STOCKTAKING0003' ", nativeQuery = true)
    List<TBusOrderProcessRecord> findByOrderProcessAndRecordTypePdWithOut3(Integer orderProcessId, String recordTypePd);


    @Query(value = "select b.bill_no,to_char(a.report_time,'yyyy-mm-dd hh24:mi:ss') bg_time,'合格品' my_type,b.body_material_name, d.process_name, cast(a.record_qty as DECIMAL(19, 3)) || '' record_qty,\n" +
            "a.record_unit,a.code_dsc record_unit_name,b.body_lot,to_char(now(),'yyyy-mm-dd hh24:mi:ss') as sys_time,c.first_name,c.name as class_name,\n" +
            "to_char(c.finish_time,'yyyy-mm-dd hh24:mi:ss') as finish_time,to_char(c.receive_time,'yyyy-mm-dd hh24:mi:ss') as receive_time\n" +
            "from (select * from t_bus_order_process_history a join t_sys_code_dsc b on a.record_unit=b.code_value where b.code_cl_id ='UNIT0000' and a.report_status='0') a\n" +
            "join t_bus_order_head b on a.order_no =b.order_no\n" +
            "join (select c1.first_name,a1.order_process_id,a1.process_id,d1.name,a1.receive_time,a1.finish_time \n" +
            "  from t_bus_order_process a1 \n" +
            "  left join t_sys_personnel_info b1 on a1.person_id=b1.personnel_id \n" +
            "  join tb_user c1 on b1.user_id =(c1.id||'')\n" +
            "  left join t_sys_class d1 on a1.class_id=d1.class_id) c on a.order_process_id =c.order_process_id\n" +
            "join t_sys_process_info d on c.process_id =d.process_id \n" +
            "where a.record_type ='3'  and a.order_process_id =?1\n" +
            "union all\n" +
            "select b.bill_no,to_char(a.report_time,'yyyy-mm-dd hh24:mi:ss') bg_time,'二级品' my_type,(b.body_material_name||'-' || d.process_name||'-'||coalesce(a.material_name,'空')) body_material_name  , \n" +
            "d.process_name, cast(a.record_qty as DECIMAL(19, 3)) || '' record_qty,a.record_unit,a.code_dsc record_unit_name ,b.body_lot,to_char(now(),'yyyy-mm-dd hh24:mi:ss') as sys_time,c.first_name,c.name as class_name,\n" +
            "to_char(c.finish_time,'yyyy-mm-dd hh24:mi:ss') as finish_time,to_char(c.receive_time,'yyyy-mm-dd hh24:mi:ss') as receive_time\n" +
            "from (select * from t_bus_order_process_history a join t_sys_code_dsc b on a.record_unit=b.code_value where b.code_cl_id ='UNIT0000' and a.report_status='0') a\n" +
            "join t_bus_order_head b on a.order_no =b.order_no\n" +
            "join  (select c1.first_name,a1.order_process_id,a1.process_id,d1.name,a1.receive_time,a1.finish_time \n" +
            "  from t_bus_order_process a1 \n" +
            "  left join t_sys_personnel_info b1 on a1.person_id=b1.personnel_id \n" +
            "  join tb_user c1 on b1.user_id =(c1.id||'')\n" +
            "  left join t_sys_class d1 on a1.class_id=d1.class_id) c  on a.order_process_id =c.order_process_id\n" +
            "join t_sys_process_info d on c.process_id =d.process_id \n" +
            "where a.record_type ='2'  and a.order_process_id =?1 ", nativeQuery = true)
    List<Map> listPrintRecord(Integer orderProcessId);

    @Transactional
    @Query(value = "select max(record_type_pd) as record_type_pd from t_bus_order_process_record where bus_type='PD' and order_process_id = ?1 GROUP BY order_process_id ", nativeQuery = true)
    String getOrderRecordTypePd(Integer orderProcessId);

    @Query(value = "select COALESCE(sum(record_qty),0) as record_qty, COALESCE(max(record_unit),'') as record_unit from t_bus_order_process_record a \n" +
            "where record_type_bg ='REPORTYPE0002' and bus_type ='BG' and order_process_id =?1 and record_type ='1'", nativeQuery = true)
    Map sumQtyAndUnit(Integer orderProcessId);

    @Query(value = "select coalesce(sum(cast(record_qty * (case when record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))),0) from t_bus_order_process_record a \n" +
            "where a.bus_type ='BG' and a.order_process_id =?1 and record_type ='1' and record_type_bg =?3 and a.device_person_group_id =?2 ", nativeQuery = true)
    Double sumBG2(Integer orderProcessId, String groupId, String bglx);

    // 累计投入，不按操作员
    @Query(value = "select a.group_code,a.material_number,a.order_process_id,a.order_ppbom_id,coalesce(sum(record_qty),0) as record_qty,max(record_unit) as record_unit from t_bus_order_process_record a \n" +
            "where a.bus_type ='BG' and record_type ='1' and a.order_no =?1 and a.process_id =?2 \n" +
            "and (a.material_id=?3 or -1=?3) " +
            "group by a.group_code,a.material_number,a.order_process_id,a.order_ppbom_id ", nativeQuery = true)
    List<Map> getPpbomRecordQtyTotal(String orderNo, Integer processId, Integer ppbomMaterialId);

    // 个人投入，按操作员
    @Query(value = "select a.order_ppbom_id,sum(coalesce(record_qty,0)) as record_qty,max(record_unit) as record_unit," +
            "(select sum(coalesce(import_pot,0)) from t_bus_order_process_record where record_type_bg ='REPORTYPE0001' and bus_type ='BG' and record_type ='1' and order_no =?1 and process_id =?2 and (material_id=?3 or -1=?3) and order_ppbom_id=a.order_ppbom_id) personanl_count from t_bus_order_process_record a  \n" +
            "where a.bus_type ='BG' and record_type ='1' and a.order_no =?1 and a.process_id =?2 \n" +
            "and (a.material_id=?3 or -1=?3) \n" +
            "group by a.order_ppbom_id ", nativeQuery = true)
    List<Map> getPpbomRecordQtyPersonal(String orderNo, Integer processId, Integer ppbomMaterialId);


    List<TBusOrderProcessRecord> findAllByOrderProcessIdAndDevicePersonGroupIdAndRecordType(Integer orderProcessId, String devicePersonGroupId, String recordType);

    List<TBusOrderProcessRecord> findALLByOrderNoAndProcessNumberAndRecordType(String orderNo, String processNumber, String recordType);

    /**
     * 获取产后报工数据
     * @param orderProcessId
     * @return
     */
    @Query(value = "select * from t_bus_order_process_record \n" +
            "where record_type ='3' and bus_type ='BG' and order_process_id=?1",nativeQuery = true)
    List<TBusOrderProcessRecord> findByExport(Integer orderProcessId);

    /**
     * 获取产后报工积累数量
     * @param orderProcessId
     * @return
     */
    @Query(value = "select coalesce(sum(record_qty * (case when record_unit='kg' then 1 else 0.001 end) ),0) from t_bus_order_process_record \n" +
            "where record_type ='3' and bus_type ='BG' and order_process_id =?1",nativeQuery = true)
    BigDecimal sumExportQty(Integer orderProcessId);

    @Query(value = "select distinct bus.order_process_id " +
            "from t_bus_order_head a \n" +
            "join t_bus_order_process_lk  a1 on a.order_id =a1.order_id \n" +
            "join (select a.*,b.order_process_id  to_order_process_id from t_bus_order_process a " +
            "left join (select order_process_id ,old_order_process_id from t_bus_order_process where type='3') as  b " +
            "on a.order_process_id =b.old_order_process_id \n ) a2 " +
            "on a1.order_process_id =a2.order_process_id \n" +
            "left join t_sys_process_info d on a2.process_id =d.process_id \n" +
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
            "left join  t_bus_order_process_record bus on d .process_id=bus.process_id " +
            "where order_status='1' and d.process_number='GX005' and a2.process_status in('1','2') order by bus.order_process_id desc"
            , nativeQuery = true)
    List<Integer> getAllLsmOrderProcessId(String userId);
}
