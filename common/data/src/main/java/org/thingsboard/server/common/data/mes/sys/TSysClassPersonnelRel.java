package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_sys_class_personnel_rel")
@ApiModel("班别人员关系类")
public class TSysClassPersonnelRel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_personnel_id")
    private Integer classPersonnelId;
    @ApiModelProperty("组别ID")
    @Column(name = "class_id")
    private Integer classId;
    @ApiModelProperty("人员ID")
    @Column(name = "personnel_id")
    private Integer personnelId;

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
