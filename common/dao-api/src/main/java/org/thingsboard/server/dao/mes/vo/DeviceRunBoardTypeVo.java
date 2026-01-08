package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@ApiModel("设备运行看板类型对象")
public class DeviceRunBoardTypeVo {

    @ApiModelProperty("设备类型")
    private String deviceType;

    @ApiModelProperty("内包机列表集合")
    private List<InsourcingDeviceRunVo> insourcingDeviceRunVoList;

    @ApiModelProperty("烤炉列表集合")
    private List<OvenDeviceRunVo> ovenDeviceRunVos;

    @ApiModelProperty("温湿度列表集合")
    private List<TanSensorDeviceRunVo> tanSensorDeviceRunVos;



}
