package org.thingsboard.server.dao.sql.mes.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeInput;

import java.util.List;

/**
 * 配方投入设置数据访问接口
 * @author: system
 * @date: 2025-01-10
 * @description: 配方投入设置Repository接口
 */
public interface TSysRecipeInputRepository extends JpaRepository<TSysRecipeInput, Integer> {

    /**
     * 根据配方ID查询投入设置列表
     * @param recipeId 配方ID
     * @return 投入设置列表
     */
    List<TSysRecipeInput> findByRecipeId(Integer recipeId);

    /**
     * 根据配方ID删除投入设置
     * @param recipeId 配方ID
     */
    @Modifying
    @Transactional
    void deleteByRecipeId(Integer recipeId);

    /**
     * 根据物料编码查询投入设置列表
     * @param materialCode 物料编码
     * @return 投入设置列表
     */
    List<TSysRecipeInput> findByMaterialCode(String materialCode);

    /**
     * 根据工序名称查询投入设置列表
     * @param processName 工序名称
     * @return 投入设置列表
     */
    List<TSysRecipeInput> findByProcessName(String processName);

    /**
     * 根据配方ID和物料编码查询投入设置
     * @param recipeId 配方ID
     * @param materialCode 物料编码
     * @return 投入设置
     */
    @Query("SELECT r FROM TSysRecipeInput r WHERE r.recipeId = :recipeId AND r.materialCode = :materialCode")
    TSysRecipeInput findByRecipeIdAndMaterialCode(@Param("recipeId") Integer recipeId, @Param("materialCode") String materialCode);

    /**
     * 检查配方中是否存在指定物料
     * @param recipeId 配方ID
     * @param materialCode 物料编码
     * @return 是否存在
     */
    @Query("SELECT COUNT(r) > 0 FROM TSysRecipeInput r WHERE r.recipeId = :recipeId AND r.materialCode = :materialCode")
    boolean existsByRecipeIdAndMaterialCode(@Param("recipeId") Integer recipeId, @Param("materialCode") String materialCode);

    /**
     * 根据配方ID统计投入设置数量
     * @param recipeId 配方ID
     * @return 数量
     */
    long countByRecipeId(Integer recipeId);
}
