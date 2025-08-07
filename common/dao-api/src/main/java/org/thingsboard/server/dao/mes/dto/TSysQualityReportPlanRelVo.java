
package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @Auther: l
 * @Date: 2022/4/20 11:04
 * @Description:工序表
 */
@Data
public class TSysQualityReportPlanRelVo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("质检日报方案id")
    private Integer planId;

    @ApiModelProperty("质检日报类目id")
    private Integer categoryId;


    @ApiModelProperty("分类信息")
    private SysQualityReportCategoryDto sysQualityReportCategoryDto;



}
