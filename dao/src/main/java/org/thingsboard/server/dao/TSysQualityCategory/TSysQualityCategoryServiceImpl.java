package org.thingsboard.server.dao.TSysQualityCategory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.dao.dto.TSysQualityCategoryDto;
import org.thingsboard.server.dao.sql.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.dao.sql.tSysCodeDsc.TSysCodeDscVersionRelRepository;
import org.thingsboard.server.dao.sql.tSysCodeDsc.TSysCodeDscVersionRepository;
import org.thingsboard.server.dao.sql.tSysQualityCategory.TSysQualityCategoryConfigRepository;
import org.thingsboard.server.dao.sql.tSysQualityCategory.TSysQualityCategoryRepository;
import org.thingsboard.server.dao.tSysQualityCategory.TSysQualityCategoryService;
import org.thingsboard.server.dao.util.JsonUtil;
import org.thingsboard.server.dao.vo.TSysQualityCategoryVo;

import org.thingsboard.server.dao.util.StringConverterUtil;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    TSysCodeDscVersionRelRepository tSysCodeDscVersionRelRepository;

    @Autowired
    TSysCodeDscRepository tSysCodeDscRepository;

    @Autowired
    TSysCodeDscVersionRepository tSysCodeDscVersionRepository;




    @Override
    public Page<TSysQualityCategory> tSysQualityCategoryList(Integer current,
                                                             Integer size,
                                                             String sortField,
                                                             String sortOrder,
                                                             TSysQualityCategoryDto tSysQualityCategoryDto) {

        Sort sort = Sort.by(Sort.Direction.DESC, "create_time");
        if (!(StringUtils.isBlank(sortField) && StringUtils.isBlank(sortOrder))){
            String converterSortField = StringConverterUtil.camelToSnake(sortField);
            sort = Sort.by(sortOrder.equals("asc")?Sort.Direction.ASC:Sort.Direction.DESC, converterSortField);
        }

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
    public Page<TSysQualityPlanConfig> getTSysQualityCategoryListToPlan(Integer current, Integer size, TSysQualityCategoryDto tSysQualityCategoryDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time");
        Pageable pageable = PageRequest.of(current, size, sort);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
//                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysQualityCategory tSysQualityCategory = new TSysQualityCategory();
        BeanUtils.copyProperties(tSysQualityCategoryDto, tSysQualityCategory);
        //检测项目
        tSysQualityCategory.setInspectionItem(StringUtils.isNotBlank(tSysQualityCategory.getInspectionItem())?tSysQualityCategory.getInspectionItem():"");
        //关键工序
        tSysQualityCategory.setKeyProcess(StringUtils.isNotBlank(tSysQualityCategory.getKeyProcess())?tSysQualityCategory.getKeyProcess():"");
        //产品名称
        tSysQualityCategory.setProductName(StringUtils.isNotBlank(tSysQualityCategory.getProductName())?tSysQualityCategory.getProductName():"");
        //启停状态
        tSysQualityCategory.setIsEnabled(StringUtils.isNotBlank(tSysQualityCategory.getIsEnabled()) ? tSysQualityCategory.getIsEnabled() : "");
        //先查出分页的质检类目信息，再组装json串
        Page<TSysQualityCategory> tSysQualityCategoryPage = tSysQualityCategoryRepository.findAllBy(tSysQualityCategory, pageable);

        List<TSysQualityCategory> processedContent = tSysQualityCategoryPage.getContent();
        // 处理内容列表


        //提取ID的合集
        List<Integer> categoryIdList = processedContent.stream().map(TSysQualityCategory::getId).collect(Collectors.toList());

        //根据ID合集查询所有的类目配置
        List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList =tSysQualityCategoryConfigRepository.findByCategoryIdIn(categoryIdList);
        //根据CategoryId对类目配置信息进行分组
        Map<Integer, List<TSysQualityCategoryConfig>> configMap = tSysQualityCategoryConfigList.stream()
                .collect(Collectors.groupingBy(TSysQualityCategoryConfig::getCategoryId));

        //用于装载数据的list
        List<TSysQualityPlanConfig> returnPlanConfigList = new ArrayList<>();
        //循环整理数据
        for (TSysQualityCategory item:
        processedContent) {
            Integer categoryId = item.getId();
            List<TSysQualityCategoryConfig> relatedConfigs = configMap.getOrDefault(categoryId, Collections.emptyList());

            //转换Json串
            List<List<String>> tableData = new ArrayList<>();
            for (TSysQualityCategoryConfig categoryConfig:
            relatedConfigs) {

                List<String> l = JsonUtil.beanToListStrConverter(StringUtils.defaultString(categoryConfig.getMaterialName()),
                        StringUtils.defaultString(categoryConfig.getFieldName()),
                        "",
                        StringUtils.defaultString(categoryConfig.getIsEnabled()),
                        StringUtils.defaultString(categoryConfig.getFieldType()),
                        StringUtils.defaultString(categoryConfig.getParameterRange()),
                        StringUtils.defaultString(categoryConfig.getDropdownFields()),
                        StringUtils.defaultString(categoryConfig.getUnit()),
                        StringUtils.defaultString(categoryConfig.getIsRequired()));
                tableData.add(l);
            }
            String json = JsonUtil.tableToJsonConverter2(tableData);

            //组装新的数据
            TSysQualityPlanConfig planConfig = new TSysQualityPlanConfig();
            BeanUtils.copyProperties(item,planConfig);
            planConfig.setCategoryId(item.getId());
            planConfig.setId(null);
            planConfig.setConfigData(json);

            returnPlanConfigList.add(planConfig);


        }

//        List<TSysQualityCategory> processedContent = tSysQualityCategoryPage.getContent().stream()
//                .map(item -> {
//                    // 处理每条数据
//                    item.setStandard(processStandard(item.getStandard()));
//                    return item;
//                })
////                .filter(item -> item.getIsEnabled().equals("1")) // 示例：过滤已启用的项
//                .collect(Collectors.toList());
//
//        // 重新封装为 Page 对象（保持分页元数据不变）
        return new PageImpl<TSysQualityPlanConfig>(returnPlanConfigList, tSysQualityCategoryPage.getPageable(), tSysQualityCategoryPage.getTotalElements());
    }

    @Override
    @Transactional
    public void saveTSysQualityCategoryAndConfig(TSysQualityCategory tSysQualityCategory, List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList) {
        this.saveTSysQualityCategory(tSysQualityCategory);
        this.saveTSysQualityCategoryConfig(tSysQualityCategory.getId(),tSysQualityCategoryConfigList);
        //暂时去除版本控制
//        List<TSysCodeDscVersionRel> versionRels = tSysCodeDscVersionRelRepository.findByRelId(tSysQualityCategory.getId());
//        //如果没有查询到历史版本则保存一份
//        if (versionRels.size()==0){
//            this.saveCodeVersionAndRel(tSysQualityCategory.getId(),"QCCF0000","1");
//        }


    }

    private void saveCodeVersionAndRel(Integer relId,String codeClId, String enabledSt) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crtTime");
        Pageable pageable = PageRequest.of(0, 999, sort);
        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
        tSysCodeDsc.setCodeClId(codeClId);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact());
        if (StringUtils.isNotBlank(enabledSt)) {
            tSysCodeDsc.setEnabledSt(enabledSt);
            matcher.withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        }
        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
        Page<TSysCodeDsc> tSysCodeDscPage = tSysCodeDscRepository.findAll(example, pageable);

        List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscPage.getContent();


        //生成UUID，一份保存的字典版本号是一致的
        UUID uuid = UUID.randomUUID();
        String compactUuid = uuid.toString().replace("-", "");

        //todo 做重复校验，如果重复引用重复的版本号


        //保存整个合集同时要生成UUID的版本编号，
        List<TSysCodeDscVersion> codeDscVersionList = new ArrayList<>();
        for (TSysCodeDsc codeDsc:
                tSysCodeDscList) {
            TSysCodeDscVersion codeDscVersion = new TSysCodeDscVersion();
            BeanUtils.copyProperties(codeDsc,codeDscVersion);
            codeDscVersion.setCodeId(null);
            codeDscVersion.setVersionNo(compactUuid);
            codeDscVersion.setVersionCrtTime(new Date());

            codeDscVersionList.add(codeDscVersion);
        }
        tSysCodeDscVersionRepository.saveAll(codeDscVersionList);
        //同时建立版本号关系表
        TSysCodeDscVersionRel rel = new TSysCodeDscVersionRel();
        rel.setRelId(relId);
        rel.setVersionNo(compactUuid);
        rel.setCrtTime(new Date());
        tSysCodeDscVersionRelRepository.saveAndFlush(rel);

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
    @Transactional
    public void saveTSysQualityCategoryConfig(Integer categoryId, List<TSysQualityCategoryConfig> tSysQualityCategoryConfigList) {
        tSysQualityCategoryConfigRepository.deleteByCategoryId(categoryId);
        tSysQualityCategoryConfigList.forEach(tSysQualityCategoryConfig -> tSysQualityCategoryConfig.setCategoryId(categoryId));
        tSysQualityCategoryConfigRepository.saveAll(tSysQualityCategoryConfigList);

    }

    @Override
    @Transactional
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

//        List<TSysCodeDscVersionRel> versionRels = tSysCodeDscVersionRelRepository.findByRelId(tSysQualityCategory.getId());
//        if (versionRels.size()>=1){
//            vo.setCodeVersionNo(versionRels.get(0).getVersionNo());
//        }

        if (null == tSysQualityCategory){
            return null;
        }else {
            return vo;
        }


    }


}
