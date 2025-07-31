package org.thingsboard.server.dao.sql.nc_inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TBusOrderHead;
import org.thingsboard.server.common.data.nc_inventory.NcInventory;
import org.thingsboard.server.dao.dto.PdMaterialsDto;

import java.util.List;
import java.util.Optional;

public interface NcInventoryRepository extends JpaRepository<NcInventory, String>, JpaSpecificationExecutor<NcInventory> {
    Page<NcInventory> findByWarehouseNameContainingAndMaterialNameContainingAndSpecContaining(
            String warehouseName, String materialName, String spec, Pageable pageable);

    @Query(value = "select * from t_bus_inventory tbi \n" +
            "where status ='生效' \n" +
            "and warehouse_code=:#{#pdMaterialsDto.warehouseCode} " +
            "and (:#{#pdMaterialsDto.material} ='' or material_name like %:#{#pdMaterialsDto.material}% or material_code like %:#{#pdMaterialsDto.material}%)",nativeQuery = true)
    List<NcInventory> pdMaterials(@Param("pdMaterialsDto") PdMaterialsDto pdMaterialsDto);

    List<NcInventory> findByWarehouseIdAndMaterialCodeAndStatusOrderByLotAsc(String warehouseId, String materialCode, String status);
}
