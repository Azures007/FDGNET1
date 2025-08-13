package org.thingsboard.server.dao.sql.mes.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.thingsboard.server.common.data.mes.sys.TSysDemo;

import java.util.Optional;

public interface TSysDemoRepository extends JpaRepository<TSysDemo, Integer>, JpaSpecificationExecutor<TSysDemo> {
    /**
     * 根据样例编码查询样例信息
     * @param demoNumber
     * @return
     */
    boolean existsByDemoNumber(String demoNumber);

    /**
     * 根据样例编码查询样例信息
     * @param demoNumber 样例编码
     * @param demoId 样例id
     * @return
     */
    Optional<TSysDemo> findByDemoNumberAndDemoIdNot(String demoNumber, Integer demoId);
}
