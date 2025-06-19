package org.thingsboard.server.dao.licheng;

public interface LiChengService {
    /**
     * 同步程序
     * @param orderId
     */
    void sync(Integer orderId);

    /**
     * 同步订单状态
     * @param orderEntryId
     */
    void syncStatus(Integer orderEntryId);

    /**
     * 添加日志
     * @param toString
     * @param s
     * @param message
     */
    void addLog(String toString, String s, String message,String syncType);

    /**
     * 同步物料表
     */
    void midMaterialSync(Boolean flag);

    /**
     * 订单变更
     * @param id
     */
    void syncOrderUpdate(Integer id);

    /**
     * 同步组织表
     */
    void syncOrg();

    /**
     * 部门表同步
     */
    void syncDept();
}
