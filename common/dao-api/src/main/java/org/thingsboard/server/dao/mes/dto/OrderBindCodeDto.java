package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.dto
 * @date 2022/7/13 11:36
 * @Description:
 */
@Data
@ApiModel("订单报工结果模型")
public class OrderBindCodeDto {

    @ApiModelProperty("订单工序执行表ID")
    private Integer orderProcessId;
    @ApiModelProperty("本工序订单号")
    private String orderNo;
    @ApiModelProperty("本工序id")
    private Integer processId;
    @ApiModelProperty("绑定料框编码")
    private String bindCodeNumber;
    @ApiModelProperty("工艺路线id")
    private Integer craftId;
    @ApiModelProperty("报工扫码绑定的扫码绑定信息ID")
    private Integer prevBindCodeId;

    @ApiModelProperty("订单报工结果表ID")
    private Integer orderProcessRecordId;
    @ApiModelProperty("订单报工历史记录表ID")
    private Integer orderProcessHistoryId;

    @ApiModelProperty("报工提交模型")
    private OrderRecordSaveDto orderRecordSaveDto;

}
