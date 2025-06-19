package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("获取指定时间段设备采集信息入参对象")
@Data
@NoArgsConstructor
public class GetIotByDevicesDto {
    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("工序执行id")
    private Integer orderProcessId;

    @ApiModelProperty("工序编码")
    private String processCode;

    @ApiModelProperty("设备编码集合")
    private List<String> devicesCodes;

    @ApiModelProperty("类目")
    private String recordType;
}
