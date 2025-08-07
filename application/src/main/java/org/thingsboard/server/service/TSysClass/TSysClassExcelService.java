package org.thingsboard.server.service.TSysClass;

import org.thingsboard.server.dao.mes.dto.TSysClassDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TSysClassExcelService {

    void download(Integer current, Integer size, TSysClassDto classDto, HttpServletResponse response) throws IOException;
}
