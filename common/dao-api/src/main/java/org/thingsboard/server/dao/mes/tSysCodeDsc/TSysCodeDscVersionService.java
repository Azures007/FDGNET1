package org.thingsboard.server.dao.mes.tSysCodeDsc;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDscVersion;

import java.util.List;

public interface TSysCodeDscVersionService {

    /**
     * 批量保存字典数据到版本表中
     * @param TSysCodeDscList
     */
    void saveBatch(Integer relId,List<TSysCodeDsc> TSysCodeDscList);

    /**
     * 返回字典类型列表(获取类型字典)
     * @return
     */
//    List<TSysCodeDscVersion> tSysCodeDscGroupList();

    /**
     * 保存字典版本信息
     * @param tSysCodeDscVersion
     */
//    void saveTSysCodeDscVersion(TSysCodeDscVersion tSysCodeDscVersion);

    /**
     * 保存字典分类(MES)
     * @param tSysCodeDscVersion
     */
//    void saveCodeCl(TSysCodeDscVersion tSysCodeDscVersion);
    /**
     * 保存字典(MES)
     * @param tSysCodeDscVersion
     */
//    void saveCode(TSysCodeDscVersion tSysCodeDscVersion);

    /**
     * 删除字典
     * @param codeId
     */
//    void deleteTSysCodeDscVersion(Integer codeId);

    /**
     * 删除字典版本
     * @param codeId
     */
//    void deleteVersion(Integer codeId);

    /**
     * 返回字典类型列表
     * @return
     */
//    Page<TSysCodeDscVersion> tSysCodeDscList(Integer current, Integer size, TSysCodeDscDto tSysCodeDscDto);

    /**
     * 返回字典类型列表(获取类型字典)
     * @return
     */
    Page<TSysCodeDsc> getCodeByCodeCl(Integer current, Integer size, String codeClId,String versionNo, String enabledSt);

//    /**
//     * 根据ID获取字典信息
//     * @param codeId
//     * @return
//     */
//    TSysCodeDscVersion getCodeById(Integer codeId);
//
//    /**
//     * 根据编码分类和编码获取字典表数据
//     * @param codeClId
//     * @param codeValue
//     * @return
//     */
//    TSysCodeDscVersion getCodeByCodeClAndCodeVale(String codeClId,String codeValue);

    /**
     * 根据编码分类和名称获取字典表数据
     * @param codeClId
     * @param codeDscStr
     * @return
     */
//    TSysCodeDscVersion getCodeByCodeClAndCodeDsc(String codeClId,String codeDscStr);

    /**
     * 根据编码分类获取字典表数据
     * @param codeClId
     * @return
     */
    List<TSysCodeDscVersion> getCodeByCodeClId(String codeClId);


}
