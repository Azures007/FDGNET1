package org.thingsboard.server.dao.mes.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author
 * @version V1.0
 * @Package org.thingsboard.server.common.data.mes.vo
 * @date 2025/12/29
 * @Description: 报工记录视图对象
 */
@Data
@ApiModel("报工记录视图对象")
public class ReportRecordVo {

    @ExcelProperty(value = "生产线", index = 0)
    @ApiModelProperty("生产线")
    private String cwkLine;

    @ExcelProperty(value = "订单号", index = 1)
    @ApiModelProperty("订单号")
    private String orderNo;

    @ExcelProperty(value = "产品名称", index = 2)
    @ApiModelProperty("产品名称")
    private String productName;

    @ExcelProperty(value = "产品编码", index = 3)
    @ApiModelProperty("产品编码")
    private String productNumber;

    @ExcelProperty(value = "工序名称", index = 4)
    @ApiModelProperty("工序名称")
    private String processName;

    @ExcelProperty(value = "报工时间", index = 5)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("报工时间")
    private Date reportTime;

    @ExcelProperty(value = "报工人员", index = 6)
    @ApiModelProperty("报工人员")
    private String personName;

    @ExcelProperty(value = "报工类型", index = 7)
    @ApiModelProperty("报工类型")
    private String recordTypeBgName;

    @ExcelProperty(value = "物料名称", index = 8)
    @ApiModelProperty("物料名称")
    private String materialName;

    @ExcelProperty(value = "物料编码", index = 9)
    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ExcelProperty(value = "锅次", index = 10)
    @ApiModelProperty("锅次")
    private Integer potNumber;

    @ExcelProperty(value = "报工数量", index = 11)
    @NumberFormat("#.####")
    @ApiModelProperty("报工数量")
    private BigDecimal recordQty;

    @ExcelProperty(value = "报工单位", index = 12)
    @ApiModelProperty("报工单位")
    private String recordUnit;
}