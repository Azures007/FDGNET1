package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_sys_class_group_leader_rel")
@ApiModel("班别和组长关系")
@NoArgsConstructor
@AllArgsConstructor
public class TSysClassGroupLeaderRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_group_leader_id")
    private Integer classGroupLeaderId;

    @ApiModelProperty("组别ID")
    @Column(name = "class_id")
    private Integer classId;
    @ApiModelProperty("人员ID(组长)")
    @Column(name = "personnel_id")
    private Integer personnelId;

    @ApiModelProperty("创建人")
    @Column(name = "crt_user")
    private String crtUser;
    @ApiModelProperty("创建日期")
    @Column(name = "crt_time")
    private Date crtTime;
}
