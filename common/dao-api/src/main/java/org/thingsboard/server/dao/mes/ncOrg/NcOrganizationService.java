package org.thingsboard.server.dao.mes.ncOrg;

import org.thingsboard.server.common.data.mes.ncOrg.NcOrganization;
import java.util.List;

public interface NcOrganizationService {
    void saveOrUpdateBatchByPkOrg(List<NcOrganization> entitys);
    List<NcOrganization> findAll();
    List<NcOrganization> findAllByPkOrgs(List<String> pkOrg);
}
