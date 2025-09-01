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
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.*;
import org.thingsboard.server.dao.mes.dto.TSysQualityCtrlDto;
import org.thingsboard.server.dao.mes.vo.TSysQualityCtrlVo;
import org.thingsboard.server.dao.sql.mes.tSysQualityCtrl.TSysQualityCtrlDetailRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityCtrl.TSysQualityCtrlRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityPlan.TSysQualityPlanConfigRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityPlan.TSysQualityPlanJudgmentRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityPlan.TSysQualityPlanRepository;
import org.thingsboard.server.dao.util.JsonUtil;
import org.thingsboard.server.dao.util.StringConverterUtil;

import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        if (!(StringUtils.isBlank(sortField) && StringUtils.isBlank(sortOrder))){
            String converterSortField = StringConverterUtil.camelToSnake(sortField);
            // 修复：排序时使用驼峰命名法字段名，而非转换后的下划线格式
            sort = Sort.by(sortOrder.equals("asc")?Sort.Direction.ASC:Sort.Direction.DESC, sortField);
        }

        Pageable pageable = PageRequest.of(current, size, sort);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
//                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
////        Example<TSysQualityPlan> example = Example.of(tSysClass, matcher);

//        Page<TSysQualityCtrl> tSysQualityCtrlPage = tSysQualityCtrlRepository.findAllBy(tSysQualityCtrlDto, pageable);

        Page<TSysQualityCtrl> tSysQualityCtrlPage = tSysQualityCtrlRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tSysQualityCtrlDto.getInspectionStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createTime"), tSysQualityCtrlDto.getInspectionStartTime()));
            }

            if (tSysQualityCtrlDto.getInspectionEndTime() != null) {
                // 结束时间处理：将结束时间设置为当天的23:59:59，确保能查询到一整天的数据
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tSysQualityCtrlDto.getInspectionEndTime());
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);

                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createTime"), calendar.getTime()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        tSysQualityCtrlPage.getContent().forEach(ctrl -> {
            if (ctrl.getCreateTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    ctrl.setCreateTime(sdf.parse(sdf.format(ctrl.getCreateTime())));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
//        String code = "SCHEDULING0000";
//        tSysQualityPlanPage.getContent().stream().forEach(tSysQualityPlan1 -> {
//            tSysQualityPlan1.setSchedulingCodeDsc(GlobalConstant.getCodeDscName(code, tSysQualityPlan1.getScheduling()));
//        });
        return tSysQualityCtrlPage;
    }

    @Override
    @Transactional
    public TSysQualityCtrlVo saveTSysQualityCtrlAndDetail(TSysQualityCtrl tSysQualityCtrl, List<TSysQualityCtrlDetail> tSysQualityCtrlDetailList) {
        // 保存主表和明细表
        this.saveTSysQualityCtrl(tSysQualityCtrl);
        this.saveTSysQualityCtrlDetails(tSysQualityCtrl.getId(), tSysQualityCtrlDetailList);

        // 构造并返回TSysQualityCtrlVo对象
        TSysQualityCtrlVo vo = new TSysQualityCtrlVo();
        BeanUtils.copyProperties(tSysQualityCtrl, vo);
        vo.setTSysQualityCtrlDetailList(tSysQualityCtrlDetailList);

        // 设置明细列表中的ctrlId
        tSysQualityCtrlDetailList.forEach(detail -> detail.setCtrlId(tSysQualityCtrl.getId()));

        return vo;
    }

    @Override
    @Transactional
    public void saveTSysQualityCtrl(TSysQualityCtrl tSysQualityCtrl) {
        if (tSysQualityCtrl.getId() == null) {
            //新增
            tSysQualityCtrl.setCreateUser(tSysQualityCtrl.getUpdateUser());
            tSysQualityCtrl.setCreateTime(tSysQualityCtrl.getUpdateTime());
        }else{
            //获取当前时间
            tSysQualityCtrl.setUpdateTime(new Date());
        }

        TSysQualityCtrl existingRecord = null;
        if (tSysQualityCtrl.getId() != null) {
            existingRecord = tSysQualityCtrlRepository.findById(tSysQualityCtrl.getId()).orElse(null);
        }

        //todo 判定当前状态，如果是提交则添加质检时间(状态枚举还没设置先假定提交为1)
        //保存：0  提交：1  已复核：2
        if ("1".equals(tSysQualityCtrl.getStatus())){
            tSysQualityCtrl.setInspectionDate(new Date());
        } else if ("2".equals(tSysQualityCtrl.getStatus()) && existingRecord != null) {
            // 已复核状态时保留原有的质检时间
            tSysQualityCtrl.setInspectionDate(existingRecord.getInspectionDate());
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
    @Transactional
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

    @Override
    public Page<TSysQualityCtrl> tSysQualityCtrlCheckList(Integer current, Integer size, String sortField, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        if (!(StringUtils.isBlank(sortField) && StringUtils.isBlank(sortOrder))) {
            String converterSortField = StringConverterUtil.camelToSnake(sortField);
            sort = Sort.by(sortOrder.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, converterSortField);
        }

        Pageable pageable = PageRequest.of(current, size, sort);

        // 假设状态"1"表示"已提交"，需要复核的记录
        Page<TSysQualityCtrl> tSysQualityCtrlPage = tSysQualityCtrlRepository.findByStatusIn(List.of("1", "2"), pageable);

        return tSysQualityCtrlPage;
    }
}
