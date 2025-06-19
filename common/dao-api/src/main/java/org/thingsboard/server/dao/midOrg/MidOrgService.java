package org.thingsboard.server.dao.midOrg;

import org.thingsboard.server.common.data.MidOrg;
import org.thingsboard.server.dao.dto.MidOrgDto;
import org.thingsboard.server.dao.vo.PageVo;

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
