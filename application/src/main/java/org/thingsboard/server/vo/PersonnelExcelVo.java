package org.thingsboard.server.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/4/26 16:07
 * @Description:
 */
@Data
public class PersonnelExcelVo extends BaseRowModel {

    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("性别")
    private String sex;
    @ExcelProperty("手机号")
    private String phone;
//    @ExcelProperty("岗位")
//    private String station;
}
