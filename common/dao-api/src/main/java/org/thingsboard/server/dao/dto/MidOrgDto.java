package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.dto;
 * @date 2022/7/9 9:21
 * @Description:
 */
@Data
@ApiModel("组织列表条件模型")
public class MidOrgDto {
    @ApiModelProperty("组织编号")
    private String kdOrgNum;
}
