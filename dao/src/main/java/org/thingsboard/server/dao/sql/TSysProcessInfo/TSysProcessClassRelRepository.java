package org.thingsboard.server.dao.sql.TSysProcessInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.TSysProcessClassRel;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/21 10:53
 * @Description:
 */
public interface TSysProcessClassRelRepository extends JpaRepository<TSysProcessClassRel,Integer> {
    List<TSysProcessClassRel>  findByProcessId(Integer processId);

    @Transactional//事务的注解
    @Modifying
    void deleteByProcessId(Integer processId);
}