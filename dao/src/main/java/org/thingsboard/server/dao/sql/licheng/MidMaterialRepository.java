package org.thingsboard.server.dao.sql.licheng;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.MidMaterial;

import java.util.HashSet;
import java.util.List;

public interface MidMaterialRepository extends JpaRepository<MidMaterial,Integer> {
    @Modifying
    @Query("delete from MidMaterial m where m.kdMaterialNumber=?1")
    void deleteBykdMaterialNumber(String kdMaterialNumber);

    MidMaterial getByKdMaterialNumber(String materialNumber);

    MidMaterial getBykdMaterialId(int parseInt);

    List<MidMaterial> findAllByKdMaterialNumber(String materialNumber);

    @Modifying
    @Query("delete from MidMaterial where kdMaterialNumber in (:codes)")
    void deleteByCodes(@Param("codes") HashSet<String> codes);

    @Modifying
    @Query("delete from MidMaterial where kdMaterialId in (:ids)")
    void deleteByIds(@Param("ids") HashSet<Integer> ids);
}
