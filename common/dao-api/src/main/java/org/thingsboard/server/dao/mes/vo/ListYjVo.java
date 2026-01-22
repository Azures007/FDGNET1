package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ApiModel("告警预测对象")
@Data
@ToString
public class ListYjVo {

    @ApiModelProperty("时间")
    private String byDateStr;

    @ApiModelProperty("设备编码")
    private String deviceCode;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("告警类型")
    private String byType;

    @ApiModelProperty("告警说明")
    private String byBr;


}
