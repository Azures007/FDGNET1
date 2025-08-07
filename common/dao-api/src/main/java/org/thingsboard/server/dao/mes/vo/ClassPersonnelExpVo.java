package org.thingsboard.server.dao.mes.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Auther: hhh
 * @Date: 2022/7/19 16:16
 * @Description:班别和组员的导出
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassPersonnelExpVo {
    //班别名称
    private String name;
    //班别编码
    private String classNumber;
    //排班
    private String scheduling;
    //组员
    private String personnel;
    //组员电话
    private String personnelPhone;
    //组员岗位
    private String personnelStation;

}
