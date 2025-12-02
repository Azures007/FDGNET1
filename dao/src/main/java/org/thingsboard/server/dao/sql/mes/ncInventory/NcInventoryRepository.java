package org.thingsboard.server.dao.sql.mes.ncInventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.dao.mes.dto.PdMaterialsDto;

import java.util.List;
import java.util.Map;

public interface NcInventoryRepository extends JpaRepository<NcInventory, String>, JpaSpecificationExecutor<NcInventory> {
    Page<NcInventory> findByWarehouseNameContainingAndMaterialNameContainingAndSpecContaining(
            String warehouseName, String materialName, String spec, Pageable pageable);

    @Query(value = "select a.* " +
            ",CASE WHEN cou>0  THEN 1 ELSE 0 END AS by_pd " +
            "from t_bus_inventory a \n" +
            "left join (select material_number ,count(material_number) cou from t_sys_pd_record " +
            "where pd_time_str=:#{#pdMaterialsDto.pdTimeStr} and by_deleted='0' and (nc_vwkname=:#{#pdMaterialsDto.vwkname} or (:#{#pdMaterialsDto.vwkname} is null and nc_vwkname is null)) group by material_number) b " +
            "on a.material_code=b.material_number " +
            "where a.status ='生效' \n" +
            "and (a.warehouse_id=:#{#pdMaterialsDto.warehouseCode} or a.warehouse_code=:#{#pdMaterialsDto.warehouseCode})" +
            "and (a.material_type=:#{#pdMaterialsDto.materialType} or :#{#pdMaterialsDto.materialType}='') " +
            "and (a.material_type_pd=:#{#pdMaterialsDto.materialTypePd} or :#{#pdMaterialsDto.materialTypePd}='') " +
            "and (a.material_code=:#{#pdMaterialsDto.materialNumber} or :#{#pdMaterialsDto.materialNumber}='') " +
            "and (:#{#pdMaterialsDto.material} ='' or a.material_name like %:#{#pdMaterialsDto.material}% or a.material_code like %:#{#pdMaterialsDto.material}%)",nativeQuery = true)
    List<Map> pdMaterials(@Param("pdMaterialsDto") PdMaterialsDto pdMaterialsDto);

    @Query(value = "select * from t_bus_inventory where warehouse_id=?1 and material_code=?2 and status=?3",nativeQuery = true)
    List<NcInventory> findByWarehouseIdAndMaterialCodeAndStatusOrderByLotAsc(String warehouseId, String materialCode, String status);

    @Query(value = "SELECT DISTINCT material_type_pd FROM t_bus_inventory WHERE material_type_pd IS NOT NULL AND material_type_pd != ''", nativeQuery = true)
    List<String> findDistinctMaterialTypePd();

    @Modifying
    @Query(value = "delete from t_bus_inventory " +
            "where ((:warehouseId is not null and :warehouseId <> '' and warehouse_id = :warehouseId) " +
            "or (:warehouseCode is not null and :warehouseCode <> '' and warehouse_code = :warehouseCode))", nativeQuery = true)
    void deleteByWarehouse(@Param("warehouseId") String warehouseId, @Param("warehouseCode") String warehouseCode);

}