package org.thingsboard.server.dao.mes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产态势监察 - 生产报工数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("生产报工数据")
public class ProductionBgVo {

    @ApiModelProperty("产线")
    private String productionLine;

    @ApiModelProperty("工序")
    private String process;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("报工标准")
    private String standard;

    @ApiModelProperty("报工数量")
    private BigDecimal recordQuantity;

    @ApiModelProperty("锅数")
    private String potStr;

    @ApiModelProperty("报工时间")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm", timezone = "GMT+8")
    private Date recordTime;
}
