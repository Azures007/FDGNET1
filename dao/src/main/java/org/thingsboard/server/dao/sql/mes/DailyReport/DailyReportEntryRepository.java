package org.thingsboard.server.dao.sql.mes.DailyReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.DailyReportEntry;

import java.util.List;

public interface DailyReportEntryRepository extends JpaRepository<DailyReportEntry,Integer> {



    List<DailyReportEntry> findByDailyreportId (Integer id);

}
