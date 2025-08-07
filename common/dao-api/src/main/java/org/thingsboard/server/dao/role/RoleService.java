package org.thingsboard.server.dao.role;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysRole;
import org.thingsboard.server.common.data.mes.sys.TSysRoleUser;
import org.thingsboard.server.dao.dto.ListRoleDto;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.role
 * @date 2022/4/7 10:06
 * @Description:
 */
public interface RoleService{
    /**
     * 返回角色列表
     * @return
     */
    Page<TSysRole> listRole(Integer current, Integer size, ListRoleDto listRoleDto);

    /**
     * 保存角色信息
     * @param role
     */
    void saveRole(TSysRole role);

    /**
     * 删除角色
     * @param roleId
     */
    void deleteRole(Integer roleId);

    /**
     * 保存用户角色关系
     * @param tSysRoleUser
     */
    void saveRoleUser(TSysRoleUser tSysRoleUser);

    /**
     * 获取角色信息
     * @param roleId
     * @return
     */
    TSysRole getById(Integer roleId);

    void test();

    TSysRole getByUserId(String useId);
}
