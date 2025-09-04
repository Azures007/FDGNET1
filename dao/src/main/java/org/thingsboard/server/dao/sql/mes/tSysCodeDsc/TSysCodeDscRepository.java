package org.thingsboard.server.dao.sql.mes.tSysCodeDsc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;

import java.util.List;

/**
 * @author cms
 * @version V1.0
 * @date 2022/4/12 18:55
 * @Description:
 */
public interface TSysCodeDscRepository extends JpaRepository<TSysCodeDsc,Integer> {

    List<TSysCodeDsc> findByCodeClId(String codeClId);

    List<TSysCodeDsc> findByCodeClIdAndEnabledSt(String codeClId,String enabledSt);

    List<TSysCodeDsc> findByCodeClIdAndCodeDsc(String codeClId,String codeDsc);

    List<TSysCodeDsc> findByCodeClIdAndCodeDscAndEnabledSt(String codeClId,String codeDsc,String enabledSt);

    @Query("SELECT c FROM TSysCodeDsc c WHERE c.codeClId = ?1 AND c.codeValue IN ?2")
    List<TSysCodeDsc> findByCodeClIdAndCodeValueIn(String recordtype0000, List<String> recordTypes);
}
