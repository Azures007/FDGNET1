package org.thingsboard.server.dao.sql.role;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TSysRole;

import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.role
 * @date 2022/4/6 13:50
 * @Description:
 */
public interface RoleRepository extends JpaRepository<TSysRole, Integer> {
    TSysRole getByRoleId(Integer roleId);

    @Query(value = "SELECT dblink_connect('mycoon','host=192.168.150.59 port=5432 dbname=postgres user=postgres password=sinceTech@2021');\n" +
            "SELECT dblink_exec('mycoon', 'BEGIN');\n" +
            "SELECT dblink_exec('mycoon', 'update test set name=:str where name=:str ');\n" +
            "SELECT dblink_exec('mycoon', 'COMMIT');\n" +
            "SELECT dblink_disconnect('mycoon');",nativeQuery = true)
    void test(@Param("str") String str);

    @Query(value = "select a.* from t_sys_role a join t_sys_role_user b on a.role_id=b.role_id where b.user_id=?1",nativeQuery = true)
    TSysRole getByUserId(String useId);
}
