package org.thingsboard.server.dao.mes.tSysAbrasiveSpecification;

import org.thingsboard.server.common.data.mes.sys.TSysAbrasiveSpecification;
import org.thingsboard.server.dao.mes.dto.tSysAbrasiveSpecification.TSysAbrasiveSpecificationDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

import java.util.List;

public interface TSysAbrasiveSpecificationService {
    /**
     * 规格磨具获取列表
     *
     * @param current
     * @param size
     * @param tSysAbrasiveSpecificationDto
     * @return
     */
    PageVo<TSysAbrasiveSpecification> getList(Integer current, Integer size, TSysAbrasiveSpecificationDto tSysAbrasiveSpecificationDto);

    /**
     * 磨具规格查看详情
     *
     * @param abrasiveSpecificationId
     * @return
     */
    TSysAbrasiveSpecification getDetail(Integer abrasiveSpecificationId);

    /**
     * 磨具保存/新增
     *
     * @param tSysAbrasiveSpecification
     */
    void save(TSysAbrasiveSpecification tSysAbrasiveSpecification);

    /**
     * 磨具规格删除
     *
     * @param abrasiveSpecificationId
     * @param version
     * @param updatedName
     */
    void delete(Integer abrasiveSpecificationId,Integer version, String updatedName);

    /**
     * 磨具规格,状态:0:禁用, 1:启用
     *
     * @param abrasiveSpecificationId
     * @param abrasiveSpecificationStatus
     * @param updatedName
     */
    void setPickingStatus(Integer abrasiveSpecificationId, String abrasiveSpecificationStatus, String updatedName);

    /**
     * 获取磨具数据
     * @return
     */
    List<TSysAbrasiveSpecification> findAll();
}
