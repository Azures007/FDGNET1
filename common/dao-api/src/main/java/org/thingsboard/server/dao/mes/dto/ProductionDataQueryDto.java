package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 投入产出比报表查询DTO
 */
@Data
@ApiModel(value = "ProductionDataQueryDto", description = "投入产出比报表查询条件")
public class ProductionDataQueryDto {

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}