package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单进度完成情况
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("订单进度完成情况")
public class OrderProgress {

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("计划量")
    private BigDecimal planQuantity;

    @ApiModelProperty("完成量")
    private BigDecimal completedQuantity;

    @ApiModelProperty("进度(%)")
    private BigDecimal progressPercentage;
}
