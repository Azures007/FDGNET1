package org.thingsboard.server.dao.dailyreport;

import org.thingsboard.server.dao.mes.vo.DailyReportVo;
import org.thingsboard.server.dao.mes.vo.PageVo;

import java.sql.SQLException;
import java.time.LocalDate;

public interface DailyReportService {


    PageVo<DailyReportVo> selectShopPerson(String id, Integer current, Integer size);

    String getBillNo(DailyReportVo dailyReportVo) throws SQLException;

    DailyReportVo getDailyDetail(DailyReportVo dailyReportVo);


    DailyReportVo saveDaily(DailyReportVo dailyReportVo);

    DailyReportVo DailyDetail(Integer id);


    PageVo<DailyReportVo> getDailyList(String userId,Integer current, Integer size, LocalDate startTime, LocalDate endTime);

    PageVo<DailyReportVo> getDailySubmitList(String userId,Integer current, Integer size,LocalDate startTime, LocalDate endTime);
}
