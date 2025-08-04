package org.thingsboard.server.dao.sql.tSysQualityCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysQualityCategory;
import org.thingsboard.server.common.data.TSysQualityCategoryConfig;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/6/29 17:59:04
 */
public interface TSysQualityCategoryConfigRepository extends JpaRepository<TSysQualityCategoryConfig,Integer> {


    void deleteByCategoryId(Integer categoryId);

    List<TSysQualityCategoryConfig> findByCategoryIdOrderByIdAsc(Integer categoryId);

    List<TSysQualityCategoryConfig> findByCategoryIdInOrderByIdAsc(List<Integer> categoryId);
}
