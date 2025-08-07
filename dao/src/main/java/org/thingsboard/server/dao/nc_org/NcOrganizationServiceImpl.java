package org.thingsboard.server.dao.nc_org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.ncOrg.NcOrganization;
import org.thingsboard.server.dao.sql.mes.ncOrg.NcOrganizationRepository;

import java.util.List;

@Service
public class NcOrganizationServiceImpl implements NcOrganizationService {
    @Autowired
    private NcOrganizationRepository repository;

    @Override
    @Transactional
    public void saveOrUpdateBatchByPkOrg(List<NcOrganization> entitys) {
        if (entitys == null || entitys.isEmpty()) {
            return;
        }
        repository.saveAll(entitys);
    }

    @Override
    public List<NcOrganization> findAll() {
        return repository.findByStatus("生效");
    }
    @Override
    public List<NcOrganization> findAllByPkOrgs(List<String> pkOrg) {
        return repository.findByPkOrgInAndStatus(pkOrg, "生效");
    }
}
