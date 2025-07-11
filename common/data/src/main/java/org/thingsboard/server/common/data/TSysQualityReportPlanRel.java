
package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/4/20 11:04
 * @Description:工序表
 */
@Data
@Entity
@Table(name = "t_sys_quality_report_plan_rel")
@ApiModel("质检日报方案表")
public class TSysQualityReportPlanRel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("质检日报方案id")
    @Column(name = "product_name")
    private Integer planId;

    @ApiModelProperty("质检日报类目id")
    @Column(name = "category_id")
    private Integer categoryId;



}
