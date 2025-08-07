package org.thingsboard.server.dao.nc_workline;

import org.thingsboard.server.common.data.mes.ncWorkline.NcWorkline;
import java.util.List;

public interface NcWorklineService {
    void saveOrUpdateBatchByCwkid(List<NcWorkline> entitys);
    List<NcWorkline> findAll();
    List<NcWorkline> findByPkOrg(String pkOrg);
    List<NcWorkline> findByVwkcodeOrVwknameLike(String keyword);

    List<NcWorkline> findAllByCwkids(List<String> cwkids);
}
