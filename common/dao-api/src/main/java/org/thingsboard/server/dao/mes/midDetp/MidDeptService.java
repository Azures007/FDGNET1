package org.thingsboard.server.dao.mes.midDetp;

import org.thingsboard.server.common.data.mes.mid.MidDept;
import org.thingsboard.server.dao.mes.dto.MidDeptDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

public interface MidDeptService {

    /**
     * 生产车间泪飙
     *
     * @param current
     * @param size
     * @param midDeptDto
     * @return
     */
    PageVo<MidDept> listMidDept(Integer current, Integer size, MidDeptDto midDeptDto) throws Exception;
}
