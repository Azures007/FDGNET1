package org.thingsboard.server.service.TSysDevice;

import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.mes.dto.TSysDeviceDto;
import org.thingsboard.server.dao.mes.vo.InsourcingDeviceRunVo;
import org.thingsboard.server.dao.mes.vo.OvenDeviceRunVo;
import org.thingsboard.server.dao.mes.vo.TanSensorDeviceRunVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/26 16:19
 * @Description:
 */
public interface TSysDeviceExcelService {
    void download(String userId, Integer current, Integer size,  TSysDeviceDto deviceDto,HttpServletResponse response) throws IOException;
    ResponseResult upload(MultipartFile file, String name);

    void downTemplate(HttpServletResponse response);

    /**
     * 设备运行报表导出
     * @param response
     * @param insourcingDeviceRunVoList
     * @param tanSensorDeviceRunVos
     * @param ovenDeviceRunVos
     */
    void exportDeviceRunBoard(HttpServletResponse response, List<InsourcingDeviceRunVo> insourcingDeviceRunVoList, List<TanSensorDeviceRunVo> tanSensorDeviceRunVos, List<OvenDeviceRunVo> ovenDeviceRunVos);
}
