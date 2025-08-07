package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;

/**
 * @Auther: l
 * @Date: 2022/7/18 17:20
 * @Description:
 */
@Data
public class HandOverStationVo {
    @ApiModelProperty("姓名")
    private String personName;
    @ApiModelProperty("岗位")
    private String personnelStation;
    @ApiModelProperty("用户id")
    private String userId;
}
