package org.thingsboard.server.dao.sql.DailyReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.DailyReportEntry;
import org.thingsboard.server.dao.dto.DailyReportDto;

import java.util.List;

public interface DailyReportEntryRepository extends JpaRepository<DailyReportEntry,Integer> {



    List<DailyReportEntry> findByDailyreportId (Integer id);

}
