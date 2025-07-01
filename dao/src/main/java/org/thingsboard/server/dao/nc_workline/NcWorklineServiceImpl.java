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

        // 收集所有的cwkid
        List<String> cwkids = entitys.stream()
                .map(NcWorkline::getCwkid)
                .collect(Collectors.toList());

        // 批量查询已存在的记录
        List<NcWorkline> existingEntities = repository.findByCwkids(cwkids);
        Map<String, NcWorkline> existingMap = existingEntities.stream()
                .collect(Collectors.toMap(NcWorkline::getCwkid, entity -> entity));

        // 准备要保存的实体列表
        List<NcWorkline> entitiesToSave = new ArrayList<>();

        for (NcWorkline entity : entitys) {
            NcWorkline existing = existingMap.get(entity.getCwkid());
            if (existing != null) {
                entity.setId(existing.getId());
            }
            entitiesToSave.add(entity);
        }

        // 批量保存
        repository.saveAll(entitiesToSave);
    }

    @Override
    public List<NcWorkline> findAll() {
        return repository.findAll();
    }

    @Override
    public List<NcWorkline> findByPkOrg(String pkOrg) {
        return repository.findByPkOrg(pkOrg);
    }

    @Override
    public List<NcWorkline> findByVwkcodeOrVwknameLike(String keyword) {
        return repository.findByVwkcodeOrVwknameLike(keyword);
    }
}
