package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 原料投入信息实体
 */
@Data
@ApiModel("原料投入信息实体")
public class RawMaterialInputInfoVo {

    @ApiModelProperty("工序名称")
    private String processName;

    @ApiModelProperty("工序状态")
    private String processStatus;

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

    @ApiModelProperty("计划锅数")
    private Integer plannedPotCount;

    @ApiModelProperty("实际累计锅数")
    private Integer actualAccumulatedPotCount;

    @ApiModelProperty("残次品重量")
    private BigDecimal defectiveProductWeight;

    @ApiModelProperty("订单号")
    private String orderNo;

}