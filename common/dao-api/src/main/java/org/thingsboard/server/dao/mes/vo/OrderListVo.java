package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel("后台订单列表-废弃")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListVo {

    @ApiModelProperty("订单id")
    private Integer order_id;

    @ApiModelProperty("单据编号")
    private String bill_no;

    @ApiModelProperty("明细-批号")
    private String body_lot;

    @ApiModelProperty("单据日期(下单时间)")
    private String bill_date;

    @ApiModelProperty("订单状态：0=未开工、1=已开工、2=暂停、3=已完工")
    private String order_status;

    @ApiModelProperty("明细-生产车间")
    private String body_prd_dept;

    @ApiModelProperty("预期产量（计划产量）")
    private BigDecimal bill_plan_qty;

    @ApiModelProperty("明细-计划完工时间")
    private String body_plan_finish_date;

    @ApiModelProperty("明细-规格型号（产品规格）")
    private String body_material_specification;

    @ApiModelProperty("单据类型（订单类型）")
    private String bill_type;

    @ApiModelProperty("当前工序")
    private String current_process;

    @ApiModelProperty("处理班别")
    private String class_id;



}
