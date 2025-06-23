package org.thingsboard.server.dao.nc_workline;

import org.thingsboard.server.common.data.nc_workline.NcWorkline;
import java.util.List;

public interface NcWorklineService {
    void saveOrUpdateBatchByCwkid(List<NcWorkline> entitys);
} 