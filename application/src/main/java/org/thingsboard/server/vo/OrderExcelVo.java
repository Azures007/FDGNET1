package org.thingsboard.server.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: hhh
 * @Date: 2022/4/30 16:07
 * @Description:
 */
@Data
public class OrderExcelVo extends BaseRowModel {

    @ExcelProperty("单据编号")
    private String billNo;

    @ExcelProperty("下单时间")
    private Date billDate;

    @ExcelProperty("生产车间")
    private String bodyPrdDept;

    @ExcelProperty("计划产量")
    private Float billPlanQty;

    @ExcelProperty("计划完工日期")
    private Date bodyPlanFinishDate;

    @ExcelProperty("产品规格")
    private String bodyMaterialSpecification;

    @ExcelProperty("订单类型")
    private String billType;

    @ExcelProperty("订单状态")
    private String orderStatus;

    @ExcelProperty("当前工序")
    private String currentProcessName;

    @ExcelProperty("处理班别")
    private String className;

}