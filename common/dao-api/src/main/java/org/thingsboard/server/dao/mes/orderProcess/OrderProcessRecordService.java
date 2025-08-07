package org.thingsboard.server.dao.mes.orderProcess;

import org.thingsboard.server.common.data.mes.mid.MidMaterial;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.mes.dto.*;
import org.thingsboard.server.dao.mes.vo.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

/**
 * @Auther: l
 * @Date: 2022/5/10 10:10
 * @Description:
 */
public interface OrderProcessRecordService {
//    List<OrderPPbomResult> getOrderPpbom(Integer orderId, Integer processId, String recordType);

    List<PpbomGroupVo> getOrderPpbom(OrderPPbomSearchDto searchDto);

    List<OrderPPbomResult> getOrderPpbomLsm(OrderPPbomSearchDto searchDto);

    List<TBusOrderProcessRecord> getOrderProcessRecord(Integer orderProcessId, String busType);

    //获取报工结果表记录
    List<TBusOrderProcessRecord> getBgOrderProcessRecords(Integer orderProcessId, String recordType);

    //获取盘点结果表记录
    List<TBusOrderProcessRecord> getPdOrderProcessRecords(Integer orderProcessId, String recordType);

    //工序报工校验
    OrderRecordCheckVo submitCheck(OrderRecordCheckDto saveDto);

    @Transactional
    ResponseResult submitAndBindCode(String userId, List<OrderBindCodeDto> tBusOrderBindCodeList) throws ParseException;

//    @Transactional
//    void customSubmit(OrderRecordSaveDto saveDto, String userId);

    ChopAndMixVo chopAndMixMsg(OrderProcessRecordSearchDto orderId, String userId);

    OrderRecordHeadVo getRecords(OrderProcessRecordSearchDto searchDto, String toString);

    PageVo<CraftProcessListVo> getCustomWCOrderProcess(Integer current, Integer size, Integer orderId);

    List<OrderRecordVo> getHistories(OrderProcessRecordSearchDto searchDto, String toString);

    /**
     * 换算剩余膜
     * @param materialId
     * @return
     */
    ResModalVo getTheResidualFilm(Integer materialId, Float getValue);

    ResModalVo getTheResidualFilm(Integer materialId, Float getValue, Float beatTimes);

    List<ResModalVo> getTheResidualFilms(TheResidualFilmSearchDto searchDtos);

    ResponseResult<WeightVo> getTotalWeight(OrderProcessRecordSearchDto searchDto, String toString);

    // 切换一级类目“AB料投入”，最右侧增加统计展示AB料累计投入锅数
    ResponseResult<WeightVo> getTotalAbImport(OrderProcessRecordSearchDto searchDto);

    // 切换一级类目“AB料称重”，最右侧增加统计展示A料、B料、废料产后累计数量
//    ResponseResult<WeightRecordTyL2Vo> getTotalRecordTypeL2Export(OrderProcessRecordSearchDto searchDto);
    ResponseResult<WeightTotalAbExportVo> getTotalAbExport(OrderProcessRecordSearchDto searchDto);

    // 切换一级类目“合格品产出”，
    ResponseResult<QualifiedExportVo> getQualifiedExport(QualifiedExportDao searchDto);

    /**
     *
     * @param startTs  开始时间戳UTC
     * @param endTs  结束时间戳UTC
     * @param deviceId  设备ID
     * @param key  查询的KEY值ID
     * @param agg  MAX或MIN
     * @return
     * @throws Exception
     */
    BigInteger getIotValueByKeyAndDeviceId(long startTs, long endTs, String deviceId, Integer key, String agg) throws Exception;

    /**
     *
     * @param startTs 开始时间戳UTC
     * @param endTs 结束时间戳UTC
     * @param deviceId 设备ID
     * @param key  查询的KEY值ID
     * @return
     * @throws Exception
     */
    BigInteger getIotDiffValueByKeyAndDeviceId(long startTs, long endTs, UUID deviceId, Integer key) throws Exception;

    /**
     *
     * @param deviceCode
     * @return
     * @throws Exception
     */
    UUID getDeviceIdByName(String deviceCode) throws Exception;

    /**
     *
     * @param keyName
     * @return
     * @throws Exception
     */
    Integer getDeviceKeyIdByKey(String keyName) throws Exception;

    /**
     * 设备开始时间和结束时间，按订单获取，当IOT报工结束时间为空时，取值订单工序的接单时间
     * @param timeDto
     * @return
     */
    OrderProcessTimeVo getProcessTime(OrderProcessTimeDto timeDto);

