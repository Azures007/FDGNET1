package org.thingsboard.server.dao.sql.mes.tSysCodeDsc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDscVersionRel;

import java.util.List;

/**
 * @author cms
 * @version V1.0
 * @date 2022/4/12 18:55
 * @Description:
 */
public interface TSysCodeDscVersionRelRepository extends JpaRepository<TSysCodeDscVersionRel,Integer> {

    List<TSysCodeDscVersionRel> findByRelId(Integer relId);

    List<TSysCodeDscVersionRel> findByVersionNo(String versionNo);

}
