package org.thingsboard.server.dao.tSysQualityReportCategory;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysProcessInfo;
import org.thingsboard.server.common.data.TSysQualityReportCategory;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.dto.SysQualityReportCategoryDto;
import org.thingsboard.server.dao.dto.TSysProcessInfoDto;
import org.thingsboard.server.dao.dto.TSysQualityReportCategorySearchDto;
import org.thingsboard.server.dao.vo.PageVo;

import java.util.List;

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
