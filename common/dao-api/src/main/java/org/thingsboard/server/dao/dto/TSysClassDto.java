package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cms
 * @version V1.0
 * @Package org.thingsboard.server.dto
 * @date 2022/4/12 11:21
 * @Description:
 */
@Data
@ApiModel("班别列表条件模型")
public class TSysClassDto {
    @ApiModelProperty("班别名称（模糊查询）")
    private String name;
    @ApiModelProperty("班别编码（模糊查询）")
    private String classNumber;
    @ApiModelProperty("是否可用 0：禁用 1：启用")
    private String enabledSt;
}
