package org.thingsboard.server.dao.mes.order;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.common.util.BigDecimalUtil;
import org.thingsboard.common.util.Utils;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.mes.*;
import org.thingsboard.server.common.data.mes.bus.*;
import org.thingsboard.server.common.data.mes.sys.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.TSysCraftinfo.TSysCraftInfoService;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.*;
import org.thingsboard.server.dao.mes.message.MessageService;
import org.thingsboard.server.dao.mes.orderProcess.AppOrderProcessRecordSubmitService;
import org.thingsboard.server.dao.mes.orderProcess.OrderProcessRecordService;
import org.thingsboard.server.dao.mes.vo.*;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftProcessRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessClassRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.mes.licheng.MidMaterialRepository;
import org.thingsboard.server.dao.sql.mes.message.MessageOrderRepository;
import org.thingsboard.server.dao.sql.mes.order.*;
import org.thingsboard.server.dao.sql.mes.role.RoleRepository;
import org.thingsboard.server.dao.sql.mes.role.RoleUserRepository;
import org.thingsboard.server.dao.sql.mes.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.dao.sql.mes.tSysDevice.TSysDeviceRepository;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.mes.tSysCodeDsc.TSysCodeDscService;
import org.thingsboard.server.dao.user.UserService;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.thingsboard.server.common.data.mes.LichengConstants.*;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.role
 * @date 2022/4/22 19:43
 * @Description:
 */
@Service
@Slf4j
public class OrderHeadServiceImpl implements OrderHeadService {

    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    RedisTemplate redisTemplate;

    private static String ORDER_HEAD_HEADER_SIZE = "order:size:";

    @Autowired
    TBusOrderUpdateRepository orderUpdateRepository;

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderPPBomRepository orderPPBomRepository;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Autowired
    TSysClassRepository classRepository;

    @Autowired
    TSysCraftInfoRepository craftInfoRepository;

    @Autowired
    TSysCraftProcessRelRepository craftProcessRelRepository;

    @Autowired
    TSysProcessInfoRepository processInfoRepository;

    @Autowired
    TSysProcessClassRelRepository processClassRelRepository;

    @Autowired
    TSysCodeDscService sysCodeDscService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MessageService messageService;

    @Autowired
    OrderProcessRecordRepository orderProcessRecordRepository;

    @Autowired
    MessageOrderRepository messageOrderRepository;

    @Autowired
    OrderProcessService orderProcessService;

    @Autowired
    OrderProcessRecordService orderProcessRecordService;

    @Autowired
    TSysCraftInfoService craftInfoService;

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Autowired
    OrderProcessDeviceRelService orderProcessDeviceRelService;

    @Autowired
    OrderProcessPersonRelService orderProcessPersonRelService;

    @Autowired
    TSysDeviceRepository tSysDeviceRepository;

    @Autowired
    RoleUserRepository roleUserRepository;

    @Autowired
    OrderProcessHistoryRepository orderProcessHistoryRepository;

    @Autowired
    MidMaterialRepository midMaterialRepository;

    @Autowired
    AppOrderProcessRecordSubmitService appOrderProcessRecordSubmitService;

    @Autowired
    protected UserService userService;

    @Autowired
    TSysCodeDscRepository tSysCodeDscRepository;

    @Autowired
    OrderProcessPersonRelRepository orderProcessPersonRelRepository;

    @Override
    public PageVo<TBusOrderHead> tBusOrderHeadList(Integer current, Integer size, TBusOrderHeadDto tBusOrderHeadDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "billDate");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("billNo", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("materialNumber", ExampleMatcher.GenericPropertyMatchers.exact());
        TBusOrderHead orderHead = new TBusOrderHead();
        BeanUtils.copyProperties(tBusOrderHeadDto, orderHead);
        Example<TBusOrderHead> example = Example.of(orderHead, matcher);
        Page<TBusOrderHead> orderHeadPage = orderHeadRepository.findAll(example, pageable);
        PageVo<TBusOrderHead> pageVo = new PageVo<>(orderHeadPage);
        return pageVo;
    }

