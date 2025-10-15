package org.thingsboard.server.dao.mes.tSysQualityPlan;

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
import org.thingsboard.server.common.data.mes.sys.TSysQualityCategory;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlan;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlanConfig;
import org.thingsboard.server.common.data.mes.sys.TSysQualityPlanJudgment;
import org.thingsboard.server.dao.mes.dto.TSysQualityPlanDto;
import org.thingsboard.server.dao.sql.mes.tSysQualityCategory.TSysQualityCategoryRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityPlan.TSysQualityPlanConfigRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityPlan.TSysQualityPlanJudgmentRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityPlan.TSysQualityPlanRepository;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.util.StringConverterUtil;
import org.thingsboard.server.dao.mes.vo.TSysQualityPlanVo;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/1 17:41:08
 */

@Service
@Slf4j
public class TSysQualityPlanServiceImpl implements TSysQualityPlanService {

    @Autowired
    TSysQualityPlanRepository tSysQualityPlanRepository;

    @Autowired
    TSysQualityPlanConfigRepository tSysQualityPlanConfigRepository;

    @Autowired
    TSysQualityPlanJudgmentRepository tSysQualityPlanJudgmentRepository;
    @Autowired
    protected UserService userService;

    @Autowired
    TSysQualityCategoryRepository tSysQualityCategoryRepository;

    @Override
    public Page<TSysQualityPlan> tSysQualityPlanList(String userId, Integer current,
                                                     Integer size,
                                                     String sortField,
                                                     String sortOrder,
                                                     TSysQualityPlanDto tSysQualityPlanDto) {
        Sort sort = Sort.by(Sort.Direction.ASC, "create_time");

        if (!(StringUtils.isBlank(sortField) && StringUtils.isBlank(sortOrder))){
            String converterSortField = StringConverterUtil.camelToSnake(sortField);
            sort = Sort.by(sortOrder.equals("asc")?Sort.Direction.ASC:Sort.Direction.DESC, converterSortField);
        }

        Pageable pageable = PageRequest.of(current, size, sort);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
//                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysQualityPlan tSysQualityPlan = new TSysQualityPlan();
        BeanUtils.copyProperties(tSysQualityPlanDto, tSysQualityPlan);
//        Example<TSysQualityPlan> example = Example.of(tSysClass, matcher);
        //获取登录的产线
        String cwkid =userService.getUserCurrentCwkid(userId);
        tSysQualityPlan.setProductionLineId(cwkid);


//        tSysQualityPlan.setIsEnabled(StringUtils.isNotBlank(tSysQualityPlan.getIsEnabled()) ? tSysQualityPlan.getIsEnabled() : "");
        //产品名称
        tSysQualityPlan.setPlanName(StringUtils.isNotBlank(tSysQualityPlan.getPlanName())?tSysQualityPlan.getPlanName():"");
        //部门名称
//        tSysQualityPlan.setProductionDepartmentName(StringUtils.isNotBlank(tSysQualityPlan.getProductionDepartmentName())?tSysQualityPlan.getProductionDepartmentName():"");
        //生产线名称
        tSysQualityPlan.setProductionLineName(StringUtils.isNotBlank(tSysQualityPlan.getProductionLineName())?tSysQualityPlan.getProductionLineName():"");
        //启停状态
        tSysQualityPlan.setIsEnabled(StringUtils.isNotBlank(tSysQualityPlan.getIsEnabled()) ? tSysQualityPlan.getIsEnabled() : "");
//        tSysQualityPlan.setIsEnabled("1");

//        tSysQualityPlan.setName(StringUtils.isNotBlank(tSysQualityPlan.getName()) ? tSysQualityPlan.getName() : "");
//        tSysQualityPlan.setClassNumber(StringUtils.isNotBlank(tSysQualityPlan.getClassNumber()) ? tSysQualityPlan.getClassNumber() : "");

        Page<TSysQualityPlan> tSysQualityPlanPage = tSysQualityPlanRepository.findAllBy(tSysQualityPlan, pageable);
//        String code = "SCHEDULING0000";
//        tSysQualityPlanPage.getContent().stream().forEach(tSysQualityPlan1 -> {
//            tSysQualityPlan1.setSchedulingCodeDsc(GlobalConstant.getCodeDscName(code, tSysQualityPlan1.getScheduling()));
//        });
        return tSysQualityPlanPage;
    }

