package org.thingsboard.server.dao.mes.board;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.DeviceRunBoardDto;
import org.thingsboard.server.dao.mes.vo.*;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.mes.tSysDevice.TSysDeviceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    TSysDeviceRepository tSysDeviceRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public List<BoardDevice> lineSellp(String byDate) {


        return null;
    }

    @Override
    public List<DeviceRunBoardVo> deviceRunBoard(DeviceRunBoardDto deviceRunBoardDto) throws ParseException {
        List<DeviceRunBoardVo> deviceRunBoardVos = new ArrayList<>();
        String byDateFront = deviceRunBoardDto.getByDateFront();
        String byDateLater = deviceRunBoardDto.getByDateLater();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFront = simpleDateFormat.parse(byDateFront);
        Date dateLater = simpleDateFormat.parse(byDateLater);

        do {
            DeviceRunBoardVo deviceRunBoardVo = new DeviceRunBoardVo();
            deviceRunBoardVo.setByDateTime(byDateFront);
            List<DeviceRunBoardTypeVo> deviceRunBoardTypeVos = new ArrayList<>();

            String toByDateFront = simpleDateFormat.format(dateFront) + " 00:00:00";
            String toByDateLater = simpleDateFormat.format(dateFront) + " 23:59:59";
            switch (deviceRunBoardDto.getDeviceType()) {
                case "全部": {
                    //全部
                    //内包机
                    deviceRunBoardTypeVos.add(createdInsourcingDeviceRunBoardVo(toByDateFront, toByDateLater));
                    //烤炉
//                    deviceRunBoardTypeVos.add(createdOvenDeviceRunBoardVo(toByDateFront, toByDateLater));
                    //温湿度仪
                    deviceRunBoardTypeVos.add(createdTANSensorDeviceRunBoardVo(toByDateFront, toByDateLater));

                }
                break;
                case "内包机":{
                    deviceRunBoardTypeVos.add(createdInsourcingDeviceRunBoardVo(toByDateFront, toByDateLater));
                }
                break;
                case "温湿度仪":{
                    deviceRunBoardTypeVos.add(createdTANSensorDeviceRunBoardVo(toByDateFront, toByDateLater));
                }
                break;

            }
            deviceRunBoardVo.setDeviceRunBoardTypeVoList(deviceRunBoardTypeVos);
            // 增加一天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFront);       // 设置原始日期
            calendar.add(Calendar.DATE, 1);    // 增加1天 (也可用 Calendar.DAY_OF_MONTH)
            dateFront = calendar.getTime(); // 获取新日期
            deviceRunBoardVos.add(deviceRunBoardVo);
        } while (dateFront.compareTo(dateLater) != 1);


        return deviceRunBoardVos;
    }

    /**
     * 烤箱设备运行报表
     * @param byDateFront
     * @param byDateLater
     * @return
     * @throws ParseException
     */
    private DeviceRunBoardTypeVo createdOvenDeviceRunBoardVo(String byDateFront, String byDateLater) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DeviceRunBoardTypeVo deviceRunBoardVo = new DeviceRunBoardTypeVo();
        deviceRunBoardVo.setDeviceType("烤炉");
        List<Map> deviceMaps = deviceRepository.findLikeName("Insourcing");
        if (deviceMaps !=null && deviceMaps.size() > 0) {
            List<Device> devices= JSON.parseArray(JSON.toJSONString(deviceMaps),Device.class);
//            List<OvenDeviceRunVo> ovenDeviceRunVos=new ArrayList<>();
            for (Device device : devices) {
//                OvenDeviceRunVo ovenDeviceRunVo=new OvenDeviceRunVo();
//                Long byDateFrontTimes=simpleDateFormat.parse(byDateFront).getTime();
//                Long byDateLaterTimes=simpleDateFormat.parse(byDateLater).getTime();
//                ovenDeviceRunVo.setDeviceCode(device.getName());
//                ovenDeviceRunVo.setDeviceName(device.getName());
//                //总运行时长
//                BigDecimal runSeund=deviceRepository.sumQtyByMykey(device.getName(),
//                        "开机时间",byDateFrontTimes,byDateLaterTimes);
//                ovenDeviceRunVo.setRunSeund(runSeund.longValue());
//                ovenDeviceRunVo.setMaxOneTemp(deviceRepository.maxQtyByMykey(device.getName(),
//                        "",byDateFrontTimes,byDateLaterTimes));
//
//                //最低温度
//                BigDecimal minTemp=new BigDecimal("0");
//                insourcingDeviceRunVo.setMinTemp(minTemp);
//                //平均温度
//                BigDecimal avgTemp=new BigDecimal("0");
//                insourcingDeviceRunVo.setAvgTemp(avgTemp);
//                //最高速度
//                insourcingDeviceRunVo.setMaxSpeed(deviceRepository.maxQtyByMykey(device.getName(),
//                        "速度",byDateFrontTimes,byDateLaterTimes));
//                insourcingDeviceRunVo.setMinSpeed(deviceRepository.minQtyByMykey(device.getName(),
//                        "速度",byDateFrontTimes,byDateLaterTimes));
//                insourcingDeviceRunVo.setAvgSpeed(deviceRepository.avgQtyByMykey(device.getName(),
//                        "速度",byDateFrontTimes,byDateLaterTimes));
//                insourcingDeviceRunVo.setPieceQty(deviceRepository.maxQtyByMykey(device.getName(),
//                        "包装件数",byDateFrontTimes,byDateLaterTimes));
//                insourcingDeviceRunVo.setOverSize(0);
//                insourcingDeviceRunVo.setTempSuccess(new BigDecimal("0"));
//                insourcingDeviceRunVos.add(insourcingDeviceRunVo);
            }
//            deviceRunBoardVo.setInsourcingDeviceRunVoList(insourcingDeviceRunVos);
        }else {
            return null;
        }
        return deviceRunBoardVo;
    }

    /**
     * 内包机设备运行报表
     *
     * @param byDateFront
     * @param byDateLater
     * @return
     */
    private DeviceRunBoardTypeVo createdInsourcingDeviceRunBoardVo(String byDateFront, String byDateLater) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DeviceRunBoardTypeVo deviceRunBoardVo = new DeviceRunBoardTypeVo();
        deviceRunBoardVo.setDeviceType("内包机");
        List<Map> deviceMaps = deviceRepository.findLikeName("Insourcing");
        if (deviceMaps !=null && deviceMaps.size() > 0) {
            List<Device> devices= JSON.parseArray(JSON.toJSONString(deviceMaps),Device.class);
            List<InsourcingDeviceRunVo> insourcingDeviceRunVos=new ArrayList<>();
            for (Device device : devices) {
                InsourcingDeviceRunVo insourcingDeviceRunVo=new InsourcingDeviceRunVo();
                Long byDateFrontTimes=simpleDateFormat.parse(byDateFront).getTime();
                Long byDateLaterTimes=simpleDateFormat.parse(byDateLater).getTime();
                insourcingDeviceRunVo.setDeviceCode(device.getName());
                insourcingDeviceRunVo.setDeviceName(device.getName());
                //总运行时长
                BigDecimal maxRunSeund=deviceRepository.maxQtyByMykey(device.getName(),
                        "开机时间",byDateFrontTimes,byDateLaterTimes);
                BigDecimal minRunSeund=deviceRepository.minQtyByMykey(device.getName(),
                        "开机时间",byDateFrontTimes,byDateLaterTimes);
                BigDecimal runSeund=maxRunSeund.subtract(minRunSeund);
                insourcingDeviceRunVo.setRunSeund(runSeund.abs().longValue());
                //最高温度
                BigDecimal maxTemp=new BigDecimal("0");
                insourcingDeviceRunVo.setMaxTemp(maxTemp);
                //最低温度
                BigDecimal minTemp=new BigDecimal("0");
                insourcingDeviceRunVo.setMinTemp(minTemp);
                //平均温度
                BigDecimal avgTemp=new BigDecimal("0");
                insourcingDeviceRunVo.setAvgTemp(avgTemp);
                //最高速度
                insourcingDeviceRunVo.setMaxSpeed(deviceRepository.maxQtyByMykey(device.getName(),
                        "速度",byDateFrontTimes,byDateLaterTimes));
                insourcingDeviceRunVo.setMinSpeed(deviceRepository.minQtyByMykey(device.getName(),
                        "速度",byDateFrontTimes,byDateLaterTimes));
                insourcingDeviceRunVo.setAvgSpeed(deviceRepository.avgQtyByMykey(device.getName(),
                        "速度",byDateFrontTimes,byDateLaterTimes).setScale(3,RoundingMode.HALF_UP));
                insourcingDeviceRunVo.setPieceQty(deviceRepository.maxQtyByMykey(device.getName(),
                        "包装件数",byDateFrontTimes,byDateLaterTimes));
                insourcingDeviceRunVo.setOverSize(0);
                insourcingDeviceRunVo.setTempSuccess(new BigDecimal("0"));
                insourcingDeviceRunVos.add(insourcingDeviceRunVo);
            }
            deviceRunBoardVo.setInsourcingDeviceRunVoList(insourcingDeviceRunVos);
        }else {
            return null;
        }
        return deviceRunBoardVo;
    }

    /**
     * 温湿度设备运行报表
     *
     * @param byDateFront
     * @param byDateLater
     * @return
     */
    private DeviceRunBoardTypeVo createdTANSensorDeviceRunBoardVo(String byDateFront, String byDateLater) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DeviceRunBoardTypeVo deviceRunBoardVo = new DeviceRunBoardTypeVo();
        deviceRunBoardVo.setDeviceType("温湿度监测");
        List<Map> deviceMaps = deviceRepository.findLikeName("TANSensor");
        if (deviceMaps !=null && deviceMaps.size() > 0) {
            List<Device> devices= JSON.parseArray(JSON.toJSONString(deviceMaps),Device.class);
            List<TanSensorDeviceRunVo> tanSensorDeviceRunVos=new ArrayList<>();
            for (Device device : devices) {
                TanSensorDeviceRunVo tanSensorDeviceRunVo=new TanSensorDeviceRunVo();
                Long byDateFrontTimes=simpleDateFormat.parse(byDateFront).getTime();
                Long byDateLaterTimes=simpleDateFormat.parse(byDateLater).getTime();
                tanSensorDeviceRunVo.setDeviceCode(device.getName());
                tanSensorDeviceRunVo.setDeviceName(device.getName());
                //总运行时长
                tanSensorDeviceRunVo.setRunSeund(86400L);
                //最高温度
                BigDecimal maxTemp=deviceRepository.maxQtyByMykey(device.getName(),
                        "温度",byDateFrontTimes,byDateLaterTimes);
                tanSensorDeviceRunVo.setMaxTemp(maxTemp);
                //最低温度
                BigDecimal minTemp=deviceRepository.minQtyByMykey(device.getName(),
                        "温度",byDateFrontTimes,byDateLaterTimes);
                tanSensorDeviceRunVo.setMinTemp(minTemp);
                //平均温度
                BigDecimal avgTemp=deviceRepository.avgQtyByMykey(device.getName(),
                        "温度",byDateFrontTimes,byDateLaterTimes);
                tanSensorDeviceRunVo.setAvgTemp(avgTemp.setScale(3,RoundingMode.HALF_UP));

                //总温度数据数量
                BigDecimal inTepmSize=new BigDecimal("10");
                String iotTemp = GlobalConstant.getCodeDscName("iot_temp", device.getName());
                inTepmSize= new BigDecimal(iotTemp);

                BigDecimal tempSuccess=deviceRepository.countByQty(device.getName(),"温度",inTepmSize,byDateFrontTimes,byDateLaterTimes);
                tanSensorDeviceRunVo.setHempSize(tempSuccess);
                BigDecimal tempSize=(inTepmSize.subtract(tempSuccess)).divide(inTepmSize,3, RoundingMode.HALF_UP);
                tanSensorDeviceRunVo.setTempSize(tempSize);

                //最高湿度
                BigDecimal maxhemp=deviceRepository.maxQtyByMykey(device.getName(),
                        "湿度",byDateFrontTimes,byDateLaterTimes);
                tanSensorDeviceRunVo.setMaxHump(maxhemp);
                //最低湿度
                BigDecimal minHemp=deviceRepository.minQtyByMykey(device.getName(),
                        "湿度",byDateFrontTimes,byDateLaterTimes);
                tanSensorDeviceRunVo.setMinHemp(minHemp);
                //平均湿度
                BigDecimal avgHemp=deviceRepository.avgQtyByMykey(device.getName(),
                        "湿度",byDateFrontTimes,byDateLaterTimes);
                tanSensorDeviceRunVo.setAvgHemp(avgHemp.setScale(3,RoundingMode.HALF_UP));

                //总湿度度数据数量
                BigDecimal inHepmSize=new BigDecimal("10");
                inHepmSize= new BigDecimal(GlobalConstant.getCodeDscName("iot_hemp",device.getName()));

                BigDecimal hempSuccess=deviceRepository.countByQty(device.getName(),"湿度",inHepmSize,byDateFrontTimes,byDateLaterTimes);
                tanSensorDeviceRunVo.setHempSuccess(hempSuccess);
                BigDecimal hempSize=(inHepmSize.subtract(hempSuccess)).divide(inHepmSize,3, RoundingMode.HALF_UP);
                tanSensorDeviceRunVo.setHempSize(hempSize);

                tanSensorDeviceRunVos.add(tanSensorDeviceRunVo);
            }
            deviceRunBoardVo.setTanSensorDeviceRunVos(tanSensorDeviceRunVos);
        }else {
            return null;
        }
        return deviceRunBoardVo;
    }
}
