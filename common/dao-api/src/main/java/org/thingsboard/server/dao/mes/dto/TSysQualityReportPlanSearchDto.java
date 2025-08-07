package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.dto
 * @date 2022/7/13 11:36
 * @Description:
 */
@Data
@ApiModel("日报检查项")
public class TSysQualityReportPlanSearchDto {

    @ApiModelProperty("品名")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty("生产线")
    private String prodLineName;

    @ApiModelProperty("是否启用 0：启用 1：禁用")
    private Integer enabled=0;


}
