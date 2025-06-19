package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.dto
 * @date 2022/4/11 9:47
 * @Description:
 */
@ApiModel("用户新增,修改模型")
public class UpdateAndSaveDto {
    @ApiModelProperty(value = "用户id,为null为新增，不为null为修改")
    private String user_id;
    @ApiModelProperty(value = "账号,修改时为null默认不改")
    private String username;
    @ApiModelProperty("用户名,为null默认不改")
    private String first_name;
    @ApiModelProperty("用户密码,仅新增使用")
    private String password;
    @ApiModelProperty("用户状态 0:可用 1：不可用,新增时为null默认为0，修改时为null默认不改")
    private String user_status;
    @ApiModelProperty("角色id,修改时为null默认不改")
    private Integer role_id;
    @ApiModelProperty(value = "修改日期",required = false)
    @Column(name = "updated_time")
    private Date updated_time;
    @ApiModelProperty(value = "修改人",required = false)
    @Column(name = "updated_name")
    private String updated_name;

    public UpdateAndSaveDto(String user_id, String username, String first_name, String password, String user_status, Integer role_id, Date updated_time, String updated_name) {
        this.user_id = user_id;
        this.username = username;
        this.first_name = first_name;
        this.password = password;
        this.user_status = user_status;
        this.role_id = role_id;
        this.updated_time = updated_time;
        this.updated_name = updated_name;
    }

    public UpdateAndSaveDto() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public Date getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(Date updated_time) {
        this.updated_time = updated_time;
    }

    public String getUpdated_name() {
        return updated_name;
    }

    public void setUpdated_name(String updated_name) {
        this.updated_name = updated_name;
    }
}
