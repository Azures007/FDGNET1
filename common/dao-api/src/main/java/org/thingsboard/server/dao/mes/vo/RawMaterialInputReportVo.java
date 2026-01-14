package org.thingsboard.server.dao.mes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 原料投入报表返回实体类
 */
@Data
@ApiModel("原料投入报表返回实体")
public class RawMaterialInputReportVo {

    // 订单基础信息
    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("下单时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    @ApiModelProperty("生产线")
    private String productionLine;

    @ApiModelProperty("产品名称")
    private String productName;

    // 合格品信息
    @ApiModelProperty("计划产量")
    private BigDecimal plannedOutput;

    @ApiModelProperty("实际产量")
    private BigDecimal actualOutput;

    @ApiModelProperty("净含量")
    private BigDecimal netContent;

    // 原料投入信息列表（按工序分组）
    @ApiModelProperty("原料投入信息列表（按工序分组）")
    private List<ProcessGroupInfoVo> processGroupInfoList;

}