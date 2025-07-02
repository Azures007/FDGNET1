package org.thingsboard.server.dao.nc_order;

import org.thingsboard.server.common.data.TBusOrderHead;
import org.thingsboard.server.common.data.nc_order.NcTBusOrderHead;

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
     * @param cmoids 订单明细id列表
     */
    void deleteBatchByCmoids(List<String> cmoids);
}
