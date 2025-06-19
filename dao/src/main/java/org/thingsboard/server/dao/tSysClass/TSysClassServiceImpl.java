package org.thingsboard.server.dao.tSysClass;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thingsboard.common.util.Utils;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysClassDto;
import org.thingsboard.server.dao.sql.tSysClass.ClassGroupLeaderRepository;
import org.thingsboard.server.dao.sql.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.sql.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.dao.sql.tSysPersonnelInfo.ClassPersonnelRepository;
import org.thingsboard.server.dao.sql.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.vo.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TSysClassServiceImpl implements TSysClassService {
    @Autowired
    TSysClassRepository tSysClassRepository;

    @Autowired
    ClassPersonnelRepository classPersonnelRepository;
    @Autowired
    ClassGroupLeaderRepository classGroupLeaderRepository;
    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Autowired
    TSysCodeDscRepository tSysCodeDscRepository;

    @Override
    public Page<TSysClass> tSysClassList(Integer current, Integer size, TSysClassDto tSysClassDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crt_time");
        Pageable pageable = PageRequest.of(current, size, sort);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
//                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysClass tSysClass = new TSysClass();
        BeanUtils.copyProperties(tSysClassDto, tSysClass);
//        Example<TSysClass> example = Example.of(tSysClass, matcher);
        tSysClass.setEnabledSt(StringUtils.isNotBlank(tSysClass.getEnabledSt()) ? tSysClass.getEnabledSt() : "");
        tSysClass.setName(StringUtils.isNotBlank(tSysClass.getName()) ? tSysClass.getName() : "");
        tSysClass.setClassNumber(StringUtils.isNotBlank(tSysClass.getClassNumber()) ? tSysClass.getClassNumber() : "");
        Page<TSysClass> tSysClassPage = tSysClassRepository.findAllBy(tSysClass, pageable);
        String code = "SCHEDULING0000";
        tSysClassPage.getContent().stream().forEach(tSysClass1 -> {
            tSysClass1.setSchedulingCodeDsc(GlobalConstant.getCodeDscName(code, tSysClass1.getScheduling()));
        });
        return tSysClassPage;
    }

    @Override
    public PageVo<ClassGroupLeaderExpVo> tSysClassListExp(Integer current, Integer size, TSysClassDto tSysClassDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crt_time");
        Pageable pageable = PageRequest.of(current, size, sort);
        TSysClass tSysClass = new TSysClass();
        BeanUtils.copyProperties(tSysClassDto, tSysClass);
        tSysClass.setEnabledSt(StringUtils.isNotBlank(tSysClass.getEnabledSt()) ? tSysClass.getEnabledSt() : "");
        tSysClass.setName(StringUtils.isNotBlank(tSysClass.getName()) ? tSysClass.getName() : "");
        tSysClass.setClassNumber(StringUtils.isNotBlank(tSysClass.getClassNumber()) ? tSysClass.getClassNumber() : "");
        var select = tSysClassRepository.findAllByExp(tSysClass, current, size);
        int total = select.size();
        PageVo<ClassGroupLeaderExpVo> pageVo = new PageVo(size, current);
        List<ClassGroupLeaderExpVo> castEntity = EntityUtils.castEntity(select, ClassGroupLeaderExpVo.class, new ClassGroupLeaderExpVo());
        pageVo.setTotal(total);
        pageVo.setList(castEntity);
        return pageVo;
    }

    @Override
    public PageVo<ClassPersonnelExpVo> tSysClassListExp2(Integer current, Integer size, TSysClassDto tSysClassDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crt_time");
        Pageable pageable = PageRequest.of(current, size, sort);
        TSysClass tSysClass = new TSysClass();
        BeanUtils.copyProperties(tSysClassDto, tSysClass);
        tSysClass.setEnabledSt(StringUtils.isNotBlank(tSysClass.getEnabledSt()) ? tSysClass.getEnabledSt() : "");
        tSysClass.setName(StringUtils.isNotBlank(tSysClass.getName()) ? tSysClass.getName() : "");
        tSysClass.setClassNumber(StringUtils.isNotBlank(tSysClass.getClassNumber()) ? tSysClass.getClassNumber() : "");
        var select = tSysClassRepository.findAllByExp2(tSysClass, current, size);
        int total = select.size();
        PageVo<ClassPersonnelExpVo> pageVo = new PageVo(size, current);
        List<ClassPersonnelExpVo> castEntity = EntityUtils.castEntity(select, ClassPersonnelExpVo.class, new ClassPersonnelExpVo());
        pageVo.setTotal(total);
        pageVo.setList(castEntity);
        return pageVo;
    }


    @Override
    public void saveTSysClass(TSysClass tSysClass) {
        if (tSysClass.getClassId() == null) {
            //新增
            tSysClass.setCrtUser(tSysClass.getUpdateUser());
            tSysClass.setCrtTime(tSysClass.getUpdateTime());
            if (StringUtils.isBlank(tSysClass.getEnabledSt())) {
                tSysClass.setEnabledSt("1");
            }
        }
        if (StringUtils.isNotEmpty(tSysClass.getName())) {
            List<TSysClass> sysClasseList = tSysClassRepository.findByName(tSysClass.getName());
            if (sysClasseList != null && sysClasseList.size() > 0 && sysClasseList.get(0).getClassId() != tSysClass.getClassId()) {
                throw new RuntimeException("班别名称已存在！");
            }
        }
        tSysClassRepository.saveAndFlush(tSysClass);
        //生成班别编码：BB+三位的ID流水号
        if (StringUtils.isEmpty(tSysClass.getClassNumber())) {
            String strNum = String.format("%03d", tSysClass.getClassId());
            tSysClass.setClassNumber("BB" + strNum);
            tSysClassRepository.saveAndFlush(tSysClass);
        }
    }

    @Transactional
    @Override
    public void saveTSysClassAndGroupLeaderRel(TSysClass tSysClass, List<TSysClassGroupLeaderRel> tSysClassGroupLeaderRelList) {
        // 任务13379 取消班别的班长校验 2022-08-01
//        List<TSysClassGroupLeaderRel> TSysClassGroupLeaderRelLits = tSysClassGroupLeaderRelList;
//        if (!TSysClassGroupLeaderRelLits.isEmpty()) {
//            //校验
//            List<Integer> personnelIdList = new ArrayList<>();
//            for (TSysClassGroupLeaderRel t :
//                    TSysClassGroupLeaderRelLits) {
//                personnelIdList.add(t.getPersonnelId());
//            }
//            List<TSysClassGroupLeaderRel> otherRel = tSysClassService.getTSysClassGroupLeaderRelByPersonnelIdAndOtherClass(tSysClass.getClassId() == null ? 0 : tSysClass.getClassId(), personnelIdList);
//            if (otherRel.size() >= 1) {
//                return ResultUtil.error("分配的班长已在其他班组，保存失败");
//            }
//        }
        this.saveTSysClass(tSysClass);
        this.saveTSysClassGroupLeaderRel(tSysClass.getClassId(), tSysClassGroupLeaderRelList);
    }

    @Transactional
    @Override
    public void deleteTSysClass(Integer classId) {
        //删除班组相关关联信息也要删除
        tSysClassRepository.deleteById(classId);
        classPersonnelRepository.deleteByClassId(classId);
        classGroupLeaderRepository.deleteByClassId(classId);
    }

    @Override
    public TSysClass getClassById(Integer classId) {
        return tSysClassRepository.findById(classId).orElse(null);
    }

    @Override
    public List<TSysClass> getClassByProcessId(Integer processId) {
        List<Map> select = classPersonnelRepository.getClassByProcessId(processId);
        try {
            List<TSysClass> castEntity = JSON.parseArray(JSON.toJSONString(select), TSysClass.class);
//            List<TSysClass> castEntity = EntityUtils.castEntity(select, TSysClass.class, new TSysClass());
            return castEntity;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ClassPersinnelRelVo> getRelByClassId(Integer classId) {
        List<Object[]> select = classPersonnelRepository.getRelByClassId(classId);
        try {
            List<ClassPersinnelRelVo> castEntity = EntityUtils.castEntity(select, ClassPersinnelRelVo.class, new ClassPersinnelRelVo());
            castEntity.stream().forEach(r -> {
                r.setStationName(GlobalConstant.getCodeDscName("JOB0000", r.getStation()));
            });
            return castEntity;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TSysClass getAndCheckByScheduling(Integer classId) {
        var sysClass = tSysClassRepository.findById(classId).orElse(null);
        //状态为禁用
        if (sysClass.getEnabledSt() == "1") {
            return null;
        }
        var scheduling = sysClass.getScheduling();
        try {
            String format = "yyyy-MM-dd HH:mm:ss";
            String formatDay = "yyyy-MM-dd";
            SimpleDateFormat df = new SimpleDateFormat(format);
            SimpleDateFormat dfDay = new SimpleDateFormat(formatDay);
            Date nowTime = df.parse(df.format(new Date()));
            List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscRepository.findByCodeClIdAndEnabledSt("SCHEDULINGTIME0000", GlobalConstant.enableTrue);
            String startTimeStr = null;
            String endTimeStr = null;
            for (TSysCodeDsc tSysCodeDsc : tSysCodeDscList) {
                if (scheduling.equals("SCHEDULING0001")) {
                    if ("早班上班".equals(tSysCodeDsc.getCodeDsc())) {
                        startTimeStr = tSysCodeDsc.getCodeValue();
                    }
                    if ("早班下班".equals(tSysCodeDsc.getCodeDsc())) {
                        endTimeStr = tSysCodeDsc.getCodeValue();
                    }
                }
                if (scheduling.equals("SCHEDULING0002")) {
                    if ("晚班上班".equals(tSysCodeDsc.getCodeDsc())) {
                        startTimeStr = tSysCodeDsc.getCodeValue();
                    }
                    if ("晚班下班".equals(tSysCodeDsc.getCodeDsc())) {
                        endTimeStr = tSysCodeDsc.getCodeValue();
                    }
                }
            }
            String nowStr = dfDay.format(new Date());
            Date startTime = df.parse(nowStr + " " + startTimeStr);
            Date endTime = df.parse(nowStr + " " + endTimeStr);
            //SCHEDULING0001 早班(7:00~19:00） SCHEDULING0002 晚班(19:00~7:00）
            if (scheduling.equals("SCHEDULING0001") && Utils.isEffectiveDate(nowTime, startTime, endTime)) {
                return sysClass;
            }
            //晚班时间判断
            //晚班上班	18:00:01; 晚班下班	6:00:01;
            if (scheduling.equals("SCHEDULING0002")) {
                Date dateTime00 = df.parse(nowStr + " " + "00:00:00");
                Date dateTime23 = df.parse(nowStr + " " + "23:59:59");
                if (Utils.isEffectiveDate(nowTime, dateTime00, endTime) || Utils.isEffectiveDate(nowTime, startTime, dateTime23)) {
                    return sysClass;
                }
                return null;
            }
        } catch (ParseException e) {
            throw new RuntimeException("获取早晚班别失败");
        }
        return null;
    }

    @Override
    public List<ClassGroupLeaderRelVo> getGroupLeaderRelByClassId(Integer classId) {
        List<Object[]> select = classGroupLeaderRepository.getRelByClassId(classId);
        try {
            List<ClassGroupLeaderRelVo> castEntity = EntityUtils.castEntity(select, ClassGroupLeaderRelVo.class, new ClassGroupLeaderRelVo());
            return castEntity;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    @Transactional
    @Override
    public void saveTSysClassPersonnelRel(Integer classId, List<TSysClassPersonnelRel> personnelList) {
        //获取旧的关联关系
        List<TSysClassPersonnelRel> classPersonnelRelList = classPersonnelRepository.findByClassId(classId);
        //如果传入的关联关系为空则删除关系表中对应的关系
        if (CollectionUtils.isEmpty(personnelList)) {
            classPersonnelRepository.deleteByClassId(classId);
            //更新删除关系的人员表中的班组信息
            List<Integer> deleteRelIds = classPersonnelRelList.stream().map(TSysClassPersonnelRel::getPersonnelId).collect(Collectors.toList());
            tSysPersonnelInfoRepository.updateClassNameByClassId(deleteRelIds, 0);
            return;
        }
        personnelList.forEach(t -> {
            t.setClassId(classId);
        });


        //获取需要插入的关系
//        List<TSysClassPersonnelRel> insertRelList = personnelList.stream().filter(item -> item.getClassPersonnelId() == null).collect(Collectors.toList());
        //获取需要更新的关系ID
        List<TSysClassPersonnelRel> updateRelList = personnelList.stream().filter(item -> item.getClassPersonnelId() != null).collect(Collectors.toList());
        List<Integer> updateRelIds = updateRelList.stream().map(TSysClassPersonnelRel::getClassPersonnelId).collect(Collectors.toList());
        //获取需求删除的关系
        List<TSysClassPersonnelRel> removeRelList = classPersonnelRelList.stream().filter(item -> !updateRelIds.contains(item.getClassPersonnelId())).collect(Collectors.toList());

        classPersonnelRepository.deleteInBatch(removeRelList);
        List<TSysClassPersonnelRel> newRelList = classPersonnelRepository.saveAll(personnelList);
        //更新删除关系的人员表中的班组信息
        List<Integer> deleteRelIds = removeRelList.stream().map(TSysClassPersonnelRel::getPersonnelId).collect(Collectors.toList());
        tSysPersonnelInfoRepository.updateClassNameByClassId(deleteRelIds, 0);
        //更新修改的人员表中班组的信息
        List<Integer> updateNewRelIds = newRelList.stream().map(TSysClassPersonnelRel::getPersonnelId).collect(Collectors.toList());
        tSysPersonnelInfoRepository.updateClassNameByClassId(updateNewRelIds, classId);
        //更新班组中组员数量
        TSysClass tSysClass = this.getClassById(classId);
        tSysClass.setTeamNum(String.valueOf(personnelList.size()));
        this.saveTSysClass(tSysClass);
    }

    @Transactional
    @Override
    public void saveTSysClassGroupLeaderRel(Integer classId, List<TSysClassGroupLeaderRel> tSysClassGroupLeaderRelList) {
        //获取旧的关联关系
        List<TSysClassGroupLeaderRel> classGroupLeaderRelList = classGroupLeaderRepository.findByClassId(classId);
        //如果传入的关联关系为空则删除关系表中对应的关系
        if (CollectionUtils.isEmpty(tSysClassGroupLeaderRelList)) {
            classGroupLeaderRepository.deleteByClassId(classId);
            //更新删除组长关系的人员表中的班组信息
            List<Integer> deleteRelIds = classGroupLeaderRelList.stream().map(TSysClassGroupLeaderRel::getPersonnelId).collect(Collectors.toList());
            tSysPersonnelInfoRepository.updateClassNameByClassId(deleteRelIds, 0);
            return;
        }
        tSysClassGroupLeaderRelList.forEach(t -> {
            t.setClassId(classId);
        });
        //获取需要插入的关系
//        List<TSysClassPersonnelRel> insertRelList = personnelList.stream().filter(item -> item.getClassPersonnelId() == null).collect(Collectors.toList());
        //获取需要更新的关系ID
        List<TSysClassGroupLeaderRel> updateRelList = tSysClassGroupLeaderRelList.stream().filter(item -> item.getClassGroupLeaderId() != null).collect(Collectors.toList());
        List<Integer> updateRelIds = updateRelList.stream().map(TSysClassGroupLeaderRel::getClassGroupLeaderId).collect(Collectors.toList());
        //获取需求删除的关系
        List<TSysClassGroupLeaderRel> removeRelList = classGroupLeaderRelList.stream().filter(item -> !updateRelIds.contains(item.getClassGroupLeaderId())).collect(Collectors.toList());

        classGroupLeaderRepository.deleteInBatch(removeRelList);
        List<TSysClassGroupLeaderRel> newRelList = classGroupLeaderRepository.saveAll(tSysClassGroupLeaderRelList);
        //更新删除组长关系的人员表中的班组信息
        List<Integer> deleteRelIds = removeRelList.stream().map(TSysClassGroupLeaderRel::getPersonnelId).collect(Collectors.toList());
        tSysPersonnelInfoRepository.updateClassNameByClassId(deleteRelIds, 0);
        //更新修改组长的人员表中班组的信息
        List<Integer> updateNewRelIds = newRelList.stream().map(TSysClassGroupLeaderRel::getPersonnelId).collect(Collectors.toList());
        tSysPersonnelInfoRepository.updateClassNameByClassId(updateNewRelIds, classId);

    }

    public List<TSysClassGroupLeaderRel> getTSysClassGroupLeaderRelByPersonnelIdAndOtherClass(Integer classId, List<Integer> personnelId) {
        List<Object[]> select = classGroupLeaderRepository.getRelByPersonnelIdAndOtherClass(classId, personnelId);
        try {
            List<TSysClassGroupLeaderRel> castEntity = EntityUtils.castEntity(select, TSysClassGroupLeaderRel.class, new TSysClassGroupLeaderRel());
            return castEntity;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserClassVo getUserClass(String userId) throws JsonProcessingException {
        UserClassVo userClassVo;
        Map<String, Object> map;
        map = tSysClassRepository.getUserClass(userId);

        if (map == null || map.size() == 0) {
            map = tSysClassRepository.getUserClass2(userId);
            if (map == null || map.size() == 0) {
                throw new RuntimeException("用户班别信息不存在");
            }
        }
        userClassVo = new UserClassVo();
        userClassVo.setClassId(map.get("class_id") == null ? null : Integer.parseInt(String.valueOf(map.get("class_id"))));
        userClassVo.setName(map.get("name") == null ? "" : String.valueOf(map.get("name")));
        userClassVo.setGroupLeader(map.get("groupleader") == null ? "" : String.valueOf(map.get("groupleader")));
        userClassVo.setGroupSize(map.get("groupsize") == null ? 0 : Integer.parseInt(String.valueOf(map.get("groupsize"))));
        userClassVo.setProcess(map.get("process") == null ? "" : String.valueOf(map.get("process")));
        userClassVo.setProcessId(map.get("processId") == null ? null : Integer.parseInt(String.valueOf(map.get("processid"))));
        TSysPersonnelInfo byUserId = tSysPersonnelInfoRepository.findByUserId(userId);
        userClassVo.setPersonId(byUserId == null ? null : byUserId.getPersonnelId());
        return userClassVo;
    }

}

