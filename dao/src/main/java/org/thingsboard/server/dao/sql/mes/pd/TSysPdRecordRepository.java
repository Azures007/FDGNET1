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
            "where pd_time_str=?1 and by_deleted='0' and pd_workshop_number=?2 and pd_class_number=?3 and pd_type='0'" +
            "order by pd_time desc limit 1",nativeQuery = true)
    TSysPdRecord findByGroup(String pdTimeStr, String pdWorkshopNumber, String pdClassNumber);

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

    @Query(value = "SELECT pd_time_str, pd_workshop_number,pd_workshop_name,max(pd_created_name) pd_created_name\n" +
            "FROM t_sys_pd_record\n" +
            "WHERE to_timestamp(pd_time_str, 'YYYY-MM-DD') \n" +
            "    BETWEEN to_timestamp(?1, 'YYYY-MM-DD') \n" +
            "    AND to_timestamp(?2, 'YYYY-MM-DD') \n" +
            "    and pd_type ='0' and by_deleted ='0'   \n" +
            "group by pd_time_str, pd_workshop_number,pd_workshop_name \n" +
            "order by pd_time_str desc\n",nativeQuery = true)
    List<Map> fpWorkshopRecord(String startDate, String endDate);
    @Query(value = "select * from t_sys_pd_record  \n" +
            "where pd_time_str=?1 and pd_workshop_number=?2  and by_deleted ='0'\n" +
            "order by pd_time desc ",nativeQuery = true)
    List<TSysPdRecord> showWorkshopRecord(String pdTimeStr, String pdWorkshopNumber);

}
