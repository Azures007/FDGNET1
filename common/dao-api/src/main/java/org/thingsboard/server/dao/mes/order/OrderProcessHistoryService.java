package org.thingsboard.server.dao.mes.order;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.dao.mes.dto.TBusOrderProcessHistoryDto;

import java.util.List;

public interface OrderProcessHistoryService {
    /**
     * 返回订单报工/盘点历史表
     * @return
     */
    Page<TBusOrderProcessHistory> tBusOrderProcessHistoryList(Integer current, Integer size, TBusOrderProcessHistoryDto tBusOrderProcessHistoryDto);

    //获取报工历史记录表记录
    List<TBusOrderProcessHistory> getBgOrderProcessRecords(Integer orderProcessId);
    List<TBusOrderProcessHistory> getBgOrderProcessRecords(Integer orderProcessId, String recordType);
    List<TBusOrderProcessHistory> getBgOrderProcessRecords(Integer orderProcessId, String recordType, String recordTypeBg);

    //获取盘点历史记录表记录
    List<TBusOrderProcessHistory> getPdOrderProcessRecords(Integer orderProcessId);
    List<TBusOrderProcessHistory> getPdOrderProcessRecords(Integer orderProcessId, String recordType);
    List<TBusOrderProcessHistory> getPdOrderProcessRecords(Integer orderProcessId, String recordType, String recordTypePd);

    //获取子级的记录记录表
    List<TBusOrderProcessHistory> getBgOrderProcessHistorys(Integer orderProcessHistoryParentId);
    List<TBusOrderProcessHistory> getPdOrderProcessHistorys(Integer orderProcessHistoryParentId);

    /**
     * 保存订单报工/盘点历史表
     * @param tBusOrderProcessHistory
     */
    void saveTBusOrderProcessHistory(TBusOrderProcessHistory tBusOrderProcessHistory);

    /**
     * 删除订单报工/盘点历史表
     * @param orderPPBomId
     */
    void deleteTBusOrderProcessHistory(Integer orderPPBomId);
}
