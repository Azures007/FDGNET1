package org.thingsboard.server.dao.sql.nc_workline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.nc_workline.NcWorkline;

import java.util.List;

public interface NcWorklineRepository extends JpaRepository<NcWorkline, Integer> {
    NcWorkline findByCwkid(String cwkid);
    @Query("SELECT w FROM NcWorkline w WHERE w.cwkid IN :cwkids")
    List<NcWorkline> findByCwkids(@Param("cwkids") List<String> cwkids);
    List<NcWorkline> findAll();
    List<NcWorkline> findByPkOrg(String pkOrg);
    @Query("SELECT w FROM NcWorkline w WHERE w.vwkcode LIKE %:keyword% OR w.vwkname LIKE %:keyword%")
    List<NcWorkline> findByVwkcodeOrVwknameLike(@Param("keyword") String keyword);
}
