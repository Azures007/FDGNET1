package org.thingsboard.server.dao.nc_department;

import org.thingsboard.server.common.data.mes.ncDepartment.NcDepartment;
import java.util.List;

public interface NcDepartmentService {
    void saveOrUpdateBatchByCdeptid(List<NcDepartment> entitys);
}
