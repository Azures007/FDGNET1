package org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.mes.sys.TSysUserDevices;

import java.util.List;
import java.util.Map;

public interface TSysUserDevicesRepository extends JpaRepository<TSysUserDevices,Integer> {
    @Query(value = "select a.devices_key,b.* from t_sys_user_devices a \n" +
            "join t_sys_personnel_info b on a.personnel_id =b.personnel_id \n" +
            "where a.devices_type ='1' and a.enabled ='1' and b.personnel_id in (?1)",nativeQuery = true)
    List<Map> listInpersonnel(List<Integer> personnelIds);
}
