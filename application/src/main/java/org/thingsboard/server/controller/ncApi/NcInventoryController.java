package org.thingsboard.server.controller.ncApi;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.ncInventory.NcInventoryService;
import org.thingsboard.server.dao.mes.dto.NcInventorySyncRequest;

@RestController
@RequestMapping("/outapi/nc/inventory")
@Api(tags = "NC仓库库存接口")
public class NcInventoryController {
    @Autowired
    private NcInventoryService service;

    @ApiOperation("批量新增/更新NC库存")
    @PostMapping("/addbatch")
    public ResponseResult create(@RequestBody NcInventorySyncRequest request) {
        service.saveOrUpdateBatchByBillId(request);
        return ResultUtil.success("同步成功");
    }
}
