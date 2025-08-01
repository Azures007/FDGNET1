package org.thingsboard.server.dao.TSysDevice;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.TsysDevice;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.dto.TSysDeviceSearchDto;
import org.thingsboard.server.dao.dto.TSysDeviceDto;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/18 17:36
 * @Description:
 */
public interface TSysDeviceService {
    Page<TsysDevice> tSysDeviceList(String userId,Integer current, Integer size, TSysDeviceDto deviceDto, Integer belongProcessId);

    List<TsysDevice> tSysDeviceList(TSysDeviceSearchDto deviceDto);

    ResponseResult saveTSysDevice(TsysDevice device);

    void deleteTSysDevice(Integer deviceId);

    void enableDevice(Integer deviceId, String enable, String name);

    TsysDevice getTSysDevice(Integer deviceId);

    List<TsysDevice> export(List<Integer> deviceIds);

    void importDevices(List<TsysDevice> devices);

    /**
     * 初始化设备时间线
     * @param endDate
     */
    void initIot(String endDate,String recordType);
}
