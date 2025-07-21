package org.thingsboard.server.dao.tSysQualityCtrl;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.dao.dto.TSysQualityCtrlDto;
import org.thingsboard.server.dao.vo.TSysQualityCtrlVo;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/18 11:46:51
 */
public interface TSysQualityCtrlService {

    /**
     * 返回质检管控列表
     * @return
     */
    Page<TSysQualityCtrl> tSysQualityCtrlList(Integer current, Integer size, String sortField, String sortOrder, TSysQualityCtrlDto tSysQualityCtrlDto);


    /**
     * 保存质检管控信息
     * @param tSysQualityCtrl
     * @param tSysQualityCtrlDetailList
     */
    void saveTSysQualityCtrlAndDetail(TSysQualityCtrl tSysQualityCtrl, List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList);

    /**
     * 保存质检管控表
     * @param tSysQualityCtrl
     */
    void saveTSysQualityCtrl(TSysQualityCtrl tSysQualityCtrl);

    /**
     * 保存质检管控配置表
     * @param categoryId
     * @param tSysQualityCtrlDetailList
     */
    void saveTSysQualityCtrlDetails(Integer categoryId, List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList);

    /**
     * 根据ID删除质检管控
     * @param ctrlId
     */
    void deleteTSysQualityCtrl(Integer ctrlId);

    /**
     * 根据ID获取质检管控详情
     * @param planId
     * @return
     */
    TSysQualityCtrlVo getQualityPlanById(Integer planId);


}
