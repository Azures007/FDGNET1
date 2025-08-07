package org.thingsboard.server.dao.order;

import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.common.data.web.ResponseResult;

import java.util.List;
import java.util.Map;

public interface OrderProcessService {

    /**
     * 根据订单ID获取当前工序
     * @param orderId
     * @param processid
     * @return
     */
    TBusOrderProcess getProcessByOrderId(Integer orderId, Integer processid);

    /**
     * 根据订单ID获取上一道工序
     * @param orderId
     * @param orderProcessId
     * @return
     */
    TBusOrderProcess getProcessPrevByOrderId(Integer orderId, Integer orderProcessId);

    /**
     * 保存/修改
     * @param tBusOrderProcess
     */
    void saveTBusOrderProcess(TBusOrderProcess tBusOrderProcess);

    /**
     * 根据工序ID获取工序
     * @param orderProcessId
     * @return
     */
    TBusOrderProcess findById(Integer orderProcessId);
    /**
     * 移交提交（废弃）
     * @param orderId
     * @param orderProcessId
     * @param classId
     * @param handOverUserId
     * @return
     */
    ResponseResult handOver(Integer orderId, Integer orderProcessId, Integer classId, String handOverUserId, String userId);

    /**
     * 移交提交
     * @param tSysPersonnelInfo
     * @param tSysClass
     * @param handOverOrderProcess
     * @param orderId
     * @param userId
     */
    void handOver(TSysPersonnelInfo tSysPersonnelInfo, TSysClass tSysClass, TBusOrderProcess handOverOrderProcess, Integer orderId, String userId);

    List<Map> listProcess();
}
