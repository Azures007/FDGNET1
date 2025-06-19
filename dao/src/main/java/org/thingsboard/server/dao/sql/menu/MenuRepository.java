package org.thingsboard.server.dao.sql.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.TSysMenu;

import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.menu
 * @date 2022/4/11 16:51
 * @Description:
 */
public interface MenuRepository extends JpaRepository<TSysMenu,Integer> {
    @Query(value = "select a.* from t_sys_menu a join t_sys_role_menu b on a.menu_id=b.menu_id where b.role_id=?1 order by menu_id asc",
            nativeQuery = true)
    List<TSysMenu> listMenuByRoleId(Integer roleId);

    @Query(value = "select a.path from t_sys_menu a join t_sys_role_menu b on a.menu_id=b.menu_id where b.role_id=?1 and a.enabled='0'",nativeQuery = true)
    List<String> listPath(Integer roleId);

    /**
     * 获取当前用户报工报表
     * @param roleId
     * @return
     */
    @Query(value = "\n" +
            "select a.* from t_sys_menu a \n" +
            "join t_sys_role_menu b on a.menu_id =b.menu_id \n" +
            "where a.flag in (?2) and b.role_id =?1 and a.enabled ='0'",nativeQuery = true)
    List<TSysMenu> listMenuByUser(Integer roleId,List<String> flag);
}
