package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.dto
 * @date 2022/4/15 14:15
 * @Description:
 */
@Data
@ApiModel("用户列表查询条件对象")
public class PageUserDto {
    @ApiModelProperty("用户名查询（支持模糊查询）")
    private String name="";
    @ApiModelProperty("账号查询（支持模糊查询）")
    private String username="";
    @ApiModelProperty("用户状态 0:启用 1:禁用")
    private String userStatus="";
}
