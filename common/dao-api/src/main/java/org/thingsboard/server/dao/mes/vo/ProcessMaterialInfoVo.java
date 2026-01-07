package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 工序物料信息VO
 */
@Data
public class ProcessMaterialInfoVo {

    @ApiModelProperty("物料编码")
    private String materialCode;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("计划投入")
    private BigDecimal plannedInput;

    @ApiModelProperty("实际投入")
    private BigDecimal actualInput;

    @ApiModelProperty("计划锅数量")
    private Integer plannedPotCount;

    @ApiModelProperty("实际累计锅数")
    private Integer actualAccumulatedPotCount;

    @ApiModelProperty("残次品重量")
    private BigDecimal defectiveWeight;

//    @ApiModelProperty("订单号")
//    private String orderNo;
}