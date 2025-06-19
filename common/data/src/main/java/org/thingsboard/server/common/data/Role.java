package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.util.Date;
import java.util.UUID;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @date 2022/4/6 11:06
 * @Description:
 */
@Data
@ApiModel("角色")
public class Role {
    @ApiModelProperty("角色id")
    private UUID roleId;
    @ApiModelProperty("角色名")
    private String roleName;
    @ApiModelProperty("角色代码")
    private String roleCode;
    @ApiModelProperty("角色说明")
    private String roleExplain;
    @ApiModelProperty("创建人")
    private String createdName;
    @ApiModelProperty("是否可用")
    private String enabled;
    @ApiModelProperty
    private Date createdTime;

}
