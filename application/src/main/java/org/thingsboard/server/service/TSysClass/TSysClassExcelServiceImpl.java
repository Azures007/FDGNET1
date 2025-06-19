package org.thingsboard.server.service.TSysClass;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysClassDto;
import org.thingsboard.server.dao.sql.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.sql.tSysPersonnelInfo.ClassPersonnelRepository;
import org.thingsboard.server.dao.tSysClass.TSysClassService;
import org.thingsboard.server.dao.vo.ClassGroupLeaderExpVo;
import org.thingsboard.server.dao.vo.ClassPersonnelExpVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.utils.ExcelUtil;
import org.thingsboard.server.vo.ClassGroupLeaderExcelVo;
import org.thingsboard.server.vo.ClassPersonnelExcelVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: hhh
 * @Date: 2022/7/12 16:16
 * @Description:
 */
@Service
@Slf4j
public class TSysClassExcelServiceImpl implements TSysClassExcelService {

    @Autowired
    TSysClassService tSysClassService;

    @Autowired
    TSysClassRepository tSysClassRepository;

    @Autowired
    ClassPersonnelRepository classPersonnelRepository;

    @Override
    public void download(Integer current, Integer size, TSysClassDto classDto, HttpServletResponse response) throws IOException {
        //获取组长
        PageVo<ClassGroupLeaderExpVo> classPage = tSysClassService.tSysClassListExp(current, size, classDto);
        List<ClassGroupLeaderExcelVo> excelVos = new ArrayList<>();
        for (var classExportVo : classPage.getList()) {
            ClassGroupLeaderExcelVo vo = new ClassGroupLeaderExcelVo();
            BeanUtils.copyProperties(classExportVo, vo);
            vo.setScheduling(GlobalConstant.getCodeDscName("SCHEDULING0000", vo.getScheduling()));
            vo.setGroupLeaderStation(GlobalConstant.getCodeDscName("JOB0000", vo.getGroupLeaderStation()));
            excelVos.add(vo);
        }
        //获取组员
        PageVo<ClassPersonnelExpVo> classPage2 = tSysClassService.tSysClassListExp2(current, size, classDto);
        List<ClassPersonnelExcelVo> excelVos2 = new ArrayList<>();
        for (ClassPersonnelExpVo classExportVo2 : classPage2.getList()) {
            ClassPersonnelExcelVo vo = new ClassPersonnelExcelVo();
            BeanUtils.copyProperties(classExportVo2, vo);
            vo.setScheduling(GlobalConstant.getCodeDscName("SCHEDULING0000", vo.getScheduling()));
            vo.setPersonnelStation(GlobalConstant.getCodeDscName("JOB0000", vo.getPersonnelStation()));
            excelVos2.add(vo);
        }
        ExcelUtil.writeExcel(response
                ,excelVos,
                excelVos2,
                System.currentTimeMillis()+"",
                "sheet1",
                "sheet2",
                new ClassGroupLeaderExcelVo(),
                new ClassPersonnelExcelVo());
    }
}
