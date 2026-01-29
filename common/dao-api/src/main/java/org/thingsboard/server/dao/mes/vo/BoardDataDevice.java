package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
@ApiModel("设备上报数据")
public class BoardDataDevice {

    @ApiModelProperty("值")
    private String byQty;

    @ApiModelProperty("时间日期")
    private String byDate;

    @ApiModelProperty("时间戳")
    private Long byTs;

}
