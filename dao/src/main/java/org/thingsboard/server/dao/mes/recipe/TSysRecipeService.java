package org.thingsboard.server.dao.mes.recipe;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysRecipe;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeInput;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeProductBinding;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;
import org.thingsboard.server.dao.mes.dto.RecipeQueryDto;
import org.thingsboard.server.dao.mes.dto.RecipeSaveDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

import java.util.List;

/**
 * 配方服务接口
 * @author: system
 * @date: 2025-01-10
 * @description: 配方管理服务接口
 */
public interface TSysRecipeService {

    /**
     * 分页查询配方列表
     * @param currentUser 当前用户
     * @param current 当前页
     * @param size 每页大小
     * @param queryDto 查询条件
     * @return 分页结果
     */
    Page<TSysRecipe> getRecipeList(String currentUser, Integer current, Integer size, RecipeQueryDto queryDto);

    /**
     * 根据ID查询配方详情
     * @param recipeId 配方ID
     * @return 配方详情
     */
    TSysRecipe getRecipeById(Integer recipeId);

    /**
     * 获取配方详情（用于前端编辑使用，返回RecipeSaveDto：主表+投入设置）
     */
    RecipeSaveDto getRecipeSaveDto(Integer recipeId);

    /**
     * 保存配方（新增或更新）
     *
     * @param saveDto
     * @param currentUser 当前用户
     * @return 保存后的配方
     */
    RecipeSaveDto saveRecipe(RecipeSaveDto saveDto, String currentUser);

    /**
     * 删除配方
     * @param recipeId 配方ID
     */
    void deleteRecipe(Integer recipeId);

    /**
     * 更新配方状态
     * @param recipeId 配方ID
     * @param status 状态
     * @param currentUser 当前用户
     */
    void updateRecipeStatus(Integer recipeId, String status, String currentUser);

    /**
     * 检查配方编号是否存在
     * @param recipeCode 配方编号
     * @param recipeId 配方ID（更新时排除）
     * @return 是否存在
     */
    boolean isRecipeCodeExists(String recipeCode, Integer recipeId);

    /**
     * 根据配方ID查询投入设置列表
     * @param recipeId 配方ID
     * @return 投入设置列表
     */
    List<TSysRecipeInput> getRecipeInputsByRecipeId(Integer recipeId);

    /**
     * 保存配方投入设置
     * @param recipeId 配方ID
     * @param recipeInputs 投入设置列表
     */
    void saveRecipeInputs(Integer recipeId, List<TSysRecipeInput> recipeInputs);

    /**
     * 根据配方ID查询产品绑定列表
     * @param recipeId 配方ID
     * @return 产品绑定列表
     */
    List<TSysRecipeProductBinding> getProductBindingsByRecipeId(Integer recipeId);

    /**
     * 保存配方产品绑定
     * @param recipeId 配方ID
     * @param productBindings 产品绑定列表
     */
    void saveProductBindings(Integer recipeId, List<TSysRecipeProductBinding> productBindings);

    /**
     * 获取可用产品列表（分页查询，过滤已绑定的产品）
     * @param currentUser 当前用户
     * @param recipeId 配方ID（用于过滤已绑定的产品）
     * @param productName 产品名称（模糊查询）
     * @param productCode 产品编码（模糊查询）
     * @param current 当前页
     * @param size 每页大小
     * @return 可用产品分页列表
     */
    PageVo<TSyncMaterial> getAvailableProducts(String currentUser, Integer recipeId, String productName, String productCode, Integer current, Integer size);

    /**
     * 移除产品绑定
     * @param bindingId 绑定ID
     */
    void removeProductBinding(Integer bindingId);

    /**
     * 根据基地ID获取配方统计信息
     * @param pkOrg 基地ID
     * @return 统计信息
     */
    long getRecipeCountByPkOrg(String pkOrg);

    /**
     * 根据状态获取配方统计信息
     * @param status 状态
     * @return 统计信息
     */
    long getRecipeCountByStatus(String status);

    
    /**
     * 复制配方及其投入设置
     * @param recipeId 要复制的配方ID
     * @param creator 创建人
     * @return 复制后的新配方
     */
    TSysRecipe copyRecipe(Integer recipeId, String creator);
}
