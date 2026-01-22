package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel("设备数据集合")
public class ListDeviceData {

    @ApiModelProperty("设备代码")
    private String deviceCode;

    @ApiModelProperty("上报数据集合")
    private List<BoardDataDevice> boardDataDeviceList;

}
