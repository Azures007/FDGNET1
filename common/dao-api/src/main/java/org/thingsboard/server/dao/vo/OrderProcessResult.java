package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/25 18:51
 * @Description:
 */
@Data
@ApiModel("工序盘点")
public class OrderProcessResult {
    @ApiModelProperty(value = "订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("明细-批号")
    private String bodyLot;

    @ApiModelProperty("明细-生产车间")
    private String bodyPrdDept;

    @ApiModelProperty("明细-物料ID")
    private Integer bodyMaterialId;

    @ApiModelProperty("明细-物料编码")
    private String bodyMaterialNumber;

    @ApiModelProperty("明细-物料名称")
    private String bodyMaterialName;

    @ApiModelProperty("明细-规格型号")
    private String bodyMaterialSpecification;

    @ApiModelProperty("明细-计划生产数量")
    private Float bodyPlanPrdQty;

    @ApiModelProperty("原辅料明细")
    private List<OrderPPbomResult> pPbomResultList;


}