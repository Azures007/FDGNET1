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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TSysPdRecordExcelServiceImpl implements TSysPdRecordExcelService {

    @Autowired
    private TSysPdRecordService tSysPdRecordService;

    @Override
    public void download(String userId, Integer current, Integer size,
                         TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException {
        var page = tSysPdRecordService.tSysPdRecordList(userId, current, size, "", "", tSysPdRecordDto);
        List<TSysPdRecordExcelVo> excelVos = new ArrayList<>();
        for (TSysPdRecordVo vo : page.getContent()) {
            TSysPdRecordExcelVo excelVo = new TSysPdRecordExcelVo();
            BeanUtils.copyProperties(vo, excelVo);
            excelVos.add(excelVo);
        }
        ExcelUtil.writeExcel(response, excelVos, System.currentTimeMillis() + "", "sheet1", new TSysPdRecordExcelVo());
    }

    @Override
    public void downloadWithSplit(String userId, Integer current, Integer size,
                                  TSysPdRecordDto tSysPdRecordDto, HttpServletResponse response) throws IOException {
        var page = tSysPdRecordService.tSysPdRecordListWithSplit(userId, current, size, "", "", tSysPdRecordDto);
        List<TSysPdRecordExcelVo> excelVos = new ArrayList<>();
        for (TSysPdRecordVo vo : page.getContent()) {
            TSysPdRecordExcelVo excelVo = new TSysPdRecordExcelVo();
            BeanUtils.copyProperties(vo, excelVo);
            excelVos.add(excelVo);
        }
        ExcelUtil.writeExcel(response, excelVos, System.currentTimeMillis() + "", "sheet1", new TSysPdRecordExcelVo());
    }
}
