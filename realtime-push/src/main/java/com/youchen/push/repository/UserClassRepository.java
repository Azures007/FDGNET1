package com.youchen.push.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserClassRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<String> findUserIdsByClassId(Integer classId) {
        String sql = "SELECT user_id FROM (\n" +
                "  SELECT b.user_id FROM t_sys_class_group_leader_rel a JOIN t_sys_personnel_info b ON a.personnel_id = b.personnel_id WHERE a.class_id = ?1\n" +
                "  UNION ALL\n" +
                "  SELECT b.user_id FROM t_sys_class_personnel_rel a JOIN t_sys_personnel_info b ON a.personnel_id = b.personnel_id WHERE a.class_id = ?2\n" +
                ") t GROUP BY user_id";
        @SuppressWarnings("unchecked")
        List<Object> rows = entityManager.createNativeQuery(sql)
                .setParameter(1, classId)
                .setParameter(2, classId)
                .getResultList();
        return rows.stream().map(o -> o == null ? null : String.valueOf(o))
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toList());
    }

    public List<String> findUserIdsByBaseLineAndRole(String baseId, String lineId, String roleCode) {
        String sql = "select cast(u.id as varchar) user_id\n" +
                "from tb_user u\n" +
                "join t_sys_role_user ru on cast(u.id as varchar)=ru.user_id\n" +
                "join t_sys_role r on ru.role_id=r.role_id\n" +
                "join t_sys_user_detail ud on cast(u.id as varchar)=ud.user_id\n" +
                "where r.role_code=?1 and ud.nc_pk_org=?2 and ud.nc_cwkid=?3";
        @SuppressWarnings("unchecked")
        List<Object> rows = entityManager.createNativeQuery(sql)
                .setParameter(1, roleCode)
                .setParameter(2, baseId)
                .setParameter(3, lineId)
                .getResultList();
        return rows.stream().map(o -> o == null ? null : String.valueOf(o))
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toList());
    }
}


