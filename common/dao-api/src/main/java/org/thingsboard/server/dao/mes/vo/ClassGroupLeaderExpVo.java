package org.thingsboard.server.dao.mes.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: hhh
 * @Date: 2022/7/19 16:16
 * @Description:班别和组长的导出
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassGroupLeaderExpVo {
    //班别名称
    private String name;
    //班别编码
    private String classNumber;
    //排班
    private String scheduling;
    //组长
    private String groupLeader;
    //组长电话
    private String groupLeaderPhone;
    //组长岗位
    private String groupLeaderStation;

}
