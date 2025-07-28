package org.thingsboard.server.dao.TSysProcessInfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysProcessInfoDto;
import org.thingsboard.server.dao.sql.TSysProcessInfo.TSysProcessClassRelRepository;
import org.thingsboard.server.dao.sql.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.order.OrderProcessRepository;
import org.thingsboard.server.dao.sql.tSysClass.TSysClassRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/20 18:13
 * @Description:
 */
@Service
@Slf4j
public class TSysProcessInfoServiceImpl implements TSysProcessInfoService {
    @Autowired
    TSysProcessInfoRepository tSysProcessInfoRepository;
    @Autowired
    TSysProcessClassRelRepository tSysProcessClassRelRepository;
    @Autowired
    TSysClassRepository tSysClassRepository;
    @Autowired
    OrderProcessRepository orderProcessRepository;


    @Override
    public void saveProcess(TSysProcessInfo processInfo) {
        this.updateVerify(processInfo);
        if (processInfo.getProcessId() != null) {
            if (!tSysProcessInfoRepository.findById(processInfo.getProcessId()).isEmpty()) {
                TSysProcessInfo processInfo1 = tSysProcessInfoRepository.findById(processInfo.getProcessId()).get();
                if (processInfo1 != null && StringUtils.isNotEmpty(processInfo1.getProcessNumber())) {
                    processInfo.setCreatedTime(processInfo1.getCreatedTime());
                    processInfo.setCreatedUser(processInfo1.getCreatedUser());
                }
            }
        }
        processInfo.setBySetImport(StringUtils.isEmpty(processInfo.getBySetImport())||(processInfo.getBySetImport().equals(GlobalConstant.enableFalse)) ? GlobalConstant.enableFalse : GlobalConstant.enableTrue);
        processInfo.setBySetExport(StringUtils.isEmpty(processInfo.getBySetExport())||(processInfo.getBySetExport().equals(GlobalConstant.enableFalse)) ? GlobalConstant.enableFalse : GlobalConstant.enableTrue);
        tSysProcessInfoRepository.saveAndFlush(processInfo);
    }

    public void updateVerify(TSysProcessInfo processInfo) {
        // 工序编码去除空格
        processInfo.setProcessNumber(processInfo.getProcessNumber().trim());
        if (StringUtils.isBlank(processInfo.getProcessNumber())) {
            throw new RuntimeException("工序编码不能为空");
        }
        if (StringUtils.isBlank(processInfo.getProcessName())) {
            throw new RuntimeException("工序名称不能为空");
        }
        List<TSysProcessInfo> processInfoList = tSysProcessInfoRepository.findByProcessNumber(processInfo.getProcessNumber());
        if (processInfo.getProcessId() == null) {
            if (processInfoList != null && processInfoList.size() > 0) {
                throw new RuntimeException("工序编码不能和已有的重复");
            }
        } else {
            if (processInfoList != null && processInfoList.size() > 0 && !processInfoList.get(0).getProcessId().equals(processInfo.getProcessId())) {
                throw new RuntimeException("工序编码不能和已有的重复");
            }
        }
    }

    @Override
    public TSysProcessInfo processDetail(Integer processId) {
        return tSysProcessInfoRepository.findById(processId).get();
    }

    @Override
    public TSysProcessInfo getAndCheck(Integer processId) {
        TSysProcessInfo tSysProcessInfo = tSysProcessInfoRepository.findById(processId).orElse(null);
        if (null == tSysProcessInfo) {
            throw new RuntimeException("工序不存在");
        }
        if (tSysProcessInfo.getEnabled() == 0) {
            throw new RuntimeException("工序已经禁用");
        }
        return tSysProcessInfo;
    }

    @Override
    public Page<TSysProcessInfo> getProcessList(Integer current, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        return tSysProcessInfoRepository.findAll(pageable);
    }

