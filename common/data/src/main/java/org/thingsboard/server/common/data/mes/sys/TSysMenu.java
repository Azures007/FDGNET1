package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @date 2022/4/11 14:24
 * @Description:
 */
@Entity
@ApiModel("菜单类")
@Data
@Table(name = "t_sys_menu")
public class TSysMenu {
    @Id
    @Column(name = "menu_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuId;
    @ApiModelProperty("菜单名")
    @Column(name = "menu_name")
    private String menuName;
    @ApiModelProperty("菜单说明")
    @Column(name = "menu_explain")
    private String menuExplain;
    @ApiModelProperty("菜单路径")
    @Column(name = "path")
    private String path;
    @ApiModelProperty("父级菜单id,根菜单为0")
    @Column(name = "parent_id")
    private Integer parentId;
    @ApiModelProperty("菜单类型，标识 0:菜单 1:功能按钮 2:Url;")
    @Column(name = "flag")
    private String flag;
    @ApiModelProperty("是否可用 0：不可用 1：可用")
//    @ApiModelProperty("是否可用 0：可用 1：不可用") 废弃
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
    @ApiModelProperty("子菜单集合")
    @Transient
    private List<TSysMenu> tSysMenus;
    @Transient
    @ApiModelProperty("是否有此权限 0:有 1：无")
    private String isFlag = "1";
    @Transient
    @ApiModelProperty("标记使用")
    private boolean select = false;
    @Column(name = "menu_type")
    @ApiModelProperty("菜单类型，toggle=折叠菜单，link=菜单")
    private String menuType;
    @Column(name = "skip_url")
    @ApiModelProperty("跳转url，只对标识为url时有效")
    private String skipUrl;
    @Column(name = "br")
    @ApiModelProperty("备注")
    private String br;
    @Column(name = "sort")
    @ApiModelProperty("排序")
    private Integer sort;

}
