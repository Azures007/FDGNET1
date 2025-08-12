package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.BaseEntity;

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
public class TSysCraftInfo extends BaseEntity {
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

    @ApiModelProperty("前道工艺路线id")
    @Column(name = "prev_craft_id")
    private Integer prevCraftId;

    // 列表的生产组织改为基地，默认为当前登录用户的基地，隐藏生产车间 hhh 2025-07-23
    // 废弃字段：生产组织id、生产车间id；新增基地id
    @ApiModelProperty("生产组织id")
    @Column(name = "kd_org_id")
    private Integer kdOrgId;

    @ApiModelProperty("生产车间id")
    @Column(name = "kd_dept_id")
    private Integer kdDeptId;

    @ApiModelProperty("基地Id")
    @Column(name = "pk_org")
    private String pkOrg;

}
