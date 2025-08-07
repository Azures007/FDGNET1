package org.thingsboard.server.dao.mes.tSysQualityCategory;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCategory;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCategoryConfig;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlanConfig;
import org.thingsboard.server.dao.mes.dto.TSysQualityCategoryDto;
import org.thingsboard.server.dao.mes.vo.TSysQualityCategoryVo;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/6/27 17:19:31
 */
public interface TSysQualityCategoryService {

    /**
     * 返回质检分类列表
     * @return
     */
    Page<TSysQualityCategory> tSysQualityCategoryList(Integer current, Integer size, String sortField, String sortOrder, TSysQualityCategoryDto tSysQualityCategoryDto);

    /**
     * 返回质检分类列表
     * @return
     */
    Page<TSysQualityPlanConfig> getTSysQualityCategoryListToPlan(Integer current, Integer size, TSysQualityCategoryDto tSysQualityCategoryDto);


    /**
     * 保存质检类目信息
     * @param tSysQualityCategory
     * @param tSysQualityCategoryConfigList
     */
    void saveTSysQualityCategoryAndConfig(TSysQualityCategory tSysQualityCategory, List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList);

    /**
     * 保存质检类目表
     * @param tSysQualityCategory
     */
    void saveTSysQualityCategory(TSysQualityCategory tSysQualityCategory);

    /**
     * 保存质检类目配置表
     * @param categoryId
     * @param tSysQualityCategoryConfigList
     */
    void saveTSysQualityCategoryConfig(Integer categoryId, List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList);

    /**
     * 根据ID删除质检类目
     * @param categoryId
     */
    void deleteTSysQualityCategory(Integer categoryId);

    /**
     * 根据ID获取质检类目详情
     * @param categoryId
     * @return
     */
    TSysQualityCategoryVo getQualityCategoryById(Integer categoryId);


}
