package org.thingsboard.server.dao.order;

import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessDeviceRel;

import java.util.List;

public interface OrderProcessDeviceRelService {
    /**
     * 获取或创建设备分组id
     * @param orderProcessId:工序执行id
     * @param deviceIds：设备id
     * @return
     */
    String createdDeviceGroupId(Integer orderProcessId,List<Integer> deviceIds);

    /**
     * 获取设备分组id
     * @param orderProcessId:工序执行id
     * @param deviceIds：设备id
     * @return
     */
    String getDeviceGroupId(Integer orderProcessId,List<Integer> deviceIds);

    List<TBusOrderProcessDeviceRel> findByDeviceGroupId(String deviceGroupId);

    /**
     * 获取机台号分组的机台名称，用，符号拼接
     * @param deviceGroupId：机台号分组标识Id
     * @return
     */
    String getDeviceGroupNames(String deviceGroupId);

}
