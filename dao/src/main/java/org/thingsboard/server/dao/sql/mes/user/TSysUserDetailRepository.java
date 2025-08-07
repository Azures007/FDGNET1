package org.thingsboard.server.dao.sql.mes.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.sys.TSysUserDetail;

import java.util.List;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.user
 * @Description: 用户详细信息数据访问接口
 */
public interface TSysUserDetailRepository extends JpaRepository<TSysUserDetail, Integer> {

    /**
     * 根据用户ID查询用户详细信息
     * @param userId 用户ID
     * @return 用户详细信息
     */
    List<TSysUserDetail> findByUserId(String userId);

    /**
     * 根据基地ID查询用户详细信息列表
     * @param ncPkOrg 基地ID
     * @return 用户详细信息列表
     */
    @Query("SELECT t FROM TSysUserDetail t WHERE t.ncPkOrg = :ncPkOrg")
    List<TSysUserDetail> findByNcPkOrg(@Param("ncPkOrg") String ncPkOrg);

    /**
     * 根据部门ID查询用户详细信息列表
     * @param ncCdeptid 部门ID
     * @return 用户详细信息列表
     */
    @Query("SELECT t FROM TSysUserDetail t WHERE t.ncCdeptid = :ncCdeptid")
    List<TSysUserDetail> findByNcCdeptid(@Param("ncCdeptid") String ncCdeptid);

    /**
     * 根据产线ID查询用户详细信息列表
     * @param ncCwkid 产线ID
     * @return 用户详细信息列表
     */
    @Query("SELECT t FROM TSysUserDetail t WHERE t.ncCwkid = :ncCwkid")
    List<TSysUserDetail> findByNcCwkid(@Param("ncCwkid") String ncCwkid);

    /**
     * 删除指定用户的详细信息
     * @param userId 用户ID
     */
    void deleteByUserId(String userId);
}
