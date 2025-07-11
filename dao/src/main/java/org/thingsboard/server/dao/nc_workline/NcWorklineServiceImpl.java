package org.thingsboard.server.dao.nc_workline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.nc_workline.NcWorkline;
import org.thingsboard.server.dao.sql.nc_workline.NcWorklineRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NcWorklineServiceImpl implements NcWorklineService {
    @Autowired
    private NcWorklineRepository repository;

    @Override
    @Transactional
    public void saveOrUpdateBatchByCwkid(List<NcWorkline> entitys) {
        if (entitys == null || entitys.isEmpty()) {
            return;
        }

        repository.saveAll(entitys);
    }

    @Override
    public List<NcWorkline> findAll() {
        return repository.findByStatus("生效");
    }

    @Override
    public List<NcWorkline> findByPkOrg(String pkOrg) {
        return repository.findByPkOrgAndStatus(pkOrg, "生效");
    }

    @Override
    public List<NcWorkline> findByVwkcodeOrVwknameLike(String keyword) {
        return repository.findByVwkcodeOrVwknameLike(keyword);
    }
    @Override
    public List<NcWorkline> findAllByCwkids(List<String> cwkids) {
        return repository.findByCwkidInAndStatus(cwkids, "生效");
    }
}
