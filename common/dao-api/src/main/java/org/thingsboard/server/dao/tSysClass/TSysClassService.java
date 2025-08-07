package org.thingsboard.server.dao.tSysClass;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.mes.sys.TSysClassGroupLeaderRel;
import org.thingsboard.server.common.data.mes.sys.TSysClassPersonnelRel;
import org.thingsboard.server.dao.dto.TSysClassDto;
import org.thingsboard.server.dao.vo.*;

import java.util.List;


public interface TSysClassService {
    /**
     * 返回班别列表
     * @return
     */
    Page<TSysClass> tSysClassList(String userId,Integer current, Integer size, TSysClassDto tSysClassDto);
    /**
     * 返回班别列表
     * @return
     */
    Page<TSysClass> tSysClassList(Integer current, Integer size, TSysClassDto tSysClassDto);
    /**
     * 返回班别组长列表(用于导出)
     * @return
     */
    PageVo<ClassGroupLeaderExpVo> tSysClassListExp(Integer current, Integer size, TSysClassDto tSysClassDto);
    /**
     * 返回班别组员列表(用于导出)
     * @return
     */
    PageVo<ClassPersonnelExpVo> tSysClassListExp2(Integer current, Integer size, TSysClassDto tSysClassDto);

    /**
     * 保存班别信息
     * @param tSysClass
     */
    void saveTSysClass(TSysClass tSysClass);

    /**
     * 保存班别信息和组长信息
     * @param tSysClass
     */
    void saveTSysClassAndGroupLeaderRel(TSysClass tSysClass, List<TSysClassGroupLeaderRel> tSysClassGroupLeaderRelList);


    /**
     * 删除班别
     * @param classId
     */
    void deleteTSysClass(Integer classId);

    /**
     * 根据ID获取班别信息
     * @param classId
     * @return
     */
    TSysClass getClassById(Integer classId);

    /**
     * 根据ID获取班别信息，并校验排班信息
     * @param classId
     * @return
     */
    TSysClass getAndCheckByScheduling(Integer classId);

    /**
     * 根据工序ID获取可执行该工序的班组列表
     * @param processId
     * @return
     */
    List<TSysClass> getClassByProcessId(Integer processId);
//    /**
//     * 获取未被分配关系的人员列表
//     * @return
//     */
//    List<TSysClassPersonnelRel> getPersonnelInfo();

    /**
     * 根据组别ID获取分配关系的人员列表
     * @param classId
     * @return
     */
    List<ClassPersinnelRelVo> getRelByClassId(Integer classId);

    /**
     * 根据组别ID获取分配组长关系的人员列表
     * @param classId
     * @return
     */
    List<ClassGroupLeaderRelVo> getGroupLeaderRelByClassId(Integer classId);

    /**
     * 创建关联关系
     * @param classId
     * @param tSysClassPersonnelRelList
     */
    void saveTSysClassPersonnelRel(Integer classId, List<TSysClassPersonnelRel> tSysClassPersonnelRelList);

    /**
     * 创建组长和班别关系表
     * @param classId
     * @param tSysClassGroupLeaderRelList
     */
    void saveTSysClassGroupLeaderRel(Integer classId, List<TSysClassGroupLeaderRel> tSysClassGroupLeaderRelList);

    /**
     * 校验要指派的组长人员是否已绑定在其他班组
     * @param classId
     * @param personnelId
     * @return
     */
    List<TSysClassGroupLeaderRel> getTSysClassGroupLeaderRelByPersonnelIdAndOtherClass(Integer classId,List<Integer> personnelId);

    /**
     * 获取用户班别信息
     * @param toString
     * @return
     */
    UserClassVo getUserClass(String toString) throws JsonProcessingException;
}
