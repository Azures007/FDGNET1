package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/4/20 10:51
 * @Description:工艺表
 */
@Entity
@ApiModel("工艺表")
@Data
@Table(name = "t_sys_craft_info")
public class TSysCraftInfo {
    @Id
    @Column(name = "craft_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer craftId;

    @ApiModelProperty("工艺路线名称")
    @Column(name = "craft_name")
    private String craftName;

    @ApiModelProperty("工艺路线编号")
    @Column(name = "craft_number")
    private String craftNumber;

    @ApiModelProperty("生效时间")
    @Column(name = "effective_time")
    private Date effectiveTime;

    @ApiModelProperty("failure_time")
    @Column(name = "failure_time")
    private Date failureTime;

    @ApiModelProperty("工艺说明")
    @Column(name = "craft_detail")
    private String craftDetail;

    @ApiModelProperty("是否启用 0：禁用 1：启用")
    @Column(name = "enabled")
    private Integer enabled=0;

    @ApiModelProperty("创建人")
    @Column(name = "created_user")
    private String createdUser;

    @ApiModelProperty("创建日期")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("修改日期")
    @Column(name = "updated_time")
    private Date updatedTime;

    @ApiModelProperty("修改人")
    @Column(name = "updated_user")
    private String updatedUser;

    @ApiModelProperty("生产组织id")
    @Column(name = "kd_org_id")
    private Integer kdOrgId;

    @ApiModelProperty("生产车间id")
    @Column(name = "kd_dept_id")
    private Integer kdDeptId;

    @ApiModelProperty("前道工艺路线id")
    @Column(name = "prev_craft_id")
    private Integer prevCraftId;

}