package org.thingsboard.server.dao.mes.tSysQualityReportCategory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.mes.sys.TSysQualityReportPlan;
import org.thingsboard.server.common.data.mes.sys.TSysQualityReportPlanRel;
import org.thingsboard.server.dao.mes.dto.SysQualityReportCategoryDto;
import org.thingsboard.server.dao.mes.dto.TSysQualityReportPlanDto;
import org.thingsboard.server.dao.mes.dto.TSysQualityReportPlanSearchDto;
import org.thingsboard.server.dao.mes.dto.TSysQualityReportPlanVo;
import org.thingsboard.server.dao.sql.mes.tSysQualityReport.TSysQualityReportPlanRelRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityReport.TSysQualityReportPlanRepository;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.mes.vo.PageVo;

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

    @Autowired
    protected UserService userService;
    @Override
    @Transactional
    public void savePlan(TSysQualityReportPlanDto tSysQualityReportPlanDto) {
        TSysQualityReportPlan reportPlan = new TSysQualityReportPlan();
        BeanUtils.copyProperties(tSysQualityReportPlanDto, reportPlan);
        if (reportPlan.getId() == null) {
            reportPlan.setCreatedMame(reportPlan.getUpdatedName());
            reportPlan.setCreatedTime(reportPlan.getUpdatedTime());
        } else {
            if (!reportPlanRepository.findById(reportPlan.getId()).isEmpty()) {
                TSysQualityReportPlan info = reportPlanRepository.findById(reportPlan.getId()).get();
                reportPlan.setCreatedTime(info.getCreatedTime());
                reportPlan.setCreatedMame(info.getCreatedMame());
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
    public PageVo<TSysQualityReportPlanDto> getPlanList(String userId,Integer current, Integer size, TSysQualityReportPlanSearchDto searchDto,Integer enabled) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(current, size, sort);
        List<String> cwkids =userService.getUserCurrentCwkid(userId);
        Page<TSysQualityReportPlan> planInfos = reportPlanRepository.findAll((root, query, cb) -> {
            List<javax.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            // 产线IN过滤
            if (cwkids != null && !cwkids.isEmpty()) {
                predicates.add(root.get("prodLineId").in(cwkids));
            }

            // 启用状态
            if (enabled != null) {
                predicates.add(cb.equal(root.get("enabled"), enabled));
            }

            // 产品名称 模糊
            if (!StringUtils.isEmpty(searchDto.getProductName())) {
                predicates.add(cb.like(root.get("productName"), "%" + searchDto.getProductName() + "%"));
            }

            // 产线名称 模糊
            if (!StringUtils.isEmpty(searchDto.getProdLineName())) {
                predicates.add(cb.like(root.get("prodLineName"), "%" + searchDto.getProdLineName() + "%"));
            }

            return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        }, pageable);
        List<TSysQualityReportPlanDto> dtos = new ArrayList<>();
        for (TSysQualityReportPlan info : planInfos) {
            TSysQualityReportPlanDto categoryDto = new TSysQualityReportPlanDto();
            BeanUtils.copyProperties(info, categoryDto);
            List<TSysQualityReportPlanRel> itemList = reportPlanRelRepository.findByPlanId(info.getId(), Sort.by(Sort.Direction.ASC, "id"));
            categoryDto.setItemList(itemList);
            dtos.add(categoryDto);
        }
        PageVo<TSysQualityReportPlanDto> pageVo = new PageVo<>();
        pageVo.setList(dtos);
        pageVo.setTotal((int) planInfos.getTotalElements());
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