    @Override
    @Transactional
    public void saveTSysQualityPlanDetail(TSysQualityPlan tSysQualityPlan, List<TSysQualityPlanJudgment> tSysQualityPlanJudgmentList, List<TSysQualityPlanConfig> tSysQualityPlanConfigList) {
        this.saveTSysQualityPlan(tSysQualityPlan);
        this.saveTSysQualityPlanJudgment(tSysQualityPlan.getId(),tSysQualityPlanJudgmentList);
        this.saveTSysQualityPlanConfig(tSysQualityPlan.getId(),tSysQualityPlanConfigList);
    }

    @Override
    public void saveTSysQualityPlan(TSysQualityPlan tSysQualityPlan) {
        if (tSysQualityPlan.getId() == null) {
            //新增
            tSysQualityPlan.setCreateUser(tSysQualityPlan.getUpdateUser());
            tSysQualityPlan.setCreateTime(tSysQualityPlan.getUpdateTime());
            if (StringUtils.isBlank(tSysQualityPlan.getIsEnabled())) {
                tSysQualityPlan.setIsEnabled("1");
            }
        } else {
            //编辑 - 保留原有的创建时间和创建人
            TSysQualityPlan existingPlan = tSysQualityPlanRepository.findById(tSysQualityPlan.getId()).orElse(null);
            if (existingPlan != null) {
                tSysQualityPlan.setCreateUser(existingPlan.getCreateUser());
                tSysQualityPlan.setCreateTime(existingPlan.getCreateTime());
            }
        }
        if (StringUtils.isNotEmpty(tSysQualityPlan.getPlanName())) {
            List<TSysQualityPlan> sysPlanList = tSysQualityPlanRepository.findByPlanName(tSysQualityPlan.getPlanName());
            if (sysPlanList != null && sysPlanList.size() > 0 && sysPlanList.get(0).getId() != tSysQualityPlan.getId()) {
                throw new RuntimeException("方案名称已存在！");
            }
        }
        tSysQualityPlanRepository.saveAndFlush(tSysQualityPlan);
//        //生成班别编码：BB+三位的ID流水号
//        if (StringUtils.isEmpty(tSysClass.getClassNumber())) {
//            String strNum = String.format("%03d", tSysClass.getClassId());
//            tSysClass.setClassNumber("BB" + strNum);
//            tSysClassRepository.saveAndFlush(tSysClass);
//        }
    }
    @Override
    public void saveTSysQualityPlanJudgment(Integer planId, List<TSysQualityPlanJudgment> tSysQualityPlanJudgmentList) {
        tSysQualityPlanJudgmentRepository.deleteByPlanId(planId);
        tSysQualityPlanJudgmentList.forEach(tSysQualityPlanJudgment -> tSysQualityPlanJudgment.setPlanId(planId));
        tSysQualityPlanJudgmentRepository.saveAll(tSysQualityPlanJudgmentList);
    }



    @Override
    public void saveTSysQualityPlanConfig(Integer planId, List<TSysQualityPlanConfig> tSysQualityPlanConfigList) {
        tSysQualityPlanConfigRepository.deleteByPlanId(planId);
        tSysQualityPlanConfigList.forEach(tSysQualityPlanConfig -> tSysQualityPlanConfig.setPlanId(planId));
        tSysQualityPlanConfigRepository.saveAll(tSysQualityPlanConfigList);

    }

    @Override
    @Transactional
    public void deleteTSysQualityPlan(Integer planId) {
        tSysQualityPlanRepository.deleteById(planId);
        tSysQualityPlanConfigRepository.deleteByPlanId(planId);
    }

