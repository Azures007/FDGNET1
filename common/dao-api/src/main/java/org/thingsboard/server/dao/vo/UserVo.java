package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.o
 * @date 2022/4/15 14:51
 * @Description:
 */
@ApiModel("用户列表返回对象")
@Data
public class UserVo {
    @ApiModelProperty("用户id")
    private String user_id;
    @ApiModelProperty("账号")
    private String username;
    @ApiModelProperty("用户名")
    private String first_name;
    @ApiModelProperty("创建日期")
    private String created_time;
    @ApiModelProperty("创建人")
    private String created_name;
    @ApiModelProperty("用户状态 0:启用 1：禁用")
    private String user_status;
    @ApiModelProperty("所属角色id")
    private Integer role_id;
    @ApiModelProperty("所属角色名")
    private String role_name;
    @ApiModelProperty("所属角色代码")
    private String role_code;
    @ApiModelProperty("所属角色描述")
    private String role_explain;
}
