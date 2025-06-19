package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: hhh
 * @Date: 2022/8/18 17:46
 * @Description:
 */
@Data
@ApiModel("工序列表条件模型")
public class TSysProcessInfoDto {
    @ApiModelProperty("工序名称（模糊查询）")
    private String processName;
    @ApiModelProperty("工序编号（模糊查询）")
    private String processNumber;
    @ApiModelProperty("状态 0：禁用 1：启用")
    private Integer enabled;
}