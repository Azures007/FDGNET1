package org.thingsboard.server.dao.tSysCodeDsc;

import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.dto.TSysCodeDscDto;
import org.thingsboard.server.dao.vo.AppVersionVo;

import java.util.List;

public interface TSysCodeDscService {

    /**
     * 返回字典类型列表(获取类型字典)
     * @return
     */
    List<TSysCodeDsc> tSysCodeDscGroupList();

    /**
     * 保存字典信息
     * @param tSysCodeDsc
     */
    void saveTSysCodeDsc(TSysCodeDsc tSysCodeDsc);

    /**
     * 保存字典分类(MES)
     * @param tSysCodeDsc
     */
    void saveCodeCl(TSysCodeDsc tSysCodeDsc);
    /**
     * 保存字典(MES)
     * @param tSysCodeDsc
     */
    void saveCode(TSysCodeDsc tSysCodeDsc);

    /**
     * 删除字典
     * @param codeId
     */
    void deleteTSysCodeDsc(Integer codeId);

    /**
     * 删除字典(MES)
     * @param codeId
     */
    void deleteCode(Integer codeId);

    /**
     * 返回字典类型列表
     * @return
     */
    Page<TSysCodeDsc> tSysCodeDscList(Integer current, Integer size, TSysCodeDscDto tSysCodeDscDto);

    /**
     * 返回字典类型列表(获取类型字典)
     * @return
     */
    Page<TSysCodeDsc> getCodeByCodeCl(Integer current, Integer size, String codeClId, String enabledSt);

    /**
     * 根据字典分类编码获取字典列表（不过滤启停状态）
     * @return
     */
    Page<TSysCodeDsc> getCodeByCodeClNotJudEt(Integer current, Integer size, String codeClId);


    /**
     * 根据ID获取字典信息
     * @param codeId
     * @return
     */
    TSysCodeDsc getCodeById(Integer codeId);

    /**
     * 根据编码分类和编码获取字典表数据
     * @param codeClId
     * @param codeValue
     * @return
     */
    TSysCodeDsc getCodeByCodeClAndCodeVale(String codeClId,String codeValue);

    /**
     * 根据编码分类和名称获取字典表数据
     * @param codeClId
     * @param codeDscStr
     * @return
     */
    TSysCodeDsc getCodeByCodeClAndCodeDsc(String codeClId,String codeDscStr);

    /**
     * 根据编码分类获取字典表数据
     * @param codeClId
     * @return
     */
    List<TSysCodeDsc> getCodeByCodeClId(String codeClId);

    /**
     * 获取app最新版本
     * @return
     */
    ResponseResult<AppVersionVo> getAPPVersion();
}
