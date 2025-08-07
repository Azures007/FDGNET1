package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessDeviceRel;

import java.util.List;

/**
 * @Auther: hhh
 * @Date: 2022/8/1 20:20
 * @Description:
 */
public interface OrderProcessDeviceRelRepository extends JpaRepository<TBusOrderProcessDeviceRel,Integer> {

    @Query(value = "select device_group_id  from (select device_group_id,count(device_id) as count from t_bus_order_process_device_rel a \n" +
            "where order_process_id =:orderProcessId \n" +
            "and device_id in (:deviceIds) \n" +
            "and device_group_id not in (\n" +
            "select device_group_id from t_bus_order_process_device_rel \n" +
            "where device_id not in (:deviceIds)  group by device_group_id )\n" +
            "group by device_group_id) t where t.count=:size",nativeQuery = true)
    String getDeviceGroupId(@Param("orderProcessId") Integer orderProcessId,
                            @Param("deviceIds") List<Integer> deviceIds,
                            @Param("size") Integer size);

    List<TBusOrderProcessDeviceRel> findByDeviceGroupId(String deviceGroupId);

    @Query(value = "select device_group_id  from (select device_group_id,count(device_id) as count from t_bus_order_process_device_rel a \n" +
            "where order_process_id =:orderProcessId \n" +
            "and device_id in (:deviceIds) \n" +
            "and device_group_id not in (\n" +
            "select device_group_id from t_bus_order_process_device_rel \n" +
            "where device_id not in (:deviceIds)  group by device_group_id )\n" +
            "group by device_group_id) t where t.count=:size",nativeQuery = true)
    Integer countDeviceGroupId(@Param("orderProcessId") Integer orderProcessId,
                               @Param("deviceIds") List<Integer> deviceIds,
                               @Param("deviceIds") List<Integer> deviceIds2);

//    @Query(value = "select  concat(info.device_name,',') from t_bus_order_process_device_rel rel left join t_sys_device info on rel.device_id=info.device_id\n" +
//            "where rel.device_group_id=:deviceGroupId",nativeQuery = true)
//    String getDeviceNameGroups(@Param("deviceGroupId")String deviceGroupId);

}
