package org.thingsboard.server.dao.nc_org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.nc_org.NcOrganization;
import org.thingsboard.server.dao.sql.nc_org.NcOrganizationRepository;

import java.util.List;

@Service
public class NcOrganizationServiceImpl implements NcOrganizationService {
    @Autowired
    private NcOrganizationRepository repository;

    @Override
    @Transactional
    public void saveOrUpdateBatchByPkOrg(List<NcOrganization> entitys) {
        for (NcOrganization entity : entitys) {
            NcOrganization old = repository.findByPkOrg(entity.getPkOrg());
            if (old != null) {
                entity.setId(old.getId());
            }
            repository.save(entity);
        }
    }

    @Override
    public List<NcOrganization> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteBatchByIds(List<String> ids) {
        repository.softDeleteBatchByIds(ids);
    }
} 