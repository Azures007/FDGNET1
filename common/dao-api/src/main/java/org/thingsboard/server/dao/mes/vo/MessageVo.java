package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@ApiModel("消息内容")
public class MessageVo {
    @ApiModelProperty("消息id")
    private Integer id;
    @ApiModelProperty("订单id")
    private Integer orderId;
    @ApiModelProperty("订单类型")
    private Integer orderType;
    @ApiModelProperty("订单号")
    private String orderNo;
    @ApiModelProperty("下单时间(转成 2022-12-05 12:12:12格式)")
    private String createdTime;
    @ApiModelProperty("产品规格")
    private String productStandard;
    @ApiModelProperty("预期产量")
    private String billPlanQty;
    @ApiModelProperty("移交信息")
    private String botMessage;
    @ApiModelProperty("消息类型 0：移交订单消息 1：订单变更消息")
    private String mesType;
    @ApiModelProperty("状态类型 0:移交 1：普通")
    private String statusType;
    @ApiModelProperty("是否已读 0:已读 1：未读")
    private String isRead;
    @ApiModelProperty("消息时间")
    private String mesTime;
}
