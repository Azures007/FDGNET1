package org.thingsboard.server.dao.mes.orderProcess;

public interface AppOrderProcessRecordDeleteService {

    /**
     * 删除报工记录
     * 默认不允许删除自动投入产出的记录
     * @param orderProcessHistoryId
     * @param checkRecordTypeBg
     */
    void deleteRecord(Integer orderProcessHistoryId, Boolean checkRecordTypeBg,String isConfirm);

    /**
     * 删除报工记录
     * @param orderProcessHistoryId
     * @param checkRecordTypeBg
     * @param deleteAutoRecord 是否可删除自动投入产出的记录
     */
    void deleteRecord(Integer orderProcessHistoryId, Boolean checkRecordTypeBg, Boolean deleteAutoRecord,String isConfirm);



}
