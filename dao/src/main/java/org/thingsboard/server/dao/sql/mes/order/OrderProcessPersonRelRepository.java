package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessPersonRel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: hhh
 * @Date: 2022/8/1 20:20
 * @Description:
 */
public interface OrderProcessPersonRelRepository extends JpaRepository<TBusOrderProcessPersonRel, Integer> {

    @Query(value = "select device_person_group_id  from (select device_person_group_id,count(device_person_id) as count from t_bus_order_process_person_rel a \n" +
            "            where order_process_id =:orderProcessId \n" +
            "            and device_person_id in (:personIds) \n" +
            "            and device_person_group_id not in (\n" +
            "            select device_person_group_id from t_bus_order_process_person_rel \n" +
            "            where device_person_id not in (:personIds)  group by device_person_group_id )\n" +
            "            group by device_person_group_id) t where t.count=:size", nativeQuery = true)
    String getPersonGroupId(@Param("orderProcessId") Integer orderProcessId,
                            @Param("personIds") List<Integer> personIds,
                            @Param("size") int size);

    List<TBusOrderProcessPersonRel> findAllByDevicePersonGroupId(String personGroupId);

    /**
     * 当前分组人员岗位信息，多个逗号隔开
     * @param devicePersonGroupId
     * @return
     */
    @Query(value = "\n" +
            " select device_person_group_id,string_agg(c.code_dsc,',') station_name,string_agg(station,',') station from t_sys_personnel_info a \n" +
            " join t_bus_order_process_person_rel  b on a.personnel_id =b.device_person_id \n" +
            "left join (select * from t_sys_code_dsc where code_cl_id='JOB0000' ) c on c.code_value =a.station " +
            " where b.device_person_group_id =?1\n" +
            " group by device_person_group_id\n" +
            " ",nativeQuery = true)
    Map getDevicePersonStation(String devicePersonGroupId);

    /**
     * 当前分组岗位列表
     * @param devicePersonGroupId
     * @return
     */
    @Query(value = " select station station_number ,c.code_dsc station_name from t_sys_personnel_info a \n" +
            " join t_bus_order_process_person_rel  b on a.personnel_id =b.device_person_id \n" +
            " left join (select * from t_sys_code_dsc where code_cl_id='JOB0000' ) c on c.code_value =a.station \n" +
            " where b.device_person_group_id in (?1)",nativeQuery = true)
    List<Map> getStationListByGroup(Set<String> devicePersonGroupId);

    /**
     * 当前分组操作员列表
     * @param devicePersonGroupId
     * @return
     */
    @Query(value = " select distinct a.personnel_id person_id, a.name person_name from t_sys_personnel_info a \n" +
            " join t_bus_order_process_person_rel  b on a.personnel_id =b.device_person_id \n" +
            " where b.device_person_group_id in (?1)",nativeQuery = true)
    List<Map> getDevicePersonListByGroup(Set<String> devicePersonGroupId);

    /**
     * 当前分组机台号列表
     * @param deviceGroupId
     * @return
     */
    @Query(value = " select distinct a.device_id device_id, a.device_name device_name from t_sys_device a \n" +
            " join t_bus_order_process_device_rel b on a.device_id = b.device_id \n" +
            " where b.device_group_id in (?1)",nativeQuery = true)
    List<Map> getDeviceListByGroup(Set<String> deviceGroupId);



//    @Query(value = "select  concat(info.name,',') from t_bus_order_process_person_rel rel left join t_sys_personnel_info info on rel.device_person_id=info.personnel_id\n" +
//            "where rel.device_person_group_id=:personGroupId",nativeQuery = true)
//    String getPersonGroups(@Param("personGroupId")String personGroupId);
}
