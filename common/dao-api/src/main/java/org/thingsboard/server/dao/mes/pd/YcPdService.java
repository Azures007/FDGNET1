package org.thingsboard.server.dao.mes.pd;

import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecord;
import org.thingsboard.server.dao.mes.vo.PdMaterialsVo;
import org.thingsboard.server.dao.mes.dto.PdMaterialsDto;

import java.util.List;
import java.util.Map;

public interface YcPdService {

    TSysPdRecord savePd(TSysPdRecord tSysPdRecord, String userId);

    PdMaterialsVo pdMaterials(PdMaterialsDto pdMaterialsDto, String userId);

    List<TSysPdRecord> fpWorkshopRecord(String startDate, String endDate,String userId);

    List<TSysPdRecord> showWorkshopRecord(String pdTimeStr, String pdWorkshopNumber, String ncVwkname);

    List<TSyncMaterial> listMaterial(String selectBy);
    
    /**
     * 结束指定物料分类的盘点
     * @param materialType 物料分类
     * @param userId 用户ID
     * @param pdTimeStr 盘点日期
     * @return 是否成功结束
     */
    boolean finishPdByMaterialType(String materialType, String userId, String pdTimeStr) throws Exception;
    
    /**
     * 根据日期、产线和物料分类查询是否已完成盘点
     * @param pdTimeStr 盘点日期
     * @param ncVwkname 产线名称
     * @param materialType 物料分类
     * @return 是否已完成盘点
     */
    TSysPdRecord findByPdTimeStrAndNcVwknameAndMaterialTypeFinished(String pdTimeStr, String ncVwkname, String materialType);

}
