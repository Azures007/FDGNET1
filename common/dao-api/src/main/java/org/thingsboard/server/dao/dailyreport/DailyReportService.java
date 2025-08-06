package org.thingsboard.server.dao.dailyreport;

import org.thingsboard.server.dao.dto.DailyReportDto;
import org.thingsboard.server.dao.vo.DailyReportVo;
import org.thingsboard.server.dao.vo.PageVo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

public interface DailyReportService {


    PageVo<Map<String, Object>> selectShopPerson(String name, Integer current, Integer size);

    String getBillNo(DailyReportVo dailyReportVo) throws SQLException;

    DailyReportVo getDailyDetail(DailyReportVo dailyReportVo);


    void saveDaily(DailyReportVo dailyReportVo);

    DailyReportVo DailyDetail(Integer id);


    PageVo<DailyReportVo> getDailyList(Integer current, Integer size, LocalDate startTime, LocalDate endTime);
}
