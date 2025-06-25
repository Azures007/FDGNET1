package org.thingsboard.server.service.order;

import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysCraftInfo;
import org.thingsboard.server.dao.dto.OrderChangeClassSaveDto;
import org.thingsboard.server.dao.dto.OrderStartOrderSaveDto;
import org.thingsboard.server.dao.dto.TBusOrderDto;
import org.thingsboard.server.dao.dto.TBusOrderHeadDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface OrderHeadExcelService {

    void download(Integer current, Integer size, TBusOrderHeadDto tBusOrderHeadDto, HttpServletResponse response) throws IOException;
    void download(Integer current, Integer size, TBusOrderDto tBusOrderDto, HttpServletResponse response) throws IOException;
    void downloadOrder(Integer current, Integer size, TBusOrderDto tBusOrderDto, HttpServletResponse response) throws Exception;


}
