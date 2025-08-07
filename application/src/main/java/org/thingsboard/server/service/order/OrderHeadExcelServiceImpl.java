package org.thingsboard.server.service.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.config.KingDeePropertiesData;
import org.thingsboard.server.dao.TSysCraftinfo.TSysCraftInfoService;
import org.thingsboard.server.dao.TSysProcessInfo.TSysProcessInfoService;
import org.thingsboard.server.dao.dto.*;
import org.thingsboard.server.dao.message.MessageService;
import org.thingsboard.server.dao.order.OrderHeadService;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftMaterialRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftProcessRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessClassRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialRepository;
import org.thingsboard.server.dao.sql.mes.tSysClass.ClassGroupLeaderRepository;
import org.thingsboard.server.dao.sql.mes.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.tSysClass.TSysClassService;
import org.thingsboard.server.dao.tSysCodeDsc.TSysCodeDscService;
import org.thingsboard.server.dao.tSysPersonnelInfo.TSysPersonnelInfoService;
import org.thingsboard.server.dao.vo.OrderSimpleListVo;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.service.rpc.LcERPCallBack;
import org.thingsboard.server.utils.ExcelUtil;
import org.thingsboard.server.vo.OrderExcelVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: hhh
 * @Date: 2022/4/30 16:16
 * @Description:
 */
@Service
@Slf4j
public class OrderHeadExcelServiceImpl implements OrderHeadExcelService {

    @Autowired
    OrderHeadService orderHeadService;

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    TSysCodeDscService sysCodeDscService;

    @Autowired
    KingDeePropertiesData kingDeePropertiesData;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Autowired
    TSysClassRepository classRepository;

    @Autowired
    TSysClassService classService;

    @Autowired
    TSysCraftInfoRepository craftInfoRepository;

    @Autowired
    TSysCraftInfoService craftInfoService;

    @Autowired
    TSysCraftProcessRelRepository craftProcessRelRepository;

    @Autowired
    TSysProcessInfoRepository processInfoRepository;

    @Autowired
    TSysProcessInfoService processInfoService;

    @Autowired
    TSysProcessClassRelRepository processClassRelRepository;

    @Autowired
    ClassGroupLeaderRepository classGroupLeaderRepository;

    @Autowired
    MessageService messageService;

    @Autowired
    TSysPersonnelInfoService personnelInfoService;

    @Autowired
    SyncMaterialRepository syncMaterialRepository;

    @Autowired
    TSysCraftMaterialRelRepository craftMaterialRelRepository;

    @Autowired
    LcERPCallBack LcERPCallBack;

    @Override
    public void download(Integer current, Integer size, TBusOrderHeadDto tBusOrderHeadDto, HttpServletResponse response) throws IOException {
        /*PageVo<TBusOrderHead> tBusOrderHeadPage= orderHeadService.tBusOrderHeadList(current,size,tBusOrderHeadDto);
        List<OrderExcelVo> excelVos=new ArrayList<>();
        for (var tBusOrderHead : tBusOrderHeadPage.getList()) {
            OrderExcelVo vo=new OrderExcelVo();
            BeanUtils.copyProperties(tBusOrderHead,vo);
            //获取字典对象：订单状态
            if (!StringUtils.isEmpty(tBusOrderHead.getOrderStatus())){
                var codeValue = tBusOrderHead.getOrderStatus();
                TSysCodeDsc codeDscOS = sysCodeDscService.getCodeByCodeClAndCodeVale("ORDERSTATUS0000", codeValue);
                vo.setOrderStatus(codeDscOS.getCodeDsc());
            }
            vo.setCurrentProcessName(tBusOrderHead.getCurrentProcess()==null?"":tBusOrderHead.getCurrentProcess().getProcessName());
            vo.setClassName(tBusOrderHead.getClassId()==null?"":tBusOrderHead.getClassId().getName());
            vo.setBillPlanQty(tBusOrderHead.getBodyPlanPrdQty());
            excelVos.add(vo);
        }
        ExcelUtil.writeExcel(response,excelVos,System.currentTimeMillis()+"","sheet1",new OrderExcelVo());*/
    }

    @Override
    public void download(String userId,Integer current, Integer size, TBusOrderDto tBusOrderDto, HttpServletResponse response) throws IOException {
        PageVo<OrderSimpleListVo> tBusOrderHeadPage= orderHeadService.getSimpleOrderList(userId,current, size, tBusOrderDto);
        List<OrderExcelVo> excelVos=new ArrayList<>();
        for (var tBusOrderHead : tBusOrderHeadPage.getList()) {
            OrderExcelVo vo=new OrderExcelVo();
            BeanUtils.copyProperties(tBusOrderHead,vo);
            //获取字典对象：订单状态
            if (!StringUtils.isEmpty(tBusOrderHead.getOrderStatus())){
                var codeValue = tBusOrderHead.getOrderStatus();
                TSysCodeDsc codeDscOS = sysCodeDscService.getCodeByCodeClAndCodeVale("ORDERSTATUS0000", codeValue);
                vo.setOrderStatus(codeDscOS.getCodeDsc());
            }
            excelVos.add(vo);
        }
        ExcelUtil.writeExcel(response,excelVos,System.currentTimeMillis()+"","sheet1",new OrderExcelVo());
    }

    @Override
    public void downloadOrder(Integer current, Integer size, TBusOrderDto tBusOrderDto, HttpServletResponse response) throws Exception {
        /*PageVo<OrderListVo> tBusOrderHeadPage= orderHeadService.getOrderHeadList(current,size,tBusOrderDto);
        List<OrderExcelVo> excelVos=new ArrayList<>();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        for (var tBusOrderHead : tBusOrderHeadPage.getList()) {
            OrderExcelVo vo=new OrderExcelVo();
            //获取字典对象：订单状态
            var codeValue = tBusOrderHead.getOrder_status();
            TSysCodeDsc codeDscOS = sysCodeDscService.getCodeByCodeClAndCodeVale("ORDERSTATUS0000", codeValue);
            vo.setBillNo(tBusOrderHead.getBill_no());
            vo.setBillDate(sdf1.parse(tBusOrderHead.getBill_date()));
            vo.setBodyPrdDept(tBusOrderHead.getBody_prd_dept());
            vo.setBillPlanQty(tBusOrderHead.getBill_plan_qty());
            vo.setBodyPlanFinishDate(sdf1.parse(tBusOrderHead.getBody_plan_finish_date()));
            vo.setBodyMaterialSpecification(tBusOrderHead.getBody_material_specification());
            vo.setBillType(tBusOrderHead.getBill_type());
            vo.setOrderStatus(tBusOrderHead.getOrder_status());
            vo.setCurrentProcessName(tBusOrderHead.getCurrent_process());
            vo.setClassName(tBusOrderHead.getClass_id());
            excelVos.add(vo);
        }
        ExcelUtil.writeExcel(response,excelVos,System.currentTimeMillis()+"","sheet1",new OrderExcelVo());*/
    }

}
