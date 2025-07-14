
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
@Table(name = "t_sys_quality_report_plan")
@ApiModel("质检日报方案表")
public class TSysQualityReportPlan {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("品名")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty("生产部门id")
    @Column(name = "prod_dept_id")
    private Integer prodDeptId;

    @ApiModelProperty("生产线id")
    @Column(name = "prod_line_id")
    private Integer prodLineId;



    @ApiModelProperty("启用/禁用")
    @Column(name = "enabled")
    private Integer enabled;


    @ApiModelProperty("创建人")
    @Column(name = "created_name")
    private String createdMame;

    @ApiModelProperty("创建时间")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("修改人")
    @Column(name = "updated_name")
    private String updatedName;

    @ApiModelProperty("修改日期")
    @Column(name = "updated_time")
    private Date updatedTime;

    @ApiModelProperty("备注")
    @Column(name = "remark")
    private String remark;


}
