package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @date 2022/4/11 9:31
 * @Description:
 */
@Data
@Entity
@Table(name = "t_sys_role_user")
@ApiModel("角色用户类")
public class TSysRoleUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    @Column(name = "role_user_id")
    private Integer roleUserId;
    @ApiModelProperty("角色表id")
    @Column(name = "role_id")
    private Integer roleId;
    @ApiModelProperty("用户表id")
    @Column(name = "user_id")
    private String userId;
    @ApiModelProperty("用户状态 0:可用 1：不可用")
    @Column(name = "user_status")
    private String userStatus;
    @ApiModelProperty("上次登陆时间")
    @Column(name = "last_time")
    private Date lastTime;
    @ApiModelProperty("创建人")
    @Column(name = "created_name")
    private String createdName;
    @ApiModelProperty("创建日期")
    @Temporal(TemporalType.DATE)
    @Column(name = "created_time")
    private Date createdTime;
    @ApiModelProperty("修改日期")
    @Temporal(TemporalType.DATE)
    @Column(name = "updated_time")
    private Date updatedTime;
    @ApiModelProperty("修改人")
    @Column(name = "updated_name")
    private String updatedName;

}
