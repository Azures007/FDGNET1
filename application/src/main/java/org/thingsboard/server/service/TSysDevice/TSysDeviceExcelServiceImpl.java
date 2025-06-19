package org.thingsboard.server.service.TSysDevice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.TsysDevice;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.TSysDevice.TSysDeviceService;
import org.thingsboard.server.dao.dto.TSysDeviceDto;
import org.thingsboard.server.dao.sql.tSysDevice.TSysDeviceRepository;
import org.thingsboard.server.utils.ExcelUtil;
import org.thingsboard.server.vo.DeviceExcelVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/26 16:16
 * @Description:
 */
@Service
@Slf4j
public class TSysDeviceExcelServiceImpl implements TSysDeviceExcelService {
    @Autowired
    TSysDeviceService deviceService;
    @Autowired
    TSysDeviceRepository tSysDeviceRepository;

    @Override
    public void download(Integer current, Integer size, TSysDeviceDto deviceDto, HttpServletResponse response) throws IOException {
        Page<TsysDevice> devicePage= deviceService.tSysDeviceList(current,size,deviceDto,-1);
        List<DeviceExcelVo> excelVos=new ArrayList<>();
        for (TsysDevice device : devicePage.getContent()) {
            DeviceExcelVo vo=new DeviceExcelVo();
            BeanUtils.copyProperties(device,vo);
                if("1".equals(device.getEnabled())){
                    vo.setEnabled("禁用");
                }else {
                    vo.setEnabled("启用");
                }
            excelVos.add(vo);
        }
        ExcelUtil.writeExcel(response,excelVos,System.currentTimeMillis()+"","sheet1",new DeviceExcelVo());
    }

    @Override
    public ResponseResult upload(MultipartFile file, String name) {
        List<Object> deviceExcelVos=ExcelUtil.readExcel(file,new DeviceExcelVo());
        List<TsysDevice> devices=new ArrayList<>();
        List<String> strings=new ArrayList<>();
        for(int i=0;i<deviceExcelVos.size();i++){
            DeviceExcelVo vo= (DeviceExcelVo) deviceExcelVos.get(i);
            String deviceNumber=vo.getDeviceNumber();
            for (int j=0;j<deviceExcelVos.size();j++){
                if(i!=j){
                    DeviceExcelVo vo1= (DeviceExcelVo) deviceExcelVos.get(j);
                    if(deviceNumber.equals(vo1.getDeviceNumber())){
                        return ResultUtil.error("设备编码重复，请检查后上传");
                    }
                }
            }
            List<TsysDevice> deviceList=tSysDeviceRepository.findByDeviceNumber(vo.getDeviceNumber());
            for (TsysDevice device : deviceList) {
                if(device.getDeviceNumber()!=null&&device.getDeviceNumber().equals(vo.getDeviceNumber())){
                    return ResultUtil.error("系统存在相同的设备编码，请检查后上传");
                }
            }
            TsysDevice device=new TsysDevice();
            BeanUtils.copyProperties(vo,device);
            device.setCreatedUser(name);
            device.setCreatedTime(new Date());
            if("禁用".equals(vo.getEnabled())){
                device.setEnabled("1");
            }else {
                device.setEnabled("0");
            }
            devices.add(device);
        }
        tSysDeviceRepository.saveAll(devices);
        tSysDeviceRepository.flush();
        return ResultUtil.success();
    }

    @Override
    public void downTemplate(HttpServletResponse response) {
        List<DeviceExcelVo> excelVos=new ArrayList<>();
        ExcelUtil.writeExcel(response,excelVos,System.currentTimeMillis()+"","sheet1",new DeviceExcelVo());
    }
}