package org.thingsboard.server.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: hhh
 * @Date: 2022/4/30 16:07
 * @Description:
 */
@Data
public class OrderExcelVo extends BaseRowModel {

    @ExcelProperty("序号")
    private Integer index;

    @ExcelProperty("订单号")
    private String orderNo;

    @ExcelProperty("下单日期")
    private String billDate;

    @ExcelProperty("订单类型")
    private String billType;

    @ExcelProperty("生产线")
    private String vwkname;

    @ExcelProperty("产品编码")
    private String code;

    @ExcelProperty("产品名称")
    private String name;

    @ExcelProperty("规格")
    private String materialspec;

    @ExcelProperty("计划产量(件)")
    private BigDecimal nnum;

    @ExcelProperty("计划开工日期")
    private String tplanstarttime;

    @ExcelProperty("订单状态")
    private String orderStatus;
}
