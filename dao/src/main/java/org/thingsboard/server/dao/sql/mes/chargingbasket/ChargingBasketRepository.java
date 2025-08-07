package org.thingsboard.server.dao.sql.mes.chargingbasket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.mes.sys.TSysChargingBasket;

import java.util.List;

public interface ChargingBasketRepository extends JpaRepository <TSysChargingBasket,Integer>{
    List<TSysChargingBasket> findByCode(String code);

    TSysChargingBasket getByCode(String bindCodeNumber);
}
