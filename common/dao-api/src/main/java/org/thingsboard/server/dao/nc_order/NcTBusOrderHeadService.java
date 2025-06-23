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
    NcTBusOrderHead updateByCmoid(String cmoid, NcTBusOrderHead entity);
    // 批量根据cmoid更新
    void updateByCmoidBatch(List<NcTBusOrderHead> entitys);
}
