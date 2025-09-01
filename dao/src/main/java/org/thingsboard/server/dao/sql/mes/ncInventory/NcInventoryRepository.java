package org.thingsboard.server.dao.sql.mes.ncInventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.dao.mes.dto.PdMaterialsDto;

import java.util.List;

public interface NcInventoryRepository extends JpaRepository<NcInventory, String>, JpaSpecificationExecutor<NcInventory> {
    Page<NcInventory> findByWarehouseNameContainingAndMaterialNameContainingAndSpecContaining(
            String warehouseName, String materialName, String spec, Pageable pageable);

    @Query(value = "select * from t_bus_inventory tbi \n" +
            "where status ='生效' \n" +
            "and warehouse_id=:#{#pdMaterialsDto.warehouseCode} " +
            "and (material_type=:#{#pdMaterialsDto.materialType} or material_type=:#{#pdMaterialsDto.materialType}='') " +
            "and (:#{#pdMaterialsDto.material} ='' or material_name like %:#{#pdMaterialsDto.material}% or material_code like %:#{#pdMaterialsDto.material}%)",nativeQuery = true)
    List<NcInventory> pdMaterials(@Param("pdMaterialsDto") PdMaterialsDto pdMaterialsDto);

    List<NcInventory> findByWarehouseIdAndMaterialCodeAndStatusOrderByLotAsc(String warehouseId, String materialCode, String status);
}
