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

    @ApiModelProperty("投入设置列表")
    private List<TSysRecipeInput> recipeInputs;
}