    @Override
    public PageVo<TBusOrderHead> tBusOrderHeadList(Integer current, Integer size, TBusOrderDto orderDto) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "billDate");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "billDate"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "orderId"));
        Sort sort1 = Sort.by(orders);
        Pageable pageable = PageRequest.of(current, size, sort1);
        Page<TBusOrderHead> orderHeadPage = orderHeadRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();

            //生产订单号
            if (!StringUtils.isEmpty(orderDto.getBillNo())) {
                predicates.add(criteriaBuilder.like(root.get("billNo"), "%" + orderDto.getBillNo() + "%"));
            }
            //MES订单号
            if (!StringUtils.isEmpty(orderDto.getOrderNo())) {
                predicates.add(criteriaBuilder.like(root.get("orderNo"), "%" + orderDto.getOrderNo() + "%"));
            }
            //产品编码
            if (!StringUtils.isEmpty(orderDto.getNcMaterialCode())) {
                predicates.add(criteriaBuilder.like(root.get("bodyMaterialNumber"), "%" + orderDto.getNcMaterialCode() + "%"));
            }
            //产品名称
            if (!StringUtils.isEmpty(orderDto.getNcMaterialName())) {
                predicates.add(criteriaBuilder.like(root.get("bodyMaterialName"), "%" + orderDto.getNcMaterialName() + "%"));
            }
            //订单状态
            if (!StringUtils.isEmpty(orderDto.getOrderStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("orderStatus"), orderDto.getOrderStatus()));
            }
            /*if (!StringUtils.isEmpty(orderDto.getOrderMatching()) && !"0".equals(orderDto.getOrderMatching())) {
                predicates.add(criteriaBuilder.equal(root.get("orderMatching"), orderDto.getOrderMatching()));
            }*/
            //生产线 ID
            if (!StringUtils.isEmpty(orderDto.getCwkid())) {
                predicates.add(criteriaBuilder.equal(root.get("cwkid"), orderDto.getCwkid()));
            }
            if (orderDto.getCwkids() != null && !orderDto.getCwkids().isEmpty()) {
                predicates.add(root.get("cwkid").in(orderDto.getCwkids()));
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (orderDto.getBillDateStart() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("billDate"), orderDto.getBillDateStart()));
                }
                if (orderDto.getBillDateEnd() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("billDate"), orderDto.getBillDateEnd()));
                }
                if (orderDto.getNcReceiveTimeStart() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("bodyPlanStartDate"), orderDto.getNcReceiveTimeStart()));
                }
                if (orderDto.getNcReceiveTimeEnd() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("bodyPlanStartDate"), orderDto.getNcReceiveTimeEnd()));
                }
                // 新增开工时间条件，支持空值查询
                /*if (orderDto.getNcReceiveTimeStart() != null || orderDto.getNcReceiveTimeEnd() != null) {
                    if (orderDto.getNcReceiveTimeStart() != null && orderDto.getNcReceiveTimeEnd() != null) {
                        // 开工时间范围查询
                        predicates.add(criteriaBuilder.between(root.get("ncReceiveTime"), orderDto.getNcReceiveTimeStart(), orderDto.getNcReceiveTimeEnd()));
                    } else if (orderDto.getNcReceiveTimeStart() != null) {
                        // 大于等于指定开始时间
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ncReceiveTime"), orderDto.getNcReceiveTimeStart()));
                    } else {
                        // 小于等于指定结束时间
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("ncReceiveTime"), orderDto.getNcReceiveTimeEnd()));
                    }
                } else {
                    // 如果没有设置时间范围，则包含 ncReceiveTime 为 null 的情况
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.isNull(root.get("ncReceiveTime")),
                            criteriaBuilder.isTrue(criteriaBuilder.literal(true)) // 占位符确保条件有效
                    ));
                }*/


                //过滤是否删除,1,非删除
                predicates.add(criteriaBuilder.equal(root.get("isDeleted"), GlobalConstant.enableFalse));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        }, pageable);
        PageVo<TBusOrderHead> pageVo = new PageVo<>(orderHeadPage);
        return pageVo;
    }

    @Override
    public PageVo<OrderListVo> getOrderHeadList(Integer current, Integer size, TBusOrderDto tBusOrderDto) throws Exception {
        PageVo<OrderListVo> pageVo = new PageVo(size, current);
        current = current * size;
        List<OrderListVo> orderVos = new ArrayList<>();
        //默认条件
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        if (tBusOrderDto.getBillDateStart() == null) {
//            Date date1 = sdf.parse("1900-01-01");
//            tBusOrderDto.setBillDateStart(new Timestamp(date1.getTime()));
//        }
//        if (tBusOrderDto.getBillDateEnd() == null) {
//            Date date2 = sdf.parse("9999-01-01");
//            tBusOrderDto.setBillDateEnd(new Timestamp(date2.getTime()));
//        }
//        if (tBusOrderDto.getPlanStartDateStart() == null) {
//            Date date1 = sdf.parse("1900-01-01");
//            tBusOrderDto.setPlanStartDateStart(new Timestamp(date1.getTime()));
//        }
//        if (tBusOrderDto.getPlanStartDateEnd() == null) {
//            Date date2 = sdf.parse("9999-01-01");
//            tBusOrderDto.setPlanStartDateEnd(new Timestamp(date2.getTime()));
//        }
        int total = orderHeadRepository.pageOrderHeadCount(tBusOrderDto);
        List<Map> maps = orderHeadRepository.pageOrderHead(current, size, tBusOrderDto);
        OrderListVo orderVo;
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map map : maps) {
            String string = objectMapper.writeValueAsString(map);
            orderVo = objectMapper.readValue(string, OrderListVo.class);
            orderVos.add(orderVo);
        }
        pageVo.setTotal(total);
        pageVo.setList(orderVos);
        return pageVo;
    }

    @Override
    public ShiftRecordDetailVo shiftRecordDetail(Integer orderProcessId, Integer toOrderProcessId) {
        Map map = orderProcessRepository.getShiftRecordDetailVo(orderProcessId);
        if (map == null || map.size() <= 0) {
            return null;
        }
        ShiftRecordDetailVo shiftRecordDetailVo = JSON.parseObject(JSON.toJSONString(map), ShiftRecordDetailVo.class);
        Map accept = orderProcessRepository.getAccept(toOrderProcessId);
        if (accept != null && accept.size() > 0) {
            shiftRecordDetailVo.setAcceptName(String.valueOf(accept.get("accept_name")));
            shiftRecordDetailVo.setAcceptClassName(String.valueOf(accept.get("accept_class_name")));
            shiftRecordDetailVo.setToOrderNo(String.valueOf(accept.get("to_order_no")));
        }
        TBusOrderProcessRecord orderProcessRecord = new TBusOrderProcessRecord();
        orderProcessRecord.setOrderProcessId(orderProcessId);
        orderProcessRecord.setRecordTypeBg(LichengConstants.REPORTYPE0002);
        orderProcessRecord.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        orderProcessRecord.setRecordQty(null);
        orderProcessRecord.setRecordManualQty(null);
        orderProcessRecord.setCapacityQty(null);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("orderProcessId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("busType", ExampleMatcher.GenericPropertyMatchers.exact())//匹配
                .withMatcher("recordTypeBg", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<TBusOrderProcessRecord> example = Example.of(orderProcessRecord, matcher);
        List<TBusOrderProcessRecord> tBusOrderProcessRecords = orderProcessRecordRepository.findAll(example);
        if (tBusOrderProcessRecords.isEmpty()) {
            throw new RuntimeException("请先进行尾料报工，再进行转移");
        } else {
            shiftRecordDetailVo.setTBusOrderProcessRecords(tBusOrderProcessRecords);
            shiftRecordDetailVo.setCount(tBusOrderProcessRecords.size());
        }
        return shiftRecordDetailVo;
    }

    @Override
    public PageVo<TaskListVo> listShiftTaskList(Integer current, Integer size, String sort, String userId, OrderTaskSelectDto selectDto) {
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "order_no");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Sort sort1 = Sort.by(orders);
//        current = current * size;
        PageRequest of = PageRequest.of(current, size, sort1);
        List<Map> select = orderHeadRepository.listShiftTaskList(userId, selectDto.getProcessNumber(), selectDto.getBodyLot(), of);
        PageVo<TaskListVo> pageVo = new PageVo(size, current);
        pageVo.setList(JSON.parseArray(JSON.toJSONString(select), TaskListVo.class));
        int count = orderHeadRepository.countShiftTaskList(userId, selectDto.getProcessNumber(), selectDto.getBodyLot());
        pageVo.setTotal(count);
        return pageVo;
    }

    @Override
    public PageVo<TaskListVo> listShiftNoAcceptTaskList(Integer current, Integer size, String sort, String userId, OrderTaskSelectDto selectDto) {
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "order_no");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Sort sort1 = Sort.by(orders);
//        current = current * size;
        PageRequest of = PageRequest.of(current, size, sort1);
        List<Map> select = orderHeadRepository.listShiftNoAcceptTaskList(userId, selectDto.getProcessNumber(), selectDto.getBodyLot(), of);
        PageVo<TaskListVo> pageVo = new PageVo(size, current);
        pageVo.setList(JSON.parseArray(JSON.toJSONString(select), TaskListVo.class));
        pageVo.setTotal(orderHeadRepository.countShiftNoAcceptTaskList(userId, selectDto.getProcessNumber(), selectDto.getBodyLot()));
        return pageVo;
    }

    @Transactional
    @Override
    public void rejectedShiftRecord(ShiftRecordDto shiftRecordDto, String userId) throws ParseException {
        //当前订单对应的工序执行ID，执行工序状态更新为"移交驳回"
        TBusOrderProcess tBusOrderProcess2 = orderProcessRepository.findById(shiftRecordDto.getOrderProcessId()).orElse(null);
        if (tBusOrderProcess2 != null && tBusOrderProcess2.getProcessStatus().equals(LichengConstants.PROCESSSTATUS_5)) {
            throw new RuntimeException("重复移交驳回");
        }
        tBusOrderProcess2.setProcessStatus(LichengConstants.PROCESSSTATUS_5);
        orderProcessRepository.saveAndFlush(tBusOrderProcess2);
        //移交方订单对应的工序执行ID，执行工序状态更新为赋值为"移交中"之前的状态。
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(tBusOrderProcess2.getOldOrderProcessId()).orElse(null);
        //更新原工序状态
        //tBusOrderProcess.setOldProcessStatus(tBusOrderProcess.getProcessStatus());
        //更新工序状态为"移交中"之前的状态
        tBusOrderProcess.setProcessStatus(tBusOrderProcess.getOldProcessStatus());
        orderProcessRepository.saveAndFlush(tBusOrderProcess);
        //系统给移交方推送APP消息，格式如下：
        TBusOrderHead tBusOrderHead = orderHeadRepository.getByOrderNo(tBusOrderProcess.getOrderNo());
        if (tBusOrderHead == null) {
            throw new RuntimeException("获取的订单不存在");
        }
        TSysMessageOrder tSysMessageOrder = new TSysMessageOrder();
        tSysMessageOrder.setUserId(tBusOrderProcess.getPersonId().getUserId());//
        tSysMessageOrder.setMesTime(new Date());
        tSysMessageOrder.setOrderId(tBusOrderHead.getOrderId());
        tSysMessageOrder.setOrderNo(tBusOrderProcess.getOrderNo());
        tSysMessageOrder.setOrderType(0);
        tSysMessageOrder.setCreatedTime(tBusOrderHead.getBillDate());
        tSysMessageOrder.setProductStandard(tBusOrderHead.getBodyMaterialName());
        tSysMessageOrder.setBillPlanQty(String.valueOf(tBusOrderHead.getBodyPlanPrdQty()));
        tSysMessageOrder.setMesType("4");
        tSysMessageOrder.setStatusType("0");
        tSysMessageOrder.setOrderProcessId(tBusOrderProcess.getOrderProcessId());
        tSysMessageOrder.setExecuteProcessStatus(tBusOrderProcess.getOldProcessStatus());
        //移交驳回信息
        List<BotMessageVo> botMessageVos = new ArrayList<>();
        botMessageVos.add(getBotMessageVo(tBusOrderProcess2));
        botMessageVos.add(getBotMessageVo(tBusOrderProcess));
        //移交驳回备注
        tSysMessageOrder.setRemark(shiftRecordDto.getRemark().isEmpty() ? "移交驳回" : shiftRecordDto.getRemark());
        //工序执行表ID
        //tSysMessageOrder.setOrderProcessId(shiftRecordDto.getOrderProcessId());
        tSysMessageOrder.setBotMessage(JSON.toJSONString(botMessageVos));
        log.info("--------------------发送消息：");
        log.info(tSysMessageOrder.toString());
        messageService.chatMessage(userId, tSysMessageOrder);
    }

    //获取移交信息
    private BotMessageVo getBotMessageVo(TBusOrderProcess tBusOrderProcess) {
        BotMessageVo botMessageVo = new BotMessageVo();
        TSysProcessInfo processId = tBusOrderProcess.getProcessId();
        botMessageVo.setProcessName(processId.getProcessName());
        botMessageVo.setName(tBusOrderProcess.getPersonId() == null ? "" : tBusOrderProcess.getPersonId().getName());
        return botMessageVo;
    }

    @Transactional
    @Override
    public void acceptShiftRecord(Integer orderProcessId, Integer toOrderProcessId, String userId) throws ParseException {
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(toOrderProcessId).orElse(null);
        if (!tBusOrderProcess.getProcessId().getProcessNumber().equals(LichengConstants.PROCESS_NUMBER_BANLIAO)) {
            throw new RuntimeException("接收订单当前工序非拌料，接收订单编号：" + tBusOrderProcess.getOrderNo());
        }
        tBusOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_3);
        orderProcessRepository.saveAndFlush(tBusOrderProcess);
        addShiftBG(orderProcessId, toOrderProcessId, userId);
    }

    @Override
    public TBusOrderHead getOrderProcess(Integer orderProcessId) {
        Integer id = orderProcessRepository.getHead(orderProcessId);
        return orderHeadRepository.findById(id).orElse(null);
    }

    /**
     * 添加报工
     *
     * @param orderProcessId
     * @param toOrderProcessId
     * @param userId
     */
    private void addShiftBG(Integer orderProcessId, Integer toOrderProcessId, String userId) throws ParseException {
        OrderRecordSaveDto saveDto = new OrderRecordSaveDto();
        TBusOrderProcess toTBusOrderProcess = orderProcessRepository.findById(toOrderProcessId).orElse(null);
        saveDto.setOrderProcessId(toOrderProcessId);
        saveDto.setOrderNo(toTBusOrderProcess.getOrderNo());
        saveDto.setProcessId(toTBusOrderProcess.getProcessId().getProcessId());
        saveDto.setProcessName(toTBusOrderProcess.getProcessId().getProcessName());
        saveDto.setProcessNumber(toTBusOrderProcess.getProcessId().getProcessNumber());
        saveDto.setRecordType(LichengConstants.ORDER_RECORD_TYPE_5);
        saveDto.setRecordTypeBg("REPORTYPE0001");
        Map map = orderProcessRecordRepository.sumQtyAndUnit(orderProcessId);
        saveDto.setRecordQty(BigDecimal.valueOf(Float.parseFloat(String.valueOf(map.get("record_qty")))));
        saveDto.setRecordUnit(String.valueOf(map.get("record_unit")));
        appOrderProcessRecordSubmitService.submit(saveDto, userId);
    }

    @Override
    public void saveTBusOrderHead(TBusOrderHead orderHead) {
        if (orderHead.getOrderId() == null) {
            //新增
            orderHead.setCreatedName(orderHead.getUpdatedName());
            orderHead.setCreatedTime(orderHead.getUpdatedTime());
        }
        orderHeadRepository.saveAndFlush(orderHead);
    }

    @Override
    public TBusOrderHead getTBusOrderHead(Integer orderId) {
        return orderHeadRepository.findById(orderId).orElse(null);
    }

    @Override
    public TBusOrderHead getOrderById(Integer orderId) {
        return orderHeadRepository.findById(orderId).orElse(null);
    }

    @Override
    public OrderDetailVo getTBusOrderDetail(Integer orderId, Integer orderProcessId) {
        OrderDetailVo orderDetailVo;
        List<OrderDetailPPBomVo> orderDetailPPBomVoList;
        OrderDetailPPBomVo orderDetailPPBomVo;
        var tBusOrderHead = orderHeadRepository.findById(orderId).get();
        orderDetailVo = new OrderDetailVo();
        BeanUtils.copyProperties(tBusOrderHead, orderDetailVo);
        orderDetailVo.setCraftName(tBusOrderHead.getCraftId() == null ? "" : tBusOrderHead.getCraftId().getCraftName());
        orderDetailVo.setCurrentProcess(tBusOrderHead.getCurrentProcess() == null ? null : tBusOrderHead.getCurrentProcess().getProcessId());
        orderDetailVo.setCurrentProcessName(tBusOrderHead.getCurrentProcess() == null ? "" : tBusOrderHead.getCurrentProcess().getProcessName());
        orderDetailVo.setClassId(tBusOrderHead.getClassId() == null ? null : tBusOrderHead.getClassId().getClassId());
        orderDetailVo.setClassName(tBusOrderHead.getClassId() == null ? "" : tBusOrderHead.getClassId().getName());
        orderDetailVo.setPersonId(tBusOrderHead.getCurrentPersonId() == null ? null : tBusOrderHead.getCurrentPersonId().getPersonnelId());
        orderDetailVo.setPersonName(tBusOrderHead.getCurrentPersonId() == null ? "" : tBusOrderHead.getCurrentPersonId().getName());
        orderDetailVo.setPersionName(tBusOrderHead.getCurrentPersonId() == null ? "" : tBusOrderHead.getCurrentPersonId().getName());
        orderDetailVo.setOrderFinishDate(Utils.formatDateTimeToString(tBusOrderHead.getOrderFinishDate()));//完工时间
        orderDetailVo.setOrderPendingDate(Utils.formatDateTimeToString(tBusOrderHead.getOrderPendingDate()));//挂起时间
        //预期产量默认等于订单明细的计划生产数量
        orderDetailVo.setBillPlanQty(tBusOrderHead.getBodyPlanPrdQty());
        orderDetailVo.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", tBusOrderHead.getBodyUnit()));
        if (tBusOrderHead.getTBusOrderProcessSet().size() > 0) {
            orderDetailVo.setHasProcessDtl(1);
        }
//        var orderProcessSet = tBusOrderHead.getTBusOrderProcessSet();
//        if (orderProcessSet.stream().filter(s -> "2".equals(s.getType() == null ? "" : s.getType())).count() > 0) {
//            orderDetailVo.setHasTransferDtl(1);
//        }

        //订单详情用料清单信息
        var orderPPBomSet = tBusOrderHead.getTBusOrderPPBomSet();
        orderDetailPPBomVoList = new ArrayList<>();
        for (var orderPPBom : orderPPBomSet) {
            orderDetailPPBomVo = new OrderDetailPPBomVo();
            BeanUtils.copyProperties(orderPPBom, orderDetailPPBomVo);
            orderDetailPPBomVo.setUnitStr(GlobalConstant.getCodeDscName("UNIT0000", orderPPBom.getUnit()));
            if (orderDetailPPBomVo.getMidPpbomEntryItemType() == null) {
            } else if (orderDetailPPBomVo.getMidPpbomEntryItemType() == 1) {
                orderDetailPPBomVo.setMidPpbomEntryItemTypeStr(ORDER_PPBOM_ITEM_TYPE_NAME_1);
            } else if (orderDetailPPBomVo.getMidPpbomEntryItemType() == 2) {
                orderDetailPPBomVo.setMidPpbomEntryItemTypeStr(ORDER_PPBOM_ITEM_TYPE_NAME_2);
            } else if (orderDetailPPBomVo.getMidPpbomEntryItemType() == 3) {
                orderDetailPPBomVo.setMidPpbomEntryItemTypeStr(ORDER_PPBOM_ITEM_TYPE_NAME_3);
            }
            orderDetailPPBomVoList.add(orderDetailPPBomVo);
        }
        orderDetailVo.setOrderDetailPPBomVoList(orderDetailPPBomVoList);
        List<TBusOrderUpdate> byOrderId = orderUpdateRepository.findByOrderId(orderId);
        orderDetailVo.setIsUpdate((byOrderId != null && byOrderId.size() > 0) ? 1 : 0);
        List<OrderTransferVo> appProcessTBusOrderTransfer = getAppProcessTBusOrderTransfer(orderId, orderProcessId);
        orderDetailVo.setHasTransferDtl(appProcessTBusOrderTransfer.size() == 0 ? 0 : 1);
        return orderDetailVo;
    }

    @Override
    public List<OrderProcessVo> getTBusOrderProcess(Integer orderId) {
        List<OrderProcessVo> orderProcessVoList;
        HashMap<Integer, OrderProcessVo> orderProcessVoMap;//KEY=ProcessSeq  VALUE=OrderProcessVo
        HashMap<Integer, List<OrderProcessRecordDtlVo>> orderProcessRdMap;//KEY=ProcessSeq  VALUE=List<OrderProcessRecordDtlVo>
        OrderProcessVo orderProcessVo;
        List<OrderProcessRecordDtlVo> orderProcessRecordDtlVoList;
        var tBusOrderHead = orderHeadRepository.findById(orderId).get();
        var orderProcessSet = tBusOrderHead.getTBusOrderProcessSet();
        orderProcessVoList = new ArrayList<>();
        orderProcessVoMap = new HashMap<>();
        orderProcessRdMap = new HashMap<>();
        for (var orderProcess : orderProcessSet) {
//            //工序执行表记录为移交订单，且工序执行表ID为其他记录的移交的订单工序ID，跳过
//            if ("2".equals(orderProcess.getType())) {
//                if (orderProcessSet.stream().filter(s -> s.getOldOrderProcessId() != null && s.getOldOrderProcessId().intValue() == orderProcess.getOrderProcessId().intValue()).count() > 0) {
//                    continue;
//                }
//            }
            //工序执行表(按工序)
            orderProcessVo = new OrderProcessVo();
            BeanUtils.copyProperties(orderProcess, orderProcessVo);
            orderProcessVo.setProcessName(orderProcess.getProcessId() == null ? "" : orderProcess.getProcessId().getProcessName());
            orderProcessVo.setProcessNumber(orderProcess.getProcessId() == null ? "" : orderProcess.getProcessId().getProcessNumber());
            orderProcessVo.setPersonName(orderProcess.getPersonId() == null ? "" : orderProcess.getPersonId().getName());
            orderProcessVo.setSuspendReason(GlobalConstant.getCodeDscName("SUSPENDREASON0000", orderProcess.getSuspendReason()));
            //完成时间：工序组长工序完成确认时间
            orderProcessVo.setFinishTime(Utils.formatDateTimeToString(orderProcess.getFinishTime()));
            orderProcessVo.setReceiveTime(Utils.formatDateTimeToString(orderProcess.getReceiveTime()));
            //报工记录
            OrderProcessRecordDtlVo orderProcessRecordDtlVo = getOrderProcessRecordDtlVo(orderProcessVo, tBusOrderHead, orderProcess);
            if (orderProcessRdMap.containsKey(orderProcess.getProcessSeq())) {
                orderProcessRecordDtlVoList = orderProcessRdMap.get(orderProcess.getProcessSeq());
                orderProcessRecordDtlVoList.add(orderProcessRecordDtlVo);
            } else {
                orderProcessRecordDtlVoList = new ArrayList<>();
                orderProcessRecordDtlVoList.add(orderProcessRecordDtlVo);
                orderProcessRdMap.put(orderProcess.getProcessSeq(), orderProcessRecordDtlVoList);
            }
            if (orderProcessVoMap.containsKey(orderProcess.getProcessSeq())) {
                continue;
            } else {
                orderProcessVoList.add(orderProcessVo);
                orderProcessVoMap.put(orderProcess.getProcessSeq(), orderProcessVo);
            }
            orderProcessVo.setBodyPlanPrdQty(tBusOrderHead.getBodyPlanPrdQty());
            orderProcessVo.setBodyUnit(tBusOrderHead.getBodyUnit());
            orderProcessVo.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", tBusOrderHead.getBodyUnit()));
        }
        //添加报工记录集合
        for (var orderProcessVo2 : orderProcessVoList) {
            if (orderProcessRdMap.containsKey(orderProcessVo2.getProcessSeq())) {
                orderProcessRecordDtlVoList = orderProcessRdMap.get(orderProcessVo2.getProcessSeq());
                orderProcessVo2.setOrderProcessRecordDtlVoList(orderProcessRecordDtlVoList);
            }
        }
        orderProcessVoList.sort(Comparator.comparing(OrderProcessVo::getProcessSeq));
        return orderProcessVoList;
    }

    @NotNull
    private OrderProcessRecordDtlVo getOrderProcessRecordDtlVo(OrderProcessVo orderProcessVo, TBusOrderHead tBusOrderHead, TBusOrderProcess orderProcess) {
        //工序实际产量
        var bodyUnit = tBusOrderHead.getBodyUnit();
        BigDecimal bodyPlanPrdQty = tBusOrderHead.getBodyPlanPrdQty();
        BigDecimal realPrdQty1BD = new BigDecimal(0);//原辅料数量(累计)
//        float realPrdQty1 = 0f;//原辅料数量(累计)
        String recordUnit1 = "";//原辅料报工单位
        String recordUnitStr1 = "";//原辅料报工单位
        BigDecimal realPrdQty2 = BigDecimal.ZERO;//二级品数量
        String recordUnit2 = "";//二级品单位
        String recordUnitStr2 = "";//二级品单位
        BigDecimal realPrdQty3 = BigDecimal.ZERO;//产后数量
        String recordUnit3 = "";//产后单位
        String recordUnitStr3 = "";//产后单位
        float capacityQty = 0f;//产能数量
        String capacityUnit = "";//产能单位
        String capacityUnitStr = "";//产能单位
        BigDecimal productManualQty = BigDecimal.ZERO;//产后数量（手动输入）
        String productManualUnit = "";//产后单位（手动输入）
        String productManualUnitStr = "";//产后单位（手动输入）
        //原辅材料除了包装工序是非重量单位，其他工序都是，所以包装是不同展示原辅料这个的数量
        float reportLsmQty = 0f;//拉伸膜重量
        String reportLsmUnit = "";//拉伸膜重量单位
        String reportLsmUnitStr = "";//拉伸膜重量单位
        float reportFmQty = 0f;//废膜重量
        String reportFmUnit = "";//废膜重量单位
        String reportFmUnitStr = "";//废膜重量单位
        float reportSymQty = 0f;//剩余膜重量
        String reportSymUnit = "";//剩余膜重量单位
        String reportSymUnitStr = "";//剩余膜重量单位
        BigDecimal ungradedWasteQty = BigDecimal.ZERO;//废品累计数
        String ungradedWasteUnit = "";//废品累计数单位
        String ungradedWasteUnitStr = "";//废品累计数单位

        String personName = "";//处理人，就是组长
        Integer processId = orderProcess.getProcessId() == null ? 0 : orderProcess.getProcessId().getProcessId();
        String processName = orderProcess.getProcessId() == null ? "" : orderProcess.getProcessId().getProcessName();
        String processNumber = orderProcess.getProcessId() == null ? "" : orderProcess.getProcessId().getProcessNumber();

        List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(orderProcess.getOrderProcessId(), "BG");
        for (var orderProcessRecord : orderProcessRecordList) {
            String recordUnitName = GlobalConstant.getCodeDscName("UNIT0000", orderProcessRecord.getRecordUnit());
            if (orderProcessRecord.getRecordType() == null) {
            } else if (orderProcessRecord.getRecordType().equals("1")) {//原辅料报工
//                realPrdQty1 += orderProcessRecord.getRecordQty();//累计不同原辅料
                realPrdQty1BD = realPrdQty1BD.add(orderProcessRecord.getRecordQty());
                recordUnit1 = orderProcessRecord.getRecordUnit();
                recordUnitStr1 = recordUnitName;
                //拉伸膜重量，按工序为拉伸膜，原辅料数量合计（同rowQty）
                //废膜重量，按工序为拉伸膜，类目编码为原辅料，二级类目编码为废膜
                //剩余膜重量，按工序为拉伸膜，类目编码为原辅料，二级类目编码为剩余膜
                //工序为拉伸膜，工序编码为GX9003832
                if (orderProcessRecord.getProcessName().isEmpty()) {
                } else if (PROCESS_NUMBER_LASHENMO.equals(orderProcessRecord.getProcessName())) {
                    reportLsmQty = realPrdQty1BD.floatValue();
                    reportLsmUnit = recordUnit1;
                    reportLsmUnitStr = recordUnitStr1;
                    //二级类目类型（二级类目编码）:1=废膜，2=剩余膜、3=袋装，4=桶装
                    if (orderProcessRecord.getRecordTypeL2() == null) {
                    } else if (orderProcessRecord.getRecordTypeL2().equals("1")) {
                        reportFmQty = orderProcessRecord.getRecordQty().floatValue();
                        reportFmUnit = recordUnit1;
                        reportFmUnitStr = recordUnitStr1;
                    } else if (orderProcessRecord.getRecordTypeL2().equals("2")) {
                        reportSymQty = orderProcessRecord.getRecordQty().floatValue();
                        reportSymUnit = recordUnit1;
                        reportSymUnitStr = recordUnitStr1;
                    }
                }
            } else if (orderProcessRecord.getRecordType().equals("2")) {//二级品报工
                if (StringUtils.isNotEmpty(orderProcessRecord.getMaterialName())) {
                    if (RECORDTYPEL20001.equals(orderProcessRecord.getMaterialName()) || RECORDTYPEL20002.equals(orderProcessRecord.getMaterialName())) {
                        realPrdQty2 = realPrdQty2.add(orderProcessRecord.getRecordQty());
                        recordUnit2 = orderProcessRecord.getRecordUnit();
                        recordUnitStr2 = recordUnitName;
                    } else if (RECORDTYPEL20003.equals(orderProcessRecord.getMaterialName())) {
                        ungradedWasteQty = ungradedWasteQty.add(orderProcessRecord.getRecordQty());
                        ungradedWasteUnit = orderProcessRecord.getRecordUnit();
                        ungradedWasteUnitStr = recordUnitName;
                    }
                }
            } else if (orderProcessRecord.getRecordType().equals("3")) {//产后报工
                //工序实际产量:从"报工/盘点结果表"获取工序产后重量。
                realPrdQty3 = realPrdQty3.add(orderProcessRecord.getRecordQty());
                recordUnit3 = orderProcessRecord.getRecordUnit();
                recordUnitStr3 = recordUnitName;
                orderProcessVo.setUnFinishQty(bodyPlanPrdQty.subtract(realPrdQty3).floatValue());//未完成数量：计划生产数量-工序实际产量
                orderProcessVo.setUnFinishUnit(bodyUnit);//未完成单位
                orderProcessVo.setUnFinishUnitStr(GlobalConstant.getCodeDscName("UNIT0000", bodyUnit));//未完成单位
                if (orderProcessRecord.getCapacityQty() != null) {
                    capacityQty = orderProcessRecord.getCapacityQty();
                    capacityUnit = orderProcessRecord.getCapacityUnit();
                    capacityUnitStr = GlobalConstant.getCodeDscName("UNIT0000", orderProcessRecord.getCapacityUnit());
                }
                if (orderProcessRecord.getRecordManualQty() != null) {
                    productManualQty = productManualQty.add(orderProcessRecord.getRecordManualQty());
                    productManualUnit = orderProcessRecord.getRecordUnit();
                    productManualUnitStr = GlobalConstant.getCodeDscName("UNIT0000", orderProcessRecord.getRecordUnit());
                }
            }
            if (StringUtils.isEmpty(personName)) {
                personName = orderProcessRecord.getPersonId() == null ? "" : orderProcessRecord.getPersonId().getName();
            }
        }
        //工序执行表(按班别)set
        OrderProcessRecordDtlVo orderProcessRecordDtlVo = new OrderProcessRecordDtlVo();
        orderProcessRecordDtlVo.setClassName(orderProcess.getClassId() == null ? "" : orderProcess.getClassId().getName());
        orderProcessRecordDtlVo.setPersonName(personName);
        //同工序，移交后，工序执行信息就会有两条，批次是一个，因为同一个订单
        orderProcessRecordDtlVo.setBodyLot(tBusOrderHead.getBodyLot());//不同班组,是否同个班别
        //汇总原辅料的数量
        //原辅材料重量，对应工序：斩拌||拌料||蟹柳
        if (PROCESS_NUMBER_ZHANBAN.equals(processNumber) || PROCESS_NUMBER_BANLIAO.equals(processNumber) || PROCESS_NUMBER_XIELIU.equals(processNumber)) {
            orderProcessRecordDtlVo.setRowQty(realPrdQty1BD.floatValue());
            orderProcessRecordDtlVo.setRowUnit(recordUnit1);
            orderProcessRecordDtlVo.setRowUnitStr(recordUnitStr1);
        } else {
            orderProcessRecordDtlVo.setRowQty(-1f);
        }
        //二级品数量
        orderProcessRecordDtlVo.setUngradedQty(realPrdQty2.floatValue());
        orderProcessRecordDtlVo.setUngradedUnit(recordUnit2);
        orderProcessRecordDtlVo.setUngradedUnitStr(recordUnitStr2);
        //产后数量
        orderProcessRecordDtlVo.setProductQty(realPrdQty3.floatValue());
        orderProcessRecordDtlVo.setProductUnit(recordUnit3);
        orderProcessRecordDtlVo.setProductUnitStr(recordUnitStr3);
        //产后数量（手动输入）
        if (PROCESS_NUMBER_LASHENMO.equals(processNumber) || PROCESS_NUMBER_BAOZHUANG.equals(processNumber)) {//拉伸膜、包装
            orderProcessRecordDtlVo.setProductManualQty(productManualQty.floatValue());
            orderProcessRecordDtlVo.setProductManualUnit(productManualUnit);
            orderProcessRecordDtlVo.setProductManualUnitStr(productManualUnitStr);
        } else {
            orderProcessRecordDtlVo.setProductManualQty(-1f);
        }
        /* 按工序来汇总的字段，不按移交的班别 */
        //AB料累计数、废品累计数（二级品数量）,按工序来汇总
        orderProcessVo.setUngradedAbQty(BigDecimalUtil.add(realPrdQty2.doubleValue(), orderProcessVo.getUngradedAbQty()).floatValue());
        orderProcessVo.setUngradedAbUnit(recordUnit2);
        orderProcessVo.setUngradedAbUnitStr(recordUnitStr2);
        orderProcessVo.setUngradedWasteQty(BigDecimalUtil.add(ungradedWasteQty.doubleValue(), orderProcessVo.getUngradedWasteQty()).floatValue());
        orderProcessVo.setUngradedWasteUnit(ungradedWasteUnit);
        orderProcessVo.setUngradedWasteUnitStr(ungradedWasteUnitStr);
        orderProcessVo.setTotalProductQty(BigDecimalUtil.add(realPrdQty3.doubleValue(), orderProcessVo.getTotalProductQty()).floatValue());
        //合格完成率、总的完成率: 合格完成率=合格品产后报工数量/（合格品产后报工数量+二级品产后报工数量）、 总的完成率=合格品产后报工数量/计划生产数量--废弃
        //合格完成率=合格品产后报工数量/计划生产数量，保留两位小数，单位% 任务12816 2022-07-13
        //合格品完成率要先除以一个每件支数：合格完成率=合格品产后报工数量/每件支数/计划生产数量*100% 2023-04-17 锦江 --废弃
        //合格完成率=(((产后数量手输*单支克重)-AB料累计数）/单支克重/每件支数)/计划产量 2023-04-18 俊良 18681
        //友臣 注释报错
        /*MidMaterial bykdMaterialId = midMaterialRepository.getBykdMaterialId(tBusOrderHead.getBodyMaterialId());
        if (PROCESS_NUMBER_LASHENMO.equals(processNumber)) {
            if (bykdMaterialId != null && bykdMaterialId.getKdMaterialEachPieceNum() > 0 && bykdMaterialId.getKdMaterialStretchWeight() > 0) {
                Integer kdMaterialEachPieceNum = bykdMaterialId.getKdMaterialEachPieceNum();//每件支数
                Float kdMaterialStretchWeight = bykdMaterialId.getKdMaterialStretchWeight();//单支克重(克)
                Float kdMaterialStretchWeightKg = bykdMaterialId.getKdMaterialStretchWeight() / 1000;//单支克重(千克)
                var productRealQtyKg = productManualQty * kdMaterialStretchWeightKg - orderProcessVo.getUngradedAbQty();//(产后数量手输*单支克重)-AB料累计数
                var productRealQtyZhi = productRealQtyKg / kdMaterialStretchWeightKg / kdMaterialEachPieceNum.floatValue();//(((产后数量手输*单支克重)-AB料累计数）/单支克重/每件支数)
                var qualifiedRate = BigDecimalUtil.div(productRealQtyZhi * 100, tBusOrderHead.getBodyPlanPrdQty().floatValue()).floatValue();
                orderProcessVo.setQualifiedRate(qualifiedRate);
            } else {
                logger.warn("合格完成率计算失败，物料：" + tBusOrderHead.getBodyMaterialNumber() + "每件支数、单支克重为空或者为0");
            }
        } else {
            //合格完成率=合格品产后报工数量/计划生产数量
            orderProcessVo.setQualifiedRate(BigDecimalUtil.div(orderProcessVo.getTotalProductQty() * 100, tBusOrderHead.getBodyPlanPrdQty().floatValue()).floatValue());
        }*/
        //产能数量、产能单位，对应工序：剥皮||蟹柳||拉伸膜||包装
        //产能数量 = 单只克重（单只克重字段如果是克为单位需除以1000）*产后数量手输 2023-04-18 18681
        //友臣 注释报错
        /*if (PROCESS_NUMBER_BOPI.equals(processNumber) || PROCESS_NUMBER_XIELIU.equals(processNumber) || PROCESS_NUMBER_LASHENMO.equals(processNumber) || PROCESS_NUMBER_BAOZHUANG.equals(processNumber)) {
            if (bykdMaterialId != null && bykdMaterialId.getKdMaterialEachPieceNum() > 0 && bykdMaterialId.getKdMaterialStretchWeight() > 0) {
                Float kdMaterialStretchWeightKg = bykdMaterialId.getKdMaterialStretchWeight() / 1000;//单支克重(千克)
                orderProcessRecordDtlVo.setCapacityQty(BigDecimalUtil.mul(productManualQty, kdMaterialStretchWeightKg).floatValue());
            }
//            orderProcessRecordDtlVo.setCapacityQty(capacityQty);
            orderProcessRecordDtlVo.setCapacityUnit(capacityUnit);
            orderProcessRecordDtlVo.setCapacityUnitStr(capacityUnitStr);
        } else {
            orderProcessRecordDtlVo.setCapacityQty(-1f);
        }*/
        //特殊字段:拉伸膜重量、废膜重量、剩余膜重量
        if (PROCESS_NUMBER_LASHENMO.equals(processNumber)) {//拉伸膜
            orderProcessRecordDtlVo.setReportLsmQty(reportLsmQty);
            orderProcessRecordDtlVo.setReportLsmUnit(reportLsmUnit);
            orderProcessRecordDtlVo.setReportLsmUnitStr(reportLsmUnitStr);
            orderProcessRecordDtlVo.setReportFmQty(reportFmQty);
            orderProcessRecordDtlVo.setReportFmUnit(reportFmUnit);
            orderProcessRecordDtlVo.setReportFmUnitStr(reportFmUnitStr);
            orderProcessRecordDtlVo.setReportSymQty(reportSymQty);
            orderProcessRecordDtlVo.setReportSymUnit(reportSymUnit);
            orderProcessRecordDtlVo.setReportSymUnitStr(reportSymUnitStr);
        } else {
            orderProcessRecordDtlVo.setReportLsmQty(-1f);
            orderProcessRecordDtlVo.setReportFmQty(-1f);
            orderProcessRecordDtlVo.setReportSymQty(-1f);
        }
        if (PROCESS_NUMBER_ZHANBAN.equals(processNumber)) {//斩拌
            //投入锅数=原辅料累计投入重量合计/每锅重量，小数点保留两位。
            //改成按原辅料的投入次数来计算，接口获取
            orderProcessRecordDtlVo.setPutInPotQty(orderPPBomRepository.findExportPotByImportAll(tBusOrderHead.getOrderId(), LichengConstants.PROCESS_NUMBER_ZHANBAN, "-1", "-1"));
        } else {
            orderProcessRecordDtlVo.setPutInPotQty(-1);
        }
        return orderProcessRecordDtlVo;
    }

    @Override
    public List<OrderTransferVo> getAppProcessTBusOrderTransfer(Integer orderId, Integer orderProcessId) {
        List<OrderTransferVo> orderTransferVoList;
        OrderTransferVo orderTransferVo;
        List<OrderTransferRecordVo> orderTransferRecordVoList;
        List<OrderProcessHistoryVo> orderProcessHistoryVoList;
        OrderTransferRecordVo orderTransferRecordVo;
        OrderProcessHistoryVo orderProcessHistoryVo;
        //查工序执行表判断如果是类型为移交订单返回这个表的信息
        //TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(orderProcessId).orElse(null);
        var tBusOrderHead = orderHeadRepository.findById(orderId).get();
        var orderProcessSet = tBusOrderHead.getTBusOrderProcessSet();
        List<TBusOrderProcess> orderProcessList = new ArrayList<>(orderProcessSet);
        List<TBusOrderProcess> orderProcessListEnd = new ArrayList<>();
        //

        orderProcessList.sort(Comparator.comparing(TBusOrderProcess::getOrderProcessId));
        //orderProcessList.sort((o1, o2) -> o2.getOrderProcessId().compareTo(o1.getOrderProcessId()));
        orderTransferVoList = new ArrayList<>();
        //boolean transferFlag = false;//是否存在移交记录
        for (var orderProcess2 : orderProcessList) {
            //1.通过移交前的ID获取移交记录:判断OldOrderProcessId为入参id,且type为移交订单
            if (orderProcessId != 0 && orderProcessId.intValue() == orderProcess2.getOldOrderProcessId().intValue() && orderProcess2.getType().equals(ORDER_PROCESS_TYPE_2)) {
                //
                orderProcessListEnd.add(orderProcess2);
            }
            //2.通过移交后的ID获取移交记录:判断OrderProcessId为入参id,且type为移交订单
            if (orderProcessId != 0 && orderProcessId.intValue() == orderProcess2.getOrderProcessId().intValue() && orderProcess2.getType().equals(ORDER_PROCESS_TYPE_2)) {
                //
                orderProcessListEnd.add(orderProcess2);
            }
        }
        //类型，包括：1=正常订单、2=移交订单 3=转移订单
        for (TBusOrderProcess orderProcess : orderProcessListEnd) {
            if (ORDER_PROCESS_TYPE_2.equals(orderProcess.getType())) {
                orderTransferVo = new OrderTransferVo();
                orderTransferVo.setBodyLot(tBusOrderHead.getBodyLot());
                orderTransferVo.setOrderNo(tBusOrderHead.getOrderNo());
                orderTransferVo.setClassId(orderProcess.getClassId());
                orderTransferVo.setClassName(orderProcess.getClassId() == null ? "" : orderProcess.getClassId().getName());
                orderTransferVo.setReportTime(Utils.formatDateTimeToString(orderProcess.getOldRecordTypePdTime()));//盘点时间=移交前的盘点时间
                orderTransferVo.setBodyMaterialId(tBusOrderHead.getBodyMaterialId());
                orderTransferVo.setBodyMaterialNumber(tBusOrderHead.getBodyMaterialNumber());
                orderTransferVo.setBodyMaterialName(tBusOrderHead.getBodyMaterialName());
                orderTransferVo.setPrdQty(tBusOrderHead.getBodyPlanPrdQty());
                orderTransferVo.setUnit(tBusOrderHead.getBodyUnit());
                orderTransferVo.setUnitStr(GlobalConstant.getCodeDscName("UNIT0000", tBusOrderHead.getBodyUnit()));
                orderTransferVo.setPersonName(orderProcess.getPersonId() == null ? "" : orderProcess.getPersonId().getName());//接收人||处理人
                orderTransferVo.setHandOverPersonName(orderProcess.getOldHandOverPerSonId() == null ? "" : orderProcess.getOldHandOverPerSonId().getName().toString());//移交人=移交前的移交人
                orderTransferVo.setTransferDate(Utils.formatDateTimeToString(orderProcess.getOldHandOverTime()));//移交时间=移交前的移交人
                orderTransferVo.setRecordTypePd(orderProcess.getOldRecordTypePd());
                orderTransferVo.setRecordTypePdName(GlobalConstant.getCodeDscName("STOCKTAKING0000", orderProcess.getOldRecordTypePd()));
                //报工结果表
                float realPrdQty1 = 0l;//原辅料数量(累计)
                float realPrdQty2 = 0l;//二级品数量
                BigDecimal realPrdQty3 = BigDecimal.ZERO;//产后数量
                //原辅材料除了包装工序是非重量单位，其他工序都是，所以包装是不同展示原辅料这个的数量
                String recordUnit1 = "";//原辅料报工单位
                orderTransferRecordVoList = new ArrayList<>();
                //获取盘点历史数据
                //List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(orderProcess.getOldOrderProcessId(), "PD");
                List<TBusOrderProcessHistory> tBusOrderProcessHistoryList = orderProcessHistoryRepository.findAllByOrderProcessIdAndBusType(orderProcess.getOldOrderProcessId(), "PD");
                for (var orderProcessRecord : tBusOrderProcessHistoryList) {
                    //获取报工数据
                    //List<TBusOrderProcessRecord> orderProcessRecordListBG = orderProcessRecordRepository.findAllByOrderNoAndProcessIdAndClassIdAndBusType(tBusOrderHead.getOrderNo(),orderProcess.getProcessId(),orderProcess.getClassId(),"BG");
                    orderTransferRecordVo = new OrderTransferRecordVo();
                    orderTransferRecordVo.setMaterialId(orderProcessRecord.getMaterialId());
                    orderTransferRecordVo.setMaterialNumber(orderProcessRecord.getMaterialNumber());
                    orderTransferRecordVo.setMaterialName(orderProcessRecord.getMaterialName());
                    orderTransferRecordVo.setQty(orderProcessRecord.getRecordQty().floatValue());
                    orderTransferRecordVo.setUnit(orderProcessRecord.getRecordUnit());
                    orderTransferRecordVo.setUnitStr(GlobalConstant.getCodeDscName("UNIT0000", orderProcessRecord.getRecordUnit()));
                    orderTransferRecordVo.setReportTime(Utils.formatDateTimeToString(orderProcessRecord.getReportTime()));//盘点时间||报工时间
                    orderTransferRecordVoList.add(orderTransferRecordVo);

                }
                //按盘点时间（报工时间），倒序排列
                orderTransferRecordVoList.sort((o1, o2) -> o2.getReportTime().compareTo(o1.getReportTime()));
//                if (tBusOrderProcessHistoryList.size() != 0) {
//                    orderTransferVo.setRecordTypePd(tBusOrderProcessHistoryList.get(0).getRecordTypePd());
//                    orderTransferVo.setRecordTypePdName(GlobalConstant.getCodeDscName("STOCKTAKING0000", tBusOrderProcessHistoryList.get(0).getRecordTypePd()));
//                }
                List<TBusOrderProcessRecord> orderProcessRecordListBG = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(orderProcess.getOrderProcessId(), "BG");
                for (var orderProcessRecordBG : orderProcessRecordListBG) {
                    if (orderProcessRecordBG.getRecordType().equals("3")) {//产后报工
                        //工序实际产量:从"报工/盘点结果表"获取工序产后重量。
                        realPrdQty3 = orderProcessRecordBG.getRecordQty();
                    }
                }
                orderTransferVo.setActualQty(realPrdQty3);//实际产量=每道工序的产后报工数据
                orderTransferVo.setUnPrdQty(orderTransferVo.getPrdQty().subtract(orderTransferVo.getActualQty()));//未生产=预期产量-实际产量
                orderTransferVo.setOrderTransferRecordVoList(orderTransferRecordVoList);
                orderTransferVoList.add(orderTransferVo);
            }
        }

        return orderTransferVoList;
    }

    @Override
    public List<OrderTransferVo> getTBusOrderTransfer(Integer orderId) {
        List<OrderTransferVo> orderTransferVoList;
        OrderTransferVo orderTransferVo;
        List<OrderTransferRecordVo> orderTransferRecordVoList;
        List<OrderProcessHistoryVo> orderProcessHistoryVoList;
        OrderTransferRecordVo orderTransferRecordVo;
        OrderProcessHistoryVo orderProcessHistoryVo;
        var tBusOrderHead = orderHeadRepository.findById(orderId).get();
        var orderProcessSet = tBusOrderHead.getTBusOrderProcessSet();
        List<TBusOrderProcess> orderProcessList = new ArrayList<>(orderProcessSet);
        orderProcessList.sort(Comparator.comparing(TBusOrderProcess::getOrderProcessId));
        orderProcessList.sort((o1, o2) -> o2.getOrderProcessId().compareTo(o1.getOrderProcessId()));
        orderTransferVoList = new ArrayList<>();
        boolean transferFlag = false;//是否存在移交记录
        for (var orderProcess : orderProcessList) {
            orderTransferVo = new OrderTransferVo();
            orderTransferVo.setBodyLot(tBusOrderHead.getBodyLot());
            orderTransferVo.setOrderNo(tBusOrderHead.getOrderNo());
            orderTransferVo.setClassId(orderProcess.getClassId());
            orderTransferVo.setClassName(orderProcess.getClassId() == null ? "" : orderProcess.getClassId().getName());
            orderTransferVo.setReportTime(Utils.formatDateTimeToString(orderProcess.getReceiveTime()));
            orderTransferVo.setBodyMaterialId(tBusOrderHead.getBodyMaterialId());
            orderTransferVo.setBodyMaterialNumber(tBusOrderHead.getBodyMaterialNumber());
            orderTransferVo.setBodyMaterialName(tBusOrderHead.getBodyMaterialName());
            orderTransferVo.setPrdQty(tBusOrderHead.getBodyPlanPrdQty());
            orderTransferVo.setUnit(tBusOrderHead.getBodyUnit());
            orderTransferVo.setUnitStr(GlobalConstant.getCodeDscName("UNIT0000", tBusOrderHead.getBodyUnit()));
            orderTransferVo.setTransferDate(Utils.formatDateTimeToString(orderProcess.getFinishTime()));//移交时间=移交前的结束时间
            TSysPersonnelInfo tSysPersonnelInfo = orderProcess.getPersonId();
            orderTransferVo.setPersonName(tSysPersonnelInfo == null ? "" : tSysPersonnelInfo.getName());//接收人||处理人

            TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(orderProcess.getOldOrderProcessId()).orElse(null);
            if (tBusOrderProcess != null) {
                tSysPersonnelInfo = tBusOrderProcess.getHandOverPerSonId();
                orderTransferVo.setHandOverPersonName(tSysPersonnelInfo == null ? "" : tSysPersonnelInfo.getName().toString());//移交人
            }
            //报工结果表
            float realPrdQty1 = 0l;//原辅料数量(累计)
            float realPrdQty2 = 0l;//二级品数量
            float realPrdQty3 = 0l;//产后数量
            //原辅材料除了包装工序是非重量单位，其他工序都是，所以包装是不同展示原辅料这个的数量
            String recordUnit1 = "";//原辅料报工单位
            orderTransferRecordVoList = new ArrayList<>();
            //获取盘点数据
            List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(orderProcess.getOrderProcessId(), "PD");
            if (orderProcessRecordList.size() == 0) {
                continue;
            }
//                List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordRepository.findAllByOrderNoAndProcessIdAndClassIdAndBusType(tBusOrderHead.getOrderNo(),orderProcess.getProcessId(),orderProcess.getClassId(),"PD");
            for (var orderProcessRecord : orderProcessRecordList) {
                //获取报工数据
//                    List<TBusOrderProcessRecord> orderProcessRecordListBG = orderProcessRecordRepository.findAllByOrderNoAndProcessIdAndClassIdAndBusType(tBusOrderHead.getOrderNo(),orderProcess.getProcessId(),orderProcess.getClassId(),"BG");
                List<TBusOrderProcessRecord> orderProcessRecordListBG = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(orderProcess.getOrderProcessId(), "BG");
                for (var orderProcessRecordBG : orderProcessRecordListBG) {
                    if (orderProcessRecordBG.getRecordType().equals("3")) {//产后报工
                        //工序实际产量:从"报工/盘点结果表"获取工序产后重量。
                        realPrdQty3 = orderProcessRecordBG.getRecordQty().floatValue();
                    }
                }
                orderTransferRecordVo = new OrderTransferRecordVo();
                orderTransferRecordVo.setMaterialId(orderProcessRecord.getMaterialId());
                orderTransferRecordVo.setMaterialNumber(orderProcessRecord.getMaterialNumber());
                orderTransferRecordVo.setMaterialName(orderProcessRecord.getMaterialName());
                orderTransferRecordVo.setQty(orderProcessRecord.getRecordQty().floatValue());
                orderTransferRecordVo.setUnit(orderProcessRecord.getRecordUnit());
                orderTransferRecordVo.setUnitStr(GlobalConstant.getCodeDscName("UNIT0000", orderProcessRecord.getRecordUnit()));
                orderTransferRecordVo.setReportTime(Utils.formatDateTimeToString(orderProcessRecord.getReportTime()));//盘点时间||报工时间
                orderTransferRecordVoList.add(orderTransferRecordVo);

            }
            //倒序
            orderTransferRecordVoList.sort((o1, o2) -> o2.getReportTime().compareTo(o1.getReportTime()));
            orderTransferVo.setRecordTypePd(orderProcessRecordList.get(0).getRecordTypePd());
            orderTransferVo.setRecordTypePdName(GlobalConstant.getCodeDscName("STOCKTAKING0000", orderProcessRecordList.get(0).getRecordTypePd()));
            orderTransferVo.setActualQty(new BigDecimal(realPrdQty3));//实际产量=每道工序的产后报工数据
            orderTransferVo.setUnPrdQty(orderTransferVo.getPrdQty().subtract(orderTransferVo.getActualQty()));//未生产=预期产量-实际产量
            orderTransferVo.setOrderTransferRecordVoList(orderTransferRecordVoList);
            orderTransferVoList.add(orderTransferVo);
        }
        return orderTransferVoList;
    }

    @Transactional
    @Override
    public void updateBillStatus(TBusOrderHead orderHead) {
        if (orderHead.getOrderId() == null) {
            throw new RuntimeException("订单id不能为空");
        }
        if (StringUtils.isBlank(orderHead.getOrderStatus())) {
            throw new RuntimeException("订单状态不能为空");
        }
        TBusOrderHead tBusOrderHeadRt = orderHeadRepository.getOne(orderHead.getOrderId());
        if (StringUtils.isEmpty(tBusOrderHeadRt.getBillNo())) {
            throw new RuntimeException("订单不存在");
        }
        orderHeadRepository.updateBillStatus(orderHead.getOrderId(), orderHead.getOrderStatus(), orderHead.getUpdatedName(), orderHead.getUpdatedTime());
    }

    @Override
    public void deleteTBusOrderHead(Integer orderHeadId) {
        if (orderHeadId == null) {
            throw new RuntimeException("订单id不能为空");
        }
        TBusOrderHead tBusOrderHead = orderHeadRepository.findById(orderHeadId).orElse(null);
        tBusOrderHead.setIsDeleted(GlobalConstant.enableTrue);
        orderHeadRepository.saveAndFlush(tBusOrderHead);
    }

    @Override
    public TBusOrderUpdate listOrderUpdate(Integer id) {
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "createdTime");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "id");
        orders.add(order1);
        orders.add(order2);
        Sort sort = Sort.by(orders);
        PageRequest pageRequest = PageRequest.of(0, 1, sort);
        TBusOrderUpdate tBusOrderUpdate = new TBusOrderUpdate();
        tBusOrderUpdate.setOrderId(id);
        Example<TBusOrderUpdate> example = Example.of(tBusOrderUpdate);
        Page<TBusOrderUpdate> tBusOrderUpdates = orderUpdateRepository.findAll(example, pageRequest);
        TBusOrderUpdate busOrderUpdate = null;
        if (tBusOrderUpdates != null && tBusOrderUpdates.getSize() == 1) {
            List<TBusOrderUpdate> content = tBusOrderUpdates.getContent();
            if (content == null || content.size() == 0) {
                throw new RuntimeException("查询的订单没有变更信息");
            }
            busOrderUpdate = content.get(0);
            String ppbomids = busOrderUpdate.getPpbomIds();
            if (StringUtils.isNotBlank(ppbomids)) {
                List<Integer> ids = JSON.parseArray(ppbomids, Integer.class);
                List<TBusOrderPPBom> byIds = orderPPBomRepository.findByIds(ids);
                byIds.stream().forEach(tBusOrderPPBom -> {
                    tBusOrderPPBom.setUnitName(GlobalConstant.getCodeDscName("UNIT0000", tBusOrderPPBom.getUnit()));
                });
                busOrderUpdate.setTBusOrderPPBomList(byIds);
            }
            busOrderUpdate.setTBusOrderHead(orderHeadRepository.findById(busOrderUpdate.getOrderId()).get());
            if (StringUtils.isNotEmpty(busOrderUpdate.getOrderJson())) {
                TBusOrderHead tBusOrderHeadOld = JSON.parseObject(busOrderUpdate.getOrderJson(), TBusOrderHead.class);
                busOrderUpdate.setBodyPlanPrdOldQty(tBusOrderHeadOld.getBodyPlanPrdQty());
            }
        }

        return busOrderUpdate;
    }

    @Override
    public void verifyPot(Integer orderId, Integer orderProcessId) {
        //2022-09-08 暂时取消锅数控制
//        TBusOrderHead tBusOrderHead = orderHeadRepository.findById(orderId).get();
//        int pot = tBusOrderHead.getBodyPotQty() == null ? 0 : tBusOrderHead.getBodyPotQty();
//        float exportPot = orderPPBomRepository.findExportPotByOne(orderProcessId, "-1", "-1");
//        if (pot != exportPot) {
//            throw new RuntimeException("产出锅数不等于计划锅数，请进行变更订单！\n产出锅数：" + exportPot + "锅  计划锅数：" + pot + "锅");
//        }
    }

    @Override
    public void sendMes(TBusOrderHead byOrderNo, String status) {
        Set<TBusOrderProcess> tBusOrderProcessSet = byOrderNo.getTBusOrderProcessSet();
        if (tBusOrderProcessSet == null || tBusOrderProcessSet.size() == 0) {
            return;
        }
        TSysMessageOrder tSysMessageOrder;
        List<TSysMessageOrder> tSysMessageOrders = new ArrayList<>();
        for (TBusOrderProcess tBusOrderProcess : tBusOrderProcessSet) {
            if (tBusOrderProcess == null || tBusOrderProcess.getPersonId() == null || org.apache.commons.lang3.StringUtils.isBlank(tBusOrderProcess.getPersonId().getUserId())) {
                continue;
            }
            tSysMessageOrder = new TSysMessageOrder();
            tSysMessageOrder.setOrderId(byOrderNo.getOrderId());
            tSysMessageOrder.setOrderNo(byOrderNo.getOrderNo());
            tSysMessageOrder.setCreatedTime(byOrderNo.getBillDate());
            tSysMessageOrder.setProductStandard(byOrderNo.getMaterialName());
            tSysMessageOrder.setBillPlanQty(String.valueOf(byOrderNo.getBodyPlanPrdQty()));
            tSysMessageOrder.setMesType(status);
            tSysMessageOrder.setStatusType("1");
            tSysMessageOrder.setIsRead("0");
            tSysMessageOrder.setMesTime(new Date());
            //用户id
            tSysMessageOrder.setUserId(tBusOrderProcess.getPersonId().getUserId());
            tSysMessageOrder.setProductStandard(byOrderNo.getBodyMaterialName());
            //订单类型
            tSysMessageOrders.add(tSysMessageOrder);
        }
        messageOrderRepository.saveAll(tSysMessageOrders);
    }

    @Transactional
    @Override
    public ResponseResult suspendProcess(Integer orderId, Integer orderProcessId, String suspendReason) {
        // 通过订单Id获取订单当前工序
        TBusOrderProcess tBusOrderProcess = orderProcessService.findById(orderProcessId);
        if (null == tBusOrderProcess) {
            return ResultUtil.error("工序执行信息错误：无法获取到订单工序执行的信息，无法进行操作！");
        }
        //工序暂停	对工序进行暂停变更，暂停操作后，订单状态变更为"暂停"	订单状态为"已开工"状态，才能进行暂停操作。
        if ("1".equals(tBusOrderProcess.getProcessStatus())) {
            if (suspendReason.isEmpty()) {
                return ResultUtil.error("暂停原因错误：暂停原因不能为空，无法进行操作！");
            }
            //获取字典对象：暂停原因
            TSysCodeDsc codeDscSr = sysCodeDscService.getCodeByCodeClAndCodeVale("SUSPENDREASON0000", suspendReason);
            if (null == codeDscSr) {
                return ResultUtil.error("暂停原因错误：没有获取到请求的暂停原因，无法进行操作！");
            }
            tBusOrderProcess.setProcessStatus("2");
            tBusOrderProcess.setSuspendReason(suspendReason);
            orderProcessService.saveTBusOrderProcess(tBusOrderProcess);
            //订单状态更新为暂停
            TBusOrderHead orderHead = orderHeadRepository.findById(orderId).orElse(null);
            if (orderHead.getOrderStatus().equals("4")) {
                throw new RuntimeException("订单已挂起，禁止操作！");
            }
            orderHead.setOrderStatus("2");//暂停
            orderHeadRepository.saveAndFlush(orderHead);
            this.sendMes(orderHead, "2");
            return ResultUtil.success();
        } else {
            return ResultUtil.error("工序状态错误：当前工序状态不是已开工，无法进行操作！");
        }
    }

    @Transactional
    @Override
    public ResponseResult recoverProcess(Integer orderId, Integer orderProcessId) {
        // 通过订单Id获取订单当前工序
        TBusOrderProcess tBusOrderProcess = orderProcessService.findById(orderProcessId);
        if (null == tBusOrderProcess) {
            return ResultUtil.error("工序执行信息错误：无法获取到订单工序执行的信息，无法进行操作！");
        }
        //工序恢复	对工序进行恢复变更，恢复操作后，订单状态变更为"已开工"	订单状态为"暂停"状态，才能进行恢复操作。
        if ("2".equals(tBusOrderProcess.getProcessStatus())) {
            tBusOrderProcess.setProcessStatus("1");
            orderProcessService.saveTBusOrderProcess(tBusOrderProcess);
            //订单状态更新为已开工
            TBusOrderHead orderHead = orderHeadRepository.findById(orderId).orElse(null);
            if (orderHead.getOrderStatus().equals("4")) {
                throw new RuntimeException("订单已挂起，禁止操作！");
            }
            var orderProcessSet = orderHead.getTBusOrderProcessSet();
            if (orderProcessSet.stream().filter(s -> "2".equals(s.getProcessStatus())).count() == 0) {
                orderHead.setOrderStatus("1");//已开工
                orderHeadRepository.saveAndFlush(orderHead);
                this.sendMes(orderHead, "3");
            }
            return ResultUtil.success();
        } else {
            return ResultUtil.error("工序状态错误：当前工序状态不是暂停，无法进行操作！");
        }
    }

    @Transactional
    @Override
    public synchronized ResponseResult stopProcess(Integer orderId, Integer orderProcessId) throws Exception {

        // 通过订单Id获取订单当前工序
        TBusOrderProcess tBusOrderProcess = orderProcessService.findById(orderProcessId);
        if (tBusOrderProcess.getProcessStatus().equals(PROCESSSTATUS_3)) {
            throw new RuntimeException("当前记录已被工序结束");
        }
        TSysProcessInfo processId = tBusOrderProcess.getProcessId();
        //工序结束判断是否有数据没有产后报工
        if(processId.getReportType().contains("3")){
            verExportQty(orderId,processId);
        }

        String bySetExport = processId.getBySetExport();
        if (bySetExport.equals(GlobalConstant.enableTrue)) {
            List<TBusOrderProcessRecord> tBusOrderProcessRecords = orderProcessRecordRepository.findByExport(tBusOrderProcess.getOrderProcessId());
            if (tBusOrderProcessRecords.isEmpty()) {
                throw new RuntimeException("请先进行产成品产出报工！");
            }
        }
        if (null == tBusOrderProcess) {
            return ResultUtil.error("工序执行信息错误：无法获取到订单工序执行的信息，无法进行操作！");
        }
        //工序结束	对工序进行结束确认，系统记录结束时间点。	操作限制：前道工序确认结束后，本道工序才能进行结束确认。
        //否则弹层提示："前道工序还未结束，不允许进行工序结束操作。"
        if (tBusOrderProcess.getProcessSeq() > 1) {
            TBusOrderProcess tBusOrderProcessPrev = orderProcessService.getProcessPrevByOrderId(orderId, orderProcessId);
            if (null == tBusOrderProcessPrev) {
                if (!"3".equals(tBusOrderProcessPrev.getProcessStatus())) {
                    return ResultUtil.error("工序校验错误：前道工序确认结束后，本道工序才能进行结束确认！");
                }
            }
        }
        if (PROCESSSTATUS_0.equals(tBusOrderProcess.getProcessStatus())) {
            return ResultUtil.error("工序校验错误：当前工单还未开工，不允许工序结束操作。");
        }
        if (!PROCESSSTATUS_3.equals(tBusOrderProcess.getProcessStatus())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date startTime = tBusOrderProcess.getReceiveTime();
            if (null == startTime) {
                return ResultUtil.error("工序校验错误：工序需要有接单时间，本道工序才能进行结束确认！");
            }
            Date finishTime = new Date();
            String startTimeStr = sdf.format(startTime);
            String finishTimeStr = sdf.format(finishTime);
            tBusOrderProcess.setProcessStatus("3");//已完工
            tBusOrderProcess.setFinishTime(new Timestamp(finishTime.getTime()));
            Integer elapsedTime = Utils.getDifferHour(finishTime, startTime);
            tBusOrderProcess.setElapsedTime(elapsedTime);//耗时（小时）=完工时间-接单时间
            //报工结果表，处理产能数量和单位
            //根据机台号、工序接单时间、工序结束时间，调用IOT平台接口，获取对应蟹柳机的产量（支数）
            String deviceCode = "";
            String processNumber = tBusOrderProcess.getProcessId() == null ? "" : tBusOrderProcess.getProcessId().getProcessNumber();
            if (LichengConstants.PROCESS_NUMBER_ZHANBAN.equals(processNumber)) {
                //斩拌工序验证锅数
                this.verifyPot(orderId, orderProcessId);
            }

            //工序执行表对应的产后报工记录，获取保存IOT数采的产能数量
            saveOrderProcessDeviceIotValue(orderProcessId, startTimeStr, finishTimeStr, processNumber);

            orderProcessService.saveTBusOrderProcess(tBusOrderProcess);
            //订单状态更新为已完工
            //工序结束：工序执行表全部记录都已完工，再去更新订单状态，更新为"已完工"；工序执行表"工序状态"更新为"已完工"
            TBusOrderHead orderHead = orderHeadRepository.findById(orderId).orElse(null);
            if (orderHead.getOrderStatus().equals("4")) {
                throw new RuntimeException("订单已挂起，禁止操作！");
            }
            TSysCraftInfo tSysCraftInfo = orderHead.getCraftId();
            TSysCraftInfoSaveDto craftInfoSaveDto = craftInfoService.detail(tSysCraftInfo.getCraftId());
            List<ProcessInfoDto> processList = craftInfoSaveDto.getProcessInfos();
            Set<TBusOrderProcess> orderProcessSet = orderHead.getTBusOrderProcessSet();
            if (orderProcessSet.stream().filter(s -> !"3".equals(s.getProcessStatus())).count() == 0) {
//            if (orderProcessSet.stream().filter(s -> "3".equals(s.getProcessStatus())).count() == processList.size()){
                orderHead.setOrderStatus("3");//已完工
                orderHead.setOrderFinishDate(new Timestamp(finishTime.getTime()));//完工时间
                orderHeadRepository.saveAndFlush(orderHead);
            }
            return ResultUtil.success();
        } else {
            return ResultUtil.error("工序状态错误：当前工序状态为已完工，无法进行操作！");
        }
    }

    /**
     * 工序结束---判断是否有产后数据没报工
     *
     * @param orderId
     * @param processId
     */
    private void verExportQty(Integer orderId, TSysProcessInfo processId) {
        //获取积累投入数据
        Float importVal = orderPPBomRepository.sumImportPotQty(orderId, processId.getProcessNumber());
        //积累产出数据
        Float exportVal = orderPPBomRepository.sumExportPotQtyAllByBL(orderId, processId.getProcessNumber());
        if (importVal > exportVal) {
            throw new RuntimeException("不允许工序结束，需提交完产成品数量才允许工序结束");
        }
    }

    //工序执行表对应的产后报工记录，获取保存IOT数采的产能数量
    private void saveOrderProcessDeviceIotValue(Integer orderProcessId, String startTimeStr, String finishTimeStr, String processNumber) {
        if (PROCESS_NUMBER_XIELIU.equals(processNumber) || PROCESS_NUMBER_BOPI.equals(processNumber)) {//蟹柳工序、剝皮工序
            List<TBusOrderProcessRecord> orderProcessRecordSet = orderProcessRecordService.getBgOrderProcessRecords(orderProcessId, ORDER_RECORD_TYPE_3);
            for (var orderProcessRecordXL : orderProcessRecordSet) {
                if (StringUtils.isEmpty(orderProcessRecordXL.getDeviceGroupId())) continue;
                BigInteger iotQty = null;
                String deviceCode;
                IotDiffValueVo iotDiffValueVo = null;
                var orderProcessDeviceRels = orderProcessDeviceRelService.findByDeviceGroupId(orderProcessRecordXL.getDeviceGroupId());
                for (var orderProcessDeviceRel : orderProcessDeviceRels) {
                    var device = tSysDeviceRepository.findById(orderProcessDeviceRel.getDeviceId()).orElse(null);
                    if (null == device) continue;
                    deviceCode = device.getDeviceNumber();
                    try {
                        if (PROCESS_NUMBER_XIELIU.equals(processNumber)) {//蟹柳工序
                            iotDiffValueVo = orderProcessRecordService.getCrabLineRunNum(startTimeStr, finishTimeStr, deviceCode);
                        } else if (PROCESS_NUMBER_BOPI.equals(processNumber)) {//剝皮工序
                            iotDiffValueVo = orderProcessRecordService.getCuttingMachineRunNum(startTimeStr, finishTimeStr, deviceCode);
                        }
                    } catch (Exception e) {
                        iotDiffValueVo = null;
                        logger.error("获取设备发生异常", e);
                    }
                    if (null != iotDiffValueVo) {
                        orderProcessRecordXL.setCapacityQty(BigDecimalUtil.add(orderProcessRecordXL.getCapacityQty(), iotDiffValueVo.getDiffValue().floatValue()).floatValue());
                        orderProcessRecordXL.setCapacityUnit(UNIT_ZHI);//默认单位为支
                    }
                }
                if (orderProcessRecordXL.getCapacityQty() > 0) {
                    orderProcessRecordRepository.saveAndFlush(orderProcessRecordXL);
                }
            }
        }
    }

    @Transactional
    @Override
    public ResponseResult resumedProcess(Integer orderId, Integer orderProcessId) {
        // 通过订单Id获取订单当前工序
        TBusOrderProcess tBusOrderProcess = orderProcessService.findById(orderProcessId);
        //增加"工序撤回"按钮，只允许订单工序为下道工序，且下道工序未开工，才允许进行撤回，否则弹层提示："下道工序已开工，不允许撤回！"
        TBusOrderHead orderHead = orderHeadRepository.findById(orderId).orElse(null);
        var orderProcessSet = orderHead.getTBusOrderProcessSet();
        List<TBusOrderProcess> orderProcessList = new ArrayList<>();
        orderProcessList.addAll(orderProcessSet);
        orderProcessList.sort(Comparator.comparing(TBusOrderProcess::getProcessSeq));
        TBusOrderProcess tBusOrderProcessNext = null;
        for (var oProcess : orderProcessList) {
            if (oProcess.getProcessSeq() > tBusOrderProcess.getProcessSeq()) {
                tBusOrderProcessNext = oProcess;
                break;
            }
        }
//        if (null == tBusOrderProcessNext) {
//            //最后一道工序不需要判断，可以撤回
//        } else if (!LichengConstants.PROCESSSTATUS_1.equals(tBusOrderProcessNext.getProcessStatus())) {
//            return ResultUtil.error("下道工序已开工，不允许撤回！");
//        }
        if ("3".equals(tBusOrderProcess.getProcessStatus())) {
            tBusOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_1);//已开工
            tBusOrderProcess.setFinishTime(null);
            tBusOrderProcess.setElapsedTime(0);
            orderProcessService.saveTBusOrderProcess(tBusOrderProcess);
            //订单状态更新为已开工
            if (LichengConstants.ORDERSTATUS_3.equals(orderHead.getOrderStatus())) {
                orderHead.setOrderStatus(LichengConstants.ORDERSTATUS_1);//已开工
                orderHeadRepository.saveAndFlush(orderHead);
            }
            return ResultUtil.success();
        } else {
            return ResultUtil.error("工序状态错误：当前工序状态不为已完工，无法进行操作！");
        }
    }

    @Transactional
    @Override
    public ResponseResult orderProcessStart(Integer orderId, Integer orderProcessId, String userId) {
        // 通过订单Id获取订单当前工序，判断当前工序状态是否是未开工，修改状态
        TBusOrderProcess tBusOrderProcess = orderProcessService.findById(orderProcessId);
        //根据用户ID获取绑定的人员信息
        TSysPersonnelInfo tSysPersonnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        if (tBusOrderProcess.getPersonId() != null) {
            //如果工序执行表的处理人不为空，判断接单用户是否是处理人
            if (!tBusOrderProcess.getPersonId().getPersonnelId().equals(tSysPersonnelInfo.getPersonnelId())) {
                return ResultUtil.error("工序已被他人接单，无法进行开工操作！");
            }
        }
        if ("0".equals(tBusOrderProcess.getProcessStatus())) {
            tBusOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_1);
            tBusOrderProcess.setPersonId(tSysPersonnelInfo);
            Date receiveTime = new Date();
            tBusOrderProcess.setReceiveTime(new Timestamp(receiveTime.getTime()));//接单时间,默认当前系统时间
            orderProcessService.saveTBusOrderProcess(tBusOrderProcess);
            TBusOrderHead orderHead = this.getTBusOrderHead(orderId);
            orderHead.setCurrentPersonId(tSysPersonnelInfo);
            orderHead.setNcReceiveTime(receiveTime);
            orderHead.setOrderStatus(LichengConstants.ORDERSTATUS_1);
            this.saveTBusOrderHead(orderHead);
            return ResultUtil.success();
        } else {
            return ResultUtil.error("工序状态错误：当前工序状态不是未开工，无法进行开工操作！");
        }
    }

    @Transactional
    @Override
    public ResponseResult receiveHandover(Integer orderProcessId, String userId) throws ParseException {
        // 通过订单Id获取订单当前工序，修改移交状态
        TBusOrderProcess tBusOrderProcess = orderProcessService.findById(orderProcessId);
        TBusOrderProcess oldtBusOrderProcess = orderProcessService.findById(tBusOrderProcess.getOldOrderProcessId());
        if (LichengConstants.PROCESSSTATUS_0.equals(tBusOrderProcess.getProcessStatus())) {
            // 判断盘点记录是否为中途完工盘点，是则不更新 2022-09-14 任务14384
            List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordService.getOrderProcessRecord(oldtBusOrderProcess.getOrderProcessId(), LichengConstants.ORDER_BUS_TYPE_PD);
            //获取盘点，订单完工盘点的记录
//            List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordService.findAllByOrderProcessIdAndBusTypeAndRecordTypePd(tBusOrderProcess.getOldOrderProcessId(), LichengConstants.ORDER_BUS_TYPE_PD, LichengConstants.STOCKTAKING0002);
            if (LichengConstants.ORDER_PROCESS_TYPE_2.equals(tBusOrderProcess.getType()) && orderProcessRecordList.size() > 0 && orderProcessRecordList.get(0).getRecordTypePd().equals(LichengConstants.STOCKTAKING0002)) {
                //订单进行"订单完工盘点"类型进行移交，把订单的工序状态更新为已完工
                tBusOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_3);
            } else if (LichengConstants.ORDER_PROCESS_TYPE_2.equals(tBusOrderProcess.getType()) && orderProcessRecordList.size() > 0 && orderProcessRecordList.get(0).getRecordTypePd().equals(LichengConstants.STOCKTAKING0003)) {
                // 判断盘点记录是否为中途完工盘点，是则不更新 2022-09-14 任务14384  2022-09-20 中途完工盘点改为已完工
                tBusOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_3);
            } else {
                tBusOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_1);
            }
            Date receiveTime = new Date();
            tBusOrderProcess.setReceiveTime(new Timestamp(receiveTime.getTime()));//接单时间,默认当前系统时间
            //移交前的工序执行表，需将状态改为已完工，计算耗时
            if (!(LichengConstants.ORDER_PROCESS_TYPE_2.equals(tBusOrderProcess.getType()) && orderProcessRecordList.size() > 0 && orderProcessRecordList.get(0).getRecordTypePd().equals(LichengConstants.STOCKTAKING0003))) {
                //2022-09-20中途完工盘 不改原订单状态
                oldtBusOrderProcess.setProcessStatus("3");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date startTime = oldtBusOrderProcess.getReceiveTime();
            if (null == startTime) {
                return ResultUtil.error("工序校验错误：工序需要有接单时间，本道工序才能进行结束确认。！");
            }
            Date finishTime = new Date();
            String startTimeStr = sdf.format(startTime);
            String finishTimeStr = sdf.format(finishTime);
            oldtBusOrderProcess.setFinishTime(new Timestamp(finishTime.getTime()));
            Integer elapsedTime = Utils.getDifferHour(finishTime, startTime);
            oldtBusOrderProcess.setElapsedTime(elapsedTime);//耗时（小时）=完工时间-接单时间
            orderProcessService.saveTBusOrderProcess(oldtBusOrderProcess);
            orderProcessService.saveTBusOrderProcess(tBusOrderProcess);
            //添加投入上道数量报工
            addBG(tBusOrderProcess, userId);
            return ResultUtil.success();
        } else {
            throw new RuntimeException("工序状态错误：当前工序状态不是未开工，无法进行开工操作！");
        }
    }

    /**
     * 新增投入上道数量报工
     *
     * @param tBusOrderProcess
     */
    private void addBG(TBusOrderProcess tBusOrderProcess, String userId) throws ParseException {
        Integer oldOrderProcessId = tBusOrderProcess.getOldOrderProcessId();
        if (oldOrderProcessId == null) {
            throw new RuntimeException("当前工序非移交工序，请重试");
        }
        TBusOrderProcessRecord tBusOrderProcessRecordOr = new TBusOrderProcessRecord();
        tBusOrderProcessRecordOr.setBusType(LichengConstants.ORDER_BUS_TYPE_PD);
        tBusOrderProcessRecordOr.setRecordType(LichengConstants.ORDER_RECORD_TYPE_5);
        tBusOrderProcessRecordOr.setOrderProcessId(oldOrderProcessId);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("orderProcessId", ExampleMatcher.GenericPropertyMatchers.exact())// id like %?%
                .withMatcher("busType", ExampleMatcher.GenericPropertyMatchers.exact())//匹配
                .withMatcher("recordType", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<TBusOrderProcessRecord> example = Example.of(tBusOrderProcessRecordOr, matcher);
        List<TBusOrderProcessRecord> records = orderProcessRecordRepository.findAll(example);
        if (records.isEmpty()) {
            return;
        }
        for (TBusOrderProcessRecord record : records) {
            OrderRecordSaveDto saveDto = new OrderRecordSaveDto();
            BeanUtils.copyProperties(record, saveDto);
            saveDto.setOrderProcessId(tBusOrderProcess.getOrderProcessId());
            saveDto.setDevicePersonId(null);
            saveDto.setDeviceId(null);
            saveDto.setRecordType("5");
            saveDto.setRecordTypeBg("REPORTYPE0001");
            appOrderProcessRecordSubmitService.submit(saveDto, userId);
        }
    }

    @Override
    public PageVo<TaskListHandOverVo> getHandOverRecords(Integer current, Integer size, String userId, String sort) {
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "order_process_id");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        Sort sort1 = Sort.by(orders);
        PageRequest of = PageRequest.of(current, size, sort1);
//        current = current * size;
//        List<Object[]> select = orderHeadRepository.getHandOverRecordsByPersonId(userId, of);
        List<Map> select = orderHeadRepository.getHandOverRecordsByPersonId(userId, of);
        try {
            PageVo<TaskListHandOverVo> pageVo = new PageVo(size, current);
//            List<TaskListHandOverVo> castEntity = EntityUtils.castEntity(select, TaskListHandOverVo.class, new TaskListHandOverVo());
            List<TaskListHandOverVo> castEntity = JSON.parseArray(JSON.toJSONString(select), TaskListHandOverVo.class);
            castEntity.stream().forEach(order -> {
                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));
            });
            int total = orderHeadRepository.getHandOverRecordsByPersonId(userId);
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            return pageVo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PageVo<TaskListRelationVo> getRelationRecords(Integer current, Integer size, String midMoSaleOrderNo, Integer processId, String sort) {
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "order_process_id");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        Sort sort1 = Sort.by(orders);
        PageRequest of = PageRequest.of(current, size, sort1);
