package org.thingsboard.server.dao.nc_department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.nc_department.NcDepartment;
import org.thingsboard.server.dao.sql.nc_department.NcDepartmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NcDepartmentServiceImpl implements NcDepartmentService {
    @Autowired
    private NcDepartmentRepository repository;

    @Override
    @Transactional
    public void saveOrUpdateBatchByCdeptid(List<NcDepartment> entitys) {
        if (entitys == null || entitys.isEmpty()) {
            return;
        }

        // 收集所有的cdeptid
        List<String> cdeptids = entitys.stream()
                .map(NcDepartment::getCdeptid)
                .collect(Collectors.toList());

        // 批量查询已存在的记录
        List<NcDepartment> existingEntities = repository.findByCdeptids(cdeptids);
        Map<String, NcDepartment> existingMap = existingEntities.stream()
                .collect(Collectors.toMap(NcDepartment::getCdeptid, entity -> entity));

        // 准备要保存的实体列表
        List<NcDepartment> entitiesToSave = new ArrayList<>();

        for (NcDepartment entity : entitys) {
            NcDepartment existing = existingMap.get(entity.getCdeptid());
            if (existing != null) {
                entity.setId(existing.getId());
            }
            entitiesToSave.add(entity);
        }

        // 批量保存
        repository.saveAll(entitiesToSave);
    }
    @Transactional
    @Override
    public void deleteBatchByIds(List<String> ids) {
        repository.softDeleteBatchByIds(ids);
    }
}
