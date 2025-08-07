package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("组合投料入参")
public class JoinRecordDto {
    @ApiModelProperty("用料id")
   private  Integer orderPpbomId;
    @ApiModelProperty("订单id")
    private  Integer orderId;
    @ApiModelProperty("工序执行ID")
    private Integer orderProcessId;
    @ApiModelProperty("操作员id集合")
    private List<Integer> ids;
}
