package org.thingsboard.server.common.data.mes.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 投入产出比报表导出Excel实体
 */
@Data
@ColumnWidth(20)
public class ProductionDataExcelVo {

    // --- 基础信息 ---
    @ExcelProperty({"基础信息", "日期"})
    private String date;

    @ExcelProperty({"基础信息", "产线"})
    private String productionLine;

    // --- 投入信息 ---
    @ExcelProperty({"投入信息", "物料编码"})
    private String materialNumber;

    @ExcelProperty({"投入信息", "物料名称"})
    private String materialName;

    @ExcelProperty({"投入信息", "材料类型"})
    private String recordType;

    @ExcelProperty({"投入信息", "单位"})
    private String recordUnit;

    @ExcelProperty({"投入信息", "计划投入"})
    private String plannedInput;

    @ExcelProperty({"投入信息", "实际投入"})
    private String actualInput;

    // --- 损耗信息 ---
    @ExcelProperty({"损耗信息", "废次品重量"})
    private String defectiveWeight;

    // --- 产出信息 ---
    @ExcelProperty({"产出信息", "计划产量（件）"})
    private String plannedOutput;

    @ExcelProperty({"产出信息", "实际产量（件）"})
    private String actualOutput;

    @ExcelProperty({"产出信息", "净含量重量"})
    private String netContentWeight;

    // --- 投入产出比 ---
    @ExcelProperty({"投入产出比", "投入产出比"})
    private String inputOutputRatio;

    @ExcelProperty({"投入产出比", "单箱原辅料消耗（kg/件）"})
    private String materialConsumptionPerBox;

    @ExcelProperty({"投入产出比", "废次品比率"})
    private String defectiveRate;

    // 用于合并单元格的辅助字段：唯一标识一组数据（产线+日期）
    @ExcelIgnore
    private String groupKey;
}