    @Override
    public Page<TSysProcessInfo> getProcessList(Integer current, Integer size, TSysProcessInfoDto tSysProcessInfoDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("processName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("processNumber", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysProcessInfo tSysProcessInfo = new TSysProcessInfo();
//        if (StringUtils.isEmpty(tSysProcessInfoDto.getProcessName())) {
//            tSysProcessInfoDto.setProcessName(null);
//        }
//        if (StringUtils.isEmpty(tSysProcessInfoDto.getProcessNumber())) {
//            tSysProcessInfoDto.setProcessNumber(null);
//        }
//        if (StringUtils.isEmpty(tSysProcessInfoDto.getEnabled())) {
//            tSysProcessInfoDto.setEnabled(null);
//        }
        BeanUtils.copyProperties(tSysProcessInfoDto, tSysProcessInfo);
        Example<TSysProcessInfo> example = Example.of(tSysProcessInfo, matcher);
        Page<TSysProcessInfo> processInfoPage = tSysProcessInfoRepository.findAll(example, pageable);
        return processInfoPage;
    }


    @Override
    public void enableProcess(Integer processId, Integer enable, String name) {
        TSysProcessInfo processInfo = tSysProcessInfoRepository.findById(processId).get();
        processInfo.setEnabled(enable);
        processInfo.setUpdatedUser(name);
        processInfo.setUpdatedTime(new Date());
        tSysProcessInfoRepository.saveAndFlush(processInfo);
    }

    @Override
    public ResponseResult processSetting(Integer processId, List<Integer> classIds, String name) {
        tSysProcessClassRelRepository.deleteByProcessId(processId);
        List<TSysProcessClassRel> rels = new ArrayList<>();
//        if(classIds.size()>1){
//            return ResultUtil.error("一个工序只能设置一个班别");
//        }
        //12035 工序管理的工序设置仅允许绑定一个白班和一个晚班的班别
        boolean hasDayClass = false;
        boolean hasNightClass = false;
        for (Integer classId : classIds) {
            var sysClass = tSysClassRepository.findById(classId).orElse(null);
            var scheduling = sysClass.getScheduling();
            if (scheduling.equals("SCHEDULING0001")) {
                if (hasDayClass) {
                    throw new RuntimeException("工序管理的工序设置仅允许绑定一个白班");
                } else {
                    hasDayClass = true;
                }
            }
            if (scheduling.equals("SCHEDULING0002")) {
                if (hasNightClass) {
                    throw new RuntimeException("工序管理的工序设置仅允许绑定一个夜班");
                } else {
                    hasNightClass = true;
                }
            }
            TSysProcessClassRel rel = new TSysProcessClassRel();
            rel.setProcessId(processId);
            rel.setClassId(classId);
            rel.setCrtTime(new Date());
            rel.setCrtUser(name);
            rel.setUpdateTime(new Date());
            rel.setUpdateUser(name);
            rels.add(rel);
        }
        tSysProcessClassRelRepository.saveAll(rels);
        tSysProcessClassRelRepository.flush();
        return ResultUtil.success();
    }

    @Override
    public List<TSysClass> classList(Integer processId) {
        List<TSysProcessClassRel> rels = tSysProcessClassRelRepository.findByProcessId(processId);
        List<Integer> classIds = new ArrayList<>();
        rels.forEach(rel -> classIds.add(rel.getClassId()));
        return tSysClassRepository.findAllById(classIds);
    }

    @Override
    public void delete(Integer processId) {
        tSysProcessInfoRepository.deleteById(processId);
        tSysProcessClassRelRepository.deleteByProcessId(processId);
    }

    @Override
    public TSysProcessInfo getByProcessNumber(String processNumber) {
        List<TSysProcessInfo> processInfoList = tSysProcessInfoRepository.findByProcessNumber(processNumber);
        return processInfoList.get(0);
    }

    @Override
    public TSysProcessInfo processDetailByOrderProcessId(Integer orderProcessId) {
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(orderProcessId).orElse(null);
        return tBusOrderProcess.getProcessId();
    }

}