    @Override
    public TSysQualityPlanVo getQualityPlanById(Integer planId) {
        TSysQualityPlanVo vo = new TSysQualityPlanVo();

        TSysQualityPlan tSysQualityPlan = tSysQualityPlanRepository.findById(planId).orElse(null);
        BeanUtils.copyProperties(tSysQualityPlan, vo);

        List<TSysQualityPlanJudgment> tSysQualityPlanJudgmentList =tSysQualityPlanJudgmentRepository.findByPlanId(planId);
        vo.settSysQualityPlanJudgmentList(tSysQualityPlanJudgmentList);

        List<TSysQualityPlanConfig> tSysQualityPlanConfigList =tSysQualityPlanConfigRepository.findByPlanIdOrderByIdAsc(planId);

        for (TSysQualityPlanConfig config : tSysQualityPlanConfigList) {
            if (config.getCategoryId() != null) {
                TSysQualityCategory category = tSysQualityCategoryRepository.findById(config.getCategoryId()).orElse(null);
                if (category != null) {
                    config.setRemarks(category.getRemarks());
                }
            }
        }
        
        vo.settSysQualityPlanConfigList(tSysQualityPlanConfigList);

        if (null == tSysQualityPlan){
            return null;
        }else {
            return vo;
        }


    }

    @Override
    public Page<TSysQualityPlan> tSysQualityPlanListWithEnable(String userId, Integer current, Integer size, String sortField, String sortOrder, TSysQualityPlanDto tSysQualityPlanDto) {
        Sort sort = Sort.by(Sort.Direction.ASC, "create_time");

        if (!(StringUtils.isBlank(sortField) && StringUtils.isBlank(sortOrder))){
            String converterSortField = StringConverterUtil.camelToSnake(sortField);
            sort = Sort.by(sortOrder.equals("asc")?Sort.Direction.ASC:Sort.Direction.DESC, converterSortField);
        }

        Pageable pageable = PageRequest.of(current, size, sort);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
//                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysQualityPlan tSysQualityPlan = new TSysQualityPlan();
        BeanUtils.copyProperties(tSysQualityPlanDto, tSysQualityPlan);
//        Example<TSysQualityPlan> example = Example.of(tSysClass, matcher);
        //获取登录的产线
        String cwkid =userService.getUserCurrentCwkid(userId);
        tSysQualityPlan.setProductionLineId(cwkid);


//        tSysQualityPlan.setIsEnabled(StringUtils.isNotBlank(tSysQualityPlan.getIsEnabled()) ? tSysQualityPlan.getIsEnabled() : "");
        //产品名称
        tSysQualityPlan.setPlanName(StringUtils.isNotBlank(tSysQualityPlan.getPlanName())?tSysQualityPlan.getPlanName():"");
        //部门名称
//        tSysQualityPlan.setProductionDepartmentName(StringUtils.isNotBlank(tSysQualityPlan.getProductionDepartmentName())?tSysQualityPlan.getProductionDepartmentName():"");
        //生产线名称
        tSysQualityPlan.setProductionLineName(StringUtils.isNotBlank(tSysQualityPlan.getProductionLineName())?tSysQualityPlan.getProductionLineName():"");
        //启停状态
//        tSysQualityPlan.setIsEnabled(StringUtils.isNotBlank(tSysQualityPlan.getIsEnabled()) ? tSysQualityPlan.getIsEnabled() : "");
        tSysQualityPlan.setIsEnabled("1");

//        tSysQualityPlan.setName(StringUtils.isNotBlank(tSysQualityPlan.getName()) ? tSysQualityPlan.getName() : "");
//        tSysQualityPlan.setClassNumber(StringUtils.isNotBlank(tSysQualityPlan.getClassNumber()) ? tSysQualityPlan.getClassNumber() : "");

        Page<TSysQualityPlan> tSysQualityPlanPage = tSysQualityPlanRepository.findAllBy(tSysQualityPlan, pageable);
//        String code = "SCHEDULING0000";
//        tSysQualityPlanPage.getContent().stream().forEach(tSysQualityPlan1 -> {
//            tSysQualityPlan1.setSchedulingCodeDsc(GlobalConstant.getCodeDscName(code, tSysQualityPlan1.getScheduling()));
//        });
        return tSysQualityPlanPage;
    }


}
