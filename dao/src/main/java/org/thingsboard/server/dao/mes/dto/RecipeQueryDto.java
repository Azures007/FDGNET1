package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 配方查询DTO
 * @author: system
 * @date: 2025-01-10
 * @description: 配方查询条件DTO
 */
@Data
@ApiModel("配方查询条件")
public class RecipeQueryDto {

    @ApiModelProperty("配方名称")
    private String recipeName;

    @ApiModelProperty("配方编号")
    private String recipeCode;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private String status;

    @ApiModelProperty("基地ID")
    private String pkOrg;

    @ApiModelProperty("创建人")
    private String creator;
}
