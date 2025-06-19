package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;


/**
 * @author cms
 * @version V1.0
 * @Package org.thingsboard.server.dto
 * @date 2022/4/13 14:21
 * @Description:
 */
@Data
@ApiModel("人员列表条件模型")
public class TSysPersonnelInfoDto {
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("班组")
    private String className;
    @ApiModelProperty("是否可用")
    private String enabledSt;

    @ApiModelProperty("岗位")
    private String station;

}
