package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("获取当前积累锅数报工详情入参对象")
public class GetPotAllRecordDetailsDto {
    @ApiModelProperty("工序执行表id")
    private Integer orderProcessId;
    @ApiModelProperty("订单id")
    private Integer orderId;
//    @ApiModelProperty("操作员集合")
//    private List<Integer> devicePersonIds;

}
