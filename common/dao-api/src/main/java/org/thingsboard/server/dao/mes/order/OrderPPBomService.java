package org.thingsboard.server.dao.mes.order;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPPBom;
import org.thingsboard.server.dao.mes.dto.TBusOrderPPBomDto;

public interface OrderPPBomService {
    /**
     * 返回订单用料清单
     * @return
     */
    Page<TBusOrderPPBom> tBusOrderPPBomList(Integer current, Integer size, TBusOrderPPBomDto tBusOrderPPBomDto);

    /**
     * 保存订单用料清单
     * @param tBusOrderPPBom
     */
    void saveTBusOrderPPBom(TBusOrderPPBom tBusOrderPPBom);

    /**
     * 删除订单用料清单
     * @param orderPPBomId
     */
    void deleteTBusOrderPPBom(Integer orderPPBomId);

    /**
     * 判断子项物料属性为“自制”且子项BOM版本为非空，如果是则为真
     * 主要用于判断用料是否为蟹肉棒
     * @param orderPPBomId
     */
    boolean getMainPpbomFlag(Integer orderPPBomId);
}
