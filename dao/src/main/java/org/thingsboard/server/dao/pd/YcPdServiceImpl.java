package org.thingsboard.server.dao.pd;

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
import org.thingsboard.server.dao.dto.PdMaterialsDto;
import org.thingsboard.server.dao.sql.mes.ncInventory.NcInventoryRepository;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordRepository;
import org.thingsboard.server.dao.sql.mes.pd.TSysPdRecordSplitRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialBomRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialRepository;

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
    public void savePd(TSysPdRecord tSysPdRecord) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        tSysPdRecord.setPdTime(new Date());
        Date pdTime = tSysPdRecord.getPdTime();
        String format = simpleDateFormat.format(pdTime);
        Integer pdSplit = null;
        tSysPdRecord.setCreatedName(tSysPdRecord.getPdCreatedName());
        if (tSysPdRecord.getPdType().equals("0")) {
            //盘点
            TSysPdRecord tSysPdRecord1 = tSysPdRecordRepository.findByGroup(format,
                    tSysPdRecord.getPdWorkshopNumber(), tSysPdRecord.getPdClassNumber());
            if (tSysPdRecord1 != null) {
                String pdCreatedName = tSysPdRecord1.getPdCreatedName();
                Set<String> nameCollect = Stream.of(pdCreatedName.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());
                nameCollect.add(tSysPdRecord.getCreatedName());
                String nameJoin = StringUtils.join(nameCollect, ", ");
                tSysPdRecord.setPdCreatedName(nameJoin);
            }
            tSysPdRecordRepository.updatePd(format, tSysPdRecord.getPdWorkshopNumber(), tSysPdRecord.getPdClassNumber());
            pdSplit = tSysPdRecord1.getPdRecordId();
        } else {
            //复盘
            Integer pdRecordId = tSysPdRecord.getPdRecordId();
            pdSplit = tSysPdRecord.getPdRecordId();
            TSysPdRecord tSysPdRecord1 = tSysPdRecordRepository.findById(pdRecordId).orElse(null);
            tSysPdRecord1.setByFp("1");
            tSysPdRecordRepository.saveAndFlush(tSysPdRecord1);
        }
        tSysPdRecord.setCreatedTime(new Date());
        tSysPdRecord.setByDeleted("0");
        tSysPdRecord.setByFp("0");
        tSysPdRecord.setPdTimeStr(format);
        tSysPdRecordRepository.saveAndFlush(tSysPdRecord);
        //拆分还原拆料
        savePdBySplit(tSysPdRecord, pdSplit);
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
            tSysPdRecordSplitRepository.delete(deletePdRecordSplitt);
        }
        String materialNumber = tSysPdRecord.getMaterialNumber();
        List<TSyncMaterialBom> tSyncMaterialBoms=syncMaterialBomRepository.findByMaterialNumber(materialNumber);
        if(tSyncMaterialBoms!=null&&tSyncMaterialBoms.size()>0){
            for (TSyncMaterialBom tSyncMaterialBom : tSyncMaterialBoms) {
                TSyncMaterial tSyncMaterial = syncMaterialRepository.findById(tSyncMaterialBom.getMaterialId()).orElse(null);
                TSysPdRecordSplit tSysPdRecordSplit=JSON.parseObject(JSON.toJSONString(tSysPdRecord),TSysPdRecordSplit.class);
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
        List<NcInventory> ncInventories = ncInventoryRepository.pdMaterials(pdMaterialsDto);
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
        return tSysPdRecords;
    }
}
