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
    
    /**
     * 更新订单基本信息（不包含BOM关联）- 使用JPQL避免类型转换问题
     */
    @Modifying
    @Query("UPDATE NcTBusOrderHead o SET " +
            "o.orderNo = :orderNo, " +
            "o.billType = :billType, " +
            "o.cpmohid = :cpmohid, " +
            "o.vbillcode = :vbillcode, " +
            "o.cmoid = :cmoid, " +
            "o.seq = :seq, " +
            "o.dbilldate = :dbilldate, " +
            "o.cdeptname = :cdeptname, " +
            "o.cdeptid = :cdeptid, " +
            "o.vwkname = :vwkname, " +
            "o.cwkid = :cwkid, " +
            "o.pkMaterial = :pkMaterial, " +
            "o.code = :code, " +
            "o.name = :name, " +
            "o.materialspec = :materialspec, " +
            "o.nnum = :nnum, " +
            "o.tplanstarttime = :tplanstarttime, " +
            "o.tplanendtime = :tplanendtime, " +
            "o.ncReceiveTime = :ncReceiveTime, " +
            "o.ncNote = :ncNote, " +
            "o.unit = :unit, " +
            "o.isDeleted = :isDeleted, " +
            "o.createdTime = :createdTime " +
            "WHERE o.orderId = :orderId")
    void updateOrderBasicInfo(
            @Param("orderId") Integer orderId,
            @Param("orderNo") String orderNo,
            @Param("billType") String billType,
            @Param("cpmohid") String cpmohid,
            @Param("vbillcode") String vbillcode,
            @Param("cmoid") String cmoid,
            @Param("seq") String seq,
            @Param("dbilldate") java.util.Date dbilldate,
            @Param("cdeptname") String cdeptname,
            @Param("cdeptid") String cdeptid,
            @Param("vwkname") String vwkname,
            @Param("cwkid") String cwkid,
            @Param("pkMaterial") String pkMaterial,
            @Param("code") String code,
            @Param("name") String name,
            @Param("materialspec") String materialspec,
            @Param("nnum") java.math.BigDecimal nnum,
            @Param("tplanstarttime") java.util.Date tplanstarttime,
            @Param("tplanendtime") java.util.Date tplanendtime,
            @Param("ncReceiveTime") java.util.Date ncReceiveTime,
            @Param("ncNote") String ncNote,
            @Param("unit") String unit,
            @Param("isDeleted") String isDeleted,
            @Param("createdTime") java.util.Date createdTime
    );
}
