package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单班别变更提交")
public class OrderChangeClassSaveDto {

    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("班别id")
    private Integer classId;

}
