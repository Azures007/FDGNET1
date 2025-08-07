package org.thingsboard.server.dao.sql.mes.DailyReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.DailyReportItem;

import java.util.List;

public interface DailyReportItemRepository extends JpaRepository<DailyReportItem,Integer> {

    List<DailyReportItem> findAllByDailyreportEntryId (Integer id);
}
