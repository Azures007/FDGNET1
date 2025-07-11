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
@Table(name = "t_sys_quality_report_item")
@ApiModel("质检日报类目详细检查项")
public class TSysQualityReportItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("关联的日报类目表id")
    @Column(name = "category_id")
    private Integer categoryId;

    @ApiModelProperty("达成（异常）清况描述")
    @Column(name = "field_name")
    private String fieldName;

    @ApiModelProperty("达成（异常）清况类型(下拉，或文本)")
    @Column(name = "field_type")
    private String fieldType;

    @ApiModelProperty("下拉框")
    @Column(name = "dropdown_fields")
    private Integer dropdownFields;

    @ApiModelProperty("是否必填")
    @Column(name = "required")
    private String required;

    @ApiModelProperty("启用/禁用")
    @Column(name = "enabled")
    private String enabled;


    @ApiModelProperty("创建人")
    @Column(name = "created_name")
    private String createdName;

    @ApiModelProperty("创建时间")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("修改人")
    @Column(name = "updated_name")
    private String updatedName;

    @ApiModelProperty("修改日期")
    @Column(name = "updated_time")
    private Date updatedTime;


}
