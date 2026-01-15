package org.thingsboard.server.service.pd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.mes.TSysPdRecord.TSysPdRecordService;
import org.thingsboard.server.dao.mes.dto.TSysPdRecordDto;
import org.thingsboard.server.dao.mes.vo.TSysPdRecordVo;
import org.thingsboard.server.utils.ExcelUtil;
import org.thingsboard.server.vo.TSysPdRecordExcelVo;
import org.thingsboard.server.vo.TSysPdRecordExcelWithoutReturnVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TSysPdRecordExcelServiceImpl implements TSysPdRecordExcelService {

    @Autowired
    private TSysPdRecordService tSysPdRecordService;

    @Autowired
    private org.thingsboard.server.dao.user.UserService userService;

    @Override
    public void download(String currentUserId, List<String> userCwkids, Integer current, Integer size,
                         TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException {
        downloadWithoutReturn(currentUserId, userCwkids, current, size, tSysPdRecordDto, response);
    }

    @Override
    public void downloadWithSplit(String currentUserId, List<String> userCwkids, Integer current, Integer size,
                                  TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException {
        var page = tSysPdRecordService.tSysPdRecordListWithSplit(currentUserId, userCwkids, current, size, "", "", tSysPdRecordDto);
        List<TSysPdRecordExcelVo> excelVos = new ArrayList<>();
        int index = 1;
        for (TSysPdRecordVo vo : page.getContent()) {
            TSysPdRecordExcelVo excelVo = new TSysPdRecordExcelVo();
            BeanUtils.copyProperties(vo, excelVo);
            // 使用顺序序号而不是数据库ID
            excelVo.setPdRecordId(index++);
            // 转换审核状态显示
            if ("1".equals(vo.getReviewStatus())) {
                excelVo.setReviewStatus("已审核");
            } else if ("0".equals(vo.getReviewStatus())) {
                excelVo.setReviewStatus("未审核");
            }
            excelVos.add(excelVo);
        }
        ExcelUtil.writeExcel(response, excelVos, System.currentTimeMillis() + "", "sheet1", new TSysPdRecordExcelVo());
    }

    @Override
    public void downloadWithoutReturn(String currentUserId, List<String> userCwkids, Integer current, Integer size,
                                      TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException {
        var page = tSysPdRecordService.tSysPdRecordList(currentUserId, userCwkids, current, size, "", "", tSysPdRecordDto);
        List<TSysPdRecordExcelWithoutReturnVo> excelVos = new ArrayList<>();
        int index = 1;
        for (TSysPdRecordVo vo : page.getContent()) {
            TSysPdRecordExcelWithoutReturnVo excelVo = new TSysPdRecordExcelWithoutReturnVo();
            BeanUtils.copyProperties(vo, excelVo);
            // 使用顺序序号而不是数据库ID
            excelVo.setPdRecordId(index++);
            // 转换审核状态显示
            if ("1".equals(vo.getReviewStatus())) {
                excelVo.setReviewStatus("已审核");
            } else if ("0".equals(vo.getReviewStatus())) {
                excelVo.setReviewStatus("未审核");
            }
            excelVos.add(excelVo);
        }
        ExcelUtil.writeExcel(response, excelVos, System.currentTimeMillis() + "", "sheet1", new TSysPdRecordExcelWithoutReturnVo());
    }
}