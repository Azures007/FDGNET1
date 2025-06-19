package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/7/22 18:00
 * @Description:
 */
@Data
@ApiModel("")
public class AppVersionVo {
    @ApiModelProperty("版本代码")
    private String vrsnCode;
    @ApiModelProperty("版本地址")
    private String upgFile;
    @ApiModelProperty("更新内容")
    private String remark;

}