package org.thingsboard.server.dao.mes.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPPBom;
import org.thingsboard.server.dao.mes.dto.TBusOrderPPBomDto;
import org.thingsboard.server.dao.sql.mes.order.OrderPPBomRepository;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.role
 * @date 2022/4/22 19:43
 * @Description:
 */
@Service
@Slf4j
public class OrderPPBomServiceImpl implements OrderPPBomService {
    @Autowired
    OrderPPBomRepository orderPPBomRepository;

    @Override
    public Page<TBusOrderPPBom> tBusOrderPPBomList(Integer current, Integer size, TBusOrderPPBomDto tBusOrderPPBomDto) {
        Pageable pageable= PageRequest.of(current,size);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name",ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("materialNumber",ExampleMatcher.GenericPropertyMatchers.exact());
        TBusOrderPPBom orderPPBom = new TBusOrderPPBom();
        BeanUtils.copyProperties(tBusOrderPPBomDto,orderPPBom);
        Example<TBusOrderPPBom> example = Example.of(orderPPBom,matcher);
        Page<TBusOrderPPBom> orderPPBomPage = orderPPBomRepository.findAll(example,pageable);
        return orderPPBomPage;
    }

    @Override
    public void saveTBusOrderPPBom(TBusOrderPPBom orderPPBom) {
//        if(orderPPBom.getOrderPPBomId() == null) {
//            //新增
////            tBusOrderPPBom.setCrtUser(orderPPBom.getUpdateUser());
////            tBusOrderPPBom.setCrtTime(orderPPBom.getUpdateTime());
////            if(StringUtils.isBlank(orderPPBom.getEnabledSt())){
////                orderPPBom.setEnabledSt("0");
////            }
//        }
        orderPPBomRepository.saveAndFlush(orderPPBom);
    }

    @Override
    public void deleteTBusOrderPPBom(Integer orderPPBomId) {
        orderPPBomRepository.deleteById(orderPPBomId);
    }

    @Override
    public boolean getMainPpbomFlag(Integer orderPPBomId) {
        if (orderPPBomId == null) {
            return false;
        }
        Integer countMainPpbomId = orderPPBomRepository.getCountMainPpbomId(orderPPBomId);
        if (countMainPpbomId > 0)
            return true;
        return false;
    }
}
