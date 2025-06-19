package org.thingsboard.server.service.order;

import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysCraftInfo;
import org.thingsboard.server.dao.dto.OrderChangeClassSaveDto;
import org.thingsboard.server.dao.dto.OrderStartOrderSaveDto;
import org.thingsboard.server.dao.dto.TBusOrderDto;
import org.thingsboard.server.dao.dto.TBusOrderHeadDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface OrderHeadExcelService {

    void download(Integer current, Integer size, TBusOrderHeadDto tBusOrderHeadDto, HttpServletResponse response) throws IOException;
    void download(Integer current, Integer size, TBusOrderDto tBusOrderDto, HttpServletResponse response) throws IOException;
    void downloadOrder(Integer current, Integer size, TBusOrderDto tBusOrderDto, HttpServletResponse response) throws Exception;

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

    TSysCraftInfo getCraftInfoByMaterial(String materialNumber,Integer orderId);

    /**
     * 更新匹配工艺路线
     * @param orderMatching
     */
    void setOrderHeadOrderMatching(Integer orderId,String orderMatching);
}
