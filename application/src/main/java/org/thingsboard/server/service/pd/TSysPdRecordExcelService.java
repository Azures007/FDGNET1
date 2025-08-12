package org.thingsboard.server.service.pd;

import org.thingsboard.server.dao.mes.dto.TSysPdRecordDto;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TSysPdRecordExcelService {
    void download(String userId, Integer current, Integer size, TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException;

    void downloadWithSplit(String userId, Integer current, Integer size, TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException;

    void downloadWithoutReturn(String userId, Integer current, Integer size, TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException;

}