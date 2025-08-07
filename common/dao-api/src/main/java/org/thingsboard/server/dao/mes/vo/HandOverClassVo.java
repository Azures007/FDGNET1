package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/7/18 17:17
 * @Description:
 */
@Data
public class HandOverClassVo {
    @ApiModelProperty("班组名称")
    private String className;
    @ApiModelProperty("班组编码")
    private String classNumber;
    @ApiModelProperty("班组编码")
    private Integer classId;
//    private String scheduling;

    @ApiModelProperty("姓名")
    private String personName;
    @ApiModelProperty("岗位")
    private String personnelStation;
    @ApiModelProperty("用户id")
    private String userId;
}
