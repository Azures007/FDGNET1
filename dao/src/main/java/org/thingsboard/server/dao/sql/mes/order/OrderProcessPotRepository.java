package org.thingsboard.server.dao.sql.mes.order;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessPot;

public interface OrderProcessPotRepository extends JpaRepository<TBusOrderProcessPot,Integer> {
//    /**
//     * 获取同一锅标识
//     */
//    @Query(value = "select coalesce(max(same_flag),0)+1 from t_bus_order_process_pot " +
//            "where order_process_id=?3 and device_id=?1 and device_person_id=?2 and order_ppbom_id=?4",nativeQuery = true)
//    int getSameFlag(Integer deviceId, Integer devicePersonId, Integer orderProcessId, Integer orderPPBomId);

    @Query(value = "select sum(su) su from (\n" +
            "select coalesce(cast(sum(record_qty / (CASE when mid_ppbom_entry_weigh_devept_qty=0 then 1 else mid_ppbom_entry_weigh_devept_qty end) * (CASE when mid_ppbom_entry_weigh_mes_qty=0 then 1 else mid_ppbom_entry_weigh_mes_qty end) )as  DECIMAL(19, 3)),0) su\n" +
            "from t_bus_order_process_record a join t_bus_order_ppbom b on a.order_ppbom_id=b.order_ppbom_id \n" +
            "where record_type ='1' and order_process_id =:orderProcessId and bus_type ='BG' \n" +
            "union all \n" +
            "select coalesce(cast(sum(record_qty) as  DECIMAL(19, 3)),0) su\n" +
            "from t_bus_order_process_record a \n" +
            "where record_type ='5' and order_process_id =:orderProcessId and bus_type ='BG' \n" +
            ") as t",nativeQuery = true)
    Float sumRecordQtyByOrderProcessIdAndRecordTypes(@Param("orderProcessId") Integer orderProcessId);

    @Query(value = "select coalesce(cast(sum(record_qty) as  DECIMAL(19, 3)),0) su\n" +
            "from t_bus_order_process_record a \n" +
            "where record_type =?2 and order_process_id =?1 and bus_type ='BG' ",nativeQuery = true)
    Float sumRecordQtyByOrderProcessIdAndRecordType(int orderProcessId, String types2);
}
