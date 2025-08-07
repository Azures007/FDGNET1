package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.dao.mes.vo.MenuListVo;

@ApiModel("设置菜单")
public class SetMenuDto {
    @ApiModelProperty("设置的角色id")
    private Integer roleId;
    @ApiModelProperty("菜单集合")
    private MenuListVo menuListVo;

    public SetMenuDto() {
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public MenuListVo getMenuListVo() {
        return menuListVo;
    }

    public void setMenuListVo(MenuListVo menuListVo) {
        this.menuListVo = menuListVo;
    }

    public SetMenuDto(Integer roleId, MenuListVo menuListVo) {
        this.roleId = roleId;
        this.menuListVo = menuListVo;
    }
}
