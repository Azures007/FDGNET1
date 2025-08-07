package org.thingsboard.server.dao.mes.midOrg;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.mid.MidOrg;
import org.thingsboard.server.dao.mes.dto.MidOrgDto;
import org.thingsboard.server.dao.sql.mes.licheng.MidOrgRepository;
import org.thingsboard.server.dao.mes.vo.PageVo;

@Service
public class MidOrgServiceImpl implements MidOrgService {

    @Autowired
    MidOrgRepository midOrgRepository;

    @Override
    public PageVo<MidOrg> listMidOrg(Integer current, Integer size, MidOrgDto midOrgDto) throws Exception {
        Sort sort = Sort.by(Sort.Direction.DESC, "gmtCreate");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("kdOrgNum", ExampleMatcher.GenericPropertyMatchers.exact());
        MidOrg midOrg = new MidOrg();
        BeanUtils.copyProperties(midOrgDto, midOrg);
        Example<MidOrg> example = Example.of(midOrg, matcher);
        Page<MidOrg> midOrgPage = midOrgRepository.findAll(example, pageable);
        PageVo<MidOrg> pageVo = new PageVo<>(midOrgPage);
        return pageVo;
    }
}
