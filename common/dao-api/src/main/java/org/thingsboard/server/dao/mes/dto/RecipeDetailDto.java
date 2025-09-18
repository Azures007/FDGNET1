package org.thingsboard.server.dao.mes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeInput;
import org.thingsboard.server.common.data.mes.sys.TSysRecipeProductBinding;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("配方详情")
public class RecipeDetailDto {

    @ApiModelProperty("配方ID")
    private Integer recipeId;

    @ApiModelProperty("配方名称")
    private String recipeName;

    @ApiModelProperty("配方编号")
    private String recipeCode;

    @ApiModelProperty("基地名称")
    private String orgName;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private String status;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("配方说明")
    private String recipeDescription;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUser;

    @ApiModelProperty("基地ID")
    private String pkOrg;

    @ApiModelProperty("投入设置明细")
    private List<TSysRecipeInput> recipeInputs;

    @ApiModelProperty("产品绑定明细")
    private List<TSysRecipeProductBinding> productBindings;
}


