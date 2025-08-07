package org.thingsboard.server.dao.midDept;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.mid.MidDept;
import org.thingsboard.server.common.data.mes.mid.MidOrg;
import org.thingsboard.server.dao.dto.MidDeptDto;
import org.thingsboard.server.dao.midDetp.MidDeptService;
import org.thingsboard.server.dao.sql.mes.licheng.MidDeptRepository;
import org.thingsboard.server.dao.sql.mes.licheng.MidOrgRepository;
import org.thingsboard.server.dao.vo.PageVo;

@Service
public class MidDeptServiceImpl implements MidDeptService {

    @Autowired
    MidDeptRepository midDeptRepository;

    @Autowired
    MidOrgRepository midOrgRepository;

    @Override
    public PageVo<MidDept> listMidDept(Integer current, Integer size, MidDeptDto midDeptDto) throws Exception {
        if (midDeptDto.getKdOrgId() != null) {
            MidOrg midOrg = midOrgRepository.findByKdOrgId(midDeptDto.getKdOrgId());
            if (midOrg != null) {
                midDeptDto.setKdOrgNum(midOrg.getKdOrgNum());
            } else {
                //过滤返回空
                midDeptDto.setKdOrgNum(midDeptDto.getKdOrgId() + "");
            }
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "gmtCreate");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("kdDeptNum", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("kdOrgNum", ExampleMatcher.GenericPropertyMatchers.exact());
        MidDept midDept = new MidDept();
        BeanUtils.copyProperties(midDeptDto, midDept);
        Example<MidDept> example = Example.of(midDept, matcher);
        Page<MidDept> midDeptPage = midDeptRepository.findAll(example, pageable);
        PageVo<MidDept> pageVo = new PageVo<>(midDeptPage);
        return pageVo;
    }
}
