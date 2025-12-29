package org.thingsboard.server.dao.sql.mes.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;

import java.util.List;

/**
 * @author
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.mes.report
 * @date 2025/12/29
 * @Description: 报工记录数据访问接口
 */
public interface ReportRecordRepository extends JpaRepository<TBusOrderProcessHistory, Integer>, JpaSpecificationExecutor<TBusOrderProcessHistory> {

}