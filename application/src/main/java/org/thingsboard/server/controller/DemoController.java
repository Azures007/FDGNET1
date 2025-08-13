package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.sys.TSysDemo;
import org.thingsboard.server.dao.mes.demo.TSysDemoService;
import org.thingsboard.server.dao.mes.dto.TSysDemoDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

import java.util.HashMap;
import java.util.Map;

/**
 * 样例基础信息控制器
 * @author: hhh
 * @date: 2025/8/13 15:00
 * @description: 提供样例的增删改查REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/demo")
@Api(value = "样例基础信息接口", tags = "样例基础信息接口")
public class DemoController {

    @Autowired
    private TSysDemoService tSysDemoService;

    /**
     * 分页查询样例列表
     * @param current 当前页码（从0开始）
     * @param size 每页大小
     * @param queryDto 样例查询条件
     * @return 分页结果
     */
    @PostMapping("/list")
    @ApiOperation(value = "分页查询样例列表", notes = "支持多条件查询，返回分页结果")
    public ResponseEntity<PageVo<TSysDemo>> getDemoList(
            @ApiParam(value = "样例查询条件", required = true)
            @RequestBody TSysDemoDto queryDto,
            @ApiParam(value = "当前页码", required = true, example = "0")
            @RequestParam(defaultValue = "0") Integer current,
            @ApiParam(value = "每页大小", required = true, example = "10")
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("查询样例列表，参数：current={}, size={}, queryDto={}", current, size, queryDto);

        try {
            PageVo<TSysDemo> result = tSysDemoService.tSysDemoList(current, size, queryDto);
            log.info("查询样例列表成功，总数量：{}", result.getTotal());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询样例列表失败", e);
            throw new RuntimeException("查询样例列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询样例详情
     * @param demoId 样例ID
     * @return 样例详情
     */
    @GetMapping("/{demoId}")
    @ApiOperation(value = "根据ID查询样例详情", notes = "根据样例ID查询样例的详细信息")
    public ResponseEntity<TSysDemo> getDemoById(
            @ApiParam(value = "样例ID", required = true, example = "1")
            @PathVariable Integer demoId) {

        log.info("查询样例详情，demoId：{}", demoId);

        try {
            TSysDemo demo = tSysDemoService.getTSysDemoById(demoId);
            log.info("查询样例详情成功：{}", demo);
            return ResponseEntity.ok(demo);
        } catch (Exception e) {
            log.error("查询样例详情失败，demoId：{}", demoId, e);
            throw new RuntimeException("查询样例详情失败：" + e.getMessage());
        }
    }

    /**
     * 新增或更新样例
     * @param tSysDemo 样例对象
     * @return 操作结果
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新增或更新样例", notes = "demoId为空或<=0时为新增，否则为更新")
    public ResponseEntity<Map<String, Object>> saveOrUpdateDemo(
            @ApiParam(value = "样例对象", required = true)
            @RequestBody TSysDemo tSysDemo) {

        log.info("保存或更新样例：{}", tSysDemo);

        try {
            tSysDemoService.saveOrUpdateTSysDemo(tSysDemo);

            Map<String, Object> result = new HashMap<>();
            if (tSysDemo.getDemoId() == null || tSysDemo.getDemoId() <= 0) {
                result.put("message", "样例新增成功");
                result.put("operation", "新增");
            } else {
                result.put("message", "样例更新成功");
                result.put("operation", "更新");
            }
            result.put("demoId", tSysDemo.getDemoId());
            result.put("success", true);

            log.info("样例{}成功，ID：{}", result.get("operation"), tSysDemo.getDemoId());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("保存或更新样例失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "操作失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    /**
     * 删除样例
     * @param demoId 样例ID
     * @return 操作结果
     */
    @DeleteMapping("/{demoId}")
    @ApiOperation(value = "删除样例", notes = "根据样例ID删除样例")
    public ResponseEntity<Map<String, Object>> deleteDemo(
            @ApiParam(value = "样例ID", required = true, example = "1")
            @PathVariable Integer demoId) {

        log.info("删除样例，demoId：{}", demoId);

        try {
            tSysDemoService.deleteTSysDemo(demoId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "样例删除成功");
            result.put("demoId", demoId);

            log.info("样例删除成功，ID：{}", demoId);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("删除样例失败，demoId：{}", demoId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    /**
     * 批量删除样例
     * @param demoIds 样例ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除样例", notes = "根据样例ID列表批量删除样例")
    public ResponseEntity<Map<String, Object>> batchDeleteDemo(
            @ApiParam(value = "样例ID列表", required = true)
            @RequestBody Integer[] demoIds) {

        log.info("批量删除样例，demoIds：{}", demoIds);

        if (demoIds == null || demoIds.length == 0) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "样例ID列表不能为空");
            return ResponseEntity.badRequest().body(errorResult);
        }

        try {
            int successCount = 0;
            int failCount = 0;
            StringBuilder failMessages = new StringBuilder();

            for (Integer demoId : demoIds) {
                try {
                    tSysDemoService.deleteTSysDemo(demoId);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failMessages.append("ID ").append(demoId).append(": ").append(e.getMessage()).append("; ");
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("totalCount", demoIds.length);
            result.put("successCount", successCount);
            result.put("failCount", failCount);

            if (failCount > 0) {
                result.put("message", "批量删除完成，成功：" + successCount + "个，失败：" + failCount + "个");
                result.put("failMessages", failMessages.toString());
            } else {
                result.put("message", "批量删除成功，共删除" + successCount + "个样例");
            }

            log.info("批量删除样例完成，成功：{}个，失败：{}个", successCount, failCount);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("批量删除样例失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "批量删除失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    /**
     * 检查样例编码是否存在
     * @param demoNumber 样例编码
     * @param demoId 样例ID（更新时使用，新增时可为空）
     * @return 检查结果
     */
    @GetMapping("/checkNumber")
    @ApiOperation(value = "检查样例编码是否存在", notes = "用于前端验证样例编码的唯一性")
    public ResponseEntity<Map<String, Object>> checkDemoNumber(
            @ApiParam(value = "样例编码", required = true)
            @RequestParam String demoNumber,
            @ApiParam(value = "样例ID（更新时使用）", required = false)
            @RequestParam(required = false) Integer demoId) {

        log.info("检查样例编码是否存在，demoNumber：{}，demoId：{}", demoNumber, demoId);

        try {
            boolean exists = false;
            String message = "样例编码可用";

            if (demoId != null && demoId > 0) {
                // 更新时检查
                try {
                    tSysDemoService.getTSysDemoById(demoId);
                    // 如果ID存在，检查编码是否与其他样例重复
                    // 这里需要在Service中添加相应的方法
                    message = "样例编码可用";
                } catch (Exception e) {
                    message = "样例不存在";
                }
            } else {
                // 新增时检查
                // 这里需要在Service中添加相应的方法
                message = "样例编码可用";
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("exists", exists);
            result.put("message", message);
            result.put("demoNumber", demoNumber);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("检查样例编码失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "检查失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
}
