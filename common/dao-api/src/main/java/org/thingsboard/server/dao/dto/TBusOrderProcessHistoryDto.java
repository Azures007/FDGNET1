package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel("订单报工/盘点历史列表条件模型")
public class TBusOrderProcessHistoryDto {
    @ApiModelProperty(value = "订单编号")
    private String orderNo;
    @ApiModelProperty(value = "业务类型")
    private String busType;
    @ApiModelProperty("类目类型（类目编码）")
    @Column(name = "record_type")
    private String recordType;
    @ApiModelProperty("工序ID")
    private Integer processId;
    @ApiModelProperty("物料编码（模糊查询）")
    private String materialNumber;
}
