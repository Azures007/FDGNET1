package org.thingsboard.server.dao.mes.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessPersonRel;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.dao.mes.order.OrderProcessPersonRelService;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessPersonRelRepository;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysPersonnelInfoRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.role
 * @date 2022/8/2 9:43
 * @Description:
 */
@Service
@Slf4j
public class OrderProcessPersonRelServiceImpl implements OrderProcessPersonRelService {
    @Autowired
    OrderProcessPersonRelRepository orderProcessPersonRelRepository;

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Override
    public String createdPersonGroupId(Integer orderProcessId, List<Integer> personIds) {
        if (personIds == null || personIds.size() == 0) {
            return "";
        }
        String personGroupId = orderProcessPersonRelRepository.getPersonGroupId(orderProcessId, personIds, personIds.size());
        if (StringUtils.isEmpty(personGroupId)) {
            personGroupId = UUID.randomUUID().toString();
            List<TBusOrderProcessPersonRel> orderProcessPersonRels = new ArrayList<>();
            TBusOrderProcessPersonRel orderProcessPersonRel = null;
            for (var personId: personIds) {
                orderProcessPersonRel = new TBusOrderProcessPersonRel();
                orderProcessPersonRel.setOrderProcessId(orderProcessId);
                orderProcessPersonRel.setDevicePersonId(personId);
                orderProcessPersonRel.setDevicePersonGroupId(personGroupId);
                orderProcessPersonRel.setCrtTime(new Date());
                orderProcessPersonRel.setUpdateTime(new Date());
                orderProcessPersonRels.add(orderProcessPersonRel);
            }
            orderProcessPersonRelRepository.saveAll(orderProcessPersonRels);
        }
        return personGroupId;
    }

    @Override
    public String getPersonGroupId(Integer orderProcessId, List<Integer> personIds) {
        if (personIds == null || personIds.size() == 0) {
            return "";
        }
        return orderProcessPersonRelRepository.getPersonGroupId(orderProcessId, personIds, personIds.size());
    }

    @Override
    public List<TBusOrderProcessPersonRel> findByPersonGroupId(String devicePersonGroupId) {
        return orderProcessPersonRelRepository.findAllByDevicePersonGroupId(devicePersonGroupId);
    }

    @Override
    public String getPersonGroupNames(String devicePersonGroupId) {
        if (StringUtils.isNotEmpty(devicePersonGroupId)) {
            List<TBusOrderProcessPersonRel> personRels = orderProcessPersonRelRepository.findAllByDevicePersonGroupId(devicePersonGroupId);
            StringBuilder personBuf = new StringBuilder();
            for (TBusOrderProcessPersonRel personRel : personRels) {
                TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findById(personRel.getDevicePersonId()).orElse(null);
                personBuf.append(personnelInfo.getName() + ",");
            }
            if (personBuf.length() > 0) {
                return personBuf.substring(0, personBuf.lastIndexOf(","));
            }
        }
        return null;
    }


}
