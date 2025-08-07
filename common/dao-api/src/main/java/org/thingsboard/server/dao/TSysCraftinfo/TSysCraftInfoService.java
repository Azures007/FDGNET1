package org.thingsboard.server.dao.TSysCraftinfo;

import org.thingsboard.server.common.data.mes.sys.TSysCraftInfo;
import org.thingsboard.server.common.data.mes.sys.TSysCraftProcessRel;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.dto.ListMaterialDto;
import org.thingsboard.server.dao.dto.TSysCraftInfoSaveDto;
import org.thingsboard.server.dao.dto.TSysCraftSearchDto;
import org.thingsboard.server.dao.vo.PageVo;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Auther: l
 * @Date: 2022/4/21 15:42
 * @Description:工艺路线管理接口
 */
public interface TSysCraftInfoService {

    void save(TSysCraftInfoSaveDto craftInfoSaveDto);

    PageVo<TSysCraftInfoSaveDto> list(String userId,Integer current, Integer size, TSysCraftSearchDto searchDto);

    TSysCraftInfoSaveDto detail(Integer craftId);

    //工艺路线获取下一道工序
    TSysProcessInfo getCraftNextProcessInfo(Integer craftId, Integer processId);

    @Transactional
    void delete(Integer craftId);

    void enable(Integer craftId, Integer enable, String name);

    /**
     * 根据ID获取工艺路线信息，并校验状态和生效时间
     * @param craftId
     * @return
     */
    public TSysCraftInfo getAndCheck(Integer craftId) throws ParseException;

    @Transactional
    ResponseResult setMaterial(Integer craftId, List<Map> materialCodes, String name);

    ResponseResult materialList(Integer craftId, Integer current, Integer size, ListMaterialDto listMaterialDto);

    TSysCraftProcessRel findByCraftIdAndProcessId(Integer craftId, Integer processId);

    /**
     * 获取工艺路线
     * @param craftId
     * @return
     */
    TSysCraftInfo findByCraftId(Integer craftId);

    /**
     * 获取工艺路线的最后一个工序信息
     * @param craftId
     * @return
     */
    TSysProcessInfo findLastProcessIdByCraftId(Integer craftId);

}
