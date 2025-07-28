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
@Table(name = "t_sys_quality_report_category")
@ApiModel("质量日报类目表")
public class TSysQualityReportCategory {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("频次，字典")
    @Column(name = "frequency")
    private String frequency;

    @ApiModelProperty("频次，字典值")
    private String frequencyValue;

    @ApiModelProperty("重点项目")
    @Column(name = "important_item")
    private String importantItem;



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
