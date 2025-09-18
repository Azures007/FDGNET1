package org.thingsboard.server.dao.sql.mes.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.sys.TSysRecipe;

import java.util.List;
import java.util.Optional;

/**
 * 配方数据访问接口
 * @author: system
 * @date: 2025-01-10
 * @description: 配方Repository接口
 */
public interface TSysRecipeRepository extends JpaRepository<TSysRecipe, Integer> {

    /**
     * 根据配方编号查询配方
     * @param recipeCode 配方编号
     * @return 配方信息
     */
    Optional<TSysRecipe> findByRecipeCode(String recipeCode);

    /**
     * 根据配方名称查询配方列表
     * @param recipeName 配方名称
     * @return 配方列表
     */
    List<TSysRecipe> findByRecipeNameContaining(String recipeName);

    /**
     * 根据状态查询配方列表
     * @param status 状态
     * @return 配方列表
     */
    List<TSysRecipe> findByStatus(String status);

    /**
     * 根据基地ID查询配方列表
     * @param pkOrg 基地ID
     * @return 配方列表
     */
    List<TSysRecipe> findByPkOrg(String pkOrg);

    /**
     * 分页查询配方列表（支持多条件查询）
     * @param recipeName 配方名称（模糊查询）
     * @param recipeCode 配方编号（模糊查询）
     * @param status 状态
     * @param pkOrg 基地ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query(value = "SELECT r FROM TSysRecipe r WHERE " +
            "(:recipeName IS NULL OR :recipeName='' OR r.recipeName LIKE %:recipeName%) AND " +
            "(:recipeCode IS NULL OR :recipeCode='' OR r.recipeCode LIKE %:recipeCode%) AND " +
            "(:status IS NULL OR :status='' OR r.status = :status) AND " +
            "(:pkOrg IS NULL OR :pkOrg='' OR r.pkOrg = :pkOrg)")
    Page<TSysRecipe> findRecipesWithConditions(@Param("recipeName") String recipeName,
                                               @Param("recipeCode") String recipeCode,
                                               @Param("status") String status,
                                               @Param("pkOrg") String pkOrg,
                                               Pageable pageable);

    /**
     * 检查配方编号是否存在（排除指定ID）
     * @param recipeCode 配方编号
     * @param recipeId 配方ID（排除此ID）
     * @return 是否存在
     */
    @Query("SELECT COUNT(r) > 0 FROM TSysRecipe r WHERE r.recipeCode = :recipeCode AND r.recipeId != :recipeId")
    boolean existsByRecipeCodeAndRecipeIdNot(@Param("recipeCode") String recipeCode, @Param("recipeId") Integer recipeId);

    /**
     * 根据创建人查询配方列表
     * @param creator 创建人
     * @return 配方列表
     */
    List<TSysRecipe> findByCreator(String creator);

    /**
     * 统计指定状态的配方数量
     * @param status 状态
     * @return 数量
     */
    long countByStatus(String status);

    /**
     * 根据基地ID统计配方数量
     * @param pkOrg 基地ID
     * @return 数量
     */
    long countByPkOrg(String pkOrg);
}
