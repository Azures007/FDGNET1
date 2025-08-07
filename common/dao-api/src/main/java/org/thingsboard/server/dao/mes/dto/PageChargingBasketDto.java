package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel("料筐列表dto")
public class PageChargingBasketDto {
    @ApiModelProperty("创建时间（前）")
    private String createdTimeFront;
    @ApiModelProperty("创建时间（后）")
    private String createdTimeLater;
    @ApiModelProperty("料筐编码（支持模糊查询）")
    private String code;
    @ApiModelProperty("状态0：作废 1：启用")
    private String chargingBasketStatus;
    @ApiModelProperty("排序条件（created_time:创建时间排序，code：料筐编码排序）")
    private String orderKey;
    @ApiModelProperty("排序值：desc:倒序，asc：升序")
    private String orderVal;
}
