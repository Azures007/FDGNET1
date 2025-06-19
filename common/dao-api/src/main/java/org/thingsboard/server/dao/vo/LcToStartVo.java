package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("批次列表")
public class LcToStartVo {
    @ApiModelProperty("是否成功")
    private boolean success;
    @ApiModelProperty("响应code")
    private int code;
    @ApiModelProperty("消息")
    private String message;
    @ApiModelProperty("数据")
    private Object data;
    @ApiModelProperty("类型")
    private int showType;
}
