package org.thingsboard.server.service.pd;

import org.thingsboard.server.dao.mes.dto.TSysPdRecordDto;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface TSysPdRecordExcelService {
    void download(String currentUserId, List<String> userCwkids, Integer current, Integer size, TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException;

    void downloadWithSplit(String currentUserId, List<String> userCwkids, Integer current, Integer size, TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException;

    void downloadWithoutReturn(String currentUserId, List<String> userCwkids, Integer current, Integer size, TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException;

}