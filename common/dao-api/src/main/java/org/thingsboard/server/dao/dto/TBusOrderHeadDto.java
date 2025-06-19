package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("订单列表条件模型")
public class TBusOrderHeadDto {
    
    @ApiModelProperty(value = "订单id")
    private Integer orderId;
    @ApiModelProperty("订单号")
    private String orderNo;
    @ApiModelProperty("订单状态")
    private String orderStatus;

    @ApiModelProperty("单据日期、下单日期")
    private Date billDate;
    @ApiModelProperty("单据编号（模糊查询）")
    private String billNo;

    @ApiModelProperty("物料编码（模糊查询）")
    private String materialNumber;

    @ApiModelProperty("当前工序")
    private String currentProcess;

    @ApiModelProperty("完工日期，订单明细-计划完工时间")
    private Date planStartDate;
    @ApiModelProperty("处理班别")
    private Integer classId;
}
