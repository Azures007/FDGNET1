package org.thingsboard.server.dao.chargingbasket;

import org.thingsboard.server.common.data.TBusOrderBindCode;
import org.thingsboard.server.common.data.TSysChargingBasket;
import org.thingsboard.server.dao.dto.OrderBindCodeDto;
import org.thingsboard.server.dao.dto.PageChargingBasketDto;
import org.thingsboard.server.dao.vo.OrderBindCodeVo;
import org.thingsboard.server.dao.vo.PageVo;

import java.util.List;

public interface ChargingBasketService {
    /**
     * 新增/修改接口
     * @param tSysChargingBasket
     */
    void update(TSysChargingBasket tSysChargingBasket);

    TSysChargingBasket getById(Integer id);

    PageVo<TSysChargingBasket> pageChargingBasket(Integer current, Integer size, PageChargingBasketDto pageChargingBasketDto);

    void delete(Integer id);

    List<TSysChargingBasket> list();

    /**
     * 扫码绑定信息表接口（产后报工扫码）
     * @param tBusOrderBindCode
     */
    void bindCheckMes(TBusOrderBindCode tBusOrderBindCode);

    Boolean isBindEnabled(Integer craftId, Integer processId, String key);

    /**
     * 接单扫码提交
     * @param tBusOrderBindCodeList
     */
//    void bindCheckMesStartTask(List<TBusOrderBindCode> tBusOrderBindCodeList);

    /**
     * 接单扫码提交(投入扫码提交)
     * @param orderBindCodeDtoList
     */
    void bindCheckMesStartTask(List<OrderBindCodeDto> orderBindCodeDtoList);

    /**
     * 接单扫码获取订单
     * @param bindCodeNumber
     */
    OrderBindCodeVo queryOrderBindCode(String bindCodeNumber, String orderNo);

    String getQR(String code) throws Exception;
    /**
     * 通过料框编码获取料框信息
     * @param code
     */
    TSysChargingBasket getByCode(String code);

    void unBind(String bindCodeNumber);
}
