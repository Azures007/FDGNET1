package org.thingsboard.server.dao.sql.tSysPersonnelInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysPersonnelInfo;

import java.util.List;

/**
 * @author cms
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.tSysPersonnelInfo
 * @date 2022/4/13 14:01
 * @Description:
 */
public interface TSysPersonnelInfoRepository extends JpaRepository<TSysPersonnelInfo,Integer> {

    @Transactional//事务的注解
    @Modifying//增删改必须有这个注解
    @Query(value = "update t_sys_personnel_info  set class_name =(select t1.name from t_sys_class t1 where t1.class_id =?2) where personnel_id in (?1)",nativeQuery = true)
    int updateClassNameByClassId(List<Integer> idList, Integer classId);

    TSysPersonnelInfo findByUserId(String userId);

    TSysPersonnelInfo getByUserId(String userId);

    @Query(value = "select a.* \n" +
            "from t_sys_personnel_info a " +
            "where 1=1 \n" +
            "and (a.name like %:#{#personInfo.name}% or :#{#personInfo.name} is null or :#{#personInfo.name} ='')\n" +
            "and (a.station like %:#{#personInfo.station}% or :#{#personInfo.station} is null or :#{#personInfo.station} ='')\n" +
            "and (a.enabled_st=:#{#personInfo.enabledSt} or :#{#personInfo.enabledSt} is null or :#{#personInfo.enabledSt} ='')" +
            "and personnel_id in (\n" +
            "-- 查询班别关联的组员组长\n" +
            "select personnel_id from (\n" +
            "select a.class_id,b.personnel_id\n" +
            "from t_sys_class a\n" +
            "join t_sys_class_group_leader_rel b on a.class_id = b.class_id\n" +
            "union\n" +
            "select a.class_id,b.personnel_id\n" +
            "from t_sys_class a\n" +
            "join t_sys_class_personnel_rel b on a.class_id = b.class_id\n" +
            ") t where 1=1 and class_id in (\n" +
            "--查询员工关联的班别\n" +
            "select class_id from (\n" +
            "select a.class_id,b.personnel_id\n" +
            "from t_sys_class a\n" +
            "join t_sys_class_group_leader_rel b on a.class_id = b.class_id\n" +
            "union\n" +
            "select a.class_id,b.personnel_id\n" +
            "from t_sys_class a\n" +
            "join t_sys_class_personnel_rel b on a.class_id = b.class_id\n" +
            ") t where 1=1 and personnel_id = :personelId \n" +
            ")\n" +
            ")"
            ,nativeQuery = true)
    Page<TSysPersonnelInfo> findAllBy(@Param("personInfo") TSysPersonnelInfo tSysPersonnelInfo, @Param("personelId") Integer personelId, Pageable pageable);

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

    @Query(value = "select a.personnel_id \n" +
            "from t_sys_personnel_info a " +
            "where 1=1 \n" +
            "and personnel_id in (\n" +
            "-- 查询班别关联的组员组长\n" +
            "select personnel_id from (\n" +
            "select a.class_id,b.personnel_id\n" +
            "from t_sys_class a\n" +
            "join t_sys_class_group_leader_rel b on a.class_id = b.class_id\n" +
            "union\n" +
            "select a.class_id,b.personnel_id\n" +
            "from t_sys_class a\n" +
            "join t_sys_class_personnel_rel b on a.class_id = b.class_id\n" +
            ") t where 1=1 and class_id in (\n" +
            "--查询员工关联的班别\n" +
            "select class_id from (\n" +
            "select a.class_id,b.personnel_id\n" +
            "from t_sys_class a\n" +
            "join t_sys_class_group_leader_rel b on a.class_id = b.class_id\n" +
            "union\n" +
            "select a.class_id,b.personnel_id\n" +
            "from t_sys_class a\n" +
            "join t_sys_class_personnel_rel b on a.class_id = b.class_id\n" +
            ") t where 1=1 and personnel_id = :personelId \n" +
            ")\n" +
            ")"
            ,nativeQuery = true)
    List<Integer> findAllByPersonnel(@Param("personelId") Integer personnelId);
}
