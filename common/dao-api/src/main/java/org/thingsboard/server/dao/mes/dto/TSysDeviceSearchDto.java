package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TSysDeviceSearchDto {
    @ApiModelProperty("所属工序id")
    private Integer belongProcessId;
    @ApiModelProperty("状态 0：启动 1：禁用")
    private String enabled;
    @ApiModelProperty("工序执行表id")
    private Integer orderProcessId;
    @ApiModelProperty("操作员id集合")
    private List<Integer> devicePersonIds;
    @ApiModelProperty("是否按操作员id过滤 0：启动 1：禁用")
    private boolean isQueryByPersonIds = false;

}