    /**
     * 设备开始时间和结束时间，按设备获取，不按订单
     * @param timeDto
     * @return
     */
    OrderProcessTimeVo getIotDeviceTime(OrderProcessIotDeviceTimeDto timeDto);

    /**
     * stretchMachine 拉伸膜机取数
     * @param startTime 格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param endTime  格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param deviceCode
     * @return
     * @throws Exception
     */
    IotDiffValueVo getStretchMachineRunNum(String startTime, String endTime, String deviceCode) throws Exception ;

    IotDiffValueVo getStretchMachineRunNum(OrderProcessIotSearchDto searchDto) throws Exception ;

    /**
     * cuttingMachine 剥皮机取数
     * @param startTime 格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param endTime  格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param deviceCode
     * @return
     * @throws Exception
     */
    IotDiffValueVo getCuttingMachineRunNum(String startTime, String endTime, String deviceCode) throws Exception ;

    /**
     * crabLine 蟹柳机取数
     * @param startTime  格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param endTime  格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param deviceCode
     * @return
     * @throws Exception
     */
    IotDiffValueVo getCrabLineRunNum(String startTime, String endTime, String deviceCode) throws Exception ;

    /**
     * packLine 包装线取数
     * @param startTime  格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param endTime  格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param deviceCode
     * @return
     * @throws Exception
     */
    IotDiffValueVo getPackLineRunNum(String startTime, String endTime, String deviceCode) throws Exception ;

    IotDiffValueVo getPackLineRunNum(OrderProcessIotSearchDto searchDto) throws Exception ;

    /**
     * iot机器取数（一段时间的差值）
     * @param startTime  格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param endTime  格式：yyyy-MM-dd HH:mm:ss:SSS
     * @param deviceCode
     * @param key
     * @return
     * @throws Exception
     */
    IotDiffValueVo getDiffValueNum(String startTime, String endTime, String deviceCode, String key) throws Exception ;

    ResponseResult getMaterialNeWeight(String materialNumber);

    MidMaterial getMidMaterial(Integer materialId);

    List<TBusOrderProcessRecord> findAllByOrderProcessIdAndBusType(Integer orderProcessId, String busType);

    List<TBusOrderProcessRecord> findAllByOrderProcessIdAndBusTypeAndRecordTypePd(Integer orderProcessId, String busType, String recordTypePd);

    ChopAndMixTotalVo chopAndMixTotal(ChopAndMixTotalSearchDto searchDto, String toString);

    GetRecordQtyUpdateVo getRecordQtyUpdate( int orderProcessId);

    GetPotVo getPot(GetPotDto getPotDto);

    List<ListPrintRecordVo> listPrintRecord(Integer orderProcessId);

    /**
     * 获取工序执行表id的盘点类型
     */
    String getOrderRecordTypePd(Integer orderProcessId);

    void submitAndBindCheckMes(SubmitAndBindCheckMesDto submitAndBindCheckMesDto, String userId) throws ParseException;

    /**
     * 修改报工记录的报工数量
     * @param saveDto
     * @param userId
     * @throws ParseException
     */
    void submitAndModify(SubmitAndModifySaveDto saveDto, String userId) throws ParseException;

    BigDecimal verifyPot(GetPotDto getPotDto);

    GetPotVo getPotBYBL(Integer orderId, List<Integer> devicePersonIds, Integer orderProcessId, String processNumberBanliao);

    /**
     * 组合报工
     * @param orderPpbomId
     */
    List<JoinRecordVo> joinRecord(Integer orderPpbomId, Integer orderId, Integer orderProcessId, List<Integer> ids);

    /**
     * 获取工序：1.根据单据编号 2.根据ERP的订单关联字段
     * @param orderId
     */
    List<TSysProcessInfo> getProcessInfos(Integer orderId);

    /**
     * 获取当前积累锅数报工详情
     * @param getPotAllRecordDetailsDto
     * @return
     */
    ListJoinRecordVo getPotAllRecordDetails(GetPotAllRecordDetailsDto getPotAllRecordDetailsDto);

    /**
     * 获取指纹认证
     * @param processId
     * @return
     */
    String getFingerprintAuthentication(Integer processId);

    /**
     * 获取指定时间段设备采集信息
     * @param getIotByDevicesDto
     * @return
     */
    GetIotByDevicesVo getIotByDevices(GetIotByDevicesDto getIotByDevicesDto) throws Exception;
}
