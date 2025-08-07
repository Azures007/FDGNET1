package org.thingsboard.server.dao.order;

import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.mes.sys.TSysCraftInfo;
import org.thingsboard.server.dao.dto.OrderChangeClassSaveDto;
import org.thingsboard.server.dao.dto.OrderStartOrderSaveDto;

import java.util.List;

public interface OrderBackendService {
    /**
     * 订单接单开工
     */
    void startOrder(Integer orderId, Integer craftId, String craftDesc) throws Exception;
    /**
     * 订单批量接单开工
     */
    void startOrderBatch(List<OrderStartOrderSaveDto> saveDtoList) throws Exception;
    /**
     * 订单获取工序的班别
     */
    List<TSysClass> getOrderClassInfo(Integer orderId);
    /**
     * 班别变更
     */
    void changeOrderClass(OrderChangeClassSaveDto saveDto);

    TSysCraftInfo getCraftInfoByMaterial(String materialNumber);

    /**
     * 更新匹配工艺路线
     * @param orderMatching
     */
    void setOrderHeadOrderMatching(Integer orderId,String orderMatching);
}
