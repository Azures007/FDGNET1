package org.thingsboard.server.dao.sql.mes.production;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;

import java.util.Date;
import java.util.List;

/**
 * 投入产出比报表数据访问接口
 */
@Repository
public interface ProductionDataRepository extends CrudRepository<TBusOrderHead, Integer> {

    /**
     * 根据时间范围查询订单头信息
     */
    @Query(value = "SELECT t FROM TBusOrderHead t WHERE t.billDate >= :startTime AND t.billDate <= :endTime")
    List<TBusOrderHead> findByTimeRange(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据订单号查询外包装工序的历史记录
     */
    @Query("SELECT t FROM TBusOrderProcessHistory t WHERE t.orderNo IN :orderNos AND t.processNumber = 'GX-011'")
    List<TBusOrderProcessHistory> findPackagingProcesses(@Param("orderNos") List<String> orderNos);

    /**
     * 根据订单号查询投入物料信息
     */
    @Query("SELECT t FROM TBusOrderProcessHistory t WHERE t.orderNo IN :orderNos")
    List<TBusOrderProcessHistory> findInputMaterials(@Param("orderNos") List<String> orderNos);
}