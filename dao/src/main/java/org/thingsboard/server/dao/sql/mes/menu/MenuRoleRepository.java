package org.thingsboard.server.dao.sql.mes.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.mes.sys.TSysRoleMenu;

import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.menu
 * @date 2022/4/12 10:59
 * @Description:
 */
public interface MenuRoleRepository extends JpaRepository<TSysRoleMenu,Integer> {
    @Query("select m.menuId from TSysRoleMenu m where m.roleId=?1")
    List<Integer> listIdByRoleId(Integer roleId);

    void deleteByRoleId(Integer roleId);
}
