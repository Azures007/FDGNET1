package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("移交信息")
public class BotMessageVo {
    @ApiModelProperty("工序名称")
    private String processName;
    @ApiModelProperty("处理人")
    private String name;

    @Override
    public String toString() {
        return "BotMessageVo{" +
                "processName='" + processName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
