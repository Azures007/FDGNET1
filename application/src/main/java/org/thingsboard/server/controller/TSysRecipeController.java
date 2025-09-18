package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.mes.sys.TSysRecipe;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeInput;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeProductBinding;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.RecipeDetailDto;
import org.thingsboard.server.dao.mes.dto.RecipeQueryDto;
import org.thingsboard.server.dao.mes.dto.RecipeSaveDto;
import org.thingsboard.server.dao.mes.recipe.TSysRecipeService;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.List;

/**
 * 配方管理控制器
 * @author: system
 * @date: 2025-01-10
 * @description: 配方管理REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/recipe")
@Api(value = "配方管理接口", tags = "配方管理接口")
public class TSysRecipeController extends BaseController {

    @Autowired
    private TSysRecipeService recipeService;

    @ApiOperation("查询配方列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", defaultValue = "10")
    })
    @PostMapping("/list")
    public ResponseResult<PageVo<TSysRecipe>> getRecipeList(
            @RequestParam(value = "current", defaultValue = "0") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestBody RecipeQueryDto queryDto) throws ThingsboardException {
        
        SecurityUser currentUser = getCurrentUser();
        Page<TSysRecipe> recipePage = recipeService.getRecipeList(
                currentUser.getId().getId().toString(), current, size, queryDto);
        
        PageVo<TSysRecipe> pageVo = new PageVo<>(recipePage);
        return ResultUtil.success(pageVo);
    }

    @ApiOperation("保存配方（新增或更新）")
    @PostMapping("/save")
    public ResponseResult<TSysRecipe> saveRecipe(@RequestBody RecipeSaveDto saveDto) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        
        // 检查配方编号是否已存在（排除当前配方）
        if (recipeService.isRecipeCodeExists(saveDto.getRecipe().getRecipeCode(), saveDto.getRecipe().getRecipeId())) {
            return ResultUtil.error("配方编号已存在");
        }

        RecipeSaveDto savedRecipe = recipeService.saveRecipe(
                saveDto,
                currentUser.getName());
        
        return ResultUtil.success(savedRecipe);
    }

    @ApiOperation("删除配方")
    @GetMapping("/delete")
    public ResponseResult deleteRecipe(@RequestParam("recipeId") Integer recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResultUtil.success();
    }

    @ApiOperation("根据ID查询配方详情")
    @GetMapping("/detail")
    public ResponseResult<RecipeSaveDto> getRecipeById(@RequestParam("recipeId") Integer recipeId) {
        RecipeSaveDto detail = recipeService.getRecipeSaveDto(recipeId);
        if (detail == null) {
            return ResultUtil.error("配方不存在");
        }
        return ResultUtil.success(detail);
    }

    @ApiOperation("更新配方状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "recipeId", value = "配方ID", required = true),
            @ApiImplicitParam(name = "status", value = "状态：0-禁用，1-启用", required = true)
    })
    @GetMapping("/updateStatus")
    public ResponseResult updateRecipeStatus(
            @RequestParam("recipeId") Integer recipeId,
            @RequestParam("status") String status) throws ThingsboardException {
        
        SecurityUser currentUser = getCurrentUser();
        recipeService.updateRecipeStatus(recipeId, status, currentUser.getName());
        return ResultUtil.success();
    }

    //@ApiOperation("获取配方投入设置列表")
    /*@GetMapping("/inputs")
    public ResponseResult<List<TSysRecipeInput>> getRecipeInputs(@RequestParam("recipeId") Integer recipeId) {
        List<TSysRecipeInput> inputs = recipeService.getRecipeInputsByRecipeId(recipeId);
        return ResultUtil.success(inputs);
    }*/

    //@ApiOperation("保存配方投入设置")
    /*@PostMapping("/saveInputs")
    public ResponseResult saveRecipeInputs(
            @RequestParam("recipeId") Integer recipeId,
            @RequestBody List<TSysRecipeInput> recipeInputs) {
        
        recipeService.saveRecipeInputs(recipeId, recipeInputs);
        return ResultUtil.success();
    }*/

    @ApiOperation("获取配方产品绑定列表")
    @GetMapping("/productBindings")
    public ResponseResult<List<TSysRecipeProductBinding>> getProductBindings(@RequestParam("recipeId") Integer recipeId) {
        List<TSysRecipeProductBinding> bindings = recipeService.getProductBindingsByRecipeId(recipeId);
        return ResultUtil.success(bindings);
    }

    @ApiOperation("保存配方产品绑定")
    @PostMapping("/saveProductBindings")
    public ResponseResult saveProductBindings(
            @RequestParam("recipeId") Integer recipeId,
            @RequestBody List<TSysRecipeProductBinding> productBindings) {
        
        recipeService.saveProductBindings(recipeId, productBindings);
        return ResultUtil.success();
    }

    @ApiOperation("获取可用产品列表（分页查询，已过滤掉已绑定的产品）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "recipeId", value = "配方ID（用于过滤已绑定的产品）"),
            @ApiImplicitParam(name = "productName", value = "产品名称（模糊查询）"),
            @ApiImplicitParam(name = "productCode", value = "产品编码（模糊查询）"),
            @ApiImplicitParam(name = "current", value = "当前页（从0开始）"),
            @ApiImplicitParam(name = "size", value = "每页大小")
    })
    @GetMapping("/availableProducts")
    public ResponseResult<PageVo<TSyncMaterial>> getAvailableProducts(
            @RequestParam(value = "recipeId", required = false) Integer recipeId,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "current", defaultValue = "0") Integer current,
            @RequestParam(value = "size", defaultValue = "15") Integer size) throws ThingsboardException {
        
        SecurityUser currentUser = getCurrentUser();
        PageVo<TSyncMaterial> products = recipeService.getAvailableProducts(
                currentUser.getId().getId().toString(), recipeId, productName, productCode, current, size);
        
        return ResultUtil.success(products);
    }

    /*@ApiOperation("移除产品绑定")
    @GetMapping("/removeProductBinding")
    public ResponseResult removeProductBinding(@RequestParam("bindingId") Integer bindingId) {
        recipeService.removeProductBinding(bindingId);
        return ResultUtil.success();
    }*/

    //@ApiOperation("获取配方统计信息")
    /*@GetMapping("/statistics")
    public ResponseResult<Object> getRecipeStatistics(@RequestParam(value = "pkOrg", required = false) String pkOrg) {
        long totalCount = pkOrg != null ? 
                recipeService.getRecipeCountByPkOrg(pkOrg) : 
                recipeService.getRecipeCountByStatus("1");
        
        return ResultUtil.success(totalCount);
    }*/
}
