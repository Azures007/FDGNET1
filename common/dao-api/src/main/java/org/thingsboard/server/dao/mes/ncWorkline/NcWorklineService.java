package org.thingsboard.server.dao.mes.ncWorkline;

import org.thingsboard.server.common.data.mes.ncWorkline.NcWorkline;
import java.util.List;

public interface NcWorklineService {
    void saveOrUpdateBatchByCwkid(List<NcWorkline> entitys);
    List<NcWorkline> findAll(String userId);
    List<NcWorkline> findByPkOrg(String pkOrg);
    List<NcWorkline> findByVwkcodeOrVwknameLike(String keyword);

    List<NcWorkline> findAllByCwkids(List<String> cwkids);

    String getBaseIdByLineId(String cwkid);
}
