package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("物料基础信息列表Dto")
@Data
public class ListMaterialDto {
    @ApiModelProperty("物料编码，支持模糊查询")
    private String materialCode;
    @ApiModelProperty("物料名称，支持模糊查询")
    private String materialName;
    @ApiModelProperty("状态 1：启用 0：禁用")
    private String materialStatus;
    @ApiModelProperty("创建时间排序 0:倒序 1：升序")
    private Integer orderCreatedTime;
    @ApiModelProperty("物料分类")
    private String ncMaterialCategory;
}
