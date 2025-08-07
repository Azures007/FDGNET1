package org.thingsboard.server.dao.mes.ncOrder;

import org.thingsboard.server.common.data.mes.ncOrder.NcTBusOrderHead;

import java.util.List;
import java.util.Optional;

public interface NcTBusOrderHeadService {
    List<NcTBusOrderHead> findAll();
    Optional<NcTBusOrderHead> findById(Integer id);
    NcTBusOrderHead save(NcTBusOrderHead entity);
    void deleteById(Integer id);
    // 新增根据cmoid查询和更新的方法
    NcTBusOrderHead findByCmoid(String cmoid);
    /**
     *  根据cmoid更新
     * @param cmoid
     * @param entity
     * @return
     */
    NcTBusOrderHead updateByCmoid(String cmoid, NcTBusOrderHead entity);

    /**
     *  批量根据cmoid更新
     * @param entitys
     */
    void updateByCmoidBatch(List<NcTBusOrderHead> entitys);

    /**
     *  批量删除订单（软删除，设置isDeleted=1）
     * @param cpmohids 订单id列表
     */
    void softDeleteBatchByCpmohids(List<String> cpmohids);
}
