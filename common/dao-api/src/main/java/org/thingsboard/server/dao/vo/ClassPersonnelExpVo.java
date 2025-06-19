package org.thingsboard.server.dao.vo;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public String getScheduling() {
        return scheduling;
    }

    public void setScheduling(String scheduling) {
        this.scheduling = scheduling;
    }

    public String getPersonnel() {
        return personnel;
    }

    public void setPersonnel(String personnel) {
        this.personnel = personnel;
    }

    public String getPersonnelPhone() {
        return personnelPhone;
    }

    public void setPersonnelPhone(String personnelPhone) {
        this.personnelPhone = personnelPhone;
    }

    public String getPersonnelStation() {
        return personnelStation;
    }

    public void setPersonnelStation(String personnelStation) {
        this.personnelStation = personnelStation;
    }
}
