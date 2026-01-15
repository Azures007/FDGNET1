package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 工序分组信息VO
 */
@Data
public class ProcessGroupInfoVo {

    @ApiModelProperty("工序名称")
    private String processName;

    @ApiModelProperty("工序状态")
    private String processStatus;

    @ApiModelProperty("工序分组标识(用于合并)")
    private String groupKey;

    @ApiModelProperty("工序下的物料信息列表")
    private List<ProcessMaterialInfoVo> materialInfoList;
}