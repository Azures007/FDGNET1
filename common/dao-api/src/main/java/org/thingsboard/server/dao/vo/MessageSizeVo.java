package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSysMessageOrder;

import java.util.List;
@Data
@ApiModel("未读消息模型")
public class MessageSizeVo {
    @ApiModelProperty("消息未读数量")
    private Integer megSize;
    @ApiModelProperty("消息列表")
    private List<TSysMessageOrder> tSysMessageOrders;
    @ApiModelProperty("用户信息")
    private GetOrderSizeVo getOrderSizeVo;
}
