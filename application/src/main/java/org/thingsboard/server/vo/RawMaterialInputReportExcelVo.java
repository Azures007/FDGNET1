package org.thingsboard.server.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * 原料投入报表导出实体
 */
@Data
public class RawMaterialInputReportExcelVo extends BaseRowModel {

    // 订单基础信息
    @ExcelProperty("订单号")
    private String orderNo;

    @ExcelProperty("下单时间")
    private String orderTime;

    @ExcelProperty("生产线")
    private String productionLine;

    @ExcelProperty("产品名称")
    private String productName;

    // 合格品信息
    @ExcelProperty("计划产量（件）")
    private String plannedOutput;

    @ExcelProperty("实际产量（件）")
    private String actualOutput;

    // 原料投入信息
    @ExcelProperty("工序名称")
    private String processName;

    @ExcelProperty("工序状态")
    private String processStatus;

    @ExcelProperty("物料编码")
    private String materialCode;

    @ExcelProperty("物料名称")
    private String materialName;

    @ExcelProperty("单位")
    private String unit;

    @ExcelProperty("计划投入")
    private String plannedInput;

    @ExcelProperty("实际投入")
    private String actualInput;

    @ExcelProperty("计划锅数")
    private String plannedPotCount;

    @ExcelProperty("实际累计锅数")
    private String actualAccumulatedPotCount;

    @ExcelProperty("残次品重量（kg）")
    private String defectiveWeight;

}