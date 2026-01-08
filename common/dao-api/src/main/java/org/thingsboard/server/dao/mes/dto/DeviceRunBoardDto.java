package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ApiModel("设备运行报表入参")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRunBoardDto {

    @ApiModelProperty("日期（前）")
    private String byDateFront;
    @ApiModelProperty("日期（后）")
    private String byDateLater;

    @ApiModelProperty("设备类型（0：全部）")
    private String deviceType;

}
