package org.thingsboard.server.common.data.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 投入信息子实体类
 */
@Data
@ApiModel(value = "InputMaterialItem", description = "投入信息子实体")
public class InputMaterialItem {

    @ApiModelProperty(value = "物料编码")
    private String materialNumber;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "材料类型")
    private String recordType;

    @ApiModelProperty(value = "单位")
    private String recordUnit;

    @ApiModelProperty(value = "计划投入")
    private BigDecimal plannedInput;

    @ApiModelProperty(value = "实际投入")
    private BigDecimal actualInput;
}