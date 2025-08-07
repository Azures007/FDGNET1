package org.thingsboard.server.dao.mes.ncDepartment;

import org.thingsboard.server.common.data.mes.ncDepartment.NcDepartment;
import java.util.List;

public interface NcDepartmentService {
    void saveOrUpdateBatchByCdeptid(List<NcDepartment> entitys);
}
