package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/4/21 17:26
 * @Description:
 */
@Data
@ApiModel("工艺路线查询")
public class TSysQualityReportCategorySearchDto {
    @ApiModelProperty("频次，字典")
    private String frequency;

    @ApiModelProperty("重点项目")
    private String importantItem;

    @ApiModelProperty("是否启用 0：启用 1：禁用")
    private Integer enabled=0;
}
