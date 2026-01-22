package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@ApiModel("内包机产量分析对线")
public class LineClVo {

    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("上报值")
    private BigDecimal byQty;

}
