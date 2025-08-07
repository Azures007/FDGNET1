package org.thingsboard.server.dao.sql.mes.tSysQualityCtrl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCtrl;
import org.thingsboard.server.dao.mes.dto.TSysQualityCtrlDto;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/18 14:42:49
 */
public interface TSysQualityCtrlRepository extends JpaRepository<TSysQualityCtrl,Integer> {

    @Query(value = "SELECT t.id, t.quality_ctrl_no, t.material_id, t.material_name, t.plan_id, t.plan_name, \n" +
            "t.production_line_id, t.production_line_name, \n" +
            "t.remarks, t.create_time, t.create_user, t.update_time, t.update_user, t.inspection_date,t.status \n" +
            "FROM t_sys_quality_ctrl t \n" +
            "where 1=1 \n" +
            "and (t.inspection_date >= :#{#tSysQualityCtrl.inspectionStartTime}) \n" +
            "and (t.inspection_date <= :#{#tSysQualityCtrl.inspectionEndTime})",nativeQuery = true)
    Page<TSysQualityCtrl> findAllBy(@Param("tSysQualityCtrl") TSysQualityCtrlDto tSysQualityCtrl, Pageable pageable);


//    List<TSysQualityCtrl> findByPlanName(String planName);

}
