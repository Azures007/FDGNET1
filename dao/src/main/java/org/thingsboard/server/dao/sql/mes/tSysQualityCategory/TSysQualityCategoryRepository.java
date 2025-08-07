package org.thingsboard.server.dao.sql.mes.tSysQualityCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCategory;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/6/27 18:05:17
 */
public interface TSysQualityCategoryRepository extends JpaRepository<TSysQualityCategory,Integer> {

    @Query(value = "SELECT t.id, t.inspection_item, t.key_process, t.monitoring_method, t.material_id, t.product_name, t.standard, t.remarks, t.is_enabled, \n" +
            "t.create_time, t.create_user, t.update_time, t.update_user, t.key_process_name, t.monitoring_method_name \n" +
            "FROM t_sys_quality_category t \n" +
            "where 1=1 " +
            "and (t.is_enabled like %:#{#tSysQualityCategory.isEnabled}% or :#{#tSysQualityCategory.isEnabled} is null or :#{#tSysQualityCategory.isEnabled} ='')\n" +
            "and (t.inspection_item like %:#{#tSysQualityCategory.inspectionItem}% or :#{#tSysQualityCategory.inspectionItem} is null or :#{#tSysQualityCategory.inspectionItem} ='')\n" +
            "and (t.key_process_name like %:#{#tSysQualityCategory.keyProcessName}% or :#{#tSysQualityCategory.keyProcessName} is null or :#{#tSysQualityCategory.keyProcessName} ='')\n" +
            "and (t.product_name=:#{#tSysQualityCategory.productName} or :#{#tSysQualityCategory.productName} is null or :#{#tSysQualityCategory.productName} ='')",nativeQuery = true)
    Page<TSysQualityCategory> findAllBy(@Param("tSysQualityCategory") TSysQualityCategory tSysQualityCategory, Pageable pageable);

    List<TSysQualityCategory> findByInspectionItem(String inspectionItem);




}
