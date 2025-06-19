package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单接单开工提交")
public class OrderStartOrderSaveDto {

    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("工艺路线id")
    private Integer craftId;

    @ApiModelProperty("备注（工艺路线）")
    private String craftDesc;

    @ApiModelProperty("订单明细的物料编码")
    private String bodyMaterialNumber;

}
