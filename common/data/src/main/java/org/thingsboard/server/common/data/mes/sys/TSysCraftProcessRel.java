package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/4/20 16:59
 * @Description:工艺工序关系表
 */
@Data
@Entity
@Table(name = "t_sys_craft_process_rel")
@ApiModel("工艺工序关系表")
public class TSysCraftProcessRel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "craft_process_id")
    private Integer craftProcessId;
    @ApiModelProperty("工艺id")
    @Column(name = "craft_id")
    private Integer craftId;
    @ApiModelProperty("工序id")
    @Column(name = "process_id")
    private Integer processId;
    @ApiModelProperty("排序")
    @Column(name = "sort")
    private Integer sort;
    @ApiModelProperty("创建人")
    @Column(name = "crt_user")
    private String crtUser;
    @ApiModelProperty("创建日期")
    @Column(name = "crt_time")
    private Date crtTime;
    @ApiModelProperty("修改日期")
    @Column(name = "update_time")
    private Date updateTime;
    @ApiModelProperty("修改人")
    @Column(name = "update_user")
    private String updateUser;

    @ApiModelProperty("报工扫码 0：否 1：是")
    @Column(name = "is_reporting_bind_code")
    private Integer isReportingBindCode = 0;
    @ApiModelProperty("接单扫码（投入扫码） 0：否 1：是")
    @Column(name = "is_receiving_bind_code")
    private Integer isReceivingBindCode = 0;
    @ApiModelProperty("扫码解绑（投入解绑） 0：否 1：是")
    @Column(name = "is_receiving_unbind_code")
    private Integer isReceivingUnBindCode = 0;
}
