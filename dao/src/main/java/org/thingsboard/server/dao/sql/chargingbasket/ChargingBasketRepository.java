package org.thingsboard.server.dao.sql.chargingbasket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.common.data.TSysChargingBasket;

import java.util.List;

public interface ChargingBasketRepository extends JpaRepository <TSysChargingBasket,Integer>{
    List<TSysChargingBasket> findByCode(String code);

    TSysChargingBasket getByCode(String bindCodeNumber);
}
