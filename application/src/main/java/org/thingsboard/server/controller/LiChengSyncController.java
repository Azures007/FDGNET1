package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.TSysDevice.TSysDeviceService;
import org.thingsboard.server.dao.mes.licheng.LiChengService;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@RestController
@RequestMapping("/licheng")
@Api(value = "力诚同步接口", tags = "力诚同步接口")
public class LiChengSyncController {

    @Autowired
    LiChengService liChengService;

    @Autowired
    TSysDeviceService deviceService;

    @Value("${spring.datasource.driverClassName}")
    public String driver;
    @Value("${spring.datasource.url}")
    public String url;
    @Value("${spring.datasource.username}")
    public String user;
    @Value("${spring.datasource.password}")
    public String password;

    @GetMapping("/test")
    public ResponseResult test(){
        return ResultUtil.success(System.getProperty("user.dir"));
    }

    @GetMapping("/initIot")
    public ResponseResult initIot(@RequestParam("endDate") String endDate,@RequestParam("recordType") String recordType){
        deviceService.initIot(endDate,recordType);
        return ResultUtil.success();
    }

    @ApiOperation("同步程序")
    @GetMapping("/sync")
    public ResponseResult sync(@RequestParam("orderId") Integer orderId) {
        try {
            liChengService.sync(orderId);
        } catch (Exception e) {
            liChengService.addLog(orderId.toString(), "1", e.getMessage(), "订单同步");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        liChengService.addLog(orderId.toString(), "0", "同步成功", "订单同步");
        return ResultUtil.success();
    }

    @ApiOperation("订单变更")
    @GetMapping("/syncOrderUpdate")
    public ResponseResult syncOrderUpdate(@RequestParam("id") Integer id) {
        try {
            liChengService.syncOrderUpdate(id);
        } catch (Exception e) {
            liChengService.addLog(id.toString(), "1", e.getMessage(), "订单变更");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        liChengService.addLog(id.toString(), "0", "同步成功", "订单变更");
        return ResultUtil.success();
    }


    @ApiOperation("同步订单的挂起状态")
    @GetMapping("/syncPendingStatus")
    public ResponseResult syncPendingStatus(@RequestParam("orderEntryId") Integer orderEntryId) {
        try {
            liChengService.syncStatus(orderEntryId);
        } catch (Exception e) {
            liChengService.addLog(orderEntryId.toString(), "1", "订单状态同步失败：" + e.getMessage(), "订单同步");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        liChengService.addLog(orderEntryId.toString(), "0", "订单状态同步成功", "订单同步");
        return ResultUtil.success();
    }

    @ApiOperation("物料同步程序")
    @GetMapping("/syncMid")
    public ResponseResult syncMid() {
        liChengService.midMaterialSync(false);
        return ResultUtil.success();
    }

    @ApiOperation("物料全量同步程序")
    @GetMapping("/syncMidAll")
    public ResponseResult syncMidAll() {
        liChengService.midMaterialSync(true);
        return ResultUtil.success();
    }

    @ApiOperation("组织表同步")
    @GetMapping("/syncOrg")
    public ResponseResult syncOrg() {
        liChengService.syncOrg();
        return ResultUtil.success();
    }

    @ApiOperation("部门表同步")
    @GetMapping("/syncDept")
    public ResponseResult syncDept() {
        liChengService.syncDept();
        return ResultUtil.success();
    }

    @ApiOperation("设备同步")
    @GetMapping("/syncDataSource")
    public ResponseResult syncDataSource(){
        //获取前一天的日期
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String dateStr = df.format(calendar.getTime());
        prepareCall(dateStr,"pc_all_job_running");
        System.out.println("同步成功");
        return ResultUtil.success();
    }

    @ApiOperation("syncCoilingmachine同步")
    @GetMapping("/syncCoilingmachine")
    public ResponseResult syncCoilingmachine(){
        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String dateStr = df.format(calendar.getTime());
        prepareCall(dateStr,"pc_cln_coilingmachine_t");
        return ResultUtil.success();
    }

    @ApiOperation("syncCrabline同步")
    @GetMapping("/syncCrabline")
    public ResponseResult synCcrabline(){
        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String dateStr = df.format(calendar.getTime());
        prepareCall(dateStr,"pc_cln_crabline_t");
        return ResultUtil.success();
    }

    @ApiOperation("syncCuttingmachine同步")
    @GetMapping("/syncCuttingmachine")
    public ResponseResult syncCuttingmachine(){
        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String dateStr = df.format(calendar.getTime());
        prepareCall(dateStr,"pc_cln_cuttingmachine_t");
        return ResultUtil.success();
    }

    @ApiOperation("syncMixingpot同步")
    @GetMapping("/syncMixingpot")
    public ResponseResult syncMixingpot(){
        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String dateStr = df.format(calendar.getTime());
        prepareCall(dateStr,"pc_cln_mixingpot_t");
        return ResultUtil.success();
    }

    @ApiOperation("syncPackline同步")
    @GetMapping("/syncPackline")
    public ResponseResult syncPackline(){
        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String dateStr = df.format(calendar.getTime());
        prepareCall(dateStr,"pc_cln_packline_t");
        return ResultUtil.success();
    }

    @ApiOperation("syncSterilizepot")
    @GetMapping("/syncSterilizepot")
    public ResponseResult syncSterilizepot(){
        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String dateStr = df.format(calendar.getTime());
        prepareCall(dateStr,"pc_cln_sterilizepot_t");
        return ResultUtil.success();
    }

    @ApiOperation("syncStretchmachine")
    @GetMapping("/syncStretchmachine")
    public ResponseResult syncStretchmachine(){
        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String dateStr = df.format(calendar.getTime());
        prepareCall(dateStr,"pc_cln_stretchmachine_t");
        return ResultUtil.success();
    }

    private  void prepareCall(String dateStr,String tableName) {

        Connection conn = null;
        CallableStatement proc = null;
        try {
            Class.forName(driver);

            conn = DriverManager.getConnection(url, user, password);

            PreparedStatement stmt = conn.prepareStatement("call "+tableName+"(?)");
            stmt.setString(1, dateStr);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("操作失败", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("操作失败", e);
        } finally {
            try {
                if (proc != null) {
                    proc.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("操作失败", e);
            }
        }
    }

}
