package org.thingsboard.server.dao.sql.mes.tSysQualityReport;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.thingsboard.server.common.data.mes.sys.TSysQualityReportItem;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/21 10:19
 * @Description:
 */
public interface TSysQualityReportItemRepository extends JpaRepository<TSysQualityReportItem,Integer> {
    @Transactional
    @Modifying
    void deleteByCategoryId(Integer id);

    List<TSysQualityReportItem> findByCategoryId(Integer id, Sort sort);
}
