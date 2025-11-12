package org.thingsboard.server.dao.mes.recipe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.*;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.RecipeQueryDto;
import org.thingsboard.server.dao.sql.mes.recipe.TSysRecipeInputRepository;
import org.thingsboard.server.dao.sql.mes.recipe.TSysRecipeProductBindingRepository;
import org.thingsboard.server.dao.sql.mes.recipe.TSysRecipeRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialRepository;
import org.thingsboard.server.dao.mes.dto.RecipeSaveDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.mes.TSysProcessInfo.TSysProcessInfoService;
import org.thingsboard.server.dao.user.UserService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Stream;

/**
 * 配方服务实现类
 * @author: system
 * @date: 2025-01-10
 * @description: 配方管理服务实现
 */
@Slf4j
@Service
@Transactional
public class TSysRecipeServiceImpl implements TSysRecipeService {

    @Autowired
    private TSysRecipeRepository recipeRepository;

    @Autowired
    private TSysRecipeInputRepository recipeInputRepository;

    @Autowired
    private TSysRecipeProductBindingRepository productBindingRepository;

    @Autowired
    private SyncMaterialRepository syncMaterialRepository;

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TSysProcessInfoService processInfoService;

    @Autowired
    private TSysProcessInfoRepository processInfoRepository;

    @Override
    public Page<TSysRecipe> getRecipeList(String currentUser, Integer current, Integer size, RecipeQueryDto queryDto) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime").and(Sort.by(Sort.Direction.ASC, "recipeCode"));
        Pageable pageable = PageRequest.of(current, size, sort);

        String recipeName = queryDto.getRecipeName();
        String recipeCode = queryDto.getRecipeCode();
        String status = queryDto.getStatus();
        // 只保留当前用户登录的基地
        String pkOrg = userService.getUserCurrentPkOrg(currentUser);

