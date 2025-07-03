package org.thingsboard.server.dao.TSysQualityCategory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysQualityCategory;
import org.thingsboard.server.common.data.TSysQualityCategoryConfig;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysQualityCategoryDto;
import org.thingsboard.server.dao.sql.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.sql.tSysQualityCategory.TSysQualityCategoryConfigRepository;
import org.thingsboard.server.dao.sql.tSysQualityCategory.TSysQualityCategoryRepository;
import org.thingsboard.server.dao.tSysQualityCategory.TSysQualityCategoryService;
import org.thingsboard.server.dao.vo.TSysQualityCategoryVo;

import java.util.List;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/6/27 17:20:43
 */

@Service
@Slf4j
public class TSysQualityCategoryServiceImpl implements TSysQualityCategoryService {

    @Autowired
    TSysQualityCategoryRepository tSysQualityCategoryRepository;

    @Autowired
    TSysQualityCategoryConfigRepository tSysQualityCategoryConfigRepository;


    @Override
    public Page<TSysQualityCategory> tSysQualityCategoryList(Integer current, Integer size, TSysQualityCategoryDto tSysQualityCategoryDto) {

        Sort sort = Sort.by(Sort.Direction.DESC, "create_time");
        Pageable pageable = PageRequest.of(current, size, sort);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
//                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysQualityCategory tSysQualityCategory = new TSysQualityCategory();
        BeanUtils.copyProperties(tSysQualityCategoryDto, tSysQualityCategory);
//        Example<TSysQualityCategory> example = Example.of(tSysClass, matcher);



//        tSysQualityCategory.setIsEnabled(StringUtils.isNotBlank(tSysQualityCategory.getIsEnabled()) ? tSysQualityCategory.getIsEnabled() : "");
        //检测项目
        tSysQualityCategory.setInspectionItem(StringUtils.isNotBlank(tSysQualityCategory.getInspectionItem())?tSysQualityCategory.getInspectionItem():"");
        //关键工序
        tSysQualityCategory.setKeyProcess(StringUtils.isNotBlank(tSysQualityCategory.getKeyProcess())?tSysQualityCategory.getKeyProcess():"");
        //产品名称
        tSysQualityCategory.setProductName(StringUtils.isNotBlank(tSysQualityCategory.getProductName())?tSysQualityCategory.getProductName():"");
        //启停状态
        tSysQualityCategory.setIsEnabled(StringUtils.isNotBlank(tSysQualityCategory.getIsEnabled()) ? tSysQualityCategory.getIsEnabled() : "");

//        tSysQualityCategory.setName(StringUtils.isNotBlank(tSysQualityCategory.getName()) ? tSysQualityCategory.getName() : "");
//        tSysQualityCategory.setClassNumber(StringUtils.isNotBlank(tSysQualityCategory.getClassNumber()) ? tSysQualityCategory.getClassNumber() : "");

        Page<TSysQualityCategory> tSysQualityCategoryPage = tSysQualityCategoryRepository.findAllBy(tSysQualityCategory, pageable);
//        String code = "SCHEDULING0000";
//        tSysQualityCategoryPage.getContent().stream().forEach(tSysQualityCategory1 -> {
//            tSysQualityCategory1.setSchedulingCodeDsc(GlobalConstant.getCodeDscName(code, tSysQualityCategory1.getScheduling()));
//        });
        return tSysQualityCategoryPage;
    }

    @Override
    public void saveTSysQualityCategoryAndConfig(TSysQualityCategory tSysQualityCategory, List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList) {
        this.saveTSysQualityCategory(tSysQualityCategory);
        this.saveTSysQualityCategoryConfig(tSysQualityCategory.getId(),tSysQualityCategoryConfigList);
    }

    @Override
    public void saveTSysQualityCategory(TSysQualityCategory tSysQualityCategory) {
        if (tSysQualityCategory.getId() == null) {
            //新增
            tSysQualityCategory.setCreateUser(tSysQualityCategory.getUpdateUser());
            tSysQualityCategory.setCreateTime(tSysQualityCategory.getUpdateTime());
            if (StringUtils.isBlank(tSysQualityCategory.getIsEnabled())) {
                tSysQualityCategory.setIsEnabled("1");
            }
        }
        if (StringUtils.isNotEmpty(tSysQualityCategory.getInspectionItem())) {
            List<TSysQualityCategory> sysCategoryList = tSysQualityCategoryRepository.findByInspectionItem(tSysQualityCategory.getInspectionItem());
            if (sysCategoryList != null && sysCategoryList.size() > 0 && sysCategoryList.get(0).getId() != tSysQualityCategory.getId()) {
                throw new RuntimeException("类目名称已存在！");
            }
        }
        tSysQualityCategoryRepository.saveAndFlush(tSysQualityCategory);
//        //生成班别编码：BB+三位的ID流水号
//        if (StringUtils.isEmpty(tSysClass.getClassNumber())) {
//            String strNum = String.format("%03d", tSysClass.getClassId());
//            tSysClass.setClassNumber("BB" + strNum);
//            tSysClassRepository.saveAndFlush(tSysClass);
//        }
    }

    @Override
    public void saveTSysQualityCategoryConfig(Integer categoryId, List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList) {
        tSysQualityCategoryConfigRepository.deleteByCategoryId(categoryId);
        tSysQualityCategoryConfigList.forEach(tSysQualityCategoryConfig -> tSysQualityCategoryConfig.setCategoryId(categoryId));
        tSysQualityCategoryConfigRepository.saveAll(tSysQualityCategoryConfigList);

    }

    @Override
    public void deleteTSysQualityCategory(Integer categoryId) {
        tSysQualityCategoryRepository.deleteById(categoryId);
        tSysQualityCategoryConfigRepository.deleteByCategoryId(categoryId);
    }

    @Override
    public TSysQualityCategoryVo getQualityCategoryById(Integer categoryId) {
        TSysQualityCategoryVo vo = new TSysQualityCategoryVo();


        TSysQualityCategory tSysQualityCategory = tSysQualityCategoryRepository.findById(categoryId).orElse(null);
        BeanUtils.copyProperties(tSysQualityCategory, vo);
        List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList =tSysQualityCategoryConfigRepository.findByCategoryId(categoryId);
        vo.settSysQualityCategoryConfigList(tSysQualityCategoryConfigList);

        if (null == tSysQualityCategory){
            return null;
        }else {
            return vo;
        }


    }


}
