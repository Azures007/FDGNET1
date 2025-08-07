package org.thingsboard.server.dao.sql.mes.tSysDevice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.mes.sys.TsysDevice;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/19 11:16
 * @Description:
 */
public interface TSysDeviceRepository extends JpaRepository<TsysDevice,Integer> {
    List<TsysDevice> findByDeviceNumber(String deviceNumber);


    List<TsysDevice> findByBelongProcessIdAndEnabled(Integer belongProcessId, String enabled);

    @Query(value = "select * from (\n" +
            " select * from t_sys_device where device_id in (\n" +
            " select distinct b.device_id\n" +
            " from t_bus_order_process_record a\n" +
            " left join t_bus_order_process_device_rel b on a.device_group_id=b.device_group_id\n" +
            " where a.device_person_group_id=?3 and a.order_process_id=?4\n" +
            " )\n" +
            " union\n" +
            " select * from t_sys_device where device_id not in (\n" +
            " select distinct b.device_id\n" +
            " from t_bus_order_process_record a\n" +
            " left join t_bus_order_process_device_rel b on a.device_group_id=b.device_group_id\n" +
            " where a.device_person_group_id!=?3 and a.order_process_id=?4\n" +
            " )\n" +
            ") t where belong_process_id =?1 and enabled=?2",nativeQuery = true)
    List<TsysDevice> findAllByDevicePersonGroupId(Integer belongProcessId, String enabled, String devicePersonGroupId, Integer orderProcessId);

    @Query(value = "select * from t_sys_device t where belong_process_id =?1 and enabled=?2",nativeQuery = true)
    List<TsysDevice> findAll(Integer belongProcessId, String enabled);
}
