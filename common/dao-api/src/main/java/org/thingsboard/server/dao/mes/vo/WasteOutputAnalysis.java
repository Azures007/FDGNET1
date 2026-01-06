package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单废料产出分析数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("订单废料产出分析")
public class WasteOutputAnalysis {

    @ApiModelProperty("时间")
    private String timeX;

    @ApiModelProperty("包材重量")
    private BigDecimal packWeight;

    @ApiModelProperty("原辅材废品重量")
    private BigDecimal wasteWeight;

    @ApiModelProperty("原辅材次品重量")
    private BigDecimal rejectWeight;

    @ApiModelProperty("废料率(%)")
    private BigDecimal wasteRate;
}
