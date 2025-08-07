package org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.TSysClassPersonnelRel;

import java.util.List;
import java.util.Map;

/**
 * @author cms
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.tSysPersonnelInfo
 * @date 2022/4/14 09:14
 * @Description:
 */
public interface ClassPersonnelRepository extends JpaRepository<TSysClassPersonnelRel,Integer> {

    /**
     * 根据组别ID删除关系
     * @param classId
     */
    void deleteByClassId(Integer classId);

    /**
     * 根据人员ID删除关系
     * @param personnelId
     */
    void deleteByPersonnelId(Integer personnelId);

    /**
     * 根据班别ID查询关系
     * @param classId
     * @return
     */
    List<TSysClassPersonnelRel> findByClassId(Integer classId);

    @Transactional//事务的注解
    @Modifying//增删改必须有这个注解
    @Query(value = "select \n" +
            "t.class_personnel_id as classPersonnelId,\n" +
            "t.class_id as classId,\n" +
            "t1.name as className,\n" +
            "t.personnel_id as personnelId,\n" +
            "t2.name as name,\n" +
            "t2.sex as sex,\n" +
            "t2.phone as phone,\n" +
            "t2.station as station,\n" +
            "t2.station as stationName\n" +
            "from t_sys_class_personnel_rel t \n" +
            "left join t_sys_class t1 \n" +
            "on t.class_id = t1.class_id\n" +
            "left join t_sys_personnel_info t2\n" +
            "on t.personnel_id = t2.personnel_id\n" +
            "where t.class_id =?1",nativeQuery = true)
    List<Object[]> getRelByClassId(Integer classId);

    @Transactional//事务的注解
    @Modifying//增删改必须有这个注解
    @Query(value = "select distinct \n" +
            "t.class_id as classId,\n" +
            "t.name as name,\n" +
            "t.process as process,\n" +
            "t.scheduling as scheduling,\n" +
            "t.team_num as teamNum,\n" +
            "t.enabled_st as enabledSt,\n" +
            "null as crtUser,\n" +
            "null as crtTime,\n" +
            "null as updateTime,\n" +
            "null as updateUser \n" +
            "from t_sys_class t \n" +
            "left join CLASS_GROUP_LEADER_VIEW t1 \n" +
            "on t.class_id=t1.class_id \n" +
            "where t.class_id in (select a.class_id from t_sys_process_class_rel a where a.process_id=?1)",nativeQuery = true)
    List<Map> getClassByProcessId(Integer processId);

    List<TSysClassPersonnelRel> findByPersonnelId(Integer personnelId);

}
