package org.thingsboard.server.dao.midDetp;

import org.thingsboard.server.common.data.MidDept;
import org.thingsboard.server.dao.dto.MidDeptDto;
import org.thingsboard.server.dao.vo.PageVo;

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
