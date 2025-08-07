package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("机台vo")
@Data
@NoArgsConstructor
public class DeviceVo {
    @ApiModelProperty("设备id")
    private Integer deviceId;
    @ApiModelProperty("设备名称")
    private String deviceName;

}
