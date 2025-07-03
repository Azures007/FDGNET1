package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务列表过滤条件")
public class OrderTaskSelectDto {
    @ApiModelProperty("工序条件过滤")
    private String processNumber = "";

    @ApiModelProperty("批次过滤条件")
    private String bodyLot = "";

    @ApiModelProperty("多字段模糊查询")
    private String selectOrField;
}
