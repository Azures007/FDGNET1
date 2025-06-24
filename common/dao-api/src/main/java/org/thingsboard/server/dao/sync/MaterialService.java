package org.thingsboard.server.dao.sync;

import org.thingsboard.server.common.data.TSyncMaterial;
import org.thingsboard.server.dao.dto.ListMaterialDto;
import org.thingsboard.server.dao.dto.TSyncMaterialSaveDto;
import org.thingsboard.server.dao.vo.ListMaterialFiterVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.TSyncMaterialVo;

public interface MaterialService {
    /**
     * 分页查询物料表数据
     * @param current
     * @param size
     * @param listMaterialDto
     * @return
     */
    PageVo<TSyncMaterialVo> listMaterial(Integer current, Integer size, ListMaterialDto listMaterialDto);

    /**
     * 新增/修改
     * @param tSyncMaterial
     */
    void update(TSyncMaterial tSyncMaterial);

    /**
     * 新增/修改
     * @param tSyncMaterialSaveDto
     */
    void update(TSyncMaterialSaveDto tSyncMaterialSaveDto);

    TSyncMaterial getById(Integer id);

    TSyncMaterialVo getById2(Integer id);


    void delete(Integer id);

    /**
     * 物料同步
     * @param name
     */
    void sync(String name);

    /**
     * 主产品列表
     * @param materialCode
     * @param craftId
     * @return
     */
    ListMaterialFiterVo listMaterialFiter(String materialCode, Integer craftId, Integer kdOrgId, Integer kdDeptId, Integer current, Integer size);
}
