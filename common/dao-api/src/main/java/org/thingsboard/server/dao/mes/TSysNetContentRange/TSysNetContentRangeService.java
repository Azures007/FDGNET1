package org.thingsboard.server.dao.mes.TSysNetContentRange;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysNetContentRange;
import org.thingsboard.server.dao.mes.dto.TSysNetContentRangeDto;
import org.thingsboard.server.dao.mes.vo.TSysNetContentRangeVo;

public interface TSysNetContentRangeService {
    /**
     * 净含量范围列表
     * @param toString
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysNetContentRangeDto
     * @return
     */
    Page<TSysNetContentRangeVo> tSysNetContentRangeList(String toString, Integer current, Integer size, String sortField, String sortOrder, TSysNetContentRangeDto tSysNetContentRangeDto);

    /**
     * 保存净含量范围
     * @param tSysNetContentRange
     * @return
     */
    TSysNetContentRange saveNetContentRange(TSysNetContentRange tSysNetContentRange);

    /**
     * 删除净含量范围
     * @param id
     */
    void deleteNetContentRangeById(Integer id);

    /**
     * 根据ID查询净含量范围明细
     * @param id
     * @return
     */
    TSysNetContentRange getNetContentRangeById(Integer id);

    /**
     * 启用/禁用净含量范围记录
     * @param id
     * @param status
     * @return
     */
    TSysNetContentRange updateNetContentRangeStatus(Integer id, String status);

}