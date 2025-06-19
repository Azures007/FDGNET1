package org.thingsboard.server.dao.TSysCraftInfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.Utils;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.TSysCraftinfo.TSysCraftInfoService;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.ListMaterialDto;
import org.thingsboard.server.dao.dto.ProcessInfoDto;
import org.thingsboard.server.dao.dto.TSysCraftInfoSaveDto;
import org.thingsboard.server.dao.dto.TSysCraftSearchDto;
import org.thingsboard.server.dao.sql.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.TSysCraftInfo.TSysCraftMaterialRelRepository;
import org.thingsboard.server.dao.sql.TSysCraftInfo.TSysCraftProcessRelRepository;
import org.thingsboard.server.dao.sql.TSysProcessInfo.TSysProcessClassRelRepository;
import org.thingsboard.server.dao.sql.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.sync.SyncMaterialRepository;
import org.thingsboard.server.dao.sql.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.vo.PageVo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: l
 * @Date: 2022/4/21 16:43
 * @Description:
 */
@Service
@Slf4j
public class TSysCraftInfoServiceImpl implements TSysCraftInfoService {
    @Autowired
    TSysCraftInfoRepository tSysCraftInfoRepository;
    @Autowired
    TSysCraftProcessRelRepository tSysCraftProcessRelRepository;
    @Autowired
    TSysProcessInfoRepository processInfoRepository;
    @Autowired
    TSysProcessClassRelRepository processClassRelRepository;
    @Autowired
    TSysClassRepository classRepository;
    @Autowired
    TSysCraftMaterialRelRepository tSysCraftMaterialRelRepository;
    @Autowired
    SyncMaterialRepository syncMaterialRepository;

    @Override
    public void save(TSysCraftInfoSaveDto craftInfoSaveDto) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        Date effectiveTime = craftInfoSaveDto.getEffectiveTime();
        Date failureTime = craftInfoSaveDto.getFailureTime();
        try {
            Date effectiveTime2 = df.parse(df2.format(effectiveTime) + " 00:00:00");
            Date failureTime2 = df.parse(df2.format(failureTime) + " 23:59:59");
            if (effectiveTime2.compareTo(failureTime2) > 0) {
                throw new RuntimeException("工艺路线的有效时间不能小于失效时间");
            }
//            else {
//                craftInfoSaveDto.setEffectiveTime(effectiveTime2);
//                craftInfoSaveDto.setFailureTime(failureTime2);
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //提示绑定生产组织和生产车间
        if (craftInfoSaveDto.getKdOrgId() == null && craftInfoSaveDto.getKdDeptId() == null) {
            throw new RuntimeException("请绑定生产车间和生产组织");
        }
        if (craftInfoSaveDto.getKdDeptId() == null) {
            throw new RuntimeException("请绑定生产车间");
        }
        if (craftInfoSaveDto.getKdOrgId() == null) {
            throw new RuntimeException("请绑定生产组织");
        }
        //插入工艺路线
        TSysCraftInfo craftInfo = new TSysCraftInfo();
        BeanUtils.copyProperties(craftInfoSaveDto, craftInfo);
        if (craftInfo.getCraftId() == null) {
            craftInfo.setCreatedUser(craftInfo.getUpdatedUser());
            craftInfo.setCreatedTime(craftInfo.getUpdatedTime());
        } else {
            if (tSysCraftInfoRepository.findById(craftInfo.getCraftId()).isEmpty()) {
                TSysCraftInfo info = tSysCraftInfoRepository.findById(craftInfo.getCraftId()).get();
                craftInfo.setCreatedTime(info.getCreatedTime());
                craftInfo.setCreatedUser(craftInfo.getCreatedUser());
            }
        }
        craftInfo = tSysCraftInfoRepository.saveAndFlush(craftInfo);
        //生成工艺路线编码：GYLX+三位的ID流水号
        if (StringUtils.isEmpty(craftInfo.getCraftNumber())) {
            String strNum = String.format("%03d", craftInfo.getCraftId());
            craftInfo.setCraftNumber("GYLX" + strNum);
            tSysCraftInfoRepository.saveAndFlush(craftInfo);
        }
        //插入工序工艺关系
        List<ProcessInfoDto> processInfoDtos = craftInfoSaveDto.getProcessInfos();
        List<TSysCraftProcessRel> rels = new ArrayList<>();
        tSysCraftProcessRelRepository.deleteByCraftId(craftInfo.getCraftId());
        for (ProcessInfoDto processInfoDto : processInfoDtos) {
            TSysCraftProcessRel rel = new TSysCraftProcessRel();
            rel.setCraftId(craftInfo.getCraftId());
            rel.setProcessId(processInfoDto.getProcessId());
            rel.setSort(processInfoDto.getSort());
            rel.setIsReportingBindCode(processInfoDto.getIsReportingBindCode() != null && processInfoDto.getIsReportingBindCode() == true ? Integer.parseInt(GlobalConstant.enableTrue) : Integer.parseInt(GlobalConstant.enableFalse));
            rel.setIsReceivingBindCode(processInfoDto.getIsReceivingBindCode() != null && processInfoDto.getIsReceivingBindCode() == true ? Integer.parseInt(GlobalConstant.enableTrue) : Integer.parseInt(GlobalConstant.enableFalse));
            rel.setIsReceivingUnBindCode(processInfoDto.getIsReceivingUnBindCode() != null && processInfoDto.getIsReceivingUnBindCode() == true ? Integer.parseInt(GlobalConstant.enableTrue) : Integer.parseInt(GlobalConstant.enableFalse));
            rels.add(rel);
        }
        tSysCraftProcessRelRepository.saveAll(rels);
        tSysCraftProcessRelRepository.flush();
    }

