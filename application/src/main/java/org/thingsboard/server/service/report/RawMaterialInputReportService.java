package org.thingsboard.server.service.report;

import org.thingsboard.server.dao.mes.dto.RawMaterialInputQueryDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.RawMaterialInputReportVo;

/**
 * 原料投入报表服务接口
 */
public interface RawMaterialInputReportService {

    /**
     * 查询原料投入报表
     * @param current 页码
     * @param size 每页数量
     * @param queryDto 查询条件
     * @return 分页结果
     */
    PageVo<RawMaterialInputReportVo> queryRawMaterialInputReport(Integer current, Integer size, RawMaterialInputQueryDto queryDto);

}
