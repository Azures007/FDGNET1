package org.thingsboard.server.dao.sql.TSysCraftInfo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.thingsboard.server.common.data.TSysCraftProcessRel;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/21 17:20
 * @Description:
 */
public interface TSysCraftProcessRelRepository extends JpaRepository<TSysCraftProcessRel,Integer> {
    List<TSysCraftProcessRel> findByCraftId(Integer craftId, Sort sort);

    @Transactional
    @Modifying
    void deleteByCraftId(Integer craftId);

    TSysCraftProcessRel findByCraftIdAndProcessId(Integer craftId, Integer processId);
}