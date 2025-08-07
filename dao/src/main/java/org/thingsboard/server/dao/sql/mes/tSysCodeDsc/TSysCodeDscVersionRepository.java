package org.thingsboard.server.dao.sql.mes.tSysCodeDsc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDscVersion;

import java.util.List;

/**
 * @author cms
 * @version V1.0
 * @date 2022/4/12 18:55
 * @Description:
 */
public interface TSysCodeDscVersionRepository extends JpaRepository<TSysCodeDscVersion,Integer> {

    List<TSysCodeDscVersion> findByCodeClId(String codeClId);

    List<TSysCodeDscVersion> findByCodeClIdAndEnabledSt(String codeClId,String enabledSt);

    List<TSysCodeDscVersion> findByCodeClIdAndCodeDsc(String codeClId,String codeDsc);

    List<TSysCodeDscVersion> findByCodeClIdAndCodeDscAndEnabledSt(String codeClId,String codeDsc,String enabledSt);

}
