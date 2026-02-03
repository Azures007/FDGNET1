package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@ApiModel("设备数采报表入场")
public class IotDeviceDto {

    @ApiModelProperty("日期（前，格式2026-02-03）")
    private String startDate;
    @ApiModelProperty("日期（后，格式2026-02-03）")
    private String endDate;
    private Long startDateTimes;
    private Long endDateTimes;
    @ApiModelProperty("设备编码，全部则为空")
    private String deviceCode;
}
