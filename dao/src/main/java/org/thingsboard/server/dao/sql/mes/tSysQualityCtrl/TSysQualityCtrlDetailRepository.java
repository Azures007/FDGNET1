package org.thingsboard.server.dao.sql.mes.tSysQualityCtrl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCtrlDetail;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/18 14:43:01
 */
public interface TSysQualityCtrlDetailRepository extends JpaRepository<TSysQualityCtrlDetail,Integer>, JpaSpecificationExecutor<TSysQualityCtrlDetail> {


    void deleteByCtrlId(Integer ctrlId);

    List<TSysQualityCtrlDetail> findByCtrlId(Integer ctrlId);
}
