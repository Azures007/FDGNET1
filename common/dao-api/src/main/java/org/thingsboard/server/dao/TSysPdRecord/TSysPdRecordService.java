package org.thingsboard.server.dao.TSysPdRecord;


import org.springframework.data.domain.Page;
import org.thingsboard.server.dao.dto.TSysPdRecordDto;
import org.thingsboard.server.dao.vo.TSysPdRecordVo;

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
}

