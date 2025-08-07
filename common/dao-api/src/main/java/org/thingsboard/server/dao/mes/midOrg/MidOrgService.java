package org.thingsboard.server.dao.mes.midOrg;

import org.thingsboard.server.common.data.mes.mid.MidOrg;
import org.thingsboard.server.dao.mes.dto.MidOrgDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

public interface MidOrgService {

    /**
     * 生产组织泪飙
     *
     * @param current
     * @param size
     * @return
     */
    PageVo<MidOrg> listMidOrg(Integer current, Integer size, MidOrgDto midOrgDto) throws Exception;
}
