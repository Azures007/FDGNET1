package org.thingsboard.server.vo;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TSysPdRecordExcelVo extends BaseRowModel {

    @ExcelProperty("序号")
    private Integer pdRecordId;

    @ExcelProperty("盘点日期")
    private String pdTime;

    @ExcelProperty("材料编码")
    private String materialNumber;

    @ExcelProperty("材料名称")
    private String materialName;

    @ExcelProperty("规格")
    private String materialSpecifications;

    @ExcelProperty("单位")
    private String pdUnit;

    @ExcelProperty("盘点余量")
    private BigDecimal pdQty;

    @ExcelProperty("盘点人")
    private String pdCreatedName;

    @ExcelProperty("盘点车间名称")
    private String pdWorkshopName;

    @ExcelProperty("是否为还原材料")
    private String isReturn;

    @ExcelProperty("车间主任")
    private String pdWorkshopLeaderName;
}