package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.dto
 * @date 2022/4/12 11:36
 * @Description:
 */
@Data
@ApiModel("设置角色菜单模型")
public class SetRoleMenuDto {
    @ApiModelProperty(value = "角色id",required = true)
    private Integer roleId;
    @ApiModelProperty(value = "菜单列表id集合",required = true)
    private List<Integer> menuIds;
    @ApiModelProperty(value = "创建人",required = false)
    private String createdName;
    @ApiModelProperty(value = "创建时间",required = false)
    private Date createdTime;

}
