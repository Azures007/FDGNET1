package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.TSysQualityReportItem;
import org.thingsboard.server.common.data.TSysQualityReportPlanRel;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.dto
 * @date 2022/7/13 11:36
 * @Description:
 */
@Data
@ApiModel("日报方案")
public class TSysQualityReportPlanDto {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("品名")
    @Column(name = "product_name")
    private String product_name;

    @ApiModelProperty("生产部门id")
    @Column(name = "prod_dept_id")
    private String prod_dept_id;

    @ApiModelProperty("生产线id")
    @Column(name = "prod_line_id")
    private String prod_line_id;



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

    @ApiModelProperty("配置信息")
    private List<TSysQualityReportPlanRel> itemList;

}
