package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单用料清单列表条件模型")
public class TBusOrderPPBomDto {
    @ApiModelProperty(value = "订单id")
    private Integer orderId;
    @ApiModelProperty(value = "订单明细id")
    private Integer orderBodyId;
    @ApiModelProperty(value = "订单用料清单id")
    private Integer orderPPBomId;
    @ApiModelProperty("物料编码（模糊查询）")
    private String materialNumber;
}
