package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NcInventoryProductDto {

    @ApiModelProperty("批次")
    private String lot;

    @ApiModelProperty("物料编码")
    private String materialCode;

    @ApiModelProperty("物料ID")
    private String materialId;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("物料分类")
    private String materialType;

    @ApiModelProperty("盘点物料分类")
    private String materialTypePd;

    @ApiModelProperty("库存数量")
    private Float qty;

    @ApiModelProperty("规格")
    private String spec;

    @ApiModelProperty("单位")
    private String unit;
}