//        current = current * size;
//        List<Object[]> select = orderHeadRepository.getRelationRecords(midMoEntrySaleOrderNo, processId, of);
        List<Map> select = orderHeadRepository.getRelationRecords(midMoSaleOrderNo, processId, of);
        try {
            PageVo<TaskListRelationVo> pageVo = new PageVo(size, current);
//            List<TaskListRelationVo> castEntity = EntityUtils.castEntity(select, TaskListRelationVo.class, new TaskListRelationVo());
            List<TaskListRelationVo> castEntity = JSON.parseArray(JSON.toJSONString(select), TaskListRelationVo.class);
            castEntity.stream().forEach(order -> {
                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));
            });
            int total = orderHeadRepository.getRelationRecords(midMoSaleOrderNo, processId);
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            return pageVo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    @Override
    public void shiftRecord(Integer orderProcessId, String userId, Integer toOrderProcessId) {
        //获取尾料转移的工序执行类
        TBusOrderProcess orderProcess = orderProcessRepository.findTailingByOrderProcessId(orderProcessId);
        if (orderProcess != null) {
            throw new RuntimeException("已进行过转移，不允许重复转移！");
        }
        Timestamp handOverTime = new Timestamp(new Date().getTime());
        //原工序订单
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(orderProcessId).orElse(null);
        if (tBusOrderProcess != null) {
            tBusOrderProcess.setType(LichengConstants.ORDER_PROCESS_TYPE_3);
            tBusOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_3);
            tBusOrderProcess.setHandOverPerSonId(tSysPersonnelInfoRepository.findByUserId(userId));
            tBusOrderProcess.setHandOverTime(handOverTime);
        }
        orderProcessRepository.saveAndFlush(tBusOrderProcess);
        //转移执行工序
        TBusOrderProcess oldOrderProcess = orderProcessRepository.findById(toOrderProcessId).orElse(null);
        String orderNo = oldOrderProcess.getOrderNo();
        List<TBusOrderHead> byOrderNo = orderHeadRepository.findByOrderNo(orderNo);
        //新工序订单
        TBusOrderProcess newOrderProcess = new TBusOrderProcess();
        newOrderProcess.setType(LichengConstants.ORDER_PROCESS_TYPE_3);
        newOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_0);
        newOrderProcess.setPersonId(oldOrderProcess.getPersonId());
        newOrderProcess.setHandOverPerSonId(tBusOrderProcess.getPersonId());
        newOrderProcess.setHandOverTime(handOverTime);
        newOrderProcess.setOldOrderProcessId(tBusOrderProcess.getOrderProcessId());
        newOrderProcess.setReceiveTime(tBusOrderProcess.getReceiveTime());
        newOrderProcess.setOrderNo(orderNo);
        newOrderProcess.setProcessSeq(oldOrderProcess.getProcessSeq());
        newOrderProcess.setProcessId(oldOrderProcess.getProcessId());
        newOrderProcess.setClassId(oldOrderProcess.getClassId());
        TBusOrderHead tBusOrderHead = byOrderNo.get(0);
        Set<TBusOrderProcess> tBusOrderProcessSet = tBusOrderHead.getTBusOrderProcessSet();
        tBusOrderProcessSet.add(newOrderProcess);
        tBusOrderHead.setTBusOrderProcessSet(tBusOrderProcessSet);
        orderHeadRepository.saveAndFlush(tBusOrderHead);
    }

    @Override
    public PageVo<OrderSimpleListVo> getSimpleOrderList(String userId, Integer current, Integer size, TBusOrderDto orderDto) {
        //获取登录的产线
        List<String> cwkids = userService.getUserCurrentCwkid(userId);
        orderDto.setCwkids(cwkids);
        PageVo<TBusOrderHead> pageVo = tBusOrderHeadList(current, size, orderDto);
        PageVo<OrderSimpleListVo> result = new PageVo<>();
        result.setTotal(pageVo.getTotal());
        List<OrderSimpleListVo> list = new java.util.ArrayList<>();
        int index = 1 + current * size;
        for (TBusOrderHead order : pageVo.getList()) {
            OrderSimpleListVo vo = new OrderSimpleListVo();
            vo.setIndex(index++);
            vo.setOrderId(order.getOrderId());
            vo.setOrderNo(order.getOrderNo());
            vo.setBillNo(order.getBillNo());
            vo.setBillDate(order.getBillDate() != null ? order.getBillDate().toString() : "");
            vo.setBillType(order.getBillType());
            vo.setVwkname(order.getVwkname());
            vo.setCode(order.getBodyMaterialNumber());
            vo.setName(order.getBodyMaterialName());
            vo.setMaterialspec(order.getBodyMaterialSpecification());
            vo.setNnum(order.getBodyPlanPrdQty());
            vo.setTplanstarttime(order.getBodyPlanStartDate() != null ? order.getBodyPlanStartDate().toString() : "");
            vo.setOrderStatus(order.getOrderStatus());
            vo.setCraftName(order.getCraftId() != null ? order.getCraftId().getCraftName() : "");
            // 新增：设置工艺工序列表
            if (order.getCraftId() != null) {
                var craftDetail = craftInfoService.detail(order.getCraftId().getCraftId());
                vo.setCraftProcesses(craftDetail.getProcessInfos());
            }
            list.add(vo);
        }
        result.setList(list);
        return result;
    }

    @Override
    public OrderDetailSimpleVo getOrderDetailSimple(Integer orderId) {
        TBusOrderHead order = orderHeadRepository.findById(orderId).orElse(null);
        if (order == null) return null;
        OrderDetailSimpleVo vo = new OrderDetailSimpleVo();
        vo.setOrderNo(order.getOrderNo());
        vo.setBillNo(order.getBillNo());
        vo.setBillType(order.getBillType());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setCraftName(order.getCraftId() != null ? order.getCraftId().getCraftName() : "");
        vo.setRemark(order.getNcNote());
        vo.setBillDate(order.getBillDate() != null ? order.getBillDate().toString() : "");
        vo.setCreatorName(order.getCreatedName());
        vo.setVwkname(order.getVwkname());
        vo.setOrderNo(order.getOrderNo());
        // 物料清单（产品信息）
        OrderDetailSimpleVo.ProductItem product = new OrderDetailSimpleVo.ProductItem();
        product.setCode(order.getBodyMaterialNumber());
        product.setName(order.getBodyMaterialName());
        product.setPlanQty(order.getBodyPlanPrdQty());
        product.setPlanStartDate(order.getBodyPlanStartDate() != null ? order.getBodyPlanStartDate().toString() : "");
        product.setPlanFinishDate(order.getBodyPlanFinishDate() != null ? order.getBodyPlanFinishDate().toString() : "");
        vo.setProduct(product);
        // 用料清单
        List<OrderDetailSimpleVo.MaterialItem> materials = new java.util.ArrayList<>();
        if (order.getTBusOrderPPBomSet() != null) {
            for (TBusOrderPPBom bom : order.getTBusOrderPPBomSet()) {
                OrderDetailSimpleVo.MaterialItem item = new OrderDetailSimpleVo.MaterialItem();
                item.setCode(bom.getMaterialNumber());
                item.setName(bom.getMaterialName());
                item.setSpec(bom.getMaterialSpecification());
                item.setModel(bom.getMaterialModel());
                item.setUnit(bom.getUnit());
                item.setQty(Float.parseFloat(bom.getMustQty().toString()));
                materials.add(item);
            }
        }
        vo.setMaterials(materials);
        // 组装工序执行情况表
        List<OrderDetailSimpleVo.ProcessExecuteVo> processExecutes = new ArrayList<>();
        int processIndex = 1;
        // 收集所有记录类型
        List<String> recordTypes = new ArrayList<>();
        if (order.getTBusOrderProcessSet() != null) {
            for (TBusOrderProcess process : order.getTBusOrderProcessSet()) {
                List<TBusOrderProcessRecord> recordList = orderProcessRecordService.getOrderProcessRecord(process.getOrderProcessId(), "BG");
                if (recordList != null) {
                    recordList.removeIf(record -> Float.valueOf(0).equals(record.getRecordQty()));
                    for (TBusOrderProcessRecord record : recordList) {
                        if (record.getRecordType() != null && !record.getRecordType().isEmpty() && !recordTypes.contains(record.getRecordType())) {
                            recordTypes.add(record.getRecordType());
                        }
                    }
                }
            }
        }
        // 批量查询所有需要的code_dsc记录
        Map<String, String> recordTypeDscMap = new HashMap<>();
        if (!recordTypes.isEmpty()) {
            List<TSysCodeDsc> codeDscList = tSysCodeDscRepository.findByCodeClIdAndCodeValueIn("RECORDTYPE0000", recordTypes);
            for (TSysCodeDsc codeDsc : codeDscList) {
                recordTypeDscMap.put(codeDsc.getCodeValue(), codeDsc.getCodeDsc());
            }
        }

        if (order.getTBusOrderProcessSet() != null) {
            for (TBusOrderProcess process : order.getTBusOrderProcessSet()) {
                List<TBusOrderProcessRecord> recordList = orderProcessRecordService.getOrderProcessRecord(process.getOrderProcessId(), "BG");
                if (recordList != null) {
                    recordList.removeIf(record -> Float.valueOf(0).equals(record.getRecordQty()));
                    for (TBusOrderProcessRecord record : recordList) {
                        OrderDetailSimpleVo.ProcessExecuteVo execVo = new OrderDetailSimpleVo.ProcessExecuteVo();
                        execVo.setIndex(processIndex++);
                        execVo.setProcessName(process.getProcessId() != null ? process.getProcessId().getProcessName() : "");
                        String recordTypeDesc = recordTypeDscMap.get(record.getRecordType());
                        execVo.setProcessType(recordTypeDesc != null ? recordTypeDesc : record.getBusType());
                        execVo.setMaterialName(record.getMaterialName());
                        execVo.setMaterialSpec("");
                        execVo.setLot(record.getBodyLot());
                        execVo.setUnit(record.getRecordUnit());
                        execVo.setQty(record.getRecordQty());
                        execVo.setClassName(process.getClassId() != null ? process.getClassId().getName() : "");
                        execVo.setPotCount(record.getExportPot() != null ? record.getExportPot().intValue() : null);
                        execVo.setPersonName(record.getPersonId() != null ? record.getPersonId().getName() : "");
                        if (record.getReportTime() != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            execVo.setReportTime(sdf.format(record.getReportTime()));
                        } else {
                            execVo.setReportTime("");
                        }
                        processExecutes.add(execVo);
                    }
                }
            }
        }
        vo.setProcessExecutes(processExecutes);
        // 新增：设置工艺工序列表
        if (order.getCraftId() != null) {
            var craftDetail = craftInfoService.detail(order.getCraftId().getCraftId());
            vo.setCraftProcesses(craftDetail.getProcessInfos());
        }
        return vo;
    }

    @Override
    public List<OrderProcessVo> getProcessHistoryInfo(Integer orderId) {
        TBusOrderHead order = orderHeadRepository.findById(orderId).orElse(null);
        if (order == null) return null;
        List<OrderProcessVo> processHistory = new ArrayList<>();
        if (order.getTBusOrderProcessSet() != null) {
            List<String> recordTypes = new ArrayList<>();
            for (TBusOrderProcess process : order.getTBusOrderProcessSet()) {
                List<TBusOrderProcessRecord> recordList = orderProcessRecordService.getOrderProcessRecord(process.getOrderProcessId(), "BG");
                if (recordList != null) {
                    recordList.removeIf(record -> Float.valueOf(0).equals(record.getRecordQty()));
                    for (TBusOrderProcessRecord record : recordList) {
                        if (record.getRecordType() != null && !record.getRecordType().isEmpty() && !recordTypes.contains(record.getRecordType())) {
                            recordTypes.add(record.getRecordType());
                        }
                    }
                }
            }
            //批量查询所有需要的code_dsc记录
            Map<String, String> recordTypeDscMap = new HashMap<>();
            if (!recordTypes.isEmpty()) {
                List<TSysCodeDsc> codeDscList = tSysCodeDscRepository.findByCodeClIdAndCodeValueIn("RECORDTYPE0000", recordTypes);
                for (TSysCodeDsc codeDsc : codeDscList) {
                    recordTypeDscMap.put(codeDsc.getCodeValue(), codeDsc.getCodeDsc());
                }
            }

            //处理数据
            for (TBusOrderProcess process : order.getTBusOrderProcessSet()) {
                List<TBusOrderProcessRecord> recordList = orderProcessRecordService.getOrderProcessRecord(process.getOrderProcessId(), "BG");
                if (recordList != null) {
                    recordList.removeIf(record -> Float.valueOf(0).equals(record.getRecordQty()));

                    for (TBusOrderProcessRecord record : recordList) {
                        OrderProcessVo execVo = new OrderProcessVo();
                        execVo.setProcessName(process.getProcessId() != null ? process.getProcessId().getProcessName() : "");
                        execVo.setProcessType(recordTypeDscMap.getOrDefault(record.getRecordType(), record.getRecordType()));
                        execVo.setMaterialName(record.getMaterialName());
                        execVo.setMaterialSpec("");
                        execVo.setLot(record.getBodyLot());
                        execVo.setUnit(record.getRecordUnit());
                        execVo.setQty(record.getRecordQty());
                        execVo.setClassName(record.getClassId() != null ? record.getClassId().getName() : "");
                        execVo.setPotCount(record.getExportPot() != null ? record.getExportPot().intValue() : null);
                        if (record.getDevicePersonGroupId() != null && !record.getDevicePersonGroupId().isEmpty()) {
                            List<TBusOrderProcessPersonRel> personRels = orderProcessPersonRelRepository
                                    .findByDevicePersonGroupId(record.getDevicePersonGroupId());
                            if (!personRels.isEmpty()) {
                                List<Integer> personIds = personRels.stream()
                                        .map(TBusOrderProcessPersonRel::getDevicePersonId)
                                        .filter(Objects::nonNull)
                                        .sorted(Collections.reverseOrder())
                                        .collect(Collectors.toList());
                                if (!personIds.isEmpty()) {
                                    List<TSysPersonnelInfo> personnelInfos = tSysPersonnelInfoRepository
                                            .findByPersonnelIdIn(personIds);
                                    List<TSysPersonnelInfo> sortedPersonnelInfos = new ArrayList<>();
                                    for (Integer personId : personIds) {
                                        personnelInfos.stream()
                                                .filter(p -> p.getPersonnelId().equals(personId))
                                                .findFirst()
                                                .ifPresent(sortedPersonnelInfos::add);
                                    }
                                    String personNames = sortedPersonnelInfos.stream()
                                            .map(TSysPersonnelInfo::getName)
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.joining(","));
                                    execVo.setPersonName(personNames);
                                } else {
                                    execVo.setPersonName("");
                                }
                            } else {
                                execVo.setPersonName("");
                            }
                        } else {
                            execVo.setPersonName(record.getPersonId() != null ? record.getPersonId().getName() : "");
                        }
                        if (record.getReportTime() != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            execVo.setReportTime(sdf.format(record.getReportTime()));
                        }
                        processHistory.add(execVo);
                    }
                }
            }
        }
        return processHistory;
    }

}

