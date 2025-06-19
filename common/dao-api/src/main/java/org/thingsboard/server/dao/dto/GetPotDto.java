package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.LichengConstants;

import java.util.List;

@ApiModel("锅数展示dto")
@Data
public class GetPotDto {
    @ApiModelProperty("订单id")
    private Integer orderId;
    @ApiModelProperty("工序执行id")
    private Integer orderProcessId;
    @ApiModelProperty("操作员集合")
    private List<Integer> devicePersonIds;
    @ApiModelProperty("传工序编码")
    private String processType;

    @ApiModelProperty("0:普通产出统计 1：尾料产出统计")
    private String type;
}
