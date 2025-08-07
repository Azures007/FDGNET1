package org.thingsboard.server.dao.menu;

import org.thingsboard.server.common.data.mes.sys.TSysMenu;
import org.thingsboard.server.dao.dto.SetRoleMenuDto;
import org.thingsboard.server.dao.vo.MenuListVo;
import org.thingsboard.server.dao.vo.PageVo;

import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.menu
 * @date 2022/4/11 16:52
 * @Description:
 */
public interface MenuService {
    /**
     * 新增/修改菜单
     * @param tSysMenu
     */
    void update(TSysMenu tSysMenu);

    /**
     * 返回菜单列表
     * @param isTree
     * @return
     */
    MenuListVo list(int isTree, Integer roleId);

    /**
     * 返回角色菜单权限集合
     * @param roleId
     * @return
     */
    List<Integer> listRoleMenu(Integer roleId);

    /**
     * 设置菜单权限
     * @param setRoleMenuDto
     */
    void setRoleMenu(SetRoleMenuDto setRoleMenuDto);
    /**
     * 返回当前用户的菜单列表
     */
    List<TSysMenu> getMyMenu(String userId);

    List<String> listPath(String toString);

    /**
     * 返回树形式菜单列表
     * @return
     */
    MenuListVo getMenuListVoByTree();

    /**
     * 分页菜单列表
     * @param current
     * @param size
     * @return
     */
    PageVo<TSysMenu> pageMenu(Integer current, Integer size);

    /**
     *  分页菜单列表
     * @param current
     * @param size
     * @return
     */
    PageVo<TSysMenu> pageMenu(Integer current, Integer size, TSysMenu tSysMenu);

    /**
     * 返回当前用户报表菜单
     * @param userId
     * @return
     */
    List<TSysMenu> listMenuByUser(String userId);

    /**
     * 通过id获取菜单对象
     * @param menuId
     * @return
     */
    TSysMenu getById(Integer menuId);

    /**
     * 删除菜单
     * @param menuId
     */
    void delete(Integer menuId);
}
