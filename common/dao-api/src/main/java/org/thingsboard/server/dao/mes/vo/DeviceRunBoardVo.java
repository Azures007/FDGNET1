package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@ApiModel("设备运行看板对象")
public class DeviceRunBoardVo {

    @ApiModelProperty("日期")
    private String byDateTime;

    @ApiModelProperty("设备运行看板类型集合")
    private List<DeviceRunBoardTypeVo> deviceRunBoardTypeVoList;



}
