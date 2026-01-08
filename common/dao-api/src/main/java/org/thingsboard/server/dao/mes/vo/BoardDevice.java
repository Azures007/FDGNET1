package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel("大屏设备")
public class BoardDevice {

    @ApiModelProperty("设备名称")
    private String deviceName;
    @ApiModelProperty("设备编号")
    private String deviceNumber;
    @ApiModelProperty("上报设备数据集")
    private List<BoardDataDevice> boardDataDeviceList;

}
