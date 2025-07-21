package org.thingsboard.server.dao.sql.tSysQualityCtrl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysQualityCtrlDetail;
import org.thingsboard.server.common.data.TSysQualityPlanJudgment;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/18 14:43:01
 */
public interface TSysQualityCtrlDetailRepository extends JpaRepository<TSysQualityCtrlDetail,Integer> {


    void deleteByCtrlId(Integer ctrlId);
}
