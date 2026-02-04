package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@ApiModel("烤炉折线图入参")
public class ListSellpOvenDto {

    @ApiModelProperty("运行日期（格式：2025-12-01）")
    private String byDate;


    @ApiModelProperty("设备编码（全部为空值）")
    private String deviceCode;

    @ApiModelProperty("设备名称（全部为空值）")
    private String deviceName;

    @ApiModelProperty("查询索引")
    private String bySelect;

}
