package org.thingsboard.server.dao.mes.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.LichengConstants;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.dao.mes.dto.TBusOrderProcessHistoryDto;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessHistoryRepository;

import java.util.List;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.role
 * @date 2022/4/23 19:43
 * @Description:
 */
@Service
@Slf4j
public class OrderProcessHistoryServiceImpl implements OrderProcessHistoryService {
    @Autowired
    OrderProcessHistoryRepository orderProcessHistoryRepository;

    @Override
    public Page<TBusOrderProcessHistory> tBusOrderProcessHistoryList(Integer current, Integer size, TBusOrderProcessHistoryDto tBusOrderProcessHistoryDto) {
        Pageable pageable= PageRequest.of(current,size);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name",ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("materialNumber",ExampleMatcher.GenericPropertyMatchers.exact());
        TBusOrderProcessHistory orderProcessHistory = new TBusOrderProcessHistory();
        BeanUtils.copyProperties(tBusOrderProcessHistoryDto,orderProcessHistory);
        Example<TBusOrderProcessHistory> example = Example.of(orderProcessHistory,matcher);
        Page<TBusOrderProcessHistory> orderProcessHistoryPage = orderProcessHistoryRepository.findAll(example,pageable);
        return orderProcessHistoryPage;
    }

    private static String busTypeBg = LichengConstants.ORDER_BUS_TYPE_BG;
    private static String busTypePd = LichengConstants.ORDER_BUS_TYPE_PD;
    private static String recordStatus0 = LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0;//正常
    private static String recordStatus1 = LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1;//删除

    @Override
    public List<TBusOrderProcessHistory> getBgOrderProcessRecords(Integer orderProcessId) {
        return orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeAndReportStatus(orderProcessId, busTypeBg, recordStatus0);
    }

    @Override
    public List<TBusOrderProcessHistory> getBgOrderProcessRecords(Integer orderProcessId, String recordType) {
        return orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeAndReportStatusAndRecordType(orderProcessId, busTypeBg, recordStatus0, recordType);
    }

    @Override
    public List<TBusOrderProcessHistory> getBgOrderProcessRecords(Integer orderProcessId, String recordType, String recordTypeBg) {
        return orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeAndReportStatusAndRecordTypeAndRecordTypeBg(orderProcessId, busTypeBg, recordStatus0, recordType, recordTypeBg);
    }

    @Override
    public List<TBusOrderProcessHistory> getPdOrderProcessRecords(Integer orderProcessId) {
        return orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeAndReportStatus(orderProcessId, busTypePd, recordStatus0);
    }

    @Override
    public List<TBusOrderProcessHistory> getPdOrderProcessRecords(Integer orderProcessId, String recordType) {
        return orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeAndReportStatusAndRecordType(orderProcessId, busTypePd, recordStatus0, recordType);
    }

    @Override
    public List<TBusOrderProcessHistory> getPdOrderProcessRecords(Integer orderProcessId, String recordType, String recordTypePd) {
        return orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeAndReportStatusAndRecordTypeAndRecordTypePd(orderProcessId, busTypePd, recordStatus0, recordType, recordTypePd);
    }

    @Override
    public List<TBusOrderProcessHistory> getBgOrderProcessHistorys(Integer orderProcessHistoryParentId) {
        return orderProcessHistoryRepository.findAllByOrderProcessHistoryParentIdAndBusTypeAndReportStatus(orderProcessHistoryParentId, busTypeBg, recordStatus0);
    }
    @Override
    public List<TBusOrderProcessHistory> getPdOrderProcessHistorys(Integer orderProcessHistoryParentId) {
        return orderProcessHistoryRepository.findAllByOrderProcessHistoryParentIdAndBusTypeAndReportStatus(orderProcessHistoryParentId, busTypePd, recordStatus0);
    }

    @Override
    public void saveTBusOrderProcessHistory(TBusOrderProcessHistory orderProcessHistory) {
//        if(orderProcessHistory.getOrderProcessHistoryId() == null) {
//            //新增
////            tBusOrderProcessHistory.setCrtUser(orderProcessHistory.getUpdateUser());
////            tBusOrderProcessHistory.setCrtTime(orderProcessHistory.getUpdateTime());
////            if(StringUtils.isBlank(orderProcessHistory.getEnabledSt())){
////                orderProcessHistory.setEnabledSt("0");
////            }
//        }
        orderProcessHistoryRepository.saveAndFlush(orderProcessHistory);
    }

    @Override
    public void deleteTBusOrderProcessHistory(Integer orderProcessHistoryId) {
        orderProcessHistoryRepository.deleteById(orderProcessHistoryId);
    }

}
