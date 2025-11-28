package org.thingsboard.server.dao.mes.TSysPdRecord;


import org.springframework.data.domain.Page;
import org.thingsboard.server.dao.mes.dto.TSysPdRecordDto;
import org.thingsboard.server.dao.mes.vo.TSysPdRecordVo;

import java.util.List;

/**
 * @author 许文言
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/30 10:01:36
 */
public interface TSysPdRecordService {
    /**
     * 盘点记录列表
     * @param userId
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysPdRecordDto
     * @return
     */
    Page<TSysPdRecordVo> tSysPdRecordList(String userId, Integer current, Integer size, String sortField, String sortOrder, TSysPdRecordDto tSysPdRecordDto);

    /**
     * 盘点还原记录列表
     * @param userId
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysPdRecordDto
     * @return
     */
    Page<TSysPdRecordVo> tSysPdRecordListWithSplit(String userId, Integer current, Integer size, String sortField, String sortOrder, TSysPdRecordDto tSysPdRecordDto);
    
    /**
     * 审核盘点记录
     * @param ids 盘点记录ID列表
     * @return 更新记录数
     */
    int reviewPdRecords(List<Integer> ids);
}