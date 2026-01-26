package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel("烤炉温度折线图")
public class ListDeviceTempDatsVo {


    @ApiModelProperty("上温度")
    private String byMaxQty;
    @ApiModelProperty("下温度")
    private String byMinQty;

    @ApiModelProperty("时间日期")
    private String byDate;

    @ApiModelProperty("时间戳")
    private Long byTs;
}
