package org.thingsboard.server.dao.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.TBusOrderProcessDeviceRel;
import org.thingsboard.server.common.data.TsysDevice;
import org.thingsboard.server.dao.sql.order.OrderProcessDeviceRelRepository;
import org.thingsboard.server.dao.sql.tSysDevice.TSysDeviceRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.role
 * @date 2022/8/2 9:43
 * @Description:
 */
@Service
@Slf4j
public class OrderProcessDeviceRelServiceImpl implements OrderProcessDeviceRelService {
    @Autowired
    OrderProcessDeviceRelRepository orderProcessDeviceRelRepository;

    @Autowired
    TSysDeviceRepository deviceRepository;

    @Override
    public String createdDeviceGroupId(Integer orderProcessId, List<Integer> deviceIds) {
        if (deviceIds == null || deviceIds.size() == 0) {
            return "";
        }
        String deviceGroupId = orderProcessDeviceRelRepository.getDeviceGroupId(orderProcessId, deviceIds, deviceIds.size());
        if (StringUtils.isEmpty(deviceGroupId)) {
            deviceGroupId = UUID.randomUUID().toString();
            List<TBusOrderProcessDeviceRel> orderProcessDeviceRels = new ArrayList<>();
            TBusOrderProcessDeviceRel orderProcessDeviceRel = null;
            for (var deviceId: deviceIds) {
                orderProcessDeviceRel = new TBusOrderProcessDeviceRel();
                orderProcessDeviceRel.setOrderProcessId(orderProcessId);
                orderProcessDeviceRel.setDeviceId(deviceId);
                orderProcessDeviceRel.setDeviceGroupId(deviceGroupId);
                orderProcessDeviceRel.setCrtTime(new Date());
                orderProcessDeviceRel.setUpdateTime(new Date());
                orderProcessDeviceRels.add(orderProcessDeviceRel);
            }
            orderProcessDeviceRelRepository.saveAll(orderProcessDeviceRels);
        }
        return deviceGroupId;
    }

    @Override
    public String getDeviceGroupId(Integer orderProcessId, List<Integer> deviceIds) {
        if (deviceIds == null || deviceIds.size() == 0) {
            return "";
        }
        return orderProcessDeviceRelRepository.getDeviceGroupId(orderProcessId, deviceIds, deviceIds.size());
    }

    @Override
    public List<TBusOrderProcessDeviceRel> findByDeviceGroupId(String deviceGroupId) {
        return orderProcessDeviceRelRepository.findByDeviceGroupId(deviceGroupId);
    }

    @Override
    public String getDeviceGroupNames(String deviceGroupId) {
        if (StringUtils.isNotEmpty(deviceGroupId)) {
            List<TBusOrderProcessDeviceRel> deviceRels = orderProcessDeviceRelRepository.findByDeviceGroupId(deviceGroupId);
            StringBuilder deviceBuf = new StringBuilder();
            for (TBusOrderProcessDeviceRel deviceRel : deviceRels) {
                TsysDevice device = deviceRepository.findById(deviceRel.getDeviceId()).orElse(null);
                deviceBuf.append(device.getDeviceName() + ",");
            }
            if (deviceBuf.length() > 0) {
                return deviceBuf.substring(0, deviceBuf.lastIndexOf(","));
            }
        }
        return null;
    }
}
