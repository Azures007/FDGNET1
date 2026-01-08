package org.thingsboard.server.service.report;

import org.thingsboard.server.dao.mes.dto.RawMaterialInputQueryDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.RawMaterialInputReportVo;
import org.thingsboard.server.vo.RawMaterialInputReportExcelVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface RawMaterialInputReportService {

    /**
     * 查询原料投入报表
     * @param current 页码
     * @param size 每页数量
     * @param queryDto 查询条件
     * @return 分页结果
     */
    PageVo<RawMaterialInputReportVo> queryRawMaterialInputReport(Integer current, Integer size, RawMaterialInputQueryDto queryDto);

    /**
     * 导出原料投入报表
     * @param current 页码
     * @param size 每页数量
     * @param queryDto 查询条件
     * @param response HTTP响应对象
     */
    void exportRawMaterialInputReport(Integer current, Integer size, RawMaterialInputQueryDto queryDto, HttpServletResponse response);

}