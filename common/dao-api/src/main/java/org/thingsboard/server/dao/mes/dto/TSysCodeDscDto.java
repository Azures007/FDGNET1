package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author cms
 * @version V1.0
 * @Package org.thingsboard.server.dto
 * @date 2022/4/12 17:21
 * @Description:
 */
@Data
@ApiModel("字典列表条件模型")
public class TSysCodeDscDto {
    @ApiModelProperty("字典分类编码")
    private String codeClId;
    @ApiModelProperty("字典名称（模糊查询）")
    private String codeDsc;
    @ApiModelProperty("状态 0：启动 1：禁用")
    private String enabledSt;
}
