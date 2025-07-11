package org.thingsboard.server.dao.nc_org;

import org.thingsboard.server.common.data.nc_org.NcOrganization;
import java.util.List;

public interface NcOrganizationService {
    void saveOrUpdateBatchByPkOrg(List<NcOrganization> entitys);
    List<NcOrganization> findAll();
    List<NcOrganization> findAllByPkOrgs(List<String> pkOrg);
}
