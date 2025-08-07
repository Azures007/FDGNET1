package org.thingsboard.server.service.order;

import org.thingsboard.server.dao.dto.TBusOrderDto;
import org.thingsboard.server.dao.dto.TBusOrderHeadDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface OrderHeadExcelService {

    void download(Integer current, Integer size, TBusOrderHeadDto tBusOrderHeadDto, HttpServletResponse response) throws IOException;
    void download(String userId,Integer current, Integer size, TBusOrderDto tBusOrderDto, HttpServletResponse response) throws IOException;
    void downloadOrder(Integer current, Integer size, TBusOrderDto tBusOrderDto, HttpServletResponse response) throws Exception;


}
