package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/4/20 16:53
 * @Description:工序设置表
 */
@Data
@Entity
@Table(name = "t_sys_process_class_rel")
@ApiModel("工序设置表")
public class TSysProcessClassRel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_class_id")
    private Integer processClassId;
    @ApiModelProperty("组别ID")
    @Column(name = "class_id")
    private Integer classId;
    @ApiModelProperty("工序id")
    @Column(name = "process_id")
    private Integer processId;

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
}
