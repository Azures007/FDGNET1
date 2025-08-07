package org.thingsboard.server.dao.sql.mes.ncOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.ncOrder.NcTBusOrderHead;

import java.util.List;

public interface NcTBusOrderHeadRepository extends JpaRepository<NcTBusOrderHead, Integer> {
    NcTBusOrderHead findByCmoid(String cmoid);
    @Query("SELECT w FROM NcTBusOrderHead w WHERE w.cpmohid IN :cpmohids")
    List<NcTBusOrderHead> findByCpmohids(@Param("cpmohids") List<String> cpmohids);
    /**
     * 逻辑删除
     */
    @Modifying
    @Query("UPDATE NcTBusOrderHead SET isDeleted='1' WHERE cpmohid IN :cpmohids")
    void deleteByCpmohids(@Param("cpmohids") List<String> cpmohids);
    /**
     * 查询已开工的订单号
     */
    @Query("SELECT distinct w.vbillcode FROM NcTBusOrderHead w WHERE w.ncReceiveTime!=null and w.cpmohid IN :cpmohids")
    List<String> findVbillcodeByCpmohids(@Param("cpmohids") List<String> cpmohids);
}