    @Override
    public PageVo<TSysCraftInfoSaveDto> list(Integer current, Integer size, TSysCraftSearchDto searchDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("craftName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("craftNumber", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysCraftInfo craftInfo = new TSysCraftInfo();
        if (StringUtils.isEmpty(searchDto.getCraftName())) {
            searchDto.setCraftName(null);
        }
        if (StringUtils.isEmpty(searchDto.getCraftNumber())) {
            searchDto.setCraftNumber(null);
        }
        BeanUtils.copyProperties(searchDto, craftInfo);

        Example<TSysCraftInfo> example = Example.of(craftInfo, matcher);
        Page<TSysCraftInfo> craftInfos = tSysCraftInfoRepository.findAll(example, pageable);
        List<TSysCraftInfoSaveDto> dtos = new ArrayList<>();
        for (TSysCraftInfo info : craftInfos) {
            TSysCraftInfoSaveDto sysCraftInfoSaveDto = new TSysCraftInfoSaveDto();
            BeanUtils.copyProperties(info, sysCraftInfoSaveDto);
            List<TSysCraftProcessRel> processRels = tSysCraftProcessRelRepository.findByCraftId(info.getCraftId(), Sort.by(Sort.Direction.ASC, "sort"));
            List<ProcessInfoDto> processInfoDtos = new ArrayList<>();
            for (TSysCraftProcessRel processRel : processRels) {
                TSysProcessInfo processInfo = processInfoRepository.findById(processRel.getProcessId()).orElse(null);
                ProcessInfoDto processInfoDto = new ProcessInfoDto();
                BeanUtils.copyProperties(processRel, processInfoDto);
                if (null != processInfo) {
                    processInfoDto.setProcessId(processInfo.getProcessId());
                    processInfoDto.setProcessName(processInfo.getProcessName());
                }
                processInfoDto.setIsReportingBindCode(processRel.getIsReportingBindCode() == null ? false : processRel.getIsReportingBindCode() == Integer.parseInt(GlobalConstant.enableTrue));
                processInfoDto.setIsReceivingBindCode(processRel.getIsReceivingBindCode() == null ? false : processRel.getIsReceivingBindCode() == Integer.parseInt(GlobalConstant.enableTrue));
                processInfoDto.setIsReceivingUnBindCode(processRel.getIsReceivingUnBindCode() == null ? false : processRel.getIsReceivingUnBindCode() == Integer.parseInt(GlobalConstant.enableTrue));
                processInfoDtos.add(processInfoDto);
            }
            sysCraftInfoSaveDto.setProcessInfos(processInfoDtos);
            dtos.add(sysCraftInfoSaveDto);
        }
        PageVo<TSysCraftInfoSaveDto> pageVo = new PageVo<>();
        pageVo.setList(dtos);
        pageVo.setTotal((int) craftInfos.getTotalElements());
        pageVo.setCurrent(current);
        pageVo.setSize(size);
        return pageVo;
    }

    @Override
    public TSysCraftInfoSaveDto detail(Integer craftId) {
        TSysCraftInfoSaveDto saveDto = new TSysCraftInfoSaveDto();
        TSysCraftInfo craftInfo = tSysCraftInfoRepository.findById(craftId).orElse(null);
        BeanUtils.copyProperties(craftInfo, saveDto);
        List<TSysCraftProcessRel> processRels = tSysCraftProcessRelRepository.findByCraftId(craftId, Sort.by(Sort.Direction.ASC, "sort"));
        List<ProcessInfoDto> processInfoDtos = new ArrayList<>();
        for (TSysCraftProcessRel processRel : processRels) {
            ProcessInfoDto processInfoDto = new ProcessInfoDto();
            BeanUtils.copyProperties(processRel, processInfoDto);
            TSysProcessInfo processInfo = processInfoRepository.findById(processRel.getProcessId()).orElse(null);
            processInfoDto.setProcessName(processInfo == null ? "" : processInfo.getProcessName());
            //查询班组信息
            List<TSysProcessClassRel> classRels = processClassRelRepository.findByProcessId(processRel.getProcessId());
            StringBuilder className = new StringBuilder();
            for (TSysProcessClassRel classRel : classRels) {
                TSysClass tSysClass = classRepository.findById(classRel.getClassId()).orElse(null);
                className.append(tSysClass == null ? "" : tSysClass.getName());
                className.append(",");
            }
            if (className.length() > 0) {
                processInfoDto.setClassName(className.toString().substring(0, className.length() - 1));
            }
            processInfoDto.setIsReportingBindCode(processRel.getIsReportingBindCode() == null ? false : processRel.getIsReportingBindCode() == Integer.parseInt(GlobalConstant.enableTrue));
            processInfoDto.setIsReceivingBindCode(processRel.getIsReceivingBindCode() == null ? false : processRel.getIsReceivingBindCode() == Integer.parseInt(GlobalConstant.enableTrue));
            processInfoDto.setIsReceivingUnBindCode(processRel.getIsReceivingUnBindCode() == null ? false : processRel.getIsReceivingUnBindCode() == Integer.parseInt(GlobalConstant.enableTrue));
            processInfoDtos.add(processInfoDto);
        }
        saveDto.setProcessInfos(processInfoDtos);
        return saveDto;
    }

    @Override
    public TSysProcessInfo getCraftNextProcessInfo(Integer craftId, Integer processId) {
        List<TSysCraftProcessRel> processRels = tSysCraftProcessRelRepository.findByCraftId(craftId, Sort.by(Sort.Direction.ASC, "sort"));
        boolean nextFlag = false;
        for (TSysCraftProcessRel processRel : processRels) {
            if (processRel.getProcessId().intValue() == processId.intValue()) {
                nextFlag = true;
            }
            if (nextFlag) {
                return processInfoRepository.findById(processRel.getProcessId()).orElse(null);
            }
        }
        return null;
    }

    @Override
    public void delete(Integer craftId) {
        tSysCraftProcessRelRepository.deleteByCraftId(craftId);
        tSysCraftInfoRepository.deleteById(craftId);
    }

    @Override
    public void enable(Integer craftId, Integer enable, String name) {
        TSysCraftInfo craftInfo = tSysCraftInfoRepository.findById(craftId).get();
        craftInfo.setUpdatedTime(new Date());
        craftInfo.setUpdatedUser(name);
        craftInfo.setEnabled(enable);
        tSysCraftInfoRepository.saveAndFlush(craftInfo);
    }

    @Override
    public TSysCraftInfo getAndCheck(Integer craftId) throws ParseException {
        TSysCraftInfo tSysCraftInfo = tSysCraftInfoRepository.findById(craftId).orElse(null);
        if (null == tSysCraftInfo) {
            throw new RuntimeException("工艺路线不存在");
        }
        if (tSysCraftInfo.getEnabled() == 0) {
            throw new RuntimeException("工艺路线已经禁用");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        Date effectiveTime = tSysCraftInfo.getEffectiveTime();
        Date failureTime = tSysCraftInfo.getFailureTime();
        Date effectiveTime2 = df.parse(df2.format(effectiveTime) + " 00:00:00");
        Date failureTime2 = df.parse(df2.format(failureTime) + " 23:59:59");
        if (!Utils.isEffectiveDate(new Date(), effectiveTime2, failureTime2)) {
            throw new RuntimeException("工艺路线已经超过有效时间");
        }
        return tSysCraftInfo;
    }

    @Override
    public ResponseResult setMaterial(Integer craftId, List<Map> materialCodes, String name) {
        tSysCraftMaterialRelRepository.deleteByCraftId(craftId);
        if (materialCodes.size() > 0) {
            List<TSysCraftMaterialRel> rels = new ArrayList<>();
            for (Map materialCode : materialCodes) {
                TSysCraftMaterialRel rel = new TSysCraftMaterialRel();
                rel.setCrtUser(name);
                rel.setCraftId(craftId);
                rel.setMaterialCode(materialCode.get("materialCode").toString());
                rel.setMaterialId(Integer.parseInt(String.valueOf(materialCode.get("materialId").toString())));
                rel.setCrtTime(new Date());
                rels.add(rel);
            }
            tSysCraftMaterialRelRepository.saveAll(rels);
        }
        return ResultUtil.success();
    }

    @Override
    public ResponseResult materialList(Integer craftId, Integer current, Integer size, ListMaterialDto listMaterialDto) {
//        syncMaterialRepository.
        return null;
    }

    @Override
    public TSysCraftProcessRel findByCraftIdAndProcessId(Integer craftId, Integer processId) {
        return tSysCraftProcessRelRepository.findByCraftIdAndProcessId(craftId, processId);
    }

    @Override
    public TSysCraftInfo findByCraftId(Integer craftId) {
        return tSysCraftInfoRepository.findById(craftId).orElse(null);
    }

    @Override
    public TSysProcessInfo findLastProcessIdByCraftId(Integer craftId) {
        List<TSysCraftProcessRel> tSysCraftProcessRelList = tSysCraftProcessRelRepository.findByCraftId(craftId, Sort.by(Sort.Direction.DESC, "sort"));
        if (tSysCraftProcessRelList.size() > 0) {
            Integer processId = tSysCraftProcessRelList.get(0).getProcessId();
            return processInfoRepository.findById(processId).orElse(null);
        }
        return null;
    }
}