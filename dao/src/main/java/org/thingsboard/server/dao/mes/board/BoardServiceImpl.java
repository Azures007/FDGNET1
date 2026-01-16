package org.thingsboard.server.dao.mes.board;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModelProperty;
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
            deviceRunBoardVo.setByDateTime(simpleDateFormat.format(dateFront));
            List<DeviceRunBoardTypeVo> deviceRunBoardTypeVos = new ArrayList<>();

            String toByDateFront = simpleDateFormat.format(dateFront) + " 00:00:00";
            String toByDateLater = simpleDateFormat.format(dateFront) + " 23:59:59";
            switch (deviceRunBoardDto.getDeviceType()) {
                case "全部": {
                    //全部
                    //内包机
                    deviceRunBoardTypeVos.add(createdInsourcingDeviceRunBoardVo(toByDateFront, toByDateLater));
                    //烤炉
                    deviceRunBoardTypeVos.add(createdOvenDeviceRunBoardVo(toByDateFront, toByDateLater));
                    //温湿度仪
                    deviceRunBoardTypeVos.add(createdTANSensorDeviceRunBoardVo(toByDateFront, toByDateLater));

                }
                break;
                case "内包机": {
                    deviceRunBoardTypeVos.add(createdInsourcingDeviceRunBoardVo(toByDateFront, toByDateLater));
                }
                break;
                case "温湿度仪": {
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
     *
     * @param byDateFront
     * @param byDateLater
     * @return
     * @throws ParseException
     */
    private DeviceRunBoardTypeVo createdOvenDeviceRunBoardVo(String byDateFront, String byDateLater) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DeviceRunBoardTypeVo deviceRunBoardVo = new DeviceRunBoardTypeVo();
        deviceRunBoardVo.setDeviceType("烤炉");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Map> deviceMaps = deviceRepository.findLikeName("Oven");
        if (deviceMaps != null && deviceMaps.size() > 0) {
            List<Device> devices = JSON.parseArray(JSON.toJSONString(deviceMaps), Device.class);
            List<OvenDeviceRunVo> ovenDeviceRunVos = new ArrayList<>();
            for (Device device : devices) {
                OvenDeviceRunVo ovenDeviceRunVo = new OvenDeviceRunVo();
                Long byDateFrontTimes = simpleDateFormat.parse(byDateFront).getTime();
                Long byDateLaterTimes = simpleDateFormat.parse(byDateLater).getTime();
                ovenDeviceRunVo.setRunDate(format.format(simpleDateFormat.parse(byDateFront)));
                ovenDeviceRunVo.setDeviceCode(device.getName());
                ovenDeviceRunVo.setDeviceName(device.getName());
                //总运行时长
                BigDecimal maxRunSeund = deviceRepository.maxQtyByMykey(device.getName(),
                        "开机时间", byDateFrontTimes, byDateLaterTimes);
                BigDecimal minRunSeund = deviceRepository.minQtyByMykey(device.getName(),
                        "开机时间", byDateFrontTimes, byDateLaterTimes);
                BigDecimal runSeund = maxRunSeund.subtract(minRunSeund);
                ovenDeviceRunVo.setRunSeund(runSeund.longValue());

                BigDecimal maxOneTempT1 = deviceRepository.maxQtyByMykey(device.getName(),
                        "一区上温度", byDateFrontTimes, byDateLaterTimes);

                BigDecimal maxOneTempT2 = deviceRepository.maxQtyByMykey(device.getName(),
                        "一区下温度", byDateFrontTimes, byDateLaterTimes);
                ovenDeviceRunVo.setMaxOneTemp(maxOneTempT1.compareTo(maxOneTempT2) == 1 ? maxOneTempT1 : maxOneTempT2);

                BigDecimal minOneTempT1 = deviceRepository.minQtyByMykey(device.getName(),
                        "一区上温度", byDateFrontTimes, byDateLaterTimes);

                BigDecimal minOneTempT2 = deviceRepository.minQtyByMykey(device.getName(),
                        "一区下温度", byDateFrontTimes, byDateLaterTimes);
                ovenDeviceRunVo.setMinOneTemp(minOneTempT1.compareTo(minOneTempT2) == 1 ? minOneTempT2 : minOneTempT1);

                BigDecimal maxTwoTempT1 = deviceRepository.maxQtyByMykey(device.getName(),
                        "二区上温度", byDateFrontTimes, byDateLaterTimes);

                BigDecimal maxTwoTempT2 = deviceRepository.maxQtyByMykey(device.getName(),
                        "二区下温度", byDateFrontTimes, byDateLaterTimes);
                ovenDeviceRunVo.setMaxTwoTemp(maxTwoTempT1.compareTo(maxTwoTempT2) == 1 ? maxTwoTempT1 : maxTwoTempT2);

                BigDecimal minTwoTempT1 = deviceRepository.minQtyByMykey(device.getName(),
                        "二区上温度", byDateFrontTimes, byDateLaterTimes);

                BigDecimal minTwoTempT2 = deviceRepository.minQtyByMykey(device.getName(),
                        "二区下温度", byDateFrontTimes, byDateLaterTimes);
                ovenDeviceRunVo.setMinTwoTemp(minTwoTempT1.compareTo(minTwoTempT2) == 1 ? minTwoTempT2 : minTwoTempT1);

                BigDecimal maxThreeTempT1 = deviceRepository.maxQtyByMykey(device.getName(),
                        "三区上温度", byDateFrontTimes, byDateLaterTimes);

                BigDecimal maxThreeTempT2 = deviceRepository.maxQtyByMykey(device.getName(),
                        "三区下温度", byDateFrontTimes, byDateLaterTimes);
                ovenDeviceRunVo.setMaxThreeTemp(maxThreeTempT1.compareTo(maxThreeTempT2) == 1 ? maxThreeTempT1 : maxThreeTempT2);

                BigDecimal minThreeTempT1 = deviceRepository.minQtyByMykey(device.getName(),
                        "三区上温度", byDateFrontTimes, byDateLaterTimes);

                BigDecimal minThreeTempT2 = deviceRepository.minQtyByMykey(device.getName(),
                        "三区下温度", byDateFrontTimes, byDateLaterTimes);
                ovenDeviceRunVo.setMinThreeTemp(minThreeTempT1.compareTo(minThreeTempT2) == 1 ? minThreeTempT2 : minThreeTempT1);

                BigDecimal maxFourTempT1 = deviceRepository.maxQtyByMykey(device.getName(),
                        "四区上温度", byDateFrontTimes, byDateLaterTimes);

                BigDecimal maxFourTempT2 = deviceRepository.maxQtyByMykey(device.getName(),
                        "四区下温度", byDateFrontTimes, byDateLaterTimes);
                ovenDeviceRunVo.setMaxFourTemp(maxFourTempT1.compareTo(maxFourTempT2) == 1 ? maxFourTempT1 : maxFourTempT2);

                BigDecimal minFourTempT1 = deviceRepository.minQtyByMykey(device.getName(),
                        "四区上温度", byDateFrontTimes, byDateLaterTimes);

                BigDecimal minFourTempT2 = deviceRepository.minQtyByMykey(device.getName(),
                        "四区下温度", byDateFrontTimes, byDateLaterTimes);
                ovenDeviceRunVo.setMinFourTemp(minFourTempT1.compareTo(minFourTempT2) == 1 ? minFourTempT2 : minFourTempT1);

                //平均温度
                BigDecimal avgTemp = new BigDecimal("0");
                BigDecimal avgOneTempT1 = deviceRepository.avgQtyByMykey(device.getName(),
                        "一区上温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal avgOneTempT2 = deviceRepository.avgQtyByMykey(device.getName(),
                        "一区下温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal avgTwoTempT1 = deviceRepository.avgQtyByMykey(device.getName(),
                        "二区上温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal avgTwoTempT2 = deviceRepository.avgQtyByMykey(device.getName(),
                        "二区下温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal avgThreeTempT1 = deviceRepository.avgQtyByMykey(device.getName(),
                        "三区上温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal avgThreeTempT2 = deviceRepository.avgQtyByMykey(device.getName(),
                        "三区下温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal avgFourTempT1 = deviceRepository.avgQtyByMykey(device.getName(),
                        "四区上温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal avgFourTempT2 = deviceRepository.avgQtyByMykey(device.getName(),
                        "四区下温度", byDateFrontTimes, byDateLaterTimes);
                avgTemp = avgTemp.add(avgOneTempT1)
                        .add(avgOneTempT2)
                        .add(avgTwoTempT1)
                        .add(avgTwoTempT2)
                        .add(avgThreeTempT1)
                        .add(avgThreeTempT2)
                        .add(avgFourTempT1)
                        .add(avgFourTempT2);
                avgTemp = avgTemp.divide(new BigDecimal("8"), 1, RoundingMode.HALF_UP);
                ovenDeviceRunVo.setAvgTemp(avgTemp);

                BigDecimal inTepmSize = new BigDecimal("10");
                String iotTemp = GlobalConstant.getCodeDscName("iot_over_temp", device.getName());
                inTepmSize = new BigDecimal(iotTemp);

                BigDecimal overSize1 = deviceRepository.countByQty(device.getName(), "一区上温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                BigDecimal overSize11 = deviceRepository.countByQty(device.getName(), "一区下温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                BigDecimal overSize2 = deviceRepository.countByQty(device.getName(), "二区上温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                BigDecimal overSize21 = deviceRepository.countByQty(device.getName(), "二区下温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                BigDecimal overSize3 = deviceRepository.countByQty(device.getName(), "三区上温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                BigDecimal overSize31 = deviceRepository.countByQty(device.getName(), "三区下温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                BigDecimal overSize4 = deviceRepository.countByQty(device.getName(), "四区上温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                BigDecimal overSize41 = deviceRepository.countByQty(device.getName(), "四区下温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                BigDecimal overSize = overSize1.add(overSize11)
                        .add(overSize2).add(overSize21).add(overSize3).add(overSize31).add(overSize4).add(overSize41);
                ovenDeviceRunVo.setOverSize(overSize.intValue());

                BigDecimal countOneTempT1 = deviceRepository.countQtyByMykey(device.getName(),
                        "一区上温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal countOneTempT2 = deviceRepository.avgQtyByMykey(device.getName(),
                        "一区下温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal countTwoTempT1 = deviceRepository.countQtyByMykey(device.getName(),
                        "二区上温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal countTwoTempT2 = deviceRepository.avgQtyByMykey(device.getName(),
                        "二区下温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal countThreeTempT1 = deviceRepository.countQtyByMykey(device.getName(),
                        "三区上温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal countThreeTempT2 = deviceRepository.avgQtyByMykey(device.getName(),
                        "三区下温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal countFourTempT1 = deviceRepository.countQtyByMykey(device.getName(),
                        "四区上温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal countFourTempT2 = deviceRepository.avgQtyByMykey(device.getName(),
                        "四区下温度", byDateFrontTimes, byDateLaterTimes);
                BigDecimal sumTempSuccess = countOneTempT1.add(countOneTempT2)
                        .add(countTwoTempT1).add(countTwoTempT2).add(countThreeTempT1).add(countThreeTempT2)
                        .add(countFourTempT1).add(countFourTempT2);
                BigDecimal tempSuccess = sumTempSuccess.subtract(overSize);
                tempSuccess = sumTempSuccess.compareTo(new BigDecimal("0")) == 0 ? new BigDecimal("0") : tempSuccess.divide(sumTempSuccess, 1, RoundingMode.HALF_UP);
                ovenDeviceRunVo.setTempSuccess(tempSuccess);

                ovenDeviceRunVo.setAvgSpeed(deviceRepository.avgQtyByMykey(device.getName(),
                        "速度", byDateFrontTimes, byDateLaterTimes).setScale(1,RoundingMode.HALF_UP));

                ovenDeviceRunVo.setAvgHotWind(deviceRepository.avgQtyByMykey(device.getName(),
                        "热风频率", byDateFrontTimes, byDateLaterTimes).setScale(1,RoundingMode.HALF_UP));

                ovenDeviceRunVo.setErrorSize(deviceRepository.sumQtyByMykey(device.getName(),
                        "故障警告", byDateFrontTimes, byDateLaterTimes).intValue());

                ovenDeviceRunVos.add(ovenDeviceRunVo);
            }
            deviceRunBoardVo.setOvenDeviceRunVos(ovenDeviceRunVos);
        } else {
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DeviceRunBoardTypeVo deviceRunBoardVo = new DeviceRunBoardTypeVo();
        deviceRunBoardVo.setDeviceType("内包机");
        List<Map> deviceMaps = deviceRepository.findLikeName("Insourcing");
        if (deviceMaps != null && deviceMaps.size() > 0) {
            List<Device> devices = JSON.parseArray(JSON.toJSONString(deviceMaps), Device.class);
            List<InsourcingDeviceRunVo> insourcingDeviceRunVos = new ArrayList<>();
            for (Device device : devices) {
                InsourcingDeviceRunVo insourcingDeviceRunVo = new InsourcingDeviceRunVo();
                Long byDateFrontTimes = simpleDateFormat.parse(byDateFront).getTime();
                Long byDateLaterTimes = simpleDateFormat.parse(byDateLater).getTime();
                insourcingDeviceRunVo.setRunDate(format.format(simpleDateFormat.parse(byDateFront)));
                insourcingDeviceRunVo.setDeviceCode(device.getName());
                insourcingDeviceRunVo.setDeviceName(device.getName());
                //总运行时长
                BigDecimal maxRunSeund = deviceRepository.maxQtyByMykey(device.getName(),
                        "开机时间", byDateFrontTimes, byDateLaterTimes);
                BigDecimal minRunSeund = deviceRepository.minQtyByMykey(device.getName(),
                        "开机时间", byDateFrontTimes, byDateLaterTimes);
                BigDecimal runSeund = maxRunSeund.subtract(minRunSeund);
                insourcingDeviceRunVo.setRunSeund(runSeund.abs().longValue());
                //最高温度
                BigDecimal maxTemp = new BigDecimal("0");
                insourcingDeviceRunVo.setMaxTemp(maxTemp);
                //最低温度
                BigDecimal minTemp = new BigDecimal("0");
                insourcingDeviceRunVo.setMinTemp(minTemp);
                //平均温度
                BigDecimal avgTemp = new BigDecimal("0");
                insourcingDeviceRunVo.setAvgTemp(avgTemp.setScale(1,RoundingMode.HALF_UP));
                //最高速度
                insourcingDeviceRunVo.setMaxSpeed(deviceRepository.maxQtyByMykey(device.getName(),
                        "速度", byDateFrontTimes, byDateLaterTimes));
                insourcingDeviceRunVo.setMinSpeed(deviceRepository.minQtyByMykey(device.getName(),
                        "速度", byDateFrontTimes, byDateLaterTimes));
                insourcingDeviceRunVo.setAvgSpeed(deviceRepository.avgQtyByMykey(device.getName(),
                        "速度", byDateFrontTimes, byDateLaterTimes).setScale(1, RoundingMode.HALF_UP));
                BigDecimal maxPieceQty = deviceRepository.maxQtyByMykey(device.getName(),
                        "包装件数", byDateFrontTimes, byDateLaterTimes);
                BigDecimal minPieceQty = deviceRepository.minQtyByMykey(device.getName(),
                        "包装件数", byDateFrontTimes, byDateLaterTimes);
                insourcingDeviceRunVo.setPieceQty(maxPieceQty.subtract(minPieceQty));
                insourcingDeviceRunVo.setOverSize(0);
                insourcingDeviceRunVo.setTempSuccess(new BigDecimal("0"));
                insourcingDeviceRunVos.add(insourcingDeviceRunVo);
            }
            deviceRunBoardVo.setInsourcingDeviceRunVoList(insourcingDeviceRunVos);
        } else {
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        deviceRunBoardVo.setDeviceType("温湿度监测");
        List<Map> deviceMaps = deviceRepository.findLikeName("TANSensor");
        if (deviceMaps != null && deviceMaps.size() > 0) {
            List<Device> devices = JSON.parseArray(JSON.toJSONString(deviceMaps), Device.class);
            List<TanSensorDeviceRunVo> tanSensorDeviceRunVos = new ArrayList<>();
            for (Device device : devices) {
                TanSensorDeviceRunVo tanSensorDeviceRunVo = new TanSensorDeviceRunVo();
                Long byDateFrontTimes = simpleDateFormat.parse(byDateFront).getTime();
                Long byDateLaterTimes = simpleDateFormat.parse(byDateLater).getTime();
                tanSensorDeviceRunVo.setRunDate(format.format(simpleDateFormat.parse(byDateFront)));
                tanSensorDeviceRunVo.setDeviceCode(device.getName());
                tanSensorDeviceRunVo.setDeviceName(device.getName());
                //总运行时长
                tanSensorDeviceRunVo.setRunSeund(86400L);
                //最高温度
                BigDecimal maxTemp = deviceRepository.maxQtyByMykey(device.getName(),
                        "温度", byDateFrontTimes, byDateLaterTimes);
                tanSensorDeviceRunVo.setMaxTemp(maxTemp);
                //最低温度
                BigDecimal minTemp = deviceRepository.minQtyByMykey(device.getName(),
                        "温度", byDateFrontTimes, byDateLaterTimes);
                tanSensorDeviceRunVo.setMinTemp(minTemp);
                //平均温度
                BigDecimal avgTemp = deviceRepository.avgQtyByMykey(device.getName(),
                        "温度", byDateFrontTimes, byDateLaterTimes);
                tanSensorDeviceRunVo.setAvgTemp(avgTemp.setScale(1, RoundingMode.HALF_UP));

                //总温度数据数量
                BigDecimal inTepmSize = new BigDecimal("10");
                String iotTemp = GlobalConstant.getCodeDscName("iot_temp", device.getName());
                inTepmSize = new BigDecimal(iotTemp);

                BigDecimal tempSuccess = deviceRepository.countByQty(device.getName(), "温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                tanSensorDeviceRunVo.setHempSize(tempSuccess);
                BigDecimal tempSize = (inTepmSize.subtract(tempSuccess)).divide(inTepmSize, 1, RoundingMode.HALF_UP);
                tanSensorDeviceRunVo.setTempSize(tempSize);

                //最高湿度
                BigDecimal maxhemp = deviceRepository.maxQtyByMykey(device.getName(),
                        "湿度", byDateFrontTimes, byDateLaterTimes);
                tanSensorDeviceRunVo.setMaxHump(maxhemp);
                //最低湿度
                BigDecimal minHemp = deviceRepository.minQtyByMykey(device.getName(),
                        "湿度", byDateFrontTimes, byDateLaterTimes);
                tanSensorDeviceRunVo.setMinHemp(minHemp);
                //平均湿度
                BigDecimal avgHemp = deviceRepository.avgQtyByMykey(device.getName(),
                        "湿度", byDateFrontTimes, byDateLaterTimes);
                tanSensorDeviceRunVo.setAvgHemp(avgHemp.setScale(1, RoundingMode.HALF_UP));

                //总湿度度数据数量
                BigDecimal inHepmSize = new BigDecimal("10");
                inHepmSize = new BigDecimal(GlobalConstant.getCodeDscName("iot_hemp", device.getName()));

                BigDecimal hempSuccess = deviceRepository.countByQty(device.getName(), "湿度", inHepmSize, byDateFrontTimes, byDateLaterTimes);
                tanSensorDeviceRunVo.setHempSuccess(hempSuccess);
                BigDecimal hempSize = (inHepmSize.subtract(hempSuccess)).divide(inHepmSize, 1, RoundingMode.HALF_UP);
                tanSensorDeviceRunVo.setHempSize(hempSize);

                tanSensorDeviceRunVos.add(tanSensorDeviceRunVo);
            }
            deviceRunBoardVo.setTanSensorDeviceRunVos(tanSensorDeviceRunVos);
        } else {
            return null;
        }
        return deviceRunBoardVo;
    }
}
