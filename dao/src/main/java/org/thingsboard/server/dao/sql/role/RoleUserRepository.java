package org.thingsboard.server.dao.sql.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TSysRoleUser;
import org.thingsboard.server.dao.dto.PageUserDto;

import java.util.List;
import java.util.Map;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.role
 * @date 2022/4/11 9:41
 * @Description:
 */
public interface RoleUserRepository extends JpaRepository<TSysRoleUser,Integer> {
    TSysRoleUser getByUserId(String userId);
    @Query(value = "select cast(a.id as varchar) user_id,a.email username,a.first_name,\n" +
            "\t to_char(b.created_time,'YYYY-MM-DD') created_time ,b.user_status ,b.created_name ,\n" +
            "\tc.role_id ,c.role_name,c.role_code ,c.role_explain \n" +
            "\tfrom tb_user a  \n" +
            "\tleft join t_sys_role_user  b on cast(a.id as varchar) =b.user_id \n" +
            "\tleft join t_sys_role c on b.role_id =c.role_id \n" +
            "\t where 1=1 \n" +
            "and (a.email like %:#{#pageUserDto.username}% or :#{#pageUserDto.username} is null or :#{#pageUserDto.username} ='') \n" +
            "and (a.first_name like %:#{#pageUserDto.name}% or :#{#pageUserDto.name} is null or :#{#pageUserDto.name} ='') \n" +
            "and (b.user_status=:#{#pageUserDto.userStatus} or :#{#pageUserDto.userStatus} is null or :#{#pageUserDto.userStatus} ='') \n"+
            " order by b.created_time desc,role_user_id desc \n"+
            "limit :size offset :current"+
            "",nativeQuery = true)
    List<Map> pageUser(@Param("current") Integer current,
                       @Param("size") int size,
                       @Param("pageUserDto") PageUserDto pageUserDto);

    @Query(value = "select cast(a.id as varchar) user_id,a.email username,a.first_name,\n" +
            "\t to_char(b.created_time,'YYYY-MM-DD') created_time ,b.user_status ,b.created_name ,\n" +
            "\tc.role_id ,c.role_name,c.role_code ,c.role_explain \n" +
            "\tfrom tb_user a  \n" +
            "\tleft join t_sys_role_user  b on cast(a.id as varchar) =b.user_id \n" +
            "\tleft join t_sys_role c on b.role_id =c.role_id \n" +
            "\t where 1=1 \n" +
            "and ((a.email like  :nameAndUsername or :nameAndUsername is null or :nameAndUsername ='') \n" +
            "or (a.first_name like :nameAndUsername or :nameAndUsername is null or :nameAndUsername ='')) \n"
            ,nativeQuery = true)
    List<Map> pageUserByUserNameAndName(@Param("nameAndUsername") String nameAndUsername);

    @Query(value = "select count(1) \n"+
            "\tfrom tb_user a  \n" +
            "\tleft join t_sys_role_user  b on cast(a.id as varchar) =b.user_id \n" +
            "\tleft join t_sys_role c on b.role_id =c.role_id \n" +
            "\t where 1=1 \n" +
            "and (a.email like %:#{#pageUserDto.username}% or :#{#pageUserDto.username} is null or :#{#pageUserDto.username} ='') \n" +
            "and (a.first_name like %:#{#pageUserDto.name}% or :#{#pageUserDto.name} is null or :#{#pageUserDto.name} ='') \n" +
            "and (b.user_status=:#{#pageUserDto.userStatus} or :#{#pageUserDto.userStatus} is null or :#{#pageUserDto.userStatus} ='')",
    nativeQuery = true)
    int pageUserCount(@Param("pageUserDto") PageUserDto pageUserDto);

    void deleteByUserId(String userId);


    @Query(value = "select cast(a.id as varchar) user_id,a.email username,a.first_name,\n" +
            "\t to_char(b.created_time,'YYYY-MM-DD') created_time ,b.user_status ,b.created_name ,\n" +
            "\tc.role_id ,c.role_name,c.role_code ,c.role_explain \n" +
            "\tfrom tb_user a  \n" +
            "\tleft join t_sys_role_user  b on cast(a.id as varchar) =b.user_id \n" +
            "\tleft join t_sys_role c on b.role_id =c.role_id \n" +
            "\t where 1=1 \n" +
            "and ((a.email like %:nemeOrAccount% or :nemeOrAccount is null or :nemeOrAccount ='') \n" +
            "or (a.first_name like %:nemeOrAccount% or :nemeOrAccount is null or :nemeOrAccount ='')) " +
            " order by b.created_time desc,role_user_id desc \n"+
            "limit :size offset :current"+
            "",nativeQuery = true)
    List<Map> pageUserByNemeOrAccount(@Param("current") Integer current,
                       @Param("size") int size,
                       @Param("nemeOrAccount") String nemeOrAccount);

    @Query(value = "select count(1) \n"+
            "\tfrom tb_user a  \n" +
            "\tleft join t_sys_role_user  b on cast(a.id as varchar) =b.user_id \n" +
            "\tleft join t_sys_role c on b.role_id =c.role_id \n" +
            "\t where 1=1 \n" +
            "and ((a.email like %:nemeOrAccount% or :nemeOrAccount is null or :nemeOrAccount ='') \n" +
            "or (a.first_name like %:nemeOrAccount% or :nemeOrAccount is null or :nemeOrAccount ='')) ",
            nativeQuery = true)
    int pageUserCountByNemeOrAccount(@Param("nemeOrAccount") String nemeOrAccount);


}
