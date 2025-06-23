package org.thingsboard.server.dao.nc_workline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.nc_workline.NcWorkline;
import org.thingsboard.server.dao.sql.nc_workline.NcWorklineRepository;

import java.util.List;

@Service
public class NcWorklineServiceImpl implements NcWorklineService {
    @Autowired
    private NcWorklineRepository repository;

    @Override
    @Transactional
    public void saveOrUpdateBatchByCwkid(List<NcWorkline> entitys) {
        for (NcWorkline entity : entitys) {
            NcWorkline old = repository.findByCwkid(entity.getCwkid());
            if (old != null) {
                entity.setId(old.getId());
            }
            repository.save(entity);
        }
    }
} 