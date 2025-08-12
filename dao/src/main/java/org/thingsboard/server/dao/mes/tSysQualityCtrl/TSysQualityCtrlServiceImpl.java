package org.thingsboard.server.dao.mes.tSysQualityCtrl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.sys.*;
import org.thingsboard.server.dao.mes.dto.TSysQualityCtrlDto;
import org.thingsboard.server.dao.sql.mes.tSysQualityCtrl.TSysQualityCtrlDetailRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityCtrl.TSysQualityCtrlRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityPlan.TSysQualityPlanConfigRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityPlan.TSysQualityPlanJudgmentRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityPlan.TSysQualityPlanRepository;
import org.thingsboard.server.dao.util.JsonUtil;
import org.thingsboard.server.dao.util.StringConverterUtil;
import org.thingsboard.server.dao.mes.vo.TSysQualityCtrlVo;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/18 12:01:01
 */
@Service
@Slf4j
public class TSysQualityCtrlServiceImpl implements TSysQualityCtrlService {

    @Autowired
    TSysQualityCtrlRepository tSysQualityCtrlRepository;

    @Autowired
    TSysQualityCtrlDetailRepository tSysQualityCtrlDetailRepository;

    @Autowired
    TSysQualityPlanRepository tSysQualityPlanRepository;

    @Autowired
    TSysQualityPlanConfigRepository tSysQualityPlanConfigRepository;

    @Autowired
    TSysQualityPlanJudgmentRepository tSysQualityPlanJudgmentRepository;


    @Override
    public Page<TSysQualityCtrl> tSysQualityCtrlList(Integer current, Integer size, String sortField, String sortOrder, TSysQualityCtrlDto tSysQualityCtrlDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time");

        if (!(StringUtils.isBlank(sortField) && StringUtils.isBlank(sortOrder))){
            String converterSortField = StringConverterUtil.camelToSnake(sortField);
            sort = Sort.by(sortOrder.equals("asc")?Sort.Direction.ASC:Sort.Direction.DESC, converterSortField);
        }

        Pageable pageable = PageRequest.of(current, size, sort);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
//                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
////        Example<TSysQualityPlan> example = Example.of(tSysClass, matcher);


        Page<TSysQualityCtrl> tSysQualityCtrlPage = tSysQualityCtrlRepository.findAllBy(tSysQualityCtrlDto, pageable);
//        String code = "SCHEDULING0000";
//        tSysQualityPlanPage.getContent().stream().forEach(tSysQualityPlan1 -> {
//            tSysQualityPlan1.setSchedulingCodeDsc(GlobalConstant.getCodeDscName(code, tSysQualityPlan1.getScheduling()));
//        });
        return tSysQualityCtrlPage;
    }

    @Override
    public void saveTSysQualityCtrlAndDetail(TSysQualityCtrl tSysQualityCtrl, List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList) {
        this.saveTSysQualityCtrl(tSysQualityCtrl);
        this.saveTSysQualityCtrlDetails(tSysQualityCtrl.getId(),tSysQualityCtrlDetailList);


    }

    @Override
    public void saveTSysQualityCtrl(TSysQualityCtrl tSysQualityCtrl) {
        if (tSysQualityCtrl.getId() == null) {
            //新增
            tSysQualityCtrl.setCreateUser(tSysQualityCtrl.getUpdateUser());
            tSysQualityCtrl.setCreateTime(tSysQualityCtrl.getUpdateTime());
        }
        //todo 判定当前状态，如果是提交则添加质检时间(状态枚举还没设置先假定提交为1)
        //保存：0  提交：1  已复核：2
        if ("1".equals(tSysQualityCtrl.getStatus())){
            tSysQualityCtrl.setInspectionDate(new Date());
        }

//        if (StringUtils.isNotEmpty(tSysQualityCtrl.getPlanName())) {
//            List<TSysQualityPlan> sysPlanList = tSysQualityCtrlRepository.findByPlanName(tSysQualityCtrl.getPlanName());
//            if (sysPlanList != null && sysPlanList.size() > 0 && sysPlanList.get(0).getId() != tSysQualityCtrl.getId()) {
//                throw new RuntimeException("方案名称已存在！");
//            }
//        }
        tSysQualityCtrlRepository.saveAndFlush(tSysQualityCtrl);
        //todo 生成编码：Q+年月日+3位流水号，例如：Q20250427001
        if (StringUtils.isEmpty(tSysQualityCtrl.getQualityCtrlNo())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String date = dateFormat.format(new Date());

            String strNum = String.format("%03d", tSysQualityCtrl.getId());
            tSysQualityCtrl.setQualityCtrlNo("Q" +date+ strNum);
            tSysQualityCtrlRepository.saveAndFlush(tSysQualityCtrl);
        }

    }

