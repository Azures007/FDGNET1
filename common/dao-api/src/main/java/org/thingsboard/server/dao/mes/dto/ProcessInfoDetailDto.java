package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/4/21 17:06
 * @Description:
 */
@Data
@ApiModel("工艺路线-工序详情模型")
public class ProcessInfoDetailDto {
    @ApiModelProperty("工序id")
    private Integer processId;
    @ApiModelProperty("工序名称")
    private String processName;
    @ApiModelProperty("班组名称")
    private String className;
    @ApiModelProperty("排序")
    private Integer sort;
}
