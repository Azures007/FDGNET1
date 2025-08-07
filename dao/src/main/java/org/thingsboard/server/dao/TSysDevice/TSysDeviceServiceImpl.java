package org.thingsboard.server.dao.TSysDevice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.mes.sys.TSysDeviceIot;
import org.thingsboard.server.common.data.mes.sys.TsysDevice;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysDeviceDto;
import org.thingsboard.server.dao.dto.TSysDeviceSearchDto;
import org.thingsboard.server.dao.order.OrderProcessDeviceRelService;
import org.thingsboard.server.dao.order.OrderProcessPersonRelService;
import org.thingsboard.server.dao.sql.mes.device.TSysDeviceIotRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessPersonRelRepository;
import org.thingsboard.server.dao.sql.mes.tSysDevice.TSysDeviceRepository;
import org.thingsboard.server.dao.user.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/18 17:37
 * @Description:
 */
@Service
@Slf4j
public class TSysDeviceServiceImpl implements TSysDeviceService {
    @Autowired
    TSysDeviceRepository tSysDeviceRepository;

    @Autowired
    TSysDeviceIotRepository tSysDeviceIotRepository;

    @Autowired
    OrderProcessDeviceRelService orderProcessDeviceRelService;

    @Autowired
    OrderProcessPersonRelService orderProcessPersonRelService;

    @Autowired
    OrderProcessPersonRelRepository orderProcessPersonRelRepository;
    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;
    @Override
    public Page<TsysDevice> tSysDeviceList(String userId,Integer current, Integer size, TSysDeviceDto deviceDto, Integer belongProcessId) {
        String pkOrg = userService.getUserCurrentPkOrg(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("deviceName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("deviceNumber", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("pkOrg", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact());

        if (belongProcessId != -1) {
            matcher.withMatcher("belongProcessId", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        TsysDevice tsysDevice = new TsysDevice();
        if (StringUtils.isEmpty(deviceDto.getDeviceNumber())) {
            deviceDto.setDeviceNumber(null);
        }
        if (StringUtils.isEmpty(deviceDto.getDeviceName())) {
            deviceDto.setDeviceName(null);
        }
        if (StringUtils.isEmpty(deviceDto.getEnabled())) {
            deviceDto.setEnabled(null);
        }
        if (belongProcessId != -1) {
            tsysDevice.setBelongProcessId(belongProcessId);
        }
        BeanUtils.copyProperties(deviceDto, tsysDevice);
        tsysDevice.setPkOrg(pkOrg);
        Example<TsysDevice> example = Example.of(tsysDevice, matcher);
        Page<TsysDevice> devicePage = tSysDeviceRepository.findAll(example, pageable);
        return devicePage;
    }

    @Override
    public List<TsysDevice> tSysDeviceList(TSysDeviceSearchDto deviceDto) {
        var devicePersonGroupId = orderProcessPersonRelService.getPersonGroupId(deviceDto.getOrderProcessId(), deviceDto.getDevicePersonIds());
        if (null == devicePersonGroupId) {
            devicePersonGroupId = "";
        }
        if (deviceDto.isQueryByPersonIds()) {
            return tSysDeviceRepository.findAllByDevicePersonGroupId(deviceDto.getBelongProcessId(), deviceDto.getEnabled(), devicePersonGroupId, deviceDto.getOrderProcessId());
        } else {
            return tSysDeviceRepository.findAll(deviceDto.getBelongProcessId(), deviceDto.getEnabled());
        }
    }

    @Override
    public ResponseResult saveTSysDevice(TsysDevice device) {
        //校验基地必填
        if (StringUtils.isEmpty(device.getPkOrg())) {
            throw new RuntimeException("请绑定基地");
        }
        //新增
        if (device.getDeviceId() == null) {
            List<TsysDevice> devices = tSysDeviceRepository.findByDeviceNumber(device.getDeviceNumber());
            if (devices.size() > 0) {
                return ResultUtil.error("当前设备号已存在");
            }
            device.setCreatedTime(device.getUpdatedTime());
            device.setCreatedUser(device.getUpdatedUser());
        }
        tSysDeviceRepository.saveAndFlush(device);
        return ResultUtil.success();
    }

    @Override
    public void deleteTSysDevice(Integer deviceId) {
        tSysDeviceRepository.deleteById(deviceId);
    }

    @Override
    public void enableDevice(Integer deviceId, String enable, String name) {
        TsysDevice device = tSysDeviceRepository.findById(deviceId).get();
        device.setDeviceId(deviceId);
        device.setUpdatedUser(name);
        device.setUpdatedTime(new Date());
        if (GlobalConstant.enableTrue.equals(enable)) {
            device.setEnabled(GlobalConstant.enableTrue);
            device.setEnableUser(device.getUpdatedUser());
            device.setEnableTime(device.getUpdatedTime());
        } else {
            device.setEnabled(GlobalConstant.enableFalse);
            device.setDisabledUser(device.getUpdatedUser());
            device.setDisabledTime(device.getUpdatedTime());
        }
        tSysDeviceRepository.saveAndFlush(device);
    }

    @Override
    public TsysDevice getTSysDevice(Integer deviceId) {
        return tSysDeviceRepository.findById(deviceId).get();
    }

    @Override
    public List<TsysDevice> export(List<Integer> deviceIds) {
        return tSysDeviceRepository.findAllById(deviceIds);
    }

    @Override
    public void importDevices(List<TsysDevice> devices) {
        tSysDeviceRepository.saveAll(devices);
        tSysDeviceRepository.flush();
    }

    @Transactional
    @Override
    public void initIot(String endDate,String recordType) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;
        try {
            parse = formatter.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<TsysDevice> tsysDevices = tSysDeviceRepository.findAll();
        TSysDeviceIot tSysDeviceIot;
        for (TsysDevice tsysDevice : tsysDevices) {
            tSysDeviceIot = new TSysDeviceIot();
            tSysDeviceIot.setDeviceCode(tsysDevice.getDeviceNumber());
            tSysDeviceIot.setDeviceId(tsysDevice.getDeviceId());
            tSysDeviceIot.setIotTime(parse);
            tSysDeviceIot.setRecordType(recordType);
            tSysDeviceIotRepository.save(tSysDeviceIot);
        }
    }
}
