package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/4/18 17:46
 * @Description:
 */
@Data
@ApiModel("设备列表条件模型")
public class TSysDeviceDto {
    @ApiModelProperty("设备名称（模糊查询）")
    private String deviceName;
    @ApiModelProperty("设备编号")
    private String deviceNumber;
    @ApiModelProperty("状态 0：启动 1：禁用")
    private String enabled;
}
