package org.thingsboard.server.dao.sql.mes.pd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecord;

import java.util.List;
import java.util.Map;

public interface TSysPdRecordRepository extends JpaRepository<TSysPdRecord, Integer>, JpaSpecificationExecutor<TSysPdRecord> {
    /**
     * 获取同班组、车间、日期的最新盘点记录
     * @param pdTimeStr
     * @param pdWorkshopNumber
     * @param pdClassNumber
     * @return
     */
    @Query(value = "select * from t_sys_pd_record " +
            "where pd_time_str=?1 and by_deleted='0' and material_number=?2 and pd_class_number=?3 and pd_type=?4 " +
            "order by pd_time desc limit 1",nativeQuery = true)
    TSysPdRecord findByGroup(String pdTimeStr, String pdWorkshopNumber, String pdClassNumber,String pdType);

    /**
     * 根据日期、物料编号、班组编号、类型和产线名称获取最新的盘点记录
     * @param pdTimeStr
     * @param materialNumber
     * @param pdClassNumber
     * @param pdType
     * @param ncVwkname
     * @return
     */
    @Query(value = "select * from t_sys_pd_record " +
            "where pd_time_str=?1 and by_deleted='0' and material_number=?2 and pd_class_number=?3 and pd_type=?4 and nc_vwkname=?5 " +
            "order by pd_time desc limit 1",nativeQuery = true)
    TSysPdRecord findByGroupAndWorkshop(String pdTimeStr, String materialNumber, String pdClassNumber, String pdType, String ncVwkname);

    /**
     * 逻辑删除同班组、车间、日期的盘点记录
     * @param format
     * @param pdWorkshopNumber
     * @param pdClassNumber
     */
    @Modifying
    @Query(value = "update t_sys_pd_record set by_deleted='1' " +
            "where pd_time_str=?1 and pd_workshop_number=?2 and pd_class_number=?3  and pd_type='0' ",nativeQuery = true)
    void updatePd(String format, String pdWorkshopNumber, String pdClassNumber);

    @Query(value = "SELECT pd_time_str, pd_workshop_number,pd_workshop_leader_name,pd_workshop_name,nc_vwkname,review_status,max(pd_created_name) pd_created_name\n" +
            "FROM t_sys_pd_record\n" +
            "WHERE to_timestamp(pd_time_str, 'YYYY-MM-DD') \n" +
            "    BETWEEN to_timestamp(?1, 'YYYY-MM-DD') \n" +
            "    AND to_timestamp(?2, 'YYYY-MM-DD') \n" +
            "    and by_deleted ='0' and pd_workshop_number in ?3   \n" +
            "group by pd_time_str, pd_workshop_number,pd_workshop_name,pd_workshop_leader_name,nc_vwkname,review_status \n" +
            "order by pd_time_str desc\n",nativeQuery = true)
    List<Map> fpWorkshopRecord(String startDate, String endDate,List<String> wokIds);

    @Query(value = "select * from t_sys_pd_record  \n" +
            "where pd_time_str=?1 and pd_workshop_number=?2 and nc_vwkname=?3 and by_deleted ='0' and pd_qty > 0\n" +
            "and material_number not like 'FINISHED_MATERIAL_TYPE_MARKER_%' " +
            "order by pd_time desc ",nativeQuery = true)
    List<TSysPdRecord> showWorkshopRecord(String pdTimeStr, String pdWorkshopNumber, String ncVwkname);
    
    /**
     * 查找指定日期、产线和物料分类的完成状态记录
     * @param pdTimeStr 盘点日期
     * @param ncVwkname 产线名称
     * @param materialType 物料分类
     * @return 完成状态记录
     */
    @Query(value = "select * from t_sys_pd_record " +
            "where pd_time_str=?1 and nc_vwkname=?2 and by_deleted='0' " +
            "and pd_type='2' and material_number='FINISHED_MATERIAL_TYPE_MARKER_'||?3 " +
            "limit 1", nativeQuery = true)
    TSysPdRecord findByPdTimeStrAndNcVwknameAndMaterialTypeFinished(String pdTimeStr, String ncVwkname, String materialType);
    
}