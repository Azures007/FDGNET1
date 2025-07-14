package org.thingsboard.server.dao.sql.sync;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TSyncMaterial;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface SyncMaterialRepository extends JpaRepository<TSyncMaterial,Integer> {
    List<TSyncMaterial> getByMaterialCode(String materialCode);

    void deleteByMaterialCode(String materialCode);

    @Modifying
    @Query(value = "delete from t_sync_material where material_code in (:codes)",nativeQuery = true)
    void deleteInCode(@Param("codes") HashSet<String> codes);

    @Modifying
    @Query(value = "delete from t_sync_material where kd_material_id in (:kdIds)",nativeQuery = true)
    void deleteInKdId(@Param("kdIds") HashSet<Integer> kdIds);

    List<TSyncMaterial> findAllByMaterialCodeAndMaterialCodeIsNotIn(String materialCode,List<String> codes);

    /**
     * 主产品列表（过滤已绑定的物料代码）
     * @param materialCode
     * @return
     */
    @Query(value = "select a.* from t_sync_material a  where material_code not in (select material_code from t_sys_craft_material_rel group by material_code)",nativeQuery = true)
    Page<TSyncMaterial> listMaterialFiter(String materialCode, PageRequest pageRequest);

    /**
     * 主产品列表（过滤已绑定的物料代码）
     * @param materialCode
     * @return
     */
    @Query(value = "select a.* from t_sync_material a \n" +
            "join mid_material b on a.kd_material_id=b.kd_material_id \n" +
            "where (material_code like %?1% or material_name like %?1% ) " +
            "and b.kd_material_use_org_id=?2 and b.kd_material_workshop_id=?3 \n" +
            "and a.kd_material_id not in (select coalesce(material_id,-1) from t_sys_craft_material_rel group by material_id)",nativeQuery = true)
    Page<TSyncMaterial> listMaterialFiter(String materialCode, Integer kdOrgId, Integer kdDeptId, PageRequest pageRequest);

    /**
     * 已选择列表
     * @return
     * @param craftId
     */
    @Query(value = "select a.* from t_sync_material a \n" +
            "where material_code in (select material_code from t_sys_craft_material_rel where craft_id=?1 group by material_code)",nativeQuery = true)
    List<TSyncMaterial> listMaterialOffFiter(Integer craftId);

    @Query(value = "select a.* from t_sync_material a " +
            "where (material_code like %:#{#tSyncMaterialTo.materialCode}%" +
            " or material_name like %:#{#tSyncMaterialTo.materialName}%) " +
            "and (material_status =:#{#tSyncMaterialTo.materialStatus} or :#{#tSyncMaterialTo.materialStatus} is null or :#{#tSyncMaterialTo.materialStatus} ='') " +
            "order by created_time desc,id desc " +
            " limit :size offset :current ",nativeQuery = true)
    List<TSyncMaterial> findAllAnd(@Param("tSyncMaterialTo") TSyncMaterial tSyncMaterialTo,
                                   @Param("current") Integer current,
                                   @Param("size") Integer size);

    @Query(value = "select " +
            "       m.kd_material_membrane_thickness AS kdMaterialMembraneThickness,\n" +
            "       m.kd_material_membrane_width     AS kdMaterialMembraneWidth,\n" +
            "       m.kd_material_membrane_density   AS kdMaterialMembraneDensity,\n" +
            "       a.id                             AS id,\n" +
            "       a.material_code                  AS materialCode,\n" +
            "       a.material_name                  AS materialName,\n" +
            "       a.material_unit                  AS materialUnit,\n" +
            "       a.group_code                     AS groupCode,\n" +
            "       a.material_model                 AS materialModel,\n" +
            "       a.br                             AS br,\n" +
            "       a.material_status                AS materialStatus,\n" +
            "       a.created_time                   AS createdTime,\n" +
            "       a.created_name                   AS createdName,\n" +
            "       a.updated_time                   AS updatedTime,\n" +
            "       a.updated_name                   AS updatedName,\n" +
            "       a.kd_material_id                 AS kdMaterialId,\n" +
            "       m.kd_material_workshop_id        AS kdMaterialWorkshopId,\n" +
            "       m.kd_material_workshop_name      AS kdMaterialWorkshopName,\n" +
            "       m.kd_material_workshop_number    AS kdMaterialWorkshopNumber,\n" +
            "       m.kd_material_use_org_id         AS kdMaterialUseOrgId,\n" +
            "       m.kd_material_use_org_number     AS kdMaterialUseOrgNumber,\n" +
            "       m.kd_material_use_org_name       AS kdMaterialUseOrgName,\n" +
            "       m.kd_material_stretch_weight     AS kdMaterialStretchWeight,\n" +
            "       m.kd_material_each_piece_num     AS kdMaterialEachPieceNum,\n" +
            "       a.nc_material_id                 AS ncMaterialId,\n" +
            "       a.nc_material_category           AS ncMaterialCategory,\n" +
            "       a.nc_material_main_category      AS ncMaterialMainCategory,\n" +
            "       a.nc_material_classification     AS ncMaterialClassification,\n" +
            "       a.nc_material_quality_num        AS ncMaterialQualityNum,\n" +
            "       a.nc_material_quality_unit       AS ncMaterialQualityUnit,\n" +
            "       a.nc_material_status             AS ncMaterialStatus \n" +
            " from t_sync_material a \n" +
            " left join mid_material m on m.kd_material_id = a.kd_material_id \n" +
            " where 1=1 \n" +
            "and (a.material_code like %:#{#tSyncMaterialTo.materialCode}% or :#{#tSyncMaterialTo.materialCode} is null or :#{#tSyncMaterialTo.materialCode} ='') \n" +
            "and (material_name like %:#{#tSyncMaterialTo.materialName}% or :#{#tSyncMaterialTo.materialName} is null or :#{#tSyncMaterialTo.materialName} = '') \n" +
            "and (a.nc_material_category like %:#{#tSyncMaterialTo.ncMaterialCategory}% or :#{#tSyncMaterialTo.ncMaterialCategory} is null or :#{#tSyncMaterialTo.ncMaterialCategory} ='') \n" +
            " and (material_status =:#{#tSyncMaterialTo.materialStatus} or :#{#tSyncMaterialTo.materialStatus} is null or :#{#tSyncMaterialTo.materialStatus} ='') \n" +
            " order by created_time desc,id desc " +
            " limit :size offset :current ",nativeQuery = true)
    List<Map> findAllJoinMid(@Param("tSyncMaterialTo") TSyncMaterial tSyncMaterialTo,
                         @Param("current") Integer current,
                         @Param("size") Integer size);

    @Query(value = "select count(1) from t_sync_material a \n" +
            " where 1=1 \n" +
            "and (a.material_code like %:#{#tSyncMaterialTo.materialCode}% or :#{#tSyncMaterialTo.materialCode} is null or :#{#tSyncMaterialTo.materialCode} ='') \n" +
            "and (material_name like %:#{#tSyncMaterialTo.materialName}% or :#{#tSyncMaterialTo.materialName} is null or :#{#tSyncMaterialTo.materialName} = '') \n" +
            "and (a.nc_material_category like %:#{#tSyncMaterialTo.ncMaterialCategory}% or :#{#tSyncMaterialTo.ncMaterialCategory} is null or :#{#tSyncMaterialTo.ncMaterialCategory} ='') \n" +
            " and (material_status =:#{#tSyncMaterialTo.materialStatus} or :#{#tSyncMaterialTo.materialStatus} is null or :#{#tSyncMaterialTo.materialStatus} ='') \n", nativeQuery = true)
    int finAllAndTotal(@Param("tSyncMaterialTo") TSyncMaterial tSyncMaterialTo);

}
