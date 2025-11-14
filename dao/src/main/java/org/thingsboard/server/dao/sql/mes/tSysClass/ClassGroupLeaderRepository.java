package org.thingsboard.server.dao.sql.mes.tSysClass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.TSysClassGroupLeaderRel;


import java.util.List;

/**
 * @author cms
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.tSysClass
 * @date 2022/5/09 09:14
 * @Description:
 */
public interface ClassGroupLeaderRepository extends JpaRepository<TSysClassGroupLeaderRel,Integer> {

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
    List<TSysClassGroupLeaderRel> findByClassId(Integer classId);

    @Transactional//事务的注解
    @Modifying//增删改必须有这个注解
    @Query(value = "select \n" +
            "t.class_group_leader_id as classGroupLeaderId,\n" +
            "t.class_id as classId,\n" +
            "t1.name as className,\n" +
            "t.personnel_id as personnelId,\n" +
            "t2.name as name,\n" +
            "t2.sex as sex,\n" +
            "t2.phone as phone\n" +
            "from t_sys_class_group_leader_rel t \n" +
            "left join t_sys_class t1 \n" +
            "on t.class_id = t1.class_id\n" +
            "left join t_sys_personnel_info t2\n" +
            "on t.personnel_id = t2.personnel_id\n" +
            "where t.class_id =?1",nativeQuery = true)
    List<Object[]> getRelByClassId(Integer classId);

    @Query(value = "select \n" +
            "t1.group_leader_id as leader_id\n" +
            "from t_sys_class t \n" +
            "left join CLASS_GROUP_LEADER_VIEW t1 \n" +
            "on t.class_id=t1.class_id \n" +
            "where t.class_id =?1",nativeQuery = true)
    List<String> getLeaderUserIdsByProcessId(Integer classId);

    @Transactional//事务的注解
    @Modifying//增删改必须有这个注解
    @Query(value = "select \n" +
            "t.class_group_leader_id as classGroupLeaderId,\n" +
            "t.class_id as classId,\n" +
            "t.personnel_id as personnelId,\n" +
            "null as crtUser,\n" +
            "null as crtTime\n" +
            "from t_sys_class_group_leader_rel t \n" +
            "where t.class_id <> ?1 and t.personnel_id in (?2)",nativeQuery = true)
    List<Object[]> getRelByPersonnelIdAndOtherClass(Integer classId,List<Integer> personnelId);

    List<TSysClassGroupLeaderRel> findByPersonnelId(Integer personnelId);

    List<TSysClassGroupLeaderRel> findAllByClassId(Integer classId);

    // 获取班别绑定的组长人员名称
    @Query(value = "select c.name AS personnelName \n" +
            "from t_sys_class_group_leader_rel b\n" +
            "left join t_sys_personnel_info c on b.personnel_id = c.personnel_id \n" +
            "where b.class_id = ?1 \n",nativeQuery = true)
    List<String> getLeaderNameByClassId(Integer classId);
}
