package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@ApiModel("时间段设备采集信息")
@Data
@NoArgsConstructor
public class GetIotByDevicesVo {
    @ApiModelProperty("iot时间区间内差值")
    private BigInteger diffValue;

    @ApiModelProperty("设备详情")
    private List<DevicesTimeVo> devicesTimeVoList;

}
