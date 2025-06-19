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
 * @date 2022/4/12 10:52
 * @Description:
 */
@Entity
@Data
@ApiModel("角色菜单关系类")
@Table(name = "t_sys_role_menu")
public class TSysRoleMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_menu_id")
    private Integer roleMenuId;
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "menu_id")
    private Integer menuId;
    @ApiModelProperty("是否可用 0：可用 1：不可用")
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
