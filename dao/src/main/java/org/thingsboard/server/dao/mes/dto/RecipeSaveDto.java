package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysRecipe;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeInput;

import java.util.List;

/**
 * 配方保存DTO
 * @author: system
 * @date: 2025-01-10
 * @description: 配方保存DTO，包含配方信息和投入设置
 */
@Data
@ApiModel("配方保存DTO")
public class RecipeSaveDto {

    @ApiModelProperty("配方信息")
    private TSysRecipe recipe;

//    @ApiModelProperty("投入设置列表")
//    private List<TSysRecipeInput> recipeInputs;
    
    // 添加新的分组数据结构
    @ApiModelProperty("按工序分组的投入设置列表")
    private List<RecipeInputGroup> groupedInputs;
    
    /**
     * 投入设置分组类
     */
    @Data
    @ApiModel("配方投入设置分组")
    public static class RecipeInputGroup {
        @ApiModelProperty("半成品名称")
        private String semiFinishedProductName;
        
        @ApiModelProperty("半成品编码")
        private String semiFinishedProductCode;
        
        @ApiModelProperty("工序编码")
        private String processNumber;
        
        @ApiModelProperty("工序名称")
        private String processName;
        
        @ApiModelProperty("该工序下的投入设置列表")
        private List<TSysRecipeInput> inputs;
    }
}