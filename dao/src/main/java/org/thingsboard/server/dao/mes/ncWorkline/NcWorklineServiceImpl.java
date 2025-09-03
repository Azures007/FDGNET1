package org.thingsboard.server.dao.mes.ncWorkline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.ncWorkline.NcWorkline;
import org.thingsboard.server.dao.sql.mes.ncWorkline.NcWorklineRepository;

import java.util.List;

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

    @Override
    public String getBaseIdByLineId(String cwkid) {
        NcWorkline w = repository.findByCwkid(cwkid);
        return w != null ? w.getPkOrg() : null;
    }
}
