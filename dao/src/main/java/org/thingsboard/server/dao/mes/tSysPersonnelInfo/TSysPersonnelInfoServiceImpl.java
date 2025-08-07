package org.thingsboard.server.dao.mes.tSysPersonnelInfo;

import com.alibaba.fastjson.JSON;
//import com.natrotech.fpJni.FpAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.common.data.mes.sys.TSysUserDevices;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.TSysPersonnelInfoDto;
import org.thingsboard.server.dao.mes.dto.VerifyDevicesDto;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysUserDevicesRepository;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TSysPersonnelInfoServiceImpl implements TSysPersonnelInfoService {
    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Autowired
    TSysUserDevicesRepository tSysUserDevicesRepository;

    @Override
    public Page<TSysPersonnelInfo> tSysPersonnelInfoList(Integer current, Integer size, TSysPersonnelInfoDto tSysPersonnelInfoDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crtTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("enabled_st", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("sex", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("className", ExampleMatcher.GenericPropertyMatchers.contains());
        TSysPersonnelInfo tSysPersonnelInfo = new TSysPersonnelInfo();
        BeanUtils.copyProperties(tSysPersonnelInfoDto, tSysPersonnelInfo);
        Example<TSysPersonnelInfo> example = Example.of(tSysPersonnelInfo, matcher);
        Page<TSysPersonnelInfo> tSysPersonnelInfoPage = tSysPersonnelInfoRepository.findAll(example, pageable);

        tSysPersonnelInfoPage.stream().forEach(r -> {
            r.setStationName(GlobalConstant.getCodeDscName("JOB0000", r.getStation()));
            //获取指纹信息
            TSysUserDevices tSysUserDevices = new TSysUserDevices();
            tSysUserDevices.setEnabled(GlobalConstant.enableTrue);
            tSysUserDevices.setDevicesType("1");
            tSysUserDevices.setPersonnelId(r.getPersonnelId());
            ExampleMatcher bymatcher = ExampleMatcher.matching() //构建对象
                    .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact())
                    .withMatcher("personnelId", ExampleMatcher.GenericPropertyMatchers.exact())
                    .withMatcher("devicesType", ExampleMatcher.GenericPropertyMatchers.exact());
            Example<TSysUserDevices> byexample = Example.of(tSysUserDevices, bymatcher);
            List<TSysUserDevices> tSysUserDevicesList = tSysUserDevicesRepository.findAll(byexample);
            r.setByDevicesType1(tSysUserDevicesList.isEmpty() ? "0" : "1");
        });
        return tSysPersonnelInfoPage;
    }

    @Override
    public Page<TSysPersonnelInfo> tSysPersonnelInfoListByUserId(Integer current, Integer size, TSysPersonnelInfoDto tSysPersonnelInfoDto, String userId) {
        var personnelInfo = tSysPersonnelInfoRepository.getByUserId(userId);
        Integer personnelId = 0;
        if (personnelInfo != null) {
            personnelId = personnelInfo.getPersonnelId();
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "personnel_id");
        Pageable pageable = PageRequest.of(current, size, sort);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
//                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysPersonnelInfo tSysPersonnelInfo = new TSysPersonnelInfo();
        BeanUtils.copyProperties(tSysPersonnelInfoDto, tSysPersonnelInfo);
//        Example<TSysClass> example = Example.of(tSysClass, matcher);
        tSysPersonnelInfo.setEnabledSt(StringUtils.isNotBlank(tSysPersonnelInfoDto.getEnabledSt()) ? tSysPersonnelInfoDto.getEnabledSt() : "");
        tSysPersonnelInfo.setName(StringUtils.isNotBlank(tSysPersonnelInfoDto.getName()) ? tSysPersonnelInfoDto.getName() : "");
        tSysPersonnelInfo.setStation(StringUtils.isNotBlank(tSysPersonnelInfoDto.getStation()) ? tSysPersonnelInfoDto.getStation() : "");
        Page<TSysPersonnelInfo> tSysPersonnelInfoPage = tSysPersonnelInfoRepository.findAllBy(tSysPersonnelInfo, personnelId, pageable);
        tSysPersonnelInfoPage.stream().forEach(r -> {
            r.setStationName(GlobalConstant.getCodeDscName("JOB0000", r.getStation()));
        });
        return tSysPersonnelInfoPage;
    }

    @Override
    public void saveTSysPersonnelInfo(TSysPersonnelInfo tSysPersonnelInfo) {
        if (tSysPersonnelInfo.getPersonnelId() == null) {
            //新增
            tSysPersonnelInfo.setCrtUser(tSysPersonnelInfo.getUpdateUser());
            tSysPersonnelInfo.setCrtTime(tSysPersonnelInfo.getUpdateTime());
            if (StringUtils.isBlank(tSysPersonnelInfo.getEnabledSt())) {
                tSysPersonnelInfo.setEnabledSt(GlobalConstant.enableTrue);
            }

        }
        tSysPersonnelInfoRepository.saveAndFlush(tSysPersonnelInfo);
    }

    @Override
    public Boolean judgeUserIdRepeat(Integer personnelId, String userId) {
        //判断绑定的用户ID是否重复
        Boolean flag = false;
        if (userId != null) {
            if (userId.length() > 1) {
                TSysPersonnelInfo tSysPersonnelInfo1 = tSysPersonnelInfoRepository.findByUserId(userId);
                if (tSysPersonnelInfo1 != null) {
                    if (tSysPersonnelInfo1.getPersonnelId().intValue() != personnelId.intValue()) {
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    @Override
    public void deleteTSysPersonnelInfo(Integer personnelId) {
        tSysPersonnelInfoRepository.deleteById(personnelId);
    }

    @Override
    public TSysPersonnelInfo getPersonnelInfoById(Integer personnelId) {
        return tSysPersonnelInfoRepository.findById(personnelId).orElse(null);
    }

    @Override
    public TSysPersonnelInfo getPersonnelInfoByUserId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException("获取人员失败，userId不能为空");
        }
        return tSysPersonnelInfoRepository.findByUserId(userId);
    }

    @Override
    public void addDevices(TSysUserDevices tSysUserDevices) {
        if (tSysUserDevices.getDevicesKey().length() <= 300) {
            throw new RuntimeException("指纹信息不合法，请重新录入");
        }
        tSysUserDevicesRepository.save(tSysUserDevices);
    }

    @Override
    public Boolean verifyDevices(VerifyDevicesDto verifyDevicesDto) {
        boolean flag = false;
        // 友臣不需要指纹识别，注释 2025-07-16
//        //获取指纹信息
//        TSysUserDevices tSysUserDevices = new TSysUserDevices();
//        tSysUserDevices.setEnabled(GlobalConstant.enableTrue);
//        tSysUserDevices.setDevicesType("1");
//        tSysUserDevices.setPersonnelId(verifyDevicesDto.getPersonnelId());
//        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
//                .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("personnelId", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("devicesType", ExampleMatcher.GenericPropertyMatchers.exact());
//        Example<TSysUserDevices> example = Example.of(tSysUserDevices, matcher);
//        List<TSysUserDevices> tSysUserDevicesList = tSysUserDevicesRepository.findAll(example);
//        if (tSysUserDevicesList.isEmpty()) {
//            throw new RuntimeException("当前人员未采集指纹信息，请先采集指纹信息");
//        }
//        String currentKey = verifyDevicesDto.getDevicesKey();
//        if (currentKey.length() <= 300) {
//            throw new RuntimeException("指纹信息不合法，请重新录入");
//        }
//        int[] score = new int[1];
//        FpAPI inst = FpAPI.getInst();
//        for (TSysUserDevices sysUserDevices : tSysUserDevicesList) {
//            if (sysUserDevices.getDevicesKey().length() < 300) {
//                continue;
//            }
//            int nRet = inst.match(sysUserDevices.getDevicesKey().getBytes(), currentKey.getBytes(), 3, score);
//            if ((nRet == 0) && (score[0] > 0)) {
//                return true;
//            }
//        }
        return flag;
    }

    @Override
    public List<String> listGroupLeader(Integer personnelId) {
//        List<String> userIds= tSysPersonnelInfoRepository.listGroupLeader(personnelId);
//        return userIds;
        return null;
    }

    @Override
    public TSysPersonnelInfo getPersonnelByDevices(String userId, String content) {
        //获取操作员列表人员id信息
        var personnelInfo = tSysPersonnelInfoRepository.getByUserId(userId);
        Integer personnelId = 0;
        if (personnelInfo != null) {
            personnelId = personnelInfo.getPersonnelId();
        }
        List<Integer> personnelIds = tSysPersonnelInfoRepository.findAllByPersonnel(personnelId);
        //通过ids获取指纹集合
        List<Map> maps = tSysUserDevicesRepository.listInpersonnel(personnelIds);
        //当前分数最高的人员Map
        Map personMap = null;
        //上一次分数
        int source = 0;
        int sourceCurrent;
        String devicesKey;
        for (Map map : maps) {
            devicesKey = map.get("devices_key").toString();
            sourceCurrent = mateKey(devicesKey, content);
            if (source < sourceCurrent) {
                source = sourceCurrent;
                personMap = map;
            }
        }
        if (source > 0) {
            TSysPersonnelInfo personnel = JSON.parseObject(JSON.toJSONString(personMap), TSysPersonnelInfo.class);
            return personnel;
        }else {
            throw new RuntimeException("您未录入指纹或不在班组中");
        }

    }

    /**
     * 指纹匹配分数
     *
     * @param devicesKey：库指纹
     * @param content：当前指纹
     * @return
     */
    private int mateKey(String devicesKey, String content) {
        // 友臣不需要指纹识别，注释 2025-07-16
//        int[] score = new int[1];
//        FpAPI inst = FpAPI.getInst();
//        int nRet = inst.match(devicesKey.getBytes(), content.getBytes(), 3, score);
//        if ((nRet == 0) && (score[0] > 0)) {
//            return score[0];
//        }
        return 0;
    }


}
