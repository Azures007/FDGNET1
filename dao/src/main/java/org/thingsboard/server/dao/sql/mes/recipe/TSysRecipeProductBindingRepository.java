package org.thingsboard.server.dao.sql.mes.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeProductBinding;

import java.util.List;

/**
 * 配方产品绑定数据访问接口
 * @author: system
 * @date: 2025-01-10
 * @description: 配方产品绑定Repository接口
 */
public interface TSysRecipeProductBindingRepository extends JpaRepository<TSysRecipeProductBinding, Integer> {

    /**
     * 根据配方ID查询产品绑定列表
     * @param recipeId 配方ID
     * @return 产品绑定列表
     */
    List<TSysRecipeProductBinding> findByRecipeId(Integer recipeId);

    /**
     * 根据配方ID删除产品绑定
     * @param recipeId 配方ID
     */
    @Modifying
    @Transactional
    void deleteByRecipeId(Integer recipeId);

    /**
     * 根据产品编码查询产品绑定列表
     * @param productCode 产品编码
     * @return 产品绑定列表
     */
    List<TSysRecipeProductBinding> findByProductCode(String productCode);

    /**
     * 根据产品名称查询产品绑定列表
     * @param productName 产品名称
     * @return 产品绑定列表
     */
    List<TSysRecipeProductBinding> findByProductNameContaining(String productName);

    /**
     * 根据配方ID和产品编码查询产品绑定
     * @param recipeId 配方ID
     * @param productCode 产品编码
     * @return 产品绑定
     */
    @Query("SELECT r FROM TSysRecipeProductBinding r WHERE r.recipeId = :recipeId AND r.productCode = :productCode")
    TSysRecipeProductBinding findByRecipeIdAndProductCode(@Param("recipeId") Integer recipeId, @Param("productCode") String productCode);

    /**
     * 检查配方中是否存在指定产品
     * @param recipeId 配方ID
     * @param productCode 产品编码
     * @return 是否存在
     */
    @Query("SELECT COUNT(r) > 0 FROM TSysRecipeProductBinding r WHERE r.recipeId = :recipeId AND r.productCode = :productCode")
    boolean existsByRecipeIdAndProductCode(@Param("recipeId") Integer recipeId, @Param("productCode") String productCode);

    /**
     * 根据配方ID统计产品绑定数量
     * @param recipeId 配方ID
     * @return 数量
     */
    long countByRecipeId(Integer recipeId);

    /**
     * 根据产品编码统计绑定数量
     * @param productCode 产品编码
     * @return 数量
     */
    long countByProductCode(String productCode);
}