    @Override
    public void saveTSysQualityCtrlDetails(Integer ctrlId, List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList) {
        tSysQualityCtrlDetailRepository.deleteByCtrlId(ctrlId);
        tSysQualityCtrlDetailList.forEach(tSysQualityCtrlDetail -> tSysQualityCtrlDetail.setCtrlId(ctrlId));
        tSysQualityCtrlDetailRepository.saveAll(tSysQualityCtrlDetailList);



    }

    @Override
    public void deleteTSysQualityCtrl(Integer ctrlId) {

    }

    @Override
    public TSysQualityCtrlVo getQualityPlanById(Integer planId) {
        //获取方案相关信息


//        TSysQualityPlanVo vo = new TSysQualityPlanVo();
        TSysQualityCtrlVo vo = new TSysQualityCtrlVo();
        List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList = new ArrayList<>();



        TSysQualityPlan tSysQualityPlan = tSysQualityPlanRepository.findById(planId).orElse(null);
        BeanUtils.copyProperties(tSysQualityPlan, vo);
        vo.setPlanId(tSysQualityPlan.getId());
        vo.setId(null);




        List<TSysQualityPlanJudgment> tSysQualityPlanJudgmentList =tSysQualityPlanJudgmentRepository.findByPlanId(planId);
//        vo.settSysQualityPlanJudgmentList(tSysQualityPlanJudgmentList);
        //转换Json串
        List<List<String>> tableData = new ArrayList<>();
        for (TSysQualityPlanJudgment tSysQualityPlanJudgment:
        tSysQualityPlanJudgmentList) {
            List<String> l = JsonUtil.beanToListStrConverter(
                    "",
                    StringUtils.defaultString(tSysQualityPlanJudgment.getFieldName()),
                    "",
                    StringUtils.defaultString(tSysQualityPlanJudgment.getIsEnabled()),
                    StringUtils.defaultString(tSysQualityPlanJudgment.getFieldType()),
                    "",
                    StringUtils.defaultString(tSysQualityPlanJudgment.getDropdownFields()),
                    StringUtils.defaultString(tSysQualityPlanJudgment.getUnit()),
                    StringUtils.defaultString(tSysQualityPlanJudgment.getIsRequired()));
            tableData.add(l);
        }
        String json = JsonUtil.tableToJsonConverter2(tableData);

        List<TSysQualityPlanConfig> tSysQualityPlanConfigList =tSysQualityPlanConfigRepository.findByPlanId(planId);


        for (TSysQualityPlanConfig planConfig:
        tSysQualityPlanConfigList) {
            TSysQualityCtrlDetail ctrlDetail = new TSysQualityCtrlDetail();
            BeanUtils.copyProperties(planConfig, ctrlDetail);
            ctrlDetail.setJudgmentData(json);
            ctrlDetail.setId(null);


            tSysQualityCtrlDetailList.add(ctrlDetail);
        }
        vo.setTSysQualityCtrlDetailList(tSysQualityCtrlDetailList);


        return vo;
    }

    @Override
    public TSysQualityCtrlVo getQualityCtrlById(Integer id) {
        TSysQualityCtrlVo vo = new TSysQualityCtrlVo();
        TSysQualityCtrl tSysQualityCtrl = tSysQualityCtrlRepository.findById(id).orElse(null);

        if (tSysQualityCtrl != null) {
            BeanUtils.copyProperties(tSysQualityCtrl, vo);
            List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList = tSysQualityCtrlDetailRepository.findByCtrlId(id);
            vo.setTSysQualityCtrlDetailList(tSysQualityCtrlDetailList);
        }

        return vo;
    }
}
