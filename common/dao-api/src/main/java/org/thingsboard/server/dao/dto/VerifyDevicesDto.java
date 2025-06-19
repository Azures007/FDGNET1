package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiOperation("指纹设备及人员信息")
public class VerifyDevicesDto {

    @ApiModelProperty("人员id")
    private Integer personnelId;

    @ApiModelProperty("设备key")
    private String devicesKey;
}
