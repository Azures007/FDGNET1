package org.thingsboard.server.dao.chargingbasket;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderBindCode;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.sys.TSysChargingBasket;
import org.thingsboard.server.common.data.mes.sys.TSysCraftProcessRel;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.OrderBindCodeDto;
import org.thingsboard.server.dao.mes.dto.PageChargingBasketDto;
import org.thingsboard.server.dao.mes.order.OrderProcessHistoryService;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftProcessRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.mes.chargingbasket.ChargingBasketRepository;
import org.thingsboard.server.dao.sql.mes.order.*;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.mes.vo.OrderBindCodeVo;
import org.thingsboard.server.dao.mes.vo.PageVo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ChargingBasketServiceImpl implements ChargingBasketService {

    private static final Logger logger = LoggerFactory.getLogger(ChargingBasketServiceImpl.class);

    @Autowired
    ChargingBasketRepository chargingBasketRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    OrderBindCodeRepository orderBindCodeRepository;

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Autowired
    OrderProcessRecordRepository orderProcessRecordRepository;

    @Autowired
    OrderProcessHistoryRepository orderProcessHistoryRepository;

    @Autowired
    OrderProcessHistoryService orderProcessHistoryService;

    @Autowired
    TSysCraftProcessRelRepository tSysCraftProcessRelRepository;

    @Autowired
    TSysProcessInfoRepository tSysProcessInfoRepository;

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Override
    public void update(TSysChargingBasket tSysChargingBasket) {
        updateVerify(tSysChargingBasket);
        chargingBasketRepository.saveAndFlush(tSysChargingBasket);
    }

    @Override
    public TSysChargingBasket getById(Integer id) {
        return chargingBasketRepository.findById(id).get();
    }

    @Override
    public TSysChargingBasket getByCode(String code) {
        var chargingBaskets = chargingBasketRepository.findByCode(code);
        if (chargingBaskets.size() == 0) {
            throw new RuntimeException("无效二维码！\n请联系管理员");
        }
        if (chargingBaskets.size() > 0) {
            var chargingBasket = chargingBaskets.stream().findFirst().orElse(null);
            if (chargingBasket == null) {
                throw new RuntimeException("无效二维码！\n请联系管理员");
            } else if ("0".equals(chargingBasket.getChargingBasketStatus())) {
                throw new RuntimeException("料框二维码已被禁用！\n请更换料框");
            }
            return chargingBasket;
        }
        return null;
    }

    @Override
    public PageVo<TSysChargingBasket> pageChargingBasket(Integer current, Integer size, PageChargingBasketDto pageChargingBasketDto) {
        pageChargingBasketVerify(pageChargingBasketDto, current, size);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select  * from t_sys_charging_basket ");
        sqlBuilder.append("where 1=1 ");
        if (StringUtils.isNotBlank(pageChargingBasketDto.getCreatedTimeFront()) && StringUtils.isNotBlank(pageChargingBasketDto.getCreatedTimeLater())) {
            sqlBuilder.append("and created_time between ");
            sqlBuilder.append("'" + pageChargingBasketDto.getCreatedTimeFront() + "'");
            sqlBuilder.append(" and ");
            sqlBuilder.append("'" + pageChargingBasketDto.getCreatedTimeLater() + "'");
        }
        if (StringUtils.isNotBlank(pageChargingBasketDto.getCode())) {
            sqlBuilder.append(" and code like '%");
            sqlBuilder.append(pageChargingBasketDto.getCode());
            sqlBuilder.append("%'");
        }
        if (StringUtils.isNotBlank(pageChargingBasketDto.getChargingBasketStatus())) {
            sqlBuilder.append("and charging_basket_status='");
            sqlBuilder.append(pageChargingBasketDto.getChargingBasketStatus());
            sqlBuilder.append("' ");
        }
        if (StringUtils.isNotBlank(pageChargingBasketDto.getOrderKey())) {
            sqlBuilder.append("order by ");
            sqlBuilder.append(pageChargingBasketDto.getOrderKey());
            sqlBuilder.append(" ");
            sqlBuilder.append(pageChargingBasketDto.getOrderVal());
        }
        sqlBuilder.append(" limit ");
        sqlBuilder.append(size);
        sqlBuilder.append(" offset ");
        sqlBuilder.append(current);
        String sql = sqlBuilder.toString();
        System.out.println(sql);
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> mapList = query.getResultList();
        PageVo<TSysChargingBasket> pageVo = new PageVo<>();
        pageVo.setCurrent(current / size);
        pageVo.setSize(size);
        pageVo.setList(JSON.parseArray(JSON.toJSONString(mapList), TSysChargingBasket.class));
        pageVo.setTotal(getPageChargingBasket(pageChargingBasketDto));
        return pageVo;
    }

    @Override
    public void delete(Integer id) {
        chargingBasketRepository.deleteById(id);
    }

    @Override
    public List<TSysChargingBasket> list() {
        return chargingBasketRepository.findAll();
    }

    @Override
    public void bindCheckMes(TBusOrderBindCode tBusOrderBindCode) {
        tBusOrderBindCode.setCreatedTime(new Date());
        String bindCodeNumber = tBusOrderBindCode.getBindCodeNumber();
        if (StringUtils.isBlank(bindCodeNumber)) {
            throw new RuntimeException("绑定的料筐不能为空！\n请联系管理员");
        }
        TSysChargingBasket chargingBasket = chargingBasketRepository.getByCode(bindCodeNumber);
        if (chargingBasket == null) {
            throw new RuntimeException("无效二维码！\n请联系管理员");
        } else if ("0".equals(chargingBasket.getChargingBasketStatus())) {
            throw new RuntimeException("料框二维码已被禁用！\n请更换料框");
        }
        TBusOrderBindCode orderBindCodeReport = orderBindCodeRepository.getByBindCodeNumberAndBindCodeTypeAndBindCodeStatus(bindCodeNumber, LichengConstants.BIND0001, 1);
        if (orderBindCodeReport != null) {
//            if(orderBindCodeReport.getOrderNo().equals(tBusOrderBindCode.getOrderNo())){
//                logger.info("绑定的料筐:{}已经存在同个订单:{}的记录，重复报工提交，无需处理",orderBindCodeReport.getBindCodeNumber(),orderBindCodeReport.getOrderNo());
//                return;
//            }
            throw new RuntimeException("料框二维码已被绑定！\n请更换料框");
        }
        if (tBusOrderBindCode.getCraftId() == null) {
            throw new RuntimeException("工艺路线id不能传入空值\n请联系管理员");
        } else {
            TSysCraftProcessRel tSysCraftProcessRel = tSysCraftProcessRelRepository.findByCraftIdAndProcessId(tBusOrderBindCode.getCraftId(), tBusOrderBindCode.getProcessId());
            if (tSysCraftProcessRel.getIsReportingBindCode() == 0) {
                throw new RuntimeException("当前工序未开启扫码报工\n请联系管理员");
            }
        }
        if (tBusOrderBindCode.getOrderProcessId() != null) {
            TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(tBusOrderBindCode.getOrderProcessId()).orElse(null);
            if (tBusOrderProcess == null) {
                throw new RuntimeException("工序执行表查不到数据\n请联系管理员");
            }
            tBusOrderBindCode.setClasssId(tBusOrderProcess.getClassId() == null ? null : tBusOrderProcess.getClassId().getClassId());
            tBusOrderBindCode.setPersonId(tBusOrderProcess.getPersonId() == null ? null : tBusOrderProcess.getPersonId().getPersonnelId());
            tBusOrderBindCode.setBindCodeStatus(1);
            tBusOrderBindCode.setBindCodeType(LichengConstants.BIND0001);
            //获取订单报工id
            var orderProcessRecordList = orderProcessRecordRepository.findAllByOrderProcessIdAndBusTypeAndRecordType(tBusOrderBindCode.getOrderProcessId(), "BG", "3");
            if (orderProcessRecordList.size() == 0) {
                throw new RuntimeException("订单报工结果表查不到数据\n请联系管理员");
            }
            if (tBusOrderBindCode.getOrderProcessRecordId() == null) {
                var orderProcessRecord = orderProcessRecordList.get(0);
                tBusOrderBindCode.setOrderProcessRecordId(orderProcessRecord.getOrderProcessRecordId());
            }

            if (tBusOrderBindCode.getOrderProcessHistoryId() == null) {
//                List<TBusOrderProcessHistory> orderProcessHistoryList = orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeAndRecordType(tBusOrderBindCode.getOrderProcessId(), "BG", "3");
                List<TBusOrderProcessHistory> orderProcessHistoryList = orderProcessHistoryService.getBgOrderProcessRecords(tBusOrderBindCode.getOrderProcessId(), "BG", "3");
                if (orderProcessHistoryList.size() == 0) {
                    throw new RuntimeException("订单报工结果表查不到数据\n请联系管理员");
                }
                TBusOrderProcessHistory orderProcessHistory = orderProcessHistoryList.get(orderProcessHistoryList.size() - 1);
                tBusOrderBindCode.setOrderProcessHistoryId(orderProcessHistory.getOrderProcessHistoryId());
            }
        } else {
            throw new RuntimeException("工序执行表id不能为空\n请联系管理员");
        }
        orderBindCodeRepository.save(tBusOrderBindCode);
    }

    @Override
    public Boolean isBindEnabled(Integer craftId, Integer processId, String key) {
        Boolean flag = null;
        TSysCraftProcessRel byCraftIdAndProcessId = tSysCraftProcessRelRepository.findByCraftIdAndProcessId(craftId, processId);
        if (byCraftIdAndProcessId != null) {
            switch (key) {
                case "isReportingBindCode": {
                    flag = byCraftIdAndProcessId.getIsReportingBindCode() == null ? false : byCraftIdAndProcessId.getIsReportingBindCode() == Integer.parseInt(GlobalConstant.enableTrue);
                    break;
                }
                case "isReceivingBindCode": {
                    flag = byCraftIdAndProcessId.getIsReceivingBindCode() == null ? false : byCraftIdAndProcessId.getIsReceivingBindCode() == Integer.parseInt(GlobalConstant.enableTrue);
                    break;
                }
                case "isReceivingUnBindCode": {
                    flag = byCraftIdAndProcessId.getIsReceivingUnBindCode() == null ? false : byCraftIdAndProcessId.getIsReceivingUnBindCode() == Integer.parseInt(GlobalConstant.enableTrue);
                    break;
                }
            }
        }
        return flag;
    }


    //获取列表总数
    private Integer getPageChargingBasket(PageChargingBasketDto pageChargingBasketDto) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select  count(1) as count from t_sys_charging_basket ");
        sqlBuilder.append("where 1=1 ");
        if (StringUtils.isNotBlank(pageChargingBasketDto.getCreatedTimeFront()) && StringUtils.isNotBlank(pageChargingBasketDto.getCreatedTimeLater())) {
            sqlBuilder.append("and created_time between ");
            sqlBuilder.append("'" + pageChargingBasketDto.getCreatedTimeFront() + "'");
            sqlBuilder.append(" and ");
            sqlBuilder.append("'" + pageChargingBasketDto.getCreatedTimeLater() + "'");
        }
        if (StringUtils.isNotBlank(pageChargingBasketDto.getCode())) {
            sqlBuilder.append(" and code like '%");
            sqlBuilder.append(pageChargingBasketDto.getCode());
            sqlBuilder.append("%'");
        }
        if (StringUtils.isNotBlank(pageChargingBasketDto.getChargingBasketStatus())) {
            sqlBuilder.append("and charging_basket_status='");
            sqlBuilder.append(pageChargingBasketDto.getChargingBasketStatus());
            sqlBuilder.append("' ");
        }
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> mapList = query.getResultList();
        return Integer.parseInt(String.valueOf(mapList.get(0).get("count")));
    }

    //数据有效性验证
    private void pageChargingBasketVerify(PageChargingBasketDto pageChargingBasketDto, Integer current, Integer size) {
        current = current * size;
        pageChargingBasketDto.setChargingBasketStatus(StringUtils.isBlank(pageChargingBasketDto.getChargingBasketStatus()) ? "" : pageChargingBasketDto.getChargingBasketStatus());
        pageChargingBasketDto.setCode(StringUtils.isBlank(pageChargingBasketDto.getCode()) ? "" : pageChargingBasketDto.getCode());
        pageChargingBasketDto.setCreatedTimeFront(StringUtils.isBlank(pageChargingBasketDto.getCreatedTimeFront()) ? "" : pageChargingBasketDto.getCreatedTimeFront());
        pageChargingBasketDto.setCreatedTimeLater(StringUtils.isBlank(pageChargingBasketDto.getCreatedTimeLater()) ? "" : pageChargingBasketDto.getCreatedTimeLater() + " 23:59:59");
        pageChargingBasketDto.setOrderKey(StringUtils.isBlank(pageChargingBasketDto.getOrderKey()) ? "created_time" : pageChargingBasketDto.getOrderKey());
        pageChargingBasketDto.setOrderVal(StringUtils.isBlank(pageChargingBasketDto.getOrderVal()) ? "desc" : pageChargingBasketDto.getOrderVal());
    }

    //数据有效性验证
    private void updateVerify(TSysChargingBasket tSysChargingBasket) {
        if (tSysChargingBasket.getChargingBasketId() == null) {
            // 新增
            if (StringUtils.isBlank(tSysChargingBasket.getCode())) {
                throw new RuntimeException("料筐编码不能为空\n请联系管理员");
            }
            List<TSysChargingBasket> tSysChargingBasketList = chargingBasketRepository.findByCode(tSysChargingBasket.getCode());
            if (tSysChargingBasketList != null && tSysChargingBasketList.size() > 0 && tSysChargingBasketList.get(0).getChargingBasketId() != tSysChargingBasket.getChargingBasketId()) {
                throw new RuntimeException("新增的料筐编码不能和已有的重复\n请联系管理员");
            }
            if (StringUtils.isBlank(tSysChargingBasket.getChargingBasketStatus())) {
                tSysChargingBasket.setChargingBasketStatus(GlobalConstant.enableTrue);
            }
            tSysChargingBasket.setCreatedTime(tSysChargingBasket.getUpdatedTime());
            tSysChargingBasket.setCreatedName(tSysChargingBasket.getUpdatedName());
        } else {
            // 修改
            List<TSysChargingBasket> tSysChargingBasketList = chargingBasketRepository.findByCode(tSysChargingBasket.getCode());
            if (tSysChargingBasketList != null && tSysChargingBasketList.size() > 0 && tSysChargingBasketList.get(0).getChargingBasketId() != tSysChargingBasket.getChargingBasketId()) {
                throw new RuntimeException("修改的料筐编码不能和已有的重复\n请联系管理员");
            }
        }
    }

    @Transactional
    @Override
    public void bindCheckMesStartTask(List<OrderBindCodeDto> orderBindCodeDtoList) {
        for (int i = 0; i < orderBindCodeDtoList.size(); i++) {
            var orderBindCodeDto = orderBindCodeDtoList.get(i);
            var saveDto = orderBindCodeDto.getOrderRecordSaveDto();//本来用于报工submit
            TBusOrderBindCode tBusOrderBindCode = new TBusOrderBindCode();
            BeanUtils.copyProperties(orderBindCodeDto, tBusOrderBindCode);
            tBusOrderBindCode.setCreatedTime(new Date());
            String bindCodeNumber = tBusOrderBindCode.getBindCodeNumber();
            if (StringUtils.isBlank(bindCodeNumber)) {
                throw new RuntimeException("绑定的料筐不能为空\n请联系管理员");
            }
            TSysChargingBasket chargingBasket = chargingBasketRepository.getByCode(bindCodeNumber);
            if (chargingBasket == null) {
                throw new RuntimeException("无效二维码！\n请联系管理员");
            } else if (GlobalConstant.enableFalse.equals(chargingBasket.getChargingBasketStatus())) {
                throw new RuntimeException("料框二维码已被停用！\n请联系管理员");
            }
            //接单扫码提交校验、扫码解绑校验
            boolean isReceivingUnBindCode = false;
            if (StringUtils.isEmpty(tBusOrderBindCode.getOrderNo())) {
                throw new RuntimeException("绑定的料筐订单号不能为空\n请联系管理员");
            } else {
                var orderHeads = orderHeadRepository.findByOrderNo(tBusOrderBindCode.getOrderNo());
                if (orderHeads.size() > 0) {
                    var orderHead0 = orderHeads.get(0);
                    TSysCraftProcessRel tSysCraftProcessRel = tSysCraftProcessRelRepository.findByCraftIdAndProcessId(orderHead0.getCraftId().getCraftId(), tBusOrderBindCode.getProcessId());
                    if (tSysCraftProcessRel == null) {
                        throw new RuntimeException("当前工序未查询到工艺工序关系记录\n请联系管理员");
                    }
                    if (tSysCraftProcessRel.getIsReceivingBindCode() == null || tSysCraftProcessRel.getIsReceivingBindCode() == Integer.parseInt(GlobalConstant.enableFalse)) {
                        throw new RuntimeException("当前工序未开启接单扫码\n请联系管理员");
                    }
                    if (tSysCraftProcessRel.getIsReceivingUnBindCode() == null || tSysCraftProcessRel.getIsReceivingUnBindCode() == Integer.parseInt(GlobalConstant.enableFalse)) {
                    } else {
                        isReceivingUnBindCode = true;
                    }
                }
            }
            if (tBusOrderBindCode.getOrderProcessId() != null) {
                TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(tBusOrderBindCode.getOrderProcessId()).orElse(null);
                if (tBusOrderProcess == null) {
                    throw new RuntimeException("工序执行表查不到数据\n请联系管理员");
                }
                tBusOrderBindCode.setClasssId(tBusOrderProcess.getClassId() == null ? null : tBusOrderProcess.getClassId().getClassId());
                tBusOrderBindCode.setPersonId(tBusOrderProcess.getPersonId() == null ? null : tBusOrderProcess.getPersonId().getPersonnelId());
                tBusOrderBindCode.setBindCodeStatus(1);
                tBusOrderBindCode.setBindCodeType(LichengConstants.BIND0002);
            } else {
                throw new RuntimeException("本工序执行表id不能为空\n请联系管理员");
            }
            //保存更新
            var tBusOrderBindCodeReport = orderBindCodeRepository.getByBindCodeNumberAndBindCodeTypeAndBindCodeStatus(bindCodeNumber, LichengConstants.BIND0001, 1);
            if (tBusOrderBindCodeReport == null) {
                throw new RuntimeException("料框二维码未被绑定！\n请联系上道工序组长进行绑定");
            } else {
                tBusOrderBindCode.setPrevBindCodeId(tBusOrderBindCodeReport.getBindCodeId());
                tBusOrderBindCode.setPrevOrderProcessRecordId(tBusOrderBindCodeReport.getOrderProcessRecordId());
                tBusOrderBindCode.setPrevOrderProcessHistoryId(tBusOrderBindCodeReport.getOrderProcessHistoryId());
                tBusOrderBindCode.setPrevOrderProcessId(tBusOrderBindCodeReport.getOrderProcessId());
                tBusOrderBindCode.setPrevOrderNo(tBusOrderBindCodeReport.getOrderNo());
                orderBindCodeRepository.save(tBusOrderBindCode);
                if (isReceivingUnBindCode) {
                    //进行料框解绑操作，获取以上扫码的料框编码（一个或多个），关联查询“扫码绑定信息表”过滤条件“扫码类型”为报工扫码，将“状态”更新为“已解绑”
                    tBusOrderBindCodeReport.setBindCodeStatus(2);
                    orderBindCodeRepository.save(tBusOrderBindCodeReport);
                }
            }
        }
    }

    /**
     * 处理扫码绑定和解绑产后绑定
     *
     * @param tBusOrderBindCode
     */
    private void checkAndBindCode(TBusOrderBindCode tBusOrderBindCode) {
        tBusOrderBindCode.setCreatedTime(new Date());
        String bindCodeNumber = tBusOrderBindCode.getBindCodeNumber();
        if (StringUtils.isBlank(bindCodeNumber)) {
            throw new RuntimeException("绑定的料筐不能为空\n请联系管理员");
        }
        TSysChargingBasket chargingBasket = chargingBasketRepository.getByCode(bindCodeNumber);
        if (chargingBasket == null) {
            throw new RuntimeException("无效二维码！\n请联系管理员");
        } else if (GlobalConstant.enableFalse.equals(chargingBasket.getChargingBasketStatus())) {
            throw new RuntimeException("料框二维码已被停用！\n请联系管理员");
        }
        //接单扫码提交校验、扫码解绑校验
        boolean isReceivingUnBindCode = false;
        if (StringUtils.isEmpty(tBusOrderBindCode.getOrderNo())) {
            throw new RuntimeException("绑定的料筐订单号不能为空\n请联系管理员");
        } else {
            var orderHeads = orderHeadRepository.findByOrderNo(tBusOrderBindCode.getOrderNo());
            if (orderHeads.size() > 0) {
                var orderHead0 = orderHeads.get(0);
                TSysCraftProcessRel tSysCraftProcessRel = tSysCraftProcessRelRepository.findByCraftIdAndProcessId(orderHead0.getCraftId().getCraftId(), tBusOrderBindCode.getProcessId());
                if (tSysCraftProcessRel == null) {
                    throw new RuntimeException("当前工序未查询到工艺工序关系记录\n请联系管理员");
                }
                if (tSysCraftProcessRel.getIsReceivingBindCode() == null || tSysCraftProcessRel.getIsReceivingBindCode() == Integer.parseInt(GlobalConstant.enableFalse)) {
                    throw new RuntimeException("当前工序未开启接单扫码\n请联系管理员");
                }
                if (tSysCraftProcessRel.getIsReceivingUnBindCode() == null || tSysCraftProcessRel.getIsReceivingUnBindCode() == Integer.parseInt(GlobalConstant.enableFalse)) {
                } else {
                    isReceivingUnBindCode = true;
                }
            }
        }
        if (tBusOrderBindCode.getOrderProcessId() != null) {
            TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(tBusOrderBindCode.getOrderProcessId()).orElse(null);
            if (tBusOrderProcess == null) {
                throw new RuntimeException("工序执行表查不到数据\n请联系管理员");
            }
            tBusOrderBindCode.setClasssId(tBusOrderProcess.getClassId() == null ? null : tBusOrderProcess.getClassId().getClassId());
            tBusOrderBindCode.setPersonId(tBusOrderProcess.getPersonId() == null ? null : tBusOrderProcess.getPersonId().getPersonnelId());
            tBusOrderBindCode.setBindCodeStatus(1);
            tBusOrderBindCode.setBindCodeType(LichengConstants.BIND0002);
        } else {
            throw new RuntimeException("本工序执行表id不能为空\n请联系管理员");
        }
        var tBusOrderBindCodeReport = orderBindCodeRepository.getByBindCodeNumberAndBindCodeTypeAndBindCodeStatus(bindCodeNumber, LichengConstants.BIND0001, 1);
        if (tBusOrderBindCodeReport == null) {
            throw new RuntimeException("料框二维码未被绑定！\n请联系上道工序组长进行绑定");
        } else {
            tBusOrderBindCode.setPrevBindCodeId(tBusOrderBindCodeReport.getBindCodeId());
            tBusOrderBindCode.setPrevOrderProcessRecordId(tBusOrderBindCodeReport.getOrderProcessRecordId());
            tBusOrderBindCode.setPrevOrderProcessHistoryId(tBusOrderBindCodeReport.getOrderProcessHistoryId());
            tBusOrderBindCode.setPrevOrderProcessId(tBusOrderBindCodeReport.getOrderProcessId());
            tBusOrderBindCode.setPrevOrderNo(tBusOrderBindCodeReport.getOrderNo());
            orderBindCodeRepository.save(tBusOrderBindCode);
            if (isReceivingUnBindCode) {
                //进行料框解绑操作，获取以上扫码的料框编码（一个或多个），关联查询“扫码绑定信息表”过滤条件“扫码类型”为报工扫码，将“状态”更新为“已解绑”
                tBusOrderBindCodeReport.setBindCodeStatus(2);
                orderBindCodeRepository.save(tBusOrderBindCodeReport);
            }
        }
    }

    @Override
    public OrderBindCodeVo queryOrderBindCode(String bindCodeNumber, String orderNo) {
        if (StringUtils.isBlank(bindCodeNumber)) {
            throw new RuntimeException("绑定的料筐不能为空\n请联系管理员");
        }
        TSysChargingBasket chargingBasket = chargingBasketRepository.getByCode(bindCodeNumber);
        if (chargingBasket == null) {
            throw new RuntimeException("无效二维码！\n请联系管理员");
        } else if ("0".equals(chargingBasket.getChargingBasketStatus())) {
            throw new RuntimeException("料框二维码已被停用！\n请联系管理员");
        }
        OrderBindCodeVo orderBindCodeVo = new OrderBindCodeVo();
        TBusOrderBindCode tBusOrderBindCodeReport = orderBindCodeRepository.getByBindCodeNumberAndBindCodeTypeAndBindCodeStatus(bindCodeNumber, LichengConstants.BIND0001, 1);
        if (tBusOrderBindCodeReport == null) {
            throw new RuntimeException("料框二维码未被绑定！\n请联系上道工序组长进行绑定");
        }
        //13283 【管理后台】扫码投入，进行单据编号比对校验优化（一阶段）
        if (StringUtils.isNotEmpty(orderNo)) {
            var billNo = orderHeadRepository.getBillNoByOrderNo(orderNo);
            var billNoPrev = orderHeadRepository.getBillNoByOrderNo(tBusOrderBindCodeReport.getOrderNo());
            if (!billNoPrev.equals(billNo)) {
                throw new RuntimeException("与本道单据编号不一致，本道单据编号：" + billNo);
            }
        }
        BeanUtils.copyProperties(tBusOrderBindCodeReport, orderBindCodeVo);
        var orderProcessHistory = orderProcessHistoryRepository.findById(tBusOrderBindCodeReport.getOrderProcessHistoryId()).orElse(null);
        if (orderProcessHistory != null) {
            orderBindCodeVo.setReportQty(orderProcessHistory.getRecordQty().floatValue());
            orderBindCodeVo.setReportUnit(orderProcessHistory.getRecordUnit());
            orderBindCodeVo.setReportUnitStr(GlobalConstant.getCodeDscName("UNIT0000", orderProcessHistory.getRecordUnit()));
        } else {
            throw new RuntimeException("当前料筐已被绑定的订单没有产后报工信息\n请联系管理员");
        }
        var tSysProcessInfo = tSysProcessInfoRepository.findById(tBusOrderBindCodeReport.getProcessId()).orElse(null);
        if (tSysProcessInfo != null) {
            orderBindCodeVo.setProcessName(tSysProcessInfo.getProcessName());
        }
        var tSysPersonnelInfo = tSysPersonnelInfoRepository.findById(tBusOrderBindCodeReport.getPersonId()).orElse(null);
        if (tSysPersonnelInfo != null) {
            orderBindCodeVo.setPersonName(tSysPersonnelInfo.getName());
        }
        var orderHeadList = orderHeadRepository.findByOrderNo(tBusOrderBindCodeReport.getOrderNo());
        if (orderHeadList.size() > 0) {
            var orderHead = orderHeadList.get(0);
            orderBindCodeVo.setBodyMaterialId(orderHead.getBodyMaterialId());
            orderBindCodeVo.setBodyMaterialNumber(orderHead.getBodyMaterialNumber());
            orderBindCodeVo.setBodyMaterialName(orderHead.getBodyMaterialName());
            orderBindCodeVo.setBodyLot(orderHead.getBodyLot());
        }
        return orderBindCodeVo;
    }

    @Override
    public String getQR(String code) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(code, BarcodeFormat.QR_CODE, 600, 600);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        Base64.Encoder encoder = Base64.getEncoder();

        String text = encoder.encodeToString(outputStream.toByteArray());
        return text;
    }

    @Transactional
    @Override
    public void unBind(String bindCodeNumber) {
        List<TBusOrderBindCode> tBusOrderBindCodes = orderBindCodeRepository.findByBindCodeNumberAndBindCodeTypeAndBindCodeStatus(bindCodeNumber, "BIND0001", 1);
        if (null !=tBusOrderBindCodes   && tBusOrderBindCodes.size() > 0) {
            tBusOrderBindCodes.stream().forEach(tBusOrderBindCode -> {
                tBusOrderBindCode.setBindCodeStatus(2);
                orderBindCodeRepository.saveAndFlush(tBusOrderBindCode);
            });
        }

    }
}
