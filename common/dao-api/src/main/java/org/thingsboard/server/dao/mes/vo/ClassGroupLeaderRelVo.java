package org.thingsboard.server.dao.mes.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassGroupLeaderRelVo {

    private Integer classGroupLeaderId;

    private Integer classId;

    private String className;

    private Integer personnelId;

    private String name;

    private String sex;

    private String phone;

    public Integer getClassGroupLeaderId() {
        return classGroupLeaderId;
    }

    public void setClassGroupLeaderId(Integer classGroupLeaderId) {
        this.classGroupLeaderId = classGroupLeaderId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(Integer personnelId) {
        this.personnelId = personnelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
