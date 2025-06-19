package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.TSysMenu;

import java.util.List;

@ApiModel("树结构菜单")
public class MenuListVo {
    @ApiModelProperty("菜单列表")
    private List<TSysMenu> tsysMenus;
    @ApiModelProperty("菜单名称")
    private String menuName="全部菜单";
    @ApiModelProperty("菜单路径")
    private String path="";
    @ApiModelProperty("是否有此权限 0:有 1：无")
    private String isFlag = "1";
    @ApiModelProperty("标记 ")
    private Boolean select=false;

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public List<TSysMenu> getTsysMenus() {
        return tsysMenus;
    }

    public void setTsysMenus(List<TSysMenu> tsysMenus) {
        this.tsysMenus = tsysMenus;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MenuListVo() {
    }

    public MenuListVo(List<TSysMenu> tsysMenus, String menuName, String path, String isFlag, Boolean select) {
        this.tsysMenus = tsysMenus;
        this.menuName = menuName;
        this.path = path;
        this.isFlag = isFlag;
        this.select = select;
    }

    public String getIsFlag() {
        return isFlag;
    }

    public void setIsFlag(String isFlag) {
        this.isFlag = isFlag;
    }
}
