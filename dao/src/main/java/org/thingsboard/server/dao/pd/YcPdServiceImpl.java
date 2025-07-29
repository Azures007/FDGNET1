package org.thingsboard.server.dao.pd;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.TSysPdRecord;
import org.thingsboard.server.common.data.nc_inventory.NcInventory;
import org.thingsboard.server.dao.dto.PdMaterialsDto;
import org.thingsboard.server.dao.sql.nc_inventory.NcInventoryRepository;
import org.thingsboard.server.dao.sql.pd.TSysPdRecordRepository;

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

    @Transactional
    @Override
    public void savePd(TSysPdRecord tSysPdRecord) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        tSysPdRecord.setPdTime(new Date());
        Date pdTime = tSysPdRecord.getPdTime();
        String format = simpleDateFormat.format(pdTime);
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
            tSysPdRecordRepository.updatePd(format,tSysPdRecord.getPdWorkshopNumber(), tSysPdRecord.getPdClassNumber());
        }else {
            //复盘
            Integer pdRecordId = tSysPdRecord.getPdRecordId();
            TSysPdRecord tSysPdRecord1 = tSysPdRecordRepository.findById(pdRecordId).orElse(null);
            tSysPdRecord1.setByFp("1");
            tSysPdRecordRepository.saveAndFlush(tSysPdRecord1);
        }
        tSysPdRecord.setCreatedTime(new Date());
        tSysPdRecord.setByDeleted("0");
        tSysPdRecord.setByFp("0");
        tSysPdRecord.setPdTimeStr(format);
        tSysPdRecordRepository.saveAndFlush(tSysPdRecord);
    }

    @Override
    public List<NcInventory> pdMaterials(PdMaterialsDto pdMaterialsDto) {
        List<NcInventory> ncInventories = ncInventoryRepository.pdMaterials(pdMaterialsDto);
        return ncInventories;
    }

    @Override
    public List<TSysPdRecord> fpWorkshopRecord(String startDate, String endDate) {
        List<Map> tSysPdRecords=tSysPdRecordRepository.fpWorkshopRecord(startDate,endDate);
        List<TSysPdRecord> tSysPdRecords1 = JSON.parseArray(JSON.toJSONString(tSysPdRecords), TSysPdRecord.class);
        return tSysPdRecords1;
    }

    @Override
    public List<TSysPdRecord> showWorkshopRecord(String pdTimeStr, String pdWorkshopNumber) {
        List<TSysPdRecord> tSysPdRecords=tSysPdRecordRepository.showWorkshopRecord(pdTimeStr,pdWorkshopNumber);
        return tSysPdRecords;
    }
}
