package org.thingsboard.server.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @Auther: hhh
 * @Date: 2022/7/12 18:07
 * @Description:
 */
@Data
public class ClassPersonnelExcelVo extends BaseRowModel {

    //增加导出功能，导出班别查询列表全部记录，导出内容包括：班别名称、排班、组长（姓名、手机号码、岗位）、组员（姓名、手机号码、岗位）等
    @ExcelProperty(value = "班别名称",index = 0)
    private String name;

    @ExcelProperty(value = "班别编码",index = 1)
    private String classNumber;

    @ExcelProperty(value ="排班",index = 2)
    private String scheduling;

    @ExcelProperty(value ="组员-姓名",index = 3)
    private String personnel;
    @ExcelProperty(value ="组员-手机号码",index = 4)
    private String personnelPhone;
    // 2025-07-23 hhh 隐藏导出表格两个页签的岗位字段
//    @ExcelProperty(value ="组员-岗位",index =5)
//    private String personnelStation;



}
