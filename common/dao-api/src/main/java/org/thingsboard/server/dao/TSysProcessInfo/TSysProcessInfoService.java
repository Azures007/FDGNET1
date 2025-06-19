package org.thingsboard.server.dao.TSysProcessInfo;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysProcessInfo;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.dto.TSysProcessInfoDto;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/20 18:02
 * @Description:工序管理接口
 */
public interface TSysProcessInfoService {
    void saveProcess(TSysProcessInfo processInfo);

    TSysProcessInfo processDetail(Integer processId);

    /**
     * 根据ID获取工序信息，并校验状态
     * @param processId
     * @return
     */
    TSysProcessInfo getAndCheck(Integer processId);

    Page<TSysProcessInfo> getProcessList(Integer current, Integer size);

    Page<TSysProcessInfo> getProcessList(Integer current, Integer size, TSysProcessInfoDto tSysProcessInfoDto);

    void enableProcess(Integer processId, Integer enable, String name);

    ResponseResult processSetting(Integer processId, List<Integer> classIds, String name);

    List<TSysClass> classList(Integer processId);

    void delete(Integer processId);

    /**
     * 根据工序编码获取工序信息
     * @param processNumber
     * @return
     */
    TSysProcessInfo getByProcessNumber(String processNumber);

    TSysProcessInfo processDetailByOrderProcessId(Integer orderProcessId);
}