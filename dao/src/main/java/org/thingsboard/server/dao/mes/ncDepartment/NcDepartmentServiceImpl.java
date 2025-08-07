package org.thingsboard.server.dao.mes.ncDepartment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.ncDepartment.NcDepartment;
import org.thingsboard.server.dao.sql.mes.ncDepartment.NcDepartmentRepository;

import java.util.List;

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
        repository.saveAll(entitys);
    }
}
