package org.thingsboard.server.dao.TSysQualityReportCategory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.dao.dto.SysQualityReportCategoryDto;
import org.thingsboard.server.dao.dto.TSysQualityReportCategorySearchDto;
import org.thingsboard.server.dao.dto.TSysQualityReportPlanDto;
import org.thingsboard.server.dao.dto.TSysQualityReportPlanSearchDto;
import org.thingsboard.server.dao.sql.TSysQualityReport.TSysQualityReportCategoryRepository;
import org.thingsboard.server.dao.sql.TSysQualityReport.TSysQualityReportItemRepository;
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


    @Override
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
        reportPlan = reportPlanRepository.saveAndFlush(reportPlan);
        //插入明细
        List<TSysQualityReportPlanRel> items = tSysQualityReportPlanDto.getItemList();
        List<TSysQualityReportPlanRel> rels = new ArrayList<>();
        reportPlanRelRepository.deleteByCategoryId(reportPlan.getId());
        for (TSysQualityReportPlanRel item : items) {
            TSysQualityReportPlanRel rel = new TSysQualityReportPlanRel();
            BeanUtils.copyProperties(item,rel);
            rel.setCategoryId(reportPlan.getId());
            rels.add(rel);
        }
        reportPlanRelRepository.saveAll(rels);
        reportPlanRelRepository.flush();

    }

    @Override
    public TSysQualityReportPlanDto planDetail(Integer id) {
        TSysQualityReportPlanDto saveDto = new TSysQualityReportPlanDto();
        TSysQualityReportPlan plan = reportPlanRepository.findById(id).orElse(null);
        BeanUtils.copyProperties(plan, saveDto);
        List<TSysQualityReportPlanRel> items = reportPlanRelRepository.findByCategoryId(id, Sort.by(Sort.Direction.ASC, "sort"));
        saveDto.setItemList(items);
        return saveDto;
    }

    @Override
    public void delete(Integer id) {
        reportPlanRepository.deleteById(id);
        reportPlanRelRepository.deleteByCategoryId(id);
    }

    @Override
    public PageVo<TSysQualityReportPlanDto> getPlanList(Integer current, Integer size, TSysQualityReportPlanSearchDto searchDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("productName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("prodDeptId", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysQualityReportPlan plan = new TSysQualityReportPlan();
        if (StringUtils.isEmpty(searchDto.getProductName())) {
            searchDto.setEnabled(null);
        }
        if (StringUtils.isEmpty(searchDto.getProdDeptId())) {
            searchDto.setProdDeptId(null);
        }
        BeanUtils.copyProperties(searchDto, plan);

        Example<TSysQualityReportPlan> example = Example.of(plan, matcher);
        Page<TSysQualityReportPlan> craftInfos = reportPlanRepository.findAll(example, pageable);
        List<TSysQualityReportPlanDto> dtos = new ArrayList<>();
        for (TSysQualityReportPlan info : craftInfos) {
            TSysQualityReportPlanDto categoryDto = new TSysQualityReportPlanDto();
            BeanUtils.copyProperties(info, categoryDto);
            List<TSysQualityReportPlanRel> itemList = reportPlanRelRepository.findByCategoryId(info.getId(), Sort.by(Sort.Direction.ASC, "sort"));
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
