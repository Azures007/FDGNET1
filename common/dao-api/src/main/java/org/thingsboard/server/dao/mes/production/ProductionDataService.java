package org.thingsboard.server.dao.mes.production;

import org.thingsboard.server.common.data.mes.vo.ProductionData;
import org.thingsboard.server.dao.mes.dto.ProductionDataQueryDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

/**
 * 投入产出比报表数据服务接口
 */
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ProductionDataService {

    /**
     * 查询投入产出比报表数据
     *
     * @param userCwkids 用户绑定的产线ID列表
     * @param current  当前页码
     * @param size     页面大小
     * @param queryDto 查询条件
     * @return 分页结果
     */
    PageVo<ProductionData> queryProductionData(List<String> userCwkids, int current, int size, ProductionDataQueryDto queryDto);

    /**
     * 导出投入产出比报表
     *
     * @param userCwkids 用户绑定的产线ID列表
     * @param queryDto 查询条件
     * @param response 响应对象
     */
    void exportProductionData(List<String> userCwkids, ProductionDataQueryDto queryDto, HttpServletResponse response);
}