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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
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
            dto.setRecipeInputs(recipeInputRepository.findByRecipeId(recipeId));
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
        List<TSysRecipeInput> recipeInputs = saveDto.getRecipeInputs();
        // 校验半成品信息的完整性
        if (recipeInputs != null && !recipeInputs.isEmpty()) {
            // 按工序编号分组校验半成品信息的完整性
            Map<String, List<TSysRecipeInput>> inputsByProcessNumber = recipeInputs.stream()
                    .filter(input -> input.getProcessNumber() != null && !input.getProcessNumber().isEmpty())
                    .collect(Collectors.groupingBy(TSysRecipeInput::getProcessNumber));
            for (Map.Entry<String, List<TSysRecipeInput>> entry : inputsByProcessNumber.entrySet()) {
                List<TSysRecipeInput> inputsInSameProcess = entry.getValue();
                boolean hasSemiFinished = inputsInSameProcess.stream()
                        .anyMatch(input -> input.getSemiFinishedProductName() != null && !input.getSemiFinishedProductName().isEmpty() &&
                                input.getSemiFinishedProductCode() != null && !input.getSemiFinishedProductCode().isEmpty());
                if (hasSemiFinished) {
                    boolean allHasSemiFinished = inputsInSameProcess.stream()
                            .allMatch(input -> input.getSemiFinishedProductName() != null && !input.getSemiFinishedProductName().isEmpty() &&
                                    input.getSemiFinishedProductCode() != null && !input.getSemiFinishedProductCode().isEmpty());
                    if (!allHasSemiFinished) {
                        // 根据工序编号查询工序名称
                        List<TSysProcessInfo> processInfos = processInfoRepository.findByProcessNumber(entry.getKey());
                        String processName = entry.getKey();
                        if (processInfos != null && !processInfos.isEmpty()) {
                            processName = processInfos.get(0).getProcessName();
                        }
                        throw new RuntimeException("工序 " + processName + " 的半成品设置不完整，请完整设置半成品");
                    }
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
        saveDto.setRecipeInputs(recipeInputs);
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
}
