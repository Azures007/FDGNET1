package org.thingsboard.server.service.TSysDevice;

import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.dto.TSysDeviceDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: l
 * @Date: 2022/4/26 16:19
 * @Description:
 */
public interface TSysDeviceExcelService {
    void download( Integer current, Integer size,  TSysDeviceDto deviceDto,HttpServletResponse response) throws IOException;
    ResponseResult upload(MultipartFile file, String name);

    void downTemplate(HttpServletResponse response);
}