 package org.thingsboard.server.dao.mes.pd;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;
import org.thingsboard.server.common.data.mes.ncWorkline.NcWorkline;
import org.thingsboard.server.common.data.mes.sys.*;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.PdMaterialsVo;
import org.thingsboard.server.dao.mes.dto.PdMaterialsDto;
import org.thingsboard.server.dao.mes.ncWorkline.NcWorklineService;
import org.thingsboard.server.dao.sql.mes.ncInventory.NcInventoryRepository;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordRepository;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordSplitRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialBomRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialRepository;
import org.thingsboard.server.dao.sql.mes.user.TSysUserDetailRepository;
import org.thingsboard.server.dao.user.UserService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class YcPdServiceImpl implements YcPdService {

    @Autowired
    TSysPdRecordRepository tSysPdRecordRepository;

    @Autowired
    NcInventoryRepository ncInventoryRepository;

    @Autowired
    UserService userService;

    @Autowired
    TSysPdRecordSplitRepository tSysPdRecordSplitRepository;

    @Autowired
    SyncMaterialBomRepository syncMaterialBomRepository;

    @Autowired
    SyncMaterialRepository syncMaterialRepository;

    @Autowired
    NcWorklineService ncWorklineService;

    @Autowired
    TSysUserDetailRepository tSysUserDetailRepository;

    @Transactional
    @Override
    public TSysPdRecord savePd(TSysPdRecord tSysPdRecord, String userId) {
//        List<String> cwkids =userService.getUserCurrentCwkid(userId);
//        // 获取产线名称
//        List<String> cwkName = new ArrayList<>();
//        if (cwkids != null && !cwkids.isEmpty()) {
//            List<NcWorkline> workline = ncWorklineService.findAllByCwkids(cwkids);
//            //获取产线名称List
//            cwkName = workline.stream().map(NcWorkline::getVwkname).collect(Collectors.toList());
//        }
        String cwkName = null;
        String cwkid = userService.getOneUserCurrentCwkid(userId);
        if (StringUtils.isNotEmpty(cwkid)) {
            NcWorkline workline = ncWorklineService.findAllByCwkid(cwkid);
            if (workline != null) {
                cwkName = workline.getVwkname();
            }
        }
        if (cwkName == null) {
            throw new RuntimeException("未获取到用户当前的产线，请检查下");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        tSysPdRecord.setPdTime(new Date());
        tSysPdRecord.setNcVwkname(cwkName);
        Date pdTime = tSysPdRecord.getPdTime();
        String format = simpleDateFormat.format(pdTime);
        Integer pdSplit = null;
        tSysPdRecord.setCreatedName(tSysPdRecord.getPdCreatedName());
        // 修改查询逻辑，按产线区分记录
        TSysPdRecord tSysPdRecord1 = null;
        // 执行查询，查找相同产线、日期、物料等条件的记录
        if (tSysPdRecord.getNcVwkname() != null && tSysPdRecord.getMaterialNumber() != null &&
            tSysPdRecord.getPdClassNumber() != null && tSysPdRecord.getPdType() != null) {
            tSysPdRecord1 = tSysPdRecordRepository.findByGroupAndWorkshop(format,
                    tSysPdRecord.getMaterialNumber(), tSysPdRecord.getPdClassNumber(), tSysPdRecord.getPdType(),
                    tSysPdRecord.getNcVwkname());
        }
        // 无论是否查询到重复记录，都需要初始化pdSplit变量
        if (tSysPdRecord1 != null) {
            //统计盘点人
            String pdCreatedName = tSysPdRecord1.getPdCreatedName();
            Set<String> nameCollect = Stream.of(pdCreatedName.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            nameCollect.add(tSysPdRecord.getCreatedName());
            String nameJoin = StringUtils.join(nameCollect, ", ");
            tSysPdRecord.setPdCreatedName(nameJoin);
            //统计累加盘点数量
            BigDecimal pdQty = tSysPdRecord1.getPdQty();
            tSysPdRecord.setPdQty(pdQty.add(tSysPdRecord.getPdQty()));
            pdSplit = tSysPdRecord1.getPdRecordId();
            tSysPdRecord1.setByDeleted("1");
            tSysPdRecordRepository.saveAndFlush(tSysPdRecord1);
        } else {
            // 如果是首次盘点该物料，直接使用当前记录的ID作为pdSplit
            pdSplit = tSysPdRecord.getPdRecordId();
        }

        if (tSysPdRecord.getPdType().equals("0")) {
            //盘点
            tSysPdRecord.setByFp("0");
//            tSysPdRecordRepository.updatePd(format, tSysPdRecord.getPdWorkshopNumber(), tSysPdRecord.getPdClassNumber());
        } else {
            //复盘
            Integer pdRecordId = tSysPdRecord.getPdRecordId();
            if (pdSplit == null) {
                pdSplit = tSysPdRecord.getPdRecordId();
            }
            //复盘原纪录需要标记为删除
            TSysPdRecord deleteRecord = tSysPdRecordRepository.findById(tSysPdRecord.getPdRecordId()).orElse(null);
            deleteRecord.setByDeleted("1");
            tSysPdRecordRepository.saveAndFlush(deleteRecord);

            TSysPdRecord tSysPdRecordByPd = tSysPdRecordRepository.findById(pdRecordId).orElse(null);
            tSysPdRecordByPd.setByFp("1");
            tSysPdRecordRepository.saveAndFlush(tSysPdRecordByPd);
            tSysPdRecord.setByFp("1");
        }
        tSysPdRecord.setCreatedTime(new Date());
        tSysPdRecord.setByDeleted("0");
        tSysPdRecord.setPdTimeStr(format);
        tSysPdRecord.setPdRecordId(null);
        // 设置产线名称
        tSysPdRecord.setNcVwkname(cwkName);
        // 先保存主记录，以获得生成的ID
        TSysPdRecord savedRecord = tSysPdRecordRepository.saveAndFlush(tSysPdRecord);
        //更新库存
        List<NcInventory> ncInventories = null;
        if (tSysPdRecord.getPdWorkshopNcId() != null) {
            ncInventories = ncInventoryRepository.findByWarehouseIdAndMaterialCodeAndStatusOrderByLotAsc(
                    tSysPdRecord.getPdWorkshopNcId(),
                    tSysPdRecord.getMaterialNumber(),
                    "生效");
        }
        if (ncInventories != null && ncInventories.size() > 0) {
            for (NcInventory ncInventory : ncInventories) {
                ncInventory.setQty(tSysPdRecord.getPdQty().floatValue());
                ncInventoryRepository.saveAndFlush(ncInventory);
            }
        }
        // 拆分还原拆料 - 使用保存后获得的ID
        savePdBySplit(savedRecord, savedRecord.getPdRecordId(), savedRecord.getNcVwkname());

        return savedRecord;
    }

    /**
     * 盘点保存---拆分还原拆料
     *
     * @param tSysPdRecord
     * @param pdSplit
     */
    private void savePdBySplit(TSysPdRecord tSysPdRecord, Integer pdSplit, String cwkName) {
        if (pdSplit != null) {
            TSysPdRecordSplit deletePdRecordSplitt = new TSysPdRecordSplit();
            deletePdRecordSplitt.setRePdRecordId(pdSplit);
            tSysPdRecordSplitRepository.deleteByRePdRecordId(deletePdRecordSplitt.getRePdRecordId());
        }
        String materialNumber = tSysPdRecord.getMaterialNumber();
        List<TSyncMaterialBom> tSyncMaterialBoms = syncMaterialBomRepository.findByMaterialNumber(materialNumber);
        if (tSyncMaterialBoms != null && tSyncMaterialBoms.size() > 0) {
            for (TSyncMaterialBom tSyncMaterialBom : tSyncMaterialBoms) {
                // 确保只有当物料BOM存在且物料存在时才生成还原记录
                TSyncMaterial tSyncMaterial = syncMaterialRepository.findById(tSyncMaterialBom.getMaterialId()).orElse(null);
                if (tSyncMaterial != null) {
                    TSysPdRecordSplit tSysPdRecordSplit = JSON.parseObject(JSON.toJSONString(tSysPdRecord), TSysPdRecordSplit.class);
                    tSysPdRecordSplit.setRePdRecordId(tSysPdRecord.getPdRecordId());
                    // 确保计算结果不为null
                    BigDecimal qty = tSysPdRecord.getPdQty();
                    if (qty != null && tSyncMaterialBom.getRatio() != null) {
                        tSysPdRecordSplit.setPdQty(qty.multiply(tSyncMaterialBom.getRatio()).doubleValue());
                    } else {
                        tSysPdRecordSplit.setPdQty(0.0);
                    }
                    tSysPdRecordSplit.setMaterialName(tSyncMaterialBom.getMaterialName());
                    tSysPdRecordSplit.setMaterialNumber(tSyncMaterial.getMaterialCode());
                    tSysPdRecordSplit.setMaterialSpecifications(tSyncMaterial.getMaterialModel());
                    // 确保产线名称正确设置，与被还原物料的产线一致
                    tSysPdRecordSplit.setNcVwkname(cwkName);
                    tSysPdRecordSplitRepository.save(tSysPdRecordSplit);
                }
            }
        }
    }

    @Override
    public PdMaterialsVo pdMaterials(PdMaterialsDto pdMaterialsDto, String userId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(new Date());

        if (pdMaterialsDto.getPdTimeStr() == null || pdMaterialsDto.getPdTimeStr().isEmpty()) {
            pdMaterialsDto.setPdTimeStr(currentDate);
        }

        // 获取当前用户的产线名称
//        String cwkName = null;
//        List<String> cwkids = userService.getUserCurrentCwkid(userId);
//        if (cwkids != null && !cwkids.isEmpty()) {
//            NcWorkline workline = ncWorklineService.findAllByCwkids(cwkids).stream().findFirst().orElse(null);
//            if (workline != null) {
//                cwkName = workline.getVwkname();
//            }
//        }
        String cwkName = pdMaterialsDto.getVwkname();

        // 根据用户信息查询仓库ID
        String warehouseId = null;
        List<String> cwkids = userService.getUserCurrentCwkid(userId);
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        if (cwkids != null && !cwkids.isEmpty() && pkOrg != null) {
            List<TSysUserDetail> userDetails = tSysUserDetailRepository.findByUserId(userId);
            if (userDetails != null && !userDetails.isEmpty()) {
                // 查找匹配的用户详情记录
                for (TSysUserDetail detail : userDetails) {
                    if (pkOrg.equals(detail.getNcPkOrg()) && cwkids.contains(detail.getNcCwkid())) {
                        warehouseId = detail.getNcWarehouseId();
                        break;
                    }
                }

                // 如果没有找到完全匹配的记录，则使用仅匹配ncPkOrg的记录
                if (warehouseId == null) {
                    for (TSysUserDetail detail : userDetails) {
                        if (pkOrg.equals(detail.getNcPkOrg())) {
                            warehouseId = detail.getNcWarehouseId();
                            break;
                        }
                    }
                }
            }
        }

        // 设置仓库ID到查询条件中
        if (warehouseId != null) {
            pdMaterialsDto.setWarehouseCode(warehouseId);
        }

        List<Map> ncInventorieMs = ncInventoryRepository.pdMaterials(pdMaterialsDto);
        List<NcInventory> ncInventories = JSON.parseArray(JSON.toJSONString(ncInventorieMs), NcInventory.class);

        // 计算统计信息
        long totalCount = ncInventorieMs.size();
        long pagedCount = ncInventorieMs.stream()
                .mapToLong(m -> ((Number) m.get("by_pd")).longValue())
                .sum();
        long unpagedCount = totalCount - pagedCount;

        // 判断当前物料分类是否已完成盘点
        boolean isMaterialTypeFinished = false;
        if (pdMaterialsDto.getMaterialTypePd() != null && !pdMaterialsDto.getMaterialTypePd().isEmpty()) {
            TSysPdRecord finishedRecord = findByPdTimeStrAndNcVwknameAndMaterialTypeFinished(
                    pdMaterialsDto.getPdTimeStr(), cwkName, pdMaterialsDto.getMaterialTypePd());
            isMaterialTypeFinished = (finishedRecord != null);
        }

        PdMaterialsVo result = new PdMaterialsVo();
        result.setMaterials(ncInventories);
        result.setTotalMaterials(totalCount);
        result.setPagedCount(pagedCount);
        result.setUnpagedCount(unpagedCount);
        result.setMaterialTypeFinished(isMaterialTypeFinished); // 设置物料分类完成状态

        return result;
    }

    @Override
    public List<TSysPdRecord> fpWorkshopRecord(String startDate, String endDate,String userId) {
        //List<String> cwkids =userService.getUserCurrentCwkid(userId);
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrg(userId,pkOrg);
        List<String> wids=new ArrayList<>();
        if(ncWarehouses!=null&& !ncWarehouses.isEmpty()) {
            for (NcWarehouse ncWarehouse : ncWarehouses) {
                wids.add(ncWarehouse.getCode());
            }
        }
        List<Map> tSysPdRecords = tSysPdRecordRepository.fpWorkshopRecord(startDate, endDate,wids);
        List<TSysPdRecord> tSysPdRecords1 = JSON.parseArray(JSON.toJSONString(tSysPdRecords), TSysPdRecord.class);
        return tSysPdRecords1;
    }

    @Override
    public List<TSysPdRecord> showWorkshopRecord(String pdTimeStr, String pdWorkshopNumber, String ncVwkname) {
        List<TSysPdRecord> tSysPdRecords = tSysPdRecordRepository.showWorkshopRecord(pdTimeStr, pdWorkshopNumber, ncVwkname);
        for (TSysPdRecord tSysPdRecord : tSysPdRecords) {
            // 过滤掉特殊标记记录
            if (tSysPdRecord.getMaterialNumber() != null && tSysPdRecord.getMaterialNumber().startsWith("FINISHED_MATERIAL_TYPE_MARKER_")) {
                continue;
            }
            tSysPdRecord.setPdQty(tSysPdRecord.getPdQty().setScale(3, RoundingMode.HALF_UP));
        }
        return tSysPdRecords;
    }

    @Override
    public List<TSyncMaterial> listMaterial(String selectBy) {
        List<TSyncMaterial> tSyncMaterials = syncMaterialRepository.listMaterialsBySelctct(selectBy);
        return tSyncMaterials;
    }

    @Override
    public PageVo<TSyncMaterial> listMaterial(String selectBy, Integer page, Integer size) {
        if (page == null || size == null) {
            List<TSyncMaterial> list = listMaterial(selectBy);
            PageVo<TSyncMaterial> pageVo = new PageVo<>();
            pageVo.setList(list);
            pageVo.setTotal(list.size());
            pageVo.setCurrent(0);
            pageVo.setSize(list.size());
            return pageVo;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<TSyncMaterial> materialPage = syncMaterialRepository.findCustomPdMaterials(selectBy, pageable);

        PageVo<TSyncMaterial> pageVo = new PageVo<>();
        pageVo.setList(materialPage.getContent());
        pageVo.setTotal((int) materialPage.getTotalElements());
        pageVo.setCurrent(materialPage.getNumber());
        pageVo.setSize(materialPage.getSize());
        return pageVo;
    }

    @Override
    public TSysPdRecord findByPdTimeStrAndNcVwknameAndMaterialTypeFinished(String pdTimeStr, String ncVwkname, String materialTypePd) {
        return tSysPdRecordRepository.findByPdTimeStrAndNcVwknameAndMaterialTypeFinished(pdTimeStr, ncVwkname, materialTypePd);
    }

    @Override
    @Transactional
    public boolean finishPdByMaterialType(String materialTypePd, String userId, String pdTimeStr,String ncVwkname, String workshopNumber) throws Exception {
        // 获取当前用户的产线名称
//        String cwkName = null;
//        List<String> cwkids = userService.getUserCurrentCwkid(userId);
//        if (cwkids != null && !cwkids.isEmpty()) {
//            NcWorkline workline = ncWorklineService.findAllByCwkids(cwkids).stream().findFirst().orElse(null);
//            if (workline != null) {
//                cwkName = workline.getVwkname();
//            }
//        }

        // 创建一条特殊记录标记该物料分类已完成盘点
        TSysPdRecord finishRecord = new TSysPdRecord();
        finishRecord.setPdTime(new Date()); // 设置盘点时间
        finishRecord.setPdTimeStr(pdTimeStr);
        finishRecord.setNcVwkname(ncVwkname);
        finishRecord.setByDeleted("0");
        finishRecord.setPdWorkshopNumber(workshopNumber);
        finishRecord.setPdWorkshopName("");
        finishRecord.setByFp("0");
        finishRecord.setPdType("2"); // 使用特殊类型标识，避免与正常记录冲突
        finishRecord.setCreatedTime(new Date());
        finishRecord.setCreatedName("系统自动结束");
        finishRecord.setPdCreatedName("系统自动结束");
        finishRecord.setMaterialNumber("FINISHED_MATERIAL_TYPE_MARKER_" + materialTypePd); // 设置特殊标记，包含物料分类信息便于识别

        // 保存标记记录
        tSysPdRecordRepository.save(finishRecord);

        return true;
    }
}
