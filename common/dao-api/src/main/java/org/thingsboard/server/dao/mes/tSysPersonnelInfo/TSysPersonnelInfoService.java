package org.thingsboard.server.dao.mes.tSysPersonnelInfo;


import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.common.data.mes.sys.TSysUserDevices;
import org.thingsboard.server.dao.mes.dto.TSysPersonnelInfoDto;
import org.thingsboard.server.dao.mes.dto.VerifyDevicesDto;

import java.util.List;

public interface TSysPersonnelInfoService {
    /**
     * 返回人员列表
     * @return
     */
    Page<TSysPersonnelInfo> tSysPersonnelInfoList(Integer current, Integer size, TSysPersonnelInfoDto tSysPersonnelInfoDto);

    Page<TSysPersonnelInfo> tSysPersonnelInfoListByUserId(Integer current, Integer size, TSysPersonnelInfoDto tSysPersonnelInfoDto, String userId);

    /**
     * 保存人员信息
     * @param tSysPersonnelInfo
     */
    void saveTSysPersonnelInfo(TSysPersonnelInfo tSysPersonnelInfo);

    /**
     * 判断绑定的用户ID是否已被绑定指其他人员
     * @param userId
     * @return
     */
    Boolean judgeUserIdRepeat(Integer personnelId,String userId);

    /**
     * 删除人员
     * @param personnelId
     */
    void deleteTSysPersonnelInfo(Integer personnelId);

    /**
     * 根据ID获取详情
     * @param personnelId
     */
    TSysPersonnelInfo getPersonnelInfoById(Integer personnelId);

    /**
     * 根据绑定的USERID获取人员信息
     * @param userId
     * @return
     */
    TSysPersonnelInfo getPersonnelInfoByUserId(String userId) ;

    /**
     * 添加设备信息
     * @param tSysUserDevices
     */
    void addDevices(TSysUserDevices tSysUserDevices);

    /**
     * 验证指纹信息
     * @param verifyDevicesDto
     * @return
     */
    Boolean verifyDevices(VerifyDevicesDto verifyDevicesDto);

    /**
     * 获取组长id列表
     * @param personnelId
     * @return
     */
    List<String> listGroupLeader(Integer personnelId);

    /**
     *  通过指纹获取人员信息
     * @param userId
     * @param content
     * @return
     */
    TSysPersonnelInfo getPersonnelByDevices(String userId, String content);
}
