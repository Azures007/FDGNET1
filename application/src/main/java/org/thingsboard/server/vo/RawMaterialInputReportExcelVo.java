package org.thingsboard.server.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * 原料投入报表导出实体
 */
@Data
public class RawMaterialInputReportExcelVo extends BaseRowModel {

    // 订单基础信息
    @ExcelProperty({"订单基础信息", "订单号"})
    private String orderNo;

    @ExcelProperty({"订单基础信息", "下单时间"})
    private String orderTime;

    @ExcelProperty({"订单基础信息", "生产线"})
    private String productionLine;

    @ExcelProperty({"订单基础信息", "产品名称"})
    private String productName;

    // 合格品信息
    @ExcelProperty({"合格品信息", "计划产量"})
    private String plannedOutput;

    @ExcelProperty({"合格品信息", "实际产量"})
    private String actualOutput;

    @ExcelProperty({"合格品信息", "净含量"})
    private String netContent;

    // 工序列表
    @ExcelProperty({"工序列表", "工序名称"})
    private String processName;

    @ExcelProperty({"工序列表", "工序状态"})
    private String processStatus;

    @ExcelProperty({"工序列表", "物料编码"})
    private String materialCode;

    @ExcelProperty({"工序列表", "物料名称"})
    private String materialName;

    @ExcelProperty({"工序列表", "单位"})
    private String unit;

    @ExcelProperty({"工序列表", "计划投入"})
    private String plannedInput;

    @ExcelProperty({"工序列表", "实际投入"})
    private String actualInput;

    @ExcelProperty({"工序列表", "计划锅数"})
    private String plannedPotCount;

    @ExcelProperty({"工序列表", "实际累计锅数"})
    private String actualAccumulatedPotCount;

    @ExcelProperty({"工序列表", "残次品重量"})
    private String defectiveWeight;

    @ExcelIgnore
    private String processGroupKey;

}