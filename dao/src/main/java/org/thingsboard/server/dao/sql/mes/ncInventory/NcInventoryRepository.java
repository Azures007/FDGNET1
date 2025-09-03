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
import java.util.Map;

public interface NcInventoryRepository extends JpaRepository<NcInventory, String>, JpaSpecificationExecutor<NcInventory> {
    Page<NcInventory> findByWarehouseNameContainingAndMaterialNameContainingAndSpecContaining(
            String warehouseName, String materialName, String spec, Pageable pageable);

    @Query(value = "select a.* " +
            ",CASE WHEN cou>0  THEN 1 ELSE 0 END AS by_pd " +
            "from t_bus_inventory a \n" +
            "left join (select material_number ,count(material_number) cou from t_sys_pd_record " +
            "where pd_time_str='2025-09-03' and by_deleted='0' group by material_number) b " +
            "on a.material_code=b.material_number " +
            "where a.status ='生效' \n" +
            "and a.warehouse_id=:#{#pdMaterialsDto.warehouseCode} " +
            "and (a.material_type=:#{#pdMaterialsDto.materialType} or :#{#pdMaterialsDto.materialType}='') " +
            "and (a.material_code=:#{#pdMaterialsDto.materialNumber} or :#{#pdMaterialsDto.materialNumber}='') " +
            "and (:#{#pdMaterialsDto.material} ='' or a.material_name like %:#{#pdMaterialsDto.material}% or a.material_code like %:#{#pdMaterialsDto.material}%)",nativeQuery = true)
    List<Map> pdMaterials(@Param("pdMaterialsDto") PdMaterialsDto pdMaterialsDto);

    List<NcInventory> findByWarehouseIdAndMaterialCodeAndStatusOrderByLotAsc(String warehouseId, String materialCode, String status);
}
