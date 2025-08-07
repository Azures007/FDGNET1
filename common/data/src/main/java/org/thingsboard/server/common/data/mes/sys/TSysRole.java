package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @date 2022/4/7 15:37
 * @Description:
 */
@Data
@Entity
@Table(name = "t_sys_role")
@ApiModel("角色类")
public class TSysRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("角色id")
    @Column(name = "role_id")
    private Integer roleId;
    @ApiModelProperty("角色名")
    @Column(name = "role_name")
    private String roleName;
    @ApiModelProperty("角色代码")
    @Column(name = "role_code")
    private String roleCode;
    @ApiModelProperty("角色描述")
    @Column(name = "role_explain")
    private String roleExplain;
    @ApiModelProperty("是否有厂长/主任权限0：有 1：无")
    @Column(name = "by_factory")
    private String byFactory;
    @ApiModelProperty("是否有工序组长权限 0：有：1：无")
    @Column(name = "by_group")
    private String byGroup;
    @ApiModelProperty("是否可用 0：不可用 1：可用")
    private String enabled;
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

