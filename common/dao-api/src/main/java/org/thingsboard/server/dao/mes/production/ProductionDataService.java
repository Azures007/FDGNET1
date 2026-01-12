package org.thingsboard.server.dao.mes.production;

import org.thingsboard.server.common.data.mes.vo.ProductionData;
import org.thingsboard.server.dao.mes.dto.ProductionDataQueryDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

/**
 * 投入产出比报表数据服务接口
 */
public interface ProductionDataService {

    /**
     * 查询投入产出比报表数据
     *
     * @param current  当前页码
     * @param size     页面大小
     * @param queryDto 查询条件
     * @return 分页结果
     */
    PageVo<ProductionData> queryProductionData(int current, int size, ProductionDataQueryDto queryDto);
}