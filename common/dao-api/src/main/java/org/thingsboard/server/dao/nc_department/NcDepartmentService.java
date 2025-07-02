package org.thingsboard.server.dao.nc_department;

import org.thingsboard.server.common.data.nc_department.NcDepartment;
import java.util.List;

public interface NcDepartmentService {
    void saveOrUpdateBatchByCdeptid(List<NcDepartment> entitys);
    void deleteBatchByIds(List<String> ids);
} 