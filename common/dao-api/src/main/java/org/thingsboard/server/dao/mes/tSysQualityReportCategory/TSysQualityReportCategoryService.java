package org.thingsboard.server.dao.mes.tSysQualityReportCategory;

import org.thingsboard.server.dao.mes.dto.SysQualityReportCategoryDto;
import org.thingsboard.server.dao.mes.dto.TSysQualityReportCategorySearchDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

/**
 * @Auther: l
 * @Date: 2022/4/20 18:02
 * @Description:工序管理接口
 */
public interface TSysQualityReportCategoryService {

    void saveCategory(SysQualityReportCategoryDto categoryDto);

    SysQualityReportCategoryDto categoryDetail(Integer id);

    void delete(Integer id);

    PageVo<SysQualityReportCategoryDto> getCategoryList(Integer current, Integer size, TSysQualityReportCategorySearchDto searchDto);

    void enableCategory(Integer processId, Integer enable, String name);
}
