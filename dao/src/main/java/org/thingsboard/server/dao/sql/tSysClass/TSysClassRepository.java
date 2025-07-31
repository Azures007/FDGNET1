package org.thingsboard.server.dao.sql.tSysClass;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TSysClass;

import java.util.List;
import java.util.Map;

/**
 * @author cms
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.tSysClass
 * @date 2022/4/12 11:01
 * @Description:
 */
public interface TSysClassRepository extends JpaRepository<TSysClass,Integer> {
    @Query(value = "select a.name,a.class_id,a.group_leader as groupleader,count(b.class_personnel_id) as groupsize,a.process,t1.group_leader_id,a.workshop_director \n" +
            "            from t_sys_class a\n" +
            "            left join t_sys_class_personnel_rel b on a.class_id =b.class_id\n" +
            "            join CLASS_GROUP_LEADER_VIEW t1 on t1.class_id = a.class_id \n" +
            "            where t1.group_leader_id=?1 \n" +
            "            group by a.name,a.group_leader,a.process,a.class_id,a.group_leader_id ,t1.group_leader_id,a.workshop_director limit 1 ",nativeQuery = true)
    Map<String, Object> getUserClass(String userId);

    @Query(value = "select a.name,a.class_id,a.group_leader as groupleader,count(b.class_personnel_id) as groupsize,a.process,t1.user_id\n" +
            "            from t_sys_class a\n" +
            "            left join t_sys_class_personnel_rel b on a.class_id =b.class_id\n" +
            "            join t_sys_personnel_info t1 on b.personnel_id = t1.personnel_id \n" +
            "            where t1.user_id=?1 \n" +
            "            group by a.name,a.group_leader,a.process,a.class_id,a.group_leader_id ,t1.user_id limit 1 ",nativeQuery = true)
    Map<String, Object> getUserClass2(String userId);

    TSysClass getByGroupLeaderID(String userId);

//    @Query(value = "select a.class_id ,name,process,group_leader ,scheduling ,crt_time ,crt_user " +
//            ",update_time ,update_user ,enabled_st ,group_leader_id ,scheduling_code_dsc ,b.cou as team_num, " +
//            "a.class_number, a.belong_process_id, a.kd_org_id, a.kd_dept_id \n" +
//            " from t_sys_class a join (select a.class_id ,count(b.process_id) as cou \n" +
//            "from t_sys_class a join t_sys_process_class_rel b on a.class_id =b.class_id \n" +
//            "group by a.class_id ) as b on a.class_id =b.class_id " +
//            "where a.class_id in(:classIds)",nativeQuery = true)
//    List<TSysClass> findAllByIds(@Param("classIds") List<Integer> classIds);

    @Query(value = "select a.*, COALESCE (b.cou,0) as team_num \n" +
            "from t_sys_class a \n" +
            "left join (select a.class_id ,count(b.class_personnel_id) as cou \n" +
            "from t_sys_class a join t_sys_class_personnel_rel b on a.class_id =b.class_id \n" +
            "group by a.class_id ) as b on a.class_id =b.class_id\n" +
            "where 1=1 \n" +
            "and (a.name like %:#{#tSysClass.name}% or :#{#tSysClass.name} is null or :#{#tSysClass.name} ='')\n" +
            "and (a.class_number like %:#{#tSysClass.classNumber}% or :#{#tSysClass.classNumber} is null or :#{#tSysClass.classNumber} ='')\n" +
            "and (a.enabled_st=:#{#tSysClass.enabledSt} or :#{#tSysClass.enabledSt} is null or :#{#tSysClass.enabledSt} ='')",nativeQuery = true)
    Page<TSysClass> findAllBy(@Param("tSysClass") TSysClass tSysClass, Pageable pageable);

    //获取班别和组员的导出
    @Query(value = "select a.name,a.class_number as classNumber,a.scheduling,\n" +
            "c.name as groupLeader,c.phone as groupLeaderPhone,c.station as groupLeaderStation \n" +
            "from t_sys_class a " +
            "join t_sys_class_group_leader_rel b on a.class_id = b.class_id " +
            "join t_sys_personnel_info c on b.personnel_id = c.personnel_id " +
            "where 1=1 \n" +
            "and (a.name like %:#{#tSysClass.name}% or :#{#tSysClass.name} is null or :#{#tSysClass.name} ='')\n" +
            "and (a.class_number like %:#{#tSysClass.classNumber}% or :#{#tSysClass.classNumber} is null or :#{#tSysClass.classNumber} ='')\n" +
            "and (a.enabled_st=:#{#tSysClass.enabledSt} or :#{#tSysClass.enabledSt} is null or :#{#tSysClass.enabledSt} ='')" +
            " limit :size offset :current",nativeQuery = true)
    List<Object[]> findAllByExp(@Param("tSysClass") TSysClass tSysClass, @Param("current") Integer current,
                         @Param("size") int size);

    //获取班别和组员的导出
    @Query(value = "select a.name,a.class_number as classNumber,a.scheduling,\n" +
            "c.name as personnel,c.phone as personnelPhone,c.station as personnelStation \n" +
            "from t_sys_class a " +
            "join t_sys_class_personnel_rel b on a.class_id = b.class_id " +
            "join t_sys_personnel_info c on b.personnel_id = c.personnel_id " +
            "where 1=1 \n" +
            "and (a.name like %:#{#tSysClass.name}% or :#{#tSysClass.name} is null or :#{#tSysClass.name} ='')\n" +
            "and (a.class_number like %:#{#tSysClass.classNumber}% or :#{#tSysClass.classNumber} is null or :#{#tSysClass.classNumber} ='')\n" +
            "and (a.enabled_st=:#{#tSysClass.enabledSt} or :#{#tSysClass.enabledSt} is null or :#{#tSysClass.enabledSt} ='')" +
            " limit :size offset :current",nativeQuery = true)
    List<Object[]> findAllByExp2(@Param("tSysClass") TSysClass tSysClass, @Param("current") Integer current,
                                @Param("size") int size);

    /**
     * 根据班别名称查询
     * @param name
     * @return
     */
    List<TSysClass> findByName(String name);

    /**
     * 获取组长
     * @param classId
     * @return
     */
    @Query(value = "select a.class_id classId, a.name className,a.class_number classNumber, \n" +
            "c.name AS personName,c.phone AS personnelPhone,c.station AS personnelStation,c.user_id userId \n" +
            "FROM t_sys_class a\n" +
            "right join t_sys_class_group_leader_rel b on a.class_id = b.class_id \n" +
            "right join t_sys_personnel_info c on b.personnel_id = c.personnel_id \n" +
            "where a.class_id =:classId",nativeQuery = true)
    List<Map> findClassLeaderByClassId(@Param("classId") Integer classId);

    @Query(value = "select a.class_id classId, a.name className,a.class_number classNumber, \n" +
            "c.name AS personName,c.phone AS personnelPhone,c.station AS personnelStation \n" +
            ",c.user_id userId \n" +
            "FROM t_sys_class a\n" +
            "right join t_sys_class_personnel_rel b on a.class_id = b.class_id \n" +
            "right join t_sys_personnel_info c on b.personnel_id = c.personnel_id \n" +
            "where a.class_id =:classId",nativeQuery = true)
    List<Map> findPersonByClassId(@Param("classId") Integer classId);

    List<TSysClass> findAllByGroupLeaderID(String leaderID);


}
