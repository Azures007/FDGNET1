package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@ApiModel("设备时间信息")
@Data
@NoArgsConstructor
public class DevicesTimeVo {

    @ApiModelProperty("设备代码")
    private String deviceCode;
    @ApiModelProperty("开始时间")
    private String createdTime;
    @ApiModelProperty("结束时间")
    private String endTime;
    @ApiModelProperty("当前时间段设备采集信息")
    private BigInteger value;
}
