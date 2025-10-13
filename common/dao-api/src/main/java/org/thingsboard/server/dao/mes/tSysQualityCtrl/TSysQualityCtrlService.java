package org.thingsboard.server.dao.mes.tSysQualityCtrl;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCtrl;
import org.thingsboard.server.common.data.mes.sys.TSysQualityCtrlDetail;
import org.thingsboard.server.dao.mes.dto.TSysQualityCtrlDto;
import org.thingsboard.server.dao.mes.vo.TSysQualityCtrlVo;

import java.util.Date;
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
    Page<TSysQualityCtrl> tSysQualityCtrlList(String userId,Integer current, Integer size, String sortField, String sortOrder, TSysQualityCtrlDto tSysQualityCtrlDto);


    /**
     * 保存质检管控信息
     * @param tSysQualityCtrl
     * @param tSysQualityCtrlDetailList
     */
    TSysQualityCtrlVo saveTSysQualityCtrlAndDetail(TSysQualityCtrl tSysQualityCtrl, List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList);
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

    /**
     * 根据ID获取质检管控及明细信息
     * @param id
     * @return
     */
    TSysQualityCtrlVo getQualityCtrlById(Integer id);

    /**
     * 获取质检管控审核列表
     * @return
     */
    Page<TSysQualityCtrl> tSysQualityCtrlCheckList(String userId,Integer current, Integer size, String sortField, String sortOrder, TSysQualityCtrlDto tSysQualityCtrlDto);

    /**
     * 根据创建时间范围和产线查询质检管控品名列表
     * @param userId 用户ID
     * @param current 页码
     * @param size 每页数量
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @param createTime 创建时间
     * @return
     */
    Page<TSysQualityCtrl> qualityCtrlNameList(String userId, Integer current, Integer size, String sortField, String sortOrder, Date createTime);
}
