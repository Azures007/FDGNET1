package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class NcInventorySyncRequest {

    @ApiModelProperty("仓库编码")
    private String warehouseCode;

    @ApiModelProperty("仓库ID")
    private String warehouseId;

    @ApiModelProperty("仓库名称")
    private String warehouseName;

    @ApiModelProperty("产品明细列表")
    private List<NcInventoryProductDto> product;
}

