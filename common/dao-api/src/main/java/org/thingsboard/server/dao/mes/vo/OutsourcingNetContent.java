package org.thingsboard.server.dao.mes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 外包净含量实况
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("外包净含量实况")
public class OutsourcingNetContent {

    @ApiModelProperty("产线")
    private String productionLine;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("净含量标准(g)Str")
    private String netContentStandardStr;

    @ApiModelProperty("净含量标准上限(g)")
    private BigDecimal netContentStandardUpper;

    @ApiModelProperty("净含量标准下限(g)")
    private BigDecimal netContentStandardLower;

    @ApiModelProperty("实际净含量(g)")
    private BigDecimal actualNetContent;

    @ApiModelProperty("报工时间")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm", timezone = "GMT+8")
    private Date recordTime;
}
