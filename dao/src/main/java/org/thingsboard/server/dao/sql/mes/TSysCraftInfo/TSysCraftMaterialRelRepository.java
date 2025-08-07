package org.thingsboard.server.dao.sql.mes.TSysCraftInfo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.thingsboard.server.common.data.mes.sys.TSysCraftMaterialRel;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/5/26 09:29
 * @Description:
 */
public interface TSysCraftMaterialRelRepository extends JpaRepository<TSysCraftMaterialRel,Integer> {
    List<TSysCraftMaterialRel> findByCraftId(Integer craftId, Sort sort);

    List<TSysCraftMaterialRel> findByMaterialCode(String craftId);

    @Transactional
    @Modifying
    void deleteByCraftId(Integer craftId);
}
