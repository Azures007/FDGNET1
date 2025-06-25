package org.thingsboard.server.dao.sync;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.TSyncMaterial;
import org.thingsboard.server.common.data.TSyncMaterialBom;
import org.thingsboard.server.common.data.TSysCodeDsc;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.ListMaterialDto;
import org.thingsboard.server.dao.dto.TSyncMaterialSaveDto;
import org.thingsboard.server.dao.sql.licheng.MidMaterialRepository;
import org.thingsboard.server.dao.sql.sync.SyncMaterialBomRepository;
import org.thingsboard.server.dao.sql.sync.SyncMaterialRepository;
import org.thingsboard.server.dao.sql.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.dao.vo.ListMaterialFiterVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.TSyncMaterialVo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("materialService")
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    SyncMaterialRepository materialRepository;

    @Autowired
    SyncMaterialBomRepository tSyncMaterialBomRepository;

    @Value("${material.sync.header_key}")
    private String materialSyncHeaderKey;

    @Value("${material.sync.size}")
    private Integer materialSyncHeaderSize;

    @Autowired
    MidMaterialRepository midMaterialRepository;

    @Autowired
    TSysCodeDscRepository tSysCodeDscRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PageVo<TSyncMaterialVo> listMaterial(Integer current, Integer size, ListMaterialDto listMaterialDto) {
        TSyncMaterial tSyncMaterialTo = new TSyncMaterial();
        tSyncMaterialTo.setMaterialCode(StringUtils.isBlank(listMaterialDto.getMaterialCode()) ? "" : listMaterialDto.getMaterialCode());
        tSyncMaterialTo.setMaterialName(StringUtils.isBlank(listMaterialDto.getMaterialCode()) ? "" : listMaterialDto.getMaterialCode());
        tSyncMaterialTo.setMaterialStatus(StringUtils.isBlank(listMaterialDto.getMaterialStatus()) ? "" : listMaterialDto.getMaterialStatus());
        int i = current * size;
        List<Map> all = materialRepository.findAllJoinMid(tSyncMaterialTo, i, size);
        List<TSyncMaterialVo> castEntity = JSON.parseArray(JSON.toJSONString(all), TSyncMaterialVo.class);
        PageVo<TSyncMaterialVo> pageVo = new PageVo<>();
        pageVo.setList(castEntity);
        pageVo.setCurrent(current);
        pageVo.setSize(size);
        pageVo.setTotal(materialRepository.finAllAndTotal(tSyncMaterialTo));
        return pageVo;
    }

    @Override
    public void update(TSyncMaterial tSyncMaterial) {
        updateVerify(tSyncMaterial);
        materialRepository.saveAndFlush(tSyncMaterial);
    }

    @Override
    @Transactional
    public void update(TSyncMaterialSaveDto materialDto) {
        TSyncMaterial tSyncMaterial = null;
        Integer materialId = materialDto.getId();
        // 获取原有物料信息
        if (materialId == null || materialId == 0) {
            tSyncMaterial = new TSyncMaterial();
        } else {
            Optional<TSyncMaterial> existingOpt = materialRepository.findById(materialId);
            if (!existingOpt.isPresent()) {
                throw new RuntimeException("物料未找到");
            } else {
                tSyncMaterial = existingOpt.get();
            }
        }

        // 更新主表字段
        BeanUtils.copyProperties(materialDto, tSyncMaterial, "id", "tSyncMaterialBoms"); // 排除 id 和 bom 集合

        // 校验必填字段、赋值创建人创建时间
        updateVerify(tSyncMaterial);

        // 保存物料主表
        materialRepository.saveAndFlush(tSyncMaterial);

        materialId = tSyncMaterial.getId();

        if (materialDto.getMaterialBoms() == null || materialDto.getMaterialBoms().isEmpty()) {
            //删除所有bom
            tSyncMaterialBomRepository.deleteByParentId(materialId);
            return;
        } else {
            // 处理 BOM 集合
            List<TSyncMaterialBom> newBoms = materialDto.getMaterialBoms();
            List<TSyncMaterialBom> existingBoms =  tSyncMaterialBomRepository.findAllByParentId(materialId);

            // 区分新增/更新/删除
            Map<Integer, TSyncMaterialBom> existingMap = existingBoms.stream()
                    .collect(Collectors.toMap(TSyncMaterialBom::getId, b -> b));

            for (TSyncMaterialBom newBom : newBoms) {
                if (newBom.getId() == null || !existingMap.containsKey(newBom.getId())) {
                    // 新增
                    newBom.setParentId(materialId); // 关联主表ID
                    tSyncMaterialBomRepository.save(newBom);
                } else {
                    // 更新
                    TSyncMaterialBom existingBom = existingMap.get(newBom.getId());
                    BeanUtils.copyProperties(newBom, existingBom);
                    tSyncMaterialBomRepository.save(existingBom);
                    existingMap.remove(newBom.getId()); // 移除已处理项
                }
            }

            // 删除剩余的未处理项（即不在新列表中的旧项）
            existingMap.values().forEach(bom -> tSyncMaterialBomRepository.deleteById(bom.getId()));
        }
    }

    @Override
    public TSyncMaterial getById(Integer id) {
        return materialRepository.findById(id).get();
    }

    @Override
    public TSyncMaterialVo getById2(Integer id) {
        TSyncMaterialVo tSyncMaterialVo = new TSyncMaterialVo();

        TSyncMaterial tSyncMaterial = materialRepository.findById(id).get();
        BeanUtils.copyProperties(tSyncMaterial, tSyncMaterialVo);
        tSyncMaterialVo.setMaterialBoms(tSyncMaterialBomRepository.findAllByParentId(id));

        return tSyncMaterialVo;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        //删除所有bom
        tSyncMaterialBomRepository.deleteByParentId(id);
        //删除物料
        materialRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void sync(String name) {
        long start = System.currentTimeMillis();
        List<TSysCodeDsc> codes = tSysCodeDscRepository.findByCodeClId(materialSyncHeaderKey);
        log.info("从中间物料表同步至订单物料表：" + codes);
        int i = 0;
        int cout = 0;
        do {
            //1.分页获取数据
            List<Map> mapList = listData(i, codes);
            if (null == mapList || 0 == mapList.size()) {
                break;
            }

            //2.找到所有库中存在的materialCode并删除
//            findCodeAndDelete(mapList);
            findKdMaterialIdndDelete(mapList);

            //3.批量插入
            insertsData(mapList, name);
            log.info("-------------本次同步数量：" + mapList.size() + "----------------");
            cout += mapList.size();
            i++;
        } while (true);
        long end = System.currentTimeMillis();
        log.info("----------------本轮同步数量：" + cout + "-----------------------------------------");
        log.info("---------------本轮耗时：" + (end - start) + " ms-----------------------");
        log.info("-------------物料表t_sync_material同步结束-------------------");
    }

    @Override
    public ListMaterialFiterVo listMaterialFiter(String materialCode, Integer craftId, Integer kdOrgId, Integer kdDeptId, Integer current, Integer size) {
        materialCode = StringUtils.isBlank(materialCode) ? "" : materialCode;
        PageRequest pageRequest = PageRequest.of(current, size);
        ListMaterialFiterVo listMaterialFiterVo = new ListMaterialFiterVo();
        listMaterialFiterVo.setOffMaterial(materialRepository.listMaterialOffFiter(craftId));
        Page<TSyncMaterial> page = materialRepository.listMaterialFiter(materialCode,pageRequest);
        listMaterialFiterVo.setNoMaterial(new PageVo<TSyncMaterial>(page));
        return listMaterialFiterVo;
    }

    //分页获取数据
    private List<Map> listData(int i, List<TSysCodeDsc> codes) {
        StringBuilder sqlBuilder;
        List<Map> mapList;
        String sql;
        Query query;
        int current = i * materialSyncHeaderSize;
        sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from mid_material where");
        for (TSysCodeDsc code : codes) {
            sqlBuilder.append(" kd_material_number like '");
            sqlBuilder.append(code.getCodeValue());
            sqlBuilder.append("%' or");
        }
        sqlBuilder.delete(sqlBuilder.length() - 3, sqlBuilder.length());
        sqlBuilder.append(" limit ");
        sqlBuilder.append(materialSyncHeaderSize);
        sqlBuilder.append(" offset ");
        sqlBuilder.append(current);
        System.out.println(sqlBuilder);
        sql = sqlBuilder.toString();
        query = entityManager.createNativeQuery(sql);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        log.info("sql:" + sql);
        mapList = query.getResultList();
        return mapList;
    }

    //找到所有库中存在的materialCode并删除(废弃)
    private void findCodeAndDelete(List<Map> mapList) {
        HashSet<String> codes = new HashSet<>();
        mapList.stream().forEach(map -> {
            codes.add(String.valueOf(map.get("kd_material_number")));
        });
        materialRepository.deleteInCode(codes);
    }

    //找到所有库中存在的kdMaterialId并删除
    private void findKdMaterialIdndDelete(List<Map> mapList) {
        HashSet<Integer> kdMaterialIds = new HashSet<>();
        mapList.stream().forEach(map -> {
            kdMaterialIds.add(Integer.parseInt(map.get("kd_material_id") + ""));
        });
        materialRepository.deleteInKdId(kdMaterialIds);
    }

    // 批量插入
    private void insertsData(List<Map> mapList, String name) {
        StringBuilder sqlBuilder = new StringBuilder();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = formatter.format(date);
        sqlBuilder.append("insert into t_sync_material(kd_material_id,material_code,material_name,material_unit,group_code,");
        sqlBuilder.append("material_model,material_status,created_time,created_name,updated_time,updated_name" +
                ",kd_material_stretch_weight,kd_material_each_piece_num" +
                ",kd_material_workshop_id,kd_material_workshop_name,kd_material_workshop_number,kd_material_use_org_id" +
                ",kd_material_use_org_number,kd_material_use_org_name" +
                ")values");
        mapList.stream().forEach(map -> {
            sqlBuilder.append("(");
            sqlBuilder.append(String.valueOf(map.get("kd_material_id")));
            sqlBuilder.append(",'");
            sqlBuilder.append(String.valueOf(map.get("kd_material_number")));
            sqlBuilder.append("',");
            sqlBuilder.append("'");
            sqlBuilder.append(String.valueOf(map.get("kd_material_name")));
            sqlBuilder.append("',");
            sqlBuilder.append("'");
            sqlBuilder.append(String.valueOf(map.get("kd_material_unit_number")));
            sqlBuilder.append("',");
            sqlBuilder.append("'");
            sqlBuilder.append(String.valueOf(map.get("kd_material_number")).substring(0, 4));
            sqlBuilder.append("',");
            sqlBuilder.append("'");
            sqlBuilder.append(String.valueOf(map.get("kd_material_spec")));
            sqlBuilder.append("',");
            sqlBuilder.append("'0',");
            sqlBuilder.append("'");
            sqlBuilder.append(dateFormat);
            sqlBuilder.append("',");
            sqlBuilder.append("'");
            sqlBuilder.append(name);
            sqlBuilder.append("',");
            sqlBuilder.append("'");
            sqlBuilder.append(dateFormat);
            sqlBuilder.append("',");
            sqlBuilder.append("'");
            sqlBuilder.append(name);
            sqlBuilder.append("',");
            //单支克重、每件支数
            sqlBuilder.append(String.valueOf(map.get("kd_material_stretch_weight")));
            sqlBuilder.append(",");
            sqlBuilder.append(String.valueOf(map.get("kd_material_each_piece_num")));
            sqlBuilder.append(",");
            sqlBuilder.append(String.valueOf(map.get("kd_material_workshop_id")));
            sqlBuilder.append(",");
            sqlBuilder.append("'");
            sqlBuilder.append(String.valueOf(map.get("kd_material_workshop_name")));
            sqlBuilder.append("',");
            sqlBuilder.append("'");
            sqlBuilder.append(String.valueOf(map.get("kd_material_workshop_number")));
            sqlBuilder.append("',");
            sqlBuilder.append(String.valueOf(map.get("kd_material_use_org_id")));
            sqlBuilder.append(",");
            sqlBuilder.append("'");
            sqlBuilder.append(String.valueOf(map.get("kd_material_use_org_number")));
            sqlBuilder.append("',");
            sqlBuilder.append("'");
            sqlBuilder.append(String.valueOf(map.get("kd_material_use_org_name")));
            sqlBuilder.append("'");
            sqlBuilder.append("),");
        });
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        String sqlString = sqlBuilder.toString();
        log.info("sql:" + sqlString);
        int size = entityManager.createNativeQuery(sqlString).executeUpdate();
        log.info("------------本次批量插入数量：" + size);
    }


    //新增/修改---数据有效性验证
    private void updateVerify(TSyncMaterial tSyncMaterial) {
        if (tSyncMaterial.getId() == null) {
            //新增
            if (StringUtils.isBlank(tSyncMaterial.getMaterialCode())) {
                throw new RuntimeException("物料编码不能为空");
            }
            if (StringUtils.isBlank(tSyncMaterial.getMaterialName())) {
                throw new RuntimeException("物料名称不能为空");
            }
            tSyncMaterial.setCreatedName(tSyncMaterial.getUpdatedName());
            tSyncMaterial.setCreatedTime(tSyncMaterial.getUpdatedTime());
            if (StringUtils.isBlank(tSyncMaterial.getMaterialStatus())) {
                tSyncMaterial.setMaterialStatus(GlobalConstant.enableTrue);
            }
        }
    }
}
