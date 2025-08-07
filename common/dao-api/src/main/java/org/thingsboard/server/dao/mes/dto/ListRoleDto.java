package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dto
 * @date 2022/4/8 17:47
 * @Description:
 */
@Data
@ApiModel("角色列表条件模型")
public class ListRoleDto {
    @ApiModelProperty("角色名称（模糊查询）")
    private String roleName;
    @ApiModelProperty("角色代码（模糊查询）")
    private String roleCode;
    @ApiModelProperty("状态 0：启动 1：禁用")
    private String enabled;
}
