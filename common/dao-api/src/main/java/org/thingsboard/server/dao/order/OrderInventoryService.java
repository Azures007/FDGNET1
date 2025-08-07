package org.thingsboard.server.dao.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.dto.OrderProcessRecordSaveDto;
import org.thingsboard.server.dao.dto.OrderProcessRecordSearchDto;
import org.thingsboard.server.dao.vo.*;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/25 19:58
 * @Description:工序盘点
 */
public interface OrderInventoryService {
    /**
     * 获取盘点用料清单集合
     * @param orderId
     * @param processId
     * @return
     * @throws JsonProcessingException
     */
    OrderProcessResult getOrderPpbom(Integer orderId, Integer processId) throws JsonProcessingException;

    /**
     * 获取盘点类型的字典集合
     * @param orderProcessId
     * @return
     */
    List<TSysCodeDsc> getOrderRecordTypePdList(Integer orderProcessId);

    List<BatchVo> getBatchList(String orderNo);

    @Transactional
    ResponseResult submit(Integer isFirst, Integer size, OrderProcessRecordSaveDto saveDto, String userId, Integer isRepeat);

    Integer getSize(OrderProcessRecordSearchDto searchDto, String toString);

    OrderProcessHistory getOrderProcessHistory(OrderProcessRecordSearchDto searchDto, String userId);

    ResponseResult<OrderHandoverVo> handOver(OrderProcessRecordSearchDto searchDto, String userId);

    List<TaskListVo> handOverOrderList(String userId);

    List<OrderProcessResult> getBillNoInfo(String orderNo);

    /**
     * 删除盘点记录
     * @param orderProcessHistoryId
     */
    void deletePDHistory(Integer orderProcessHistoryId);
}
