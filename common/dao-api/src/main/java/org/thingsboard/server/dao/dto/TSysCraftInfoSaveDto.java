package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/21 16:49
 * @Description:
 */
@Data
@ApiModel("工艺路线模型")
public class TSysCraftInfoSaveDto {

    @ApiModelProperty("id")
    private Integer craftId;

    @ApiModelProperty("工艺路线名称")
    private String craftName;

    @ApiModelProperty("工艺路线编号")
    private String craftNumber;

    @ApiModelProperty("生效时间")
    private Date effectiveTime;

    @ApiModelProperty("failure_time")
    private Date failureTime;

    @ApiModelProperty("工艺说明")
    private String craftDetail;

    @ApiModelProperty("是否启用 0：启用 1：禁用")
    private Integer enabled=0;

    @ApiModelProperty("创建人")
    private String createdUser;

    @ApiModelProperty("创建日期")
    private Date createdTime;

    @ApiModelProperty("修改日期")
    private Date updatedTime;

    @ApiModelProperty("修改人")
    private String updatedUser;

    @ApiModelProperty("工序列表")
    private List<ProcessInfoDto> processInfos;

    @ApiModelProperty("生产组织id")
    private Integer kdOrgId;

    @ApiModelProperty("生产车间id")
    private Integer kdDeptId;

    @ApiModelProperty("前道工艺路线id")
    private Integer prevCraftId;

}