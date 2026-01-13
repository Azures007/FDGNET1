package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 原料投入报表查询条件
 */
@Data
@ApiModel("原料投入报表查询条件")
public class RawMaterialInputQueryDto {

    @ApiModelProperty("下单日期-开始时间")
    private Date orderDateStart;

    @ApiModelProperty("下单日期-结束时间")
    private Date orderDateEnd;

    @ApiModelProperty("产品名称（支持模糊查询）")
    private String productName = "";

    @ApiModelProperty("订单号（支持模糊查询）")
    private String orderNo = "";

}

