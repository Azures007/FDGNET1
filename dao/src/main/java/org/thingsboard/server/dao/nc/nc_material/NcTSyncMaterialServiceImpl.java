package org.thingsboard.server.dao.nc.nc_material;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.nc.nc_material.NcTSyncMaterial;
import org.thingsboard.server.dao.sql.nc.nc_material.NcTSyncMaterialRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NcTSyncMaterialServiceImpl implements NcTSyncMaterialService {

    @Autowired
    private NcTSyncMaterialRepository ncTSyncMaterialRepository;

    @Override
    public NcTSyncMaterial save(NcTSyncMaterial ncTSyncMaterial) {
        return ncTSyncMaterialRepository.save(ncTSyncMaterial);
    }

    @Override
    public void syncMaterial(List<NcTSyncMaterial> ncTSyncMaterials) {
        // 提取所有 ncMaterialId
        List<String> materialIds = ncTSyncMaterials.stream()
                .map(NcTSyncMaterial::getNcMaterialId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        // 一次性查询已存在的物料信息
        Map<String, NcTSyncMaterial> existingMap = new HashMap<>();
        if (!materialIds.isEmpty()) {
            List<NcTSyncMaterial> existingMaterials = ncTSyncMaterialRepository.findByNcMaterialIdIn(materialIds);
            existingMap = existingMaterials.stream()
                    .collect(Collectors.toMap(
                            NcTSyncMaterial::getNcMaterialId,
                            material -> material
                    ));
        }

        // 分类处理：需要更新的和需要新增的
        List<NcTSyncMaterial> toUpdate = new ArrayList<>();
        List<NcTSyncMaterial> toCreate = new ArrayList<>();

        for (NcTSyncMaterial material : ncTSyncMaterials) {
            String ncMaterialId = material.getNcMaterialId();
            if (ncMaterialId == null) {
                continue; // 跳过无ID的数据，或根据业务决定如何处理
            }

            NcTSyncMaterial existing = existingMap.get(ncMaterialId);
            if (existing != null) {
                // 更新已有记录
                updateExistingMaterial(existing, material);
                toUpdate.add(existing);
            } else {
                // 新增记录
                toCreate.add(material);
            }
        }

        // 批量保存
        if (!toUpdate.isEmpty()) {
            ncTSyncMaterialRepository.saveAll(toUpdate);
        }
        if (!toCreate.isEmpty()) {
            ncTSyncMaterialRepository.saveAll(toCreate);
        }

    }

    // 可选：封装更新逻辑
    private void updateExistingMaterial(NcTSyncMaterial target, NcTSyncMaterial source) {
        target.setMaterialName(source.getMaterialName());
        target.setMaterialCode(source.getMaterialCode());
        target.setMaterialUnit(source.getMaterialUnit());
        target.setMaterialModel(source.getMaterialModel());
        target.setMaterialStatus(source.getMaterialStatus());
        target.setNcMaterialCategory(source.getNcMaterialCategory());
        target.setNcMaterialMainCategory(source.getNcMaterialMainCategory());
        target.setNcMaterialClassification(source.getNcMaterialClassification());
        target.setNcMaterialQualityNum(source.getNcMaterialQualityNum());
        target.setNcMaterialQualityUnit(source.getNcMaterialQualityUnit());
        target.setNcMaterialStatus(source.getNcMaterialStatus());
    }
}
