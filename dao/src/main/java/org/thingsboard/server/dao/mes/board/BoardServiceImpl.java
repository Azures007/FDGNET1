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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    TSysDeviceRepository tSysDeviceRepository;

    @Autowired
    DeviceRepository deviceRepository;



    @Override
    public List<BoardDataDevice> lineSellp(String deviceCode, String key, String type) {
        // 1. 指定时区
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        // 2. 获取当前时间的Instant（UTC时间，可直接转毫秒戳）
        Instant nowInstant = Instant.now();
        long currentTimestamp = nowInstant.toEpochMilli();

        // 3. 最近一小时开始：当前时间减1小时（3600*1000毫秒）
        long lastHourStartTimestamp = nowInstant.minus(1, ChronoUnit.HOURS).toEpochMilli();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        // 4. 最近一小时结束：当前时间（即该时间段的结束）
        long lastHourEndTimestamp = currentTimestamp;
        List<Map> lineSellpMaps;
        if (type.equals("long")) {
            lineSellpMaps = deviceRepository.lineSellp(deviceCode, lastHourStartTimestamp, lastHourEndTimestamp, key);
        } else {
            lineSellpMaps = deviceRepository.lineSellpByDb(deviceCode, lastHourStartTimestamp, lastHourEndTimestamp, key);

        }
        List<BoardDataDevice> boardDataDevices = JSON.parseArray(JSON.toJSONString(lineSellpMaps), BoardDataDevice.class);
        BoardDataDevice boardDataDevice=BoardDataDevice.builder()
                .byQty(boardDataDevices.get(boardDataDevices.size()-1).getByQty())
                .byTs(currentTimestamp)
                .byDate(simpleDateFormat.format(new Date()))
                .build();

        boardDataDevices.add(boardDataDevice);
        return boardDataDevices;
    }

    @Override
    public List<DeviceRunBoardTypeVo> deviceRunBoard(DeviceRunBoardDto deviceRunBoardDto) throws ParseException {
        String byDateFront = deviceRunBoardDto.getByDateFront();
        String byDateLater = deviceRunBoardDto.getByDateLater();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFront = simpleDateFormat.parse(byDateFront);
        Date dateLater = simpleDateFormat.parse(byDateLater);

        List<DeviceRunBoardTypeVo> deviceRunBoardTypeVos = new ArrayList<>();

        String toByDateFront = simpleDateFormat.format(dateFront) + " 00:00:00";
        String toByDateLater = simpleDateFormat.format(dateLater) + " 23:59:59";
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
            case "烤炉": {
                deviceRunBoardTypeVos.add(createdOvenDeviceRunBoardVo(toByDateFront, toByDateLater));
            }
            break;
            case "温湿度仪": {
                deviceRunBoardTypeVos.add(createdTANSensorDeviceRunBoardVo(toByDateFront, toByDateLater));
            }
            break;

        }
        return deviceRunBoardTypeVos;
    }

    @Override
    public List<LineClVo> lineCl(List<String> deviceCodes, String key) {
        // 1. 指定时区
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        // 2. 获取当前时间的Instant（UTC时间，可直接转毫秒戳）
        Instant nowInstant = Instant.now();
        long currentTimestamp = nowInstant.toEpochMilli();

        // 3. 最近一小时开始：当前时间减1小时（3600*1000毫秒）
        long lastHourStartTimestamp = nowInstant.minus(1, ChronoUnit.HOURS).toEpochMilli();

        // 4. 最近一小时结束：当前时间（即该时间段的结束）
        long lastHourEndTimestamp = currentTimestamp;
        List<LineClVo> lineClVos = new ArrayList<>();
        LineClVo lineClVo;
        for (String deviceCode : deviceCodes) {
            lineClVo = LineClVo.builder()
                    .deviceCode(deviceCode)
                    .build();
            BigDecimal maxQty = deviceRepository.maxQtyByMykey(deviceCode, key, lastHourStartTimestamp, lastHourEndTimestamp);
            BigDecimal minQty = deviceRepository.minQtyByMykey(deviceCode, key, lastHourStartTimestamp, lastHourEndTimestamp);
            lineClVo.setByQty(maxQty.subtract(minQty));
            lineClVos.add(lineClVo);
        }
        return lineClVos;
    }

    @Override
    public ListYjVo listYj() {


        return null;
    }

    @Override
    public List<ListDeviceIotVo> listDeviceIot(String deviceType) {
        ZoneId zone = ZoneId.of("Asia/Shanghai");
        // 一行获取0点毫秒戳
        long start = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli();
        // 一行获取23:59:59.999毫秒戳
        long end = LocalDate.now(zone).atTime(23,59,59,999_000_000).atZone(zone).toInstant().toEpochMilli();
        List<ListDeviceIotVo> listDeviceIotVos = new ArrayList();
        ListDeviceIotVo listDeviceIotVo;
        List<Map> maps = deviceRepository.listDeviceIot(deviceType);
        if (maps != null && maps.size() > 0) {
            for (Map map : maps) {
                //设备状态
                BigDecimal deviceStatus = deviceRepository.listDeviceKvLatest(map.get("name").toString(), "开机状态");
                //热风效率
                BigDecimal deviceHz = deviceRepository.listDeviceKvLatest(map.get("name").toString(), "热风频率");
                //速度
                BigDecimal deviceSd = deviceRepository.listDeviceKvLatest(map.get("name").toString(), "速度");
                listDeviceIotVo = ListDeviceIotVo.builder()
                        .deviceCode(map.get("name").toString())
                        .deviceName(map.get("label").toString())
                        .deviceFactory("清蒙工厂")
                        .deviceHz(deviceHz)
                        .deviceStatus(deviceStatus)
                        .deviceSd(deviceSd)
                        .maxOneTemp(deviceRepository.listDeviceKvLatest(map.get("name").toString(), "一区上温度"))
                        .minOneTemp(deviceRepository.listDeviceKvLatest(map.get("name").toString(), "一区下温度"))
                        .maxTwoTemp(deviceRepository.listDeviceKvLatest(map.get("name").toString(), "二区上温度"))
                        .minTwoTemp(deviceRepository.listDeviceKvLatest(map.get("name").toString(), "二区下温度"))
                        .maxThreeTemp(deviceRepository.listDeviceKvLatest(map.get("name").toString(), "三区上温度"))
                        .minThreeTemp(deviceRepository.listDeviceKvLatest(map.get("name").toString(), "三区下温度"))
                        .maxFourTemp(deviceRepository.listDeviceKvLatest(map.get("name").toString(), "四区上温度"))
                        .minFourTemp(deviceRepository.listDeviceKvLatest(map.get("name").toString(), "四区下温度"))
                        .overOneUpMaxTemp(GlobalConstant.getCodeDscName("iot_over_error", "一区上温度最大值"))
                        .overOneUpMinTemp(GlobalConstant.getCodeDscName("iot_over_error", "一区上温度最小值"))
                        .overOneDownMaxTemp(GlobalConstant.getCodeDscName("iot_over_error", "一区下温度最大值"))
                        .overOneDownMinTemp(GlobalConstant.getCodeDscName("iot_over_error", "一区下温度最小值"))
                        .overTwoUpMaxTemp(GlobalConstant.getCodeDscName("iot_over_error", "二区上温度最大值"))
                        .overTwoUpMinTemp(GlobalConstant.getCodeDscName("iot_over_error", "二区上温度最小值"))
                        .overTwoDownMaxTemp(GlobalConstant.getCodeDscName("iot_over_error", "二区下温度最大值"))
                        .overTwoDownMinTemp(GlobalConstant.getCodeDscName("iot_over_error", "二区下温度最小值"))
                        .overThreeUpMaxTemp(GlobalConstant.getCodeDscName("iot_over_error", "三区上温度最大值"))
                        .overThreeUpMinTemp(GlobalConstant.getCodeDscName("iot_over_error", "三区上温度最小值"))
                        .overThreeDownMaxTemp(GlobalConstant.getCodeDscName("iot_over_error", "三区下温度最大值"))
                        .overThreeDownMinTemp(GlobalConstant.getCodeDscName("iot_over_error", "三区下温度最小值"))
                        .overFourUpMaxTemp(GlobalConstant.getCodeDscName("iot_over_error", "四区上温度最大值"))
                        .overFourUpMinTemp(GlobalConstant.getCodeDscName("iot_over_error", "四区上温度最小值"))
                        .overFourDownMaxTemp(GlobalConstant.getCodeDscName("iot_over_error", "四区下温度最大值"))
                        .overFourDownMinTemp(GlobalConstant.getCodeDscName("iot_over_error", "四区下温度最小值"))
                        .build();
                listDeviceIotVos.add(listDeviceIotVo);
            }
        }

        return listDeviceIotVos;
    }

    @Override
    public List<ListDeviceTempDatsVo> listDeviceTempDats(String deviceCode,String type) {
        List<ListDeviceTempDatsVo> listDeviceTempDatsVos=new ArrayList<>();
        List<BoardDataDevice> boardDataDevices = this.lineSellp(deviceCode, type + "上温度", "dbl");
        for (BoardDataDevice boardDataDevice : boardDataDevices) {
//            GlobalConstant.getCodeDscName("",)
            ListDeviceTempDatsVo listDeviceTempDatsVo=ListDeviceTempDatsVo.builder()
                    .byDate(boardDataDevice.getByDate())
                    .byTs(boardDataDevice.getByTs())
                    .byMaxQty(boardDataDevice.getByQty())
                    .byMinQty(deviceRepository.getKvByTSAndKey(deviceCode,boardDataDevice.getByTs(),type + "下温度").toString())
                    .build();
            listDeviceTempDatsVos.add(listDeviceTempDatsVo);
        }
        return listDeviceTempDatsVos;
    }

    @Override
    public List<BoardDataDevice> getErrorDatas(String deviceCode) {
        ZoneId zone = ZoneId.of("Asia/Shanghai");
        // 一行获取0点毫秒戳
        long start = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli();
        // 一行获取23:59:59.999毫秒戳
        long end = LocalDate.now(zone).atTime(23,59,59,999_000_000).atZone(zone).toInstant().toEpochMilli();
        String overOneUpMaxTemp = GlobalConstant.getCodeDscName("iot_over_error", "一区上温度最大值");
        String overOneDownMaxTemp = GlobalConstant.getCodeDscName("iot_over_error", "一区下温度最大值");
        String overTwoUpMaxTemp = GlobalConstant.getCodeDscName("iot_over_error", "二区上温度最大值");
        String overTwoDownMaxTemp = GlobalConstant.getCodeDscName("iot_over_error", "二区下温度最大值");
        String overThreeUpMaxTemp = GlobalConstant.getCodeDscName("iot_over_error", "三区上温度最大值");
        String overThreeDownMaxTemp = GlobalConstant.getCodeDscName("iot_over_error", "三区下温度最大值");
        String overFourUpMaxTemp = GlobalConstant.getCodeDscName("iot_over_error", "四区上温度最大值");
        String overFourDownMaxTemp = GlobalConstant.getCodeDscName("iot_over_error", "四区下温度最大值");
        List<Map> errMaps=deviceRepository.getErrorDatas(deviceCode,start,end,
                new BigDecimal(overOneUpMaxTemp),
                new BigDecimal(overOneDownMaxTemp),
                new BigDecimal(overTwoUpMaxTemp),
                new BigDecimal(overTwoDownMaxTemp),
                new BigDecimal(overThreeUpMaxTemp),
                new BigDecimal(overThreeDownMaxTemp),
                new BigDecimal(overFourUpMaxTemp),
                new BigDecimal(overFourDownMaxTemp));
        List<BoardDataDevice> boardDataDevices=JSON.parseArray(JSON.toJSONString(errMaps),BoardDataDevice.class);
        return boardDataDevices;
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFront = format.parse(byDateFront);
        Date dateLater = format.parse(byDateLater);
        DeviceRunBoardTypeVo deviceRunBoardVo = new DeviceRunBoardTypeVo();
        deviceRunBoardVo.setDeviceType("烤炉");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<OvenDeviceRunVo> ovenDeviceRunVos = new ArrayList<>();
        List<Map> deviceMaps = deviceRepository.findLikeName("Oven");
        do {
            String toByDateFront = format.format(dateFront) + " 00:00:00";
            String toByDateLater = format.format(dateFront) + " 23:59:59";
            if (deviceMaps != null && deviceMaps.size() > 0) {
                List<Device> devices = JSON.parseArray(JSON.toJSONString(deviceMaps), Device.class);
                for (Device device : devices) {
                    OvenDeviceRunVo ovenDeviceRunVo = new OvenDeviceRunVo();
                    Long byDateFrontTimes = simpleDateFormat.parse(toByDateFront).getTime();
                    Long byDateLaterTimes = simpleDateFormat.parse(toByDateLater).getTime();
                    ovenDeviceRunVo.setRunDate(format.format(simpleDateFormat.parse(toByDateFront)));
                    ovenDeviceRunVo.setDeviceCode(device.getName());
                    ovenDeviceRunVo.setDeviceName(device.getName());
                    //总运行时长
                    BigDecimal maxRunSeund = deviceRepository.maxQtyByMykey(device.getName(),
                            "开机时间", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal minRunSeund = deviceRepository.minQtyByMykey(device.getName(),
                            "开机时间", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal runSeund = maxRunSeund.subtract(minRunSeund);
                    ovenDeviceRunVo.setRunSeund(runSeund);

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
                    if (iotTemp != null) {
                        inTepmSize = new BigDecimal(iotTemp);
                    }

                    BigDecimal overSize1 = deviceRepository.countByQty(device.getName(), "一区上温度", new BigDecimal(GlobalConstant.getCodeDscName("iot_over_error","一区上温度最大值")), byDateFrontTimes, byDateLaterTimes);
                    BigDecimal overSize2 = deviceRepository.countByQty(device.getName(), "二区上温度", new BigDecimal(GlobalConstant.getCodeDscName("iot_over_error","二区上温度最大值")), byDateFrontTimes, byDateLaterTimes);
                    BigDecimal overSize3 = deviceRepository.countByQty(device.getName(), "三区上温度", new BigDecimal(GlobalConstant.getCodeDscName("iot_over_error","三区上温度最大值")), byDateFrontTimes, byDateLaterTimes);
                    BigDecimal overSize4 = deviceRepository.countByQty(device.getName(), "四区上温度", new BigDecimal(GlobalConstant.getCodeDscName("iot_over_error","四区上温度最大值")), byDateFrontTimes, byDateLaterTimes);
                    BigDecimal overSize11 = deviceRepository.countByQty(device.getName(), "一区上温度", new BigDecimal(GlobalConstant.getCodeDscName("iot_over_error","一区下温度最大值")), byDateFrontTimes, byDateLaterTimes);
                    BigDecimal overSize21 = deviceRepository.countByQty(device.getName(), "二区上温度", new BigDecimal(GlobalConstant.getCodeDscName("iot_over_error","二区下温度最大值")), byDateFrontTimes, byDateLaterTimes);
                    BigDecimal overSize31 = deviceRepository.countByQty(device.getName(), "三区上温度", new BigDecimal(GlobalConstant.getCodeDscName("iot_over_error","三区下温度最大值")), byDateFrontTimes, byDateLaterTimes);
                    BigDecimal overSize41 = deviceRepository.countByQty(device.getName(), "四区上温度", new BigDecimal(GlobalConstant.getCodeDscName("iot_over_error","四区下温度最大值")), byDateFrontTimes, byDateLaterTimes);
                    BigDecimal overSize = overSize1
                            .add(overSize2).add(overSize3).add(overSize4).add(overSize21).add(overSize31).add(overSize41).add(overSize11);
                    ovenDeviceRunVo.setOverSize(overSize.intValue());

                    //达标率
                    BigDecimal countOneTempT1 = deviceRepository.countQtyByMykey(device.getName(),
                            "一区上温度", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal countTwoTempT1 = deviceRepository.countQtyByMykey(device.getName(),
                            "二区上温度", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal countThreeTempT1 = deviceRepository.countQtyByMykey(device.getName(),
                            "三区上温度", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal countFourTempT1 = deviceRepository.countQtyByMykey(device.getName(),
                            "四区上温度", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal countOneTempT11 = deviceRepository.countQtyByMykey(device.getName(),
                            "一区下温度", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal countTwoTempT11 = deviceRepository.countQtyByMykey(device.getName(),
                            "二区下温度", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal countThreeTempT11 = deviceRepository.countQtyByMykey(device.getName(),
                            "三区下温度", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal countFourTempT11 = deviceRepository.countQtyByMykey(device.getName(),
                            "四区下温度", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal sumTempSuccess = countOneTempT1
                            .add(countTwoTempT1).add(countThreeTempT1)
                            .add(countFourTempT1).add(countTwoTempT11).add(countThreeTempT11)
                            .add(countFourTempT11).add(countOneTempT11);
                    BigDecimal tempSuccess = sumTempSuccess.subtract(overSize);
                    tempSuccess = overSize.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("0") : sumTempSuccess.compareTo(new BigDecimal("0")) == 0 ? new BigDecimal("0") : tempSuccess.divide(sumTempSuccess, 4, RoundingMode.HALF_UP);
                    ovenDeviceRunVo.setTempSuccess(tempSuccess.multiply(new BigDecimal("100")));

                    ovenDeviceRunVo.setAvgSpeed(deviceRepository.avgQtyByMykey(device.getName(),
                            "速度", byDateFrontTimes, byDateLaterTimes).setScale(1, RoundingMode.HALF_UP));

                    ovenDeviceRunVo.setAvgHotWind(deviceRepository.avgQtyByMykey(device.getName(),
                            "热风频率", byDateFrontTimes, byDateLaterTimes).setScale(1, RoundingMode.HALF_UP));

                    BigDecimal gz = deviceRepository.countByQty(device.getName(),
                            "故障警告",BigDecimal.ZERO, byDateFrontTimes, byDateLaterTimes);
                    BigDecimal aq = deviceRepository.countByQty(device.getName(),
                            "安全报警",BigDecimal.ZERO, byDateFrontTimes, byDateLaterTimes);
                    BigDecimal cw = deviceRepository.countByQty(device.getName(),
                            "超温报警",BigDecimal.ZERO, byDateFrontTimes, byDateLaterTimes);
                    BigDecimal dj = deviceRepository.countByQty(device.getName(),
                            "电机报警",BigDecimal.ZERO, byDateFrontTimes, byDateLaterTimes);
                    BigDecimal xt = deviceRepository.countByQty(device.getName(),
                            "系统报警",BigDecimal.ZERO, byDateFrontTimes, byDateLaterTimes);
                    ovenDeviceRunVo.setErrorSize(gz.add(aq).add(cw).add(dj).add(xt).intValue());

                    ovenDeviceRunVos.add(ovenDeviceRunVo);
                }
            } else {
                return null;
            }
            // 增加一天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFront);       // 设置原始日期
            calendar.add(Calendar.DATE, 1);    // 增加1天 (也可用 Calendar.DAY_OF_MONTH)
            dateFront = calendar.getTime(); // 获取新日期
        } while (dateFront.compareTo(dateLater) != 1);
        deviceRunBoardVo.setOvenDeviceRunVos(ovenDeviceRunVos);
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFront = format.parse(byDateFront);
        Date dateLater = format.parse(byDateLater);
        DeviceRunBoardTypeVo deviceRunBoardVo = new DeviceRunBoardTypeVo();
        deviceRunBoardVo.setDeviceType("内包机");
        List<InsourcingDeviceRunVo> insourcingDeviceRunVos = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map> deviceMaps = deviceRepository.findLikeName("Insourcing");

        do {
            String toByDateFront = format.format(dateFront) + " 00:00:00";
            String toByDateLater = format.format(dateFront) + " 23:59:59";

            if (deviceMaps != null && deviceMaps.size() > 0) {
                List<Device> devices = JSON.parseArray(JSON.toJSONString(deviceMaps), Device.class);
                for (Device device : devices) {
                    InsourcingDeviceRunVo insourcingDeviceRunVo = new InsourcingDeviceRunVo();
                    Long byDateFrontTimes = simpleDateFormat.parse(toByDateFront).getTime();
                    Long byDateLaterTimes = simpleDateFormat.parse(toByDateLater).getTime();
                    insourcingDeviceRunVo.setRunDate(format.format(simpleDateFormat.parse(toByDateFront)));
                    insourcingDeviceRunVo.setDeviceCode(device.getName());
                    insourcingDeviceRunVo.setDeviceName(device.getName());
                    //总运行时长
                    BigDecimal maxRunSeund = deviceRepository.maxQtyByMykey(device.getName(),
                            "开机时间", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal minRunSeund = deviceRepository.minQtyByMykey(device.getName(),
                            "开机时间", byDateFrontTimes, byDateLaterTimes);
                    BigDecimal runSeund = maxRunSeund.subtract(minRunSeund);
                    insourcingDeviceRunVo.setRunSeund(runSeund.abs());
                    //最高温度
                    BigDecimal maxTemp = new BigDecimal("0");
                    insourcingDeviceRunVo.setMaxTemp(maxTemp);
                    //最低温度
                    BigDecimal minTemp = new BigDecimal("0");
                    insourcingDeviceRunVo.setMinTemp(minTemp);
                    //平均温度
                    BigDecimal avgTemp = new BigDecimal("0");
                    insourcingDeviceRunVo.setAvgTemp(avgTemp.setScale(1, RoundingMode.HALF_UP));
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
            } else {
                return null;
            }
            // 增加一天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFront);       // 设置原始日期
            calendar.add(Calendar.DATE, 1);    // 增加1天 (也可用 Calendar.DAY_OF_MONTH)
            dateFront = calendar.getTime(); // 获取新日期
        } while (dateFront.compareTo(dateLater) != 1);
        deviceRunBoardVo.setInsourcingDeviceRunVoList(insourcingDeviceRunVos);
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFront = format.parse(byDateFront);
        Date dateLater = format.parse(byDateLater);
        DeviceRunBoardTypeVo deviceRunBoardVo = new DeviceRunBoardTypeVo();
        deviceRunBoardVo.setDeviceType("温湿度监测");
        List<TanSensorDeviceRunVo> tanSensorDeviceRunVos = new ArrayList<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map> deviceMaps = deviceRepository.findLikeName("TANSensor");

        do {
            String toByDateFront = format.format(dateFront) + " 00:00:00";
            String toByDateLater = format.format(dateFront) + " 23:59:59";

            if (deviceMaps != null && deviceMaps.size() > 0) {
                List<Device> devices = JSON.parseArray(JSON.toJSONString(deviceMaps), Device.class);
                for (Device device : devices) {
                    TanSensorDeviceRunVo tanSensorDeviceRunVo = new TanSensorDeviceRunVo();
                    Long byDateFrontTimes = simpleDateFormat.parse(toByDateFront).getTime();
                    Long byDateLaterTimes = simpleDateFormat.parse(toByDateLater).getTime();
                    tanSensorDeviceRunVo.setRunDate(format.format(simpleDateFormat.parse(toByDateFront)));
                    tanSensorDeviceRunVo.setDeviceCode(device.getName());
                    tanSensorDeviceRunVo.setDeviceName(device.getName());
                    //总运行时长

                    // ========== 核心判断逻辑：按日期计算运行时长 ==========
                    BigDecimal runSecond;
                    // 1. 解析toByDateLater为Date对象，提取其日期部分
                    Date targetDate = simpleDateFormat.parse(toByDateFront);
                    LocalDate targetLocalDate = targetDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    // 2. 获取当前系统日期（当天）
                    LocalDate currentLocalDate = LocalDate.now();

                    // 3. 判断目标日期是否是当天
                    if (targetLocalDate.equals(currentLocalDate)) {
                        // 当天：计算当前时间 - 当天零点的秒数
                        Calendar calendar = Calendar.getInstance();
                        // 重置为当天零点（00:00:00）
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        long zeroTime = calendar.getTimeInMillis(); // 当天零点的毫秒数
                        long currentTime = System.currentTimeMillis(); // 当前时间的毫秒数
                        long secondDiff = (currentTime - zeroTime) / 1000; // 转成秒数（毫秒/1000）
                        runSecond = new BigDecimal(secondDiff);
                    } else {
                        // 非当天：固定为86400秒（一天的总秒数）
                        runSecond = new BigDecimal("86400");
                    }

                    tanSensorDeviceRunVo.setRunSeund(runSecond);
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

                    //总温度数量
                    BigDecimal countTepmSize = deviceRepository.countQtyByMykey(device.getName(),
                            "湿度", byDateFrontTimes, byDateLaterTimes);

                    BigDecimal inTepmSize = new BigDecimal("20");
                    String iotTemp = GlobalConstant.getCodeDscName("iot_temp", device.getName());
                    if (iotTemp != null) {
                        inTepmSize = new BigDecimal(iotTemp);
                    }

                    BigDecimal tempSize = deviceRepository.countByQty(device.getName(), "温度", inTepmSize, byDateFrontTimes, byDateLaterTimes);
                    tanSensorDeviceRunVo.setTempSize(tempSize);

                    BigDecimal tempSuccess = countTepmSize.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("0")
                            : (countTepmSize.subtract(tempSize)).divide(countTepmSize, 1, RoundingMode.HALF_UP);
                    tanSensorDeviceRunVo.setTempSuccess(tempSuccess.multiply(new BigDecimal("100")));

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
                    tanSensorDeviceRunVo.setAvgHemp(avgHemp.setScale(4, RoundingMode.HALF_UP));

                    //字典湿度数量
                    BigDecimal inHepmSize = new BigDecimal("10");
                    String iotHemp = GlobalConstant.getCodeDscName("iot_hemp", device.getName());
                    if (iotHemp != null) {
                        inHepmSize = new BigDecimal(iotHemp);
                    }


                    //总湿度数量
                    BigDecimal countHepmSize = deviceRepository.countQtyByMykey(device.getName(),
                            "湿度", byDateFrontTimes, byDateLaterTimes);

                    BigDecimal hempSize = deviceRepository.countByQty(device.getName(), "湿度", inHepmSize, byDateFrontTimes, byDateLaterTimes);
                    tanSensorDeviceRunVo.setHempSize(hempSize);
                    BigDecimal hempSuccess = (countHepmSize.subtract(hempSize));
                    hempSuccess = hempSize.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("0") : hempSuccess.divide(countHepmSize, 4, RoundingMode.HALF_UP);
                    tanSensorDeviceRunVo.setHempSuccess(hempSuccess.multiply(new BigDecimal("100")));

                    tanSensorDeviceRunVos.add(tanSensorDeviceRunVo);
                }
            } else {
                return null;
            }
            // 增加一天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFront);       // 设置原始日期
            calendar.add(Calendar.DATE, 1);    // 增加1天 (也可用 Calendar.DAY_OF_MONTH)
            dateFront = calendar.getTime(); // 获取新日期
        } while (dateFront.compareTo(dateLater) != 1);
        deviceRunBoardVo.setTanSensorDeviceRunVos(tanSensorDeviceRunVos);
        return deviceRunBoardVo;
    }
}
