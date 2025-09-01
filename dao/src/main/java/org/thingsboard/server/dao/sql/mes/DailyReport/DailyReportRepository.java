package org.thingsboard.server.dao.sql.mes.DailyReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.DailyReportHead;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface DailyReportRepository extends JpaRepository<DailyReportHead,Integer> {

    @Query(value = "select t.bill_no as billNo \n" +
            "from t_bus_daily_report_head t \n" +
            "where t.bill_no LIKE :billNoParam || '%'  " +
            "order by bill_no desc LIMIT 1", nativeQuery = true)
    String getHaveBillNo(@Param("billNoParam") String billNoParam);
    List<DailyReportHead> findAllByProdLineIdAndCreatedTimeBetweenOrderByIdDesc(String prodLineId,LocalDate startTime, LocalDate endTime);

    List<DailyReportHead> findAllByProdLineIdAndSubmitOrderByIdDesc(String prodLineId,boolean submit);

    List<DailyReportHead> findAllByProdLineIdAndSaveStausOrderByIdDesc(String prodLineId,boolean saveStaus);
}
