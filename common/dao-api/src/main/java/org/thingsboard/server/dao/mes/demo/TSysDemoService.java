package org.thingsboard.server.dao.mes.demo;

import org.thingsboard.server.common.data.mes.sys.TSysDemo;
import org.thingsboard.server.dao.mes.dto.TSysDemoDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

public interface TSysDemoService {

    /**
     * 查询样例列表
     * @param current
     * @param size
     * @param tSysDemoDto
     * @return
     */
    PageVo<TSysDemo> tSysDemoList(Integer current, Integer size, TSysDemoDto tSysDemoDto);

    /**
     * 根据id查询样例
     * @param demoId
     * @return
     */
    TSysDemo getTSysDemoById(Integer demoId);

    /**
     * 新增或更新样例（demoId为空或<=0时为新增，否则为更新）
     * @param tSysDemo
     */
    void saveOrUpdateTSysDemo(TSysDemo tSysDemo);

    /**
     * 删除样例
     * @param demoId
     */
    void deleteTSysDemo(Integer demoId);


}
