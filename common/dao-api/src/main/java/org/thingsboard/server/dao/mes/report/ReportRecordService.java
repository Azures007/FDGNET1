package org.thingsboard.server.dao.mes.report;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.dao.mes.vo.ReportRecordVo;
import org.thingsboard.server.dao.mes.dto.ReportRecordQueryDto;

import java.util.List;

/**
 * @author
 * @version V1.0
 * @Package org.thingsboard.server.dao.mes.report
 * @date 2025/12/29
 * @Description: 报工记录服务接口
 */
public interface ReportRecordService {
    
    /**
     * 分页查询报工记录列表
     * @param current 页码
     * @param size 每页大小
     * @param queryDto 查询条件
     * @return 报工记录分页数据
     */
    Page<TBusOrderProcessHistory> getReportRecordList(Integer current, Integer size, ReportRecordQueryDto queryDto);

    /**
     * 分页查询报工记录列表（VO对象）
     * @param current 页码
     * @param size 每页大小
     * @param queryDto 查询条件
     * @return 报工记录VO分页数据
     */
    Page<ReportRecordVo> getReportRecordListVo(Integer current, Integer size, ReportRecordQueryDto queryDto);
    
    /**
     * 查询报工记录列表用于导出
     * @param queryDto 查询条件
     * @return 报工记录VO列表
     */
    List<ReportRecordVo> getReportRecordListForExport(ReportRecordQueryDto queryDto);
    
    /**
     * 查询报工记录列表用于导出（优化版，使用分页查询避免内存问题）
     * @param queryDto 查询条件
     * @return 报工记录VO列表
     */
    List<ReportRecordVo> getReportRecordListForExportOptimized(ReportRecordQueryDto queryDto);
}