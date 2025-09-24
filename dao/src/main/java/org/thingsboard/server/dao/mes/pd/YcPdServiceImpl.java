package org.thingsboard.server.dao.mes.pd;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterialBom;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecord;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecordSplit;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.dao.mes.dto.PdMaterialsDto;
import org.thingsboard.server.dao.sql.mes.ncInventory.NcInventoryRepository;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordRepository;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordSplitRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialBomRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class YcPdServiceImpl implements YcPdService {

    @Autowired
    TSysPdRecordRepository tSysPdRecordRepository;

    @Autowired
    NcInventoryRepository ncInventoryRepository;

    @Autowired
    TSysPdRecordSplitRepository tSysPdRecordSplitRepository;

    @Autowired
    SyncMaterialBomRepository syncMaterialBomRepository;

    @Autowired
    SyncMaterialRepository syncMaterialRepository;

    @Transactional
    @Override
    public TSysPdRecord savePd(TSysPdRecord tSysPdRecord) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        tSysPdRecord.setPdTime(new Date());
        Date pdTime = tSysPdRecord.getPdTime();
        String format = simpleDateFormat.format(pdTime);
        Integer pdSplit = null;
        tSysPdRecord.setCreatedName(tSysPdRecord.getPdCreatedName());
        TSysPdRecord tSysPdRecord1 = tSysPdRecordRepository.findByGroup(format,
                tSysPdRecord.getMaterialNumber(), tSysPdRecord.getPdClassNumber(), tSysPdRecord.getPdType());
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
        tSysPdRecordRepository.saveAndFlush(tSysPdRecord);
        //更新库存
        List<NcInventory> ncInventories = ncInventoryRepository.findByWarehouseIdAndMaterialCodeAndStatusOrderByLotAsc(tSysPdRecord.getPdWorkshopNcId(),
                tSysPdRecord.getMaterialNumber(), "生效");
        if (ncInventories != null && ncInventories.size() > 0) {
            for (NcInventory ncInventory : ncInventories) {
                ncInventory.setQty(tSysPdRecord.getPdQty().floatValue());
                ncInventoryRepository.saveAndFlush(ncInventory);
            }
        }
        //拆分还原拆料
        savePdBySplit(tSysPdRecord, pdSplit);

        return tSysPdRecord;
    }

    /**
     * 盘点保存---拆分还原拆料
     *
     * @param tSysPdRecord
     * @param pdSplit
     */
    private void savePdBySplit(TSysPdRecord tSysPdRecord, Integer pdSplit) {
        if (pdSplit != null) {
            TSysPdRecordSplit deletePdRecordSplitt = new TSysPdRecordSplit();
            deletePdRecordSplitt.setRePdRecordId(pdSplit);
            tSysPdRecordSplitRepository.deleteByRePdRecordId(deletePdRecordSplitt.getRePdRecordId());
        }
        String materialNumber = tSysPdRecord.getMaterialNumber();
        List<TSyncMaterialBom> tSyncMaterialBoms = syncMaterialBomRepository.findByMaterialNumber(materialNumber);
        if (tSyncMaterialBoms != null && tSyncMaterialBoms.size() > 0) {
            for (TSyncMaterialBom tSyncMaterialBom : tSyncMaterialBoms) {
                TSyncMaterial tSyncMaterial = syncMaterialRepository.findById(tSyncMaterialBom.getMaterialId()).orElse(null);
                TSysPdRecordSplit tSysPdRecordSplit = JSON.parseObject(JSON.toJSONString(tSysPdRecord), TSysPdRecordSplit.class);
                tSysPdRecordSplit.setRePdRecordId(tSysPdRecord.getPdRecordId());
                tSysPdRecordSplit.setPdQty(tSysPdRecord.getPdQty().multiply(tSyncMaterialBom.getRatio()).doubleValue());
                tSysPdRecordSplit.setMaterialName(tSyncMaterialBom.getMaterialName());
                tSysPdRecordSplit.setMaterialNumber(tSyncMaterial.getMaterialCode());
                tSysPdRecordSplit.setMaterialSpecifications(tSyncMaterial.getMaterialModel());
                tSysPdRecordSplitRepository.save(tSysPdRecordSplit);
            }
        }
    }

    @Override
    public List<NcInventory> pdMaterials(PdMaterialsDto pdMaterialsDto) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        pdMaterialsDto.setPdTimeStr(simpleDateFormat.format(new Date()));
        List<Map> ncInventorieMs = ncInventoryRepository.pdMaterials(pdMaterialsDto);
        List<NcInventory> ncInventories = JSON.parseArray(JSON.toJSONString(ncInventorieMs), NcInventory.class);
        return ncInventories;
    }

    @Override
    public List<TSysPdRecord> fpWorkshopRecord(String startDate, String endDate) {
        List<Map> tSysPdRecords = tSysPdRecordRepository.fpWorkshopRecord(startDate, endDate);
        List<TSysPdRecord> tSysPdRecords1 = JSON.parseArray(JSON.toJSONString(tSysPdRecords), TSysPdRecord.class);
        return tSysPdRecords1;
    }

    @Override
    public List<TSysPdRecord> showWorkshopRecord(String pdTimeStr, String pdWorkshopNumber) {
        List<TSysPdRecord> tSysPdRecords = tSysPdRecordRepository.showWorkshopRecord(pdTimeStr, pdWorkshopNumber);
        for (TSysPdRecord tSysPdRecord : tSysPdRecords) {
            tSysPdRecord.setPdQty(tSysPdRecord.getPdQty().setScale(3, RoundingMode.HALF_UP));
        }
        return tSysPdRecords;
    }

    @Override
    public List<TSyncMaterial> listMaterial(String selectBy) {
        List<TSyncMaterial> tSyncMaterials = syncMaterialRepository.listMaterialsBySelctct(selectBy);
        return tSyncMaterials;
    }
}
