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
public class ClassGroupLeaderExcelVo extends BaseRowModel {

    //增加导出功能，导出班别查询列表全部记录，导出内容包括：班别名称、排班、组长（姓名、手机号码、岗位）、组员（姓名、手机号码、岗位）等
    @ExcelProperty("班别名称")
    private String name;

    @ExcelProperty("班别编码")
    private String classNumber;

    @ExcelProperty("排班")
    private String scheduling;

    @ExcelProperty("组长-姓名")
    private String groupLeader;
    @ExcelProperty("组长-手机号码")
    private String groupLeaderPhone;
    @ExcelProperty("组长-岗位")
    private String groupLeaderStation;

//    @ExcelProperty("组员-姓名")
//    private String personnelName;
//    @ExcelProperty("组员-手机号码")
//    private String personnelPhone;
//    @ExcelProperty("组员-岗位")
//    private String personnelStation;



}