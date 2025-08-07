package org.thingsboard.server.dao.order;

import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessPersonRel;

import java.util.List;

public interface OrderProcessPersonRelService {

    /**
     * 获取或创建操作员分组id
     * @param orderProcessId:工序执行id
     * @param deviceIds：设备id
     * @return
     */
    String createdPersonGroupId(Integer orderProcessId, List<Integer> deviceIds);

    /**
     * 获取操作员分组id
     * @param orderProcessId:工序执行id
     * @param deviceIds：设备id
     * @return
     */
    String getPersonGroupId(Integer orderProcessId, List<Integer> deviceIds);

    /**
     * 获取订单报工操作员附表
     * @param devicePersonGroupId
     * @return
     */
    List<TBusOrderProcessPersonRel> findByPersonGroupId(String devicePersonGroupId);

    /**
     * 获取操作员分组的人员名称，用，符号拼接
     * @param devicePersonGroupId：操作员分组标识Id
     * @return
     */
    String getPersonGroupNames(String devicePersonGroupId);

}