        return recipeRepository.findRecipesWithConditions(recipeName, recipeCode, status, pkOrg, pageable);
    }

    @Override
    public TSysRecipe getRecipeById(Integer recipeId) {
        Optional<TSysRecipe> recipeOpt = recipeRepository.findById(recipeId);
        if (recipeOpt.isPresent()) {
            TSysRecipe recipe = recipeOpt.get();
            // 加载关联的投入设置和产品绑定
            recipe.setRecipeInputs(recipeInputRepository.findByRecipeId(recipeId));
            recipe.setProductBindings(productBindingRepository.findByRecipeId(recipeId));
            return recipe;
        }
        return null;
    }

    @Override
    public RecipeSaveDto getRecipeSaveDto(Integer recipeId) {
        Optional<TSysRecipe> recipeOpt = recipeRepository.findById(recipeId);
        if (recipeOpt.isPresent()) {
            TSysRecipe recipe = recipeOpt.get();
            RecipeSaveDto dto = new RecipeSaveDto();
            dto.setRecipe(recipe);
            List<TSysRecipeInput> recipeInputs = recipeInputRepository.findByRecipeId(recipeId);
            // dto.setRecipeInputs(recipeInputs);

            if (recipeInputs != null && !recipeInputs.isEmpty()) {
                Map<String, Map<String, Map<String, Map<String, List<TSysRecipeInput>>>>> groupedInputs = recipeInputs.stream()
                    .collect(Collectors.groupingBy(
                        input -> input.getSemiFinishedProductCode() != null ? input.getSemiFinishedProductCode() : "",
                        Collectors.groupingBy(
                            input -> input.getSemiFinishedProductName() != null ? input.getSemiFinishedProductName() : "",
                            Collectors.groupingBy(
                                input -> input.getProcessNumber() != null ? input.getProcessNumber() : "",
                                Collectors.groupingBy(
                                    input -> input.getProcessName() != null ? input.getProcessName() : ""
                                )
                            )
                        )
                    ));
                List<RecipeSaveDto.RecipeInputGroup> groupedList = new ArrayList<>();
                for (Map.Entry<String, Map<String, Map<String, Map<String, List<TSysRecipeInput>>>>> entry1 : groupedInputs.entrySet()) {
                    String semiFinishedProductCode = entry1.getKey();
                    for (Map.Entry<String, Map<String, Map<String, List<TSysRecipeInput>>>> entry2 : entry1.getValue().entrySet()) {
                        String semiFinishedProductName = entry2.getKey();
                        for (Map.Entry<String, Map<String, List<TSysRecipeInput>>> entry3 : entry2.getValue().entrySet()) {
                            String processNumber = entry3.getKey();
                            for (Map.Entry<String, List<TSysRecipeInput>> entry4 : entry3.getValue().entrySet()) {
                                String processName = entry4.getKey();
                                RecipeSaveDto.RecipeInputGroup group = new RecipeSaveDto.RecipeInputGroup();
                                group.setSemiFinishedProductCode(semiFinishedProductCode.isEmpty() ? null : semiFinishedProductCode);
                                group.setSemiFinishedProductName(semiFinishedProductName.isEmpty() ? null : semiFinishedProductName);
                                group.setProcessNumber(processNumber.isEmpty() ? null : processNumber);
                                group.setProcessName(processName.isEmpty() ? null : processName);
                                group.setInputs(entry4.getValue());
                                groupedList.add(group);
                            }
                        }
                    }
                }
                dto.setGroupedInputs(groupedList);
            }
            
            return dto;
        }
        return null;
    }

    @Override
    public RecipeSaveDto saveRecipe(RecipeSaveDto saveDto, String currentUser) {
        TSysPersonnelInfo person = tSysPersonnelInfoRepository.findAllByUserId(currentUser);
        if(person==null) {
            throw new RuntimeException("用户信息异常");
        }
        TSysRecipe recipe=saveDto.getRecipe();
        List<TSysRecipeInput> recipeInputs = new ArrayList<>();

        if (saveDto.getGroupedInputs() != null && !saveDto.getGroupedInputs().isEmpty()) {
            // 用于检查每组内半成品+工序的组合是否重复
            Set<String> groupProcessCombinations = new HashSet<>();
            // 用于检查同一分组下物料是否重复
            Set<String> materialCodesInGroup = new HashSet<>();
            for (RecipeSaveDto.RecipeInputGroup group : saveDto.getGroupedInputs()) {
                if (group.getProcessNumber() == null || group.getProcessNumber().trim().isEmpty()) {
                    throw new RuntimeException("工序不能为空");
                }
                // 检查同一组内半成品+工序不能重复
                String semiFinishedProductCode = group.getSemiFinishedProductCode() != null ? group.getSemiFinishedProductCode() : "";
                String processNumber = group.getProcessNumber() != null ? group.getProcessNumber() : "";
                String combination = semiFinishedProductCode + "|" + processNumber;
                if (groupProcessCombinations.contains(combination)) {
                    throw new RuntimeException("同一配方下，半成品和工序的组合不能重复");
                }
                groupProcessCombinations.add(combination);
                // 检查同一分组下物料是否重复
                materialCodesInGroup.clear();
                if (group.getInputs() != null && !group.getInputs().isEmpty()) {
                    long count = group.getInputs().stream()
                            .filter(input -> "1".equals(input.getPotCalculationBasis()))
                            .count();
                    if (count != 1) {
                        throw new RuntimeException("每个工序组必须且只能有一个锅数计算基准项");
                    }
                    long zeroCount = group.getInputs().stream()
                            .filter(input -> "0".equals(input.getPotCalculationBasis()))
                            .count();
                    if ((count + zeroCount) != group.getInputs().size()) {
                        throw new RuntimeException("锅数计算基准必须为0或1");
                    }
                    // 检查物料是否重复及计划投入比例是否超过100
                    for (TSysRecipeInput input : group.getInputs()) {
                        // 检查物料编码是否重复
                        if (input.getMaterialCode() != null) {
                            if (materialCodesInGroup.contains(input.getMaterialCode())) {
                                throw new RuntimeException("同一分组下物料不能重复");
                            }
                            materialCodesInGroup.add(input.getMaterialCode());
                        }
                        // 检查计划投入比例是否超过100
                        if (input.getPlanInputRatio() != null && input.getPlanInputRatio().compareTo(new BigDecimal(100)) > 0) {
                            throw new RuntimeException("计划投入比例不能超过100%");
                        }
                    }
                }
                
                for (TSysRecipeInput input : group.getInputs()) {
                    input.setSemiFinishedProductName(group.getSemiFinishedProductName());
                    input.setSemiFinishedProductCode(group.getSemiFinishedProductCode());
                    input.setProcessNumber(group.getProcessNumber());
                    input.setProcessName(group.getProcessName());
                    recipeInputs.add(input);
                }
            }
        }
        
        boolean isNew = recipe.getRecipeId() == null;

        if (isNew) {
            // 新增配方
            recipe.setCreator(person.getName());
            recipe.setCreateTime(new Date());
        } else {
            TSysRecipe oldrecipe=recipeRepository.getOne(recipe.getRecipeId());
            recipe.setCreator(oldrecipe.getCreator());
            recipe.setCreateTime(oldrecipe.getCreateTime());
            // 更新配方
            recipe.setUpdateUser(person.getName());
            recipe.setUpdateTime(new Date());
        }
        // 保存配方主表
        TSysRecipe savedRecipe = recipeRepository.save(recipe);
        // 保存投入设置
        if (recipeInputs != null && !recipeInputs.isEmpty()) {
            // 先删除原有的投入设置
            recipeInputRepository.deleteByRecipeId(savedRecipe.getRecipeId());

            // 保存新的投入设置
            for (TSysRecipeInput input : recipeInputs) {
                input.setRecipeId(savedRecipe.getRecipeId());
                input.setCreateTime(new Date());
                input.setUpdateTime(new Date());
            }
            recipeInputs=recipeInputRepository.saveAll(recipeInputs);
        }
        saveDto.setRecipe(savedRecipe);
        // saveDto.setRecipeInputs(recipeInputs);
        return saveDto;
    }

    @Override
    public void deleteRecipe(Integer recipeId) {
        // 删除投入设置
        recipeInputRepository.deleteByRecipeId(recipeId);

        // 删除产品绑定
        productBindingRepository.deleteByRecipeId(recipeId);

        // 删除配方主表
        recipeRepository.deleteById(recipeId);
    }

    @Override
    public void updateRecipeStatus(Integer recipeId, String status, String currentUser) {
        TSysPersonnelInfo person = tSysPersonnelInfoRepository.findAllByUserId(currentUser);
        if(person==null) {
            throw new RuntimeException("用户信息异常");
        }
        Optional<TSysRecipe> recipeOpt = recipeRepository.findById(recipeId);
        if (recipeOpt.isPresent()) {
            TSysRecipe recipe = recipeOpt.get();
            recipe.setStatus(status);
            recipe.setUpdateUser(person.getName());
            recipe.setUpdateTime(new Date());
            recipeRepository.save(recipe);
        }
    }

    @Override
    public boolean isRecipeCodeExists(String recipeCode, Integer recipeId) {
        if (recipeId == null) {
            // 新增时检查
            return recipeRepository.findByRecipeCode(recipeCode).isPresent();
        } else {
            // 更新时检查（排除当前ID）
            return recipeRepository.existsByRecipeCodeAndRecipeIdNot(recipeCode, recipeId);
        }
    }

    @Override
    public List<TSysRecipeInput> getRecipeInputsByRecipeId(Integer recipeId) {
        return recipeInputRepository.findByRecipeId(recipeId);
    }

    @Override
    public void saveRecipeInputs(Integer recipeId, List<TSysRecipeInput> recipeInputs) {
        // 先删除原有的投入设置
        recipeInputRepository.deleteByRecipeId(recipeId);

        // 保存新的投入设置
        if (recipeInputs != null && !recipeInputs.isEmpty()) {
            for (TSysRecipeInput input : recipeInputs) {
                input.setRecipeId(recipeId);
                input.setCreateTime(new Date());
                input.setUpdateTime(new Date());
            }
            recipeInputRepository.saveAll(recipeInputs);
        }
    }

    @Override
    public List<TSysRecipeProductBinding> getProductBindingsByRecipeId(Integer recipeId) {
        return productBindingRepository.findByRecipeId(recipeId);
    }

    @Override
    public void saveProductBindings(Integer recipeId, List<TSysRecipeProductBinding> productBindings) {
        // 先删除原有的产品绑定
        productBindingRepository.deleteByRecipeId(recipeId);

        // 保存新的产品绑定
        if (productBindings != null && !productBindings.isEmpty()) {
            for (TSysRecipeProductBinding binding : productBindings) {
                binding.setRecipeId(recipeId);
                binding.setCreateTime(new Date());
            }
            productBindingRepository.saveAll(productBindings);
        }
    }

    @Override
    public PageVo<TSyncMaterial> getAvailableProducts(String currentUser, Integer recipeId, String productName, String productCode, Integer current, Integer size) {
        Pageable pageable = PageRequest.of(current, size);

        // 构建查询条件
        String searchTerm = "";
        if (productName != null && !productName.trim().isEmpty()) {
            searchTerm = productName.trim();
        } else if (productCode != null && !productCode.trim().isEmpty()) {
            searchTerm = productCode.trim();
        }

        // 如果recipeId为null，查询所有物料；否则过滤掉已绑定的产品
        Page<TSyncMaterial> page;
        if (recipeId == null) {
            // 查询所有启用的物料
            page = syncMaterialRepository.findAvailableMaterials(searchTerm, pageable);
        } else {
            // 查询可用物料（排除已绑定的产品）
            page = syncMaterialRepository.findAvailableMaterialsExcludingRecipe(recipeId, searchTerm, pageable);
        }

        // 转换为 PageVo
        PageVo<TSyncMaterial> pageVo = new PageVo<>();
        pageVo.setList(page.getContent());
        pageVo.setCurrent(current);
        pageVo.setSize(size);
        pageVo.setTotal((int)page.getTotalElements());
        return pageVo;
    }

    @Override
    public void removeProductBinding(Integer bindingId) {
        productBindingRepository.deleteById(bindingId);
    }

    @Override
    public long getRecipeCountByPkOrg(String pkOrg) {
        return recipeRepository.countByPkOrg(pkOrg);
    }

    @Override
    public long getRecipeCountByStatus(String status) {
        return recipeRepository.countByStatus(status);
    }

    
    @Override
    @Transactional
    public TSysRecipe copyRecipe(Integer recipeId, String creator) {
        TSysRecipe originalRecipe = getRecipeById(recipeId);
        if (originalRecipe == null) {
            throw new RuntimeException("配方不存在");
        }
        String originalCode = originalRecipe.getRecipeCode();
        String prefix = originalCode + "_";
        List<TSysRecipe> existingRecipes = recipeRepository.findByRecipeCodeStartingWith(prefix);
        // 提取已存在的流水号
        Set<Integer> existingNumbers = new HashSet<>();
        for (TSysRecipe recipe : existingRecipes) {
            String code = recipe.getRecipeCode();
            if (code.startsWith(prefix)) {
                try {
                    String numberStr = code.substring(prefix.length());
                    if (numberStr.matches("\\d{3}")) {
                        existingNumbers.add(Integer.parseInt(numberStr));
                    }
                } catch (Exception e) {
                }
            }
        }
        int sequenceNum = 1;
        while (existingNumbers.contains(sequenceNum)) {
            sequenceNum++;
        }
        String sequence = String.format("%03d", sequenceNum);
        String newRecipeCode = originalCode + "_" + sequence;
        // 复制配方主表信息
        TSysRecipe newRecipe = new TSysRecipe();
        newRecipe.setRecipeName(originalRecipe.getRecipeName());
        newRecipe.setRecipeCode(newRecipeCode);
        newRecipe.setOrgName(originalRecipe.getOrgName());
        newRecipe.setStatus(originalRecipe.getStatus());
        newRecipe.setRecipeDescription(originalRecipe.getRecipeDescription());
        newRecipe.setPkOrg(originalRecipe.getPkOrg());
        newRecipe.setCreator(creator);
        newRecipe.setCreateTime(new Date());
        newRecipe.setUpdateTime(new Date());
        TSysRecipe savedRecipe = recipeRepository.save(newRecipe);
        // 获取原始配方的投入设置
        List<TSysRecipeInput> originalInputs = originalRecipe.getRecipeInputs();
        if (originalInputs != null && !originalInputs.isEmpty()) {
            // 保存新的投入设置
            List<TSysRecipeInput> newInputs = new ArrayList<>();
            for (TSysRecipeInput input : originalInputs) {
                TSysRecipeInput newInput = new TSysRecipeInput();
                newInput.setRecipeId(savedRecipe.getRecipeId());
                newInput.setMaterialName(input.getMaterialName());
                newInput.setMaterialCode(input.getMaterialCode());
                newInput.setStandardInput(input.getStandardInput());
                newInput.setUnit(input.getUnit());
                newInput.setLowerLimitRatio(input.getLowerLimitRatio());
                newInput.setUpperLimitRatio(input.getUpperLimitRatio());
                newInput.setProcessName(input.getProcessName());
                newInput.setProcessNumber(input.getProcessNumber());
                newInput.setSemiFinishedProductName(input.getSemiFinishedProductName());
                newInput.setSemiFinishedProductCode(input.getSemiFinishedProductCode());
                newInput.setPlanInputRatio(input.getPlanInputRatio());
                newInput.setPotCalculationBasis(input.getPotCalculationBasis());
                newInput.setCreateTime(new Date());
                newInput.setUpdateTime(new Date());
                newInputs.add(newInput);
            }
            // 保存新的投入设置
            recipeInputRepository.saveAll(newInputs);
        }
        return savedRecipe;
    }
}