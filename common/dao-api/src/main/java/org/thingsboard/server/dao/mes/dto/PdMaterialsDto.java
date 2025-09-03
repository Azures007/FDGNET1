package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel("pd物料入参")
public class PdMaterialsDto {

    @ApiModelProperty("物料搜索值")
    private String material;

    @ApiModelProperty("仓库编码")
    private String warehouseCode;

    @ApiModelProperty("物料分类：自制品")
    private String materialType = "";

    @ApiModelProperty("复盘时传的物料编码")
    private String materialNumber="";


}
