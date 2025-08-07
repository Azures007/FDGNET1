package org.thingsboard.server.dao.mes.TSysDevice;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysDevice;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.mes.dto.TSysDeviceSearchDto;
import org.thingsboard.server.dao.mes.dto.TSysDeviceDto;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/18 17:36
 * @Description:
 */
public interface TSysDeviceService {
    Page<TSysDevice> tSysDeviceList(String userId, Integer current, Integer size, TSysDeviceDto deviceDto, Integer belongProcessId);

    List<TSysDevice> tSysDeviceList(TSysDeviceSearchDto deviceDto);

    ResponseResult saveTSysDevice(TSysDevice device);

    void deleteTSysDevice(Integer deviceId);

    void enableDevice(Integer deviceId, String enable, String name);

    TSysDevice getTSysDevice(Integer deviceId);

    List<TSysDevice> export(List<Integer> deviceIds);

    void importDevices(List<TSysDevice> devices);

    /**
     * 初始化设备时间线
     * @param endDate
     */
    void initIot(String endDate,String recordType);
}
