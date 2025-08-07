package org.thingsboard.server.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.mes.bus.TBusOrderBindCode;
import org.thingsboard.server.common.data.mes.sys.TSysChargingBasket;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.PageChargingBasketDto;
import org.thingsboard.server.dao.mes.vo.OrderBindCodeVo;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.utils.ExcelUtil;
import org.thingsboard.server.vo.ChargingBasketExcel;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/chargingbasket")
@Api(value = "料筐相关接口", tags = "料筐相关接口")
public class ChargingBasketController extends BaseController {

    @ApiOperation("新增/修改（有无id区别）")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody TSysChargingBasket tSysChargingBasket) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        tSysChargingBasket.setUpdatedName(currentUser.getName());
        tSysChargingBasket.setUpdatedTime(new Date());
        chargingBasketService.update(tSysChargingBasket);
        return ResultUtil.success();
    }

    @ApiOperation("获取料框信息")
    @GetMapping("/getChargingBasket")
    public ResponseResult<TSysChargingBasket> getChargingBasket(@RequestParam("code") String code) {
        return ResultUtil.success(chargingBasketService.getByCode(code));
    }

    @ApiOperation("查看二维码")
    @GetMapping("/getQR")
    public ResponseResult<String> getQR(@RequestParam("code") String code) throws Exception {
        String qr=chargingBasketService.getQR(code);
        return ResultUtil.success(qr);
    }

    @ApiOperation("禁用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "料筐Id", required = true),
            @ApiImplicitParam(name = "enabled", value = "禁用标识 0：不可用 1：可用", required = true)
    })
    @GetMapping("/isEnabled")
    public ResponseResult isEnabled(@RequestParam("id") Integer id, @RequestParam("enabled") Integer enabled) throws Exception {
        TSysChargingBasket tSysChargingBasket = chargingBasketService.getById(id);
        tSysChargingBasket.setChargingBasketStatus(enabled == 0 ? GlobalConstant.enableFalse : GlobalConstant.enableTrue);
        chargingBasketService.update(tSysChargingBasket);
        return ResultUtil.success();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", required = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", required = false)

    })
    @ApiOperation("料筐列表")
    @PostMapping("/pageChargingBasket")
    public ResponseResult<PageVo<TSysChargingBasket>> pageChargingBasket(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                         @RequestBody PageChargingBasketDto pageChargingBasketDto) {
        PageVo<TSysChargingBasket> tSysChargingBasketPageVo = chargingBasketService.pageChargingBasket(current, size, pageChargingBasketDto);
        return ResultUtil.success(tSysChargingBasketPageVo);
    }

    @ApiOperation("删除")
    @GetMapping("/delete")
    public ResponseResult delete(@RequestParam("id") Integer id) {
        chargingBasketService.delete(id);
        return ResultUtil.success();
    }

    @ApiOperation("导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", required = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", required = false)

    })
    @PostMapping("/export")
    public void export(@RequestParam(value = "current", defaultValue = "0") Integer current,
                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                       @RequestBody PageChargingBasketDto pageChargingBasketDto,
                       HttpServletResponse response) {
        List<TSysChargingBasket> chargingBasketList =pageChargingBasket(current,size,pageChargingBasketDto).getData().getList();
        List<ChargingBasketExcel> chargingBasketExcels = JSON.parseArray(JSON.toJSONString(chargingBasketList), ChargingBasketExcel.class);
        ExcelUtil.writeExcel(response, chargingBasketExcels, "export", "sheet", new ChargingBasketExcel());
    }

    @ApiOperation("下载模板")
    @GetMapping("/downTemplate")
    public void downTemplate(HttpServletResponse response){
        List<ChargingBasketExcel> chargingBasketExcels=new ArrayList<>();
        ExcelUtil.writeExcel(response, chargingBasketExcels, "muban", "sheet", new ChargingBasketExcel());
    }


    @Transactional
    @ApiOperation("导入excel")
    @PostMapping("/importExcel")
    public ResponseResult importExcel(MultipartFile file) throws ThingsboardException {
        List<Object> objects = ExcelUtil.readExcel(file, new ChargingBasketExcel());
        List<TSysChargingBasket> chargingBaskets = JSON.parseArray(JSON.toJSONString(objects), TSysChargingBasket.class);
        for (TSysChargingBasket chargingBasket : chargingBaskets) {
            update(chargingBasket);
        }
        return ResultUtil.success();
    }

    @ApiOperation("扫码绑定信息表接口（产后报工扫码）")
    @PostMapping("/bindCheckMes")
    public ResponseResult bindCheckMes(@RequestBody TBusOrderBindCode tBusOrderBindCode) {
        chargingBasketService.bindCheckMes(tBusOrderBindCode);
        return ResultUtil.success();
    }

    @ApiOperation("判断工序是否启用扫码相关设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "craftId", value = "工艺路线id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "processId", value = "工序id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "key", value = "键 isReportingBindCode：报工扫码 isReceivingBindCode：接单扫码（投入扫码） isReceivingUnBindCode：扫码解绑（投入解绑）", required = true, dataTypeClass = String.class)
    })
    @GetMapping("/isBindEnabled")
    public ResponseResult<Boolean> isBindEnabled(@RequestParam("craftId") Integer craftId,
                                                 @RequestParam("processId") Integer processId,
                                                 @RequestParam("key") String key) {
        Boolean flag = chargingBasketService.isBindEnabled(craftId, processId, key);
        return ResultUtil.success(flag);
    }


    @ApiOperation("接单扫码获取订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bindCodeNumber", value = "绑定料框编码", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderNo", value = "订单号", required = false, dataTypeClass = String.class)
    })
    @GetMapping("/queryOrderBindCode")
    public ResponseResult<OrderBindCodeVo> queryOrderBindCode(@RequestParam("bindCodeNumber") String bindCodeNumber, @RequestParam(value = "orderNo",defaultValue = "",required = false) String orderNo){
        OrderBindCodeVo orderBindCodeVo = chargingBasketService.queryOrderBindCode(bindCodeNumber, orderNo);
        return ResultUtil.success(orderBindCodeVo);
    }


    @ApiOperation("解绑")
    @GetMapping("/unBind")
    public ResponseResult unBind(@ApiParam(name = "bindCodeNumber",value = "料筐编码",required = true) @RequestParam("bindCodeNumber") String bindCodeNumber){
        chargingBasketService.unBind(bindCodeNumber);
        return ResultUtil.success();
    }

}
