package org.thingsboard.server.dao.tSysQualityReportCategory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.dao.dto.*;
import org.thingsboard.server.dao.sql.TSysQualityReport.TSysQualityReportPlanRelRepository;
import org.thingsboard.server.dao.sql.TSysQualityReport.TSysQualityReportPlanRepository;
import org.thingsboard.server.dao.tSysQualityReportCategory.TSysQualityReportCategoryService;
import org.thingsboard.server.dao.tSysQualityReportCategory.TSysQualityReportPlanService;
import org.thingsboard.server.dao.vo.PageVo;

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
public class TSysQualityReportPlanServiceImpl implements TSysQualityReportPlanService {
    @Autowired
    TSysQualityReportPlanRepository reportPlanRepository;
    @Autowired
    TSysQualityReportPlanRelRepository reportPlanRelRepository;

    @Autowired
    TSysQualityReportCategoryService sysQualityReportCategoryService;


    @Override
    @Transactional
    public void savePlan(TSysQualityReportPlanDto tSysQualityReportPlanDto) {
        TSysQualityReportPlan reportPlan = new TSysQualityReportPlan();
        BeanUtils.copyProperties(tSysQualityReportPlanDto, reportPlan);
        if (reportPlan.getId() == null) {
            reportPlan.setCreatedMame(reportPlan.getCreatedMame());
            reportPlan.setCreatedTime(reportPlan.getUpdatedTime());
        } else {
            if (reportPlanRepository.findById(reportPlan.getId()).isEmpty()) {
                TSysQualityReportPlan info = reportPlanRepository.findById(reportPlan.getId()).get();
                reportPlan.setCreatedTime(info.getCreatedTime());
                reportPlan.setCreatedMame(reportPlan.getCreatedMame());
            }
        }
        reportPlan.setEnabled(1);
        reportPlan = reportPlanRepository.saveAndFlush(reportPlan);
        //插入明细
        List<TSysQualityReportPlanRel> items = tSysQualityReportPlanDto.getItemList();
        List<TSysQualityReportPlanRel> rels = new ArrayList<>();
        reportPlanRelRepository.deleteByPlanId(reportPlan.getId());
        for (TSysQualityReportPlanRel item : items) {
            TSysQualityReportPlanRel rel = new TSysQualityReportPlanRel();
            BeanUtils.copyProperties(item,rel);
            rel.setPlanId(reportPlan.getId());
            rels.add(rel);
        }
        reportPlanRelRepository.saveAll(rels);
        reportPlanRelRepository.flush();

    }

    @Override
    public TSysQualityReportPlanVo planDetail(Integer id) {
        TSysQualityReportPlanVo saveDto = new TSysQualityReportPlanVo();
        TSysQualityReportPlan plan = reportPlanRepository.findById(id).orElse(null);
        BeanUtils.copyProperties(plan, saveDto);
        List<TSysQualityReportPlanRel> items = reportPlanRelRepository.findByPlanId(id, Sort.by(Sort.Direction.ASC, "id"));
        List<SysQualityReportCategoryDto> dtoList=new ArrayList<>();
        for (TSysQualityReportPlanRel item : items) {
            SysQualityReportCategoryDto dto=sysQualityReportCategoryService.categoryDetail(item.getCategoryId());
            dtoList.add(dto);
        }
        saveDto.setItemList(dtoList);
        return saveDto;
    }

    @Override
    public void delete(Integer id) {
        reportPlanRepository.deleteById(id);
        reportPlanRelRepository.deleteByPlanId(id);
    }

    @Override
    public PageVo<TSysQualityReportPlanDto> getPlanList(Integer current, Integer size, TSysQualityReportPlanSearchDto searchDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("productName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("prodLineName", ExampleMatcher.GenericPropertyMatchers.contains());
        TSysQualityReportPlan plan = new TSysQualityReportPlan();
        if (StringUtils.isEmpty(searchDto.getProductName())) {
            searchDto.setEnabled(null);
        }
        if (StringUtils.isEmpty(searchDto.getProdLineName())) {
            searchDto.setProdLineName(null);
        }
        BeanUtils.copyProperties(searchDto, plan);

        plan.setEnabled(null);
        Example<TSysQualityReportPlan> example = Example.of(plan, matcher);
        Page<TSysQualityReportPlan> craftInfos = reportPlanRepository.findAll(example, pageable);
        List<TSysQualityReportPlanDto> dtos = new ArrayList<>();
        for (TSysQualityReportPlan info : craftInfos) {
            TSysQualityReportPlanDto categoryDto = new TSysQualityReportPlanDto();
            BeanUtils.copyProperties(info, categoryDto);
            List<TSysQualityReportPlanRel> itemList = reportPlanRelRepository.findByPlanId(info.getId(), Sort.by(Sort.Direction.ASC, "id"));
            categoryDto.setItemList(itemList);
            dtos.add(categoryDto);
        }
        PageVo<TSysQualityReportPlanDto> pageVo = new PageVo<>();
        pageVo.setList(dtos);
        pageVo.setTotal((int) craftInfos.getTotalElements());
        pageVo.setCurrent(current);
        pageVo.setSize(size);
        return pageVo;
    }

    @Override
    public void enablePlan(Integer planId, Integer enable, String name) {
        TSysQualityReportPlan plan = reportPlanRepository.findById(planId).get();
        plan.setUpdatedTime(new Date());
        plan.setUpdatedName(name);
        plan.setEnabled(enable);
        reportPlanRepository.saveAndFlush(plan);
    }
}
