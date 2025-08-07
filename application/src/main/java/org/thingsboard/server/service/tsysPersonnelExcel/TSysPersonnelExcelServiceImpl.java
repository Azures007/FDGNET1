package org.thingsboard.server.service.tsysPersonnelExcel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.utils.ExcelUtil;
import org.thingsboard.server.vo.PersonnelExcelVo;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TSysPersonnelExcelServiceImpl implements TSysPersonnelExcelService {

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Override
    public void upload(MultipartFile file, String name) {
        List<Object> personnelExcelVos = ExcelUtil.readExcel(file,new PersonnelExcelVo());
        List<TSysPersonnelInfo> tSysPersonnelInfos=new ArrayList<>();
        for (Object personnelExcelVo : personnelExcelVos) {
            PersonnelExcelVo vo= (PersonnelExcelVo) personnelExcelVo;
            TSysPersonnelInfo tSysPersonnelInfo=new TSysPersonnelInfo();
            BeanUtils.copyProperties(vo,tSysPersonnelInfo);
            tSysPersonnelInfo.setEnabledSt("1");
            tSysPersonnelInfos.add(tSysPersonnelInfo);
        }
        tSysPersonnelInfoRepository.saveAll(tSysPersonnelInfos);
        tSysPersonnelInfoRepository.flush();
    }

    @Override
    public void downTemplate(HttpServletResponse response) {
        List<PersonnelExcelVo> excelVos=new ArrayList<>();
        ExcelUtil.writeExcel(response,excelVos,"人员导入模板","sheet1",new PersonnelExcelVo());
    }
}
