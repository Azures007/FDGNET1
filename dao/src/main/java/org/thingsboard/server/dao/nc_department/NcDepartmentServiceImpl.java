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
        repository.saveAll(entitys);
    }
}
