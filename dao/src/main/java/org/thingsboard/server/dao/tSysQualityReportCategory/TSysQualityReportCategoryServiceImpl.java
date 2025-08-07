package org.thingsboard.server.dao.tSysQualityReportCategory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.dao.dto.SysQualityReportCategoryDto;
import org.thingsboard.server.dao.dto.TSysQualityReportCategorySearchDto;
import org.thingsboard.server.dao.sql.tSysQualityReport.TSysQualityReportCategoryRepository;
import org.thingsboard.server.dao.sql.tSysQualityReport.TSysQualityReportItemRepository;
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
public class TSysQualityReportCategoryServiceImpl implements TSysQualityReportCategoryService {
    @Autowired
    TSysQualityReportItemRepository reportItemRepository;
    @Autowired
    TSysQualityReportCategoryRepository reportCategoryRepository;


    @Override
    public void saveCategory(SysQualityReportCategoryDto categoryDto) {
        TSysQualityReportCategory category = new TSysQualityReportCategory();
        BeanUtils.copyProperties(categoryDto, category);
        if (category.getId() == null) {
            category.setCreatedMame(category.getUpdatedName());
            category.setCreatedTime(category.getUpdatedTime());
        } else {
            if (reportCategoryRepository.findById(categoryDto.getId()).isPresent()) {
                TSysQualityReportCategory info = reportCategoryRepository.findById(category.getId()).get();
                category.setCreatedTime(info.getCreatedTime());
                category.setCreatedMame(info.getCreatedMame());
            }
        }
        category.setEnabled(1);
        category = reportCategoryRepository.saveAndFlush(category);
        //插入明细
        List<TSysQualityReportItem> items = categoryDto.getItemList();
        List<TSysQualityReportItem> rels = new ArrayList<>();
        reportItemRepository.deleteByCategoryId(category.getId());
        for (TSysQualityReportItem item : items) {
            TSysQualityReportItem rel = new TSysQualityReportItem();
            BeanUtils.copyProperties(item,rel);
            rel.setCategoryId(category.getId());
            rels.add(rel);
        }
        reportItemRepository.saveAll(rels);
        reportItemRepository.flush();

    }

    @Override
    public SysQualityReportCategoryDto categoryDetail(Integer id) {
        SysQualityReportCategoryDto saveDto = new SysQualityReportCategoryDto();
        TSysQualityReportCategory category = reportCategoryRepository.findById(id).orElse(null);
        if(category==null){
            return null;
        }
        BeanUtils.copyProperties(category, saveDto);
        List<TSysQualityReportItem> items = reportItemRepository.findByCategoryId(id, Sort.by(Sort.Direction.ASC, "id"));
        saveDto.setItemList(items);
        return saveDto;
    }

    @Override
    public void delete(Integer id) {
        reportCategoryRepository.deleteById(id);
        reportItemRepository.deleteByCategoryId(id);
    }

    @Override
    public PageVo<SysQualityReportCategoryDto> getCategoryList(Integer current, Integer size, TSysQualityReportCategorySearchDto searchDto) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("frequency", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("importantItem", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysQualityReportCategory category = new TSysQualityReportCategory();
        if (StringUtils.isEmpty(searchDto.getFrequency())) {
            searchDto.setFrequency(null);
        }
        if (StringUtils.isEmpty(searchDto.getImportantItem())) {
            searchDto.setImportantItem(null);
        }
        BeanUtils.copyProperties(searchDto, category);

        Example<TSysQualityReportCategory> example = Example.of(category, matcher);
        Page<TSysQualityReportCategory> categoryInfos = reportCategoryRepository.findAll(example, pageable);
        List<SysQualityReportCategoryDto> dtos = new ArrayList<>();
        for (TSysQualityReportCategory info : categoryInfos) {
            SysQualityReportCategoryDto categoryDto = new SysQualityReportCategoryDto();
            BeanUtils.copyProperties(info, categoryDto);
            List<TSysQualityReportItem> itemList = reportItemRepository.findByCategoryId(info.getId(), Sort.by(Sort.Direction.ASC, "id"));
            categoryDto.setItemList(itemList);
            categoryDto.setEnabled(info.getEnabled());
            dtos.add(categoryDto);
        }
        PageVo<SysQualityReportCategoryDto> pageVo = new PageVo<>();
        pageVo.setList(dtos);
        pageVo.setTotal((int) categoryInfos.getTotalElements());
        pageVo.setCurrent(current);
        pageVo.setSize(size);
        return pageVo;
    }

    @Override
    public void enableCategory(Integer processId, Integer enable, String name) {
        TSysQualityReportCategory category = reportCategoryRepository.findById(processId).get();
        category.setUpdatedTime(new Date());
        category.setUpdatedName(name);
        category.setEnabled(enable);
        reportCategoryRepository.saveAndFlush(category);

    }
}
